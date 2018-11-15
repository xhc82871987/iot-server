package com.tcp.client;

import com.tcp.utils.ParseData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现了Client接口的部分方法，作为各种类型的设备的通用方法
 **/
public abstract class AbstractClient implements Client {
    /**
     * 心跳包数据
     */
    public static final byte[] HEART_PACKAGE={17,0,7,0,0,0,22};
    /*
     * 客户端对应的标识
     */
    protected String Id;
    /*
     * 客户端是否处于连接状态
     */
    protected boolean connect;
    /*
     * 客户端和服务器建立的连接
     */
    protected SocketChannel socketChannel;
    /*
     * 客户端最后一次接收数据的时间，用于判断客户端是否掉线
     */
    protected Date lastReceiveTime;
    /*
     * 向客户端发送数据的时间间隔
     */
    protected int sendDataTimeOut;
    /*
     * 向客户端最后一次发送数据的时间
     */
    protected Date lastSendDataTime;

    private static ExecutorService servicePool;
    //-----------------------------------------------------------------------------
    //                                 构造方法
    //-----------------------------------------------------------------------------
    static {
        int processorsCount=Runtime.getRuntime().availableProcessors();
        servicePool = Executors.newFixedThreadPool(processorsCount);
    }
    /**
     * 初始化客户端信息，设备连接true，上一次收到数据为当前时间，初始化发送数据间隔为3秒，
     * 最后一次发送数据的时间为当前时间
     */
    public AbstractClient() {
        connect = true;
        lastReceiveTime=new Date();
        lastSendDataTime=new Date(lastReceiveTime.getTime()-sendDataTimeOut);
    }
    //-----------------------------------------------------------------------------
    //                                 继承方法区
    //-----------------------------------------------------------------------------
    @Override
    public String getId() {
        return Id;
    }
    @Override
    public void setId(String Id) {
        this.Id = Id;
    }
    @Override
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
    @Override
    public void init() throws IOException{
        sendData(AbstractClient.HEART_PACKAGE);
        clientInit();
    }
    /**
     * 对于不同类型的Client，需要实现该方法来进行业务处理，该方法由父类的init方法调用
     * @throws IOException
     */
    public abstract void clientInit();
    @Override
    public void setSocketChannel(SocketChannel channel) {
        this.socketChannel = channel;
    }
    @Override
    public boolean isConnect() {
        return connect;
    }
    @Override
    public abstract void setConnect(boolean status);
    @Override
    public synchronized void sendData(byte[] data){
        servicePool.submit(()->{
            long sleepTime = sendDataTimeOut - (System.currentTimeMillis() - lastSendDataTime.getTime());
            if (sleepTime>0){
                try {
                    Thread.currentThread().sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(new Date()+" "+e.getMessage());
                }
            }
            try {
                System.out.print("发送的数据是：");
                for (byte b :data){
                    System.out.print(b+" ");
                }
                System.out.println();
                this.socketChannel.write(ByteBuffer.wrap(data));
            } catch (IOException e) {
                System.out.println(new Date()+" "+e.getMessage());
                setConnect(false);
            }
            updateLastSendDataTime();
        });
    }
    @Override
    public abstract void doRequest(ParseData parseData) throws IOException ;
    @Override
    public Date getLastReceiveTime() {
        return lastReceiveTime;
    }
    @Override
    public void updateLastReceiveTime() {
        lastReceiveTime = new Date();
    }
    //-----------------------------------------------------------------------------
    //                                 get/set
    //-----------------------------------------------------------------------------
    /**
     * 设置最后一次收到客户端数据的时间
     * @param lastReceiveTime
     */
    public void setLastReceiveTime(Date lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }
    /**
     * 设置发送数据的时间间隔
     * @param sendDataTimeOut 以毫秒为单位
     */
    public void setSendDataTimeOut(int sendDataTimeOut) {
        this.sendDataTimeOut = sendDataTimeOut;
    }
    /**
     * 返回最后一次发送数据的时间
     * @return
     */
    public Date getLastSendDataTime() {
        return lastSendDataTime;
    }
    /**
     * 更新最后一次发送数据的时间
     */
    public void updateLastSendDataTime() {
        this.lastSendDataTime = new Date();
    }
    //----------------------------------------------------------------------------------
    //                            重写equals方法和hashCode方法
    //----------------------------------------------------------------------------------
    public boolean equals(Object obj){
        if (obj instanceof Client){
            Client client = (Client) obj;
            if (this.Id.equals(client.getId())){
                return true;
            }
        }
        return false;
    }
    public int hashCode(){
        return this.Id.hashCode();
    }

}
