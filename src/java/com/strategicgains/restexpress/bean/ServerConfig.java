/*
    Copyright 2012, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.strategicgains.restexpress.bean;

/**
 * @author toddf
 * @since May 31, 2012
 */
public class ServerConfig
{
	private static final int DEFAULT_EXECUTOR_THREAD_COUNT = 0;

	private String name;
	private int port;
	private boolean keepAlive = true;
	private boolean reuseAddress = true;
	private boolean handleChunking = true;
	private boolean useCompression = true;
	private Integer maxChunkSize = null;

	// This controls the number of concurrent connections the application can
	// handle.
	// Netty default is 2 * number of processors (or cores).
	private int ioThreadCount = 0;

	// This controls the number of concurrent requests the application can
	// process.
	private int processingThreadCount = DEFAULT_EXECUTOR_THREAD_COUNT;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isKeepAlive()
	{
		return keepAlive;
	}

	public void setKeepAlive(boolean isKeepAlive)
	{
		this.keepAlive = isKeepAlive;
	}

	public boolean isReuseAddress()
	{
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress)
	{
		this.reuseAddress = reuseAddress;
	}

	public boolean isHandleChunking()
	{
		return handleChunking;
	}

	public void setHandleChunking(boolean handleChunking)
	{
		this.handleChunking = handleChunking;
	}

	public boolean isUseCompression()
	{
		return useCompression;
	}

	public void setUseCompression(boolean useCompression)
	{
		this.useCompression = useCompression;
	}

	public Integer getMaxChunkSize()
	{
		return maxChunkSize;
	}

	public void setMaxChunkSize(Integer maxChunkSize)
	{
		this.maxChunkSize = maxChunkSize;
	}

	public int getIoThreadCount()
	{
		return ioThreadCount;
	}

	public void setIoThreadCount(int ioThreadCount)
	{
		this.ioThreadCount = ioThreadCount;
	}

	public int getProcessingThreadCount()
	{
		return processingThreadCount;
	}

	public void setProcessingThreadCount(int processingThreadCount)
	{
		this.processingThreadCount = processingThreadCount;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
}
