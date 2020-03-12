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
	
	@Test
	public void testAggregate(){
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k1", "v1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k2", "v2"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k1", "v1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "k1", "v3"));
	}
	
	@Test
	public void testReduce(){
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "xiaoming", "101"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "xiaoming", "50"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "xiaohong", "78"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreams.TOPIC_INPUT, "xiaoqing", "100"));
	}
	
	@Test
	public void testWindowedAggregate(){
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k1", "1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k2", "2"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k1", "1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k1", "3"));
	}
	
	@Test
	public void testWindowedAggregate2() throws InterruptedException{
		
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k1", "1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k2", "2"));
		
		//2s后放入一条
		Thread.sleep(1000*2);
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k1", "10"));
		
		//4s后放入一条
		Thread.sleep(1000*4);
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k1", "1"));
		
		//6s后放入一条
		Thread.sleep(1000*6);
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsWindowed.TOPIC_INPUT, "k1", "3"));
	}
	
	@Test
	public void testJoin() throws InterruptedException {
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k1", "1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k2", "2"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k2", "33"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k2", "2"));
		
		//2s后放入一条
		Thread.sleep(1000*2);
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k3", "3"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k4", "4"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k3", "3"));
		
		//4s后放入一条
		Thread.sleep(1000*4);
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k5", "5"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k4", "4321"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k1", "1a"));
		
		//6s后放入一条
		Thread.sleep(1000*6);
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k5", "5"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k3", "3333"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k3", "222333"));
	}
	
	@Test
	public void testTableJoin() throws InterruptedException {
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k1", "1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_LEFT, "k2", "2"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k2", "33"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsJoin.TOPIC_INPUT_RIGHT, "k2", "2"));
	}
	
	@Test
	public void testLocalStore(){
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsStore.TOPIC_INPUT, "k1", "v1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsStore.TOPIC_INPUT, "k2", "v2"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsStore.TOPIC_INPUT, "k1", "v1"));
		producer.send(new ProducerRecord<String, String>(TestKafkaStreamsStore.TOPIC_INPUT, "k1", "v3"));
	}
	
}
