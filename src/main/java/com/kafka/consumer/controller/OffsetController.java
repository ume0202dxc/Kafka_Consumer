package com.kafka.consumer.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kafka.consumer.form.OffsetInfoForm;
import com.kafka.util.KafkaConsumerUtil;

/**
 * 
 * This Controller class is for consumer offset HTTP API requests
 * 
 */

@Controller
public class OffsetController {
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootStrapServers;
	
	@Value("${spring.kafka.consumer.isolation.level}")
	private String isolationLevel;
	
	@Value("${spring.kafka.consumer.properties.max.poll.records}")
	private String maxPollRecords;
	
	@Value("${spring.kafka.consumer.auto-offset-reset}")
	private String autoOffsetReset;
	
	@Value("${spring.kafka.consumer.enable-auto-commit}")
	private String enableAutoCommit;
	
	public static final Logger log = LoggerFactory.getLogger(OffsetController.class);
	
	/*
	 * This method is used to populate UI for set offset
	 * 
	 */
	@GetMapping("/offset")
	public String index(@ModelAttribute("model") ModelMap model) {
		return "index";
	}
	
	/*
	 * The method setOffsetByDateTime is used for setting the offset for specific date and time, 
	 * topic and group id
	 * 
	 */
	 @PostMapping(value = {"/setOffsetByDateTime"})
	 public @ResponseBody String setOffsetByDateTime(@RequestBody OffsetInfoForm offsets) {
	        
        KafkaConsumerUtil kafkaConsumerUtil;
		try {
			DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("M/d/yy, h:mm a").toFormatter(Locale.US);
        	LocalDateTime dateTime = LocalDateTime.parse(offsets.getOffsetDate(), formatter);
			kafkaConsumerUtil = new KafkaConsumerUtil();
			
			final var properties = new Properties();
			properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
		    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
		    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		    properties.put(ConsumerConfig.GROUP_ID_CONFIG, offsets.getGroupId());
		    properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
		    
		    properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
		    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
		    
			kafkaConsumerUtil.setOffsetByDateTime(properties, offsets.getGroupId(), offsets.getTopicName(), dateTime);
		} catch (Exception e) {
			log.error(e.getMessage());
			return "Set Offset By Date Time Failed..";
		}
		return "Set Offset By Date Time Success..";
	 }
}
