package org.srcm.heartfulness.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.repository.ChannelRepository;

@Service
public class ChannelServiceImpl implements ChannelService{

	@Autowired
	private ChannelRepository channelRepository;

	
	@Override
	public List<Channel> findAllActiveChannels() {
		return channelRepository.findAllActiveChannels();
	}
	
	@Override
	public List<String> findAllActiveChannelNames() {
		return channelRepository.findAllActiveChannelNames();
	}
	
	

}
