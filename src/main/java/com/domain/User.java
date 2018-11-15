package com.domain;
import java.util.ArrayList;
/**
 * 每个实例对应一个APP用户在数据库中的信息。其中openid为QQ登录时的统一认证码，用于标识一个QQ用户。
 * unionid为微信登录时的统一认证码，用于标识一个微信用户。
 */
public class User {
    private String userID;
    private byte isAlterdUserID;
    private String nickName;
    private String password;
    private String headImg;
    private byte sex;
    private String birth;
    private String phoneNumber;
    private String email;
    private String registerTime;
    private String address;
    private ArrayList<Socket> sockets;
    private String openid;
    private String unionid;
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getNickName() {
        if (nickName==null){
            nickName="";
        }
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getHeadImg() {
        return headImg;
    }
    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
    public byte getSex() {
        if (sex<0||sex>2){
            sex=0;
        }
        return sex;
    }
    public void setSex(byte sex) {
        this.sex = sex;
    }
    public String getBirth() {
        if (birth==null){
            birth="";
        }
        return birth;
    }
    public void setBirth(String birth) {
        this.birth = birth;
    }
    public String getPhoneNumber() {
        if (phoneNumber==null){
            phoneNumber="";
        }
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        if (email==null){
            email="";
        }
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRegisterTime() {
        return registerTime;
    }
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
    public String getAddress() {
        return address==null?"":address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public ArrayList<Socket> getSockets() {
        return sockets;
    }
    public void setSockets(ArrayList<Socket> sockets) {
        this.sockets = sockets;
    }
    public byte getIsAlterdUserID() {
        return isAlterdUserID;
    }
    public void setIsAlterdUserID(byte isAlterdUserID) {
        this.isAlterdUserID = isAlterdUserID;
    }
    public String getOpenid() {
        return openid;
    }
    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getUnionid() {
        return unionid;
    }
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", isAlterdUserID=" + isAlterdUserID +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", headImg='" + headImg + '\'' +
                ", sex=" + sex +
                ", birth='" + birth + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", registerTime='" + registerTime + '\'' +
                ", address='" + address + '\'' +
                ", sockets=" + sockets +
                ", openid='" + openid + '\'' +
                ", unionid='" + unionid +
                '}';
    }
}
