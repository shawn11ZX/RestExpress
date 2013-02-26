/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.restexpress.pipeline;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

/**
 * Builds the Netty pipeline to support handling RestExpress requests.
 * 
 * @author toddf
 * @since Aug 27, 2010
 */
public class PipelineBuilder
implements ChannelPipelineFactory
{
	// SECTION: INSTANCE VARIABLES

	private ChannelHandler dispatcher = null;

	
	// SECTION: CONSTRUCTORS

	public PipelineBuilder(ChannelHandler dispatcher)
	{
		super();
		this.dispatcher = dispatcher;
	}

	// SECTION: CHANNEL PIPELINE FACTORY

	@Override
	public ChannelPipeline getPipeline()
	throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkWriter", new ChunkedWriteHandler());
		pipeline.addLast("inflater", new HttpContentDecompressor());
		pipeline.addLast("deflater", new HttpContentCompressor());
		pipeline.addLast("dispatcher", dispatcher);
		return pipeline;
	}
}
