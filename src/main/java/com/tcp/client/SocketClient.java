package com.tcp.client;

import com.dao.SocketDAO;
import com.dao.SocketTimingDAO;
import com.domain.SocketTiming;
import com.tcp.utils.ParseData;
import org.springframework.cglib.core.Local;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 智能插座对应的Client对象，该类的对象表示一个智能插座，用来处理智能插座向数据库发送的数据。
 **/
public class SocketClient extends AbstractClient {
    /**
     * 发送通电数据命令的数据包
     */
    public static final byte[] POWER_ON={17,0,11,3,0,0,1,1,0,5,22};
    /**
     * 发送断电命令的数据包
     */
    public static final  byte[] POWER_OFF={17,0,11,3,0,0,1,0,0,4,22};
    /*
     * 客户端的状态，通电状态/断电状态
     */
    private volatile boolean powerStatus;
    /*
     * 缓存接收到的数据
     */
    private List<Byte> dataBuffer;
    /*
     * 从数据库中获取设备信息的对象
     */
    private SocketDAO socketDAO;
    /*
     * 获取定时信息的数据库操作类
     */
    private SocketTimingDAO socketTimingDAO;
    /*
     * 设备的定时信息
     */
    private List<SocketTiming> timings;
    //-----------------------------------------------------------------------------
    //                                 构造方法
    //-----------------------------------------------------------------------------
    /**
     * 初始化一个SocketClient对象
     */
    public SocketClient() {
        super();
        dataBuffer = new ArrayList<>();
        timings = new ArrayList<>();
    }
    //-----------------------------------------------------------------------------
    //                                 继承方法
    //-----------------------------------------------------------------------------
    /**
     * 当实例初始化时调用该方法，该方法用于更新数据库中设备的在线状态，以及向设备发送上一次在线时的状态。
     * @throws IOException 当网络连接断开时抛出IOException异常
     */
    @Override
    public void clientInit(){
        try {
            socketDAO.setConnect(getId(), (byte) 1);
            updateTimings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setPowerStatus();
        if (powerStatus){
            sendData(SocketClient.POWER_ON);
        }else{
            sendData(SocketClient.POWER_OFF);
        }
        System.out.println(new Date()+ " --> "+getId()+"  init success！");
    }
    @Override
    public void setConnect(boolean status){
        this.connect = status;
        try {
            socketDAO.setConnect(getId(), (byte) (status==true?1:0));
        } catch (SQLException e) {
            System.out.println(new Date()+" "+e.getMessage());
        }
    }
    @Override
    public void doRequest(ParseData parseData) throws IOException {
        List<Byte> receiveData = parseData.getData(socketChannel);
        if (receiveData.size()>0){
            dataBuffer.addAll(receiveData);
        }
        if (dataBuffer.size()>0){
            List<List<Byte>> packages = parseData.getPackages(dataBuffer);
            Iterator<List<Byte>> iterator = packages.iterator();
            while (iterator.hasNext()){
                List<Byte> pack = iterator.next();
                iterator.remove();
                if (parseData.isComplete(pack)){
                    int type = parseData.getDataType(pack);
                    if (type == ParseData.DATA_PACKAGE){
                        updateLastReceiveTime();
                        byte status = pack.get(8);
                        if (status == 0){
                           this.powerStatus=false;
                        }else if (status == 1){
                            this.powerStatus=true;
                        }
                    }else if (type == ParseData.HEART_PACKAGE){
                        updateLastReceiveTime();
                    }
                }
            }
        }
    }
    //-----------------------------------------------------------------------------
    //                                 get/set方法
    //-----------------------------------------------------------------------------
    /**
     * 判断是否处于插座是否处于通电状态
     * @return true 通电状态，false 断电状态
     */
    public boolean isPowerOn() {
        return powerStatus;
    }
    /**
     * 从数据库中获取插座的通电状态并设置插座的通电和断电状态
     */
    private void setPowerStatus() {
        byte power = 0;
        try {
            power=socketDAO.getStatus(getId());
        } catch (SQLException e) {
            power = 0;
        }
        powerStatus = power==0?false:true;
    }
    /**
     * 设置开关的通电和断电状态
     * @param powerStatus 要设置的状态
     */
    public synchronized void setPowerStatus(boolean powerStatus) throws SQLException{
        System.out.println(new Date()+" -->"+getId()+" setPowerStatus-status:"+powerStatus);
        this.powerStatus=powerStatus;
        socketDAO.alterStatus(getId(), (byte) (this.powerStatus==true?1:0));
        System.out.println(new Date()+ " "+getId()+" 状态在数据库中更新成功");
        if (powerStatus){
            System.out.println(new Date()+" 发送通电命令"+getId());
            sendData(SocketClient.POWER_ON);
        }else{
            System.out.println(new Date()+" 发送断电命令"+getId());
            sendData(SocketClient.POWER_OFF);
        }
    }
    /**
     * 获取该对象的数据缓冲区
     * @return 返回一个List，集合中的元素为当前未处理的数据
     */
    public List<Byte> getDataBuffer() {
        return dataBuffer;
    }
    /**
     * 设置该对象用来和数据库交互的DAO类
     * @param socketDAO
     */
    public void setSocketDAO(SocketDAO socketDAO) {
        this.socketDAO = socketDAO;
    }
    /**
     * 给SocketClient实例设置操作数据中定时信息的数据库操作类
     * @param socketTimingDAO
     */
    public void setSocketTimingDAO(SocketTimingDAO socketTimingDAO) {
        this.socketTimingDAO = socketTimingDAO;
    }
    //-----------------------------------------------------------------------------
    //                                 定时相关方法
    //-----------------------------------------------------------------------------
    /**
     * 每天更新一次当天应该监控的定时信息
     * @throws SQLException 当出现数据库查询异常时抛出该异常
     */
    public synchronized void updateTimings() throws SQLException {
        //从数据库中获取该设备所有的定时信息
        this.timings = socketTimingDAO.getTiming(getId());
        System.out.println(new Date()+"-->从数据库中获取所有定时信息：updateTimings");
    }
    /**
     * 获取设备的定时信息
     * @return 返回该设备的所有定时信息
     */
    public List<SocketTiming> getTimings(){
        return this.timings;
    }
    /**
     * 更新定时信息,查找与传入的定时信息具有相同时间的定时，若找到，删除它，最后把新的定时信息添加到定时列表中
     * @param timing
     */
    public synchronized void updateTiming(SocketTiming timing){
        System.out.println(new Date()+"--> 更新定时："+timing);
        List<SocketTiming> timingList  = new ArrayList<>();
        timingList.addAll(this.timings);
        Iterator<SocketTiming> iterator = timingList.iterator();
        while (iterator.hasNext()){
            SocketTiming timing1 = iterator.next();
            iterator.remove();
            String time1= timing1.getTime().substring(0,5);
            String time2 = timing.getTime().substring(0,5);
            if (time1.equals(time2)){
                this.timings.remove(timing1);
                System.out.println(new Date()+"--> 找到该定时并删除:"+timing);
            }
        }
        timings.add(timing);
        System.out.println(new Date()+"--> 更新定时完成:"+timing);
    }
    /**
     * 添加一个定时信息
     * @param timing
     */
    public synchronized void addTiming(SocketTiming timing){
        this.timings.add(timing);
        System.out.println(new Date()+"--> addTiming"+timing);
    }
    /**
     * 删除一个定时信息
     * @param timing 要删除的定时信息
     */
    public synchronized void deleteTiming(SocketTiming timing){
        System.out.println(new Date()+"--> deleteTiming"+timing);
        Iterator<SocketTiming> iterator = this.timings.iterator();
        while (iterator.hasNext()){
            SocketTiming timing1 = iterator.next();
            String time1 = timing1.getTime().substring(0,5);
            String time2 = timing.getTime().substring(0,5);
            if (time1.equals(time2)){
                iterator.remove();
                System.out.println(new Date()+"--> deleteTiming 完成"+timing1);
            }
        }
    }
    /**
     * 删除所有的定时信息
     */
    public void deleteAllTiming(){
        this.timings.clear();
    }
    /**
     * 改变定时的状态是开启中还是已经关闭了
     * @param timing
     * @param status
     * @throws SQLException
     */
    public synchronized void updateTimingStatus(SocketTiming timing, Byte status) throws SQLException {
        socketTimingDAO.alterStatus(getId(),timing.getTime(),status);
    }
}
