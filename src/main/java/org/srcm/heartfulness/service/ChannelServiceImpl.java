package org.srcm.heartfulness.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.model.json.response.ProgramChannelType;
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

	@Override
	public List<ProgramChannelType> getListOfChannelTypes(String channel) {
		List<ProgramChannelType> listOfChannelTypes = new ArrayList<ProgramChannelType>();
		int channelId = channelRepository.getChannelId(channel);
		if( 0 == channelId){
			return listOfChannelTypes;
		}else{
			listOfChannelTypes = channelRepository.getChannelType(channelId);
			return listOfChannelTypes;
		}
	}

}
