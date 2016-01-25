
package org.srcm.heartfulness.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.web.WebAppConfiguration;
import org.srcm.heartfulness.test.cases.ExcelDataV1ImplTest;
import org.srcm.heartfulness.test.cases.ExcelDataV2ImplTest;


/**
 * @author Koustav Dutta
 *
 */
@WebAppConfiguration
@RunWith(Suite.class)
@Suite.SuiteClasses({
	ExcelDataV1ImplTest.class,
	ExcelDataV2ImplTest.class
})
public class PmpApplicationTestSuite {
}
