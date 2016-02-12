package com.lee.websocket;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketHander implements WebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHander.class);

    private static final ArrayList<WebSocketSession> users = new ArrayList<>();

    //初次链接成功执行
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug("链接成功......");
        users.add(session);
        String userName = (String) session.getHandshakeAttributes().get("WEBSOCKET_USERNAME");
        if(userName!= null){
            session.sendMessage(new TextMessage("欢迎来到Nathan的聊天室，我们开始聊天吧！~"));
        }
    }

    //接受消息处理消息
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
    	String clientName = (String) session.getHandshakeAttributes().get("WEBSOCKET_USERNAME");
    	
    	clientName = "<a onclick='changeChater(this)'>" + clientName + "</a>";
    		
    	String msg = webSocketMessage.getPayload().toString();
    	String charter = "";
    	
    	String msgs[] = msg.split("\\|");
    	if (msgs.length > 1) {
    		msg = msgs[1];
    		charter = msgs[0];
    		sendMessageToUser(charter, new TextMessage(clientName + "  悄悄地对你说 ：" + msg));
    	} else {
    		sendMessageToUsers(new TextMessage(clientName + "  说：" + msg));
    	}
        
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if(webSocketSession.isOpen()){
            webSocketSession.close();
        }
        logger.debug("链接出错，关闭链接......");
        users.remove(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.debug("链接关闭......" + closeStatus.toString());
        users.remove(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (WebSocketSession user : users) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     */
    public void sendMessageToUser(String userName, TextMessage message) {
        for (WebSocketSession user : users) {
            if (user.getHandshakeAttributes().get("WEBSOCKET_USERNAME").equals(userName)) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
