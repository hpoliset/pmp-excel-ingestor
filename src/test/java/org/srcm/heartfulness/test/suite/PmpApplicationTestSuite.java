/**
 * 
 */
package org.srcm.heartfulness.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.web.WebAppConfiguration;
import org.srcm.heartfulness.test.cases.ParticipantDeduplicationTest;

/**
 * @author Koustav Dutta
 *
 */
@WebAppConfiguration
@RunWith(Suite.class)
@Suite.SuiteClasses({
	ParticipantDeduplicationTest.class
})
public class PmpApplicationTestSuite {
}