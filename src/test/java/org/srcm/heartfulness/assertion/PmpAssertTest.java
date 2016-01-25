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
	public void assertValidExcelFile(List<String> errorEesponse) {
		Assert.assertNull("Excel file should not have any errors.", errorEesponse);
	}
	/**
	 * Validating an invalid excel file(v1.0) structure  with 21 error responses.
	 * @param errorResponse
	 */
	public void validateV1Structure(List<String> errorResponse) {
		Assert.assertEquals("Template V1 structure should have 21 error responses",21,errorResponse.size());
	}
	/**
	 * Validating an invalid excel file(v1.0) mandatory parameters with 9 error responses.
	 * @param errorResponse
	 */
	public void validateV1MandatoryParameters(List<String> errorResponse) {
		Assert.assertEquals("V1 structure should have 9 error responses",9,errorResponse.size());
	}

	/**
	 * Validating an invalid excel file(v2.1) structure  with 39 error responses.
	 * @param errorResponse
	 */
	public void validateV2Structure(List<String> errorResponse) {
		Assert.assertEquals("Template V2.1 structure should have 39 error responses",39,errorResponse.size());
	}
	/**
	 * Validating an invalid excel file(v2.1) mandatory parameters with 3 error responses.
	 * @param errorResponse
	 */
	public void validateV2MandatoryParameters(List<String> errorResponse) {
		Assert.assertEquals("V2.1 structure should have 3 error responses for invalid country,state,city",3,errorResponse.size());
	}
	/**
	 * Validating if participant data is deduplicated.
	 * @param initialParticipantCount
	 * @param finalParticipantCount
	 */
	public void validateDataDeduplication(int initialParticipantCount, int finalParticipantCount) {
		Assert.assertEquals("Initail and Final paticipant count should be same.", initialParticipantCount,finalParticipantCount);
	}
}
