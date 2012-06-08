/*
 * Copyright 2012, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.restexpress.postprocessor;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.pipeline.Postprocessor;
import com.strategicgains.restexpress.util.StringUtils;

/**
 * @author toddf
 * @since Jun 8, 2012
 */
public class CorsHeaderPostprocessor
implements Postprocessor
{
    private static final String CORS_ORIGIN_HEADER = "Access-Control-Allow-Origin";

    private String corsHeader = null;

	public CorsHeaderPostprocessor(String... originDomains)
	{
		super();
		corsHeader = StringUtils.join(" ", (Object[]) originDomains);
	}

	@Override
	public void process(Request request, Response response)
	{
		if (request.isMethodGet() && corsHeader != null)
		{
			response.addHeader(CORS_ORIGIN_HEADER, corsHeader);
		}
	}
}
