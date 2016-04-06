package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.srcm.heartfulness.constants.EventConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.SmsUtil;

/**
 * Program Repository
 * Created by vsonnathi on 11/17/15.
 */
@Repository
public class ProgramRepositoryImpl implements ProgramRepository {

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertProgram;
	private SimpleJdbcInsert insertCoOrdinatorStatistics;
	private ParticipantRepository participantRepository;


	@Autowired
	public ProgramRepositoryImpl(DataSource dataSource, ParticipantRepository participantRepository) {
		this.participantRepository = participantRepository;
		
		this.insertProgram = new SimpleJdbcInsert(dataSource)
		.withTableName("program")
		.usingGeneratedKeyColumns("program_id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.insertCoOrdinatorStatistics=new SimpleJdbcInsert(dataSource).withTableName("coordinator_statistics").usingGeneratedKeyColumns("id");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByHashCode(java.lang.String)
	 */
	@Override
	public Collection<Program> findByHashCode(String hashCode) throws DataAccessException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findById(java.lang.Integer)
	 */
	@Override
	public Program findById(int id) throws DataAccessException {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("program_id", id);
		program = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT * FROM program WHERE program_id=:program_id",
				params, BeanPropertyRowMapper.newInstance(Program.class)
				);

		// get the participantList
		List<Participant> participants = this.participantRepository.findByProgramId(id);
		program.setParticipantList(participants);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findUpdatedProgramIdsSince(java.util.Date;)
	 */
	@Override
	public List<Integer> findUpdatedProgramIdsSince(Date lastBatchRun) {
		List<Integer> programIds = this.jdbcTemplate.queryForList("SELECT program_id from program where update_time > ?",
				new Object[]{lastBatchRun}, Integer.class);
		//update the last batch run
		this.jdbcTemplate.update("UPDATE batch_operations_status set last_normalization_run=?", new Date());

		return programIds;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#save(Program program)
	 */
	@Override
	public void save(Program program) {

		// Find if there is an existing program row
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_hash_code=?",
					new Object[]{program.getProgramHashCode()},
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

			if (programId > 0) {
				program.setProgramId(programId);
			}
		}

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(program);
		if (program.getProgramId() == 0) {
			Number newId = this.insertProgram.executeAndReturnKey(parameterSource);
			program.setProgramId(newId.intValue());
		} else {
			// TODO: Need to deal with Hashcode.
			this.namedParameterJdbcTemplate.update(
					"UPDATE program SET " +
							"program_hash_code=:programHashCode, " +
							"program_name=:programName, " +
							"program_channel=:programChannel, " +
							"program_start_date=:programStartDate, " +
							"program_end_date=:programEndDate," +
							"coordinator_name=:coordinatorName," +
							"coordinator_email=:coordinatorEmail," +
							"coordinator_mobile=:coordinatorMobile," +
							"event_place=:eventPlace," +
							"event_city=:eventCity," +
							"event_state=:eventState," +
							"event_country=:eventCountry," +
							"organization_department=:organizationDepartment," +
							"organization_name=:organizationName," +
							"organization_web_site=:organizationWebSite," +
							"organization_contact_name=:organizationContactName," +
							"organization_contact_email=:organizationContactEmail," +
							"organization_contact_mobile=:organizationContactMobile," +
							"preceptor_name=:preceptorName," +
							"preceptor_id_card_number=:preceptorIdCardNumber," +
							"welcome_card_signed_by_name=:welcomeCardSignedByName," +
							"welcome_card_signer_id_card_number=:welcomeCardSignerIdCardNumber," +
							"remarks=:remarks, " +
							"auto_generated_event_id=:autoGeneratedEventId, " +
							"auto_generated_intro_id=:autoGeneratedIntroId " +
							"WHERE program_id=:programId", parameterSource);
		}

		// If there are participants update them.
		List<Participant> participants = program.getParticipantList();
		for (Participant participant : participants) {
			participant.setProgramId(program.getProgramId());
			participantRepository.save(participant);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#save(Program program)
	 */
	@Override
	public void saveWithProgramName(Program program) {
		
		if(null != program.getAutoGeneratedEventId()){
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where auto_generated_event_id=?",
					new Object[]{program.getAutoGeneratedEventId()},
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
			program.setProgramId(programId);
			program.setAutoGeneratedIntroId(getAutoGeneratedIntroId(programId));
		}

		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_hash_code=?",
					new Object[]{program.getProgramHashCode()},
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
			if (programId > 0) {
				program.setProgramId(programId);
				
				String autoGeneratedEventId = getAutogeneratedEventId(programId);
				String autoGeneratedIntroId = getAutoGeneratedIntroId(programId);
				
				if(autoGeneratedEventId!=null && !autoGeneratedEventId.isEmpty()){
					program.setAutoGeneratedEventId(autoGeneratedEventId);
				}else{
					program.setAutoGeneratedEventId(EventConstants.EVENT_ID_PREFIX + SmsUtil.generateRandomNumber(6));
				}
				
				if(autoGeneratedIntroId!=null && !autoGeneratedIntroId.isEmpty()){
					program.setAutoGeneratedIntroId(autoGeneratedIntroId);
				}else{
					program.setAutoGeneratedIntroId(EventConstants.INTRO_ID_PREFIX  +SmsUtil.generateRandomNumber(7));
				}
				//program.setAutoGeneratedIntroId(getAutoGeneratedIntroId(programId));

			}else{

				program.setAutoGeneratedEventId(EventConstants.EVENT_ID_PREFIX + SmsUtil.generateRandomNumber(6));
				program.setAutoGeneratedIntroId(EventConstants.INTRO_ID_PREFIX + SmsUtil.generateRandomNumber(7));
			}

		}

		/*// Find if there is an existing program row
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_name=?",
					new Object[]{program.getProgramName()},
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

			if (programId > 0) {
				program.setProgramId(programId);
			}
		}
*/
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(program);
		if (program.getProgramId() == 0) {
			Number newId = this.insertProgram.executeAndReturnKey(parameterSource);
			program.setProgramId(newId.intValue());
		} else {
			// TODO: Need to deal with Hashcode.
			this.namedParameterJdbcTemplate.update(
					"UPDATE program SET " +
							"program_hash_code=:programHashCode, " +
							"program_name=:programName, " +
							"program_channel=:programChannel, " +
							"program_start_date=:programStartDate, " +
							"program_end_date=:programEndDate," +
							"coordinator_name=:coordinatorName," +
							"coordinator_email=:coordinatorEmail," +
							"coordinator_mobile=:coordinatorMobile," +
							"event_place=:eventPlace," +
							"event_city=:eventCity," +
							"event_state=:eventState," +
							"event_country=:eventCountry," +
							"organization_department=:organizationDepartment," +
							"organization_name=:organizationName," +
							"organization_web_site=:organizationWebSite," +
							"organization_contact_name=:organizationContactName," +
							"organization_contact_email=:organizationContactEmail," +
							"organization_contact_mobile=:organizationContactMobile," +
							"preceptor_name=:preceptorName," +
							"preceptor_id_card_number=:preceptorIdCardNumber," +
							"welcome_card_signed_by_name=:welcomeCardSignedByName," +
							"welcome_card_signer_id_card_number=:welcomeCardSignerIdCardNumber," +
							"remarks=:remarks, " +
							"auto_generated_event_id=:autoGeneratedEventId, " +
							"auto_generated_intro_id=:autoGeneratedIntroId " +
							"WHERE program_id=:programId", parameterSource);
		}

		// If there are participants update them.
		List<Participant> participants = program.getParticipantList();
		for (Participant participant : participants) {
			participant.setProgramId(program.getProgramId());
			participantRepository.save(participant);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#isProgramExist(Program program)
	 */
	@Override
	public boolean isProgramExist(Program program) {
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_hash_code=?",
					new Object[]{program.getProgramHashCode()},
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

			if (programId > 0) {
				program.setProgramId(programId);
				return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#isProgramExist(Program program)
	 */
	@Override
	public boolean isProgramExistByProgramName(Program program) {
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_name=?",
					new Object[]{program.getProgramName()},
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

			if (programId > 0) {
				program.setProgramId(programId);
				return true;
			}
		}
		return false;
	}
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByAutoGeneratedEventId(java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedEventId(String autoGeneratedEventid){
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("auto_generated_event_id", autoGeneratedEventid);
		try{
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE auto_generated_event_id=:auto_generated_event_id",
					params, BeanPropertyRowMapper.newInstance(Program.class)
					);
		}catch(EmptyResultDataAccessException ex){
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if(program!=null && program.getProgramId()>0){
			participants =  this.participantRepository.findByProgramId(program.getProgramId());
		}else{
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByEventName(java.lang.String)
	 */
	@Override
	public Program findByEventName(String eventName){
		Program program = new Program();
		program.setProgramChannel(eventName);
		Map<String, Object> params = new HashMap<>();
		params.put("program_hash_code", program.getProgramHashCode());
		try{
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE program_hash_code=:program_hash_code",
					params, BeanPropertyRowMapper.newInstance(Program.class)
					);
		}catch(EmptyResultDataAccessException ex){
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if(program!=null && program.getProgramId()>0){
			participants =  this.participantRepository.findByProgramId(program.getProgramId());
		}else{
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#findByAutoGeneratedIntroId(java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedIntroId(String autoGeneratedIntroId) {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("auto_generated_intro_id", autoGeneratedIntroId);
		try{
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE auto_generated_intro_id=:auto_generated_intro_id",
					params, BeanPropertyRowMapper.newInstance(Program.class)
					);
		}catch(EmptyResultDataAccessException ex){
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if(program!=null && program.getProgramId()>0){
			participants =  this.participantRepository.findByProgramId(program.getProgramId());
		}else{
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}
	
	
	
	@Override
	public List<Program> getEventByEmail(String email,boolean isAdmin) {
		List<Program> program = null;
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		
		if(!isAdmin){
			whereCondition.append("coordinator_email=:coordinator_email");
		}
		program = this.namedParameterJdbcTemplate.query(
				"SELECT program_id,program_channel,program_start_date,program_end_date,"
				+ "coordinator_name,coordinator_email,coordinator_mobile,event_place,"
				+ "event_city,event_state,event_country,organization_department,"
				+ "organization_name,organization_web_site,organization_contact_name,"
				+ "organization_contact_email,organization_contact_mobile,preceptor_name,"
				+ "preceptor_id_card_number,welcome_card_signed_by_name,welcome_card_signer_Id_card_number,"
				+ "remarks,auto_generated_event_id,auto_generated_intro_id"
				+ " FROM program"+(whereCondition.length()>0 ? " WHERE "+whereCondition : ""), sqlParameterSource,
				BeanPropertyRowMapper.newInstance(Program.class)
				);
		return program;
	}

	@Override
	public Program getEventById(int id) {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		program = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT program_id,program_channel,program_start_date,program_end_date,"
						+ "coordinator_name,coordinator_email,coordinator_mobile,event_place,"
						+ "event_city,event_state,event_country,organization_department,"
						+ "organization_name,organization_web_site,organization_contact_name,"
						+ "organization_contact_email,organization_contact_mobile,preceptor_name,"
						+ "preceptor_id_card_number,welcome_card_signed_by_name,welcome_card_signer_Id_card_number,"
						+ "remarks,auto_generated_event_id,auto_generated_intro_id"
						+ " FROM program WHERE program_id=:id" + " OR auto_generated_event_id=:id", params,
				BeanPropertyRowMapper.newInstance(Program.class));
		List<Participant> participants = null;
		// get the participantList
		if(program!=null && program.getProgramId()>0){
			participants =  this.participantRepository.findByProgramId(id);
		}else{
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}

	@Override
	public List<Participant> getParticipantList(int decryptedProgramId) {
		List<Participant> participants = new ArrayList<Participant>();
		participants =  this.participantRepository.findByProgramId(decryptedProgramId);
		return participants;
	}
	
	
	@Override
	public List<Program> getEventsByEmail(String email,boolean isAdmin) {
		List<Program> program = null;
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);

		if(!isAdmin){
			whereCondition.append("coordinator_email=:coordinator_email");
		}
		program = this.namedParameterJdbcTemplate.query(
				"SELECT auto_generated_event_id,program_channel,program_name,program_start_date,program_end_date,"
						+ "coordinator_name,coordinator_email,coordinator_mobile,event_place,"
						+ "event_city,event_state,event_country,preceptor_name,"
						+ "preceptor_id_card_number"
						+ " FROM program"+(whereCondition.length()>0 ? " WHERE "+whereCondition : ""), sqlParameterSource,
						BeanPropertyRowMapper.newInstance(Program.class)
				);
		return program;
	}



	@Override
	public Program saveProgram(Program program) {

		if(null != program.getAutoGeneratedEventId()){
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where auto_generated_event_id=?",
					new Object[]{program.getAutoGeneratedEventId()},
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
			program.setProgramId(programId);
			program.setAutoGeneratedIntroId(getAutoGeneratedIntroId(programId));
		}

		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query(
					"SELECT program_id from program where program_hash_code=?",
					new Object[]{program.getProgramHashCode()},
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
			if (programId > 0) {
				program.setProgramId(programId);
				
				String autoGeneratedEventId = getAutogeneratedEventId(programId);
				String autoGeneratedIntroId = getAutoGeneratedIntroId(programId);
				
				if(autoGeneratedEventId!=null && !autoGeneratedEventId.isEmpty()){
					program.setAutoGeneratedEventId(autoGeneratedEventId);
				}else{
					program.setAutoGeneratedEventId(EventConstants.EVENT_ID_PREFIX + SmsUtil.generateRandomNumber(6));
				}
				
				if(autoGeneratedIntroId!=null && !autoGeneratedIntroId.isEmpty()){
					program.setAutoGeneratedIntroId(autoGeneratedIntroId);
				}else{
					program.setAutoGeneratedIntroId(EventConstants.INTRO_ID_PREFIX  +SmsUtil.generateRandomNumber(7));
				}
				//program.setAutoGeneratedIntroId(getAutoGeneratedIntroId(programId));

			}else{

				program.setAutoGeneratedEventId(EventConstants.EVENT_ID_PREFIX + SmsUtil.generateRandomNumber(6));
				program.setAutoGeneratedIntroId(EventConstants.INTRO_ID_PREFIX + SmsUtil.generateRandomNumber(7));
			}

		}

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(program);
		if (program.getProgramId() == 0) {
			Number newId = this.insertProgram.executeAndReturnKey(parameterSource);
			program.setProgramId(newId.intValue());
		} else {
			// TODO: Need to deal with Hashcode.
			this.namedParameterJdbcTemplate.update(
					"UPDATE program SET " +
							"program_hash_code=:programHashCode, " +
							"program_name=:programName, " +
							"program_channel=:programChannel, " +
							"program_start_date=:programStartDate, " +
							"program_end_date=:programEndDate," +
							"coordinator_name=:coordinatorName," +
							"coordinator_email=:coordinatorEmail," +
							"coordinator_mobile=:coordinatorMobile," +
							"event_place=:eventPlace," +
							"event_city=:eventCity," +
							"event_state=:eventState," +
							"event_country=:eventCountry," +
							"organization_department=:organizationDepartment," +
							"organization_name=:organizationName," +
							"organization_web_site=:organizationWebSite," +
							"organization_contact_name=:organizationContactName," +
							"organization_contact_email=:organizationContactEmail," +
							"organization_contact_mobile=:organizationContactMobile," +
							"preceptor_name=:preceptorName," +
							"preceptor_id_card_number=:preceptorIdCardNumber," +
							"welcome_card_signed_by_name=:welcomeCardSignedByName," +
							"welcome_card_signer_id_card_number=:welcomeCardSignerIdCardNumber," +
							"remarks=:remarks, " +
							"auto_generated_event_id=:autoGeneratedEventId, " +
							"auto_generated_intro_id=:autoGeneratedIntroId " +
							"WHERE program_id=:programId", parameterSource);
		}
		return program;
	}


	public String getAutogeneratedEventId(int programId){

		String autoGeneratedEventId = this.jdbcTemplate.query(
				"SELECT auto_generated_event_id from program where program_id=?",
				new Object[]{programId},
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getString(1);
						}
						return "";
					}
				}
				);
		return autoGeneratedEventId;
	}

	public String getAutoGeneratedIntroId(int programId){

		String autoGeneratedIntroId = this.jdbcTemplate.query(
				"SELECT auto_generated_intro_id from program where program_id=?",
				new Object[]{programId},
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getString(1);
						}
						return "";
					}
				}
				);
		return autoGeneratedIntroId;
	}

	@Override
	public int getProgramIdByEventId(String eventId) {

		int programId = this.jdbcTemplate.query(
				"SELECT program_id from program where auto_generated_event_id=?",
				new Object[]{eventId},
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

		return programId;
	}
	
	@Override
	public int getEventCountByEmail(String email, boolean isAdmin) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		if (!isAdmin) {
			whereCondition.append("AND coordinator_email=:coordinator_email");
		}
		int count = this.namedParameterJdbcTemplate
				.queryForObject(
						"SELECT COUNT(program_channel)"
								+ " FROM program"
								+ (whereCondition.length() > 0 ? " WHERE (program_channel IS NULL OR program_channel IS NOT NULL OR program_channel='') "
										+ whereCondition
										: ""), params, Integer.class);

		return count;
	}

	@Override
	public int getNonCategorizedEventsByEmail(String email, boolean isAdmin) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		if (!isAdmin) {
			whereCondition.append("AND coordinator_email=:coordinator_email");
		}
		int count = this.namedParameterJdbcTemplate.queryForObject("SELECT COUNT(program_channel)"
				+ " FROM program"
				+ (whereCondition.length() > 0 ? " WHERE (program_channel IS NULL OR program_channel='') "
						+ whereCondition : ""), params, Integer.class);
		return count;
	}
	
	@Override
	public List<String> getAllEventCategories() {
		try {
			List<String> events = this.jdbcTemplate.queryForList("SELECT distinct name" + " FROM channel", null,
					String.class);
			return events;
		} catch (EmptyResultDataAccessException ex) {
			return new ArrayList<String>();
		}
	}
	
	@Override
	public int getEventCountByCategory(String email, boolean isAdmin, String eventCategory) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		params.put("program_channel", eventCategory);

		if (!isAdmin) {
			whereCondition.append("AND coordinator_email=:coordinator_email");
		}
		int count = this.namedParameterJdbcTemplate.queryForObject("SELECT COUNT(program_channel)" + " FROM program"
				+ (whereCondition.length() > 0 ? " WHERE (program_channel=:program_channel) " + whereCondition : ""),
				params, Integer.class);
		return count;
	}
	
	@Override
	public int getMiscellaneousEventsByEmail(String email, boolean isAdmin, List<String> eventcategories) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		if (!isAdmin) {
			whereCondition.append("AND coordinator_email=:coordinator_email");
		}
		int count = this.namedParameterJdbcTemplate
				.queryForObject(
						"SELECT COUNT(program_channel)"
								+ " FROM program"
								+ (whereCondition.length() > 0 ? " WHERE ( (program_channel NOT IN (SELECT distinct `name` FROM `channel`)) "
										+ "AND ( program_channel IS NOT NULL " + "AND program_channel!='') ) "
										+ whereCondition
										: ""), params, Integer.class);
		return count;
	}
	
	@Override
	public void updateCoOrdinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("auto_generated_event_id", eventAdminChangeRequest.getEventId());
			Program	program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE auto_generated_event_id=:auto_generated_event_id", params,
					BeanPropertyRowMapper.newInstance(Program.class));
			eventAdminChangeRequest.setProgramId(program.getProgramId());
			BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(eventAdminChangeRequest);
			Number newId = this.insertCoOrdinatorStatistics.executeAndReturnKey(parameterSource);
			eventAdminChangeRequest.setId(newId.intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Participant findParticipantBySeqId(String seqId, int programId) {
		Participant participant;
		Map<String, Object> params = new HashMap<>();
		params.put("seqId", seqId);
		params.put("programId", programId);
		try {
			participant = this.namedParameterJdbcTemplate
					.queryForObject(
							"SELECT * FROM participant WHERE seqId=:seqId AND program_id=:programId",
							params, BeanPropertyRowMapper.newInstance(Participant.class));
			System.out.println(participant.toString());
			return participant;

		} catch (EmptyResultDataAccessException ex) {
			// participant=new ParticipantRequest();
			return null;
		}

	}

	@Override
	public void UpdateParticipantsStatus(String participantIds, String eventId, String introduced) {
		Map<String, Object> params = new HashMap<>();
		params.put("introduced", PMPConstants.REQUIRED_YES.equalsIgnoreCase(introduced) ? 1 : 0);
		params.put("programId", getProgramIdByEventId(eventId));
		params.put("seqId", participantIds);
		this.namedParameterJdbcTemplate.update(
				"UPDATE participant SET introduced=:introduced WHERE program_id=:programId AND seqId=:seqId ", params);

	}

}
