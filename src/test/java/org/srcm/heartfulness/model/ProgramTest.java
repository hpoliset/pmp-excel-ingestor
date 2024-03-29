package org.srcm.heartfulness.model;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for Program Domain Object.
 *
 * @author Venkat Sonnathi
 */
public class ProgramTest {

    private static final String PROGRAM_CHANNEL = "U Connect";
    private static final String EVENT_CITY = "Event City";
    private static final String EVENT_PLACE = "Adyar";
    private static final String ORGANIZATION_NAME = "Anna University";
    private static final String ORGANIZATION_DEPARTMENT = "Physics 1st Year Section A";
    private static final String EXPECTED_PROGRAM_HASH_CODE = "00e46bad30648651e7297b76174b90e0";
    private static final Calendar programStartCalendar = new GregorianCalendar(1955, 8, 28);


    @Test
    public void testComputeHashCode() throws UnsupportedEncodingException {
        Program program = new Program();
        programStartCalendar.setTimeZone(TimeZone.getTimeZone("IST"));
        program.setProgramChannel(PROGRAM_CHANNEL);
        program.setEventCity(EVENT_CITY);
        program.setEventPlace(EVENT_PLACE);
        program.setProgramStartDate(programStartCalendar.getTime());
        program.setOrganizationName(ORGANIZATION_NAME);
        program.setOrganizationDepartment(ORGANIZATION_DEPARTMENT);

        String hashCode = program.computeHashCode();
        Assert.assertEquals("Hash code not generated correctly", EXPECTED_PROGRAM_HASH_CODE, hashCode);
    }

    @Test
    public void testDifferentComputeHashCode() throws UnsupportedEncodingException {
        Program program = new Program();
        program.setProgramChannel(PROGRAM_CHANNEL);
        program.setEventCity(EVENT_CITY);
        program.setEventPlace(EVENT_PLACE);
        program.setProgramStartDate(new Date());
        program.setOrganizationName(ORGANIZATION_NAME);
        program.setOrganizationDepartment(ORGANIZATION_DEPARTMENT);

        String hashCode = program.computeHashCode();
        Assert.assertNotEquals("Hash code not generated correctly", EXPECTED_PROGRAM_HASH_CODE, hashCode);
    }
}
