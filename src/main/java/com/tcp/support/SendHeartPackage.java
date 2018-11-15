package com.tcp.support;

import com.tcp.client.AbstractClient;
import com.tcp.client.Client;
import com.tcp.collection.ClientCollection;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
/**
 * 向客户端发送心跳包
 **/
public class SendHeartPackage {
    private ClientCollection collection;
    private int timeOut;
    public void init() {
       new Thread(()->{
           while (true){
               try {
                   Thread.currentThread().sleep(timeOut);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               List<Client> clients = collection.getAllClient();
               Iterator<Client> iterator = clients.iterator();
               while (iterator.hasNext()){
                   Client client = iterator.next();
                   try {
                       if (client.isConnect()){
                           client.sendData(AbstractClient.HEART_PACKAGE);
                       }
                   } catch (IOException e) {
                       client.setConnect(false);
                       try {
                           client.getSocketChannel().close();
                       } catch (IOException e1) {
                           e1.printStackTrace();
                       }
                   }
               }
           }
       },"SendHeartPackage").start();
    }

    public void setCollection(ClientCollection collection) {
        this.collection = collection;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
