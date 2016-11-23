/**
 * 
 */
package org.srcm.heartfulness.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * @author Koustav Dutta
 *
 */
@RestController
@RequestMapping("/api")
public class CivicrmSubscriberAPITest extends RestTemplate{

	private static final Logger LOGGER = LoggerFactory.getLogger(CivicrmSubscriberAPITest.class);

	@RequestMapping(value = "/subscribe", method = RequestMethod.GET)
	public ResponseEntity<?> subscribeViaCivicrm(){
		//setProxy();
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();


		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = null;
		try{
			//new url
			//response = this.exchange("http://civicrm.heartfulness.org/sites/all/modules/civicrm/extern/rest.php?entity=Contact&action=get&api_key=RyY1iYQbZjQzr1XnIzkJin4M&key=6c1dd71ec23afe5cb585752fa8122940&json=1&debug=1&version=3&entity=Contact&action=get&email=balachandiran777@gmail.com", HttpMethod.GET, httpEntity,String.class);

			//original civicrm api(check contact exists or not)
			response = this.exchange("http://civicrm.heartfulness.org/sites/all/modules/civicrm/extern/rest.php?entity=Contact&action=get&api_key=RyY1iYQbZjQzr1XnIzkJin4M&key=6c1dd71ec23afe5cb585752fa8122940&json=1&entity=Contact&action=get&email=balachandiran777@gmail.com", HttpMethod.GET, httpEntity,String.class);
			LOGGER.info("Success response==",response);
		}catch(Exception ex){
			LOGGER.error("Error response==",ex);
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(ex),HttpStatus.OK);
		}
		return response;
	}


	/*private void setProxy() {

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials("koustavd", "123Welcome"));
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.useSystemProperties();
		clientBuilder.setProxy(new HttpHost("10.1.28.12", 8080));
		clientBuilder.setDefaultCredentialsProvider(credsProvider);
		clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
		CloseableHttpClient client = clientBuilder.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		this.setRequestFactory(factory);

	}*/

}
