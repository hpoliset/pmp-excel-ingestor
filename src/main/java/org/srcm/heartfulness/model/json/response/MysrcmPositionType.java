/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Koustav Dutta
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MysrcmPositionType extends MysrcmResult{
	
	@JsonProperty("results")
	private PositionType positionType[];

	public PositionType[] getPositionType() {
		return positionType;
	}

	public void setPositionType(PositionType[] positionType) {
		this.positionType = positionType;
	}

	@Override
	public String toString() {
		return "MysrcmPositionType [positionType=" + Arrays.toString(positionType) + ", getPositionType()="
				+ Arrays.toString(getPositionType()) + ", getCount()=" + getCount() + ", getNext()=" + getNext()
				+ ", getPrevious()=" + getPrevious() + "]";
	}
	
	
	

}
