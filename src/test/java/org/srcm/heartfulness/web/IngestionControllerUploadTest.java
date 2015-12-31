package org.srcm.heartfulness.web;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;


/**
 * Created by goutham on 24/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class IngestionControllerUploadTest {

	@Autowired
	private ResourceLoader resourceLoader;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(new IngestionController()).build();
	}

	@Test
	public void processUploadTest() throws Exception{
		try{
			String TEST_FILE_ONE = "v21ValidEventDate.xlsm";
			String TEST_FILE_TWO = "HFN-DATA-MH-PUNE-INPSAD332-20151018.xlsx";
			Resource resourceOne = resourceLoader.getResource("classpath:" + TEST_FILE_ONE);
			Resource resourceTwo = resourceLoader.getResource("classpath:" + TEST_FILE_TWO);

			FileInputStream inputFileOne = new FileInputStream(resourceOne.getFile());  
			MockMultipartFile multipartFileOne = new MockMultipartFile("file", TEST_FILE_ONE, "multipart/form-data", inputFileOne);

			FileInputStream inputFileTwo = new FileInputStream(resourceTwo.getFile());  
			MockMultipartFile multipartFileTwo = new MockMultipartFile("file", TEST_FILE_ONE, "multipart/form-data", inputFileTwo);

			/*mockMvc.perform(MockMvcRequestBuilders.fileUpload("/ingest/processBulkUpload")
					.file(multipartFileOne)
					.file(multipartFileTwo))
					.andExpect(status().is(200))
					.andExpect(MockMvcResultMatchers.view().name("bulkUploadResponse"));*/
		}catch(Exception e){
			e.printStackTrace();
		}finally{

		}
	}

}
