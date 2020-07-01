package me.ooi.demo.testspringboot213;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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
	
	@Component
	public class Receiver {

//	  private CountDownLatch latch = new CountDownLatch(1);

	  public void receiveMessage(String message) {
	    System.out.println("Received <" + message + ">");
//	    latch.countDown();
	  }

//	  public CountDownLatch getLatch() {
//	    return latch;
//	  }

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
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	
}