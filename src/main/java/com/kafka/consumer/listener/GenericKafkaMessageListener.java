package com.kafka.consumer.listener;

import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;

/*
 * GenericKafkaMessageListener is Kafka Listener class which consumes the data from Event hub
 */

@Component
public class GenericKafkaMessageListener {
	
	public static final Logger log = LoggerFactory.getLogger(GenericKafkaMessageListener.class);

	/*
	 * handleMessage method consumes data for a topic and group id
	 */
	 @KafkaListener(topics = "${spring.kafka.consumer.topics}" , concurrency = "${spring.kafka.consumer.concurrency}"
			 , groupId = "${spring.kafka.consumer.groupId}", containerFactory = "${spring.kafka.consumer.containerFactory}") 
	 public void handleMessage(ConsumerRecord<String, String> cr, final String message
			 , @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic
			 , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String receivedPartition
			 , @Header(KafkaHeaders.OFFSET) String offset
			 , @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String receivedTimestamp
			 , @Header(KafkaHeaders.TIMESTAMP_TYPE) String timestampType) throws Exception {
		 
		 log.info("GenericKafkaMessageListener");
		 
		 log.info("Logger 1 [JSON] received key {}: Payload: {} | Record: {}", cr.key(),
	                message, cr.toString());
		 
		 log.debug("RECEIVED_TOPIC - {} ", receivedTopic);
		 log.debug("RECEIVED_PARTITION_ID - {} ", receivedPartition);
		 log.debug("OFFSET - {} ", offset);
		 log.debug("RECEIVED_TIMESTAMP - {} ", receivedTimestamp);
		 log.debug("TIMESTAMP_TYPE - {} ", timestampType);
		 
		 log.info("message - {}", message); 
		 
		 latch.countDown();
	 }
	 
	 private CountDownLatch latch = new CountDownLatch(1);

	 public CountDownLatch getLatch() {
		 return latch;
	 }
}
