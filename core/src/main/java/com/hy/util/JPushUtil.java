package com.hy.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class JPushUtil {
	
	public static final String APP_KEY="a0e812e6a3258cee1fa0e62d";
	
	public static final String MASTER_SECRET="6ac3b5943624e0973f12a231";
	
	/**
	 * 推送消息
	 * @param payload
	 */
	public static void push(String alias,String title,String content){
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());

	    try {
	        PushResult result = jpushClient.sendPush(buildPushObject_all_alias_alert(title, content, alias));
	        System.out.println("result: "+result);
	    } catch (APIConnectionException e) {
	        // Connection error, should retry later
	    	System.out.println("Connection error, should retry later: "+e);

	    } catch (APIRequestException e) {
	        // Should review the error, and fix the request
	    	System.out.println("Should review the error, and fix the request "+e);
	    	System.out.println("HTTP Status: " + e.getStatus());
	    	System.out.println("Error Code: " + e.getErrorCode());
	    	System.out.println("Error Message: " + e.getErrorMessage());
	    }
	}
	
	/**
	 * 所有人通知
	 * @param content
	 * @return
	 */
	public static PushPayload buildPushObject_all_all_alert(String content) {
        return PushPayload.alertAll(content);
    }
	
	/**
	 * 别名推送
	 * @param content
	 * @param alias
	 * @return
	 */
	public static PushPayload buildPushObject_all_alias_alert(String title,String content,String alias) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1).build())
                        .build())
                .build();
    }
	
	public static void main(String[] args) {
		JPushUtil.push("7b92cbc20c72423c9fd015406ae55209","测试标题","测试内容");
	}
}
