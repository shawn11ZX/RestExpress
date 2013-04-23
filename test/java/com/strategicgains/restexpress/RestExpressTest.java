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
package com.strategicgains.restexpress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.strategicgains.restexpress.response.ResponseProcessor;


/**
 * @author toddf
 * @since Jan 28, 2011
 */
public class RestExpressTest
{
	private RestExpress server = new RestExpress();

	@Test
	public void shouldUseDefaults()
	{
		assertEquals(Format.JSON, server.getDefaultFormat());
		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(2, server.getResponseProcessors().size());

//		assertEquals(8081, server.getPort());
		assertEquals(0, server.getPort());
		assertTrue(server.getMessageObservers().isEmpty());
		assertTrue(server.getPostprocessors().isEmpty());
		assertTrue(server.getPreprocessors().isEmpty());
		assertTrue(server.shouldUseSystemOut());
//		assertNotNull(server.getRouteDeclarations());
	}
	
	@Test
	public void shouldDisableJson()
	{
		server.noJson();
		assertEquals(Format.JSON, server.getDefaultFormat());
		assertFalse(server.getResponseProcessors().containsKey(Format.JSON));
		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(1, server.getResponseProcessors().size());
	}
	
	@Test
	public void shouldDisableXml()
	{
		server.noXml();
		assertEquals(Format.JSON, server.getDefaultFormat());
		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
		assertFalse(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(1, server.getResponseProcessors().size());
	}
	
	@Test
	public void shouldMakeXmlDefault()
	{
		server.supportXml(true);
		assertEquals(Format.XML, server.getDefaultFormat());
		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(2, server.getResponseProcessors().size());
	}

	@Test
	public void shouldCustomizeJsonSerializer()
	{
		server.putResponseProcessor(Format.JSON, ResponseProcessor.defaultJsonProcessor());
		assertEquals(Format.JSON, server.getDefaultFormat());
		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(2, server.getResponseProcessors().size());
	}

	@Test
	public void shouldCustomizeXmlSerializer()
	{
		server.putResponseProcessor(Format.XML, ResponseProcessor.defaultXmlProcessor());
		assertEquals(Format.JSON, server.getDefaultFormat());
		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(2, server.getResponseProcessors().size());
	}

	@Test
	public void shouldNotUpdateJsonSerializer()
	{
		ResponseProcessor rp = ResponseProcessor.defaultJsonProcessor();
		server.putResponseProcessor(Format.JSON, rp);
		server.supportJson(true);
		assertEquals(Format.JSON, server.getDefaultFormat());
		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(2, server.getResponseProcessors().size());
		
		assertTrue(rp == server.getResponseProcessors().get(Format.JSON));
	}

	@Test
	public void shouldNotUpdateXmlSerializer()
	{
		ResponseProcessor rp = ResponseProcessor.defaultXmlProcessor();
		server.putResponseProcessor(Format.XML, rp);
		server.supportXml(true);
		assertEquals(Format.XML, server.getDefaultFormat());
		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
		assertEquals(2, server.getResponseProcessors().size());
		
		assertTrue(rp == server.getResponseProcessors().get(Format.XML));
	}
	
	@Test
	public void shouldNotUseSystemOut()
	{
		server.noSystemOut();
		assertFalse(server.shouldUseSystemOut());
	}
}
