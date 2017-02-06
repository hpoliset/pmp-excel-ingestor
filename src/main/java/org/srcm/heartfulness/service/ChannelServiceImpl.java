package org.srcm.heartfulness.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.repository.ChannelRepository;

/**
 * Service Implementation for managing <code>Channel</code> domain objects.
 * 
 * @author himasreev
 *
 */
@Service
public class ChannelServiceImpl implements ChannelService {

	@Autowired
	private ChannelRepository channelRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.ChannelService#findAllActiveChannels()
	 */
	@Override
	public List<Channel> findAllActiveChannels() {
		return channelRepository.findAllActiveChannels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.ChannelService#findAllActiveChannelNames()
	 */
	@Override
	public List<String> findAllActiveChannelNames() {
		return channelRepository.findAllActiveChannelNames();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ChannelService#findAllActiveChannelsBasedOnRole(java.lang.String)
	 */
	@Override
	public List<Channel> findAllActiveChannelsBasedOnRole(String role) {
		return channelRepository.findAllActiveChannelsBasedOnRole(role);		
	}

}
