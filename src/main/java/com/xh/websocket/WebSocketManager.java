package com.xh.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.websocket.Session;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xh.scheduler.manager.QuartzManager;
import com.xh.util.ConfigPropertyUtil;
import com.xh.util.JsonUtil;
import com.xh.util.Utils;
import com.xh.websocket.pong.PongJob;

public class WebSocketManager {

	public static String GLOBAL_SERVICE_ID ="global-service-id";
	
	private static Map<String, Object> sessions = Maps.newConcurrentMap();
	private static Map<String, Set<String>> events = Maps.newConcurrentMap();

	/**
	 * 注册客户端
	 * @param user
	 * @param session
	 * @throws IOException
	 */
	public static void registerClient(String user, Object session) throws IOException {
		if(isRegisterClient(user)) {
			sessions.remove(user);
		}
		sessions.put(user, session);
		subscribeServer(user, GLOBAL_SERVICE_ID);
		sendMessage(user, "register-connect", true, user+" 连接成功");
	}
	/**
	 * 注销客户端
	 * @param user
	 * @throws IOException
	 */
	public static void unregisterClient(String user) throws IOException {
		unSubscribePong(user);
		unSubscribeServer(user, null);
		sessions.remove(user);
	}
	/**
	 * 判断是否已经注册
	 * @param user
	 * @return
	 */
	public static Boolean isRegisterClient(String user) {
		return sessions.containsKey(user) && sessions.get(user) != null;
	}

	/**
	 * 订阅 服务
	 * @param user
	 * @param eventId 服务ID
	 * @throws IOException
	 */
	public static void subscribeServer(String user, String eventId) throws IOException {

		Set<String> enentids = events.get(user);
		if (enentids == null) {
			enentids = Sets.newConcurrentHashSet();
		}
		enentids.add(eventId);
		events.put(user, enentids);
		sendMessage(user, "subscribe-"+eventId, true,  "subscribe ok");
	}
	/**
	 * 取消服务订阅
	 * @param user
	 * @param eventId 为空时,取消用户所有的订阅
	 * @throws IOException
	 */
	public static void unSubscribeServer(String user, String eventId) throws IOException {
		Set<String> enentids = events.get(user);
		if (enentids == null) {
			sendMessage(user, "unSubscribe-"+eventId, false, "unSubscribe fail");
			return;
		}
		if (Utils.isEmpty(eventId)) {
			enentids.clear();
			events.remove(user);
		} else {
			enentids.remove(eventId);
			events.put(user, enentids);
		}
		sendMessage(user, "unSubscribe-"+eventId, true, "unSubscribe ok");
	}

	/**
	 * 推送服务,将数据推送给订阅了服务的客户端
	 * @param user ,为空时,推送给订阅了该服务的所有用户
	 * @param eventId
	 * @param data
	 * @throws Exception
	 */
	public static void dispatchServer(String user, String eventId, Object data) throws Exception {

		Set<Object> s = events.keySet().stream().filter(u -> user == null || user.equals(u))
				.filter(u -> events.get(u) != null && events.get(u).contains(eventId)).map(u -> sessions.get(u))
				.collect(Collectors.toSet());

		if (s != null && s.size() > 0) {
			for (Object ss : s) {
				sendMessage(ss, eventId , true,data);
			}
		}
	}

	/**
	 * 订阅心跳服务
	 * @param user
	 * @throws IOException
	 */
	public static void subscribePong(String user) throws IOException {
		if (isRegisterClient(user) && !QuartzManager.hasJob(user + "-websocket-pong")) {
			
			int interval = Integer.valueOf(ConfigPropertyUtil.getProperty("websocket.pong.interval.second", "10"));

			Map<String, Object> parameter = Maps.newHashMap();
			parameter.put("user", user);
			QuartzManager.addRepeatJob(user + "-websocket-pong", null, null, null, PongJob.class, parameter, null, interval);
			
			sendMessage(user, "subscribe-ping",  true,"subscribe ok");
		}else {
			sendMessage(user, "subscribe-ping",  false,"subscribe fail");
		}
		
	}
	/**
	 * 退订心跳服务
	 * @param user
	 * @throws IOException
	 */
	public static void unSubscribePong(String user) throws IOException {
		if (isRegisterClient(user)) {
			QuartzManager.removeJob(user + "-websocket-pong");
		}
		sendMessage(user, "unSubscribe-ping", true,"unSubscribe ok");
	}

	/**
	 * 订阅http服务,未实现
	 * @param user
	 * @param content
	 * @throws IOException
	 */
	public static void subscribeHttp(String user, Object content) throws IOException {
		sendMessage(user, "subscribe-http", false ,"not supported");
	}
	
	/**
	 * 异常处理
	 * @param user
	 * @param e
	 * @throws IOException
	 */
	public static void dealError(String user,Throwable e) throws IOException {
		//sendMessage(user, "error", false,  e.getMessage());
	}
	
	/**
	 * 发送事件消息
	 * @param user
	 * @param eventId
	 * @param success
	 * @param data
	 * @throws IOException
	 */
	public static void sendMessage(String user,String eventId ,boolean success, Object data) throws IOException {
		Object session = sessions.get(user);
		sendMessage(session, eventId,success, data);
	}
	/**
	 * 发送事件消息
	 * @param session
	 * @param eventId
	 * @param success
	 * @param data
	 * @throws IOException
	 */
	public static void sendMessage(Object session,String eventId ,boolean success , Object data) throws IOException {
		
		Map<String, Object> res = Maps.newHashMap();
		res.put("eventId", eventId);
		res.put("success", success); 
		res.put("data", data); 
		String message = JsonUtil.transObjectTojson(res);
		
		if (session != null && session instanceof WebSocketSession && ((WebSocketSession) session).isOpen()) {
			TextMessage returnMessage = new TextMessage(message);
			((WebSocketSession) session).sendMessage(returnMessage);
		} else if (session != null && session instanceof Session && ((Session) session).isOpen()) {
			((Session) session).getBasicRemote().sendText(message);
		}
	}
	
}
