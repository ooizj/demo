package me.ooi.demo.testkafka210_streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 官方文档: https://kafka.apache.org/21/documentation/streams/developer-guide/interactive-queries.html
 * @author jun.zhao
 * @since 1.0
 */
public class TestKafkaStreamsStore {
	
	//需要先创建这个队列
	public static String TOPIC_INPUT = "streams-TestKafkaStreamsStore3-input" ; 
	
	private StreamsBuilder builder ; 
	private KafkaStreams streams ; 
	
	@Before
	public void init() {
		builder = new StreamsBuilder();
	}
	
	@After
	public void destroy() throws InterruptedException{
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			streams.close();
	    }));
		
		Thread.sleep(1000*60);
	}
	
	@Test
	public void testLocalStore() throws InterruptedException {
		
		KStream<String, String> source = builder.stream(TOPIC_INPUT);
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		groupedStream.count(Materialized.as("local-store")) ; 
		
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-TestKafkaStreams");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		
		Topology topology = builder.build();
		streams = new KafkaStreams(topology, props);
		streams.start();
		while( streams.state() != KafkaStreams.State.RUNNING ){
			Thread.sleep(1000*1);
		}
		
		//等放入数据了看看
		Thread.sleep(1000*5);
		ReadOnlyKeyValueStore<String, Long> keyValueStore =
			    streams.store("local-store", QueryableStoreTypes.keyValueStore());
		Long count = keyValueStore.get("k1") ; 
		System.out.println("k1="+count);
	}
	
	
//	@Test
//	public void testGlobalStore() throws InterruptedException {
//		try {
//			StreamsBuilder builder2 = new StreamsBuilder() ; 
//			GlobalKTable<String, String> gt = builder2.globalTable("gt", Materialized.as("gt-store")) ; 
//			System.out.println(gt.queryableStoreName());
//			
//			KStream<String, String> source = builder.stream(TOPIC_INPUT);
//			KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
//			groupedStream.count(Materialized.as("gt-store")) ; 
//			
//			Properties props = new Properties();
//			props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-TestKafkaStreams");
//			props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//			props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//			props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//			
//			Topology topology = builder.build();
//			streams = new KafkaStreams(topology, props);
//			streams.start();
//			while( streams.state() != KafkaStreams.State.RUNNING ){
//				Thread.sleep(1000*1);
//			}
//			
//			//等放入数据了看看
//			Thread.sleep(1000*5);
//			ReadOnlyKeyValueStore<String, Long> keyValueStore =
//				    streams.store("gt-store", QueryableStoreTypes.keyValueStore());
//			Long count = keyValueStore.get("k1") ; 
//			System.out.println("k1="+count);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
}