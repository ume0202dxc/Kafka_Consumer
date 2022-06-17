package com.kafka.consumer.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/*
 * Offset Information Form Bean
 */

public class OffsetInfoForm {
	
	public OffsetInfoForm() {

	}
	
	public OffsetInfoForm(String topicName, String groupId, String offsetDate) {
		super();
		this.topicName = topicName;
		this.groupId = groupId;
		this.offsetDate = offsetDate;
	}
	
	@NotNull
	@NotBlank
	private String topicName;

	@NotNull
	@NotBlank
	private String groupId;
	
	@NotNull
	@NotBlank
	private String offsetDate;
	
	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getOffsetDate() {
		return offsetDate;
	}

	public void setOffsetDate(String offsetDate) {
		this.offsetDate = offsetDate;
	}

}
