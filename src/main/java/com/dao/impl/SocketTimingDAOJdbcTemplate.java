package com.dao.impl;

import com.dao.SocketTimingDAO;
import com.domain.SocketTiming;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
/**
 * 使用JdbcTemplate数据库操作类实现了SocketTimingDAO接口
 **/
public class SocketTimingDAOJdbcTemplate implements SocketTimingDAO {
    private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<SocketTiming> getTiming(String socketID) throws SQLException {
        System.out.println(new Date()+" --> dao getTiming-socketID:"+socketID);
        List<SocketTiming> timings = null;
        String sql = "select * from socketTiming where socketID = ?";
        RowMapper<SocketTiming> rowMapper = new BeanPropertyRowMapper<>(SocketTiming.class);
        try{
            timings = jdbcTemplate.query(sql,rowMapper,socketID);
        }catch (DataAccessException e){
            throw e;
        }finally {
            System.out.println(new Date()+" --> dao getTiming:"+timings.toString());
            return timings;
        }
    }
    @Override
    public SocketTiming getTiming(Long timingID) throws SQLException {
        System.out.println(new Date()+" --> dao getTiming-id:"+timingID);
        SocketTiming timing = null;
        String sql = "select * from socketTiming where id = ?";
        RowMapper<SocketTiming> rowMapper = new BeanPropertyRowMapper<>(SocketTiming.class);
        try{
            timing = jdbcTemplate.queryForObject(sql,rowMapper,timingID);
        }catch (IncorrectResultSizeDataAccessException e){
            timing=null;
        }catch (DataAccessException e){
            throw new SQLException();
        }finally {
            System.out.println(new Date()+" --> dao getTiming:"+timing);
            return timing;
        }
    }
    @Override
    public void addTiming(SocketTiming timing) throws SQLException {
        System.out.println(new Date()+" --> dao addTiming:"+timing);
        String sql="insert into socketTiming(socketID,time,operate,status,repeate) values(?,?,?,?,?)";
        try{
            jdbcTemplate.update(sql,timing.getSocketID(),timing.getTime(),timing.getOperate(),timing.getStatus(),timing.getRepeate());
            System.out.println(new Date()+" --> dao addTiming-OK");
        }catch (DataAccessException e){
            throw e;
        }
    }
    @Override
    public void updateTiming(SocketTiming timing) throws SQLException {
        System.out.println(new Date()+" --> dao updateTiming:"+timing);
        String sql="update socketTiming set time=?,operate=?,status=?,repeate=? where id=?";
        try{
            jdbcTemplate.update(sql,timing.getTime(),timing.getOperate(),timing.getStatus(),timing.getRepeate(),timing.getId());
            System.out.println(new Date()+" --> dao updateTiming-OK");
        }catch (DataAccessException e){
            throw e;
        }
    }
    @Override
    public void deleteTiming(Long timingID) throws SQLException {
        System.out.println(new Date()+" --> dao deleteTiming:"+timingID);
        String sql = "delete from socketTiming where id = ?";
        try{
            jdbcTemplate.update(sql,timingID);
            System.out.println(new Date()+" --> dao deleteTiming-OK");
        }catch (DataAccessException e){
            throw e;
        }
    }
    @Override
    public void deleteTiming(String socketID) throws SQLException {
        System.out.println(new Date()+" --> dao deleteTiming-all:"+socketID);
        String sql = "delete from socketTiming where socketID = ?";
        try{
            jdbcTemplate.update(sql,socketID);
            System.out.println(new Date()+" --> dao deleteTiming-all-OK");
        }catch (DataAccessException e){
            throw e;
        }
    }
    @Override
    public void alterStatus(String socketID,String time, Byte status) throws SQLException {
        String time1 = time+"%";
        String sql="update socketTiming set status=? where time like ? and socketID=?";
        System.out.println(new Date()+" --> dao alterStatus:"+socketID+" time:"+time+",status:"+status);
        try{
            jdbcTemplate.update(sql,status,time1,socketID);
            System.out.println(new Date()+" --> dao alterStatus-OK");
        }catch (DataAccessException e){
            throw e;
        }
    }
}
