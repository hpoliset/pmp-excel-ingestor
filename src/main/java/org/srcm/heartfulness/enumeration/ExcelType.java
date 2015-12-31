package org.srcm.heartfulness.enumeration;

import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.excelupload.transformer.impl.ExcelDataExtractorV1Impl;
import org.srcm.heartfulness.excelupload.transformer.impl.ExcelDataExtractorV2Impl;
import org.srcm.heartfulness.validator.EventDetailsExcelValidator;
import org.srcm.heartfulness.validator.impl.ExcelV1ValidatorImpl;
import org.srcm.heartfulness.validator.impl.ExcelV2ValidatorImpl;

/**
 * Enumeration to identify the Heartfulness excel upload types
 * 
 * @author Koustav Dutta
 *
 */
public enum ExcelType {

	V1(new ExcelV1ValidatorImpl(), new ExcelDataExtractorV1Impl()), // Denotes Excel version altered 1.0
	V2_1 (new ExcelV2ValidatorImpl(), new ExcelDataExtractorV2Impl()),// Denotes Excel extractor for altered 1.0
	INVALID(null); // Holder to display error message for invalid formats


	private EventDetailsExcelValidator validator;
	private ExcelDataExtractor extractor;

	/*
	 * constructor
	 */
	private ExcelType(EventDetailsExcelValidator validator) {
		this.validator = validator;
	}


	private ExcelType(EventDetailsExcelValidator validator, ExcelDataExtractor extractor) {
		this.validator = validator;
		this.extractor = extractor;
	}

	/**
	 * Getter method for returning the instance of the corresponding Validator.
	 * 
	 * @return @see {@link EventDetailsExcelValidator}
	 */
	public EventDetailsExcelValidator getValidator() {
		return validator;
	}

	/**
	 * Getter method for returning the instance of the corresponding extractor.
	 * 
	 * @return @see {@link ExcelDataExtractor}
	 */
	public ExcelDataExtractor getExtractor() {
		return extractor;
	}

}
