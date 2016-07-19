/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.webservice.PushNotificationController;

import org.srcm.heartfulness.helper.AmazonSNSClientWrapper;
import org.srcm.heartfulness.helper.MessageGenerator;
import org.srcm.heartfulness.helper.MessageGenerator.Platform;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;

/**
 * @author Koustav Dutta
 *
 */
@Component
public class PushNotificationHelper {

	private static final Logger log = LoggerFactory.getLogger(PushNotificationController.class);
	private AmazonSNSClientWrapper snsClientWrapper;
	
	public static final Map<Platform, Map<String, MessageAttributeValue>> attributesMap = new HashMap<Platform, Map<String, MessageAttributeValue>>();
	
	static {
		attributesMap.put(Platform.GCM, null);
		attributesMap.put(Platform.APNS, null);
		attributesMap.put(Platform.APNS_SANDBOX, null);
	}
	
	public void sendNotification(){
		AmazonSNS sns = new AmazonSNSClient(new PropertiesCredentials(
				PushNotificationHelper.class
				.getResourceAsStream("/resources/application.properties")));
		sns.setEndpoint("https://sns.us-west-2.amazonaws.com");
		log.debug("===========================================\n");
		log.debug("Getting Started with Amazon SNS");
		log.debug("===========================================\n");
		try {
			
			this.snsClientWrapper = new AmazonSNSClientWrapper(sns);
			demoAndroidAppNotification();
			demoAppleAppNotification();
			
		}catch (AmazonServiceException ase) {
			log.debug("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SNS, but was rejected with an error response for some reason.");
			log.debug("Error Message:    " + ase.getMessage());
			log.debug("HTTP Status Code: " + ase.getStatusCode());
			log.debug("AWS Error Code:   " + ase.getErrorCode());
			log.debug("Error Type:       " + ase.getErrorType());
			log.debug("Request ID:       " + ase.getRequestId());
			ase.printStackTrace();
		} catch (AmazonClientException ace) {
			log.debug("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SNS, such as not "
					+ "being able to access the network.");
			log.debug("Error Message: " + ace.getMessage());
		}
	}
	
	public void demoAndroidAppNotification() {
		
		String payload = MessageGenerator.getSampleAndroidMessage("PMP PUSH NOTIFICATION DEMO");
		String serverAPIKey = "AIzaSyA8bfS1LSTydvf3N8ws3-hJHbewj1WmnY4";
		String applicationName = "HTC-HFN-DEMO-APP";
		String registrationId = "fs7wjuBbphQ:APA91bGtn4zsI8M6A5h5obGyVLIprRoqmkOaRTpzwHnfVwI7PZbR7ztx4ckxqbVEIme9LwojqcSl7HzvwoQS7L1C2dkE5rGaksh2JFhSMnI-esdaJvSBH3REcJy11BNUleHpoiUgJKP7";
		snsClientWrapper.demoNotification(Platform.GCM, "", serverAPIKey,
				registrationId, applicationName, attributesMap,payload);
	}
	
	
	public void demoAppleAppNotification() {
		
		String payload = MessageGenerator.getSampleAppleMessage();
		String certificate ="-----BEGIN CERTIFICATE-----" + "\n"
						+	"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" + "\n"
						+	"-----END CERTIFICATE-----" + "\n"; 
		String privateKey ="-----BEGIN PRIVATE KEY-----" + "\n"
						+	"MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQC2XU0enfdxGxucEq5yERm1kikXxmP0KjctMlmEH8Odzurae7Z1yaJBqxkPQrmpdf2P/nsAUTDnbghXd2Wz3J0Ezm2BaxAM9ZM7ORaMciGpWZ/zd29IhEKqRPoJC50fP72c/o998XKsQQpJuViHhlxkn7vIUxEUwa23yPqhJtpUcU/qo+nSAF9Xfh8bOMfJb/9t6dph+NiybjCcJCVQWtE2+p9KbCq/V5eGaKYd+Vhb2LeLjlErpVmcNm8d1XMu7L01To8uosGU/z0cS7PN4Ienq1VQy5GKCrf1CPfbnpUxPpjAXzOZQkTIr9BBKKPTLZ/zf/K+o4KzPsY9yuqpyD2rAgMBAAECggEAQGdY+gwdvCMvsAHk+8HQS5CP1Kaq3Csgi2SxunihVBUkWf7A5H5OQFTVp88P1Kon33enJuiFleYFKiavk0LBbVYd1EvkQj9kPNEfhoA0JSPS2l/+vCSBirKLfzBL0CJpq403Teo8blsV8A12F367GIpU/h1f9e2klTh0Uv9usc0t/SCKrkbRoiPVww+Uxi2Oc2DUR8a+F7hqDmq34qh5A6USHONU/dnR2aKSEEcssAdw4Vk9SsTgn073EjO+ABdBc0tqYWgaPkJdVvnoaN00ZPZhxF6/zVgX08zvgrxmUcZXvVZhP+4lLbGWiQGffBRQ/2avz5M6fTLi5oJg/pbboQKBgQDiYqBwbFrej67sMbRm8/FqXhvNSDlI1PvwYCtFjKZvPYlG89ti8VTfE/xqwhBvrDmduNaajUSbuZoTgGGqxF42d+Y9UNGrm1enzg7w1MuxUdRUfIqxiM9C+VBvK4ovE7HvD7Ndp7jOyqIng4AnFID20o7kB1fZqeaMOSyPczurewKBgQDOOHhdqKhkET9M2cYf+gY5SrM16z+zoVY5vlYLzdUPRa7pYKuMeRxZ+PMQsEGPlCgFt5LGSqWAdoqLwqxx0uaNb8NJX7J+7R26H3hO5LY7cHn8V1mo/7fVKxixN8X9K2a1OAq4EKsg5KNwz7Uc+TW8MHxD4cSp59OYvM80szdHkQJ/a+KUkv8QTwAqXGVTk7ytUAOJPEsvAYZ+ig9Kj9p3dovc3/NWmdBooebkY/wX2SA6VO/BVeX8zDmnM6P652atf0ekcZQSV7WwEEgi1qKSb+6PQbQYZILOQxyNOG2HbOf14/eXgwprCCGJJUuFxxn+W6H8dq8JLrHJLQoSMkq4JQKBgCmpTufdAuf0dA1akaQ0Jr28Uag69oubtIfF5CxOURTcxxFu7jla3ldu6+wbL6OKEuvUwlo9CmKBkEvb8GquaNV6FsHkC3OJA9BuOyf1HLu1ZW+NVhfGUaVHJ53l3qHe+sjxoXi+JVRn3H0V+Uj5L9ntEa3FFq4cn6rjuhizfB+BAoGBAKwTfGwTsD9bKJxPOBSJawcZ4b2yRXqowE46gMtFWRMYhnBx3fOcCZqo8qsZsxWebe1fYWa+UoK6eGEaEfe9PajzJxbGPIFUwdwuqRd/JMKF9jHVAXuqvVx3/ykNrDve9fsBY5yT2Ru/6Qi/lCX+rV8Yh7WSb+qJ7EZXrUV9yLD4" + "\n"
						+	"-----END PRIVATE KEY-----" +  "\n"; 
		String applicationName = "HTC-HFN-DEMO-APP";
		String deviceToken ="7b698c0b122c1c03125f735643015c190e7904da9d2e8a234ae0c9d1c10ba6fe";
		snsClientWrapper.demoNotification(Platform.APNS,certificate,privateKey,deviceToken,applicationName,attributesMap,payload);
	}
}
