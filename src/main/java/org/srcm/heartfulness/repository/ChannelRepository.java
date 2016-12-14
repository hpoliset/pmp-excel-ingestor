package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.Channel;

/**
 * Repository interface for managing <code>Channel</code> domain objects.
 * 
 * @author rramesh
 *
 */
public interface ChannelRepository {

	/**
	 * Method to get the active channel list with channel details from the
	 * Heartfulness backend.
	 * 
	 * @return <code>List<Channel></code>
	 */
	public List<Channel> findAllActiveChannels();

	/**
	 * Method to get the active channel list from the Heartfulness backend.
	 * 
	 * @return <code>List<String></code> list of channel names.
	 */
	public List<String> findAllActiveChannelNames();

	/**
	 * Method to get the list of active channels based on the user role.
	 * 
	 * @param role
	 * @return <code>List<Channel></code> list of channel names.
	 */
	public List<Channel> findAllActiveChannelsBasedOnRole(String role);

}
