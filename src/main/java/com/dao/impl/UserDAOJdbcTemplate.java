package com.dao.impl;

import com.dao.UserDAO;
import com.domain.Socket;
import com.domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
/**
 * 使用Spring的JdbcTemplate方式实现UserDAO接口
 */
public class UserDAOJdbcTemplate implements UserDAO {
    private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public boolean existUser(String userID) throws SQLException {
        String sql="select count(userID) from user where userID=?";
        Integer count=0;
        try{
            count = jdbcTemplate.queryForObject(sql,Integer.class,userID);
        }catch(IncorrectResultSizeDataAccessException e){
            count= 0;
        }catch (DataAccessException e){
            throw e;
        }
        return count>0?true:false;
    }
    @Override
    public User getUser(String userID) throws SQLException {
        String selectUser="select * from user where userID=?";
        User user = null;
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        try{
            user=jdbcTemplate.queryForObject(selectUser,userRowMapper,userID);
            return user;
        }catch (IncorrectResultSizeDataAccessException e){
            user= null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return user;
    }
    @Override
    public synchronized void addUser() throws SQLException {
        LocalDateTime time = LocalDateTime.now();
        String id =""+time.getYear()+time.getDayOfYear()+time.getHour()+time.getMinute()+time.getSecond();
        int random = ((int)(Math.random()*1000))%1000;
        id+=random;
        String sql="insert into user(userID,isAlterdUserID,registerTime) values(?,?,?)";
        try{
            jdbcTemplate.update(sql,id,0,new Date());
        }catch (DataAccessException e){
            throw e;
        }
    }
    @Override
    public synchronized void addUser(User user) throws SQLException {
        String sql="insert into user(userID,isAlterdUserID,nickName,password,registerTime,openid,unionid) values(?,?,?,?,?,?,?)";
        try{
            jdbcTemplate.update(sql,user.getUserID(),0,user.getNickName(),"123456", new Date(),user.getOpenid(),user.getUnionid());
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public synchronized void deleteUser(String userID) throws SQLException {
        String sql = "delete from user where userID = ?";
        try{
            jdbcTemplate.update(sql,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public User getUserByPhone(String phoneNumber) throws SQLException {
        String selectUser="select * from user where phoneNumber=?";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        User user=null;
        try{
            user=jdbcTemplate.queryForObject(selectUser,userRowMapper,phoneNumber);
        }catch (IncorrectResultSizeDataAccessException e){
            user= null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return user;
    }
    @Override
    public User getUserByEmail(String email) throws SQLException {
        String selectUser="select * from user where email=?";
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        User user=null;
        try{
            user=jdbcTemplate.queryForObject(selectUser,userRowMapper,email);
        }catch (IncorrectResultSizeDataAccessException e){
            user= null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return user;
    }
    @Override
    public User getUserByUnionid(String unionid) throws SQLException {
        System.out.println("getUserByUnionid");
        String sql="select userID from user where unionid=?";
        User user = null;
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        try{
            user = jdbcTemplate.queryForObject(sql,userRowMapper,unionid);
        }catch (IncorrectResultSizeDataAccessException e){
            user= null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return user;
    }
    @Override
    public User getUserByOpenid(String openid) throws SQLException {
        String sql="select userID from user where openid=?";
        User user = null;
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        try{
            user = jdbcTemplate.queryForObject(sql,userRowMapper,openid);
        }catch (IncorrectResultSizeDataAccessException e){
            user= null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return user;
    }

    @Override
    public List<User> getAllUser() throws SQLException {
        List<User> users=null;
        String sql= "select * from user";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        try{
            users = jdbcTemplate.query(sql,rowMapper);
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return users;
    }
    @Override
    public int getUserCounts() throws SQLException {
        Integer count = null;
        String sql = "select count(*) from user";
        try{
            count = jdbcTemplate.queryForObject(sql,Integer.class);
        }catch (IncorrectResultSizeDataAccessException e){
            count = 0;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return count;
    }
    @Override
    public List<Socket> getSockets(String userID) throws SQLException {
        String selectSockets="select * from wifiSocket where socketID in (select socketID from userAndSocketBind where userID = ?)";
        RowMapper<Socket> socketRowMapper=new BeanPropertyRowMapper<>(Socket.class);
        List<Socket> sockets=null;
        try{
            sockets= jdbcTemplate.query(selectSockets,socketRowMapper,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return sockets;
    }
    @Override
    public boolean isAlterdUserID(String userID) throws SQLException {
        String sql="select isAlterdUserID from user where userID=?";
        Byte num=0;
        try{
            if (existUser(userID)){
                num = jdbcTemplate.queryForObject(sql,Byte.class,userID);
            }
        }catch (IncorrectResultSizeDataAccessException e){
            num= 0;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return num==1?true:false;
    }
    @Override
    public void alterUserID(String oldUserID, String newUserID) throws SQLException {
        try{
            if (existUser(oldUserID)&&!isAlterdUserID(oldUserID)){
                String sql="update user set userID=?,isAlterdUserID=1 where userID=?";
                jdbcTemplate.update(sql,newUserID,oldUserID);
            }
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getNickName(String userID) throws SQLException {
        String nickName=null;
        try{
            String sql="select nickName from user where userID=?";
            nickName=jdbcTemplate.queryForObject(sql,String.class,userID);
        }catch (IncorrectResultSizeDataAccessException e){
            nickName=null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return nickName;
    }
    @Override
    public void alterNickName(String userID, String newNickName) throws SQLException {
        String sql="update user set nickName=? where userID=?";
        try{
            jdbcTemplate.update(sql,newNickName,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getPassword(String userID) throws SQLException {
        String password=null;
        try{
            String sql="select password from user where userID=?";
            password=jdbcTemplate.queryForObject(sql,String.class,userID);
        }catch (IncorrectResultSizeDataAccessException e){
            password=null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return password;
    }
    @Override
    public void alterPassword(String userID, String newPassword) throws SQLException {
        try{
            String sql="update user set password=? where userID=?";
            jdbcTemplate.update(sql,newPassword,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getHeadImgURL(String userID) throws SQLException {
        String url="";
        try{
            String sql="select headImg from user where userID=?";
            url=jdbcTemplate.queryForObject(sql,String.class,userID);
        }catch (IncorrectResultSizeDataAccessException e){
            url=null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return url;
    }
    @Override
    public void alterHeadImgURL(String userID, String newURL) throws SQLException {
        try{
            String sql="update user set headImg=? where userID=?";
            jdbcTemplate.update(sql,newURL,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public byte getSex(String userID) throws SQLException{
        Byte sex=0;
        try{
            String sql="select sex from user where userID=?";
            sex=jdbcTemplate.queryForObject(sql,Byte.class,userID);
        }catch (IncorrectResultSizeDataAccessException e){
            sex=0;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return sex;
    }
    @Override
    public void alterSex(String userID, byte newSex) throws SQLException {
        try{
            String sql="update user set sex=? where userID=?";
            jdbcTemplate.update(sql,newSex,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getBirth(String userID) throws SQLException {
        String birth=null;
        try{
            String sql="select birth from user where userID=?";
            birth = jdbcTemplate.queryForObject(sql,String.class,userID);
        }catch (IncorrectResultSizeDataAccessException e){
            birth="";
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return birth;
    }
    @Override
    public void alterBirth(String userID, String newBirth) throws SQLException {
        try{
            String sql="update user set birth=? where userID=?";
            jdbcTemplate.update(sql,newBirth,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getPhoneNumber(String userID) throws SQLException {
        String phone=null;
        try{
            String sql="select phoneNumber from user where userID=?";
            phone=jdbcTemplate.queryForObject(sql,String.class,userID);

        }catch (IncorrectResultSizeDataAccessException e){
            phone="";
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return phone;
    }
    @Override
    public void alterPhoneNumber(String userID, String newPhoneNumber) throws SQLException {
        try{
            String sql="update user set phoneNumber=? where userID=?";
            jdbcTemplate.update(sql,newPhoneNumber,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getEmail(String userID) throws SQLException {
        String email=null;
        try{
            String sql="select email from user where userID=?";
            email=jdbcTemplate.queryForObject(sql,String.class,userID);

        }catch (IncorrectResultSizeDataAccessException e){
            email="";
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return email;
    }
    @Override
    public void alterEmail(String userID, String newEmail) throws SQLException {
        try{
            String sql="update user set email=? where userID=?";
            jdbcTemplate.update(sql,newEmail,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getRegisterTime(String userID) throws SQLException {
        String registerTime=null;
        try{
            String sql="select registerTime from user where userID=?";
            registerTime=jdbcTemplate.queryForObject(sql,String.class,userID);
        }catch (IncorrectResultSizeDataAccessException e){
            registerTime="";
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return registerTime;
    }
    @Override
    public String getAddress(String userID) throws SQLException {
        String address=null;
        try{
            String sql="select address from user where userID=?";
            address=jdbcTemplate.queryForObject(sql,String.class,userID);

        }catch (IncorrectResultSizeDataAccessException e){
            address = "";
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return address;
    }
    @Override
    public void alterAddress(String userID, String newAddress) throws SQLException {
        try{
            String sql="update user set address=? where userID=?";
            jdbcTemplate.update(sql,newAddress,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public void unbindSocket(String userID, String socketID) throws SQLException {
        try{
            String sql="delete from userAndSocketBind where userID=? AND socketID=?";
            jdbcTemplate.update(sql,userID,socketID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public void unbindAllSocket(String userID) throws SQLException {
        try{
            String sql="delete from userAndSocketBind where userID=?";
            jdbcTemplate.update(sql,userID);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
}
