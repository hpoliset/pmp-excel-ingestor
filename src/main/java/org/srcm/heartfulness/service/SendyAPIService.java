package org.srcm.heartfulness.service;

/**
 * 
 * @author rramesh
 *
 */
public interface SendyAPIService {

	/**
	 * To add new subscriber to the sendy list
	 * @return 
	 */
	public String subscribe(String printName,String mailID);

	/**
	 * To unsubscribe a subscriber from the sendy list
	 */
	public String unsubscribe(String mailID);

}
