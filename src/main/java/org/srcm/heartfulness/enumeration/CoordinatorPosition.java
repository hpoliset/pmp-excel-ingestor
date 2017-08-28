/**
 * 
 */
package org.srcm.heartfulness.enumeration;

/**
 * @author Koustav Dutta
 *
 */
public enum CoordinatorPosition {
	
	CENTER_COORDINATOR(1,"Center Coordinator"),
	ZONE_COORDINATOR(2,"Zone Coordinator"),
	COUNTRY_COORDINATOR(3,"Country Coordinator"),
	PRESIDENT(4,"President");
	
	private int positionValue;
	private String positionType;
	
	private CoordinatorPosition(String positionType){
		this.positionType = positionType;
	}

	private CoordinatorPosition(int positionValue, String positionType) {
		this.positionValue = positionValue;
		this.positionType = positionType;
	}

	public String getPositionType() {
		return positionType;
	}


	public void setPositionType(String positionType) {
		this.positionType = positionType;
	}

	public int getPositionValue() {
		return positionValue;
	}

	public void setPositionValue(int positionValue) {
		this.positionValue = positionValue;
	}
	
}
