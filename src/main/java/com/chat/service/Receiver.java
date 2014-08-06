package com.chat.service;

import com.chat.dto.EmailDTO;

public class Receiver {
	public void receiveMessage(EmailDTO message) throws Exception {
		System.out.println("Received <" + message.getMsg() + ">");
	}
}
