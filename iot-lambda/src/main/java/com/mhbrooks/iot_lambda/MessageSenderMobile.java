package com.mhbrooks.iot_lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

/**
 * Class to handle sending of SNS message.
 *
 * @author Jonathan.Lyne
 */
public class MessageSenderMobile implements Runnable {

	private final AmazonSNSClient client;
	private final DataEntry data;

	private static final String TOPIC = "arn:aws:sns:eu-west-1:820731746647:pi-data-send-mobile";
	private static LambdaLogger logger;

	public MessageSenderMobile(Context context, DataEntry data) {
		client = new AmazonSNSClient();
		// client.setEndpoint(
		// "arn:aws:sns:eu-west-1:820731746647:endpoint/GCM/HelloWorldAndroidApp/3fe58902-26f5-37e0-a0fe-c80ac38da3f9");
		client.setRegion(Region.getRegion(Regions.EU_WEST_1));
		logger = context.getLogger();
		this.data = data;
	}

	@Override
	public void run() {
		logger.log("Attempting to send message: " + data);

		final String message = "{\n\"default\": \"HITHERE\",\n\"GCM\": \"{ \\\"data\\\": \n{ \\\"message\\\": \\\"HITHERE\\\"} }\"\n}";
		final JSONObject j = new JSONObject();
		final Map<String, String> m = new HashMap<>();
		// final Map<String, Object> n = new HashMap<>();
		m.put("data", "testy");
		// n.put("data", m);
		try {
			j.put("GCM", "{ \"data\": { \"message\": \"Hi\" } }");
			j.put("default", "Hi");
		} catch (final JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final PublishRequest r = new PublishRequest();
		r.setMessageStructure("json");
		r.setMessage(j.toString());
		// r.setTopicArn(TOPIC);
		r.setTargetArn(
				"arn:aws:sns:eu-west-1:820731746647:endpoint/GCM/HelloWorldAndroidApp/3fe58902-26f5-37e0-a0fe-c80ac38da3f9");
		logger.log("JSONOBJECT: " + j.toString());
		final PublishResult pr = client.publish(r);

		logger.log("pubres " + pr);
	}

}
