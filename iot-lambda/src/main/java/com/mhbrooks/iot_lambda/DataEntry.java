package com.mhbrooks.iot_lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DataEntry {

	public final static String MESSAGE_ID = "MessageId";
	public final static String TOPICARN = "TopicArn";
	public final static String SUBJECT = "Subject";
	public final static String MESSAGE = "Message";
	public final static String TIMESTAMP = "Timestamp";
	

	private final String messageId;
	private final String topicArn;
	private final String subject;
	private final String message;
	private final String timestamp;
	private static final String SENSOR_TYPE = "sensorType";
	private static final String DEVICE_ID = "deviceId";
	
	private final Map<String, String> messageAttributes;

	public DataEntry(String messageId, String topicArn, String subject, String message, String timestamp,
			Map<String, String> messageAttributes) {
		this.messageId = messageId;
		this.topicArn = topicArn;
		this.subject = subject;
		this.message = message;
		this.timestamp = timestamp;
		this.messageAttributes = messageAttributes;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getTopicArn() {
		return topicArn;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public Map<String, String> getMessageAttributes() {
		return messageAttributes;
	}

	@Override
	public String toString() {
		return "DataEntry [messageId=" + messageId + ", topicArn=" + topicArn + ", subject=" + subject + ", message="
				+ message + ", timestamp=" + timestamp + "]";
	}

	public Map<String, AttributeValue> getAsAttributes() {
		final Map<String, AttributeValue> attributes = new HashMap<>();

		attributes.put(MESSAGE_ID, new AttributeValue(getMessageId()));
		attributes.put(TOPICARN, new AttributeValue(getTopicArn()));
		attributes.put(SUBJECT, new AttributeValue(getSubject()));
		attributes.put(MESSAGE, new AttributeValue(getMessage()));
		attributes.put(TIMESTAMP, new AttributeValue(getTimestamp()));
		for (final String key : messageAttributes.keySet()) {
			attributes.put(key, new AttributeValue(messageAttributes.get(key)));
		}

		return attributes;
	}
	
	public String getDevice() {
		String deviceId = "";
		for (final String key : this.getMessageAttributes().keySet()) {
			if (DEVICE_ID.equals(key)) {
				deviceId = this.getMessageAttributes().get(key);
			}
		}
		return deviceId;
	}
	
	public String getSensor() {
		String sensorId = "";
		for (final String key : this.getMessageAttributes().keySet()) {
			if (SENSOR_TYPE.equals(key)) {
				sensorId = this.getMessageAttributes().get(key);
			}
		}
		return sensorId;
	}

}
