package com.hy.util;

import com.hy.core.Constants;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

//邮件发送类
@Component
public class SendMail{
	private static Logger logger= LogManager.getLogger(SendMail.class);
    //Mail发送人的地址
    private String mailFrom ;
    //SMTP主机地址
    private String smtpHost;
    //是否采用调试方式
    private boolean debug = false;
    //附件基本路径
    private String messageBasePath = null;

    //附件文件的容器
    private Vector attachedFileList;
    //邮件总数
    private String mailAccount = null;

//    private String mailPass = null;
    //邮件发送的信息格式类型
    private String messageContentMimeType ="text/html; charset=gb2312";
    //BCCMail地址（暗送）
    private String mailbccTo = null;
    //CCMail地址（抄送）
    private String mailccTo = null;
    private String mailUser;
    //邮箱密码
    private String mailPassword ;


    //属性封装
    //[封装开始]
    //收件人邮件地址
//    public void setMailTo(String to){mailTo=to;}
    //Mail发送人的地址
    public void setMailFrom(String from){mailFrom=from;}
    //SMTP主机地址
    public void setSMTPHost(String host){smtpHost=host;}
    //是否采用调试方式
    public void setDebug(boolean debugFlag){debug=debugFlag;}
    //附件基本路径
    public void setMessageBasePath(String basePath){messageBasePath=basePath;}
//    //Mail主题
//    public void setSubject(String sub){subject=sub;}
//    //Mail内容
//    public void setMsgContent(String content){msgContent=content;}
    //附件文件的容器
    public void setAttachedFileList(Vector filelist){attachedFileList = filelist;}
    //邮件总数
    public void setMailAccount(String strAccount){mailAccount = strAccount;}

