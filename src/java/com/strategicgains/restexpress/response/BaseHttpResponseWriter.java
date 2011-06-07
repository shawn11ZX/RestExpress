/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.restexpress.response;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.util.HttpSpecification;

/**
 * @author toddf
 * @since Aug 26, 2010
 */
public abstract class BaseHttpResponseWriter
implements HttpResponseWriter
{
	@Override
	public void write(ChannelHandlerContext ctx, Request request, Response response)
	{
		HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, response.getResponseStatus());
		addHeaders(response, httpResponse);
		handleBody(ctx, httpResponse, response);

		if (request.isKeepAlive())
	  	{
	  		// Add 'Content-Length' header only for a keep-alive connection.
			if (!response.hasHeader(CONTENT_LENGTH)
				&& HttpSpecification.isContentLengthAllowed(response))
	  		{
				httpResponse.setHeader(CONTENT_LENGTH, String.valueOf(httpResponse.getContent().readableBytes()));
	  		}

	  		ctx.getChannel().write(httpResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
	  	}
		else
		{
			httpResponse.setHeader(CONNECTION, "close");

			// Close the connection as soon as the message is sent.
			ctx.getChannel().write(httpResponse).addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
     * @param response
     * @param httpResponse
     */
    private void addHeaders(Response response, HttpResponse httpResponse)
    {
    	for (String name : response.getHeaderNames())
    	{
    		for (String value : response.getHeaders(name))
    		{
    			httpResponse.addHeader(name, value);
    		}
    	}
    }

	/**
	 * @param httpResponse
	 * @param response
	 */
	protected abstract void handleBody(ChannelHandlerContext ctx, HttpResponse httpResponse, Response response);
}
