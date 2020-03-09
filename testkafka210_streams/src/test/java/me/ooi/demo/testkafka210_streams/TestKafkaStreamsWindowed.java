package me.ooi.demo.testkafka210_streams;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.SessionWindowedKStream;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 官方文档: https://kafka.apache.org/21/documentation/streams/developer-guide/dsl-api.html
 * @author jun.zhao
 * @since 1.0
 */
public class TestKafkaStreamsWindowed {
	
	//需要先创建这个队列
	public static String TOPIC_INPUT = "fffsssfff" ; 
	
	private StreamsBuilder builder ; 
	private KStream<String, String> source ;
	
	@Before
	public void init() {
		builder = new StreamsBuilder();
		source = builder.stream(TOPIC_INPUT);
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
	
	//test aggregate
	//Rolling aggregation. Aggregates the values of (non-windowed) records by the grouped key. 
	//Aggregating is a generalization of reduce and allows, 
	//for example, the aggregate value to have a different type than the input values.
	@Test
	public void testAggregate() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		KTable<String, Long> aggregatedStream = groupedStream.aggregate(
				()->0L, //initializer 
				(key, value, aggregate) -> {
					return Integer.parseInt(value) + aggregate ; 
				},
				Materialized.with(Serdes.String(), Serdes.Long())) ; 
		aggregatedStream.toStream().print(Printed.toSysOut());
	}
	
	//Windowed aggregation. Aggregates the values of records, per window, by the grouped key. 
	//Aggregating is a generalization of reduce and allows, 
	//for example, the aggregate value to have a different type than the input values. 
	//连续时间段内的数据为一组数据
	@Test
	public void testSessionWindowedAggregate() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		SessionWindowedKStream<String, String> sessionWindowedKStream = groupedStream.windowedBy(SessionWindows.with(Duration.ofSeconds(5L)));
		KTable<Windowed<String>, Long> aggregatedStream = sessionWindowedKStream.aggregate(
				()->0L, //initializer 
				(key, value, aggregate) -> {
					System.out.println("key："+key+"，值："+value+"，聚合值："+aggregate);
					return Integer.parseInt(value) + aggregate ;
				},
				(aggKey, aggOne, aggTwo) -> {
					System.out.println("aggKey："+aggKey+"，前面的值："+aggOne+"，后面的值："+aggTwo);
					return aggTwo ; 
				},
				Materialized.with(Serdes.String(), Serdes.Long())) ; 
		aggregatedStream.toStream().print(Printed.toSysOut());
	}
	
	//test TimeWindows
	//固定时间段内的数据为一组数据
	@Test
	public void testTimeWindowedAggregate() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		TimeWindowedKStream<String, String> timeWindowedKStream = groupedStream.windowedBy(TimeWindows.of(Duration.ofSeconds(5L)));
		KTable<Windowed<String>, Long> aggregatedStream = timeWindowedKStream.aggregate(
				()->0L, //initializer 
				(key, value, aggregate) -> {
					System.out.println("key："+key+"，值："+value+"，聚合值："+aggregate);
					return Integer.parseInt(value) + aggregate ;
				},
				Materialized.with(Serdes.String(), Serdes.Long())) ; 
		aggregatedStream.toStream().print(Printed.toSysOut());
	}
	
	//test count
	@Test
	public void testWindowedCount() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		TimeWindowedKStream<String, String> timeWindowedKStream = groupedStream.windowedBy(TimeWindows.of(Duration.ofSeconds(5L)));
		KTable<Windowed<String>, Long> aggregatedStream = timeWindowedKStream.count() ; 
		aggregatedStream.toStream().print(Printed.toSysOut());
	}
	
	//test Reduce
	@Test
	public void testReduce() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		TimeWindowedKStream<String, String> timeWindowedKStream = groupedStream.windowedBy(TimeWindows.of(Duration.ofSeconds(5L)));
		KTable<Windowed<String>, String> aggregatedStream = timeWindowedKStream.reduce(
				(value1, value2) -> {
					System.out.println("聚合值："+value1+"，值："+value2);
					return String.valueOf(Long.parseLong(value1)+Long.parseLong(value2)) ; 
				}) ; 
		aggregatedStream.toStream().print(Printed.toSysOut());
	}
	
	
}