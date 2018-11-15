package com.tcp.support;

import com.dao.SocketTimingDAO;
import com.domain.SocketTiming;
import com.tcp.client.Client;
import com.tcp.client.SocketClient;
import com.tcp.collection.ClientCollection;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 监测智能插座的定时
 **/
public class SocketTimingThread {
    private ClientCollection collection;
    private SocketTimingDAO socketTimingDAO;
    /**
     * 便利Client结合，选择出SocketClient对象，并判断是否设置了定时，
     * 若有定时则检测定时信息并做出处理
     * */
    public void init(){
        new Thread(()->{
            while (true){
                List<Client> clients=collection.getAllClient();
                Iterator<Client> iterator = clients.iterator();
                while (iterator.hasNext()){
                    Client client = iterator.next();
                    //查找WiFiSocketClient
                    if (client instanceof SocketClient){
                        //若是wifi插座则提交给一个新的线程去处理
                        SocketClient socketClient = (SocketClient) client;
                        dealTiming(socketClient);
                    }
                }
            }
        },"SocketTimingThread").start();
    }
    /**
     * 设置要遍历的Client集合
     * @param collection
     */
    public void setCollection(ClientCollection collection) {
        this.collection = collection;
    }
    /**
     * 为该对象设置一个处理数据库中定时信息的数据库操作类
     * @param socketTimingDAO 操作数据库中定时信息的数据库操作类实例
     */
    public void setSocketTimingDAO(SocketTimingDAO socketTimingDAO) {
        this.socketTimingDAO = socketTimingDAO;
    }
    /**
     * 判断当前时间是否处于定时范围内
     * @return 返回比较结果，如果当前时间处于定时信息的开始时间和结束时间范围内，返回true
     * 否则返回false
     */
    private boolean nowTimeIsTiming(SocketTiming timing) {
        if (timing != null) {
            LocalTime now =LocalTime.now();
            int nowHour = now.getHour();
            int nowMin = now.getMinute();
            LocalTime time = LocalTime.parse(timing.getTime());
            if (nowHour == time.getHour() && nowMin == time.getMinute()){
                return true;
            }
        }
        return false;
    }
    /**
     * 处理具有定时信息的设备的开关状态
     * @param socketClient
     */
    private void dealTiming(SocketClient socketClient){
        List<SocketTiming> timings = socketClient.getTimings();
        //复制一份，遍历复制的list，由于定时的添加和删除具有不定时性，防止发生异常。
        List<SocketTiming> timingsCopy =new ArrayList<>();
        timingsCopy.addAll(timings);
        Iterator<SocketTiming> timingIterator = timingsCopy.iterator();
        while (timingIterator.hasNext()){
            SocketTiming timing = timingIterator.next();
            //判断定时是否开启
            if (timing!=null){
                if (timing.getStatus() == SocketTiming.STATUS_ON){
                    String repeate = timing.getRepeate();
                    LocalDate now  = LocalDate.now();
                    String weekDay = now.getDayOfWeek().getValue()+"";
                    //判断该定时是否是当天的
                    if (repeate.equals(SocketTiming.NO_REPEATE)||repeate.contains(weekDay)){
                        //判断当前时刻是否是定时的时间
                        if (nowTimeIsTiming(timing)){
                            System.out.println(socketClient.getId()+" --> 定时时间："+timing);
                            try{
                                //判断该定时的操作项，是给设备通电还是给设备断电
                                if (timing.getOperate() == SocketTiming.OPERATE_ON){
                                    System.out.println("向"+socketClient.getId()+"发送通电命令");
                                    socketClient.setPowerStatus(true);
                                    System.out.println("向"+socketClient.getId()+"发送通电命令完成");
                                }else{
                                    System.out.println("向"+socketClient.getId()+"发送断电命令");
                                    socketClient.setPowerStatus(false);
                                    System.out.println("向"+socketClient.getId()+"发送断电命令完成");
                                }
                            }catch (SQLException e) {
                                System.out.println("SocketTimingThread 修改设备状态失败！");
                                return;
                            }
                            try{
                                //若定时是单次的，则操作完成时改变定时的状态，改为关闭状态
                                if (repeate.equals(SocketTiming.NO_REPEATE)){
                                    socketClient.updateTimingStatus(timing, SocketTiming.STATUS_OFF);
                                }
                                socketClient.deleteTiming(timing);
                            } catch (SQLException e) {
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }

}
