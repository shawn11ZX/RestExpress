/*
    Copyright 2013, Strategic Gains, Inc.

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
package com.strategicgains.restexpress.pipeline;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.strategicgains.restexpress.Request;

/**
 * Converts an incoming HttpRequest into a RestExpress Request in the pipeline.
 * 
 * @author toddf
 * @since Feb 20, 2013
 */
public class RestExpressRequestDecoder
implements ChannelUpstreamHandler
{
	private RequestDecoder decoder = new RequestDecoder();

	@Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event)
    throws Exception
    {
		decoder.handleUpstream(context, event);
    }
	
	private class RequestDecoder
	extends OneToOneDecoder
	{
		@Override
        protected Object decode(ChannelHandlerContext context, Channel channel, Object message)
        throws Exception
        {
			return new Request((HttpRequest) message);
        }
	}
}
