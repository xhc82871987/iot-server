package com.dao;

import com.domain.SocketTiming;
import java.sql.SQLException;
import java.util.List;

/**
 * 管理数据库中智能插座的定时信息
 **/
public interface SocketTimingDAO {
    /**
     * 获取指定设备的定时信息
     * @param socketID 要获取定时信息的设备的编号
     * @return 返回查询到的该设备的所有的定时信息
     * @throws SQLException 当数据库操作异常时抛出该异常
     */
    List<SocketTiming> getTiming(String socketID) throws SQLException;
    /**
     * 获取指定编号的定时信息
     * @param timingID 要获取的定时信息的记录
     * @return 返回查询到的定时信息
     * @throws SQLException 当数据库操作异常时抛出该异常
     */
    SocketTiming getTiming(Long timingID) throws SQLException;
    /**
     * 给指定设备添加一条定时信息
     * @param timing 要添加的定时信息
     * @throws SQLException 当数据库操作异常时抛出该异常
     */
    void addTiming(SocketTiming timing) throws SQLException;
    /**
     * 更新定时信息
     * @param timing 要更新的定时信息
     * @throws SQLException 当数据库操作异常时抛出该异常
     */
    void updateTiming(SocketTiming timing) throws SQLException;
    /**
     * 删除一条定时信息
     * @param timingID 要删除的定时的编号
     * @throws SQLException 当数据库操作异常时抛出该异常
     */
    void deleteTiming(Long timingID) throws SQLException;
    /**
     * 删除指定设备的定时信息
     * @param socketID 要删除定时的设备编号
     * @throws SQLException 当数据库操作异常时抛出该异常
     */
    void deleteTiming(String socketID) throws SQLException;
    /**
     * 修改定时的状态信息
     * @param time 定时的时间
     * @param status 定时的状态
     * @throws SQLException 当数据库操作异常时抛出该异常
     */
    void alterStatus(String socketID, String time, Byte status) throws SQLException;
}
