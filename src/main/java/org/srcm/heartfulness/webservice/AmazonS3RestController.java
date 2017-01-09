package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.service.AmazonS3Service;

/**
 * Rest Controller - For managing file objects in AWS S3.
 * 
 * @author himasreev
 *
 */
@RestController
@RequestMapping("/api/aws/")
public class AmazonS3RestController {

	@Autowired
	AmazonS3Service amazonS3Service;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<?> uploadLetterForEvent(
			@RequestParam("file") MultipartFile multipartFile, @Context HttpServletRequest httpRequest)
			throws ParseException, IOException {
		

		return amazonS3Service.uploadFileToAWS(multipartFile);

	}

}
