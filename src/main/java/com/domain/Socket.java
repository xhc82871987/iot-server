package com.domain;

import java.util.List;
/**
 * 每个实例对应一个智能插座在数据库中的信息，用来记录设备的id，名称，是否在线，邀请码，开关状态等信息。
 */
public class Socket {
    private String socketID;
    private String name;
    private byte connect;
    private String inviteID;
    private byte status;
    private List<String> users;
    private String bindTime;

    public String getSocketID() {
        return socketID;
    }
    public void setSocketID(String socketID) {
        this.socketID = socketID;
    }
    public byte getStatus() {
        if (status>1||status<0){
            status=0;
        }
        return status;
    }
    public void setStatus(byte status) {
        this.status = status;
    }
    public String getBindTime() {
        if (bindTime==null||bindTime.equals("")){
            bindTime="2018-01-01 00:00:00";
        }
        return bindTime;
    }
    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }
    public String getName() {
        if (name == null){
            name="";
        }
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public byte getConnect() {
        if (connect<0||connect>1){
            connect = 0;
        }
        return connect;
    }
    public void setConnect(byte connect) {
        this.connect = connect;
    }
    public String getInviteID() {
        if (inviteID==null){
            inviteID="";
        }
        return inviteID;
    }
    public void setInviteID(String inviteID) {
        this.inviteID = inviteID;
    }
    public void setUsers(List<String> users) {
        this.users = users;
    }
    public List<String> getUsers(){
        return users;
    }
    @Override
    public String toString() {
        return "Socket{" +
                "socketID='" + socketID + '\'' +
                ", name='" + name + '\'' +
                ", connect=" + connect +
                ", inviteID='" + inviteID + '\'' +
                ", status=" + status +
                ", users=" + users +
                ", bindTime='" + bindTime + '\'' +
                '}';
    }
}
