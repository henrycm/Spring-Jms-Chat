package com.chat.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.chat.dto.EmailDTO;

public class Receiver {
	@Autowired
	private ChatService service;

	public void receiveMessage(EmailDTO message) throws Exception {
		System.out.println("Received <" + message.getMsg() + ">");
		for (WebSocketSession sock : service.getConns()) {
			try {
				sock.sendMessage(new TextMessage(message.getAsunto()));
			} catch (IOException e) {
				System.out.println("Error when sending message message");
			}
		}
	}
}
