package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantRepositoryImpl.class);

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
	 * org.srcm.heartfulness.repository.ParticipantRepository#findByProgramIdAndRole
	 * (java.lang.Integer)
	 */
	@Override
	public List<Participant> findByProgramIdAndRole(int programId, List<String> emailList,String userRole) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder emailString = new StringBuilder("");

		/*String userRole = this.jdbcTemplate.query("SELECT role from user WHERE email=? ", new Object[] { mail },
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getString(1);
						}
						return null;
					}
				});*/
		if(null != emailList && emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}

		if (null != userRole && !userRole.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if (userRole.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)) {
				whereCondition
				.append(" ( p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+ " OR (p.coordinator_email IN(" + emailString + ") OR pc.email IN(" + emailString + "))) ");
			} else {
				whereCondition.append(" (p.coordinator_email IN(" + emailString + ") OR pc.email IN(" + emailString + ")) ");
			}
		}
		whereCondition
		.append((whereCondition.length() > 0) ? " AND pr.program_id=:programId AND pr.program_id=p.program_id "
				: " pr.program_id=:programId AND pr.program_id=p.program_id ");

		List<Participant> participants = this.namedParameterJdbcTemplate
				.query("SELECT DISTINCT pr.* FROM participant pr,program p LEFT JOIN program_coordinators pc ON p.program_id = pc.program_id WHERE "
						+ whereCondition, sqlParameterSource, BeanPropertyRowMapper.newInstance(Participant.class));

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

			Integer participantId = 0;

			participantId = this.jdbcTemplate.query(
					"SELECT id from participant where print_name=? AND email=? AND mobile_phone=? AND program_id=?",
					new Object[] { participant.getPrintName(), participant.getEmail(), participant.getMobilePhone(), participant.getProgramId() }, new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});

			if (participantId <= 0) {
				participantId = this.jdbcTemplate.query(
						"SELECT id from participant where  print_name=? AND excel_sheet_sequence_number=? AND program_id=?",
						new Object[] { participant.getPrintName(), participant.getExcelSheetSequenceNumber(),  participant.getProgramId() }, new ResultSetExtractor<Integer>() {
							@Override
							public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
								if (resultSet.next()) {
									return resultSet.getInt(1);
								}
								return 0;
							}
						});
			}

			if (participantId <= 0) {
				participantId = this.jdbcTemplate.query(
						"SELECT id from participant where print_name=? AND email=? AND program_id=?",
						new Object[] { participant.getPrintName(), participant.getEmail(), participant.getProgramId() }, new ResultSetExtractor<Integer>() {
							@Override
							public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
								if (resultSet.next()) {
									return resultSet.getInt(1);
								}
								return 0;
							}
						});
			}

			if (participantId <= 0) {
				participantId = this.jdbcTemplate.query(
						"SELECT id from participant where print_name=? AND mobile_phone=? AND program_id=?",
						new Object[] { participant.getPrintName(), participant.getMobilePhone(), participant.getProgramId() }, new ResultSetExtractor<Integer>() {
							@Override
							public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
								if (resultSet.next()) {
									return resultSet.getInt(1);
								}
								return 0;
							}
						});

			}

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
						if(null != oldParticipantDetails.getWelcomeCardNumber() && !oldParticipantDetails.getWelcomeCardNumber().isEmpty() 
								&& oldParticipantDetails.getWelcomeCardNumber().matches(ExpressionConstants.EWELCOME_ID_REGEX)){
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
					participant.setSeqId(checkExistanceOfAutoGeneratedSeqId(SmsUtil.generateFourDigitPIN(),participant.getProgramId()));
				}
			} else {
				participant.setSeqId(checkExistanceOfAutoGeneratedSeqId(SmsUtil.generateFourDigitPIN(),participant.getProgramId()));
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
					+ "is_ewelcome_id_informed=:isEwelcomeIdInformed, " + "batch=:batch, " + "receive_updates=:receiveUpdates, " + "introduced=:introduced, "
					+ "seqId=:seqId, " + "ewelcome_id_state=:ewelcomeIdState, " + "ewelcome_id_remarks=:ewelcomeIdRemarks, " +"total_days=:totalDays, "
					+ "phone=:phone,district=:district "
					+ "WHERE id=:id", parameterSource);
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

	/*@Override
	public List<Participant> getParticipantListToGenerateEWelcomeID() {

		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT * FROM participant WHERE create_time <= CURRENT_TIMESTAMP" 
						+ " AND ewelcome_id_state = 'T' AND ("
						+ "welcome_card_number IS NULL" + " OR welcome_card_number = '')",
				BeanPropertyRowMapper.newInstance(Participant.class));
		return participants;

	}*/

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
	public List<Participant> getEWelcomeIdGeneratedParticipants(String programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		List<Participant> participants = this.namedParameterJdbcTemplate.query(
				"SELECT * from participant WHERE create_time <= CURRENT_TIMESTAMP AND program_id=:programId AND "
						+ "ewelcome_id_state = 'C' AND ( welcome_card_number IS NOT NULL OR welcome_card_number <> '') AND  is_ewelcome_id_informed=0"
						,params,
						BeanPropertyRowMapper.newInstance(Participant.class));

		return participants;
	}

	@Override
	public List<Integer> getProgramIDsToGenerateEwelcomeIds() {

		return this.jdbcTemplate.queryForList("SELECT DISTINCT(pr.program_id) FROM program p,participant pr"
				+ " WHERE p.program_id = pr.program_id AND pr.create_time < CURDATE()"
				+ " AND pr.ewelcome_id_state = 'T'", null, Integer.class);
	}

	@Override
	public List<Participant> getParticipantwithProgramIdToGenerateEwelcomeId(Integer programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		return this.namedParameterJdbcTemplate
				.query("SELECT print_name"
						+ ",first_name,date_of_birth,email,first_sitting_date,third_sitting_date,third_sitting,city,state,country,welcome_card_number,"
						+ "welcome_card_date,id,mobile_phone,introduction_date,address_line1,address_line2,"
						+ "ewelcome_id_state,ewelcome_id_remarks,seqId from participant "
						+ " WHERE program_id=:programId AND create_time < NOW() "
						+ " AND ewelcome_id_state = 'T'", params,
						BeanPropertyRowMapper.newInstance(Participant.class));
	}

	@Override
	public void updateParticipantEwelcomeIDDetails(Participant participant) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", participant.getId());
		params.put("welcomeCardNumber", participant.getWelcomeCardNumber());
		params.put("welcomeCardDate", participant.getWelcomeCardDate());
		params.put("introduced", participant.getIntroduced());
		params.put("introductionDate", participant.getIntroductionDate());
		params.put("introducedBy", participant.getIntroducedBy());
		params.put("ewelcomeIdState", participant.getEwelcomeIdState());
		params.put("ewelcomeIdRemarks", participant.getEwelcomeIdRemarks());
		params.put("isEwelcomeIdInformed", participant.getIsEwelcomeIdInformed());
		if (0 != participant.getId()) {
			this.namedParameterJdbcTemplate.update("UPDATE participant SET " + "introduction_date=:introductionDate,"
					+ "introduced_by=:introducedBy," + "welcome_card_number=:welcomeCardNumber,"
					+ "welcome_card_date=:welcomeCardDate," + "is_ewelcome_id_informed=:isEwelcomeIdInformed, "
					+ "introduced=:introduced, " + "ewelcome_id_state=:ewelcomeIdState, "
					+ "ewelcome_id_remarks=:ewelcomeIdRemarks " + "WHERE id=:id", params);
		}
	}

	@Override
	public String checkExistanceOfAutoGeneratedSeqId(String generatedSeqId,int programId) {
		try{
			String eventIdExistence = this.jdbcTemplate.query(
					"SELECT seqId from participant WHERE seqId=? AND program_id=? ",
					new Object[] { generatedSeqId,programId }, new ResultSetExtractor<String>() {
						@Override
						public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getString(1);
							}
							return null;
						}
					});

			if (null != eventIdExistence) {
				generatedSeqId = ExpressionConstants.EVENT_ID_PREFIX + SmsUtil.generateFourDigitPIN();
				return checkExistanceOfAutoGeneratedSeqId(generatedSeqId, programId);
			} else {
				return generatedSeqId;
			}
		}catch(Exception e){
			LOGGER.error("Exception while checking the existance of the seqId for the particpant seqId : {} , programId : {} , Exception : {} ",
					generatedSeqId,programId,e);
			return generatedSeqId;
		}
	}

	@Override
	public int getParticipantCountByProgIdAndSeqId(int programId, String seqId) {

		int pctptCount = this.jdbcTemplate.query("SELECT count(id) FROM participant WHERE seqId=? AND program_id=?",
				new Object[] { seqId,programId }, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
				return 0;
			}
		});
		return pctptCount;
	}
}
