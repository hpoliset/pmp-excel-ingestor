package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class holds the details of the event coordinators.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramCoordinators {

	private int id;

	private int program_id;

	private int user_id;

	private String coordinator_name;

	private String coordinator_email;

	private int is_primary_coordinator;
	
	public ProgramCoordinators() {
		super();
	}

	public ProgramCoordinators(int id, int program_id, int user_id, String coordinator_name, String coordinator_email,
			int is_primary_coordinator) {
		super();
		this.id = id;
		this.program_id = program_id;
		this.user_id = user_id;
		this.coordinator_name = coordinator_name;
		this.coordinator_email = coordinator_email;
		this.is_primary_coordinator = is_primary_coordinator;
	}
	
	public ProgramCoordinators(int program_id, int user_id, String coordinator_name, String coordinator_email,
			int is_primary_coordinator) {
		super();
		this.program_id = program_id;
		this.user_id = user_id;
		this.coordinator_name = coordinator_name;
		this.coordinator_email = coordinator_email;
		this.is_primary_coordinator = is_primary_coordinator;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProgram_id() {
		return program_id;
	}

	public void setProgram_id(int program_id) {
		this.program_id = program_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getCoordinator_name() {
		return coordinator_name;
	}

	public void setCoordinator_name(String coordinator_name) {
		this.coordinator_name = coordinator_name;
	}

	public String getCoordinator_email() {
		return coordinator_email;
	}

	public void setCoordinator_email(String coordinator_email) {
		this.coordinator_email = coordinator_email;
	}

	public int getIs_primary_coordinator() {
		return is_primary_coordinator;
	}

	public void setIs_primary_coordinator(int is_primary_coordinator) {
		this.is_primary_coordinator = is_primary_coordinator;
	}

	@Override
	public String toString() {
		return "ProgramCoordinators [id=" + id + ", program_id=" + program_id + ", user_id=" + user_id
				+ ", coordinator_name=" + coordinator_name + ", coordinator_email=" + coordinator_email
				+ ", is_primary_coordinator=" + is_primary_coordinator + "]";
	}

}
