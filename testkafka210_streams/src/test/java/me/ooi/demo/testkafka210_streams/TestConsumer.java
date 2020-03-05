package me.ooi.demo.testkafka210_streams;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestConsumer {
	
	public static String TOPIC_OUTPUT = "streams-TestConsumer-output" ; 
	
	public void run() throws IOException {
	    Properties props = new Properties() ; 
	    props.load(TestConsumer.class.getResourceAsStream("/consumer.properties"));
	    KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);
	    consumer.subscribe(Arrays.asList(TOPIC_OUTPUT));
	    
	    Runtime.getRuntime().addShutdownHook(new Thread(()->{
	        consumer.close();
	    }));

	    //轮询消息
	    while (true) {
	        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(10)); //最多等10s 
	        if( records != null ){
	            System.out.println("本次获取到的消息数量："+records.count());
	            for (ConsumerRecord<Integer, String> record : records) {
	            	System.out.println("收到消息：key["+record.key()+"],value["+record.value()+"]") ; 
	            }
	        }
	    }
	}
	
	public static void main(String[] args) throws Exception {
		new TestConsumer().run();
	}

}
