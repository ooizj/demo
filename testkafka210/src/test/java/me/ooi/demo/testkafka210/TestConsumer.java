package me.ooi.demo.testkafka210;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestConsumer {
	
	public void run() throws IOException {
	    final String TOPIC = "topic-9" ; 
	    Properties props = new Properties() ; 
	    props.load(TestConsumer.class.getResourceAsStream("/consumer.properties"));
	    KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);
	    consumer.subscribe(Arrays.asList(TOPIC));
	    
	    Runtime.getRuntime().addShutdownHook(new Thread(()->{
	        consumer.close();
	    }));

	    //轮询消息
	    while (true) {
	        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(10)); //最多等10s 
	        if( records != null ){
	            System.out.println("本次获取到的消息数量："+records.count());
	            for (ConsumerRecord<Integer, String> record : records) {
	            	System.out.println("收到消息："+record.value()) ; 
	            }
	        }
	    }
	}
	
	public void testSeek() throws IOException {
	    final String TOPIC = "topic-9" ; 
	    Properties props = new Properties() ; 
	    props.load(TestConsumer.class.getResourceAsStream("/consumer.properties"));
	    KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);
	    consumer.subscribe(Arrays.asList(TOPIC), new ConsumerRebalanceListener() {
			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
			}
			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				//设置offset为0以获取包含以前的所有数据
//				consumer.seek(new TopicPartition(TOPIC, 0), 0);
				consumer.seekToBeginning(partitions) ; 
			}
		});
	    
	    Runtime.getRuntime().addShutdownHook(new Thread(()->{
	        consumer.close();
	    }));

	    //轮询消息
	    while (true) {
	        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(10)); //最多等10s 
	        if( records != null ){
	            System.out.println("本次获取到的消息数量："+records.count());
	            for (ConsumerRecord<Integer, String> record : records) {
	            	System.out.println("收到消息："+record.value()) ; 
	            }
	        }
	    }
	}
	
	public static void main(String[] args) throws Exception {
//		new TestConsumer().run();
		new TestConsumer().testSeek();
	}

}
