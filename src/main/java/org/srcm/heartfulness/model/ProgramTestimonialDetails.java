package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class to hold the coordinator permission letter details of an Program/event.
 * 
 * @author gopinatha
 *
 */
public class ProgramTestimonialDetails {

	@JsonIgnore
	private int testimonialId;
	@JsonIgnore
	private int programId;
	@JsonIgnore
	private String testimonialPath;
	
	private String testimonialName;
	private String testimonialType;
	private String presignedURL;
	private String uploadedBy;

	public ProgramTestimonialDetails() {
	}

	public ProgramTestimonialDetails( int programId, String testimonialName, String testimonialPath,
			String testimonialType, String uploadedBy) {
		super();
		this.programId = programId;
		this.testimonialName = testimonialName;
		this.testimonialPath = testimonialPath;
		this.testimonialType = testimonialType;
		this.uploadedBy = uploadedBy;
	}

	public int getTestimonialId() {
		return testimonialId;
	}

	public void setTestimonialId(int testimonialId) {
		this.testimonialId = testimonialId;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public String getTestimonialName() {
		return testimonialName;
	}

	public void setTestimonialName(String testimonialName) {
		this.testimonialName = testimonialName;
	}

	public String getTestimonialPath() {
		return testimonialPath;
	}

	public void setTestimonialPath(String testimonialPath) {
		this.testimonialPath = testimonialPath;
	}

	public String getTestimonialType() {
		return testimonialType;
	}

	public String getPresignedURL() {
		return presignedURL;
	}

	public void setPresignedURL(String presignedURL) {
		this.presignedURL = presignedURL;
	}

	public void setTestimonialType(String testimonialType) {
		this.testimonialType = testimonialType;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	@Override
	public String toString() {
		return "TestimonialDetails [testimonialId=" + testimonialId + ", programId=" + programId + ", testimonialName="
				+ testimonialName + ", testimonialPath=" + testimonialPath + ", testimonialType=" + testimonialType
				+ ", uploadedBy=" + uploadedBy + "]";
	}

}
