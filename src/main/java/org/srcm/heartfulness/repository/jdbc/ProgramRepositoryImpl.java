package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.Coordinator;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramPermissionLetterdetails;
import org.srcm.heartfulness.model.UploadedFiles;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.SmsUtil;

/**
 * Program Repository Created by vsonnathi on 11/17/15.
 */
@Repository
public class ProgramRepositoryImpl implements ProgramRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(ProgramRepositoryImpl.class);

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertProgram;
	private ParticipantRepository participantRepository;

	private SimpleJdbcInsert insertCoOrdinatorStatistics;
	private SimpleJdbcInsert insertDeletedParticipants;
	private SimpleJdbcInsert insertProgramPermissionLetter;
	private SimpleJdbcInsert insertUploadedFiles;

	@Autowired
	public ProgramRepositoryImpl(DataSource dataSource, ParticipantRepository participantRepository) {
		this.participantRepository = participantRepository;

		this.insertProgram = new SimpleJdbcInsert(dataSource).withTableName("program").usingGeneratedKeyColumns("program_id");
		this.insertCoOrdinatorStatistics = new SimpleJdbcInsert(dataSource).withTableName("coordinator_statistics").usingGeneratedKeyColumns("id");
		this.insertDeletedParticipants = new SimpleJdbcInsert(dataSource).withTableName("deleted_participants").usingGeneratedKeyColumns("id");
		this.insertProgramPermissionLetter=new SimpleJdbcInsert(dataSource).withTableName("program_permission_letters").usingGeneratedKeyColumns("permission_letter_id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.insertUploadedFiles = new SimpleJdbcInsert(dataSource).withTableName("uploaded_files").usingGeneratedKeyColumns("id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#findByHashCode(java
	 * .lang.String)
	 */
	@Override
	public Collection<Program> findByHashCode(String hashCode) throws DataAccessException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#findById(java.lang
	 * .Integer)
	 */
	@Override
	public Program findById(int id) throws DataAccessException {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("program_id", id);
		program = this.namedParameterJdbcTemplate.queryForObject("SELECT * FROM program WHERE program_id=:program_id",
				params, BeanPropertyRowMapper.newInstance(Program.class));

		// get the participantList
		List<Participant> participants = this.participantRepository.findByProgramId(id);
		program.setParticipantList(participants);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#findUpdatedProgramIdsSince
	 * (java.util.Date;)
	 */
	@Override
	public List<Integer> findUpdatedProgramIdsSince(Date lastBatchRun) {
		List<Integer> programIds = this.jdbcTemplate.queryForList(
				"SELECT program_id from program where update_time > ?", new Object[] { lastBatchRun }, Integer.class);
		// update the last batch run
		this.jdbcTemplate.update("UPDATE batch_operations_status set last_normalization_run=?", new Date());

		return programIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.ProgramRepository#save(Program
	 * program)
	 */
	@Override
	public void save(Program program) {

		if (null != program.getAutoGeneratedEventId()) {
			int programId = this.jdbcTemplate.query("SELECT program_id from program where auto_generated_event_id=?",
					new Object[] { program.getAutoGeneratedEventId() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});
			program.setProgramId(programId);
			program.setAutoGeneratedIntroId(getAutoGeneratedIntroId(programId));
		}

		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query("SELECT program_id from program where program_hash_code=?",
					new Object[] { program.getProgramHashCode() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});
			if (programId > 0) {
				program.setProgramId(programId);

				String autoGeneratedEventId = getAutogeneratedEventId(programId);
				String autoGeneratedIntroId = getAutoGeneratedIntroId(programId);

				if (autoGeneratedEventId != null && !autoGeneratedEventId.isEmpty()) {
					program.setAutoGeneratedEventId(autoGeneratedEventId);
				} else {
					program.setAutoGeneratedEventId(checkExistanceOfAutoGeneratedEventId(ExpressionConstants.EVENT_ID_PREFIX
							+ SmsUtil.generateRandomNumber(6)));
				}

				if (autoGeneratedIntroId != null && !autoGeneratedIntroId.isEmpty()) {
					program.setAutoGeneratedIntroId(autoGeneratedIntroId);
				} else {
					program.setAutoGeneratedIntroId(checkExistanceOfAutoGeneratedIntroId(ExpressionConstants.INTRO_ID_PREFIX
							+ SmsUtil.generateRandomNumber(7)));
				}

			} else {

				program.setAutoGeneratedEventId(checkExistanceOfAutoGeneratedEventId(ExpressionConstants.EVENT_ID_PREFIX + SmsUtil.generateRandomNumber(6)));
				program.setAutoGeneratedIntroId(checkExistanceOfAutoGeneratedIntroId(ExpressionConstants.INTRO_ID_PREFIX + SmsUtil.generateRandomNumber(7)));
			}

		}

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(program);
		if (program.getProgramId() == 0) {
			Number newId = this.insertProgram.executeAndReturnKey(parameterSource);
			program.setProgramId(newId.intValue());
		} else {
			// TODO: Need to deal with Hashcode.
			this.namedParameterJdbcTemplate.update("UPDATE program SET " + "program_hash_code=:programHashCode, "
					+ "program_name=:programName, " + "program_channel=:programChannel, "
					+ "program_start_date=:programStartDate, " + "program_end_date=:programEndDate,"
					+ "coordinator_name=:coordinatorName," + "coordinator_email=:coordinatorEmail,"
					+ "coordinator_mobile=:coordinatorMobile," + "event_place=:eventPlace," + "event_city=:eventCity,"
					+ "event_state=:eventState," + "event_country=:eventCountry,"
					+ "organization_department=:organizationDepartment," + "organization_name=:organizationName,"
					+ "organization_web_site=:organizationWebSite,"
					+ "organization_contact_name=:organizationContactName,"
					+ "organization_contact_email=:organizationContactEmail,"
					+ "organization_contact_mobile=:organizationContactMobile," + "preceptor_name=:preceptorName,"
					+ "preceptor_id_card_number=:preceptorIdCardNumber,"
					+ "welcome_card_signed_by_name=:welcomeCardSignedByName,"
					+ "welcome_card_signer_id_card_number=:welcomeCardSignerIdCardNumber," + "remarks=:remarks, "
					+ "auto_generated_event_id=:autoGeneratedEventId, "
					+ "auto_generated_intro_id=:autoGeneratedIntroId, " + "abyasi_ref_no=:abyasiRefNo, "
					+ "srcm_group=:srcmGroup, " + "first_sitting_by=:firstSittingBy, " + "coordinator_abhyasi_id=:coordinatorAbhyasiId, "
					+ "program_zone=:programZone, "+ "program_center=:programCenter, "+ "organization_batch_no=:organizationBatchNo, "
					+ "organization_city=:organizationCity, "+ "organization_location=:organizationLocation, "+ "organization_full_address=:organizationFullAddress, "
					+ "organization_decision_maker_name=:organizationDecisionMakerName, "+ "organization_decision_maker_email=:organizationDecisionMakerEmail, "
					+ "organization_decision_maker_phone_no=:organizationDecisionMakerPhoneNo, "
					+ "is_ewelcome_id_generation_disabled=:isEwelcomeIdGenerationDisabled, "
					+ "jira_issue_number=:jiraIssueNumber, "
					+ "senders_email_address=:sendersEmailAddress, "
					+ "uploaded_file_id=:uploadedFileId, "
					+ "user_id=:userId "
					+ "WHERE program_id=:programId",
					parameterSource);
		}
		// If there are participants update them.
		List<Participant> participants = program.getParticipantList();
		for (Participant participant : participants) {
			participant.setCreatedSource(program.getCreatedSource());
			participant.setProgramId(program.getProgramId());
			participantRepository.save(participant);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.ProgramRepository#save(Program
	 * program)
	 */
	@Override
	public void saveWithProgramName(Program program) {

		// Find if there is an existing program row
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query("SELECT program_id from program where program_name=?",
					new Object[] { program.getProgramName() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});

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
			this.namedParameterJdbcTemplate
			.update("UPDATE program SET " + "program_hash_code=:programHashCode, "
					+ "program_name=:programName, " + "program_channel=:programChannel, "
					+ "program_start_date=:programStartDate, " + "program_end_date=:programEndDate,"
					+ "coordinator_name=:coordinatorName," + "coordinator_email=:coordinatorEmail,"
					+ "coordinator_mobile=:coordinatorMobile," + "event_place=:eventPlace,"
					+ "event_city=:eventCity," + "event_state=:eventState," + "event_country=:eventCountry,"
					+ "organization_department=:organizationDepartment,"
					+ "organization_name=:organizationName," + "organization_web_site=:organizationWebSite,"
					+ "organization_contact_name=:organizationContactName,"
					+ "organization_contact_email=:organizationContactEmail,"
					+ "organization_contact_mobile=:organizationContactMobile,"
					+ "preceptor_name=:preceptorName," + "preceptor_id_card_number=:preceptorIdCardNumber,"
					+ "welcome_card_signed_by_name=:welcomeCardSignedByName,"
					+ "welcome_card_signer_id_card_number=:welcomeCardSignerIdCardNumber,"
					+ "remarks=:remarks, " + "auto_generated_event_id=:autoGeneratedEventId, "
					+ "auto_generated_intro_id=:autoGeneratedIntroId, "
					+ "is_ewelcome_id_generation_disabled=:isEwelcomeIdGenerationDisabled "
					+ "WHERE program_id=:programId",
					parameterSource);
		}

		// If there are participants update them.
		List<Participant> participants = program.getParticipantList();
		for (Participant participant : participants) {
			participant.setCreatedSource(PMPConstants.CREATED_SOURCE_SMS);
			participant.setProgramId(program.getProgramId());
			participantRepository.save(participant);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#isProgramExist(Program
	 * program)
	 */
	@Override
	public boolean isProgramExist(Program program) {
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query("SELECT program_id from program where program_hash_code=?",
					new Object[] { program.getProgramHashCode() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});

			if (programId > 0) {
				program.setProgramId(programId);
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#isProgramExist(Program
	 * program)
	 */
	@Override
	public boolean isProgramExistByProgramName(Program program) {
		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query("SELECT program_id from program where program_name=?",
					new Object[] { program.getProgramName() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});

			if (programId > 0) {
				program.setProgramId(programId);
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#findByAutoGeneratedEventId
	 * (java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedEventId(String autoGeneratedEventid) {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("auto_generated_event_id", autoGeneratedEventid);
		try {
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE auto_generated_event_id=:auto_generated_event_id", params,
					BeanPropertyRowMapper.newInstance(Program.class));
		} catch (EmptyResultDataAccessException ex) {
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if (program != null && program.getProgramId() > 0) {
			participants = this.participantRepository.findByProgramId(program.getProgramId());
		} else {
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#findByEventName(java
	 * .lang.String)
	 */
	@Override
	public Program findByEventName(String eventName) {
		Program program = new Program();
		program.setProgramChannel(eventName);
		Map<String, Object> params = new HashMap<>();
		params.put("program_hash_code", program.getProgramHashCode());
		try {
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE program_hash_code=:program_hash_code", params,
					BeanPropertyRowMapper.newInstance(Program.class));
		} catch (EmptyResultDataAccessException ex) {
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if (program != null && program.getProgramId() > 0) {
			participants = this.participantRepository.findByProgramId(program.getProgramId());
		} else {
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ProgramRepository#findByAutoGeneratedIntroId
	 * (java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedIntroId(String autoGeneratedIntroId) {
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("auto_generated_intro_id", autoGeneratedIntroId);
		try {
			program = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM program WHERE auto_generated_intro_id=:auto_generated_intro_id", params,
					BeanPropertyRowMapper.newInstance(Program.class));
		} catch (EmptyResultDataAccessException ex) {
			program = new Program();
		}
		List<Participant> participants = null;
		// get the participantList
		if (program != null && program.getProgramId() > 0) {
			participants = this.participantRepository.findByProgramId(program.getProgramId());
		} else {
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
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
						+ "remarks,first_sitting_by,auto_generated_event_id,auto_generated_intro_id"
						+ " FROM program WHERE program_id=:id" + " OR auto_generated_event_id=:id", params,
						BeanPropertyRowMapper.newInstance(Program.class));
		List<Participant> participants = null;
		// get the participantList
		if (program != null && program.getProgramId() > 0) {
			participants = this.participantRepository.findByProgramId(id);
		} else {
			participants = new ArrayList<Participant>();
		}
		program.setParticipantList(participants);
		return program;
	}

	@Override
	public List<Program> getEventsByEmail(String email, boolean isAdmin, int offset, int pageSize) {
		List<Program> program = null;
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder limitCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);

		if (!isAdmin) {
			whereCondition.append("coordinator_email=:coordinator_email");
		}

		if ((offset != 0 && pageSize != 0) || (offset == 0 && pageSize != 0)) {
			limitCondition.append(" LIMIT " + offset + "," + pageSize);
		}
		program = this.namedParameterJdbcTemplate.query(
				"SELECT program_id,program_channel,program_start_date,program_end_date,"
						+ "coordinator_name,coordinator_email,coordinator_mobile,event_place,"
						+ "event_city,event_state,event_country,organization_department,"
						+ "organization_name,organization_web_site,organization_contact_name,"
						+ "organization_contact_email,organization_contact_mobile,preceptor_name,"
						+ "preceptor_id_card_number,welcome_card_signed_by_name,welcome_card_signer_Id_card_number,"
						+ "remarks,auto_generated_event_id,auto_generated_intro_id" + " FROM program"
						+ (whereCondition.length() > 0 ? " WHERE " + whereCondition : "")
						+ (limitCondition.length() > 0 ? limitCondition : ""), sqlParameterSource,
						BeanPropertyRowMapper.newInstance(Program.class));
		return program;
	}

	@Override
	public List<Participant> getParticipantList(int decryptedProgramId,List<String> mail,String role) {
		List<Participant> participants = new ArrayList<Participant>();
		participants = this.participantRepository.findByProgramIdAndRole(decryptedProgramId,mail,role);
		return participants;
	}

	/**
	 * Repository method to create a new record or update an existing event
	 * record.
	 * 
	 * @param event
	 *            to persist into the database.
	 * @return Program
	 */
	@Override
	public Program saveProgram(Program program) {

		if (null != program.getAutoGeneratedEventId()) {
			int programId = this.jdbcTemplate.query("SELECT program_id from program where auto_generated_event_id=?",
					new Object[] { program.getAutoGeneratedEventId() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});
			program.setProgramId(programId);
			program.setAutoGeneratedIntroId(getAutoGeneratedIntroId(programId));
		}

		if (program.getProgramId() == 0) {
			int programId = this.jdbcTemplate.query("SELECT program_id from program where program_hash_code=?",
					new Object[] { program.getProgramHashCode() }, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}
			});
			if (programId > 0) {
				program.setProgramId(programId);

				String autoGeneratedEventId = getAutogeneratedEventId(programId);
				String autoGeneratedIntroId = getAutoGeneratedIntroId(programId);

				if (autoGeneratedEventId != null && !autoGeneratedEventId.isEmpty()) {
					program.setAutoGeneratedEventId(autoGeneratedEventId);
				} else {
					program.setAutoGeneratedEventId(checkExistanceOfAutoGeneratedEventId(ExpressionConstants.EVENT_ID_PREFIX
							+ SmsUtil.generateRandomNumber(6)));
				}

				if (autoGeneratedIntroId != null && !autoGeneratedIntroId.isEmpty()) {
					program.setAutoGeneratedIntroId(autoGeneratedIntroId);
				} else {
					program.setAutoGeneratedIntroId(checkExistanceOfAutoGeneratedIntroId(ExpressionConstants.INTRO_ID_PREFIX
							+ SmsUtil.generateRandomNumber(7)));
				}
				// program.setAutoGeneratedIntroId(getAutoGeneratedIntroId(programId));

			} else {

				program.setAutoGeneratedEventId(checkExistanceOfAutoGeneratedEventId(ExpressionConstants.EVENT_ID_PREFIX
						+ SmsUtil.generateRandomNumber(6)));

				program.setAutoGeneratedIntroId(checkExistanceOfAutoGeneratedIntroId(ExpressionConstants.INTRO_ID_PREFIX
						+ SmsUtil.generateRandomNumber(7)));
			}

		}

		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(program);
		if (program.getProgramId() == 0) {
			Number newId = this.insertProgram.executeAndReturnKey(parameterSource);
			program.setProgramId(newId.intValue());
		} else {
			// TODO: Need to deal with Hashcode.
			this.namedParameterJdbcTemplate.update("UPDATE program SET " + "program_hash_code=:programHashCode, "
					+ "program_name=:programName, " + "program_channel=:programChannel, "
					+ "program_start_date=:programStartDate, " + "program_end_date=:programEndDate,"
					+ "coordinator_name=:coordinatorName," + "coordinator_email=:coordinatorEmail,"
					+ "coordinator_mobile=:coordinatorMobile," + "event_place=:eventPlace," + "event_city=:eventCity,"
					+ "event_state=:eventState," + "event_country=:eventCountry,"+ "first_sitting_by=:firstSittingBy, "
					+ "organization_department=:organizationDepartment," + "organization_name=:organizationName,"
					+ "organization_web_site=:organizationWebSite,"	+ "organization_contact_name=:organizationContactName,"
					+ "organization_contact_email=:organizationContactEmail,"+ "organization_contact_mobile=:organizationContactMobile," 
					+ "preceptor_name=:preceptorName,"+ "preceptor_id_card_number=:preceptorIdCardNumber,"
					+ "welcome_card_signed_by_name=:welcomeCardSignedByName," + "remarks=:remarks, "
					+ "welcome_card_signer_id_card_number=:welcomeCardSignerIdCardNumber," + "abyasi_ref_no=:abyasiRefNo, "
					+ "auto_generated_event_id=:autoGeneratedEventId, "	+ "auto_generated_intro_id=:autoGeneratedIntroId, " 
					+ "coordinator_abhyasi_id=:coordinatorAbhyasiId, "+ "coordinator_permission_letter_path=:coordinatorPermissionLetterPath, "
					+ "program_zone=:programZone, "+ "program_center=:programCenter, "+ "organization_batch_no=:organizationBatchNo, "
					+ "organization_city=:organizationCity, "+ "organization_location=:organizationLocation, "+ "organization_full_address=:organizationFullAddress, "
					+ "organization_decision_maker_name=:organizationDecisionMakerName, "+ "organization_decision_maker_email=:organizationDecisionMakerEmail, "
					+ "organization_decision_maker_phone_no=:organizationDecisionMakerPhoneNo, "
					+ "is_ewelcome_id_generation_disabled=:isEwelcomeIdGenerationDisabled "
					+ "WHERE program_id=:programId", parameterSource);
		}
		return program;
	}

	/**
	 * method to get the auto generated eventID for the given programID
	 * 
	 * @param programId
	 * @return
	 */
	public String getAutogeneratedEventId(int programId) {

		String autoGeneratedEventId = this.jdbcTemplate.query(
				"SELECT auto_generated_event_id from program where program_id=?", new Object[] { programId },
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getString(1);
						}
						return "";
					}
				});
		return autoGeneratedEventId;
	}

	/**
	 * method to get the auto generated introID for the given programID
	 * 
	 * @param programId
	 * @return
	 */
	public String getAutoGeneratedIntroId(int programId) {

		String autoGeneratedIntroId = this.jdbcTemplate.query(
				"SELECT auto_generated_intro_id from program where program_id=?", new Object[] { programId },
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getString(1);
						}
						return "";
					}
				});
		return autoGeneratedIntroId;
	}

	/**
	 * gets the program ID for the given event ID
	 * 
	 * @param eventID
	 * @return
	 */
	@Override
	public int getProgramIdByEventId(String eventId) {

		int programId = this.jdbcTemplate.query("SELECT program_id from program where auto_generated_event_id=?",
				new Object[] { eventId }, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
				return 0;
			}
		});

		return programId;
	}

	/**
	 * gets the count of the events available for the given mail and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	@Override
	public int getEventCountByEmail(String email, boolean isAdmin) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		if (!isAdmin) {
			whereCondition.append("coordinator_email=:coordinator_email");
		}
		int count = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT count(program_channel ) + count( CASE WHEN program_channel IS NULL THEN 1 END )	FROM program"
						+ (whereCondition.length() > 0 ? " WHERE " + whereCondition : ""), params, Integer.class);

		return count;
	}

	/**
	 * gets the count of un-categorized events for the given email and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	@Override
	public int getNonCategorizedEventsByEmail(String email, boolean isAdmin) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", email);
		if (!isAdmin) {
			whereCondition.append(" coordinator_email=:coordinator_email");
		}
		int count = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT COUNT(CASE WHEN program_channel='' THEN 1 END)"
						+ " + COUNT(CASE WHEN program_channel IS NULL THEN 1 END) " + " FROM program "
						+ (whereCondition.length() > 0 ? " WHERE " + whereCondition : ""), params, Integer.class);
		return count;
	}

	/**
	 * gets the all the available event categories from the database
	 * 
	 * @return List<String>
	 */
	@Override
	public List<String> getAllEventCategories() {
		try {
			List<String> events = this.jdbcTemplate.queryForList("SELECT distinct name FROM channel", null,
					String.class);
			return events;
		} catch (EmptyResultDataAccessException ex) {
			return new ArrayList<String>();
		}
	}

	/**
	 * gets the count of events for the given email and based on user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventCategory
	 * @return
	 */
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

	/**
	 * gets the count of miscellaneous events for the given email and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventcategories
	 * @return
	 */
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
										+ "AND ( program_channel IS NOT NULL "
										+ "AND program_channel!='') ) "
										+ whereCondition
										: ""), params, Integer.class);
		return count;
	}

	/**
	 * Update the coordinator details in the database after changing the admin
	 * for the event
	 * 
	 * @param eventAdminChangeRequest
	 */
	@Override
	public void updateCoordinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("auto_generated_event_id", eventAdminChangeRequest.getEventId());
			Program program = this.namedParameterJdbcTemplate.queryForObject(
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

	/**
	 * gets the participant for the given seqId and program ID
	 * 
	 * @param seqId
	 * @param programId
	 * @return
	 */
	@Override
	public Participant findParticipantBySeqId(String seqId, int programId) {
		Participant participant;
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("seqId", seqId);
		params.put("programId", programId);
		try {
			participant = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT * FROM participant WHERE seqId=:seqId AND program_id=:programId", params,
					BeanPropertyRowMapper.newInstance(Participant.class));
			if (participant != null && participant.getProgramId() > 0) {
				program = participantRepository.findOnlyProgramById(participant.getProgramId());
			} else {
				program = new Program();
			}
			participant.setProgram(program);
			return participant;

		} catch (EmptyResultDataAccessException ex) {
			// participant=new ParticipantRequest();
			return null;
		}

	}

	@Override
	public Participant findParticipantBySeqIdAndRole(String seqId, int programId, List<String> emailList,String userRole) {
		Participant participant;
		Program program;
		Map<String, Object> params = new HashMap<>();
		params.put("seqId", seqId);
		params.put("programId", programId);

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
		
		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}
		
		if (!userRole.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if (userRole.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)) {
				whereCondition
						.append(" AND ( p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
								+ " OR (p.coordinator_email IN(" + emailString + ") OR pc.email IN(" + emailString + "))) ");
			} else {
				whereCondition.append(" AND (p.coordinator_email IN(" + emailString + ") OR pc.email IN(" + emailString + ")) ");
			}
		}

		try {
			participant = this.namedParameterJdbcTemplate
					.queryForObject(
							"SELECT DISTINCT pr.* FROM participant pr,program p LEFT JOIN program_coordinators pc ON p.program_id = pc.program_id WHERE seqId=:seqId AND pr.program_id=:programId "
									+ whereCondition, params, BeanPropertyRowMapper.newInstance(Participant.class));
			if (participant != null && participant.getProgramId() > 0) {
				program = participantRepository.findOnlyProgramById(participant.getProgramId());
			} else {
				program = new Program();
			}
			participant.setProgram(program);
			return participant;

		} catch (EmptyResultDataAccessException ex) {
			// participant=new ParticipantRequest();
			return null;
		}

	}
	/**
	 * Updates the participant introduced status for the given participant Ids
	 * of the given eventId
	 * 
	 * @param participantIds
	 * @param eventId
	 * @param introduced
	 */
	@Override
	public void updateParticipantsStatus(String participantIds, String eventId, String introduced, String userEmailID) {
		Map<String, Object> params = new HashMap<>();
		params.put("introduced", PMPConstants.REQUIRED_YES.equalsIgnoreCase(introduced) ? 1 : 0);
		params.put("introducedBy", userEmailID);
		params.put("introducedDate", new Date());
		params.put("programId", getProgramIdByEventId(eventId));
		params.put("seqId", participantIds);
		this.namedParameterJdbcTemplate
		.update("UPDATE participant SET introduced=:introduced,introduced_by=:introducedBy,introduction_date=:introducedDate WHERE program_id=:programId AND seqId=:seqId ",
				params);

	}

	/**
	 * gets the count of un-catgorized events for the given email and based on
	 * user role
	 * 
	 * @param username
	 * @param isAdmin
	 * @return List<String>
	 */
	@Override
	public List<String> getNonCategorizedEventListByEmail(String username, boolean isAdmin) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		params.put("coordinator_email", username);
		if (!isAdmin) {
			whereCondition.append("AND coordinator_email=:coordinator_email");
		}
		List<String> UncategorizedEvents = this.namedParameterJdbcTemplate.queryForList("SELECT distinct program_name"
				+ " FROM program"
				+ (whereCondition.length() > 0 ? " WHERE (program_channel IS NULL OR program_channel='') "
						+ whereCondition : ""), params, String.class);
		return UncategorizedEvents;
	}

	/**
	 * gets the all available coordinators from the database
	 * 
	 * @return List<Coordinator>
	 */
	@Override
	public List<Coordinator> getAllCoOrdinatorsList() {

		List<Coordinator> coOrdinators = null;
		Map<String, Object> params = new HashMap<>();
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);

		coOrdinators = this.namedParameterJdbcTemplate
				.query("SELECT DISTINCT * from program WHERE coordinator_email IS NOT NULL AND coordinator_email != '' order by coordinator_email asc",
						sqlParameterSource, BeanPropertyRowMapper.newInstance(Coordinator.class));
		return coOrdinators;

	}

	/**
	 * deletes participant for the given seq Id of the given Event Id
	 * 
	 * @param seqId
	 * @param eventId
	 * @return Participant
	 */
	@Override
	public Participant deleteParticipant(String seqId, String eventId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", getProgramIdByEventId(eventId));
		params.put("seqId", seqId);
		Participant participant = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT * FROM participant WHERE seqId=:seqId AND program_id=:programId", params,
				BeanPropertyRowMapper.newInstance(Participant.class));
		if (null != participant) {
			this.namedParameterJdbcTemplate.update(
					"DELETE FROM `participant` WHERE program_id=:programId AND seqId=:seqId ", params);
			return participant;
		}
		return new Participant();
	}

	/**
	 * updates the deleted participant details in the database
	 * 
	 * @param deletedParticipant
	 * @param deletedBy
	 */
	@Override
	public void updateDeletedParticipant(Participant deletedParticipant, String deletedBy) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("seqId", deletedParticipant.getSeqId());
			BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(deletedParticipant);
			Number newId = this.insertDeletedParticipants.executeAndReturnKey(parameterSource);
			deletedParticipant.setId(newId.intValue());
			if (null != newId) {
				params.put("deleted_by", deletedBy);
				params.put("id", deletedParticipant.getId());
				this.namedParameterJdbcTemplate.update(
						"UPDATE deleted_participants SET deleted_by=:deleted_by WHERE id=:id ", params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getEventIdByProgramID(int programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("program_id", programId);
		try {
			String eventId = this.namedParameterJdbcTemplate.queryForObject(
					"SELECT auto_generated_event_id from program where program_id=:program_id", params,
					BeanPropertyRowMapper.newInstance(String.class));
			return eventId;
		} catch (EmptyResultDataAccessException e) {
			return "";
		}
	}

	@Override
	public int getProgramCount(String userEmail, boolean isAdmin) {
		StringBuilder whereCondition = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		if (!isAdmin) {
			whereCondition.append("coordinator_email=:coordinator_email");
			params.put("coordinator_email", userEmail);
		}
		int programCount = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT count(DISTINCT program_id ) FROM program"
						+ (whereCondition.length() > 0 ? " WHERE " + whereCondition : ""), params, Integer.class);

		return programCount;
	}

	@Override
	public Program getProgramDetailsToGenerateEwelcomeIDById(Integer programId) {
		Map<String, Object> params = new HashMap<>();
		params.put("programId", programId);
		return this.namedParameterJdbcTemplate
				.queryForObject(
						"SELECT program_start_date,auto_generated_event_id,program_id,preceptor_id_card_number,first_sitting_by,coordinator_email From program WHERE program_id=:programId",
						params, BeanPropertyRowMapper.newInstance(Program.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.ProgramRepository#
	 * isEventCoordinatorExistsWithUserEmailId(java.lang.String)
	 */
	@Override
	public boolean isEventCoordinatorExistsWithUserEmailId(String email) {
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		List<String> eventIds = this.namedParameterJdbcTemplate.query(
				"SELECT auto_generated_event_id FROM program WHERE coordinator_email=:email", sqlParameterSource,
				BeanPropertyRowMapper.newInstance(String.class));
		if (eventIds.size() == 0) {
			List<String> programCoordinators = this.namedParameterJdbcTemplate.query(
					"SELECT id FROM program_coordinators WHERE email=:email", sqlParameterSource,
					BeanPropertyRowMapper.newInstance(String.class));
			return (programCoordinators.size() > 0) ? true : false;
		} else {
			return true;
		}
	}

	/**
	 * Method to update the preceptor details after validating the preceptor ID
	 * against MYSRCM.
	 * 
	 * @param program
	 */
	@Override
	public void updatePreceptorDetails(Program program) {
		System.out.println(program.getPreceptorName() + "  " + program.getAbyasiRefNo() + "  "
				+ program.getAbyasiRefNo() + "   " + program.getFirstSittingBy() + "   " + program.getProgramId());
		Map<String, Object> params = new HashMap<>();
		params.put("preceptorName", program.getPreceptorName());
		params.put("abyasiRefNo", program.getAbyasiRefNo());
		params.put("firstSittingBy", program.getFirstSittingBy());
		params.put("programId", program.getProgramId());
		if (0 != program.getProgramId()) {
			this.namedParameterJdbcTemplate.update("UPDATE program SET " + "preceptor_name=:preceptorName,"
					+ "abyasi_ref_no=:abyasiRefNo, " + "first_sitting_by=:firstSittingBy "
					+ "WHERE program_id=:programId", params);
		}

	}

	@Override
	public int getProgramCountWithUserRoleAndEmailId(List<String> emailList, String role) {
		
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder emailString = new StringBuilder("");

		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}

		if (!role.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(role.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append(" (p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) " );
			}else{
				whereCondition.append(" (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) ");
			}
		}


		int programCount = this.jdbcTemplate.queryForObject(
				"SELECT count(DISTINCT p.program_id ) "
						+ " FROM program p LEFT JOIN program_coordinators pc "
						+ " ON p.program_id = pc.program_id "
						+ (whereCondition.length() > 0 ? " WHERE " +  whereCondition : ""), null, Integer.class);

		return programCount;
	}

	@Override
	public List<Program> getEventsByEmailAndRole(List<String> emailList, String role, int offset, int pageSize) {

		List<Program> programs = new ArrayList<Program>();
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder limitCondition = new StringBuilder("");
		StringBuilder emailString = new StringBuilder("");

		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}

		if (!role.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(role.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append(" (p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+ 	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) " );
			}else{
				whereCondition.append(" (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) ");
			}
		}

		if ((offset != 0 && pageSize != 0) || (offset == 0 && pageSize != 0)) {
			limitCondition.append(" LIMIT " + offset + "," + pageSize);
		}

		programs = this.jdbcTemplate.query("SELECT DISTINCT p.program_id,p.program_channel,p.program_start_date,"
				+ "p.program_end_date,p.coordinator_name,p.coordinator_email,p.coordinator_mobile,"
				+ "p.event_place,p.event_city,p.event_state,p.event_country,p.organization_department,"
				+ "p.organization_name,p.organization_web_site,p.organization_contact_name,"
				+ "p.organization_contact_email,p.organization_contact_mobile,p.preceptor_name,"
				+ "p.preceptor_id_card_number,p.welcome_card_signed_by_name,p.welcome_card_signer_Id_card_number,"
				+ "p.remarks,p.auto_generated_event_id,p.auto_generated_intro_id,p.jira_issue_number "
				+ "FROM program p LEFT JOIN program_coordinators pc "
				+ " ON p.program_id = pc.program_id "
				+ (whereCondition.length() > 0 ? " WHERE " +  whereCondition : "")
				+ (limitCondition.length() > 0 ? limitCondition : "")
				, BeanPropertyRowMapper.newInstance(Program.class));

		return programs;
	}

	@Override
	public int getPgrmCountBySrchParamsWithUserRoleAndEmailId(SearchRequest searchRequest, List<String> emailList/*String email*/, String role) {
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder orderBy = new StringBuilder("");
		StringBuilder emailString = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		
		if (!("ALL".equals(searchRequest.getSearchField())) && null != searchRequest.getSearchField()
				&& !searchRequest.getSearchField().isEmpty() ) {
			if (null != searchRequest.getSearchText() && !searchRequest.getSearchText().isEmpty()) {
				whereCondition.append(whereCondition.length() > 0 ? " AND p." + searchRequest.getSearchField()
				+ " LIKE '%" + searchRequest.getSearchText() + "%'" : "p."+searchRequest.getSearchField()
				+ " LIKE '%" + searchRequest.getSearchText() + "%'");
			}
		}
		
		if ((searchRequest.getDateFrom() != null && !searchRequest.getDateFrom().isEmpty())) {
			try {
				whereCondition.append(whereCondition.length() > 0 ? " and p.program_start_date >=:program_start_date "
						: " p.program_start_date >=:program_start_date ");
				params.put("program_start_date", DateUtils.parseToSqlDate(searchRequest.getDateFrom()));
			} catch (ParseException e) {
				LOGGER.error("Error While converting date", e);
			}
		}

		if (searchRequest.getDateTo() != null && !searchRequest.getDateTo().isEmpty()) {
			try {
				whereCondition
				.append(whereCondition.length() > 0 ? " and CASE WHEN p.program_start_date IS NOT NULL THEN p.program_start_date <=:program_end_date ELSE TRUE END "
						: " CASE WHEN p.program_start_date IS NOT NULL THEN p.program_start_date <=:program_end_date ELSE TRUE END ");
				params.put("program_end_date", DateUtils.parseToSqlDate(searchRequest.getDateTo()));
			} catch (ParseException e) {
				LOGGER.error("Error While converting date", e);
			}
		}
		
		if (null != searchRequest.getSortBy() && !searchRequest.getSortBy().isEmpty()) {
			orderBy.append(orderBy.length() > 0 ? ", " + searchRequest.getSortBy() : searchRequest.getSortBy());
			if (null != searchRequest.getSortDirection() && !searchRequest.getSortDirection().isEmpty()) {
				orderBy.append(searchRequest.getSortDirection().equalsIgnoreCase("0") ? " asc" : " desc");
			}
		}
		
		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}
		
		if (!role.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(role.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append( whereCondition.length() > 0 ? " AND (p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) "
						: " (p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
								+	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) ");
			}else{
				whereCondition.append( whereCondition.length() > 0 ? " AND (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) "
						: " (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) ");
			}
		}

		int programCount = this.namedParameterJdbcTemplate.queryForObject(
				"SELECT count(DISTINCT p.program_id ) "
						+ " FROM program p LEFT JOIN program_coordinators pc"
						+ " ON p.program_id = pc.program_id "
						+ (whereCondition.length() > 0 ? " WHERE " +  whereCondition : ""), params, Integer.class);

		return programCount;
	}

	@Override
public List<Program> searchEventsWithUserRoleAndEmailId(SearchRequest searchRequest, List<String> emailList/*String email*/, String role, int offset) {
		
		List<Program> programs = null;
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder orderBy = new StringBuilder("");
		StringBuilder emailString = new StringBuilder("");
		Map<String, Object> params = new HashMap<>();
		
		if (!("ALL".equals(searchRequest.getSearchField())) && null != searchRequest.getSearchField()
				&& !searchRequest.getSearchField().isEmpty()) {
			if (null != searchRequest.getSearchText() && !searchRequest.getSearchText().isEmpty()) {
				whereCondition.append(whereCondition.length() > 0 ? " AND p." + searchRequest.getSearchField()
				+ " LIKE '%" + searchRequest.getSearchText() + "%'" : "p."+searchRequest.getSearchField()
				+ " LIKE '%" + searchRequest.getSearchText() + "%'");
			}
		}
		
		if ((searchRequest.getDateFrom() != null && !searchRequest.getDateFrom().isEmpty())) {
			try {
				whereCondition.append(whereCondition.length() > 0 ? " and p.program_start_date >=:program_start_date "
						: " p.program_start_date >=:program_start_date ");
				params.put("program_start_date", DateUtils.parseToSqlDate(searchRequest.getDateFrom()));
			} catch (ParseException e) {
				LOGGER.error("Error While converting date", e);
			}
		}

		if (searchRequest.getDateTo() != null && !searchRequest.getDateTo().isEmpty()) {
			try {
				whereCondition
				.append(whereCondition.length() > 0 ? " and CASE WHEN p.program_start_date IS NOT NULL THEN p.program_start_date <=:program_end_date ELSE TRUE END "
						: " CASE WHEN p.program_start_date IS NOT NULL THEN p.program_start_date <=:program_end_date ELSE TRUE END ");
				params.put("program_end_date", DateUtils.parseToSqlDate(searchRequest.getDateTo()));
			} catch (ParseException e) {
				LOGGER.error("Error While converting date", e);
			}
		}
		
		if (null != searchRequest.getSortBy() && !searchRequest.getSortBy().isEmpty()) {
			orderBy.append(orderBy.length() > 0 ? ", " + searchRequest.getSortBy() : searchRequest.getSortBy());
			if (null != searchRequest.getSortDirection() && !searchRequest.getSortDirection().isEmpty()) {
				orderBy.append(searchRequest.getSortDirection().equalsIgnoreCase("0") ? " asc" : " desc");
			}
		}
		
		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}
		
		if (!role.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(role.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append( whereCondition.length() > 0 ? " AND (p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) "
						: " (p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
								+	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) ");
			}else{
				whereCondition.append( whereCondition.length() > 0 ? " AND (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) "
						: " (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) ");
			}
		}
		
		programs = this.namedParameterJdbcTemplate.query( "SELECT DISTINCT p.program_id,p.auto_generated_event_id,p.program_channel, "
						+ " p.program_name,p.program_start_date,p.program_end_date, "
						+ " p.coordinator_name,p.coordinator_email,p.coordinator_mobile,"
						+ " p.event_place,p.event_city,p.event_state,p.event_country,p.organization_name,"
						+ " p.organization_department,p.preceptor_name,p.preceptor_id_card_number,p.jira_issue_number"
						+ " FROM program p LEFT JOIN program_coordinators pc"
						+ " ON p.program_id = pc.program_id "
						+ (whereCondition.length() > 0 ? " WHERE " + whereCondition : "")
						+ (orderBy.length() > 0 ? " ORDER BY " + orderBy : "") 
						+ " LIMIT " + offset + "," + searchRequest.getPageSize(), params, BeanPropertyRowMapper.newInstance(Program.class));
		return programs;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#checkExistanceOfAutoGeneratedEventId(java.lang.String)
	 */
	@Override
	public String checkExistanceOfAutoGeneratedEventId(String generatedEventId) {
		String eventIdExistence = this.jdbcTemplate.query(
				"SELECT auto_generated_event_id FROM program WHERE auto_generated_event_id=?",
				new Object[] { generatedEventId }, new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getString(1);
						}
						return null;
					}
				});

		if (null != eventIdExistence) {
			generatedEventId = ExpressionConstants.EVENT_ID_PREFIX + SmsUtil.generateRandomNumber(6);
			return checkExistanceOfAutoGeneratedEventId(generatedEventId);
		} else {
			return generatedEventId;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#checkExistanceOfAutoGeneratedIntroId(java.lang.String)
	 */
	@Override
	public String checkExistanceOfAutoGeneratedIntroId(String generatedIntroId) {

		String introIdExistence = this.jdbcTemplate.query(
				"SELECT auto_generated_intro_id FROM program WHERE auto_generated_intro_id=?",
				new Object[] { generatedIntroId }, new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getString(1);
						}
						return null;
					}
				});
		if (null != introIdExistence) {
			generatedIntroId = ExpressionConstants.INTRO_ID_PREFIX + SmsUtil.generateRandomNumber(7);
			return checkExistanceOfAutoGeneratedIntroId(generatedIntroId);
		} else {
			return generatedIntroId;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#getProgramAndParticipantDetails(java.lang.String)
	 */
	@Override
	public List<String> getProgramAndParticipantDetails(String autoGeneratedEventId) {

		List<String> eventPctptDetails =  null;
		eventPctptDetails = this.jdbcTemplate.query(
				"SELECT pgrm.program_id,pgrm.is_ewelcome_id_generation_disabled,"
						+ " pgrm.coordinator_email "
						+ " FROM program pgrm LEFT JOIN participant pctpt on pgrm.program_id = pctpt.program_id "
						+ " WHERE pgrm.auto_generated_event_id=?",
						new Object[] { autoGeneratedEventId }, new ResultSetExtractor<List<String>>() {
							@Override
							public List<String> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
								List<String> details = new ArrayList<>(3);
								if (resultSet.next()) {
									details.add(String.valueOf(resultSet.getInt(1)));
									details.add(resultSet.getString(2));
									details.add(resultSet.getString(3));
								}
								return details;
							}
						});
		return eventPctptDetails;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#saveProgramPermissionLetterDetails(org.srcm.heartfulness.model.ProgramPermissionLetterdetails)
	 */
	@Override
	public void saveProgramPermissionLetterDetails(ProgramPermissionLetterdetails programPermissionLetterdetails) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(
				programPermissionLetterdetails);
		if (0 == programPermissionLetterdetails.getPermissionLetterId()) {
			int permissionLetterId = this.jdbcTemplate
					.query("SELECT permission_letter_id FROM program_permission_letters WHERE program_id=? AND permission_letter_name=? ",
							new Object[] { programPermissionLetterdetails.getProgramId(),
									programPermissionLetterdetails.getPermissionLetterName() },
							new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException,
						DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});
			programPermissionLetterdetails.setPermissionLetterId(permissionLetterId);
		}

		if (0 == programPermissionLetterdetails.getPermissionLetterId()) {
			this.insertProgramPermissionLetter.executeAndReturnKey(parameterSource);
		} else {
			this.namedParameterJdbcTemplate
			.update("UPDATE program_permission_letters SET permission_letter_name=:permissionLetterName, permission_letter_path=:permissionLetterPath"
					+ ", uploaded_by=:uploadedBy WHERE program_id=:programId AND permission_letter_id=:permissionLetterId", parameterSource);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.repository.ProgramRepository#getListOfPermissionLetters(int)
	 */
	@Override
	public List<ProgramPermissionLetterdetails> getListOfPermissionLetters(int programId) {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("programId", programId);
		return this.namedParameterJdbcTemplate.query(
				"SELECT * FROM program_permission_letters WHERE program_id=:programId", params,
				BeanPropertyRowMapper.newInstance(ProgramPermissionLetterdetails.class));
	}

	/**
	 * This method is used to get the program details 
	 * using email,role of logged in user and auto generated event Id.
	 * @param emailList emails associated with Abhyasi Id for the
	 * logged in person.
	 * @param userRole, role of the logged in user.
	 * @param agEventId, auto generated event Id for a particular event.
	 * @return program details based on emails,role of log in user and 
	 * auto generated event Id.
	 */
	@Override
	public Program getProgramByEmailAndRole(List<String> emailList, String userRole,String agEventId) {
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder emailString = new StringBuilder("");
		Program program = null;
		Map<String, Object> params = new HashMap<>();
		params.put("auto_generated_event_id", agEventId);

		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}

		if (!userRole.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(userRole.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append(" ( p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) " );
			}else{
				whereCondition.append(" (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) ");
			}
		}

		try{
			
			program = this.namedParameterJdbcTemplate.queryForObject("SELECT DISTINCT p.* "
					+ " FROM program p LEFT JOIN program_coordinators pc"
					+ " ON p.program_id = pc.program_id "
					+ " WHERE "
					+ (whereCondition.length() > 0 ? whereCondition +" AND ": "")
					+ " p.auto_generated_event_id=:auto_generated_event_id ", params,
					BeanPropertyRowMapper.newInstance(Program.class));
			
			program.setIsReadOnly(CoordinatorAccessControlConstants.IS_READ_ONLY_FALSE);
		} catch(Exception ex){
			
			program = this.namedParameterJdbcTemplate.queryForObject("SELECT DISTINCT p.* "
					+ " FROM program p LEFT JOIN program_coordinators pc"
					+ " ON p.program_id = pc.program_id "
					+ " WHERE "
					+ " p.auto_generated_event_id=:auto_generated_event_id ", params,
					BeanPropertyRowMapper.newInstance(Program.class));
			program.setIsReadOnly(CoordinatorAccessControlConstants.IS_READ_ONLY_TRUE);
		}
		return program;
	}

	/**
	 * This method is used to get all the program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 * @param emailList List of emails associated witha abhyasi id for the 
	 * logged in user.
	 * @param userRole, role of the logged in user.
	 * @return Map<Integer,String> containing program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 */
	@Override
	public LinkedHashMap<Integer,String> getListOfProgramIdsByEmail(List<String> emailList,String userRole) {
		
		LinkedHashMap<Integer,String> programIds = new LinkedHashMap<Integer,String>();
		StringBuilder whereCondition = new StringBuilder("");
		StringBuilder emailString = new StringBuilder("");

		if(emailList.size() == 1){
			emailString.append("'"+emailList.get(0)+"'");
		}else{
			for(int i = 0; i < emailList.size(); i++){
				emailString.append( i != (emailList.size() -1 ) ? "'"+emailList.get(i)+"'" + ",": "'"+emailList.get(i)+"'");
			}
		}

		if (!userRole.equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN)) {

			if(userRole.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)){
				whereCondition
				.append(" (p.program_channel NOT REGEXP ('G-Connect|G Connect|GConnect|G-Conect|G - Connect|G.CONNECT|G -CONNECT|G- connect|G-Connet|G  Connect')"
						+	" OR (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + "))) " );
			}else{
				whereCondition.append(" (p.coordinator_email IN( " + emailString +") OR pc.email IN("+ emailString + ")) ");
			}
		}
		try{
			programIds = this.jdbcTemplate.query("SELECT p.program_id,p.auto_generated_event_id  "
					+ " FROM program p LEFT JOIN program_coordinators pc "
					+ " ON p.program_id = pc.program_id "
					+ (whereCondition.length() > 0 ? " WHERE " +  whereCondition : ""),
					new Object[] {}, new ResultSetExtractor<LinkedHashMap<Integer,String>>() {
						@Override
						public LinkedHashMap<Integer,String> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							LinkedHashMap<Integer,String> ids = new LinkedHashMap<Integer,String>(); 
							while (resultSet.next()) {
								ids.put(resultSet.getInt(1), resultSet.getString(2));
							}
							return ids;
						}
					});
			
		}catch(Exception ex){
			LOGGER.error("EXCEPTION : while fetching events for the logged in user with email id "+emailList.toString());
		}
		return programIds;
	}
	
	@Override
	public List<Integer> getProgramIdsForSQSPush() {
		return this.jdbcTemplate
				.queryForList(
						"SELECT DISTINCT(pr.program_id)"
								+ " FROM program p,participant pr"
								+ " WHERE p.program_id = pr.program_id"
								+ " AND pr.create_time <= CURRENT_TIMESTAMP"
								+ " AND pr.ewelcome_id_state = 'T'",
								//+ " AND p.sqs_push_status=0",
								null, Integer.class);
	}
	
	/*@Override
	public void updateProgramIdStatus(Integer status, List<Integer> programIds) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("status", status);
		parameters.addValue("programIds", programIds);
		this.namedParameterJdbcTemplate.update("UPDATE program set sqs_push_status=:status WHERE program_id IN (:programIds) ", parameters);
	}*/
	
	@Override
	public void saveUploadedFiles(UploadedFiles uploadFiles) {
		BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(uploadFiles);
		if(uploadFiles.getId()==0){
			Number id = this.insertUploadedFiles.executeAndReturnKey(source);
			uploadFiles.setId(id.intValue());
		}else{
			this.namedParameterJdbcTemplate.update("UPDATE uploaded_files set status =:status where id=:id ", source);
		}
		
	}

}
