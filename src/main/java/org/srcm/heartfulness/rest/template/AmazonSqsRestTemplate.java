package org.srcm.heartfulness.rest.template;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.proxy.ProxyHelper;
import org.srcm.heartfulness.repository.jdbc.ProgramRepositoryImpl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

/**
 * Template class to communicate with AWS SQS and send and receive messages
 * to/from AWS SQS.
 * 
 * @author Koustav Dutta
 *
 */
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(locations = "classpath:dev.aws.sqs.properties", ignoreUnknownFields = true, prefix = "aws.sqs")
public class AmazonSqsRestTemplate extends RestTemplate {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AmazonSqsRestTemplate.class);

	@Autowired
	ProxyHelper proxyHelper;

	private String accesskeyid;

	private String secretkey;

	private String region;

	private String queuename;
	
	@Value("${proxy}")
	private boolean proxy;

	@Value("${proxyHost}")
	private String proxyHost;

	@Value("${proxyPort}")
	private int proxyPort;

	@Value("${proxyUser}")
	private String proxyUser;

	@Value("${proxyPassword}")
	private String proxyPassword;
	
	/*private BasicAWSCredentials credentials;*/
	
    private AmazonSQS sqs;
    
    /*private String simpleQueue;*/

	public String getAccesskeyid() {
		return accesskeyid;
	}

	public void setAccesskeyid(String accesskeyid) {
		this.accesskeyid = accesskeyid;
	}

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getQueuename() {
		return queuename;
	}

	public void setQueuename(String queuename) {
		this.queuename = queuename;
	}
	
	/**
     * Creates a queue in your region and returns the url of the queue
     * @param queueName
     * @return
     */
    /*public String createQueue(String queueName){
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        String queueUrl = this.sqs.createQueue(createQueueRequest).getQueueUrl();
        return queueUrl;
    }*/

    /**
     * returns the queueurl for for sqs queue if you pass in a name
     * @param queueName
     * @return
     */
    public String getQueueUrl(String queueName){
        GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
        return this.sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
    }

    /**
     * lists all your queue.
     * @return
     */
    /*public ListQueuesResult listQueues(){
       return this.sqs.listQueues();
    }*/

    /**
     * send a single message to your sqs queue
     * @param queueUrl
     * @param message
     */
    public void sendMessageToQueue(String queueUrl, String message){
        SendMessageResult messageResult =  this.sqs.sendMessage(new SendMessageRequest(queueUrl, message));
        LOGGER.info("Message result - {}",messageResult.toString());
    }

    /**
     * gets messages from your queue
     * @param queueUrl
     * @return
     */
    public List<Message> getMessagesFromQueue(String queueUrl){
       ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
       List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
       return messages;
    }

    /**
     * deletes a single message from your queue.
     * @param queueUrl
     * @param message
     */
    public void deleteMessageFromQueue(String queueUrl, Message message){
        String messageRecieptHandle = message.getReceiptHandle();
        LOGGER.info("Message deleted : " + message.getBody() + "." + message.getReceiptHandle());
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));
    }

	/**
	 * Method to set the proxy (development use only)
	 */
	/*public void setProxy() {
		if (proxy) {
			this.credentials = new   BasicAWSCredentials(getAccesskeyid(), getSecretkey());
			this.simpleQueue = getQueuename();
            
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setProtocol(Protocol.HTTP);
            clientConfig.setProxyHost(proxyHost);
            clientConfig.setProxyPort(proxyPort);
            clientConfig.setProxyUsername(proxyUser);
            clientConfig.setProxyPassword(proxyPassword);
            
            AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard();
            builder.setRegion(getRegion());
            builder.setClientConfiguration(clientConfig);
            this.sqs = builder.withCredentials(new AWSStaticCredentialsProvider(this.credentials)).build();
		}
	}*/

}
