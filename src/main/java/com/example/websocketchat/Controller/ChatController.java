package com.example.websocketchat.Controller;

import com.example.websocketchat.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage chat(final ChatMessage message){
        return message;
    }

    @MessageMapping("/private")
    public void privateMsg(Principal principal, final ChatMessage message){
        messagingTemplate.convertAndSendToUser(message.getTo(), "/queue/reply", message);
//        messagingTemplate.convertAndSendToUser(message.getTo(), "/queue/reply", message);
    }
}
