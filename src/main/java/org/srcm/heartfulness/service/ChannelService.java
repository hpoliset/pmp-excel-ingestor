package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.Channel;

/**
 * Service Class  for managing <code>Channel</code> domain objects.
 * 
 * @author rramesh
 *
 */
public interface ChannelService {

	/**
	 * Service to get the active channel list with channel details from the
	 * Heartfulness backend.
	 * 
	 * @return <code>List<Channel></code>
	 */
	public List<Channel> findAllActiveChannels();

	/**
	 * Service to get the active channel list from the Heartfulness backend.
	 * 
	 * @return <code>String<Channel></code> list of channel names.
	 */
	List<String> findAllActiveChannelNames();
}
