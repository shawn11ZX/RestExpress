/*
 * Copyright 2011, Pearson eCollege.  All rights reserved.
 */
package com.strategicgains.restexpress.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;

import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.exception.NotFoundException;
import com.strategicgains.restexpress.exception.ServiceException;

/**
 * Streams a File returned from a Controller action (method).  The returned file can be either a literal
 * 'return' as in from the method and in the signature, or returned by setting the response body (e.g.
 * response.setBody(File).
 * 
 * The file will be streamed using the chunk size set in the RestExpress server.
 * 
 * @author toddf
 * @since Jun 3, 2011
 */
public class StreamingHttpResponseWriter
extends BaseHttpResponseWriter
{
	private int chunkSize;

	public StreamingHttpResponseWriter(int chunkSize)
	{
		super();
		this.chunkSize = chunkSize;
	}

	@Override
	protected void handleBody(ChannelHandlerContext ctx,
		HttpResponse httpResponse, Response response)
	{
		Object body = response.getBody();
		if (!response.hasBody())
			throw new ServiceException(
				"No File returned from controller to stream.");
		if (!body.getClass().isAssignableFrom(File.class))
			throw new ServiceException(
				"Streaming response handles types of File only.");

		File file = (File) body;
		RandomAccessFile raf;

		try
		{
			raf = new RandomAccessFile(file, "r");
		}
		catch (FileNotFoundException e)
		{
			throw new NotFoundException(e);
		}

		try
		{
			long fileLength = raf.length();
			httpResponse
				.setHeader(HttpHeaders.Names.CONTENT_LENGTH, fileLength);
			Channel ch = ctx.getChannel();
			ch.write(httpResponse);

			streamFileContent(ch, raf, fileLength);
		}
		catch (IOException e)
		{
			throw new ServiceException(e);
		}
	}

	private void streamFileContent(Channel ch, RandomAccessFile raf, long fileLength)
	throws IOException
	{
		// Write the content.
		ChannelFuture writeFuture;
		if (ch.getPipeline().get(SslHandler.class) != null)
		{
			// Cannot use zero-copy with HTTPS.
			writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, chunkSize));
		}
		else
		{
			// No encryption - use zero-copy.
			final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
			writeFuture = ch.write(region);
			writeFuture.addListener(new ChannelFutureProgressListener()
			{
				public void operationComplete(ChannelFuture future)
				{
					region.releaseExternalResources();
				}

				public void operationProgressed(ChannelFuture future, long amount, long current, long total)
				{
					System.out.printf("%s: %d / %d (+%d)%n", "file", current, total, amount);
				}
			});
		}
	}
}
