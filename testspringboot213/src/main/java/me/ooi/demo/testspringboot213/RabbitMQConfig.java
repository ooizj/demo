package me.ooi.demo.testspringboot213;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Configuration
public class RabbitMQConfig {

	public static final String topicExchangeName = "spring-boot-exchange";
	
	public static final String directExchangeName2 = "spring-boot-exchange2";
	
	public static final String queueName = "spring-boot";
	
	public static final String queueName2 = "spring-boot2";
	
	public static final String queueName3 = "spring-boot3";
	
	@Component
	public class Receiver {

		// 注意：开启事务后报错会不断重试
		public void receiveMessage(String message) {
//		  int a = 3/0;
			System.out.println("Received <" + message + ">");
		}
	}
	
	//注意：开启事务后报错会不断重试
	@RabbitListener(queuesToDeclare = {@org.springframework.amqp.rabbit.annotation.Queue(name = "q3")})
	public void testListener(byte[] data) {
		try {
			System.out.println("receive q3 message:"+new String(data, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
//		int a = 3/0;
	}
	
	//注意：开启事务后报错会不断重试
	@RabbitListener(queues = queueName3)
	public void testListener2(String message) {
		System.out.println(String.format("receive %s message:%s", queueName3, message));
//			int a = 3/0;
	}

	@Bean
	public Queue queue() {
		return new Queue(queueName, false);
	}
	
	@Bean
	public Queue queue2() {
		return new Queue(queueName2, false);
	}
	
	@Bean
	public Queue queue3() {
		return new Queue(queueName3, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(topicExchangeName);
	}
	
	@Bean
	public DirectExchange exchange2() {
		return new DirectExchange(directExchangeName2);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
	}
	
	@Bean
	public Binding binding2(@Qualifier("queue2") Queue queue2, @Qualifier("exchange2") DirectExchange exchange2) {
		return BindingBuilder.bind(queue2).to(exchange2).with("abc");
	}
	
	@Bean
	public Binding binding3(@Qualifier("queue3") Queue queue3, @Qualifier("exchange2") DirectExchange exchange2) {
		return BindingBuilder.bind(queue3).to(exchange2).with("ddd");
	}
	
	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter, PlatformTransactionManager transactionManager) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		
		//enable transaction
		container.setTransactionManager(transactionManager);
		container.setChannelTransacted(true);
		
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		
		//enable transaction
		rabbitTemplate.setChannelTransacted(true);
		
		return rabbitTemplate;
	}
	
}