package com.lee.websocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

public class HandshakeInterceptor implements org.springframework.web.socket.server.HandshakeInterceptor {

    //进入hander之前的拦截
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            
            String clientName = (String)servletRequest.getServletRequest().getParameter("name");
            System.out.println(clientName);
            
            HttpSession session = servletRequest.getServletRequest().getSession(true);
//            String userName = "lee";
            if (session != null) {
                //使用userName区分WebSocketHandler，以便定向发送消息
//                String clientName = (String) session.getAttribute("WEBSOCKET_USERNAME");
                map.put("WEBSOCKET_USERNAME", clientName);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
    
}