    //邮件发送的信息格式类型
    public void setMessageContentMimeType(String mimeType){messageContentMimeType = mimeType;}
    //BCCMail地址（暗送）
    public void setMailbccTo(String bccto){mailbccTo = bccto;}
    //CCMail地址（抄送）
    public void setMailccTo(String ccto){mailccTo = ccto;}

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public String getMailPassword() {
        return mailPassword;
    }
    //邮箱密码
    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }
    //[封装完毕]

	/**
	 *	[ 函数说明    ]  :
	 *	[## private String fillMail() {} ] :
	 *			参数   : Session session 邮件会话
	 *                   MimeMessage msg 邮件消息单元
	 *			返回值 : String          返回邮件信息填充是否成功的信息
	 *  		修饰符 : private
	 *			功能   : 完成邮件信息填充的功能
	 */
    private String fillMail(MimeMessage msg, String mailTo, String mailFrom, String subject, String msgContent)
            throws IOException, MessagingException {
    	String caseList = "";
			//附件文件名
	        String fileName = null;
	        //创建一个邮件单元
	        Multipart mPart = new MimeMultipart();

	        if(mailFrom != null){
	        	//设置Mail发送人的地址
	            msg.setFrom(new InternetAddress(mailFrom));
	        }
	        else {
	            return "没有指定发送人邮件地址！";
	        }

	        if (mailTo != null) {
	        	//设置收件人邮件地址
	            InternetAddress[] address = InternetAddress.parse(mailTo);
	            msg.setRecipients(Message.RecipientType.TO, address);
	        }
	        else {
	        	logger.info("没有指定收件人邮件地址！");
	        }

	        if (mailccTo != null) {
	            //设置CCMail地址（抄送）
	            InternetAddress[] ccaddress = InternetAddress.parse(mailccTo);
	            msg.setRecipients(Message.RecipientType.CC, ccaddress);
	        }

	        if (mailbccTo != null) {
	            //设置BCCMail地址（暗送）
	            InternetAddress[] bccaddress = InternetAddress.parse(mailbccTo);
	            msg.setRecipients(Message.RecipientType.BCC, bccaddress);
	        }

	        //设置邮件主题
	        msg.setSubject(subject);

	        //设置邮件回复地址
	        InternetAddress[] replyAddress = { new InternetAddress(mailFrom)};
	        msg.setReplyTo(replyAddress);

	        //创建消息体并填充邮件内容
	        MimeBodyPart mBodyContent = new MimeBodyPart();
	        //填充邮件内容和设置邮件发送的信息格式类型
	        if (msgContent != null){
	            mBodyContent.setContent(msgContent, messageContentMimeType);
	        }
	        else{
	            mBodyContent.setContent("", messageContentMimeType);
	        }
	        //将邮件内容和设置邮件发送的信息格式类型加入邮件单元
	        mPart.addBodyPart(mBodyContent);

	        //设置邮件的附件信息
	        if (attachedFileList != null){
	            for (Enumeration fileList = attachedFileList.elements(); fileList.hasMoreElements();){
	            	//获得附件文件名
	                fileName = (String) fileList.nextElement();
	                //创建消息体
	                MimeBodyPart mBodyPart = new MimeBodyPart();

	                //获取文件资源
	                FileDataSource fds = new FileDataSource(messageBasePath + fileName);
	                //将附件文件句柄加入消息体
	                mBodyPart.setDataHandler(new DataHandler(fds));
	                //在消息体中设置文件名
	                mBodyPart.setFileName(fileName);
	                //将消息体加入邮件单元
	                mPart.addBodyPart(mBodyPart);
	            }
	        }

	        //将消息单元加入邮件信息中
	        msg.setContent(mPart);

	        //设置邮件信息生成时间
	        msg.setSentDate(new java.util.Date());
	        return "填充邮件发送信息成功！";
    }


	/**
	 *	[ 函数说明    ]  :
	 *	[## public String sendMail()  {} ] :
	 *			参数   : String HUAWEI_MAIL_USER      发送服务器地址    如：adtm2006@163.com
	 *                   String HUAWEI_MAIL_PASSWORD  发送服务器密码    如：000000
	 *                   String smtpHost              SMTP主机地址      如：smtp.163.com
	 *                   String mailFrom              Mail发送人的地址  如：adtm2006@163.com
	 *                   String mailTo                收件人邮件地址    如：xxxxx@tom.com
	 *                   String subject               Mail主题          如：标题:zzzzz
	 *                   String msgContent            Mail内容          如：内容:aaaaa
	 *                   boolean isCheck              是否需要验证
	 *			返回值 : String  返回邮件发送是否成功的信息
	 *  		修饰符 : public
	 *			功能   : 带参数的邮件发送方法
	 */
    public String sendMail(final String mailUser,final String mailPassword,String smtpHost,String mailFrom,String mailTo,String subject,String msgContent,boolean isCheck)
            throws IOException, MessagingException, GeneralSecurityException {
        //TODO:初始化到
/*        this.smtpHost   = smtpHost;   //SMTP主机地址
        this.mailFrom   = mailFrom;   //Mail发送人的地址
        this.mailTo     = mailTo;     //收件人邮件地址
        this.subject    = subject;    //Mail主题
        this.msgContent = msgContent; //Mail内容*/
        Properties props = System.getProperties();
        if(smtpHost==null) smtpHost=this.smtpHost;
        props.put("mail.smtp.host", smtpHost); //设置SMTP主机地址
//        props.put("mail.smtp.port", 465);
        props.put("mail.smtp.auth", "true");   //要求身份验证
        props.put("mail.smtp.ssl.enable", "true");   //开启SSL链接
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.socketFactory", sf);
        //邮件身份验证对象
        Authenticator auth = null;
        if(isCheck){
        	auth = new Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailUser,mailPassword);
                }
            };
        }

        //创建邮件会话
        //session会自动调用Authenticator类或继承类的
        //getPasswordAuthentication方法
        Session session = null;
        if(isCheck){
        	session = Session.getInstance(props, auth);
        }
        else{
        	session = Session.getInstance(props);
        }

        //设置debug状态
        //如果为true,则在发送邮件过程中监控mail命令的话
        session.setDebug(debug);

        //构建邮件信息体
        MimeMessage msg = new MimeMessage(session);
        //Transport类实现了发送信息的协议（通称为SMTP）
        //此类是一个抽象类，可以使用这个类的静态方法send()来发送消息：
        Transport trans = null;

        try {
            ////填充邮件发送信息
            String re  = fillMail(msg,mailTo,mailFrom,subject,msgContent);
            if(re.equals("填充邮件发送信息成功！")){
            }
            else{
            	return re;
            }

            //获得邮件发送端口
            trans = session.getTransport("smtp");

            try {
                //建立与邮件服务器的连接
                trans.connect(smtpHost, mailUser,mailPassword);
            }
            catch (AuthenticationFailedException e) {
                e.printStackTrace();
                //Log.writeLog("error","邮件服务器验证时产生错误: " + e.toString());
                return "没有通过邮件服务器验证：";
            }
            catch (MessagingException e) {
            	e.printStackTrace();
            	//Log.writeLog("error","连接邮件服务器时产生错误: " + e.toString());
                return "连接邮件服务器错误：";
            }

            //发送邮件
            Transport.send(msg);

            //关闭和邮件服务器的连接
            trans.close();
        }
        catch (MessagingException mex) {
            mex.printStackTrace();
            //Log.writeLog("error","发送邮件时产生错误: " + mex.toString());
            Exception ex = null;

            if ((ex = mex.getNextException()) != null) {
                ex.printStackTrace();
            }

            return "发送邮件失败！";
        }
        finally {
            try {
                if (trans != null && trans.isConnected())
                    trans.close();
            }
            catch (Exception e) {
                //logger.info(e.toString());
            }
        }

        return "发送邮件成功！";
    }

    public String sendEmail(String emailAdress,String title,String content) {
        String str = "";
        String content_all = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>"+title+"</title></head><body><div style=\"padding-left:15px;font-family:'Microsoft Yahei', Verdana, Simsun, sans-serif; font-size:18px;\">"
                + content
                + "</div></body></html>";
        try {
//            str = sendMail("service@surong100.com", "srmaiL100!&!$", "smtp.exmail.qq.com", "service@surong100.com", emailAdress, title, content_all, true);
            str = sendMail(Constants.getSystemStringValue("mailUser"),Constants.getSystemStringValue("mailPassword"), Constants.getSystemStringValue("smtpHost"), Constants.getSystemStringValue("mailFrom"), emailAdress, title, content_all, true);
            // }
            if (!str.equals("发送邮件成功！")) {
                logger.error("邮件发送错误：" + str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
        return str;
    }

    public String sendEmail(String emailAdress,String title,String content,String mailUser,String mailPassword) {
        String str = "";
        String content_all = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>"+title+"</title></head><body><div style=\"padding-left:15px;font-family:'Microsoft Yahei', Verdana, Simsun, sans-serif; font-size:18px;\">"
                + content
                + "</div></body></html>";
        try {
            str = sendMail(mailUser, mailPassword, "smtp.qq.com", "85164925@qq.com", emailAdress, title, content_all, true);
            // }
            if (!str.equals("发送邮件成功！")) {
                logger.error("邮件发送错误：" + str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
        return str;
    }

}
