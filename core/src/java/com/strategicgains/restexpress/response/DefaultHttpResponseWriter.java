/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.restexpress.response;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

import com.strategicgains.restexpress.ContentType;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.util.HttpSpecification;

/**
 * @author toddf
 * @since Aug 26, 2010
 */
public class DefaultHttpResponseWriter
implements HttpResponseWriter
{
	@Override
	public void write(ChannelHandlerContext ctx, Request request, Response response)
	{
		FullHttpResponse httpResponse;

		if (response.hasBody() && HttpSpecification.isContentAllowed(response))
		{
			// If the response body already contains a ByteBuf, use it.
			if (ByteBuf.class.isAssignableFrom(response.getBody().getClass()))
			{
				httpResponse = new DefaultFullHttpResponse(request.getHttpVersion(),
					response.getResponseStatus(),
					Unpooled.wrappedBuffer((ByteBuf) response.getBody()));
			}
			else // response body is assumed to be a string (e.g. JSON or XML).
			{
				httpResponse = new DefaultFullHttpResponse(request.getHttpVersion(),
					response.getResponseStatus(),
//					Unpooled.copiedBuffer(response.getBody().toString(), ContentType.CHARSET));
					Unpooled.wrappedBuffer(response.getBody().toString().getBytes(ContentType.CHARSET)));
			}
		}
		else	// No response body.
		{
			httpResponse = new DefaultFullHttpResponse(request.getHttpVersion(), response.getResponseStatus());
		}

		addHeaders(response, httpResponse);		

		if (request.isKeepAlive())
	  	{
	  		// Add 'Content-Length' header only for a keep-alive connection.
			if (HttpSpecification.isContentLengthAllowed(response))
	  		{
				httpResponse.headers().set(CONTENT_LENGTH, String.valueOf(httpResponse.content().readableBytes()));
	  		}

			// Support "Connection: Keep-Alive" for HTTP 1.0 requests.
			if (request.isHttpVersion1_0()) 
			{
				httpResponse.headers().add(CONNECTION, "Keep-Alive");
			}

	  		ctx.channel().write(httpResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
	  	}
		else
		{
			httpResponse.headers().set(CONNECTION, "close");

			// Close the connection as soon as the message is sent.
			ctx.channel().write(httpResponse).addListener(ChannelFutureListener.CLOSE);
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
    			httpResponse.headers().add(name, value);
    		}
    	}
    }
}
