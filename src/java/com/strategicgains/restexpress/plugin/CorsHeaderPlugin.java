/*
 * Copyright 2012, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.restexpress.plugin;

import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.postprocessor.CorsHeaderPostprocessor;

/**
 * @author toddf
 * @since Jun 8, 2012
 */
public class CorsHeaderPlugin
extends AbstractPlugin
{
	private String[] originDomains;

	public CorsHeaderPlugin(String... origins)
	{
		super();
		this.originDomains = origins;
	}

	@Override
	public CorsHeaderPlugin register(RestExpress server)
	{
		if (isRegistered()) return this;

		super.register(server);
		server.addPostprocessor(new CorsHeaderPostprocessor(originDomains));
		return this;
	}

	@Override
	public void bind(RestExpress server)
	{
		// do nothing during bind().
	}
}
