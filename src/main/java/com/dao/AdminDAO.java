package com.dao;

import com.domain.Admin;
import com.domain.Socket;
import com.domain.User;

import java.sql.SQLException;
import java.util.List;
/**
 * 管理数据库中管理员的账户信息
 */
public interface AdminDAO {
    /**
     * 获取管理员信息
     * @param id 管理员账号
     * @return 返回查询到的结果，若返回null则表示没有该用户
     * @throws SQLException 操作失败抛出异常
     */
    Admin getAdmin(String id) throws SQLException;

    /**
     * 修改姓名
     * @param id 需要修改姓名的账号
     * @param name 将要修改的名字
     * @throws SQLException 操作失败抛出异常
     */
    void alterName(String id, String name) throws SQLException;
    /**
     * 修改管理员账号的生日信息
     * @param id 需要修改生日的账号
     * @param birth 生日
     * @throws SQLException 操作失败抛出异常
     */
    void alterBirth(String id, String birth) throws SQLException;
    /**
     * 获取管理员密码
     * @param id 管理员账号
     * @return 返回查询到的密码
     * @throws SQLException 操作失败抛出异常
     */
    String getPassword(String id) throws SQLException;
    /**
     * 修改管理员密码
     * @param id 管理员账号
     * @throws SQLException 操作失败抛出异常
     */
    void alterPassword(String id, String newPassword) throws SQLException;
    /**
     * 获取管理员性别
     * @param id
     * @return 0：未设置，1:男，2：女
     * @throws SQLException 操作失败抛出异常
     */
    byte getSex(String id) throws SQLException;
    /**
     * 修改性别
     * @param id
     * @throws SQLException 操作失败抛出异常
     */
    void alterSex(String id, byte newSex) throws SQLException;
    /**
     * 获取账号绑定的手机号码
     * @param id
     * @return 有则返回，无则返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getPhone(String id) throws SQLException;
    /**
     * 修改手机号码
     * @param id
     * @throws SQLException 操作失败抛出异常
     */
    void alterPhone(String id, String newNumber) throws SQLException;
    /**
     * 获取账号绑定的邮箱账号
     * @param id
     * @return 有则返回，无则返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getEmail(String id) throws SQLException;
    /**
     * 修改账号绑定的邮箱账号
     * @param id
     * @throws SQLException 操作失败抛出异常
     */
    void alterEmail(String id, String newCount) throws SQLException;
}
