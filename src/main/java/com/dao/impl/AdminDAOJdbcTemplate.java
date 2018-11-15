package com.dao.impl;

import com.dao.AdminDAO;
import com.domain.Admin;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;
/**
 * 使用Spring的JdbcTemplate方式实现AdminDAO接口
 */
public class AdminDAOJdbcTemplate implements AdminDAO {
    private JdbcTemplate jdbcTemplate;
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Admin getAdmin(String id) throws SQLException {
        String sql="select * from admin where adminID=?";
        RowMapper<Admin> rowMapper =new BeanPropertyRowMapper<>(Admin.class);
        Admin admin=null;
        try{
            admin = jdbcTemplate.queryForObject(sql,rowMapper,id);
        }catch (IncorrectResultSizeDataAccessException e){
            admin = null;
        }catch (DataAccessException e){
            throw new SQLException("未查到指定的管理员信息");
        }
        return admin;
    }
    @Override
    public void alterName(String id, String name) throws SQLException {
        String sql = "update admin set name = ? where adminID = ?";
        try{
            jdbcTemplate.update(sql,name,id);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }

    @Override
    public void alterBirth(String id, String birth) throws SQLException {
        String sql = "update admin set birth = ? where adminID = ?";
        try{
            jdbcTemplate.update(sql,birth,id);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getPassword(String id) throws SQLException {
        String sql="select password from admin where adminID=?";
        String password=null;
        try {
            password = jdbcTemplate.queryForObject(sql,String.class,id);
        }catch (IncorrectResultSizeDataAccessException e){
            password=null;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return password;
    }
    @Override
    public void alterPassword(String id, String newPassword) throws SQLException {
        String sql = "update admin set password = ? where adminID = ?";
        try{
            jdbcTemplate.update(sql,newPassword,id);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public byte getSex(String id) throws SQLException {
        Byte sex = null;
        try{
            String sql="select sex from admin where adminID=?";
            sex = jdbcTemplate.queryForObject(sql,Byte.class,id);
        }catch (IncorrectResultSizeDataAccessException e){
            sex = 0;
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return sex;
    }
    @Override
    public void alterSex(String id, byte newSex) throws SQLException {
        try{
            String sql="update admin set sex = ? where adminID = ?";
            jdbcTemplate.update(sql,newSex,id);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getPhone(String id) throws SQLException {
        String phone = null;
        try{
            String sql="select phoneNumber from admin where adminID = ?";
            phone = jdbcTemplate.queryForObject(sql,String.class,id);
        }catch (IncorrectResultSizeDataAccessException e){
            phone = "";
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return phone;
    }
    @Override
    public void alterPhone(String id, String newNumber) throws SQLException {
        try{
            String sql="update admin set phoneNumber = ? where adminID = ?";
            jdbcTemplate.update(sql,newNumber,id);
        }catch (DataAccessException e){
            throw new SQLException();
        }
    }
    @Override
    public String getEmail(String id) throws SQLException {
        String email = null;
        try{
            String sql="select email from admin where adminID = ?";
            email = jdbcTemplate.queryForObject(sql,String.class,id);
        }catch (IncorrectResultSizeDataAccessException e){
            email = "";
        }catch (DataAccessException e){
            throw new SQLException();
        }
        return email;
    }
    @Override
    public void alterEmail(String id, String newCount) throws SQLException {
        try {
            String sql = "update admin set email = ? where adminID = ?";
            jdbcTemplate.update(sql,newCount,id);
        }catch (DataAccessException e) {
            throw new SQLException();
        }
    }
}
