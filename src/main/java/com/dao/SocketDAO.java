package com.dao;

import com.domain.Socket;
import com.domain.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 对设备信息进行数据库操作
 */
public interface SocketDAO {
    /**
     * 向数据库中添加socket
     * @param socket
     */
    void addSocket(Socket socket) throws SQLException;
    /**
     * 向数据库中添加多个socket
     * @param sockets
     */
    void addSockets(List<Socket> sockets) throws SQLException;
    /**
     * 根据设备ID获取设备信息
     * @param socketID 要获取信息的设备ID
     * @return 返回查询到的设备信息，若有则返回，否则返回null
     * @throws SQLException 操作失败抛出异常
     */
    Socket getSocket(String socketID) throws SQLException;
    /**
     * 获取所有的设备信息
     * @return
     * @throws SQLException
     */
    List<Socket> getAllSockets() throws SQLException;
    /**
     * 获取设备数量
     * @return 返回数据库中设备的数量
     * @throws SQLException
     */
    int getSocketCounts() throws SQLException;
    /**
     * 获取该设备绑定的用户信息
     * @param socketID 设备ID
     * @return 返回查询到的用户信息，以集合形式返回
     * @throws SQLException 操作失败抛出异常
     */
    List<String> getUserID(String socketID) throws SQLException;
    /**
     * 查看设备是否在线
     * @param socketID
     * @return true:设备在线，false：设备不再线
     * @throws SQLException 操作失败抛出异常
     */
    boolean isConnect(String socketID) throws SQLException;
    /**
     * 当设备上线或者下线时，修改设备的状态信息
     * @param socketID 要修改的设备
     * @param status 要修改的状态
     * @throws SQLException 操作失败抛出异常
     */
    void setConnect(String socketID, byte status) throws SQLException;
    /**
     * 获取设备的邀请码，用于分享给其他用户来绑定该设备
     * @param socketID 要获取的设备
     * @return 返回查询到的邀请码，若无，返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getInviteID(String socketID) throws SQLException;
    /**
     * 设置设备的邀请码
     * @param socketID 需要设置邀请码的设备
     * @param id 邀请码
     * @throws SQLException 操作失败抛出异常
     */
    void setInviteID(String socketID, String id) throws SQLException;
    /**
     * 获取设备的名称
     * @param socketID
     * @return 有则返回，无则返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getName(String socketID) throws SQLException;
    /**
     * 设置/修改设备的名称
     * @param socketID
     * @param newName 要给设备修改/设置的名字
     * @throws SQLException 操作失败抛出异常
     */
    void alterName(String socketID, String newName) throws SQLException;
    /**
     * 获取设备状态信息
     * @param socketID 设备ID
     * @return 返回设备状态 1：通电状态，0：断点状态
     * @throws SQLException 操作失败抛出异常
     */
    byte getStatus(String socketID) throws SQLException;
    /**
     * 设置设备状态信息
     * @param socketID 设备ID
     * @param status 设备状态，可以有两种状态，STATUS_ON和STATUS_OFF
     *@throws SQLException 操作失败抛出异常
     */
    void alterStatus(String socketID, byte status) throws SQLException;
//    /**
//     * 获取给设备设置的定时信息
//     * @param socketID 设备ID
//     * @return 以map的形式返回，其中key有，begin（开始时间），end（结束时间），若没有定时信息则map的size为0
//     * @throws SQLException 操作失败抛出异常
//     */
//    Map<String,String> getTiming(String socketID) throws SQLException;
//    /**
//     * 添加定时
//     * @param begin 定时的开始时间
//     * @param end 定时的结束时间
//     *@throws SQLException 操作失败抛出异常
//     */
//    void addTiming(String socketID, String begin, String end) throws SQLException;
//    /**
//     * 删除定时信息
//     * @param socketID 设备ID
//     * @throws SQLException 操作失败抛出异常
//     */
//    void deleteTiming(String socketID) throws SQLException;
    /**
     * 根据用户名和设备编号绑定设备
     * @param userID 用户名
     * @param socketID 设备编号
     * @throws SQLException 当执行sql语句异常时抛出
     */
    void bind(String userID, String socketID) throws SQLException;
    /**
     * 根据用户名和邀请码绑定设备
     * @param userID 用户名
     * @param invited 设备邀请码
     * @throws SQLException 当执行sql语句异常时抛出
     */
    void bindWithInvited(String userID, String invited) throws SQLException;
    /**
     * 解除指定的设备与所有用户的绑定
     * @param socketID 设备ID
     * @throws SQLException 操作失败抛出异常
     */
    void unbindAll(String socketID) throws SQLException;
    /**
     * 解除用户与设备的绑定
     * @param userID
     * @param socketID
     * @throws SQLException 操作失败抛出异常
     */
    void unbind(String userID, String socketID) throws SQLException;
    /**
     * 获取按ASCII码排序最大的ID值
     * @return
     * @throws SQLException
     */
    String getMaxID() throws SQLException;
}
