/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package org.restexpress.pipeline;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Provides a tiny DSL to define the pipeline features.
 * 
 * @author toddf
 * @since Aug 27, 2010
 */
public class PipelineBuilder
implements ChannelPipelineFactory
{
	// SECTION: CONSTANTS

	private static final int DEFAULT_MAX_CONTENT_LENGTH = 20480;


	// SECTION: INSTANCE VARIABLES

	private List<ChannelHandler> requestHandlers = new ArrayList<ChannelHandler>();
	private ExecutionHandler executionHandler = null;
	private int maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;
	private SSLContext sslContext = null;

	
	// SECTION: CONSTRUCTORS

	public PipelineBuilder()
	{
		super();
	}

	
	// SECTION: BUILDER METHODS
	
	public PipelineBuilder setExecutionHandler(ExecutionHandler handler)
	{
		this.executionHandler = handler;
		return this;
	}

	public PipelineBuilder addRequestHandler(ChannelHandler handler)
	{
		if (!requestHandlers.contains(handler))
		{
			requestHandlers.add(handler);
		}

		return this;
	}
	
	/**
	 * Set the maximum length of the aggregated (chunked) content. If the length of the
	 * aggregated content exceeds this value, a TooLongFrameException will be raised during
	 * the request, which can be mapped in the RestExpress server to return a
	 * BadRequestException, if desired.
	 * 
	 * @param value
	 * @return this PipelineBuilder for method chaining.
	 */
	public PipelineBuilder setMaxContentLength(int value)
	{
		this.maxContentLength = value;
		return this;
	}

	public PipelineBuilder setSSLContext(SSLContext sslContext)
	{
		this.sslContext = sslContext;
		return this;
	}
	
	public SSLContext getSSLContext()
	{
		return sslContext;
	}

	// SECTION: CHANNEL PIPELINE FACTORY

	@Override
	public ChannelPipeline getPipeline()
	throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();

		if (null != sslContext)
		{
			SSLEngine sslEngine = sslContext.createSSLEngine();
			sslEngine.setUseClientMode(false);
			SslHandler sslHandler = new SslHandler(sslEngine);
			pipeline.addLast("ssl", sslHandler);
		}
		
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpObjectAggregator(maxContentLength));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkWriter", new ChunkedWriteHandler());
		pipeline.addLast("inflater", new HttpContentDecompressor());
		pipeline.addLast("deflater", new HttpContentCompressor());

		if (executionHandler != null)
		{
			pipeline.addLast("executionHandler", executionHandler);
		}

		for (ChannelHandler handler : requestHandlers)
		{
			pipeline.addLast(handler.getClass().getSimpleName(), handler);
		}

		return pipeline;
	}
}
