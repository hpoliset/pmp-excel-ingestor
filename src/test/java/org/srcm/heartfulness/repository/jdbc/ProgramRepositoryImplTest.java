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
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;

import java.util.Date;

/**
 * Test class for ProgramRepository JDBC implementation.
 *
 * Created by vsonnathi on 11/18/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PmpApplication.class)
public class ProgramRepositoryImplTest {

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    ParticipantRepository participantRepository;

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
    public void testInsertParticipant() {
        Participant participant = createPartcipant();

        participantRepository.save(participant);
        Assert.assertNotEquals("Incorrect Id => Not able save?", 0, participant.getId());

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "participant", "id=?", participant.getId());
    }

    @Test
    public void testUpdateProgram() {
        Program program = createProgram();

        programRepository.save(program);
        Assert.assertNotEquals("Incorrect Id => Not able save?", 0, program.getProgramId());

        Program newProgram = programRepository.findById(program.getProgramId());
        Assert.assertNotNull("Could not find newly created object", newProgram);

        String updatedRemarks = program.getRemarks() + " Updated";
        newProgram.setRemarks(updatedRemarks);

        String updatedWelcomeCardSignedByName = program.getWelcomeCardSignedByName() + " Updated";
        newProgram.setWelcomeCardSignedByName(updatedWelcomeCardSignedByName);

        String updatedCoordinatorMobile = "9840-49-2831";
        newProgram.setCoordinatorMobile(updatedCoordinatorMobile);

        // Update.
        programRepository.save(newProgram);

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

    @Test
    public void testUpdateParticipant() {
        Participant participant = createPartcipant();
        Program program = createProgram();

        programRepository.save(program);
        participant.setProgram(program);

        participantRepository.save(participant);
        Assert.assertNotEquals("Incorrect Id => Not able save?", 0, participant.getId());

        Participant newParticipant = participantRepository.findById(participant.getId());
        Assert.assertNotNull("Could not find newly created object", newParticipant);

        String updatedRemarks = newParticipant.getRemarks() + " Updated";
        newParticipant.setRemarks(updatedRemarks);

        String updatedPrintName = newParticipant.getPrintName() + " Updated";
        newParticipant.setPrintName(updatedPrintName);

        String updatedCoordinatorMobile = "9840-49-2831";
        newParticipant.setMobilePhone(updatedCoordinatorMobile);

        // Update.
        newParticipant.setProgram(program);
        participantRepository.save(newParticipant);

        // Read
        Participant updatedParticipant = participantRepository.findById(newParticipant.getId());

        Assert.assertEquals("Remarks not updated", updatedRemarks, updatedParticipant.getRemarks());
        Assert.assertEquals("WelcomeCardSignedByName not updated", updatedPrintName,
                updatedParticipant.getPrintName());
        Assert.assertEquals("CoordinatorMobile not updated", updatedCoordinatorMobile,
                updatedParticipant.getMobilePhone());
        Assert.assertNotNull("Address1 is null", updatedParticipant.getAddressLine1());

        // Clean up the new created/updated row.
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "program", "program_id=?", program.getProgramId());
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "participant", "id=?", newParticipant.getId());
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

    private Participant createPartcipant() {
        Participant participant = new Participant();
//        participant.setId;
        participant.setPrintName("Print Name");
        participant.setEmail("participate@test.com");
        participant.setMobilePhone("9900213110");
        participant.setGender("M");
        participant.setDateOfBirth(new Date());
        participant.setDateOfRegistration(new Date());
        participant.setAbhyasiId("USVSAA013");
        participant.setStatus(1);
        participant.setAddressLine1("201 Ebony");
        participant.setAddressLine2("River View Link Road");
        participant.setCity("Chennai");
        participant.setState("TN");
        participant.setCountry("India");
        participant.setProgramId(1);
        participant.setProfession("Software Engineer");
        participant.setIntroductionDate(new Date());
        participant.setIntroducedBy("Test prefect");
        participant.setAgeGroup("30-40");
        participant.setFirstSittingDate(new Date());
        participant.setSecondSittingDate(new Date());
        participant.setThirdSittingDate(new Date());

        return participant;
    }


}
