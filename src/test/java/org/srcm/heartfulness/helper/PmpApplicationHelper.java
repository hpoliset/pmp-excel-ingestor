package org.srcm.heartfulness.helper;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

/**
 * @author Koustav Dutta
 *
 */
@Service
public class PmpApplicationHelper {

	@Autowired
	private ResourceLoader resourceLoader;
	/**
	 * This method will return Excel file content.
	 * @param fileName
	 * @return the extracted file content for a valid excel file.
	 * @throws IOException
	 */
	public byte[] getValidExcelWorkbook(String fileName) throws IOException{
		Resource ValidResource = resourceLoader.getResource("classpath:" + fileName);
		byte[] fileContent = StreamUtils.copyToByteArray(ValidResource.getInputStream());
		return fileContent;
	}

}
