/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.repository.BouncedEmailRepository;

/**
 * The BouncedEmailRepositoryImpl encapsulates all data access
 * behaviors,marks the participant email as bounced if the email
 * passed as an argument matches with any of the participant email.
 * 
 * 
 * @author Koustav Dutta
 *
 */
@Repository
public class BouncedEmailRepositoryImpl implements BouncedEmailRepository {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BouncedEmailRepositoryImpl.class);
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	@Autowired
	public BouncedEmailRepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * Updates participant record and marks is_bounced as 1 or 0.
	 * If email matches with any participant email it will update as 1 
	 * else 0.
	 * @param email to match with participant email.
	 */
	@Override
	public int updateBouncedEmails(String email){
		LOGGER.debug("START: Updating participant email "+email + " as bounced");
		int createStatus = 0;
		Map<String,String> mapParams = new HashMap<>();
		mapParams.put("bouncedEmail","1");
		mapParams.put("email",email);
		createStatus = this.namedParameterJdbcTemplate.update("UPDATE participant SET "
				+ "is_bounced=:bouncedEmail "
				+ "WHERE email=:email",mapParams );
		if(createStatus > 0 ){
			LOGGER.debug("END: Completed updating participant email "+email + " as bounced");
		}else{
			LOGGER.debug("END: Failed to update participant email "+email + " as bounced");
		}
		return createStatus;  
	}
	
}
