package org.srcm.heartfulness.enumeration;
/**
 * 
 * @author Barath
 *
 */
public enum ProgramStatus {

	completed("completed"),
	discontinued("discontinued"),
	cancelled("cancelled");
	
	String value;
	
	private ProgramStatus(){
		
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ProgramStatus(String value) {
		this.value = value;
	}
	
	
	public static boolean contains(String test) {

	    for (ProgramStatus status : ProgramStatus.values()) {
	        if (status.name().equals(test)) {
	            return true;
	        }
	    }
	    return false;
	}

}
