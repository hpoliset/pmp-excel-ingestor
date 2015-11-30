package org.srcm.heartfulness.repository.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.srcm.heartfulness.PmpApplication;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.repository.OrganisationRepository;

/**
 * Created by vsonnathi on 11/28/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PmpApplication.class)
public class OrganisationRepositoryImplTest {

    static final Logger LOGGER = LoggerFactory.getLogger(OrganisationRepositoryImplTest.class);

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void testInsertOrganization() {
        Organisation organisation = createOrganization();
        organisationRepository.save(organisation);
        Assert.assertNotEquals("Incorrect Id => Not able save?", 0, organisation.getId());

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "organisation", "id=?", organisation.getId());
    }


    @Test
    public void testUpdateOrganization() {
        Organisation organisation = createOrganization();
        organisationRepository.save(organisation);
        Assert.assertNotEquals("Incorrect Id => Not able save?", 0, organisation.getId());

        Organisation newOrganisation = organisationRepository.findById(organisation.getId());
        Assert.assertNotNull("Could not find newly created object", newOrganisation);

        String updatedName = newOrganisation.getName() + " updated";
        newOrganisation.setName(updatedName);

        String updatedWebSite = newOrganisation.getWebSite() + " updated";
        newOrganisation.setWebSite(updatedWebSite);
        organisationRepository.save(newOrganisation);

        Organisation updatedOrganisation = organisationRepository.findById(newOrganisation.getId());

        Assert.assertEquals("Name not updated", updatedName, updatedOrganisation.getName());
        Assert.assertEquals("WebSite not updated", updatedWebSite, updatedOrganisation.getWebSite());

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "organisation", "id=?", newOrganisation.getId());
    }

    @Test
    public void testInvalidId() {
        int count;
        try {
            count = jdbcTemplate.queryForObject("SELECT id FROM organisation where id=?", new Object[]{123},
                    Integer.class);
        } catch (DataAccessException e) {
//            e.printStackTrace();
            count = 0;
        }
        LOGGER.info("Count is:" + count);
    }

    private Organisation createOrganization() {
        Organisation organisation = new Organisation();
        organisation.setName("MES College");
        organisation.setContactName("Shri Krishna Rao");
        organisation.setEmail("test@mes.com");
        organisation.setWebSite("www.meskk.org");
        organisation.setPhone("1234569870");

        return organisation;
    }

}