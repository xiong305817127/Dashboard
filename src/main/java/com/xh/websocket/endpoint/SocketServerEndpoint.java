package com.xh.websocket.endpoint;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.xh.dto.UserDto;
import com.xh.util.JsonUtil;
import com.xh.util.Utils;
import com.xh.websocket.WebSocketManager;
import com.xh.websocket.config.HttpSessionConfigurator;

/**
 * 注解方式 的web socket处理器
 * 发送  {"type":"subscribe/unsubscribe","id":"ping/http/EventID" ,"data":"数据"} 格式的数据 进行服务订阅
 * @author XH
 * @since 2018年1月25日
 *
 */
@ServerEndpoint(value = "/ws/websocket1",configurator = HttpSessionConfigurator.class)
public class SocketServerEndpoint {
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) throws IOException {
		setUser(session, config);
		WebSocketManager.registerClient(getUser(session), session);
	}

	@OnError
	public void onError(Session session, Throwable thr) throws IOException {
		WebSocketManager.dealError(getUser(session), thr);
		thr.printStackTrace();
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) throws IOException {
		WebSocketManager.unregisterClient(getUser(session));
	}

	/**
	 * 
	 * @param message ,json字符串:{type:subscribe/unsubscribe , id: ping/http/ID ,data:数据}
	 * @param session
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@OnMessage
	public void onMessage(String message, Session session) throws JsonParseException, JsonMappingException, IOException {
		if(Utils.isEmpty(message) || !message.startsWith("{")) {
			return ;
		}
		Map<String,Object> msg = JsonUtil.transJsonToObject(message, Map.class);
		
		String type = (String) msg.get("type"); //类型: subscribe / unsubscribe
		String id = (String) msg.get("id"); //事件ID: ping / http(未实现) / 事件Id
		String data = (String) msg.get("data"); //数据
		
		if(Utils.isEmpty(id)) {
			return ;
		}
		
		switch(id) {
			case "ping" : if( "subscribe".equalsIgnoreCase(type) ) {
								 WebSocketManager.subscribePong(getUser(session)); 
							}else {
								 WebSocketManager.unSubscribePong(getUser(session)) ;
							}
							break ;
			case "http" :  WebSocketManager.subscribeHttp(getUser(session),  data); 
							break ;
			default :  if("subscribe".equalsIgnoreCase( type )) {
							WebSocketManager.subscribeServer(getUser(session), id); 
						}else {
							WebSocketManager.unSubscribeServer(getUser(session), id );
						}
						break ;
		 }
	}
	
	private String getUser(Session session) {
		UserDto user = (UserDto) session.getUserProperties().get("user");
		if( user != null ) {
			return user.getUsername();
		}
		 return "anonymous";
	}
	
	private void setUser(Session session,EndpointConfig config) {
		
		UserDto user = (UserDto) config.getUserProperties().get("user");
		if(user ==  null) {
			HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
			user = (UserDto) httpSession.getAttribute("user");
		}
		if(user != null) {
			session.getUserProperties().put("user", user);
		}
	}
}
