package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.util.SmsUtil;

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

		this.insertParticipant = new SimpleJdbcInsert(dataSource).withTableName("participant")
				.usingGeneratedKeyColumns("id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ParticipantRepository#findByHashCode
	 * (java.lang.String)
	 */
	@Override
	public Collection<Participant> findByHashCode(String hashCode) throws DataAccessException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ParticipantRepository#findById(java.
	 * lang.Integer)
	 */
	@Override
	public Participant findById(int id) throws DataAccessException {
		try {
			Participant participant;
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			participant = this.namedParameterJdbcTemplate.queryForObject("SELECT * FROM participant WHERE id=:id",
					params, BeanPropertyRowMapper.newInstance(Participant.class));
			return participant;
		} catch (EmptyResultDataAccessException ex) {
			return new Participant();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ParticipantRepository#findByProgramId
	 * (java.lang.Integer)
	 */
	@Override
	public List<Participant> findByProgramId(int programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);

		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT * FROM participant WHERE program_id=:programId", sqlParameterSource,
				BeanPropertyRowMapper.newInstance(Participant.class));

		return participants;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ParticipantRepository#save(Participant
	 * participant)
	 */
	@Override
	public void save(Participant participant) {

		// See if this participant already exists or not
		if (participant.getId() == 0) {
			Integer participantId = this.jdbcTemplate.query(
					"SELECT id from participant where excel_sheet_sequence_number=? AND print_name=? AND program_id=?",
					new Object[] { participant.getExcelSheetSequenceNumber(), participant.getPrintName(),
							participant.getProgramId() }, new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});

			if (participantId > 0) {
				participant.setId(participantId);
				String seqId = this.jdbcTemplate.query("SELECT seqId from participant where id=?",
						new Object[] { participantId }, new ResultSetExtractor<String>() {
							@Override
							public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
								if (resultSet.next()) {
									return resultSet.getString(1);
								}
								return "";
							}
						});
				if (null != seqId && !seqId.isEmpty()) {
					participant.setSeqId(seqId);
					Map<String, Object> params = new HashMap<>();
					params.put("participantId", participantId);
					Participant oldParticipantDetails=this.namedParameterJdbcTemplate.queryForObject(
							"SELECT * FROM participant WHERE id=:participantId", params,
							BeanPropertyRowMapper.newInstance(Participant.class));
					if(null != oldParticipantDetails){
						if(null != oldParticipantDetails.getWelcomeCardNumber() && !oldParticipantDetails.getWelcomeCardNumber().isEmpty()){
							participant.setWelcomeCardNumber(oldParticipantDetails.getWelcomeCardNumber());
							participant.setEwelcomeIdState(oldParticipantDetails.getEwelcomeIdState());
							participant.setIntroduced(oldParticipantDetails.getIntroduced());
							participant.setIntroducedBy(oldParticipantDetails.getIntroducedBy());
							participant.setIsEwelcomeIdInformed(oldParticipantDetails.getIsEwelcomeIdInformed());
							participant.setWelcomeCardDate(oldParticipantDetails.getWelcomeCardDate());
							participant.setIntroductionDate(oldParticipantDetails.getIntroductionDate());
						}
					}
				} else {
					participant.setSeqId(SmsUtil.generateFourDigitPIN());
				}
			} else {
				participant.setSeqId(SmsUtil.generateFourDigitPIN());
			}
		}

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(participant);
		if (participant.getId() == 0) {
			Number newId = this.insertParticipant.executeAndReturnKey(parameterSource);
			participant.setId(newId.intValue());
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE participant SET " + "print_name=:printName, "
					+ "first_name=:firstName, " + "last_name=:lastName, " + "middle_name=:middleName, "
					+ "email=:email, " + "mobile_phone=:mobilePhone," + "gender=:gender,"
					+ "date_of_birth=:dateOfBirth," + "date_of_registration=:dateOfRegistration,"
					+ "abhyasi_id=:abhyasiId," + "status=:status," + "address_line1=:addressLine1,"
					+ "address_line2=:addressLine2," + "city=:city," + "state=:state," + "country=:country,"
					+ "program_id=:programId," + "profession=:profession," + "remarks=:remarks,"
					+ "id_card_number=:idCardNumber," + "language=:language," + "introduction_date=:introductionDate,"
					+ "introduced_by=:introducedBy," + "welcome_card_number=:welcomeCardNumber,"
					+ "welcome_card_date=:welcomeCardDate," + "age_group=:ageGroup," + "upload_status=:uploadStatus,"
					+ "first_sitting=:firstSitting," + "second_sitting=:secondSitting,"
					+ "third_sitting=:thirdSitting," + "first_sitting_date=:firstSittingDate, "
					+ "second_sitting_date=:secondSittingDate, " + "third_sitting_date=:thirdSittingDate, "
					+ "is_ewelcome_id_informed=:isEwelcomeIdInformed, " + "batch=:batch, " + "introduced=:introduced, "
					+ "seqId=:seqId, " + "ewelcome_id_state=:ewelcomeIdState, " + "ewelcome_id_remarks=:ewelcomeIdRemarks " + "WHERE id=:id", parameterSource);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.ParticipantRepository#
	 * getParticipantByIntroIdAndMobileNo(java.lang.String,java.lang.String)
	 */
	@Override
	public Participant getParticipantByIntroIdAndMobileNo(String introId, String seqNumber) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("auto_generated_intro_id", introId);
			params.put("seqId", seqNumber);
			SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);

			Participant participant = null;

			Program program = null;
			List<Participant> participants = this.namedParameterJdbcTemplate.query(
					"SELECT * FROM participant p INNER JOIN program pr on p.program_id=pr.program_id "
							+ " WHERE pr.auto_generated_intro_id =:auto_generated_intro_id and p.seqId =:seqId",
					sqlParameterSource, BeanPropertyRowMapper.newInstance(Participant.class));
			if (participants.size() > 0) {
				participant = participants.get(0);
			} else {
				participant = new Participant();
			}
			if (participant != null && participant.getProgramId() > 0) {
				program = findOnlyProgramById(participant.getProgramId());
			} else {
				program = new Program();
			}
			participant.setProgram(program);
			return participant;
		} catch (EmptyResultDataAccessException ex) {
			return new Participant();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ParticipantRepository#findOnlyProgramById
	 * (java.lang.Integer)
	 */
	@Override
	public Program findOnlyProgramById(int id) {
		try {
			Program program;
			Map<String, Object> params = new HashMap<>();
			params.put("program_id", id);
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE program_id=:program_id", params,
					BeanPropertyRowMapper.newInstance(Program.class));
			return program;
		} catch (EmptyResultDataAccessException ex) {
			return new Program();
		}
	}

	/**
	 * Retrieve <code>List<Participant></code> from the data store by values
	 * given in the SearchRequest.
	 * 
	 * @param searchRequest
	 * @return <code>List<Participant></code>
	 */
	@Override
	public List<Participant> getParticipantList(SearchRequest searchRequest) {
		List<Participant> participants = null;
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder orderBy = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		if (!("ALL".equals(searchRequest.getSearchField())) && null != searchRequest.getSearchField()
				&& !searchRequest.getSearchField().isEmpty()) {
			if (null != searchRequest.getSearchText() && !searchRequest.getSearchText().isEmpty()) {
				whereCondition.append(whereCondition.length() > 0 ? " and " + searchRequest.getSearchField()
						+ " LIKE '%" + searchRequest.getSearchText() + "%'" : searchRequest.getSearchField()
						+ " LIKE '%" + searchRequest.getSearchText() + "%'");
			}
		}

		if (null != searchRequest.getSortBy() && !searchRequest.getSortBy().isEmpty()) {
			orderBy.append(orderBy.length() > 0 ? ", " + searchRequest.getSortBy() : searchRequest.getSortBy());
			if (null != searchRequest.getSortDirection() && !searchRequest.getSortDirection().isEmpty()) {
				orderBy.append(searchRequest.getSortDirection().equalsIgnoreCase("0") ? " asc" : " desc");
			}
		}

		participants = this.namedParameterJdbcTemplate.query("SELECT * FROM participant"
				+ (whereCondition.length() > 0 ? " WHERE " + whereCondition : "")
				+ (orderBy.length() > 0 ? " ORDER BY " + orderBy : ""), params,
				BeanPropertyRowMapper.newInstance(Participant.class));

		return participants;

	}

	@Override
	public List<Participant> getParticipantListToGenerateEWelcomeID() {

		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT * FROM participant WHERE create_time <= CURRENT_TIMESTAMP" 
						+ " AND ewelcome_id_state = 'T' AND ("
						+ "welcome_card_number IS NULL" + " OR welcome_card_number = '')",
				BeanPropertyRowMapper.newInstance(Participant.class));
		return participants;

	}

	@Override
	public List<Participant> getEWelcomeIdGenerationFailedParticipants(String programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT * from participant WHERE create_time <= CURRENT_TIMESTAMP AND program_id=:programId AND "
				+ "ewelcome_id_state = 'F' AND ( welcome_card_number IS NULL OR welcome_card_number = '') AND  is_ewelcome_id_informed=0"
						,params,
						BeanPropertyRowMapper.newInstance(Participant.class));
		
		return participants;
		
	}

	@Override
	public List<Participant> getEWelcomeIdGeneratedPartcicipants(String programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT * from participant WHERE create_time <= CURRENT_TIMESTAMP AND program_id=:programId AND "
				+ "ewelcome_id_state = 'C' AND ( welcome_card_number IS NOT NULL OR welcome_card_number <> '') AND  is_ewelcome_id_informed=0"
						,params,
						BeanPropertyRowMapper.newInstance(Participant.class));
		
		return participants;
	}

}
