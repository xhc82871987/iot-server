package com.domain;
/**
 * 管理员实例对象，用来记录管理员的账号信息，包括ID，姓名，密码，性别，生日，手机号，email信息。
 */
public class Admin {
    private String adminID;
    private String name;
    private String password;
    private byte sex;
    private String birth;
    private String phoneNumber;
    private String email;

    public String getPassword() {
        return password == null?"":password;
    }
    public void setPassword(String password) {
        this.password = password;
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
        return birth==null||birth.equals("")?"2018-01-01":birth;
    }
    public void setBirth(String birth) {
        this.birth = birth;
    }
    public String getPhoneNumber() {
        return phoneNumber==null?"":phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email==null?"":email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAdminID() {
        return adminID;
    }
    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }
    public String getName() {
        return name==null?"":name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminID='" + adminID + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", birth='" + birth + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
