package com.tcp.client;

import com.tcp.utils.ParseData;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Date;

/**
 * 设备端对象的统一接口，提供了获取设备标识，获取设备和服务器间建立的SocketChannel，以及判断设备是否在线，
 * 向设备发送数据和解析设备端的数据。
 */
public interface Client {
    /**
     * 获取该客户端的标识
     * @return 返回客户端的标识信息
     */
    public String getId();
    /**
     * 设置客户端标识
     * @param Id
     */
    public void setId(String Id);
    /**
     * 获取客户端的通道
     * @return 返回客户端与服务器建立的Channel对象
     */
    public SocketChannel getSocketChannel();
    /**
     * 设置客户端channel
     * @param channel
     */
    public void setSocketChannel(SocketChannel channel);
    /**
     * 判断客户端连接是否正常
     * @return true 正常连接，false 与客户端失去连接
     */
    public boolean isConnect();
    /**
     * 设置设备是否处于连接状态
     * @param status
     */
    public void setConnect(boolean status);
    /**
     * 向客户端发送数据
     * @param data 要发送的数据，以byte数组形式发送
     */
    public void sendData(byte[] data) throws IOException;
    /**
     *处理客户端设备的请求
     */
    public void doRequest(ParseData parseData) throws IOException;
    /**
     * 获取设备最近一次传输数据的时间
     * @return 最近一次接收到数据的时间
     */
    public Date getLastReceiveTime();
    /**
     * 更新最后一次收到数据的时间
     */
    public void updateLastReceiveTime();
    /**
     * 用于在创建了对象后初始化该对象的一些信息
     */
    public void init() throws IOException;
}
