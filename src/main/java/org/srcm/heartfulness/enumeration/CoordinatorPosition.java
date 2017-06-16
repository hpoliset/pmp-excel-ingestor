/**
 * 
 */
package org.srcm.heartfulness.enumeration;

/**
 * @author Koustav Dutta
 *
 */
public enum CoordinatorPosition {
	
	CENTER_COORDINATOR("Center Coordinator"),
	ZONE_COORDINATOR("Zone Coordinator"),
	COUNTRY_COORDINATOR("Country Coordinator");
	
	private String positionType;
	
	
	private CoordinatorPosition(String positionType){
		this.positionType = positionType;
	}

	public String getPositionType() {
		return positionType;
	}


	public void setPositionType(String positionType) {
		this.positionType = positionType;
	}

}
