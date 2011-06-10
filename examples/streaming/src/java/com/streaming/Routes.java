package com.streaming;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.streaming.service.StreamingController;
import com.strategicgains.restexpress.route.RouteDeclaration;

/**
 * @author toddf
 * @since May 21, 2010
 */
public class Routes
extends RouteDeclaration
{
	private StreamingController controller;
	
	public Routes()
	{
		super();
		this.controller = new StreamingController();
	}
	
	@Override
	protected void defineRoutes()
	{
		regex("/video/(.*)", controller)
			.method(HttpMethod.GET)
			.useStreamedResponse();

		uri("/videos.{format}", controller)
			.action("directory", HttpMethod.GET);

		uri("/favicon.ico", controller)
			.action("readFavIcon", HttpMethod.GET);
	}
}
