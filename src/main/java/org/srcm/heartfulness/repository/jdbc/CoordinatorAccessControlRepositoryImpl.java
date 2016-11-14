package org.srcm.heartfulness.repository.jdbc;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.repository.CoordinatorAccessControlRepository;

/**
 * 
 * @author himasreev
 *
 */
@Service
public class CoordinatorAccessControlRepositoryImpl implements CoordinatorAccessControlRepository {
	
	private static Logger LOGGER = LoggerFactory.getLogger(CoordinatorAccessControlRepositoryImpl.class);

	private final JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private SimpleJdbcInsert insertProgramCoordinators;
	
	@Autowired
	public CoordinatorAccessControlRepositoryImpl(DataSource dataSource) {
		this.insertProgramCoordinators=new SimpleJdbcInsert(dataSource).withTableName("program_coordinators")
				.usingGeneratedKeyColumns("id");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void savecoordinatorDetails(ProgramCoordinators programCoordinators) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(programCoordinators);
		if (programCoordinators.getId() == 0) {
			Number newId = this.insertProgramCoordinators.executeAndReturnKey(parameterSource);
			programCoordinators.setId(newId.intValue());
		}
		
	}

}
