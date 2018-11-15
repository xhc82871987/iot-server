package com.utils;

import com.exception.IllegalIndexSocketID;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
/**
 * 用来生成设备ID
 */
public class CreateIDs {

    /**
     * 用于生成一系列的socketID
     * @param prevID 数据库中目前存储的socketID的值中按ACSII码排序最大的那个
     * @param number 要生成的个数
     * @param pre 生成设备ID时的前缀
     * @return 返回生成的socketID集合
     * @throws IllegalIndexSocketID 当传入的fromStr的值不是一个6位字符串时抛出该异常
     */
    public static List<String> createIDs(String prevID,String pre,int number){
        //若传入的前一个ID号不符合格式，抛出异常
        List<String> ids = new ArrayList<>();
        String id=prevID;
        for (int i=0;i<number;i++){
            String temp = nextID(id);
            while (true){
                if (checkID(temp)){
                    id=temp;
                    id=id.substring(1,id.length());
                    id=pre+id;
                    ids.add(id);
                    break;
                }else{
                    temp=nextID(temp);
                }
            }
        }
        return ids;
    }
    /**
     * 用于根据传入的ASCII数组来生成一个ID
     * @param prevId 上一个ID号码
     * @return 返回一个合法的socketID
     */
    private static String nextID(String prevId){
        if (prevId.equals("")||prevId ==null){
            return "000000";
        }
        byte[] prevBytes = prevId.getBytes();
        byte[] idBytes = new byte[prevBytes.length];
        String id = null;
        byte id0=prevBytes[0];
        byte id1=prevBytes[1];
        byte id2=prevBytes[2];
        byte id3=prevBytes[3];
        byte id4=prevBytes[4];
        byte id5=prevBytes[5];
        id5+=1;
        if (id5>122){
            id5=48;
            id4+=1;
            if (id4>122){
                id4=48;
                id3+=1;
                if (id3>122){
                    id3=48;
                    id2+=1;
                    if (id2>122){
                        id2=48;
                        id1+=1;
                        if (id1>122){
                            id1=48;
                            id0+=1;
                        }
                    }
                }
            }
        }
        idBytes[0]=id0;
        idBytes[1]=id1;
        idBytes[2]=id2;
        idBytes[3]=id3;
        idBytes[4]=id4;
        idBytes[5]=id5;
        id=new String(idBytes);
        return id;
    }
    /**
     * 检查生成的ID是否符合标准（由字母，数字，下划线组成）
     * @param id
     * @return 返回检查的结果
     */
    private static boolean checkID(String id){
        boolean flag = false;
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_]){6}$");
        if (pattern.matcher(id).matches()){
            flag = true;
        }
        return flag;
    }
}
