package com.echo.controller;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

/**
 * @author toddf
 * @since Aug 31, 2010
 */
public class EchoController
{
	public String create(Request request, Response response)
	{
		response.setResponseCreated();
		return request.getBodyAs(String.class);
	}

	public String update(Request request, Response response)
	{
		return request.getBodyAs(String.class);
	}
}
