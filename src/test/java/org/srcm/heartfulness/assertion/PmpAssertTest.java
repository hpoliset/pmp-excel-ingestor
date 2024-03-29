/**
 * 
 */
package org.srcm.heartfulness.assertion;

import java.util.List;

import org.junit.Assert;
import org.springframework.stereotype.Service;

/**
 * @author Koustav Dutta
 *
 */
@Service
public class PmpAssertTest {

	/**
	 * Error response should be empty for a valid excel file.
	 * @param errorEesponse
	 */
	public void assertValidExcelFile(List<String> errorResponse) {
		Assert.assertEquals(0, errorResponse.size());
	}

	public void validateParticipantCount(int initialParticipantCount,int finalParticipantCount){
		Assert.assertEquals("Initail and Final paticipant count should be same.", initialParticipantCount,finalParticipantCount);
	}
	
	public void dedupeParticipantWithNameAndEmail(String expectedMobileNo,String actualMobileNo){
		Assert.assertEquals("Two Participant with same name and email but different mobile number", expectedMobileNo,actualMobileNo);
	}
	
	public void dedupeParticipantWithNameAndMobile(String expectedEmail,String actualEmail){
		Assert.assertEquals("Two Participant with same name and email but different mobile number", expectedEmail,actualEmail);
	}

}
