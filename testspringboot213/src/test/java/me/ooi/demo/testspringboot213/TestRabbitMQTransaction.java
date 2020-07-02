package me.ooi.demo.testspringboot213;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class TestRabbitMQTransaction {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Transactional
	public void testWrite() {
		rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
		
//		int a = 3/0;
	}
	
	@Transactional
	public void testWrite2() {
		rabbitTemplate.convertAndSend(RabbitMQConfig.directExchangeName2, "abc", "123456");
		
		int a = 3/0;
	}
	
	@Transactional
	public void testWrite3() {
		rabbitTemplate.convertAndSend(RabbitMQConfig.directExchangeName2, "ddd", "aaaaaaaaaaaassssssssss2sdfsdf是的发送到发送到");
		
//		int a = 3/0;
	}
	
	@Transactional
	public void testRead() {
		String message = (String) rabbitTemplate.receiveAndConvert(RabbitMQConfig.queueName2);
		System.out.println(message);
		
//		int a = 3/0;
	}
	
}
