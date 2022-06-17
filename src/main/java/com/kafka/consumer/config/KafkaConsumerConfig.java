package com.kafka.consumer.config;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Optional;

/**
 * 
 * This class handles the error by throwing specific exception.
 * Also it is used for Retry logic for specific number of attempts
 * and Recovery logic
 * 
 */

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {
	
	@Value("${spring.kafka.consumer.retry}")
	private int retryCount;
	
	public static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Bean
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
    
    	ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    	configurer.configure(factory, kafkaConsumerFactory);
    	
    	RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setBackOffPolicy(new ExponentialRandomBackOffPolicy());
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(retryCount));
        factory.setRetryTemplate(retryTemplate);

        factory.setRecoveryCallback((context -> {

        	ConsumerRecord<?, ?> consumerRecord = (ConsumerRecord<?, ?>) context.getAttribute("record");
        	log.error("Retries exhausted for Topic: {}, Partition: {}, Offset: {}, Message: {}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.value());
	        
        	if(context.getLastThrowable().getCause() instanceof RecoverableDataAccessException){
	        	/* 
	        	 * here you can do your recovery mechanism 
	        	 * where you can put back on to the topic using a Kafka producer
	        	 */
	        } else{
	        	/* 
	        	 * here you can log things and throw some custom exception 
	        	 * that Error handler will take care of ..
	        	 */
	        	log.error(context.getLastThrowable().getMessage());
	        	throw new RuntimeException(context.getLastThrowable().getMessage());
	        }
	        return Optional.empty();

        }));

        factory.setErrorHandler(new SeekToCurrentErrorHandler());

        return factory;
    }

}
