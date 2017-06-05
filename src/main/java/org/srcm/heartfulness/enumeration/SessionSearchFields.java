/**
 * 
 */
package org.srcm.heartfulness.enumeration;

/**
 * @author Koustav Dutta
 *
 */
public enum SessionSearchFields {
	
	sessionNumber("session_number"),
	preceptorName("preceptor_name"),
	preceptorIdCardNumber("preceptor_id_card_no"),
	topicCovered("topic_covered");

	private String fieldValue;

	private SessionSearchFields(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
