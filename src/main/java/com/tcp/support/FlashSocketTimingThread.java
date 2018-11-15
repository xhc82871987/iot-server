package com.tcp.support;

import com.tcp.client.Client;
import com.tcp.client.SocketClient;
import com.tcp.collection.ClientCollection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
/**
 * 每日刷新一次设备的定时信息
 **/
public class FlashSocketTimingThread {
    private ClientCollection collection;
    public void init(){
        new Thread(()->{
            while (true){
                List<Client> clients=collection.getAllClient();
                Iterator<Client> iterator = clients.iterator();
                while (iterator.hasNext()){
                    Client client = iterator.next();
                    //查找WiFiSocketClient
                    if (client instanceof SocketClient){
                        SocketClient socketClient = (SocketClient) client;
                        try {
                            socketClient.updateTimings();
                        } catch (SQLException e) {
                            continue;
                        }
                    }
                }
                try {
                    Thread.currentThread().sleep(24*60*60*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"FlashSocketTimingThread").start();
    }
    public void setCollection(ClientCollection collection) {
        this.collection = collection;
    }
}
