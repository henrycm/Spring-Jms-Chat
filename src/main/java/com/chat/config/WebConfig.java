package com.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.chat.handler.ChatWebSocketHandler;

@Configuration
@EnableWebMvc
@EnableWebSocket
@ComponentScan(basePackages = { "com.chat.service" })
public class WebConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(chatWebSocketHandler(), "/chat").withSockJS();
		System.out.println("Handler registered!");
	}

	@Bean
	public WebSocketHandler chatWebSocketHandler() {
		return new PerConnectionWebSocketHandler(ChatWebSocketHandler.class);
	}

	// Allow serving HTML files through the default Servlet
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}
