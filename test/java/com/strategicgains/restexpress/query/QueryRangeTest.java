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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.common.query.QueryRange;
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
		Request request = new Request(httpRequest);
		QueryRange r = QueryRanges.parseFrom(request);
		assertEquals(25, r.getLimit());
		assertEquals(0, r.getOffset());
		assertEquals(24, r.getEnd());
	}

	@Test
	public void shouldHandleNullOffset()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("limit", "5");
		Request request = new Request(httpRequest);
		QueryRange r = QueryRanges.parseFrom(request);
		assertEquals(5, r.getLimit());
		assertEquals(0, r.getOffset());
		assertEquals(4, r.getEnd());
	}

	@Test(expected=BadRequestException.class)
	public void shouldThrowOnNullLimitWithOffset()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("offset", "25");
		Request request = new Request(httpRequest);
		QueryRanges.parseFrom(request);
		fail("Should have thrown");
	}

	@Test
	public void shouldHandleNullLimitWithDefault()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("offset", "25");
		Request request = new Request(httpRequest);
		QueryRange r = QueryRanges.parseFrom(request, 5);
		assertEquals(5, r.getLimit());
		assertEquals(25, r.getOffset());
		assertEquals(29, r.getEnd());
	}

	@Test(expected=BadRequestException.class)
	public void shouldThrowExceptionOnNonNumericRange()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=A-24");
		Request request = new Request(httpRequest);
		QueryRanges.parseFrom(request);
		fail("Did not throw exception as expected.");
	}

	@Test(expected=BadRequestException.class)
	public void shouldThrowExceptionOnReversedRange()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=24-23");
		Request request = new Request(httpRequest);
		QueryRanges.parseFrom(request);
		fail("Did not throw exception as expected.");
	}

	@Test(expected=BadRequestException.class)
	public void shouldThrowExceptionOnReversedRangeAroundZero()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=1-0");
		Request request = new Request(httpRequest);
		QueryRanges.parseFrom(request);
		fail("Did not throw exception as expected.");
	}

	@Test
	public void shouldParseNextPageRange()
	{
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.example.com/somethings");
		httpRequest.addHeader("Range", "items=200-299");
		Request request = new Request(httpRequest);
		QueryRange r = QueryRanges.parseFrom(request);
		assertEquals(100, r.getLimit());
		assertEquals(200, r.getStart());
		assertEquals(299, r.getEnd());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowOnZeroLimitInConstructor()
	{
		new QueryRange(1, 0);
		fail("did not throw");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowOnZeroLimit()
	{
		QueryRange r = new QueryRange();
		r.setOffset(1);
		r.setLimit(0);
		fail("did not throw");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowOnNegativeOffset()
	{
		QueryRange r = new QueryRange();
		r.setOffset(-1);
		fail("did not throw");
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
		Request request = new Request(httpRequest);
		QueryRange r = QueryRanges.parseFrom(request);
		assertEquals(100, r.getLimit());
		assertEquals(200, r.getStart());
		assertEquals(299, r.getEnd());
	}

	@Test
	public void shouldAssembleStringRange()
	{
		QueryRange r = new QueryRange(0, 25);
		assertEquals("items 0-24", r.toString());

		r.setStart(1);
		r.setLimitViaEnd(25l);
		assertEquals(25, r.getLimit());
		assertEquals(25, r.getEnd());
		assertEquals("items 1-25", r.toString());

		r.setStart(0l);
		r.setLimitViaEnd(4l);
		assertEquals(5, r.getLimit());
		assertEquals(4, r.getEnd());
		assertEquals("items 0-4", r.toString());
		
		r.setStart(15l);
		r.setLimitViaEnd(19l);
		assertEquals(5, r.getLimit());
		assertEquals(19, r.getEnd());
		assertEquals("items 15-19", r.toString());

		r.setStart(1l);
		r.setLimitViaEnd(1l);
		assertEquals("items 1-1", r.toString());
	}

	@Test
	public void shouldCreateAsContentRange()
	{
		QueryRange r = new QueryRange(0, 25);
		assertEquals("items 0-24/25", r.asContentRange(25));
		assertEquals("items 0-24/20", r.asContentRange(20));
		assertEquals("items 0-24/0", r.asContentRange(0));
		assertEquals("items 0-24/1", r.asContentRange(1));
		assertEquals("items 0-24/2", r.asContentRange(2));
	}

	@Test
	public void shouldExtendBeyond()
	{
		QueryRange r= new QueryRange(0, 3);
		assertTrue(r.extendsBeyond(0, 0l));
		assertFalse(r.extendsBeyond(0, 2l));
		assertFalse(r.extendsBeyond(0, 3l));
		assertFalse(r.extendsBeyond(0, 4l));
		assertTrue(r.extendsBeyond(1, 2l));
		assertFalse(r.extendsBeyond(1, 3l));
		assertFalse(r.extendsBeyond(1, 4l));
		assertTrue(r.extendsBeyond(2, 2l));
		assertFalse(r.extendsBeyond(2, 3l));
		assertFalse(r.extendsBeyond(2, 4l));

		r= new QueryRange(2, 5);
		assertTrue(r.extendsBeyond(0, 0l));
		assertFalse(r.extendsBeyond(0, 5l));
		assertFalse(r.extendsBeyond(0, 6l));
		assertFalse(r.extendsBeyond(0, 7l));
		assertFalse(r.extendsBeyond(0, 8l));
		assertTrue(r.extendsBeyond(3, 5l));
		assertTrue(r.extendsBeyond(3, 6l));
		assertFalse(r.extendsBeyond(3, 7l));
		assertFalse(r.extendsBeyond(3, 8l));
	}

	@Test
	public void shouldSpan()
	{
		QueryRange r= new QueryRange(0, 3);
		assertFalse(r.spans(0,  0l));
		assertFalse(r.spans(0,  2l));
		assertFalse(r.spans(0,  3l));
		assertFalse(r.spans(0,  4l));
		assertTrue(r.spans(1,  1l));
		assertTrue(r.spans(2,  2l));
		assertTrue(r.spans(3,  3l));
		assertTrue(r.spans(4,  4l));
		assertFalse(r.spans(4,  3l));
		assertFalse(r.spans(2,  3l));
		assertFalse(r.spans(1,  3l));
		
		r = new QueryRange(2, 5);
		assertFalse(r.spans(0,  6l));
		assertFalse(r.spans(0,  7l));
		assertFalse(r.spans(0,  8l));
		assertFalse(r.spans(1,  5l));
		assertFalse(r.spans(2,  5l));
		assertFalse(r.spans(3,  5l));
	}

	@Test
	public void shouldBeOutside()
	{
		QueryRange r= new QueryRange(0, 3);
		assertFalse(r.isOutside(0, 0l));
		assertTrue(r.isOutside(0, 1l));
		assertTrue(r.isOutside(0, 2l));
		assertTrue(r.isOutside(0, 3l));
		assertTrue(r.isOutside(0, 4l));
		assertFalse(r.isOutside(1, 1l));
		assertFalse(r.isOutside(1, 2l));
		assertFalse(r.isOutside(1, 3l));
		assertFalse(r.isOutside(1, 4l));
		assertFalse(r.isOutside(2, 3l));
		assertFalse(r.isOutside(3, 3l));
		assertFalse(r.isOutside(3, 3l));
		
		r = new QueryRange(2, 5);
		assertFalse(r.isOutside(0, 0l));
		assertTrue(r.isOutside(0, 1l));
		assertTrue(r.isOutside(0, 6l));
		assertTrue(r.isOutside(0, 7l));
		assertTrue(r.isOutside(0, 8l));
		assertFalse(r.isOutside(1, 1l));
		assertFalse(r.isOutside(1, 6l));
		assertFalse(r.isOutside(1, 7l));
		assertFalse(r.isOutside(1, 8l));
		assertFalse(r.isOutside(6, 6l));
		assertFalse(r.isOutside(7, 7l));
		assertFalse(r.isOutside(8, 8l));
	}

	@Test
	public void shouldBeInside()
	{
		QueryRange r= new QueryRange(0, 5);
		assertFalse(r.isInside(0, 4l));
		assertFalse(r.isInside(0, 5l));
		assertFalse(r.isInside(0, 6l));
		assertTrue(r.isInside(5, 7l));
		assertTrue(r.isInside(5, 6l));
		assertFalse(r.isInside(5, 5l));
		assertTrue(r.isInside(4, 5l));

		r = new QueryRange(2, 5);
		assertFalse(r.isInside(0, 4l));
		assertFalse(r.isInside(0, 5l));
		assertFalse(r.isInside(0, 6l));
		assertTrue(r.isInside(4, 7l));
		assertTrue(r.isInside(4, 8l));
		assertTrue(r.isInside(5, 9l));
		assertTrue(r.isInside(5, 8l));
		assertTrue(r.isInside(5, 7l));
		assertFalse(r.isInside(5, 6l));		

		r = new QueryRange(1l, 2);
		assertTrue(r.isInside(2, 3l));
	}
}
