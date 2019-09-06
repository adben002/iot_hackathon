package com.mhbrooks.iot_lambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Hello world!
 *
 */
public class App {

	private final Map<String, String> flatStructure = new HashMap<>();

	private static LambdaLogger logger;

	public static void main(String[] args) {
		System.out.println("Hello World!");
	}

	public String myHandler(Object object, Context context) throws InterruptedException {

		// Configure logging
		PropertyConfigurator.configure("logging.properties");
		final Long start = System.currentTimeMillis();
		logger = context.getLogger();
		logger.log("original message: " + object);
		logger.log("#######################################");
		addOrParse(flatStructure, "root", object);

		final DataEntry messageData = createEntry(flatStructure);
		logger.log(messageData.toString());

		final List<Thread> threads = new ArrayList<>();
		threads.add(new Thread(new MessageStorer(context, messageData)));

		if (DeviceTypes.FRONT_DOOR.equals(messageData.getDevice())) {
			threads.add(new Thread(new MessageSenderDoor(context, messageData)));
		}
		if (DeviceTypes.TEMPERATURE.equals(messageData.getDevice())) {
			threads.add(new Thread(new MessageSenderTemperature(context, messageData)));
		}
		//threads.add(new Thread(new MessageSenderMobile(context, messageData)));

		for (final Thread t : threads) {
			t.start();
		}

		while (true) {
			if (threads.isEmpty()) {
				break;
			}
			for (final Thread t : threads) {
				if (!t.isAlive()) {
					threads.remove(t);
					break;
				}
			}
			Thread.sleep(50);
		}

		logger.log("total runtime: " + (System.currentTimeMillis() - start));
		return String.valueOf("0");
	}

	private static DataEntry createEntry(Map<String, String> flatStructure) {
		final Map<String, String> attributes = new HashMap<String, String>();
		for (final String key : flatStructure.keySet()) {
			if (key.contains("attr_")) {
				logger.log("adding attr: " + key);
				attributes.put(key.split("attr_")[1], flatStructure.get(key));
			}
		}
		final DataEntry entry = new DataEntry(flatStructure.get(DataEntry.MESSAGE_ID),
				flatStructure.get(DataEntry.TOPICARN), flatStructure.get(DataEntry.SUBJECT),
				flatStructure.get(DataEntry.MESSAGE), flatStructure.get(DataEntry.TIMESTAMP), attributes);

		return entry;
	}

	private static void addOrParse(Map<String, String> flatStructure, String key, Object o) {
		if (o instanceof String) {
			logger.log("Putting key: " + key + " with value: " + (String) o);
			flatStructure.put(key, (String) o);
		} else if (o instanceof List) {
			for (final Object oe : (List) o) {
				addOrParse(flatStructure, key, oe);
			}
		} else if (o instanceof Map) {
			for (final Object k : ((Map) o).keySet()) {
				if (((String) k).contains("attr")) {
					addOrParseAttributes(flatStructure, (String) k, ((Map) o).get(k));
				} else {
					addOrParse(flatStructure, (String) k, ((Map) o).get(k));
				}
			}
		}
	}

	private static void addOrParseAttributes(Map<String, String> flatStructure, String key, Object o) {
		logger.log("Putting key: " + key + " with value: " + ((String) ((Map) o).get("Value")));
		flatStructure.put(key, (String) ((Map) o).get("Value"));
	}
}
