/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.restexpress.response;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.strategicgains.restexpress.ContentType;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.util.HttpSpecification;

/**
 * @author toddf
 * @since Aug 26, 2010
 */
public class DefaultHttpResponseWriter
extends BaseHttpResponseWriter
{
	@Override
	protected void handleBody(ChannelHandlerContext ctx, HttpResponse httpResponse, Response response)
	{
		if (response.hasBody()
			&& HttpSpecification.isContentAllowed(response))
		{
			StringBuilder builder = new StringBuilder(response.getBody().toString());
			builder.append("\r\n");

			httpResponse.setContent(ChannelBuffers.copiedBuffer(builder.toString(), Charset.forName(ContentType.ENCODING)));
		}
	}
}
