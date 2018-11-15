package com.web.handle.service.email;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * 实现了EmailService接口，用户需要在web根目录下的/WEB-INF/conf/mai.properties配置文件中
 * 配置邮箱服务器的相关信息
 */
public class EmailServiceImpl implements EmailService {
    /*
     * 配置邮箱服务器参数
     */
    private Properties properties;
    /**
     * 用于实例化发送邮件的对象
     */
    public EmailServiceImpl(){}
    /**
     * 用于读取/WEB-INF/conf/mai.properties配置文件信息
     * @param properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    @Override
    public void sendSimpleEmail(String count, String subject, String msg) throws MessagingException {
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(properties.getProperty("mail.userName"),properties.getProperty("mail.password"));
            }
        };
        //创建Session对象
        Session session = Session.getInstance(properties,auth);
        session.setDebug(true);
        //创建message
        Message message = new MimeMessage(session);
        //设置发送者
        message.setFrom(new InternetAddress("82871987@qq.com"));
        //设置发送方式与收件者
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(count));
        //设置主题
        message.setSubject(subject);
        //设置内容
        message.setContent(msg,"text/html;charset=utf-8");
        //创建Transport用于将邮件发送
        Transport.send(message);
    }
}
