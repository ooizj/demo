package me.ooi.demo.testspringboot213;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringTest1 {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private TestRabbitMQTransaction testRabbitMQTransaction;
	
	@Test
	public void t1() {
		rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
	}
	
	@Test
	public void t2() {
		rabbitTemplate.convertAndSend(RabbitMQConfig.directExchangeName2, "abc", "123456");
	}
	
	@Test
	public void t3() {
		String message = (String) rabbitTemplate.receiveAndConvert(RabbitMQConfig.queueName2);
		System.out.println(message);
	}
	
	
	@Test
	public void testWrite() {
		testRabbitMQTransaction.testWrite();
	}
	
	@Test
	public void testWrite2() {
		testRabbitMQTransaction.testWrite2();
	}
	
	@Test
	public void testWrite3() {
		testRabbitMQTransaction.testWrite3();
	}
	
	@Test
	public void testRead() {
		testRabbitMQTransaction.testRead();
	}

}
