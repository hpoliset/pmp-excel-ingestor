package org.srcm.heartfulness.repository;

import java.util.List;
import java.util.Map;

import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.model.json.response.ProgramChannelType;

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

	/**
	 * Method is used to get the channel Id 
	 * based on the channel passed.
	 * @param channel to retrieve channel Id.
	 * @return channel Id.
	 */
	public int getChannelId(String channel);

	/**
	 * Method is used to get the channel types based on channel Id.
	 * @param channelId is used to retrieve channel types.
	 * @return List<ProgramChannelType> channelTypes.
	 */
	public List<ProgramChannelType> getChannelType(int channelId);
	
	/**
	 * Method is used to check whether channel is 
	 * available for the provided channel type Id.
	 * @param channelTypeId Id of channel type.
	 * @return true if channel is available for the
	 * provided id.
	 */
	public boolean validateChannelType(int channelTypeId);
	
	/**
	 * Method is used to get the channel types based on channel Id.
	 * @param channelId is used to retrieve channel types.
	 * @return List<ProgramChannelType> channelTypes.
	 */
	public List<ProgramChannelType> getChannelType(/*int channelId,*/ String channelName);
}
