package com.echo;

import com.strategicgains.restexpress.route.RouteDeclaration;

/**
 * @author toddf
 * @since May 21, 2010
 */
public class Routes
extends RouteDeclaration
{
	private Configuration config;
	
	public Routes(Configuration config)
	{
		super();
		this.config = config;
	}
	
	@Override
	protected void defineRoutes()
	{
		// This route supports POST and PUT, echoing the body in the response.
		uri("/echo/{delay_ms}.{format}", config.getEchoController())
			.noSerialization();

		// Waits the delay_ms number of milliseconds and responds with a 200.
		// Supports GET, PUT, POST, DELETE methods.
		uri("/success/{delay_ms}.{format}", config.getSuccessController());

		// Waits the delay_ms number of milliseconds and responds with the
		// specified HTTP response code.
		// Supports GET, PUT, POST, DELETE methods.
		uri("/status/{delay_ms}/{http_response_code}.{format}", config.getStatusController());
	}
}
