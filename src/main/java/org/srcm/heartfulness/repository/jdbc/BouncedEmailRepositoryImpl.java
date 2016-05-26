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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.BouncedEmailRepository;

/**
 * The BouncedEmailRepositoryImpl encapsulates all data access behaviors,marks
 * the participant email as bounced if the email passed as an argument matches
 * with any of the participant email.
 * 
 * 
 * @author Koustav Dutta
 *
 */
@Repository
public class BouncedEmailRepositoryImpl implements BouncedEmailRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(BouncedEmailRepositoryImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertSubscriber;

	@Autowired
	public BouncedEmailRepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.insertSubscriber = new SimpleJdbcInsert(dataSource).withTableName("welcome_email_log")
				.usingGeneratedKeyColumns("id");
	}

	/**
	 * Updates participant record and marks is_bounced as 1 or 0. If email
	 * matches with any participant email it will update as 1 else 0.
	 * 
	 * @param email
	 *            to match with participant email.
	 */
	@Override
	public int updateEmailAsBounced(String email) {
		LOGGER.debug("START: Updating participant email " + email + " as bounced");
		int createStatus = 0;
		Map<String, String> mapParams = new HashMap<>();
		mapParams.put("bouncedEmail", "1");
		mapParams.put("email", email);
		createStatus = this.namedParameterJdbcTemplate.update("UPDATE participant SET " + "is_bounced=:bouncedEmail "
				+ "WHERE email=:email", mapParams);
		if (createStatus > 0) {
			LOGGER.debug("END: Completed updating participant email " + email + " as bounced");
		} else {
			LOGGER.debug("END: Failed to update participant email " + email + " as bounced");
		}
		return createStatus;
	}

	/**
	 * Updates unsubscribed flag as 1 and email_status as BOUNCED if subscriber
	 * email is present in the table.
	 * 
	 * @param bouncedEmail
	 *            is used to update subscriber data.
	 */
	@Override
	public int updateEmailStatusAsBounced(String bouncedEmail) {
		LOGGER.debug("START: Setting unsubscribed flag as 1 and email status as BOUNCED");
		int updateStatus = 0;
		Map<String, String> mapParams = new HashMap<>();
		mapParams.put("setUnsubscribed", "1");
		mapParams.put("emailStatus", "BOUNCED");
		mapParams.put("email", bouncedEmail);
		updateStatus = this.namedParameterJdbcTemplate
				.update("UPDATE welcome_email_log SET " + " unsubscribed=:setUnsubscribed "
						+ " ,email_status=:emailStatus " + " WHERE email=:email", mapParams);
		if (updateStatus > 0) {
			LOGGER.debug("END: Completed setting unsubscribed flag as 1 and email status as BOUNCED");
		} else {
			LOGGER.debug("END: Failed to set unsubscribed flag as 1 and email status  as BOUNCED");
		}
		return updateStatus;
	}

	/**
	 * If the subscriber email is not present it will create a new record in the
	 * table with unsubscribed as 1 and email_status as BOUNCED.
	 * 
	 * @param bouncedEmail
	 *            to create a new record.
	 */
	@Override
	public void createEmailAsBounced(String bouncedEmail) {
		LOGGER.debug("START: Creating new email record with email status as BOUNCED");

		WelcomeMailDetails wlcmMailDetails = new WelcomeMailDetails();
		wlcmMailDetails.setId(0);
		wlcmMailDetails.setEmail(bouncedEmail);
		wlcmMailDetails.setUnsubscribed(1);
		wlcmMailDetails.setEmailStatus("BOUNCED");

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(wlcmMailDetails);
		Number newId = this.insertSubscriber.executeAndReturnKey(parameterSource);
		wlcmMailDetails.setId(newId.intValue());
		LOGGER.debug("END: Completed creating new email record with email status as BOUNCED");

	}

}
