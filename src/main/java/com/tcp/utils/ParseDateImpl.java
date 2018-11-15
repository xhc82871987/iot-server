package com.tcp.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * ParseData实现类
 **/
public class ParseDateImpl implements ParseData {
    @Override
    public List<Byte> getData(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        List<Byte> dataList = new ArrayList<>();
        while (channel.read(buffer)>0){
            buffer.flip();
            while (buffer.position()<buffer.limit()){
                dataList.add(buffer.get());
            }
            buffer.clear();
        }
        return dataList;
    }
    @Override
    public List<List<Byte>> getPackages(List<Byte> data) {
        //每次调用此方法都实例化一个泛型类型为ArrayList<Byte>，该list用于存放收到的所有数据包
        List<List<Byte>>packages=new ArrayList<>();
        //遍历data，查询其中出现的数据包，当遇到0x11时，假设它是帧头，获取这个帧的数据长度，根据数据长度，获取帧尾
        //若根据计算到的长度获取的值是帧尾，则认为这段数据是一条数据包，将这条数据包存入packages中。从data中删除该条数据包对应的数据。
        //若得到的数据不是0x11，则删除这个数据
        while (data.size()>0){
            //判断数据是否为帧头
            if (0x11==data.get(0)){
                //计算数据帧长度
                int length=getDataLength(data);
                if(length>1&&data.size()>=length){
                    //判断是否是帧尾
                    if(0x16==data.get(length-1)){
                        //获取数据帧
                        List<Byte> pack0=data.subList(0,length);
                        ArrayList<Byte> pack= new ArrayList<>();
                        pack.addAll(pack0);
                        //添加数据帧到packages
                        packages.add(pack);
                        //删除receive中对应的数据
                        for (int i=0;i<length;i++){
                            data.remove(0);
                        }
                    }else{
                        data.remove(0);
                    }
                }else{
                    //receive中的数据长度不足一条数据包长度，停止遍历
                    break;
                }
            }else {
                data.remove(0);
            }
        }
        return packages;
    }
    @Override
    public boolean isComplete(List<Byte> data) {
        int length=data.size();
        byte c1=data.get(length-3);
        byte c2=data.get(length-2);
        short serverCheckNum=0;
        for (int i = 3; i <length-3 ; i++) {
            serverCheckNum+=data.get(i);
        }
        byte sc1= (byte) (serverCheckNum>>8);
        byte sc2= (byte) serverCheckNum;
        if (c1==sc1&&c2==sc2){
            return true;
        }
        return false;
    }
    @Override
    public int getDataType(List<Byte> data) {
        return data.get(4);
    }
    /**
     * 计算数据帧的长度，若传入的数据包的只有一个元素，则返回1，否则返回计算的结果
     * @param dataPackage 传入的数据包
     * @return 返回计算的数据包的长度
     */
    public int getDataLength(List<Byte> dataPackage) {
        int length=1;
        if(dataPackage.size()>3){
            byte l1=dataPackage.get(1);
            byte l2=dataPackage.get(2);
            length=(l1<<8)+(l2&0xff);
        }
        return length;
    }
}
