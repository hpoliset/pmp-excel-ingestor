package org.srcm.heartfulness.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.amazonaws.ClientConfiguration;

/**
 * Helper Class - To Set Proxy (Development use)
 * 
 * @author himasreev
 *
 */
@Component
@PropertySource("classpath:application.properties")
public class ProxyHelper {

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

	/**
	 * Method to set proxy configuration to the AWS Client Configuration.
	 * 
	 * @param cfg
	 * @return
	 */
	public ClientConfiguration setProxyToAWSS3() {
		ClientConfiguration cfg = null;
		if (proxy) {
			cfg = new ClientConfiguration();
			cfg.setProxyHost(proxyHost);
			cfg.setProxyPassword(proxyPassword);
			cfg.setProxyUsername(proxyUser);
			cfg.setProxyPort(proxyPort);
			return cfg;
		} else {
			return null;
		}
	}

	/**
	 * Method to set the proxy (development use only)
	 */
	public void setProxy() {
		if (proxy) {
			/*
			 * CredentialsProvider credsProvider = new
			 * BasicCredentialsProvider(); credsProvider.setCredentials(new
			 * AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new
			 * UsernamePasswordCredentials(proxyUser, proxyPassword));
			 * HttpClientBuilder clientBuilder = HttpClientBuilder.create();
			 * clientBuilder.useSystemProperties(); clientBuilder.setProxy(new
			 * HttpHost(proxyHost, proxyPort));
			 * clientBuilder.setDefaultCredentialsProvider(credsProvider);
			 * clientBuilder.setProxyAuthenticationStrategy(new
			 * ProxyAuthenticationStrategy()); CloseableHttpClient client =
			 * clientBuilder.build(); HttpComponentsClientHttpRequestFactory
			 * factory = new HttpComponentsClientHttpRequestFactory();
			 * factory.setHttpClient(client); this.setRequestFactory(factory);
			 */
		}

	}

}
