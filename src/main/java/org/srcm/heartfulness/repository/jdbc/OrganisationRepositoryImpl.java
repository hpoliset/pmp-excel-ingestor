package org.srcm.heartfulness.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.repository.OrganisationRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Organisation Repository JDBC implementation to manage persistence <code>Organisation</code> domain objects.
 *
 * Created by vsonnathi on 11/28/15.
 */
@Repository
public class OrganisationRepositoryImpl implements OrganisationRepository {

    private final SimpleJdbcInsert insertOrganization;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public OrganisationRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertOrganization = new SimpleJdbcInsert(dataSource)
                .withTableName("organisation")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Organisation findByNameAndWebsite(String name, String website) {
        return jdbcTemplate.queryForObject("SELECT * FROM organisation WHERE name=? AND web_site=?",
                new Object[]{name, website}, Organisation.class);
    }

    @Override
    public Organisation findById(int organizationId) {
        return jdbcTemplate.queryForObject("SELECT * FROM organisation WHERE id=?",
                new Object[]{organizationId}, BeanPropertyRowMapper.newInstance(Organisation.class));
    }

    @Override
    public void save(Organisation organisation) {
        if (organisation.getId() == 0) {
            int organizationId = jdbcTemplate.query(
                    "SELECT id from organisation where name=? AND web_site=?",
                    new Object[]{organisation.getName(), organisation.getWebSite()},
                    new ResultSetExtractor<Integer>() {
                        @Override
                        public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                            if (resultSet.next()) {
                                return resultSet.getInt(1);
                            }
                            return 0;
                        }
                    });

            if (organizationId > 0) {
                organisation.setId(organizationId);
            }
        }

        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(organisation);
        if (organisation.getId() == 0 ) {
            Number newId = this.insertOrganization.executeAndReturnKey(source);
            organisation.setId(newId.intValue());
        } else {
            namedParameterJdbcTemplate.update(
                    "UPDATE organisation SET " +
                            "name=:name, " +
                            "contact_name=:contactName," +
                            "email=:email," +
                            "web_site=:webSite," +
                            "phone=phone," +
                            "address_line1=:addressLine1," +
                            "address_line2=:addressLine2," +
                            "city=:city," +
                            "state=:state," +
                            "zip=:zip," +
                            "country=:country " +
                            "WHERE id=:id ", source
            );
        }
    }
}
