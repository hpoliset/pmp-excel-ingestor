/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.APIAccessLogDetails;
import org.srcm.heartfulness.repository.APIAccesslogRepository;

/**
 * @author himasreev
 *
 */
@Repository
public class APIAccessLogrepositoryImpl implements APIAccesslogRepository {
	
	private SimpleJdbcInsert insertLogDetails;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public APIAccessLogrepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.insertLogDetails = new SimpleJdbcInsert(dataSource).withTableName("api_access_log").usingGeneratedKeyColumns("id");
	}

	@Override
	public void saveAccessLogData(APIAccessLogDetails logDetails) {
		
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(logDetails);
		if (logDetails.getId() == 0) {
			Number newId = this.insertLogDetails.executeAndReturnKey(parameterSource);
			logDetails.setId(newId.intValue());
		} /*else {
			this.namedParameterJdbcTemplate.update("UPDATE api_access_log SET " + "username=:userName, "
					+ "ip_address=:ipAddress, " + "api_name=:apiName, " + "request_time=:requestTime, "
					+ "response_time=:responseTime " + "WHERE id=:id", parameterSource);
		}*/
	}

}
