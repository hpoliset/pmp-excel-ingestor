/**
 * 
 */
package org.srcm.heartfulness.enumeration;

/**
 * @author Koustav Dutta
 *
 */
public enum MobileDataProgramCols {
	
	EVENT_ID("Event ID", 1, 0), 
	EVENT_TYPE("Event Type", 2, 0),
	EVENT_NAME("Event Name", 3, 0),
	EVENT_FROM_DATE("Event Date", 4, 0),
	EVENT_TO_DATE("To Date", 5, 0),
	EVENT_CITY("Event City", 6, 0), 
	EVENT_STATE("Event State", 7, 0), 
	EVENT_COORDINATORNAME("Event Coordinator Name", 8, 0), 
	EVENT_COORDINATOR_MAIL("Event Coordinator Email ID", 9, 0), 
	ORGANIZATION_NAME("Organization Name", 10, 0),
	PRECEPTOR_NAME("Preceptor Name", 11, 0), 
	PRECEPTOR_ID("Preceptor ID",12, 0);	
	
	private String header;
	private int cell;
	private int row;
	
	private MobileDataProgramCols(String header, int cell, int row) {
		this.header = header;
		this.cell = cell;
		this.row = row;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
}
