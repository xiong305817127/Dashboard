package com.xh.websocket.config;

import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * 握手处理
 * @author XH
 * @since 2018年1月24日
 *
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		
		
	      HttpSession httpSession= ((ServletServerHttpRequest)request).getServletRequest().getSession(true);
		  attributes.put(HttpSession.class.getName(),httpSession);
	      if(httpSession.getAttribute("user") != null) {
	         attributes.put("user", httpSession.getAttribute("user"));
	      }
	      
	      attributes.put("test", "test");
		 return super.beforeHandshake(request, response, wsHandler, attributes);
		
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
		
		super.afterHandshake(request, response, wsHandler, ex);
	}
}
