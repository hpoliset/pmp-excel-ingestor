package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.Channel;

/**
 * 
 * @author rramesh
 *
 */
public interface ChannelService {

	public List<Channel> findAllActiveChannels();

	public List<Channel> findAllActiveChannelsBasedOnRole(String role);
}
