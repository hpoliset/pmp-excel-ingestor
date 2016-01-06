package org.srcm.heartfulness.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.repository.ParticipantRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vsonnathi on 11/23/15.
 */
@Repository
public class ParticipantRepositoryImpl implements ParticipantRepository {

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertParticipant;

	@Autowired
	public ParticipantRepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);

		this.insertParticipant = new SimpleJdbcInsert(dataSource)
		.withTableName("participant")
		.usingGeneratedKeyColumns("id");
	}

	@Override
	public Collection<Participant> findByHashCode(String hashCode) throws DataAccessException {
		return null;
	}

	@Override
	public Participant findById(int id) throws DataAccessException {
		Participant participant;
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		participant = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT * FROM participant WHERE id=:id",
				params, BeanPropertyRowMapper.newInstance(Participant.class)
				);
		return participant;
	}

	@Override
	public List<Participant> findByProgramId(int programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);

		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT * FROM participant WHERE program_id=:programId", sqlParameterSource,
				BeanPropertyRowMapper.newInstance(Participant.class)
				);

		return participants;
	}

	@Override
	public void save(Participant participant) {

		//See if this participant already exists or not
		if (participant.getId() == 0) {
			Integer participantId = this.jdbcTemplate.query(
					"SELECT id from participant where excel_sheet_sequence_number=? AND print_name=? AND program_id=?",
					new Object[]{participant.getExcelSheetSequenceNumber(), participant.getPrintName(), participant.getProgramId()},
					new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					}
					);

			if (participantId > 0) {
				participant.setId(participantId);
			}
		}

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(participant);
		if (participant.getId() == 0) {
			Number newId = this.insertParticipant.executeAndReturnKey(parameterSource);
			participant.setId(newId.intValue());
		} else {
			// TODO: Need to deal with Hashcode.
			this.namedParameterJdbcTemplate.update(
					"UPDATE participant SET " +
							"print_name=:printName, " +
							"first_name=:firstName, " +
							"last_name=:lastName, " +
							"middle_name=:middleName, " +
							"email=:email, " +
							"mobile_phone=:mobilePhone," +
							"gender=:gender," +
							"date_of_birth=:dateOfBirth," +
							"date_of_registration=:dateOfRegistration," +
							"abhyasi_id=:abhyasiId," +
							"status=:status," +
							"address_line1=:addressLine1," +
							"address_line2=:addressLine2," +
							"city=:city," +
							"state=:state," +
							"country=:country," +
							"program_id=:programId," +
							"profession=:profession," +
							"remarks=:remarks," +
							"id_card_number=:idCardNumber," +
							"language=:language," +
							"introduction_date=:introductionDate," +
							"introduced_by=:introducedBy," +
							"welcome_card_number=:welcomeCardNumber," +
							"welcome_card_date=:welcomeCardDate," +
							"age_group=:ageGroup," +
							"first_sitting_date=:firstSittingDate, " +
							"second_sitting_date=:secondSittingDate, " +
							"third_sitting_date=:thirdSittingDate, " +
							"batch=:batch " +
							"WHERE id=:id", parameterSource);
		}
	}
}
