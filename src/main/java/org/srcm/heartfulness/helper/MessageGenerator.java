package org.srcm.heartfulness.helper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class MessageGenerator {

	/*
	 * This message is delivered if a platform specific message is not specified
	 * for the end point. It must be set. It is received by the device as the
	 * value of the key "default".
	 */
	public static final String defaultMessage = "This is the default message";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static enum Platform {
		// Apple Push Notification Service
		APNS,
		// Sandbox version of Apple Push Notification Service
		APNS_SANDBOX,
		// Google Cloud Messaging
		GCM;
	}

	public static String jsonify(Object message) {
		try {
			return objectMapper.writeValueAsString(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;
		}
	}

	private static Map<String, String> getData(String message) {
		Map<String, String> payload = new LinkedHashMap<String, String>();
		payload.put("message", message);
		payload.put("created_at", String.valueOf(Calendar.getInstance().getTimeInMillis()));
		payload.put("title", "PMP");
		return payload;
	}

	public static String getSampleAppleMessage() {
		Map<String, Object> appleMessageMap = new HashMap<String, Object>();
		Map<String, Object> appMessageMap = new HashMap<String, Object>();
		appMessageMap.put("alert", "PMP PUSH NOTIFICATION DEMO");
		appMessageMap.put("badge", 9);
		appMessageMap.put("sound", "default");
		appleMessageMap.put("aps", appMessageMap);
		return jsonify(appleMessageMap);
	}

	public static String getSampleAndroidMessage(String message) {
		Map<String, Object> androidMessageMap = new HashMap<String, Object>();
		// androidMessageMap.put("collapse_key", "Welcome");
		androidMessageMap.put("data", getData(message));
		// androidMessageMap.put("delay_while_idle", true);
		// androidMessageMap.put("time_to_live", 125);
		// androidMessageMap.put("dry_run", false);
		return jsonify(androidMessageMap);
	}

}