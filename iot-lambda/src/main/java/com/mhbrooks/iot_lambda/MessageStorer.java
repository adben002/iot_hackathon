package com.mhbrooks.iot_lambda;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class MessageStorer implements Runnable {

	AmazonDynamoDBClient client;

	private final static String TABLE_NAME = "mhbrooks-hackathon";
	private static LambdaLogger logger;
	private final DataEntry data;

	public MessageStorer(Context context, DataEntry data) {
		client = new AmazonDynamoDBClient();
		client.setRegion(Region.getRegion(Regions.EU_WEST_1));
		logger = context.getLogger();
		this.data = data;
	}

	public void storeMessage() {
	}

	@Override
	public void run() {
		logger.log("Attempting to store message: " + data);
		client.putItem(TABLE_NAME, data.getAsAttributes());
	}

}
