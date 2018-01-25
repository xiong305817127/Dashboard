package com.xh.websocket.endpoint;

import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.xh.dto.UserDto;
import com.xh.util.JsonUtil;
import com.xh.util.Utils;
import com.xh.websocket.WebSocketManager;

/**
 * spring xml 方式的websocket 处理器 (/ws/websocket)
 * 发送  {"type":"subscribe/unsubscribe","id":"ping/http/EventID" ,"data":"数据"} 格式的数据 进行服务订阅
 * @author XH
 * @since 2018年1月24日
 *
 */
public class WebsocketEndPoint extends TextWebSocketHandler {
	
	
    @SuppressWarnings("unchecked")
	@Override  
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {  
        super.handleTextMessage(session, message); 
        if(message == null || Utils.isEmpty(message.getPayload()) || !message.getPayload().startsWith("{")) {
			return ;
		}
        Map<String,Object> msg = JsonUtil.transJsonToObject(message.getPayload(), Map.class);
		
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
    
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		WebSocketManager.registerClient(getUser(session), session);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		WebSocketManager.dealError(getUser(session), exception);
		exception.printStackTrace();
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		WebSocketManager.unregisterClient(getUser(session));
	}
	
	
	
	private String getUser(WebSocketSession session) {
		UserDto user = (UserDto) session.getAttributes().get("user");
		if( user != null ) {
			return user.getUsername();
		}
		 return "anonymous";
	}
	
}
