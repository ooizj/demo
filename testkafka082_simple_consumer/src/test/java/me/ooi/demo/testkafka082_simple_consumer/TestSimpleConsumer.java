package me.ooi.demo.testkafka082_simple_consumer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestSimpleConsumer {
	
	private static void printMessages(int partition, ByteBufferMessageSet messageSet) throws UnsupportedEncodingException {
		for (MessageAndOffset messageAndOffset : messageSet) {
			ByteBuffer payload = messageAndOffset.message().payload();
			byte[] bytes = new byte[payload.limit()];
			payload.get(bytes);
			System.out.println("partition["+partition+"]获取到消息："+new String(bytes, "UTF-8"));
		}
	}

	public static void main(String[] args) throws Exception {
		String clientId = "c1" ; 
		String topic = "topic-9" ; 
		int partitionCount = 3 ; 
		int fetchSize = 500 ; //单条数据大小
		int soTimeout = 1000*2 ; 
		int bufferSize = 1024*64 ; 
		for (int partition = 0; partition < partitionCount; partition++) {
			SimpleConsumer simpleConsumer = new SimpleConsumer("localhost", 9092, soTimeout, bufferSize, clientId);
			for (int offset = 0; offset < 10000; offset++) {
				FetchRequest req = new FetchRequestBuilder().clientId(clientId)
						.addFetch(topic, partition, offset, fetchSize).build();
				FetchResponse fetchResponse = simpleConsumer.fetch(req);
				ByteBufferMessageSet messageSet = fetchResponse.messageSet(topic, partition) ; 
				if( messageSet.sizeInBytes() == 0 ){
					break ; 
				}
				printMessages(partition, messageSet);
			}
		}
	}

}
