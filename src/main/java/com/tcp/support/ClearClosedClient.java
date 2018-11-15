package com.tcp.support;

import com.tcp.client.Client;
import com.tcp.collection.ClientCollection;
import java.util.Iterator;
import java.util.List;

/**
 * 监视掉线的客户端，当有客户端掉线时将其清除掉。
 **/
public class ClearClosedClient {
    private ClientCollection collection;
    private int outTime;
    /**
     * 实例化一个对象用于检测掉线的连接，并将其移除
     */
    public ClearClosedClient(){
        outTime = 60000;
    }
    public void init(){
        new Thread(()->{
            while (true){
                List<Client> clients = collection.getAllClient();
                Iterator<Client> iterator = clients.iterator();
                while (iterator.hasNext()){
                    Client client = iterator.next();
                    if ((client.isConnect()==false)||(System.currentTimeMillis()-client.getLastReceiveTime().getTime()>outTime)){
                        collection.removeClient(client);
                        System.out.println(client.getId()+"掉线，被移除");
                    }
                }
            }
        },"ClearClosedClient").start();
    }
    public void setCollection(ClientCollection collection) {
        this.collection = collection;
    }
    /**
     * 设置允许客户端最久未发送数据的时间，默认为1分钟
     * @param outTime 以秒为单位，默认为60秒
     */
    public void setOutTime(int outTime) {
        this.outTime = outTime;
    }
}
