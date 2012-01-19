package com.echo.controller;

import org.jboss.netty.buffer.ChannelBuffer;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

/**
 * @author toddf
 * @since Aug 31, 2010
 */
public class EchoController
extends AbstractDelayingController
{
	/**
     * 
     */
    private static final String ECHO_PARAMETER_NOT_FOUND = "'echo' query-string parameter not found";
	private static final String ECHO_HEADER = "echo";

	public ChannelBuffer create(Request request, Response response)
	{
		delay(request);
		response.setResponseCreated();
		return request.getBody(); //.toString(ContentType.CHARSET);
	}
	
	public String delete(Request request, Response response)
	{
		delay(request);
		return request.getUrlDecodedHeader(ECHO_HEADER, ECHO_PARAMETER_NOT_FOUND);
	}
	
	public String read(Request request, Response response)
	{
		delay(request);
		return request.getUrlDecodedHeader(ECHO_HEADER, ECHO_PARAMETER_NOT_FOUND);
	}

	public ChannelBuffer update(Request request, Response response)
	{
		delay(request);
		return request.getBody(); //.toString(ContentType.CHARSET);
	}
}
