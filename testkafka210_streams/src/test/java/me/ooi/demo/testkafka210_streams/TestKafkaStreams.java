package me.ooi.demo.testkafka210_streams;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestKafkaStreams {
	
	//需要先创建这个队列
	public static String TOPIC_INPUT = "streams-TestKafkaStreams-input" ; 
	
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
	
	//test map
	//Takes one record and produces one record. You can modify the record key and value, including their types.
	//eg: input[(k1,v1),(k2,v2)], output[(k1_k,v1_v),(k2_k,v2_v)]
	@Test
	public void testMap() {
		source.map((key, value) -> KeyValue.pair(key+"_k", value+"_v")).to(TestConsumer.TOPIC_OUTPUT); 
	}
	
	//Marks the stream for data re-partitioning: Applying a grouping or a join after map will result in re-partitioning of the records. If possible use mapValues instead, which will not cause data re-partitioning.
	//eg: input[(k1,v1),(k2,v2)], output[(k1,v1_v),(k2,v2_v)]
	@Test
	public void testMapValues() {
		source.mapValues((key, value)->value+"_v").to(TestConsumer.TOPIC_OUTPUT);
	}
	
	//test flatMap
	//Takes one record and produces zero, one, or more records, while retaining the key of the original record. You can modify the record values and the value type.
	//eg: input[(k1,1_1),(k2,2_2)], output[(k1,1),(k1,1),(k2,2),(k2,2)]
	@Test
	public void testFlatMap() {
		source.flatMap((key, value) -> {
			String[] vs = value.split("_") ; 
			return Arrays.asList(KeyValue.pair(key, vs[0]), KeyValue.pair(key, vs[1])) ; 
		}).to(TestConsumer.TOPIC_OUTPUT);
	}
	
	//flatMapValues is preferable to flatMap because it will not cause data re-partitioning. However, you cannot modify the key or key type like flatMap does.
	//eg: input[(k1,1_1),(k2,2_2)], output[(k1,1),(k1,1),(k2,2),(k2,2)]
	@Test
	public void testFlatMapValues() {
		source.flatMapValues((key, value) -> {
			String[] vs = value.split("_") ; 
			return Arrays.asList(vs[0], vs[1]) ; 
		}).to(TestConsumer.TOPIC_OUTPUT);
	}
	
	//test filter/filterNot
	@Test
	public void testFilter() {
		source.filter((key, value) -> {
			try {
				return Integer.parseInt(value) < 5 ; 
			} catch (Exception e) {
				return false ; 
			}
		}).to(TestConsumer.TOPIC_OUTPUT);
	}
	
	//test foreach
	@Test
	public void testForeach() {
		source.foreach((key, value) -> System.out.println("key:"+key+",value:"+value));
	}
	
	//test print
	@Test
	public void testPrint() {
		source.print(Printed.toSysOut());
	}
	
	//test selectKey
	//Assigns a new key – possibly of a new key type – to each record
	@Test
	public void testSelectKey() {
		source.selectKey((key, value) -> "NEW"+key).to(TestConsumer.TOPIC_OUTPUT);
	}
	
	//test groupByKey
	//Groups the records by the existing key.
	//每次计算的都是所有的数据，包含以前的；
	//非实时计算
	@Test
	public void testGroupByKey() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		groupedStream.count().toStream().print(Printed.toSysOut());
	}
	
	//test groupBy
	@Test
	public void testGroupBy() {
		KGroupedStream<String, String> groupedStream = source.groupBy((key, value) -> key+value) ; 
		groupedStream.count().toStream().print(Printed.toSysOut());
	}
	
}