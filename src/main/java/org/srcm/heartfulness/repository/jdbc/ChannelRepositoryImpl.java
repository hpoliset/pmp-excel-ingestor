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
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.repository.ChannelRepository;

@Repository
public class ChannelRepositoryImpl implements ChannelRepository{

	private final SimpleJdbcInsert insertChannel;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public ChannelRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertChannel = new SimpleJdbcInsert(dataSource)
                .withTableName("channel")
                .usingGeneratedKeyColumns("id");
    }
    
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

}
