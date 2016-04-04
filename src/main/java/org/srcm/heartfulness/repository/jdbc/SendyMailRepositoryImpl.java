package org.srcm.heartfulness.repository.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.SendyMailRepository;


@Repository
public class SendyMailRepositoryImpl implements SendyMailRepository {

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertSubscriber;

	@Autowired
	public SendyMailRepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);

		this.insertSubscriber = new SimpleJdbcInsert(dataSource).withTableName("welcome_mail_subscribers")
				.usingGeneratedKeyColumns("id");
	}

	
	@Override
	public List<Participant> getParticipantsToSendWelcomeMail() {
		/*Map<String, Object> params = new HashMap<>();
		params.put("createDate", date);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);*/

		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT print_name,email,language FROM participant WHERE create_time < CURDATE() AND welcome_mail_sent=0",
				BeanPropertyRowMapper.newInstance(Participant.class));

		return participants;
	}
	
	@Override
	public int getIntroducedParticipantCount(String printName, String email) {
		int introducedParticipantsCount = this.jdbcTemplate.queryForObject(
				"SELECT count(id) FROM welcome_mail_subscribers WHERE email=? && print_name=?",new Object[] {email,printName},Integer.class);
		return introducedParticipantsCount;
	}


	@Override
	public void save(WelcomeMailDetails welcomeMailDetails) {
		System.out.println(welcomeMailDetails);
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(welcomeMailDetails);
		if (welcomeMailDetails.getId() == 0) {
			Number newId = this.insertSubscriber.executeAndReturnKey(parameterSource);
			welcomeMailDetails.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE welcome_mail_subscribers SET " + 
				"print_name=:printName, " + 
				"email=:mail, " +
				"email_sent_time=:createTime ", parameterSource);
		}
	}

	@Override
	public List<WelcomeMailDetails> getSubscribersToUnsubscribe() {
		List<WelcomeMailDetails> subscribers = this.namedParameterJdbcTemplate.query(
				"SELECT email FROM welcome_mail_subscribers WHERE email_sent_time >= CURDATE()",
				BeanPropertyRowMapper.newInstance(WelcomeMailDetails.class));

		return subscribers;
	}

	@Override
	public void updateParticipant() {
		this.jdbcTemplate.update("UPDATE participant set welcome_mail_sent=1 WHERE create_time < CURDATE()-1");
		
	}
}
