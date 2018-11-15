package com.tcp.server;

import com.tcp.client.Client;
import com.tcp.collection.ClientCollection;
import com.tcp.utils.ParseData;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 用于和客户端设备建立连接并与客户端设备进行数据传输。
 */
public class TcpServer implements ApplicationContextAware {
    /*
     * 用于检测TCP服务器是否启动
     */
    private volatile boolean start;
    /*
     * 用来检测SocketChannel是否可用的选择器
     */
    private Selector selector;
    /*
     * 服务器端的通道
     */
    private ServerSocketChannel serverSocketChannel;
    /*
     * 当检测到某个通道有数据需要读取时，暂时取消该通道与selector的注册关系，并将该通道与selector的注册关系
     * SelectionKey 对象暂存到该集合中
     */
    private List<SelectionKey> readQueen;
    /*
     * 当有某个通道的数据需要进行业务处理时，将开启一条线程来处理该通道数据的业务逻辑
     */
    private ExecutorService servicePool;
    /*
     * 保存客户端的集合，用于管理和服务器连接的客户端
     */
    private ClientCollection clientCollection;
    /*
     * 读取数据并将数据进行分包处理的类
     */
    private ParseData parseData;
    /*
     * 用于从spring容器中获取指定的Client
     */
    private HashMap<String,String> clientMap;
    /*
     * SPRING 容器
     */
    private ApplicationContext springContext;
    /**
     * 实例化TcpServer对象
     * @param PORT 该服务所占用的端口，客户端设备连接服务器时的端口号必须和这个端口号相同
     */
    public TcpServer(int PORT){
        start = false;
        readQueen = new LinkedList<>();
        int processorsCount=Runtime.getRuntime().availableProcessors();
        servicePool = Executors.newFixedThreadPool(processorsCount);
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.out.println("TCP服务启动失败，请检查"+PORT+"端口是否被占用");;
        }
    }
    /**
     * 启动服务器
     */
    public void start(){
        while (true){
            int selectCount=0;
            try {
                selectCount = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClosedSelectorException e){
                System.out.println("TCP服务被关闭");
                break;
            }
            if (selectCount>0){
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()){
                        doAcceptable(key);
                    }else if (key.isReadable()){
                        key.cancel();
                        doReadable(key);
                    }
                }
            }else{
                synchronized (readQueen){
                    while (readQueen.size()>0){
                        SelectionKey key = readQueen.remove(0);
                        SocketChannel channel = (SocketChannel) key.channel();
                        try {
                            channel.register(selector,SelectionKey.OP_READ);
                        } catch (ClosedChannelException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    //-----------------------------------------------------------------------------
    //                              业务逻辑方法
    //-----------------------------------------------------------------------------
    /**
     * 处理客户端连接，当有客户端连接时，将该客户端与服务器建立的通道设置成非阻塞式，并且注册到selector上
     * @param key selector监听到有连接事件的SelectionKey对象
     */
    public void doAcceptable(SelectionKey key){
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel=null;
        try {
            channel = serverSocketChannel.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.register(selector,SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
    /**
     * 接收客户端发送过来的数据，并进行处理
     * @param key
     */
    public void doReadable(SelectionKey key){
        servicePool.submit(()->{
            SocketChannel channel = (SocketChannel) key.channel();
            /*
             * 通过SocketChannel对象获取保存在clientCollection中的Cleint对象，若存在该对象，
             * 则调用cleint对象的业务方法处理数据，否则新建一个Client对象
             */
            Client client = clientCollection.getClient(channel);
            if (client!=null){
                if (client.isConnect()){
                    try {
                        client.doRequest(parseData);
                    } catch (IOException e) {
                        client.setConnect(false);
                        try {
                            channel.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        return;
                    }
                }else{
                    return;
                }
            }else{
                try {
                    addNewClient(channel);
                } catch (IOException e) {
                    try {
                        channel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    return;
                }
            }
            //处理完业务后将key添加到队列，准备重新注册并监听
            addKey(key);
        });
    }
    /**
     * 释放资源
     */
    public void close() throws IOException {
        if (serverSocketChannel!=null){
            serverSocketChannel.close();
        }
        if (selector!=null){
            selector.close();
        }
        start=false;
        System.out.println("TCP服务关闭成功...");
    }
    /**
     * 处理新连接
     * @param channel
     */
    public void addNewClient(SocketChannel channel) throws IOException {
        List<Byte> readData=parseData.getData(channel);
        if (readData.size()>0){
            List<List<Byte>> packages = parseData.getPackages(readData);
            Iterator<List<Byte>> iterator = packages.iterator();
            while (iterator.hasNext()){
                List<Byte> data = iterator.next();
                iterator.remove();
                if (parseData.isComplete(data)){
                    if (parseData.getDataType(data)==ParseData.CONNECT_PACKAGE){
                        byte[] idBytes = new byte[6];
                        for (int i=0; i<6;i++){
                            idBytes[i]=data.get(i+7);
                        }
                        //将设备ID编码转换成字符串
                        String id= new String(idBytes);
                        //获取设备类型
                        String clientType = ""+data.get(3);
                        Client client = (Client) springContext.getBean(clientMap.get(clientType));
                        //给新初始化的client对象设置ID和channel
                        client.setId(id);
                        client.setSocketChannel(channel);
                        client.init();
                        clientCollection.addClient(client);
                    }
                }
            }
        }
    }
    /**
     * 向readQueen集合中添加SelectionKey对象
     * @param key 从selector中暂时移除的SelectionKey对象
     */
    public synchronized void addKey(SelectionKey key){
        readQueen.add(key);
        selector.wakeup();
    }
    //-----------------------------------------------------------------------------
    //                              set/get方法区
    //-----------------------------------------------------------------------------
    /**
     * 判断服务器是否已经启动
     * @return 当服务器启动时true，否则false
     */
    public boolean isStart() {
        return start;
    }
    public void setStart(boolean status){
        this.start = status;
    }
    /**
     * 设置服务器管理客户端对象的集合
     * @param clientCollection 用来保存和服务器建立连接的客户端对象
     */
    public void setClientCollection(ClientCollection clientCollection) {
        this.clientCollection = clientCollection;
    }
    /**
     * 传入一个数据解析器
     * @param parseData 数据解析器
     */
    public void setParseData(ParseData parseData) {
        this.parseData = parseData;
    }
    /**
     * 指定一个Map用来映射设备编号和设备对象之间的关系。例如使用1代表SocketClient对象。
     * @param clientMap 该map在spring配置文件applicationContext.xml中配置
     */
    public void setClientMap(Map<String, String> clientMap) {
        this.clientMap = (HashMap<String, String>) clientMap;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException  {
        this.springContext = applicationContext;
    }
}
