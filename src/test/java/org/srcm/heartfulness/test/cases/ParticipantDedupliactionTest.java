/**
 * 
 */
package org.srcm.heartfulness.test.cases;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.srcm.heartfulness.PmpApplication;

/**
 * @author Koustav Dutta
 *
 */

@SpringApplicationConfiguration(classes = PmpApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ParticipantDedupliactionTest {
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private MockMvc mockMvc;
	
	
	@Test
	public void uploadExcelFileAndPersist(){
		
		String TEST_FILE_ONE 			= 	"upload_testsheet_via_pmp.xlsx";
		String TEST_FILE_TWO 			= 	"upload_testsheet_via_service.xlsx";
		String TEST_FILE_ONE_UPADTED 	= 	"upload_testsheet_via_pmp_updated.xlsx";
		String TEST_FILE_TWO_UPDATED 	= 	"upload_testsheet_via_service_updated.xlsx";
		
		Resource resourceFileOne = resourceLoader.getResource("classpath:" + TEST_FILE_ONE);
		//Resource resourceFileTwo = resourceLoader.getResource("classpath:" + TEST_FILE_TWO);

		FileInputStream inputFileOne;
		try {
			
			inputFileOne = new FileInputStream(resourceFileOne.getFile());
			MockMultipartFile multipartFileOne = new MockMultipartFile("file", TEST_FILE_ONE, "multipart/form-data", inputFileOne);
			
			
			/*mockMvc.perform(MockMvcRequestBuilders.fileUpload("/ingest/processBulkUpload")
					.file(multipartFileOne)
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.view().name("bulkUploadResponse"));*/
			

			//FileInputStream inputFileTwo = new FileInputStream(resourceFileTwo.getFile());  
			//MockMultipartFile multipartFileTwo = new MockMultipartFile("file", TEST_FILE_ONE, "multipart/form-data", inputFileTwo);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} 
		

		/*mockMvc.perform(MockMvcRequestBuilders.fileUpload("/ingest/processBulkUpload")
				.file(multipartFileOne)
				.file(multipartFileTwo))
				.andExpect(status().is(200))
				.andExpect(MockMvcResultMatchers.view().name("bulkUploadResponse"));*/
		
		
	}


	private Object status() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
