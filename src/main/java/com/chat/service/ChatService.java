package com.chat.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.chat.dto.EmailDTO;

@Service
public class ChatService {

	private Set<WebSocketSession> conns = java.util.Collections.synchronizedSet(new HashSet<WebSocketSession>());
	private Map<WebSocketSession, String> nickNames = new ConcurrentHashMap<WebSocketSession, String>();

	@Autowired
	private JmsTemplate jmsTemplate;

	public void registerOpenConnection(WebSocketSession session) {
		conns.add(session);
	}

	public void registerCloseConnection(WebSocketSession session) {
		String nick = nickNames.get(session);
		conns.remove(session);
		nickNames.remove(session);
		if (nick != null) {
			String messageToSend = "{\"removeUser\":\"" + nick + "\"}";
			for (WebSocketSession sock : conns) {
				try {
					sock.sendMessage(new TextMessage(messageToSend));
				} catch (IOException e) {
					System.out.println("IO exception when sending remove user message");
				}
			}
		}
	}

	public void processMessage(WebSocketSession session, String message) {
		if (!nickNames.containsKey(session)) {
			// No nickname has been assigned by now
			// the first message is the nickname
			// escape the " character first
			message = message.replace("\"", "\\\"");

			// broadcast all the nicknames to him
			for (String nick : nickNames.values()) {
				try {
					session.sendMessage(new TextMessage("{\"addUser\":\"" + nick + "\"}"));
				} catch (IOException e) {
					System.out.println("Error when sending addUser message");
				}
			}

			// Register the nickname with the
			nickNames.put(session, message);

			// broadcast him to everyone now
			String messageToSend = "{\"addUser\":\"" + message + "\"}";
			for (WebSocketSession sock : conns) {
				try {
					sock.sendMessage(new TextMessage(messageToSend));
				} catch (IOException e) {
					System.out.println("Error when sending broadcast addUser message");
				}
			}
		} else {
			// Broadcast the message
			final String messageToSend = "{\"nickname\":\"" + nickNames.get(session) + "\", \"message\":\""
					+ message.replace("\"", "\\\"") + "\"}";
			jmsTemplate.send(new MessageCreator() {
				public ObjectMessage createMessage(Session session) throws JMSException {
					ObjectMessage message = session.createObjectMessage();
					EmailDTO dto = new EmailDTO();
					dto.setDe("henrycm@gmail.com");
					dto.setPara("linaberrioh@hotmail.com");
					dto.setAsunto(messageToSend);
					dto.setMsg("... prueba " + new Date());
					message.setObject(dto);
					return message;
				}
			});			
		}
	}

	public Set<WebSocketSession> getConns() {
		return conns;
	}

	public void setConns(Set<WebSocketSession> conns) {
		this.conns = conns;
	}

}
