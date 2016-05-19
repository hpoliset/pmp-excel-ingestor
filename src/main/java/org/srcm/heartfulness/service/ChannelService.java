package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.Channel;

/**
 * This class is service provider for the event channel based actions.
 * 
 * @author rramesh
 *
 */
public interface ChannelService {

	/**
	 * Retrieve <code>List<Channel></code> from the data store.
	 * 
	 * @return List<Channel>
	 */
	public List<Channel> findAllActiveChannels();
}
