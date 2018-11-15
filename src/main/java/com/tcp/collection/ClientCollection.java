package com.tcp.collection;

import com.tcp.client.Client;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * 用于系统管理Client的实现类，当有新设备连接服务器时，服务器验证完设备端的信息，验证通过则会实例化一个Client对象，
 * 并将该Client交给该ClientCollection类管理。
 **/
public class ClientCollection {
    /*
     * 使用一个线程安全的HashMap来管理所有的客户端信息
     */
    private Map<String, Client> clients = Collections.synchronizedMap(new HashMap<>());
    //---------------------------------------------------------------------------------
    //                                  实现接口方法
    //---------------------------------------------------------------------------------
    /**
     * 向集合中添加一个Client对象
     * @param client 要添加的client
     */
    public synchronized void addClient(Client client) {
        if (!clients.containsKey(client.getId())){
            clients.put(client.getId(),client);
        }
    }
    /**
     * 通过设备的ID编号检查集合中是否存在该对象
     * @param id 待检查的设备的编号
     * @return 返回是否存在该设备对应的Client对象。若存在返回true，否则返回false
     */
    public boolean exitClient(String id) {
        return clients.containsKey(id);
    }
    /**
     * 通过设备与服务器之间建立的SocketChannel来检查集合中是否存在对应的Client对象
     * @param channel 设备与服务器之间建立的SocketChannel
     * @return 返回检查结果，若存在返回true，否则返回false
     */
    public boolean exitClient(SocketChannel channel) {
        Client client = getClient(channel);
        if (client != null){
            return true;
        }
        return false;
    }
    /**
     * 通过设备编号获取Client对象
     * @param id 设备编号
     * @return 返回查找的Client对象，若无则返回null
     */
    public Client getClient(String id) {
        return clients.get(id);
    }
    /**
     * 通过SocketChannel获取Clinet对象
     * @param channel 设备与服务器建立的连接
     * @return 返回查找到的Client对象，若无返回null
     */
    public Client getClient(SocketChannel channel) {
        List<Client> list = new ArrayList<>(clients.values());
        Iterator<Client> iterator = list.iterator();
        while (iterator.hasNext()){
            Client client = iterator.next();
            if (client.getSocketChannel().equals(channel)) {
                return client;
            }
        }
        return null;
    }
    /**
     * 获取集合中存在的所有的client
     * @return 集合中存在的所有的client
     */
    public List<Client> getAllClient() {
        ArrayList<Client> values = new ArrayList<>(clients.values());
        return values;
    }
    /**
     * 删除指定的Client
     * @param client 待删除的Client
     * @return 返回被删除的Client，若不存在该Client，则返回null
     */
    public synchronized Client removeClient(Client client) {
        if (clients.containsKey(client.getId())){
            return clients.remove(client.getId());
        }
        return null;
    }
    /**
     * 删除指定编号的Client
     * @param id 要删除的Client的编号
     * @return 返回被删除的Client，若不存在该Client，则返回null
     */
    public synchronized Client removeClient(String id) {
        if (clients.containsKey(id)){
            return clients.remove(id);
        }
        return null;
    }
    /**
     * 通过设备与服务器之间的连接信息删除Client
     * @param channel 设备与服务器之间建立的SocketChannel
     * @return 返回被删除的Client，若不存在该Client，则返回null
     */
    public synchronized Client removeClient(SocketChannel channel) {
        List<Client> values = new ArrayList<>(clients.values());
        Iterator<Client> iterator = values.iterator();
        while (iterator.hasNext()){
            Client client = iterator.next();
            if (client.getSocketChannel().equals(channel)){
                iterator.remove();
                return client;
            }
        }
        return null;
    }
}