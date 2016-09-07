/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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

	private SimpleJdbcInsert insertPmpAPIAccessLog;
	private SimpleJdbcInsert insertPmpAPIAccessLogDetails;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public APIAccessLogrepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.insertPmpAPIAccessLog = new SimpleJdbcInsert(dataSource).withTableName("pmp_api_access_log")
				.usingGeneratedKeyColumns("id");
		this.insertPmpAPIAccessLogDetails = new SimpleJdbcInsert(dataSource).withTableName("pmp_api_access_log_details")
				.usingGeneratedKeyColumns("id");

	}

	/**
	 * Service to persist/update the PMP API request and response information <PMPAPIAccessLog> in the DB.
	 * @param accessLog
	 * @return  accessLogId
	 */
	@Override
	public int createOrUpdatePmpAPIAccessLog(PMPAPIAccessLog accessLog) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(accessLog);
		if (accessLog.getId() == 0) {
			Number id = this.insertPmpAPIAccessLog.executeAndReturnKey(parameterSource);
			accessLog.setId(id.intValue());
		}else {
			this.namedParameterJdbcTemplate.update("UPDATE pmp_api_access_log SET "
					+  	"username=:username, "
					+ 	"ip_address=:ipAddress, "
					+	"api_name=:apiName, " 
					+  	"total_requested_time=:totalRequestedTime, " 
					+  	"total_response_time=:totalResponseTime, "
					+   "status=:status, "
					+ 	"error_message=:errorMessage, "
					+ 	"request_body=:requestBody, "
					+ 	"response_body=:responseBody "
					+ 	"WHERE id=:id", parameterSource);
		}
		return accessLog.getId();
	}

	/**
	 * Service to persist/update the MySRCM API request and response information <PMPAPIAccessLog> in the DB.
	 * @param accessLog
	 * @return  accessLogId
	 */
	@Override
	public int createOrUpdatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(accessLogDetails);
		if (accessLogDetails.getId() == 0) {
			Number id = this.insertPmpAPIAccessLogDetails.executeAndReturnKey(parameterSource);
			accessLogDetails.setId(id.intValue());
		}else {
			this.namedParameterJdbcTemplate.update("UPDATE pmp_api_access_log_details SET "
					+	"pmp_access_log_id=:pmpAccessLogId, "
					+  	"endpoint=:endpoint, "
					+  	"requested_time=:requestedTime, " 
					+  	"response_time=:responseTime, "
					+   "status=:status, "
					+ 	"error_message=:errorMessage, "
					+ 	"request_body=:requestBody, "
					+ 	"response_body=:responseBody "
					+ 	"WHERE id=:id", parameterSource);
		}
		return accessLogDetails.getId();
	}

}
