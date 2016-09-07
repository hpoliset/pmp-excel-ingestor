/**
 * 
 */
package org.srcm.heartfulness.repository.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.repository.MailLogRepository;

/**
 * @author Koustav Dutta
 *
 */
@Repository
public class MailLogRepositoryImpl implements MailLogRepository {
	
	private SimpleJdbcInsert insertpmpMailLog;
	
	@Autowired
	public MailLogRepositoryImpl(DataSource dataSource) {
		this.insertpmpMailLog = new SimpleJdbcInsert(dataSource).withTableName("pmp_email_log")
				.usingGeneratedKeyColumns("id");
	}
	
	
	/**
	 * Repository class to persist the email information sent from PMP  <PMPMailLog> in the DB.
	 * @param pmpMailLog
	 */
	
	@Override
	public void createMailLog(PMPMailLog pmpMailLog) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(pmpMailLog);
		if (pmpMailLog.getId() == 0) {
				this.insertpmpMailLog.executeAndReturnKey(parameterSource);
		}
		
	}
	

}
