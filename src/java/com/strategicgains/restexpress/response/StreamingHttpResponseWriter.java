/*
 * Copyright 2011, Strategic Gains, Inc.  All rights reserved.
 */
package com.strategicgains.restexpress.response;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.stream.ChunkedFile;

import com.strategicgains.restexpress.Request;
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
	public void write(Channel ch, Request request, Response response)
	{
		HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, response.getResponseStatus());
		addHeaders(response, httpResponse);
		File file = getBodyAsFile(response);
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
			httpResponse.setHeader(CONTENT_LENGTH, fileLength);
			ch.write(httpResponse);

			streamFileContent(ch, raf, fileLength, request.isKeepAlive());
		}
		catch (IOException e)
		{
			throw new ServiceException(e);
		}
	}

	private File getBodyAsFile(Response response)
    {
	    Object body = response.getBody();

		if (!response.hasBody())
		{
			throw new ServiceException("No File returned from controller to stream.");
		}

		if (!body.getClass().isAssignableFrom(File.class))
		{
			throw new ServiceException("Streaming response handles types of File only.");
		}

		File file = (File) body;
	    return file;
    }

	private void streamFileContent(Channel ch, RandomAccessFile raf, long fileLength, boolean isKeepAlive)
	throws IOException
	{
		System.out.println("Attempting to send file of size: " + fileLength);
		ChannelFuture writeFuture;
//		if (ch.getPipeline().get(SslHandler.class) != null)
//		{
//			// Cannot use zero-copy with HTTPS.
			writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, chunkSize));
//		}
//		else
//		{
//			// No encryption - use zero-copy.
//			final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
//			writeFuture = ch.write(region);
//			writeFuture.addListener(new ChannelFutureProgressListener()
//			{
//				public void operationComplete(ChannelFuture future)
//				{
//					region.releaseExternalResources();
//				}
//
//				public void operationProgressed(ChannelFuture future, long amount, long current, long total)
//				{
//					System.out.printf("%s: %d / %d (+%d)%n", "stream", current, total, amount);
//				}
//			});
//		}
		
		if (!isKeepAlive)
		{
			// Close the connection when the whole content is written out.
			writeFuture.addListener(ChannelFutureListener.CLOSE);
		}
//		else
//		{
//	  		writeFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
//		}
	}
}
