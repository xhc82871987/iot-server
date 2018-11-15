package com.web.handle.service.email;

import javax.mail.MessagingException;
/**
 * 为发送邮件提供接口
 */
public interface EmailService {
    /**
     * 用来发送简单的邮件信息，如验证码，链接等
     * @param count 接收方账号
     * @param subject 邮件主题，例如验证码
     * @param msg 邮件内容
     * @throws MessagingException 当发送失败时抛出MessagingException异常
     */
    void sendSimpleEmail(String count, String subject, String msg) throws MessagingException;

}
