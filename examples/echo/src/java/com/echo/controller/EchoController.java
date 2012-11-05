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
    private static final String ECHO_PARAMETER_NOT_FOUND = "'echo' header or query-string parameter not found";
	private static final String ECHO_HEADER = "echo";

	public ChannelBuffer create(Request request, Response response)
	{
		delay(request);
		response.setResponseCreated();
		return request.getBody();
	}
	
	public String delete(Request request, Response response)
	{
		delay(request);
		return request.getUrlDecodedHeader(ECHO_HEADER, ECHO_PARAMETER_NOT_FOUND);
	}
	
	public String read(Request request, Response response)
	{
//		delay(request);
//		return request.getUrlDecodedHeader(ECHO_HEADER, ECHO_PARAMETER_NOT_FOUND);
		
		// the following mimics the nodeJS echo.js program for benchmarking.
		response.setContentType("text/xml");
		String echo = request.getUrlDecodedHeader(ECHO_HEADER);
		
		if (echo == null)
		{
			return "<http_test><error>no value specified</error></http_test>";
		}
		
		return String.format("<http_test><value>%s</value></http_test>", echo);
	}

	public ChannelBuffer update(Request request, Response response)
	{
		delay(request);
		return request.getBody();
	}
}
