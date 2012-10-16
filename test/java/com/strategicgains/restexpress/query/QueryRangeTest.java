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
package com.strategicgains.restexpress.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.exception.BadRequestException;

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

	@Test(expected=BadRequestException.class)
	public void shouldThrowExceptionOnNonNumericRange()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=A-24");
		Request request = new Request(httpRequest, null);
		QueryRange.parseFrom(request);
		fail("Did not throw exception as expected.");
	}

	@Test(expected=BadRequestException.class)
	public void shouldThrowExceptionOnReversedRange()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=24-0");
		Request request = new Request(httpRequest, null);
		QueryRange.parseFrom(request);
		fail("Did not throw exception as expected.");
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
		httpRequest.addHeader("Range", "items=0-24");
		// RestExpress parses the query-string into headers.
		httpRequest.addHeader("limit", "100");
		httpRequest.addHeader("offset", "200");
		Request request = new Request(httpRequest, null);
		QueryRange r = QueryRange.parseFrom(request);
		assertEquals(100, r.getLimit());
		assertEquals(200, r.getStart());
		assertEquals(299, r.getEnd());
	}

	@Test
	public void shouldAssembleStringRangeUsingEnd()
	{
		QueryRange r = new QueryRange(1, 25l);
		assertEquals("items 1-25", r.toString());
	}

	@Test
	public void shouldAssembleStringRangeUsingLimit()
	{
		QueryRange r = new QueryRange(0, 25);
		assertEquals("items 0-24", r.toString());
	}

	@Test
	public void shouldCreateAsContentRange()
	{
		QueryRange r = new QueryRange(0, 25);
		assertEquals("items 0-24/25", r.asContentRange(25));
		assertEquals("items 0-19/20", r.asContentRange(20));
		assertEquals("items 0-0/0", r.asContentRange(0));
		assertEquals("items 0-0/1", r.asContentRange(1));
		assertEquals("items 0-1/2", r.asContentRange(2));
	}
}
