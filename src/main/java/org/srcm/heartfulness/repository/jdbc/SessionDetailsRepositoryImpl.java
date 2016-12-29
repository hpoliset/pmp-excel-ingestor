/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.helper.SessionDetailsHelper;
import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.SessionImageDetails;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.SuccessResponse;
import org.srcm.heartfulness.repository.SessionDetailsRepository;

/**
 * @author Koustav Dutta
 *
 */
@Repository
public class SessionDetailsRepositoryImpl implements SessionDetailsRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDetailsRepositoryImpl.class);

	private final JdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert saveSessionDetails;
	private SimpleJdbcInsert saveSessionImages;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	SessionDetailsHelper sessionDtlsHlpr;

	@Autowired
	public SessionDetailsRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.saveSessionDetails = new SimpleJdbcInsert(dataSource).withTableName("session_details")
				.usingGeneratedKeyColumns("session_id");
		this.saveSessionImages = new SimpleJdbcInsert(dataSource).withTableName("session_images")
				.usingGeneratedKeyColumns("image_id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SessionDetailsRepository#
	 * saveOrUpdateSessionDetails(org.srcm.heartfulness.model.SessionDetails)
	 */
	@Override
	public PMPResponse saveOrUpdateSessionDetails(SessionDetails sessionDetails) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(sessionDetails);
		String autoGnrtdSessionId = "";

		if (null == sessionDetails.getAutoGeneratedSessionId() || sessionDetails.getAutoGeneratedSessionId().isEmpty()) {
			while (true) {
				autoGnrtdSessionId = "S" + sessionDtlsHlpr.generateSessionId(6);
				int sessionIdCount = checkAutoGnrtdSessionIdAlreadyExists(autoGnrtdSessionId);
				if (sessionIdCount == 0) {
					break;
				}
			}
			sessionDetails.setAutoGeneratedSessionId(autoGnrtdSessionId);
		} else {
			int sessionId = getSessionId(sessionDetails.getAutoGeneratedSessionId());
			if (sessionId == -1 || sessionId == 0) {
				return new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_SESSION_ID);
			}
			sessionDetails.setSessionId(sessionId);
		}
		if (sessionDetails.getSessionId() == 0) {
			this.saveSessionDetails.executeAndReturnKey(parameterSource);
			return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, ErrorConstants.SESSION_SUCCESSFULLY_CREATED);
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE session_details SET " + "session_number=:sessionNumber, "
					+ "session_date=:sessionDate, " + "number_of_participants=:numberOfParticipants, "
					+ "number_of_new_participants=:numberOfNewParticipants, " + "topic_covered=:topicCovered, "
					+ "preceptor_name=:preceptorName, " + "preceptor_id_card_no=:preceptorIdCardNo, "
					+ "comments=:comments " + "WHERE auto_generated_session_id=:autoGeneratedSessionId"
					+ " AND program_id=:programId", parameterSource);

			return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, ErrorConstants.SESSION_SUCCESSFULLY_UPDATED);
		}
	}

	/**
	 * 
	 * @param autoGnrtdSessionId
	 * @return
	 */
	private int checkAutoGnrtdSessionIdAlreadyExists(String autoGnrtdSessionId) {
		int sessionIdCount = 0;
		try {
			sessionIdCount = this.jdbcTemplate.query(
					"SELECT count(session_id) from session_details where auto_generated_session_id=?",
					new Object[] { autoGnrtdSessionId }, new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});
		} catch (DataAccessException ex) {
			LOGGER.error("DAE Failed to get count of already generated session id for auto generated session id "
					+ autoGnrtdSessionId);
			return 0;
		} catch (Exception ex) {
			LOGGER.error("Ex Failed to get count of already generated session id for auto generated session id "
					+ autoGnrtdSessionId);
			return -1;
		}
		return sessionIdCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SessionDetailsRepository#getSessionId
	 * (java.lang.String)
	 */
	@Override
	public int getSessionId(String autoGnrtdSessionId) {
		int sessionId = 0;
		try {
			sessionId = this.jdbcTemplate.query(
					"SELECT session_id from session_details where auto_generated_session_id=?",
					new Object[] { autoGnrtdSessionId }, new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});
		} catch (DataAccessException ex) {
			LOGGER.error("DAE Error while fetching session id for auto generated session id :" + autoGnrtdSessionId);
			return 0;
		} catch (Exception ex) {
			LOGGER.error("EX Error while fetching session id for auto generated session id :" + autoGnrtdSessionId);
			LOGGER.error("EX Error while fetching session id ", ex);
			return -1;
		}
		return sessionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SessionDetailsRepository#deleteSessionDetail
	 * (org.srcm.heartfulness.model.SessionDetails)
	 */
	@Override
	public int deleteSessionDetail(SessionDetails sessionDetails) {

		int isUpdated = this.jdbcTemplate.update("UPDATE session_details set "
				+ "is_deleted = 1 WHERE auto_generated_session_id=? AND program_id=?",
				new Object[] { sessionDetails.getAutoGeneratedSessionId(), sessionDetails.getProgramId() });
		return isUpdated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SessionDetailsRepository#getSessionDetails
	 * (int)
	 */
	@Override
	public List<SessionDetails> getSessionDetails(int programId) {
		List<SessionDetails> sessionList = null;
		sessionList = this.jdbcTemplate.query("SELECT auto_generated_session_id,"
				+ "session_number,session_date,number_of_participants,"
				+ "number_of_new_participants,topic_covered,preceptor_name,"
				+ "preceptor_id_card_no,comments from session_details " + "WHERE is_deleted = 0 AND program_id = ?",
				new Object[] { programId }, new ResultSetExtractor<List<SessionDetails>>() {
					@Override
					public List<SessionDetails> extractData(ResultSet resultSet) throws SQLException,
							DataAccessException {
						List<SessionDetails> sessionDtlsList = new ArrayList<SessionDetails>();
						while (resultSet.next()) {
							SessionDetails session = new SessionDetails();
							session.setAutoGeneratedSessionId(resultSet.getString(1));
							session.setSessionNumber(resultSet.getString(2));
							session.setSessionDate(resultSet.getDate(3));
							session.setNumberOfParticipants(resultSet.getInt(4));
							session.setNumberOfNewParticipants(resultSet.getInt(5));
							session.setTopicCovered(resultSet.getString(6));
							session.setPreceptorName(resultSet.getString(7));
							session.setPreceptorIdCardNo(resultSet.getString(8));
							session.setComments(resultSet.getString(9));
							sessionDtlsList.add(session);
						}
						return sessionDtlsList;
					}
				});
		return sessionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SessionDetailsRepository#
	 * getSessionDetailsIdBySessionIdandProgramId(java.lang.String, int)
	 */
	@Override
	public int getSessionDetailsIdBySessionIdandProgramId(String sessionId, int programId) {
		int sessionDetailsId = this.jdbcTemplate.query(
				"SELECT session_id from session_details where program_id=? AND auto_generated_session_id=?",
				new Object[] { programId, sessionId }, new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getInt(1);
						}
						return 0;
					}
				});

		return sessionDetailsId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.SessionDetailsRepository#saveSessionFiles
	 * (org.srcm.heartfulness.model.SessionImageDetails)
	 */
	@Override
	public void saveSessionFiles(SessionImageDetails sessionFiles) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(sessionFiles);
		if (sessionFiles.getImageId() == 0) {
			int sessionImageDetailsId = this.jdbcTemplate.query(
					"SELECT image_id from session_images where session_id=? and image_path=?", new Object[] {
							sessionFiles.getSessionId(), sessionFiles.getImagePath() },
					new ResultSetExtractor<Integer>() {
						@Override
						public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
							if (resultSet.next()) {
								return resultSet.getInt(1);
							}
							return 0;
						}
					});
			sessionFiles.setImageId(sessionImageDetailsId);
		}
		if (sessionFiles.getImageId() == 0) {
			this.saveSessionImages.executeAndReturnKey(parameterSource);
		} else {
			this.namedParameterJdbcTemplate.update("UPDATE session_images SET " + "image_name=:imageName, "
					+ "image_path=:imagePath, " + "uploaded_by=:uploadedBy" + "WHERE image_id=:imageId",
					parameterSource);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SessionDetailsRepository#
	 * getCountOfSessionImages(int)
	 */
	@Override
	public int getCountOfSessionImages(int sessionDetailsId) {
		return this.jdbcTemplate.query("SELECT count(image_id) FROM session_images WHERE session_id= ?",
				new Object[] { sessionDetailsId }, new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						if (resultSet.next()) {
							return resultSet.getInt(1);
						}
						return 0;
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.SessionDetailsRepository#
	 * getListOfSessionImages(int)
	 */
	@Override
	public List<SessionImageDetails> getListOfSessionImages(int sessionDetailsId) {
		Map<String, Object> params = new HashMap<>();
		params.put("sessionId", sessionDetailsId);
		return this.namedParameterJdbcTemplate.query("Select * from session_images WHERE session_id=:sessionId",
				params, BeanPropertyRowMapper.newInstance(SessionImageDetails.class));
	}
}
