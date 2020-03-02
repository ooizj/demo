package me.ooi.demo.testkafka082_hight_level_consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestHightLevelConsumer {
	
	private final ConsumerConnector consumer;
	private final String topic;

	public TestHightLevelConsumer(String topic) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
	}

	private static ConsumerConfig createConsumerConfig() {
		
		//https://kafka.apache.org/082/documentation.html
		//Consumer Configs
		
		Properties props = new Properties();
		props.put("zookeeper.connect", "localhost:2181");
		props.put("group.id", "g1");
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	public void run() throws InterruptedException {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic) ; 
		final KafkaStream<byte[], byte[]> stream = streams.get(0);
		
		//test shutdown
		addShutdownTimer(1000*20);
		
		ConsumerIterator<byte[], byte[]> it = stream.iterator(); //stream.xxx() 将会进入等待
		while (it.hasNext()){
			System.out.println("获取到消息："+new String(it.next().message()));
		}
	}
	
	private void addShutdownTimer(final long millis){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				consumer.shutdown();
			}
		}).start();
	}
	
	public static void main(String[] args) throws Exception {
		new TestHightLevelConsumer("topic-9").run();
	}

}
