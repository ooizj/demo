package me.ooi.demo.testkafka210;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestProducer {
	
	public void run() throws IOException {
		final String TOPIC = "topic-9";
		Properties props = new Properties();
		props.load(TestProducer.class.getResourceAsStream("/producer.properties"));
		Producer<String, String> producer = new KafkaProducer<>(props);

		System.out.println("发送开始");
		final int MAX_COUNT = 10;
		for (int i = 0; i < MAX_COUNT; i++) {
			producer.send(new ProducerRecord<String, String>(TOPIC, "msg-" + i));
		}
		System.out.println("发送结束");

		producer.close();
	}
	
	public static void main(String[] args) throws Exception {
		new TestProducer().run();
	}

}
