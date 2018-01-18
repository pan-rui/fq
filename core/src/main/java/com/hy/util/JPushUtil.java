package com.hy.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.google.gson.JsonObject;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JPushUtil {
	private static final Logger logger = LogManager.getLogger(JPushUtil.class);
	public static final String USER_APP = "U_";
	public static final String SALE_APP = "S_";
	public static final String APP_KEY="6ed38b1c85a58a2ca6d1d04d";
	public static final String SALE_APP_KEY="752a1c126636e9a39bbaff5c";
	public static final String SALE_MASTER_SECRET="f49855441042b976219688f6";
	public static final String MASTER_SECRET="7389c3053a29057a869669de";
	private static final ThreadPoolExecutor scheduPool = new ThreadPoolExecutor(3,15,30, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(15),new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		}
	});


		/**
	 * 推送消息	//payload
	 */
	public static void pushByAlias(String appType,String msgType,String title,String content,JsonObject extra,String... alias){
	    try {
	        PushResult result = getPushClient(appType).sendPush(buildPushObject_all_alias_alert(ParamsMap.newMap(Table.USER_ID,appType.split(Table.FIELD_INTERVAL)[1]).addParams(Table.TYPE,msgType),title, content, extra,null,null, Arrays.asList(alias)));
	        System.out.println("result: "+result);
	    } catch (APIConnectionException|APIRequestException e) {
	        // Connection error, should retry later
			e.printStackTrace();
	    	logger.error("Connection error, should retry later: ",e);
	    }
	}

	public static void pushByRegId(String appType,String msgType,String title,String content,JsonObject extra,String regId){
		try {
			PushResult result = getPushClient(appType).sendPush(buildPushObject_all_alias_alert(ParamsMap.newMap(Table.USER_ID,appType.split(Table.FIELD_INTERVAL)[1]).addParams(Table.TYPE,msgType),title, content, extra,regId,null, null));
			System.out.println("result: "+result);
		} catch (APIConnectionException|APIRequestException e) {
			e.printStackTrace();
			logger.error("Connection error, should retry later: ",e);
		}
	}

	public static void pushByTags(String appType,String msgType,String title,String content,JsonObject extra,String... tags){
		try {
			PushResult result = getPushClient(appType).sendPush(buildPushObject_all_alias_alert(ParamsMap.newMap(Table.USER_ID,appType.split(Table.FIELD_INTERVAL)[1]).addParams(Table.TYPE,msgType),title, content, extra,null,Arrays.asList(tags), null));
			System.out.println("result: "+result);
		} catch (APIConnectionException|APIRequestException e) {
			e.printStackTrace();
			logger.error("Connection error, should retry later: ",e);
		}
	}

	public static void multiPush(String appType,String msgType,String title,String content,JsonObject extra,List<String> alias,String... tags){
		try {
			PushResult result = getPushClient(appType).sendPush(buildPushObject_all_alias_alert(ParamsMap.newMap(Table.USER_ID,appType.split(Table.FIELD_INTERVAL)[1]).addParams(Table.TYPE,msgType),title, content, extra,null,Arrays.asList(tags), alias));
			System.out.println("result: "+result);
		} catch (APIConnectionException|APIRequestException e) {
			e.printStackTrace();
			logger.error("Connection error, should retry later: ",e);
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
	public static PushPayload buildPushObject_all_alias_alert(ParamsMap<String,Object> recordMap, String title, String content, JsonObject extra, String registerId, List<String> tag, List<String> alias) {
		Audience.Builder builder = Audience.newBuilder();
		if(!StringUtils.isEmpty(registerId))
			builder.addAudienceTarget(AudienceTarget.registrationId(registerId));
		if(!CollectionUtils.isEmpty(tag)) {
			builder.addAudienceTarget(AudienceTarget.tag(tag));
			recordMap.addParams(Table.UserMessage.TAGS.name(), tag);
		}
		if(!CollectionUtils.isEmpty(alias))
			builder.addAudienceTarget(AudienceTarget.alias(alias));
			Constants.baseDao.insertByProsInTab(Table.FQ + Table.USER_MESSAGE, recordMap.addParams(Table.UserMessage.TITLE.name(),title).addParams(Table.UserMessage.CONTENT.name(),content).addParams(Table.UserMessage.MSG_EXT.name(), extra.toString()));
		return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(builder.build())
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title).addExtra("data",extra).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1).addExtra("data",extra).build())
                        .build())
                .build();
	}

	public static JPushClient getPushClient(String appType) {
		if (appType.startsWith(USER_APP)) {
			return new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());
		} else if (appType.startsWith(SALE_APP)) {
			return new JPushClient(SALE_MASTER_SECRET, SALE_APP_KEY, null, ClientConfig.getInstance());
		}
		return new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());
	}
	
	public static void main(String[] args) {
		JsonObject jsonObject=new JsonObject();
		jsonObject.addProperty("aa", "gfweter");
		JPushUtil.pushByRegId(JPushUtil.USER_APP+"1165","NOTIFY","测试标题","测试内容",jsonObject,"190e35f7e04166a2161");
	}

	public static void submitTask(Runnable runnable) {
		scheduPool.submit(runnable);
	}
}
