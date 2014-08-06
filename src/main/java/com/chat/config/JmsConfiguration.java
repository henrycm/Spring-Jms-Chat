package com.chat.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import com.chat.service.Receiver;

@Configuration
public class JmsConfiguration {

	private final String QUEUE_NAME = "chat";

	@Bean
	Receiver receiver() {
		return new Receiver();
	}

	@Bean
	public ConnectionFactory jmsConnectionFactory() {
		final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("tcp://localhost:61616");
		RedeliveryPolicy rp = new RedeliveryPolicy();
		rp.setMaximumRedeliveries(2);
		rp.setInitialRedeliveryDelay(0);
		rp.setRedeliveryDelay(10000);
		factory.setRedeliveryPolicy(rp);
		return factory;
	}

	@Bean
	public Queue requestsQueue() {
		return new ActiveMQQueue(QUEUE_NAME);
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		final JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory());
		jmsTemplate.setDefaultDestination(requestsQueue());
		return jmsTemplate;
	}

	@Bean
	MessageListenerAdapter adapter(Receiver receiver) {
		MessageListenerAdapter messageListener = new MessageListenerAdapter(receiver);
		messageListener.setDefaultListenerMethod("receiveMessage");
		return messageListener;
	}

	@Bean
	SimpleMessageListenerContainer container(MessageListenerAdapter messageListener, ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setMessageListener(messageListener);
		container.setConnectionFactory(connectionFactory);
		container.setDestinationName(QUEUE_NAME);
		container.setSessionTransacted(true);
		return container;
	}

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(JmsConfiguration.class);
		ctx.refresh();
		SimpleMessageListenerContainer myBean = ctx.getBean(SimpleMessageListenerContainer.class);
		System.out.println(myBean.toString());
	}
}
