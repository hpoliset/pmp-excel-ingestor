package org.srcm.heartfulness.repository.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.repository.ChannelRepository;

/**
 * Repository Implementation for managing <code>Channel</code> domain objects.
 * 
 * @author rramesh
 *
 */
@Repository
public class ChannelRepositoryImpl implements ChannelRepository {

	private final SimpleJdbcInsert insertChannel;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public ChannelRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.insertChannel = new SimpleJdbcInsert(dataSource).withTableName("channel").usingGeneratedKeyColumns("id");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ChannelRepository#findAllActiveChannels
	 * ()
	 */
	@Override
	public List<Channel> findAllActiveChannels() {
		Map<String, Object> params = new HashMap<>();
		params.put("active", 1);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		List<Channel> listOfChannels = this.namedParameterJdbcTemplate.query(
				"SELECT * FROM channel WHERE active=:active", sqlParameterSource,
				BeanPropertyRowMapper.newInstance(Channel.class));
		return listOfChannels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.repository.ChannelRepository#findAllActiveChannelNames
	 * ()
	 */
	@Override
	public List<String> findAllActiveChannelNames() {
		Map<String, Object> params = new HashMap<>();
		params.put("active", 1);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		List<String> listOfChannels = this.namedParameterJdbcTemplate.queryForList(
				"SELECT name FROM channel WHERE active=:active", sqlParameterSource, String.class);
		return listOfChannels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.repository.ChannelRepository#
	 * findAllActiveChannelsBasedOnRole(java.lang.String)
	 */
	@Override
	public List<Channel> findAllActiveChannelsBasedOnRole(String role) {

		Map<String, Object> params = new HashMap<>();
		params.put("active", 1);
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		StringBuilder whereCondition = new StringBuilder("");
		whereCondition.append("active=:active");
		if (!role.equalsIgnoreCase(PMPConstants.ROLE_PREFIX + PMPConstants.LOGIN_GCONNECT_ADMIN)) {
			whereCondition.append(whereCondition.length() > 0 ? " AND (name NOT LIKE '%G-Connect%' ) "
					: "(name NOT LIKE '%G-Connect%' )");
		}
		List<Channel> listOfChannels = this.namedParameterJdbcTemplate.query(
				"SELECT * FROM channel" + (whereCondition.length() > 0 ? " WHERE " + whereCondition : ""),
				sqlParameterSource, BeanPropertyRowMapper.newInstance(Channel.class));
		return listOfChannels;
	}

}
