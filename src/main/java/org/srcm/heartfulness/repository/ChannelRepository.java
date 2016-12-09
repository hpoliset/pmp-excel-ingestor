package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.Channel;
/**
 *  Repository interface for managing <code>Channel</code> domain objects.
 *  
 * @author rramesh
 *
 */
public interface ChannelRepository {

	public List<Channel> findAllActiveChannels();

	public List<Channel> findAllActiveChannelsBasedOnRole(String role);
	
}
