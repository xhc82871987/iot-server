package com.dao;

import com.domain.Socket;
import com.domain.User;
import java.sql.SQLException;
import java.util.List;

/**
 * 管理数据库中的用户信息
 */
public interface UserDAO {
    /**
     * 直接添加新用户
     * @throws SQLException 操作失败抛出异常
     */
    void addUser() throws SQLException;
    /**
     * 添加新用户
     * @param user 要添加的用户
     * @throws SQLException 操作失败抛出异常
     */
    void addUser(User user) throws SQLException;
    /**
     * 注销用户
     * @param userID 要注销的用户
     * @throws SQLException
     */
    void deleteUser(String userID) throws SQLException;
    /**
     * 检查用户是否已经存在
     * @param userID
     * @return true:存在，false：不存在
     * @throws SQLException 操作失败抛出异常
     */
    boolean existUser(String userID) throws SQLException;
    /**
     * 根据用户名获取用户信息
     * @param userID
     * @return 返回查找到的结果，有则返回User对象，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    User getUser(String userID) throws SQLException;
    /**
     * 根据手机号获取用户信息
     * @param phoneNumber 用户绑定的手机号码
     * @return 返回查找到的结果，有则返回User对象，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    User getUserByPhone(String phoneNumber) throws SQLException;
    /**
     * 根据email获取用户信息
     * @param email 用户绑定的邮箱账号
     * @return 返回查找到的结果，有则返回User对象，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    User getUserByEmail(String email) throws SQLException;
    /**
     * 根据微信的unionid获取用户信息
     * @param unionID 用户绑定的微信的unionID
     * @return 返回查找到的结果，有则返回User对象，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    User getUserByUnionid(String unionID) throws SQLException;
    /**
     * 根据QQ的openid来获取用户信息
     * @param openid 用户绑定的QQ号对应的openid
     * @return 返回查找到的结果，有则返回User对象，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    User getUserByOpenid(String openid) throws SQLException;
    /**
     * 获取所有的用户信息
     * @return
     * @throws SQLException
     */
    List<User> getAllUser() throws SQLException;
    /**
     * 获取已经注册的用户的数量
     * @return 返回已经注册的用户的用户数量
     * @throws SQLException
     */
    int getUserCounts() throws SQLException;
    /**
     * 获取用户绑定的所有设备
     * @param userID
     * @return 以List集合形式返回，若无设备信息，则list的size（）方法返回0
     * @throws SQLException 操作失败抛出异常
     */
    List<Socket> getSockets(String userID) throws SQLException;
    /**
     * 查看用户名是否更改过
     * @param userID
     * @return true：更改过，false：未更改过
     * @throws SQLException 操作失败抛出异常
     */
    boolean isAlterdUserID(String userID) throws SQLException;
    /**
     * 更改用户名，用户若是调用第三方接口登陆，系统会随机分配一个用户名，
     * 用户可以有一次机会更改该用户名
     * @param oldUserID 旧的ID
     * @param newUserID 新的ID号
     * @throws SQLException 操作失败抛出异常
     */
    void alterUserID(String oldUserID, String newUserID) throws SQLException;
    /**
     * 获取用户昵称
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getNickName(String userID) throws SQLException;
    /**
     * 修改用户昵称
     * @param userID
     * @param newNickName 新的昵称
     * @throws SQLException 操作失败抛出异常
     */
    void alterNickName(String userID, String newNickName) throws SQLException;
    /**
     * 获取密码
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getPassword(String userID) throws SQLException;
    /**
     * 修改密码
     * @param userID
     * @param newPassword
     * @throws SQLException 操作失败抛出异常
     */
    void alterPassword(String userID, String newPassword) throws SQLException;
    /**
     * 获取头像地址
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getHeadImgURL(String userID) throws SQLException;
    /**
     * 修改头像地址
     * @param userID
     * @param newURL 新的头像地址
     * @throws SQLException 操作失败抛出异常
     */
    void alterHeadImgURL(String userID, String newURL) throws SQLException;
    /**
     * 获取性别
     * @param userID
     * @return 0：未设置，1：男性，2：女性
     * @throws SQLException 操作失败抛出异常
     */
    byte getSex(String userID) throws SQLException;
    /**
     * 修改性别
     * @param userID
     * @param newSex
     * @throws SQLException 操作失败抛出异常
     */
    void alterSex(String userID, byte newSex) throws SQLException;
    /**
     * 获取生日信息
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getBirth(String userID) throws SQLException;
    /**
     * 修改生日信息
     * @param userID
     * @param newBirth
     * @throws SQLException 操作失败抛出异常
     */
    void alterBirth(String userID, String newBirth) throws SQLException;
    /**
     * 获取手机号码
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getPhoneNumber(String userID) throws SQLException;
    /**
     * 修改手机号码
     * @param userID
     * @param newPhoneNumber
     * @throws SQLException 操作失败抛出异常
     */
    void alterPhoneNumber(String userID, String newPhoneNumber) throws SQLException;
    /**
     * 获取邮箱账号
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getEmail(String userID) throws SQLException;
    /**
     * 修改邮箱账号
     * @param userID
     * @param newEmail
     * @throws SQLException 操作失败抛出异常
     */
    void alterEmail(String userID, String newEmail) throws SQLException;
    /**
     * 获取注册时间
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getRegisterTime(String userID) throws SQLException;
    /**
     * 获取用户的地址
     * @param userID
     * @return 有则返回，无返回null
     * @throws SQLException 操作失败抛出异常
     */
    String getAddress(String userID) throws SQLException;
    /**
     * 修改地址
     * @param userID
     * @param newAddress
     * @throws SQLException 操作失败抛出异常
     */
    void alterAddress(String userID, String newAddress) throws SQLException;
    /**
     * 与对应的设备解除绑定
     * @param socketID
     * @throws SQLException 操作失败抛出异常
     */
    void unbindSocket(String userID, String socketID) throws SQLException;
    /**
     *  解除该用户与所有的设备的绑定
     * @param userID
     * @throws SQLException 操作失败抛出异常
     */
    void unbindAllSocket(String userID) throws SQLException;

}
