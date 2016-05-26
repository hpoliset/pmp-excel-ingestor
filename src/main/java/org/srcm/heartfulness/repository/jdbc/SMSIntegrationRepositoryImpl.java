package org.srcm.heartfulness.repository.jdbc;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.repository.SMSIntegrationRepository;

@Repository
public class SMSIntegrationRepositoryImpl implements SMSIntegrationRepository {

	private final JdbcTemplate jdbcTemplate;

	private static Logger LOGGER = LoggerFactory.getLogger(ParticipantFullDetailsRepositoryImpl.class);

	@Autowired
	public SMSIntegrationRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SMSIntegrationRepository#
	 * getRegisteredParticipantsCount(java.lang.String)
	 */
	@Override
	public int getRegisteredParticipantsCount(String eventId) {
		int registeredParticipantsCount = this.jdbcTemplate
				.queryForObject(
						"SELECT count(p.id) FROM program pr INNER JOIN participant p on p.program_id=pr.program_id WHERE pr.auto_generated_event_id=?",
						new Object[] { eventId }, Integer.class);
		return registeredParticipantsCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SMSIntegrationRepository#
	 * getIntroducedParticipantsCount(java.lang.String)
	 */
	@Override
	public int getIntroducedParticipantsCount(String introId) {
		int registeredParticipantsCount = this.jdbcTemplate
				.queryForObject(
						"SELECT count(p.id) FROM program pr INNER JOIN participant p on p.program_id=pr.program_id WHERE pr.auto_generated_intro_id=? and p.welcome_card_number is not null",
						new Object[] { introId }, Integer.class);
		return registeredParticipantsCount;
	}

}
