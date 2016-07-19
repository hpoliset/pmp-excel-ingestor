package org.srcm.heartfulness.helper;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.srcm.heartfulness.helper.MessageGenerator;
import org.srcm.heartfulness.helper.MessageGenerator.Platform;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;


//import net.mikasa.snsclient.MessageGenerator.Platform;

public class AmazonSNSClientWrapper {

	private Logger log = LoggerFactory.getLogger(AmazonSNSClientWrapper.class.getName());
	private final AmazonSNS snsClient;

	public AmazonSNSClientWrapper(AmazonSNS client) {
		this.snsClient = client;
	}

	private CreatePlatformApplicationResult createPlatformApplication(String applicationName, Platform platform, String principal,String credential) {
		CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("PlatformPrincipal", principal);
		attributes.put("PlatformCredential", credential);
		platformApplicationRequest.setAttributes(attributes);
		platformApplicationRequest.setName(applicationName);
		platformApplicationRequest.setPlatform(platform.name());
		return snsClient.createPlatformApplication(platformApplicationRequest);
	}

	private CreatePlatformEndpointResult createPlatformEndpoint(Platform platform, String customData, String platformToken,	String applicationArn) {
		CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
		platformEndpointRequest.setCustomUserData(customData);
		String token = platformToken;
		//String userId = null;
		platformEndpointRequest.setToken(token);
		platformEndpointRequest.setPlatformApplicationArn(applicationArn);
		return snsClient.createPlatformEndpoint(platformEndpointRequest);
	}

	private void deletePlatformApplication(String applicationArn) {
		DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
		request.setPlatformApplicationArn(applicationArn);
		snsClient.deletePlatformApplication(request);
	}

	private PublishResult publish(String endpointArn, Platform platform, Map<Platform, Map<String, MessageAttributeValue>> attributesMap,String payload) {
		PublishRequest publishRequest = new PublishRequest();
		Map<String, MessageAttributeValue> notificationAttributes = getValidNotificationAttributes(attributesMap.get(platform));

		if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
			publishRequest.setMessageAttributes(notificationAttributes);
		}
		publishRequest.setMessageStructure("json");
		// If the message attributes are not set in the requisite method, notification is sent with default attributes
		String message; //= getPlatformSampleMessage(platform, payload);
		Map<String, String> messageMap = new HashMap<String, String>();
		messageMap.put(platform.name(), payload);
		message = MessageGenerator.jsonify(messageMap);
		// For direct publish to mobile end points, topicArn is not relevant.
		publishRequest.setTargetArn(endpointArn);

		// Display the message that will be sent to the endpoint/
		log.info("{Message Body: " + message + "}");
		StringBuilder builder = new StringBuilder();
		builder.append("{Message Attributes: ");
		for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes.entrySet()) {
			builder.append("(\"" + entry.getKey() + "\": \"" + entry.getValue().getStringValue() + "\"),");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("}");
		log.info(builder.toString());

		publishRequest.setMessage(message);
		return snsClient.publish(publishRequest);
	}

	public void demoNotification(Platform platform, String principal,String credential,String platformToken,String applicationName,Map<Platform, Map<String, MessageAttributeValue>> attrsMap, String payload) {

		// Create Platform Application. This corresponds to an app on a platform.
		CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(applicationName, platform, principal, credential);
		System.out.println("Platform application Result:"+platformApplicationResult.toString());
		log.info(platformApplicationResult.toString());

		// The Platform Application Arn can be used to uniquely identify the Platform Application.
		String platformApplicationArn = platformApplicationResult.getPlatformApplicationArn();

		// Create an Endpoint. This corresponds to an app on a device.
		CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(platform, "CustomData - Useful to store endpoint specific data",platformToken, platformApplicationArn);
		System.out.println("Platform Endpoint Response:"+platformEndpointResult);
		log.info(platformEndpointResult.toString());
		
		// Publish a push notification to an Endpoint.
		PublishResult publishResult = publish(platformEndpointResult.getEndpointArn(), platform, attrsMap, payload);
		System.out.println("Published! \n{MessageId:" + publishResult.getMessageId());
		log.info("Published! \n{MessageId=" + publishResult.getMessageId() + "}");
		// Delete the Platform Application since we will no longer be using it.
		deletePlatformApplication(platformApplicationArn);
	}

	private String getPlatformSampleMessage(Platform platform, String payload) {
		switch (platform) {
		case APNS:
			return MessageGenerator.getSampleAppleMessage();
		case APNS_SANDBOX:
			return MessageGenerator.getSampleAppleMessage();
		case GCM:
			return MessageGenerator.getSampleAndroidMessage(payload);
		default:
			throw new IllegalArgumentException("Platform not supported : " + platform.name());
		}
	}

	public static Map<String, MessageAttributeValue> getValidNotificationAttributes(Map<String, MessageAttributeValue> notificationAttributes) {
		Map<String, MessageAttributeValue> validAttributes = new HashMap<String, MessageAttributeValue>();

		if (notificationAttributes == null)
			return validAttributes;

		for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes.entrySet()) {
			if (!StringUtils.isBlank(entry.getValue().getStringValue())) {
				validAttributes.put(entry.getKey(), entry.getValue());
			}
		}
		return validAttributes;
	}
}