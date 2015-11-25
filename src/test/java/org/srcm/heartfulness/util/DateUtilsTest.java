package org.srcm.heartfulness.util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by vsonnathi on 11/25/15.
 */
public class DateUtilsTest {

    @Test
    public void testParseFormatStandard() throws ParseException {
        String dateString = "10-sep-15";
        Date date = DateUtils.parseDate(dateString);
        System.out.println("dateString = " + dateString + " date: " + date);
        Assert.assertNotNull("Date is null", date);
    }

    @Test
    public void testParseFormatDots() throws ParseException {
        String dateString = "10.09.2015";
        Date date = DateUtils.parseDate(dateString);
        System.out.println("dateString = " + dateString  + " date: " + date);
        Assert.assertNotNull("Date is null", date);
    }
}
