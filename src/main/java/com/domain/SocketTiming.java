package com.domain;

/**
 * 记录wifi插座定时信息
 **/
public class SocketTiming {
    public static final byte OPERATE_ON =1;
    public static final byte OPERATE_OFF =0;
    public static final byte STATUS_ON =1;
    public static final byte STATUS_OFF =0;
    public static final String NO_REPEATE ="0";
    private Long id;
    private String socketID;
    private String time;
    private Byte operate ;
    private String repeate;
    private Byte status;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSocketID() {
        return socketID;
    }

    public void setSocketID(String socketID) {
        this.socketID = socketID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Byte getOperate() {
        return operate;
    }

    public void setOperate(Byte operate) {
        this.operate = operate;
    }

    public String getRepeate() {
        return repeate;
    }

    public void setRepeate(String repeate) {
        this.repeate = repeate;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "SocketTiming{" +
                "id=" + id +
                ", socketID='" + socketID + '\'' +
                ", time='" + time + '\'' +
                ", operate=" + operate +
                ", repeate='" + repeate + '\'' +
                ", status=" + status +
                '}';
    }
}
