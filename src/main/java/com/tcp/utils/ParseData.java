package com.tcp.utils;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * 处理客户端和服务器之间的数据，包括读取数据，分包，判断是否丢包，以及判断数据包类型
 **/
public interface ParseData {
    /*
     *  数据包类型
     */
    /**
     * 表示该数据包是心跳包
     */
    public final static int HEART_PACKAGE=0;
    /**
     * 表示该数据是连接信息数据包
     */
    public final static int CONNECT_PACKAGE=1;
    /**
     * 表示该数据包是普通的数据包
     */
    public final static int DATA_PACKAGE=2;
    /**
     * 表示该数据包是控制数据包
     */
    public final static int CONTROL_PACKAGE=3;
    /*
     *  数据包格式
     */
    /**
     * 数据包头部编号
     */
    public final int PACKAGE_HEAD=0X11;
    /**
     * 数据包尾部编号
     */
    public final int PACKAGE_TAIL=0X16;
    /**
     * 检查传入的数据包是否是完整的数据包
     * @param data 传入的待检测的数据包
     * @return 返回检测的结果，若是正确的数据包返回true，否则返回false
     */
    public boolean isComplete(List<Byte> data);
    /**
     * 将传入的数据分割成单个的数据包，若传入的数据中包含多个数据包则返回包含多个数据包的集合
     * 若只有一条数据包则返回只有一条数据包的集合
     * @param data 要分割的数据
     * @return 返回数据包集合
     */
    public List<List<Byte>> getPackages(List<Byte> data);
    /**
     * 判断数据包类型，有HEART_PACKAGE，CONNECT_PACKAGE，DATA_PACKAGE，CONTROL_PACKAGE，4类
     * @param data 要判断的数据包
     * @return 返回数据包类型
     */
    public int getDataType(List<Byte> data);
    /**
     * 读取客户端发送过来的数据
     * @param channel 客户端和服务器之间建立的连接
     * @return 返回本次读取到的信息
     */
    public List<Byte> getData(SocketChannel channel) throws IOException;
}
