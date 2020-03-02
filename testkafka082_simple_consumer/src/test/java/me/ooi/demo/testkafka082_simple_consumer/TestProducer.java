package me.ooi.demo.testkafka082_simple_consumer;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestProducer {
	
	private Producer<Integer, String> producer;
	private String topic;

	public TestProducer(String topic) {
		
		//https://kafka.apache.org/082/documentation.html 
		//Producer Configs
		
		Properties props = new Properties();
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", "localhost:9092");
		// Use random partitioner. Don't need the key type. Just set it to
		// Integer.
		// The message is of type String.
		producer = new Producer<Integer, String>(new ProducerConfig(props));
		this.topic = topic;
	}

	public void run() {
		for (int i = 0; i < 30; i++) {
			producer.send(new KeyedMessage<Integer, String>(topic, "Message_"+i));
		}
	}
	
	public static void main(String[] args) {
		new TestProducer("topic-9").run();
	}

}
