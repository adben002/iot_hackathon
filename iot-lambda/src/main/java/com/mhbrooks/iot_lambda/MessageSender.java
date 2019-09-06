package com.mhbrooks.iot_lambda;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sns.AmazonSNSClient;

/**
 * Class to handle sending of SNS message.
 *
 * @author Jonathan.Lyne
 */
public abstract class MessageSender implements Runnable {

	protected final AmazonSNSClient client;
	protected final DataEntry data;

	protected static final String TOPIC = "arn:aws:sns:eu-west-1:820731746647:pi-data-send";
	protected static LambdaLogger logger;

	public MessageSender(Context context, DataEntry data) {
		client = new AmazonSNSClient();
		client.setRegion(Region.getRegion(Regions.EU_WEST_1));
		logger = context.getLogger();
		this.data = data;
	}

	protected abstract String formattedMessage();

	@Override
	public abstract void run();

}
