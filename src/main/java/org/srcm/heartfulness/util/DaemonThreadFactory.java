/**
 * 
 */
package org.srcm.heartfulness.util;

import java.util.concurrent.ThreadFactory;

/**
 * @author Koustav Dutta
 *
 */
public class DaemonThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable runnableObject) {

		Thread sendWelcomeMailThread = new Thread(runnableObject);
		sendWelcomeMailThread.setDaemon(true);
		return sendWelcomeMailThread;
		
	}

}
