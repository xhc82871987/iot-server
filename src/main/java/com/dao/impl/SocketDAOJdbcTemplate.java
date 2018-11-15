package com.dao.impl;

import com.dao.SocketDAO;
import com.domain.Socket;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
/**
 * SocketDAO接口的实现类，使用Spring框架中的JdbcTemplate实现了SocketDAO接口中方法
 */
public class SocketDAOJdbcTemplate implements SocketDAO {
    private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void addSocket(Socket socket)  throws SQLException{
        String sql = "insert into wifiSocket(socketID,name,inviteID,connect,status) values(?,?,?,?,?)";
        try{
            jdbcTemplate.update(sql,socket.getSocketID(),socket.getName(),socket.getInviteID(),socket.getConnect(),
                    socket.getStatus());
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    //此处要添加事务
    @Transactional(propagation = Propagation.REQUIRED ,
            isolation = Isolation.DEFAULT,
            rollbackFor = {DataAccessException.class})
    @Override
    public void addSockets(List<Socket> sockets) throws SQLException {
        String sql = "insert into wifiSocket(socketID,name,inviteID,connect,status) values(?,?,?,?,?)";
        try{
           jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1,sockets.get(i).getSocketID());
                    ps.setString(2,sockets.get(i).getName());
                    ps.setString(3,sockets.get(i).getInviteID());
                    ps.setByte(4,sockets.get(i).getConnect());
                    ps.setByte(5,sockets.get(i).getStatus());
                }
                @Override
                public int getBatchSize() {
                    return sockets.size();
                }
            });
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public Socket getSocket(String socketID) throws SQLException {
        Socket socket=null;
        String sql="select * from wifiSocket where socketID=?";
        try {
            RowMapper<Socket> rowMapper = new BeanPropertyRowMapper<>(Socket.class);
            socket=jdbcTemplate.queryForObject(sql,rowMapper,socketID);
        }catch (IncorrectResultSizeDataAccessException e){
            socket=null;
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
        return socket;
    }
    @Override
    public List<Socket> getAllSockets() throws SQLException {
        List<Socket> sockets = null;
        RowMapper<Socket> rowMapper = new BeanPropertyRowMapper<>(Socket.class);
        String sql = "select * from wifiSocket";
        try{
            sockets = jdbcTemplate.query(sql,rowMapper);
        }catch(DataAccessException e){
            throw new SQLException();
        }
        return sockets;
    }
    @Override
    public int getSocketCounts() throws SQLException {
        Integer count = null;
        String  sql = "select count(*) from wifiSocket";
        try {
            count = jdbcTemplate.queryForObject(sql,Integer.class);
        }catch (IncorrectResultSizeDataAccessException e){
            count = 0;
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
        return count;
    }
    @Override
    public List<String> getUserID(String socketID) throws SQLException {
        List<String> usersID=null;
        String sql="select userID from userAndSocketBind where socketID=?";
        try {
            usersID=jdbcTemplate.queryForList(sql,String.class,socketID);
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
        return usersID;
    }
    @Override
    public boolean isConnect(String socketID) throws SQLException {
        String sql="select connect from wifiSocket where socketID=?";
        Byte num=null;
        try{
            num=jdbcTemplate.queryForObject(sql,Byte.class,socketID);
        }catch (IncorrectResultSizeDataAccessException e){
            num = 0;
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
        if (num == 1){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public void setConnect(String socketID, byte status) throws SQLException {
        String sql="update wifiSocket set connect=? where socketID=?";
        try{
            jdbcTemplate.update(sql,status,socketID);
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public String getInviteID(String socketID) throws SQLException {
        String invite=null;
        String sql="select inviteID from wifiSocket where socketID=?";
        try{
            invite=jdbcTemplate.queryForObject(sql,String.class,socketID);
        }catch (IncorrectResultSizeDataAccessException e){
            invite = "";
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
        return invite;
    }
    @Override
    public void setInviteID(String socketID, String id) throws SQLException {
        String sql="update wifiSocket set inviteID=? where socketID=?";
        try{
            jdbcTemplate.update(sql,id,socketID);
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public String getName(String socketID) throws SQLException {
        String name="";
        String sql="select name from wifiSocket where socketID=?";
        try{
            name=jdbcTemplate.queryForObject(sql,String.class,socketID);
        }catch (IncorrectResultSizeDataAccessException e){
            name = "";
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
        return name;
    }
    @Override
    public void alterName(String socketID, String newName) throws SQLException {
        String sql="update wifiSocket set name=? where socketID=?";
        try{
            jdbcTemplate.update(sql,newName,socketID);
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public byte getStatus(String socketID) throws SQLException {
        Byte status=0;
        String sql="select status from wifiSocket where socketID=?";
        try{
            status=jdbcTemplate.queryForObject(sql,Byte.class,socketID);
        }catch (IncorrectResultSizeDataAccessException e){
            status = 0;
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
        return status;
    }
    @Override
    public void alterStatus(String socketID, byte status) throws SQLException {
        System.out.println(new Date()+" -->dao 修改设备状态："+socketID+" -- " +status);
        String sql="update wifiSocket set status = ? where socketID=?";
        try{
            jdbcTemplate.update(sql,status,socketID);
            System.out.println(new Date()+ "--> dao 修改设备状态成功："+socketID+" -- " +status);
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public void bind(String userID, String socketID) throws SQLException {
        String sql = "insert into userAndSocketBind values (?,?,?)";
        try{
            jdbcTemplate.update(sql,userID,socketID,new Date());
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public void bindWithInvited(String userID, String invited) throws SQLException {
        String socketID=null;
        String getsocketID="select socketID from wifiSocket where inviteID = ?";
        String sql = "insert into userAndSocketBind(userID,socketID,bindTime) values (?,?,?)";
        try{
            socketID = jdbcTemplate.queryForObject(getsocketID,String.class,invited);
            jdbcTemplate.update(sql,userID,socketID,new Date());
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public void unbind(String userID, String socketID) throws SQLException {
        String sql="delete from userAndSocketBind where userID=? AND socketID=?";
        try{
            jdbcTemplate.update(sql,userID,socketID);
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
    @Override
    public String getMaxID() throws SQLException {
        String sql="select IFNULL( MAX(socketID),'') maxID from wifiSocket";
        String maxID=null;
        try {
            maxID = jdbcTemplate.queryForObject(sql,String.class);
        }catch (IncorrectResultSizeDataAccessException e){
            maxID = "000000";
        }catch (DataAccessException e){
            throw  new SQLException(e.getMessage());
        }
        return maxID;
    }
    @Override
    public void unbindAll(String socketID) throws SQLException {
        String sql="delete from userAndSocketBind where socketID=?";
        try{
            jdbcTemplate.update(sql,socketID);
        }catch (DataAccessException e){
            throw new SQLException(e.getMessage());
        }
    }
}
