/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.repository.APIAccesslogRepository;

/**
 * @author himasreev
 *
 */
@Repository
public class APIAccessLogrepositoryImpl implements APIAccesslogRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(APIAccessLogrepositoryImpl.class);

	private SimpleJdbcInsert insertPmpAPIAccessLog;
	private SimpleJdbcInsert insertPmpAPIAccessLogDetails;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public APIAccessLogrepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.insertPmpAPIAccessLog = new SimpleJdbcInsert(dataSource).withTableName("pmp_api_access_log")
				.usingGeneratedKeyColumns("id");
		this.insertPmpAPIAccessLogDetails = new SimpleJdbcInsert(dataSource)
				.withTableName("pmp_api_access_log_details").usingGeneratedKeyColumns("id");
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	/**
	 * Service to persist/update the PMP API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	@Override
	public int createOrUpdatePmpAPIAccessLog(PMPAPIAccessLog accessLog) {
		try {
			BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(accessLog);
			if (accessLog.getId() == 0) {
				Number id = this.insertPmpAPIAccessLog.executeAndReturnKey(parameterSource);
				accessLog.setId(id.intValue());
			} else {
				this.namedParameterJdbcTemplate.update("UPDATE pmp_api_access_log SET " + "username=:username, "
						+ "ip_address=:ipAddress, " + "api_name=:apiName, "
						+ "total_requested_time=:totalRequestedTime, " + "total_response_time=:totalResponseTime, "
						+ "status=:status, " + "error_message=:errorMessage, " + "request_body=:requestBody, "
						+ "response_body=:responseBody " + "WHERE id=:id", parameterSource);
			}
			return accessLog.getId();
		} catch (DataAccessException daex) {
			LOGGER.error("Failed to create/update PMP API Access Log {}", daex);
			return 0;
		} catch (Exception ex) {
			LOGGER.error("Failed to create/update PMP API Access Log {}", ex);
			return 0;
		}
	}

	/**
	 * Service to persist/update the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	@Override
	public int createOrUpdatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails) {

		try {
			BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(accessLogDetails);
			if (accessLogDetails.getId() == 0) {
				Number id = this.insertPmpAPIAccessLogDetails.executeAndReturnKey(parameterSource);
				accessLogDetails.setId(id.intValue());
			} else {
				this.namedParameterJdbcTemplate.update("UPDATE pmp_api_access_log_details SET "
						+ "pmp_access_log_id=:pmpAccessLogId, " + "endpoint=:endpoint, "
						+ "requested_time=:requestedTime, " + "response_time=:responseTime, " + "status=:status, "
						+ "error_message=:errorMessage, " + "request_body=:requestBody, "
						+ "response_body=:responseBody " + "WHERE id=:id", parameterSource);
			}
			return accessLogDetails.getId();
		} catch (DataAccessException daex) {
			LOGGER.error("Failed to create/update PMP API Access Log Details {}", daex);
			return 0;
		} catch (Exception ex) {
			LOGGER.error("Failed to create/update PMP API Access Log Details {}", ex);
			return 0;
		}

	}

	@Override
	public List<PMPAPIAccessLog> fetchPmpApiAccessLogData() {
		List<PMPAPIAccessLog> accessLogData = this.jdbcTemplate.query("SELECT id,username,"
				+ "ip_address,api_name,total_requested_time,total_response_time," + "status FROM pmp_api_access_log",
				new Object[] {}, new ResultSetExtractor<List<PMPAPIAccessLog>>() {
					@Override
					public List<PMPAPIAccessLog> extractData(ResultSet resultSet) throws SQLException,
							DataAccessException {
						List<PMPAPIAccessLog> logData = new ArrayList<PMPAPIAccessLog>();
						while (resultSet.next()) {
							PMPAPIAccessLog log = new PMPAPIAccessLog(resultSet.getInt(1), resultSet.getString(2),
									resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet
											.getString(6), resultSet.getString(7));
							logData.add(log);
						}
						return logData;
					}
				});
		return accessLogData;
	}

	@Override
	public List<PMPAPIAccessLog> fetchPmpApiAccessErrorLogData(String accessLogId) {
		List<PMPAPIAccessLog> accessLogData = this.jdbcTemplate.query(
				"SELECT error_message,request_body,response_body FROM pmp_api_access_log " + "WHERE id = ?",
				new Object[] { accessLogId }, new ResultSetExtractor<List<PMPAPIAccessLog>>() {
					@Override
					public List<PMPAPIAccessLog> extractData(ResultSet resultSet) throws SQLException,
							DataAccessException {
						List<PMPAPIAccessLog> logData = new ArrayList<PMPAPIAccessLog>();
						while (resultSet.next()) {
							PMPAPIAccessLog log = new PMPAPIAccessLog();
							log.setErrorMessage(resultSet.getString(1));
							log.setRequestBody(resultSet.getString(2));
							log.setResponseBody(resultSet.getString(3));
							logData.add(log);
						}
						return logData;
					}
				});
		return accessLogData;
	}

	@Override
	public List<PMPAPIAccessLogDetails> fetchPmpApiLogDetailsData(String accessLogId) {
		List<PMPAPIAccessLogDetails> logDetails = null;
		logDetails = this.jdbcTemplate.query("SELECT id,endpoint,requested_time,"
				+ "response_time,status FROM pmp_api_access_log_details" + " WHERE pmp_access_log_id = ?",
				new Object[] { accessLogId }, new ResultSetExtractor<List<PMPAPIAccessLogDetails>>() {
					@Override
					public List<PMPAPIAccessLogDetails> extractData(ResultSet resultSet) throws SQLException,
							DataAccessException {
						List<PMPAPIAccessLogDetails> logDetailsData = new ArrayList<PMPAPIAccessLogDetails>();
						while (resultSet.next()) {
							PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails();
							accessLogDetails.setId(resultSet.getInt(1));
							accessLogDetails.setEndpoint(resultSet.getString(2));
							accessLogDetails.setRequestedTime(resultSet.getString(3));
							accessLogDetails.setResponseTime(resultSet.getString(4));
							accessLogDetails.setStatus(resultSet.getString(5));
							logDetailsData.add(accessLogDetails);
						}
						return logDetailsData;
					}
				});
		return logDetails;
	}

	@Override
	public List<PMPAPIAccessLogDetails> fetchPmpApiErrorLogDetailsData(String logDetailsId) {
		List<PMPAPIAccessLogDetails> logDetails = null;
		logDetails = this.jdbcTemplate.query(
				"SELECT error_message,request_body,response_body FROM pmp_api_access_log_details" + " WHERE id = ?",
				new Object[] { logDetailsId }, new ResultSetExtractor<List<PMPAPIAccessLogDetails>>() {
					@Override
					public List<PMPAPIAccessLogDetails> extractData(ResultSet resultSet) throws SQLException,
							DataAccessException {
						List<PMPAPIAccessLogDetails> logDetailsData = new ArrayList<PMPAPIAccessLogDetails>();
						while (resultSet.next()) {
							PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails();
							accessLogDetails.setErrorMessage(resultSet.getString(1));
							accessLogDetails.setRequestBody(resultSet.getString(2));
							accessLogDetails.setResponseBody(resultSet.getString(3));
							logDetailsData.add(accessLogDetails);
						}
						return logDetailsData;
					}
				});
		return logDetails;
	}

}
