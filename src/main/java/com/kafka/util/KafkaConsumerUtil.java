package com.kafka.util;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.Map.Entry;

/*
 * KafkaConsumerUtil is a Consumer Utility class 
 */

public class KafkaConsumerUtil {
	
	public static final Logger log = LoggerFactory.getLogger(KafkaConsumerUtil.class);

	/**
	   * Set consumer group id's offset by date time.
	   * @param groupId Consumer Group ID
	   * @param topicName Topic Name
	   * @param localDateTime DateTime of enqueue dateTime
	   * @return A map of TopicPartition and OffsetAndMetadata.
	   * @throws Exception 
	   **/
		public synchronized Map<TopicPartition, OffsetAndMetadata> setOffsetByDateTime(Properties properties, String groupId, String topicName,
				LocalDateTime localDateTime) throws Exception {
			
			log.debug("**** KafkaConsumerUtil - setOffsetByDateTime ****");

		    KafkaConsumer<String, String> offsetterKafkaConsumer = new KafkaConsumer<>(properties);
		      
			ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("GMT"));
			long targetEpochMilli = zonedDateTime.toInstant().toEpochMilli();
			offsetterKafkaConsumer.subscribe(Arrays.asList(topicName));
			offsetterKafkaConsumer.poll(Duration.ofMillis(10_000L));
			Collection<TopicPartition> partitions = offsetterKafkaConsumer.assignment();

			Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
			partitions.forEach(tp -> {
				timestampsToSearch.computeIfAbsent(tp, tp1 -> targetEpochMilli);
			});

	  	  	Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsetsMapTemp = new HashMap<TopicPartition, OffsetAndMetadata>() ;

			Map<TopicPartition, OffsetAndTimestamp> partitionOffsetTimestamp = offsetterKafkaConsumer
					.offsetsForTimes(timestampsToSearch);
			for (Entry<TopicPartition, OffsetAndTimestamp> tp : partitionOffsetTimestamp.entrySet()) {
				OffsetAndMetadata oam = offsetterKafkaConsumer.committed(tp.getKey());
				if (oam != null) {
					log.info(tp.getKey() + "Current offset is " + oam.offset());
				} else {
					log.info("No committed offsets");
				}
				Long offset = tp.getValue().offset();
				if (offset != null) {
					log.info(tp.getKey() + "Seeking to " + tp.getValue().offset());
					offsetterKafkaConsumer.seek(tp.getKey(), tp.getValue().offset());
					// offsetterKafkaConsumer.seek(tp.getKey(), offset);
					OffsetAndMetadata oamNew;
					if(oam != null) {
						 oamNew = new OffsetAndMetadata(tp.getValue().offset(), "Old offset="+oam.offset() + ", Group Id="+groupId);
					} else {
						oamNew = new OffsetAndMetadata(tp.getValue().offset(), "Old offset= -1" +", Group Id="+groupId);

					}
	            	consumerGroupOffsetsMapTemp.put(tp.getKey(), oamNew);
					offsetterKafkaConsumer.commitSync();
				}
			}
			offsetterKafkaConsumer.commitSync();
			offsetterKafkaConsumer.close();

			log.info("Starting the consumer ::: Topic=" + topicName + " GroupId=" + groupId);
			return consumerGroupOffsetsMapTemp;
		}
}