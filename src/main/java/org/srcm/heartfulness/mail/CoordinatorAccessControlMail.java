/**
 * 
 */
package org.srcm.heartfulness.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Koustav Dutta
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.cac.mail.properties", ignoreUnknownFields = false, prefix = "mail.cac")
public class CoordinatorAccessControlMail {
	
	private String username;
	private String password;
	private String hostname;
	private String port;
	
	
}
