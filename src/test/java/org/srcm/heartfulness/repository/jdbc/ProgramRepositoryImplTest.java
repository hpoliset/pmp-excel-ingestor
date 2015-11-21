package org.srcm.heartfulness.repository.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.srcm.heartfulness.PmpApplication;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ProgramRepository;

import java.util.Date;

/**
 * Created by vsonnathi on 11/18/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PmpApplication.class)
public class ProgramRepositoryImplTest {

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void testInsertProgram() {
        Program program = createProgram();

        programRepository.save(program);
        Assert.assertNotEquals("Incorrect Id => Not able save?", 0, program.getProgramId());



//         delete the new created row from the database
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "program", "program_id=?", program.getProgramId());
    }

    @Test
    public void testUpdateProgram() {
        Program program = createProgram();

        programRepository.save(program);
        Assert.assertNotEquals("Incorrect Id => Not able save?", 0, program.getProgramId());

        Program newProgram = programRepository.findById(program.getProgramId());
        Assert.assertNotNull("Could not find newly created object", newProgram);

        String updatedRemarks = program.getRemarks() + " Updated";
        program.setRemarks(updatedRemarks);

        String updatedWelcomeCardSignedByName = program.getWelcomeCardSignedByName() + " Updated";
        program.setWelcomeCardSignedByName(updatedWelcomeCardSignedByName);

        String updatedCoordinatorMobile = "9840-49-2831";
        program.setCoordinatorMobile(updatedCoordinatorMobile);

        // Update.
        programRepository.save(program);

        // Read
        Program updatedProgram = programRepository.findById(program.getProgramId());

        Assert.assertEquals("Remarks not updated", updatedRemarks, updatedProgram.getRemarks());
        Assert.assertEquals("WelcomeCardSignedByName not updated", updatedWelcomeCardSignedByName,
                updatedProgram.getWelcomeCardSignedByName());
        Assert.assertEquals("CoordinatorMobile not updated", updatedCoordinatorMobile,
                updatedProgram.getCoordinatorMobile());

        // Clean up the new created/updated row.
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "program", "program_id=?", program.getProgramId());
    }

    private Program createProgram() {
        Program program = new Program();
        program.setProgramChannel("U Connect");
        program.setEventCountry("India");
        program.setEventCity("Bangalore");
        program.setEventPlace("MESKK College, Malleswaram");
        program.setCoordinatorName("Saroja Agarwal");
        program.setCoordinatorMobile("9845-11-2345");

        program.setOrganizationDepartment("Science Department");
        program.setOrganizationName("MES College");
        program.setOrganizationWebSite("www.meskk.org");

        program.setPreceptorName("Bhaskar Sharma");
        program.setPreceptorIdCardNumber("INBHSA123");

        program.setProgramStartDate(new Date());
        program.setProgramEndDate(new Date());
        program.setEventState("Karnataka");

        program.setCoordinatorEmail("sa928367@gmail.com");

        program.setOrganizationContactName("Shri Krishna Rao");
        program.setOrganizationContactEmail("krishna.rao3678@gmail.com");
        program.setOrganizationContactMobile("9836-21-4567");

        program.setWelcomeCardSignedByName("Arun Bose");
        program.setWelcomeCardSignerIdCardNumber("INARBS345");

        program.setRemarks("Attended BSC, MSC 1st year students");
        return program;
    }


}
