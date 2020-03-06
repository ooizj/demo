package me.ooi.demo.testkafka210_streams;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestProducer {
	
	private Producer<String, String> producer ; 
	
	@Before
	public void init() throws IOException{
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/producer.properties"));
		producer = new KafkaProducer<>(props);
	}
	
	@After
	public void destroy(){
		producer.close();
	}
	
	@Test
	public void testMap(){
		for (int i = 0; i < 3; i++) {
			producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k"+i, "v"+i));
		}
	}
	
	@Test
	public void testFlatMap(){
		for (int i = 0; i < 3; i++) {
			producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k"+i, i+"_"+i));
		}
	}
	
	@Test
	public void testFilter(){
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k000", "v1"));
		for (int i = 1; i <= 10; i++) {
			producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k"+i, ""+i));
		}
	}
	
	@Test
	public void testForeach(){
		for (int i = 1; i <= 10; i++) {
			producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k"+i, "v"+i));
		}
	}
	
	@Test
	public void testPrint(){
		for (int i = 1; i <= 10; i++) {
			producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k"+i, "v"+i));
		}
	}
	
	@Test
	public void testSelectKey(){
		for (int i = 1; i <= 10; i++) {
			producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k"+i, "v"+i));
		}
	}
	
	@Test
	public void testGroupBy(){
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k1", "v1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k2", "v2"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k1", "v1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k1", "v3"));
	}
	
	
}
