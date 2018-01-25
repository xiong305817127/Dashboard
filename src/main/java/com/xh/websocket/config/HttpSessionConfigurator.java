package com.xh.websocket.config;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

public class HttpSessionConfigurator extends Configurator {
	
	@Override
    public void modifyHandshake(ServerEndpointConfig sec,  HandshakeRequest request, HandshakeResponse response) {

         HttpSession httpSession=(HttpSession) request.getHttpSession();
         sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
         if(httpSession.getAttribute("user") != null) {
        	 sec.getUserProperties().put("user", httpSession.getAttribute("user"));
         }
         
         sec.getUserProperties().put("test", "test");
         
    }
	
}
