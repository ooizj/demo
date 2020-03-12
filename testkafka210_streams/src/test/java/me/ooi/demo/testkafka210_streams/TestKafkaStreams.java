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
import org.apache.kafka.streams.kstream.KGroupedTable;
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
	//非实时
	@Test
	public void testGroupByKey() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		KTable<String, Long> ktable = groupedStream.count() ; 
		ktable.toStream().print(Printed.toSysOut());
	}
	
	//test groupBy
	@Test
	public void testGroupBy() {
		KGroupedStream<String, String> groupedStream = source.groupBy((key, value) -> key+value) ; 
		KTable<String, Long> ktable = groupedStream.count() ; 
		ktable.toStream().print(Printed.toSysOut());
	}
	
	//test GlobalKTable
	@Test
	public void testGlobalKTable(){
		
	}
	
	//test Reduce
	@Test
	public void testReduce() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		KTable<String, String> aggregatedTable = groupedStream.reduce(
				(value1, value2) -> {
					System.out.println("聚合值："+value1+"，值："+value2);
					return value1+value2 ; 
				}) ; 
		aggregatedTable.toStream().print(Printed.toSysOut());
	}
	
	//test Reduce
	@Test
	public void testReduce2() {
		KGroupedStream<String, String> groupedStream = source.groupByKey() ; 
		KTable<String, String> ktable = groupedStream.reduce((aggValue, value) -> {
					System.out.println("聚合值："+aggValue+"，值："+value);
					try {
						int av = Integer.parseInt(aggValue) ; 
						int v = Integer.parseInt(value) ; 
						if( v > 100 ){
							return aggValue ; 
						}else {
							return String.valueOf(av+v) ; 
						}
					} catch (Exception e) {
						e.printStackTrace();
						return "0" ; 
					}
//					return "0" ; 
				}) ; 
		
		KGroupedTable<String, String>  groupedTable = ktable.groupBy((key, value)->KeyValue.pair(key, value)) ; //no change
		
		KTable<String, String> ktable2 = groupedTable.reduce((aggValue, value) -> {
			System.out.println("add 聚合值："+aggValue+"，值："+value);
			try {
				if( aggValue == null ){
					aggValue = "0" ; 
				}
				int av = Integer.parseInt(aggValue) ; 
				int v = Integer.parseInt(value) ; 
				return String.valueOf(av+v) ; 
			} catch (Exception e2) {
				e2.printStackTrace();
				return "0" ; 
			}
		}, (aggValue, value) -> {
			System.out.println("sub 聚合值："+aggValue+"，值："+value); 
			try {
				if( aggValue == null ){
					aggValue = "0" ; 
				}
				int av = Integer.parseInt(aggValue) ; 
				int v = Integer.parseInt(value) ; 
				return String.valueOf(av-v) ; 
			} catch (Exception e2) {
				e2.printStackTrace();
				return "0" ; 
			}
		}) ; 
		ktable2.toStream().print(Printed.toSysOut());
	}
	
}