/*
    Copyright 2012, Strategic Gains, Inc.

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
package com.strategicgains.restexpress.postprocessor;

import static org.junit.Assert.*;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.query.QueryRange;

/**
 * @author toddf
 * @since May 24, 2012
 */
public class QueryRangeTest
{
	@Test
	public void shouldParseZeroBasedRange()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=0-24");
		Request request = new Request(httpRequest, null);
		QueryRange r = QueryRange.parseFrom(request);
		assertEquals(25, r.getLimit());
		assertEquals(0, r.getStart());
		assertEquals(24, r.getEnd());
	}
	@Test
	public void shouldParseNextPageRange()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=200-299");
		Request request = new Request(httpRequest, null);
		QueryRange r = QueryRange.parseFrom(request);
		assertEquals(100, r.getLimit());
		assertEquals(200, r.getStart());
		assertEquals(299, r.getEnd());
	}

	@Test
	public void shouldCreateZeroBasedRange()
	{
		QueryRange r = new QueryRange(0, 25);
		assertEquals(25, r.getLimit());
		assertEquals(0, r.getStart());
		assertEquals(24, r.getEnd());
	}

	@Test
	public void shouldCreateNextPageRange()
	{
		QueryRange r = new QueryRange(25, 25);
		assertEquals(25, r.getLimit());
		assertEquals(25, r.getStart());
		assertEquals(49, r.getEnd());
	}

	@Test
	public void shouldFavorQueryStringParameters()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("limit", "100");
		httpRequest.addHeader("offset", "200");
		httpRequest.addHeader("Range", "items=0-24");
		Request request = new Request(httpRequest, null);
		QueryRange r = QueryRange.parseFrom(request);
		assertEquals(100, r.getLimit());
		assertEquals(200, r.getStart());
		assertEquals(299, r.getEnd());
	}
}
