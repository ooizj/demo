package me.ooi.demo.testkafka210_streams;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 官方文档: https://kafka.apache.org/21/documentation/streams/developer-guide/dsl-api.html
 * @author jun.zhao
 * @since 1.0
 */
public class TestKafkaStreamsJoin {
	
	//需要先创建这个队列
	public static String TOPIC_INPUT_LEFT = "streams-TestKafkaStreamsJoin-left-input" ; 
	public static String TOPIC_INPUT_RIGHT = "streams-TestKafkaStreamsJoin-right-input" ; 
	
	private StreamsBuilder builder ; 
	
	@Before
	public void init() {
		builder = new StreamsBuilder();
	}
	
	@After
	public void destroy() throws InterruptedException{
		
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-TestKafkaStreams");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		
		final Topology topology = builder.build();
		final KafkaStreams streams = new KafkaStreams(topology, props);

		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			streams.close();
	    }));

		streams.start();
		
		Thread.sleep(1000*60);
	}
	
	//test join
	//是根据key来进行join的，key相同的才会join起来
	//按照JoinWindows设置的固定时间来进行分段，同一时间段内的才join的起来
	@Test
	public void testJoin() {
		KStream<String, String> left = builder.stream(TOPIC_INPUT_LEFT);
		KStream<String, String> right = builder.stream(TOPIC_INPUT_RIGHT);
		KStream<String, String> joined = left.join(
				right, 
				(leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue, /* ValueJoiner */ 
				JoinWindows.of(Duration.ofSeconds(5L))) ; 
		joined.print(Printed.toSysOut());
	}
	
	//test join
	@Test
	public void testTableJoin() {
		KStream<String, String> left = builder.stream(TOPIC_INPUT_LEFT);
		KStream<String, String> right = builder.stream(TOPIC_INPUT_RIGHT);
		KTable<String, Long> leftTable = left.groupByKey().count() ; 
		KTable<String, Long> rightTable = right.groupByKey().count() ; 
		KTable<String, String> joined = leftTable.join(
				rightTable, 
				(leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue /* ValueJoiner */ ) ; 
		joined.toStream().print(Printed.toSysOut());
	}
	
}