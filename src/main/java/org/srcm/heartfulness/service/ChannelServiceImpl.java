package org.srcm.heartfulness.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.repository.ChannelRepository;

/**
 * This class is service Implementation for the event channel based actions.
 * 
 * @author rramesh
 *
 */
@Service
public class ChannelServiceImpl implements ChannelService {

	@Autowired
	private ChannelRepository channelRepository;

	/**
	 * Retrieve <code>List<Channel></code> from the data store.
	 * 
	 * @return List<Channel>
	 */
	@Override
	public List<Channel> findAllActiveChannels() {
		return channelRepository.findAllActiveChannels();
	}

}
