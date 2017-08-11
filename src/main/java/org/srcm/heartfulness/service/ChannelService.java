package org.srcm.heartfulness.service;

import java.util.List;
import java.util.Map;

import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.model.json.response.ProgramChannelType;

/**
 * Service Class for managing <code>Channel</code> domain objects.
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

	/**
	 * Service to get the active channel details from the Heartfulness backend.
	 * 
	 * @param role
	 * @return<code>List<Channel></code> list of channel.
	 */
	public List<Channel> findAllActiveChannelsBasedOnRole(String role);

	/**
	 * Service to get the list of channel types 
	 * based on the channel.
	 * @param channel is used to get the channel types.
	 * @return List<ProgramChannelType> channelTypes.
	 */
	public List<ProgramChannelType> getListOfChannelTypes(String channel);
}
