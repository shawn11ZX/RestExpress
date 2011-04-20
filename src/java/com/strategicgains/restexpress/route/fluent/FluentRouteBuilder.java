/*
    Copyright 2011, Strategic Gains, Inc.

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
package com.strategicgains.restexpress.route.fluent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.strategicgains.restexpress.route.Route;
import com.strategicgains.restexpress.route.RouteBuilder;

/**
 * @author toddf
 * @since Apr 20, 2011
 */
public class FluentRouteBuilder
extends RouteBuilder
{
	private List<UriSegment> uriSegments = new ArrayList<UriSegment>();

	/**
     * @param uri
     * @param controller
     */
    public FluentRouteBuilder()
    {
	    super();
    }

	@Override
	protected Route newRoute(String pattern, Object controller, Method action,
	    HttpMethod method, boolean shouldSerializeResponse,
	    boolean shouldUseWrappedResponse, String name,
	    List<String> supportedFormats, String defaultFormat, Set<String> flags,
	    Map<String, Object> parameters)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public FluentRouteBuilder segment(String segment)
	{
		uriSegments.add(new StringUriSegment(segment));
		return this;
	}
	
	public FluentRouteBuilder param(String parameterName)
	{
		uriSegments.add(new ParameterUriSegment(parameterName));
		return this;
	}

	public FluentRouteBuilder regex(String regex)
	{
		uriSegments.add(new RegexUriSegment(regex));
		return this;
	}

	public FluentRouteBuilder doNotInheritFormats()
	{
		return this;
	}

	public FluentRouteBuilder inheritFormats()
	{
		return this;
	}
}
