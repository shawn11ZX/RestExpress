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
package org.restexpress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.restexpress.exception.NoRoutesDefinedException;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;


/**
 * @author toddf
 * @since Jan 28, 2011
 */
public class RestExpressTest
{
	private static final String TEST_PATH = "/restexpress/test1";
	private static final int TEST_PORT = 8901;
	private static final String TEST_URL = "http://localhost:" + TEST_PORT + TEST_PATH;

//	@Test
//	public void shouldUseDefaults()
//	{
//		assertEquals(Format.JSON, server.getDefaultFormat());
//		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
//		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(2, server.getResponseProcessors().size());
//
//		assertEquals(0, server.getPort());
//		assertTrue(server.getMessageObservers().isEmpty());
//		assertTrue(server.getPostprocessors().isEmpty());
//		assertTrue(server.getPreprocessors().isEmpty());
//		assertTrue(server.shouldUseSystemOut());
//	}

//	@Test
//	public void shouldDisableJson()
//	{
//		server.noJson();
//		assertEquals(Format.JSON, server.getDefaultFormat());
//		assertFalse(server.getResponseProcessors().containsKey(Format.JSON));
//		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(1, server.getResponseProcessors().size());
//	}
	
//	@Test
//	public void shouldDisableXml()
//	{
//		server.noXml();
//		assertEquals(Format.JSON, server.getDefaultFormat());
//		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
//		assertFalse(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(1, server.getResponseProcessors().size());
//	}
	
//	@Test
//	public void shouldMakeXmlDefault()
//	{
//		server.supportXml(true);
//		assertEquals(Format.XML, server.getDefaultFormat());
//		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
//		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(2, server.getResponseProcessors().size());
//	}

//	@Test
//	public void shouldCustomizeJsonSerializer()
//	{
//		server.putResponseProcessor(Format.JSON, provider.newProcessor(Format.JSON));
//		assertEquals(Format.JSON, server.getDefaultFormat());
//		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
//		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(2, server.getResponseProcessors().size());
//	}

//	@Test
//	public void shouldCustomizeXmlSerializer()
//	{
//		server.putResponseProcessor(Format.XML, provider.newProcessor(Format.XML));
//		assertEquals(Format.JSON, server.getDefaultFormat());
//		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
//		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(2, server.getResponseProcessors().size());
//	}

//	@Test
//	public void shouldNotUpdateJsonSerializer()
//	{
//		ResponseProcessor rp = provider.newProcessor(Format.JSON);
//		server.putResponseProcessor(Format.JSON, rp);
//		server.supportJson(true);
//		assertEquals(Format.JSON, server.getDefaultFormat());
//		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
//		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(2, server.getResponseProcessors().size());
//		
//		assertTrue(rp == server.getResponseProcessors().get(Format.JSON));
//	}

//	@Test
//	public void shouldNotUpdateXmlSerializer()
//	{
//		ResponseProcessor rp = provider.newProcessor(Format.XML);
//		server.putResponseProcessor(Format.XML, rp);
//		server.supportXml(true);
//		assertEquals(Format.XML, server.getDefaultFormat());
//		assertTrue(server.getResponseProcessors().containsKey(Format.JSON));
//		assertTrue(server.getResponseProcessors().containsKey(Format.XML));
//		assertEquals(2, server.getResponseProcessors().size());
//		
//		assertTrue(rp == server.getResponseProcessors().get(Format.XML));
//	}
	
	@Test
	public void shouldNotUseSystemOut()
	{
		RestExpress re = new RestExpress();
		re.noSystemOut();
		assertFalse(re.shouldUseSystemOut());
	}

	@Test
	public void shouldCallDefaultMethods()
	throws ClientProtocolException, IOException
	{
		RestExpress re = new RestExpress();
		NoopController controller = new NoopController();
		re.uri(TEST_PATH, controller);
		re.bind(TEST_PORT);
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(TEST_URL);
		HttpResponse response = (HttpResponse) client.execute(get);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(1, controller.read);
		assertEquals(0, controller.create);
		assertEquals(0, controller.update);
		assertEquals(0, controller.delete);
		assertEquals(0, controller.options);
		assertEquals(0, controller.head);
		assertEquals(0, controller.patch);
		get.releaseConnection();
		
		HttpPost post = new HttpPost(TEST_URL);
		response = (HttpResponse) client.execute(post);
		assertEquals(201, response.getStatusLine().getStatusCode());
		assertEquals(1, controller.create);
		assertEquals(1, controller.read);
		assertEquals(0, controller.update);
		assertEquals(0, controller.delete);
		assertEquals(0, controller.options);
		assertEquals(0, controller.head);
		assertEquals(0, controller.patch);
		post.releaseConnection();

		HttpPut put = new HttpPut(TEST_URL);
		response = (HttpResponse) client.execute(put);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(1, controller.update);
		assertEquals(1, controller.read);
		assertEquals(1, controller.create);
		assertEquals(0, controller.delete);
		assertEquals(0, controller.options);
		assertEquals(0, controller.head);
		assertEquals(0, controller.patch);
		put.releaseConnection();

		HttpDelete delete = new HttpDelete(TEST_URL);
		response = (HttpResponse) client.execute(delete);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(1, controller.delete);
		assertEquals(1, controller.read);
		assertEquals(1, controller.create);
		assertEquals(1, controller.update);
		assertEquals(0, controller.options);
		assertEquals(0, controller.head);
		assertEquals(0, controller.patch);
		delete.releaseConnection();

		HttpOptions options = new HttpOptions(TEST_URL);
		response = (HttpResponse) client.execute(options);
		assertEquals(405, response.getStatusLine().getStatusCode());
		options.releaseConnection();

		re.shutdown();
	}

	@Test
	public void shouldCallAltMethods()
	throws ClientProtocolException, IOException
	{
		RestExpress re = new RestExpress();
		NoopController controller = new NoopController();
		re.uri(TEST_PATH, controller)
			.method(HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.PATCH);
		re.bind(TEST_PORT);
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(TEST_URL);
		HttpResponse response = (HttpResponse) client.execute(get);
		assertEquals(405, response.getStatusLine().getStatusCode());
		get.releaseConnection();

		HttpOptions options = new HttpOptions(TEST_URL);
		response = (HttpResponse) client.execute(options);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(0, controller.delete);
		assertEquals(0, controller.read);
		assertEquals(0, controller.create);
		assertEquals(0, controller.update);
		assertEquals(1, controller.options);
		assertEquals(0, controller.head);
		assertEquals(0, controller.patch);
		options.releaseConnection();

		HttpHead head = new HttpHead(TEST_URL);
		response = (HttpResponse) client.execute(head);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(0, controller.delete);
		assertEquals(0, controller.read);
		assertEquals(0, controller.create);
		assertEquals(0, controller.update);
		assertEquals(1, controller.options);
		assertEquals(1, controller.head);
		assertEquals(0, controller.patch);
		head.releaseConnection();

		HttpPatch patch = new HttpPatch(TEST_URL);
		response = (HttpResponse) client.execute(patch);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(0, controller.delete);
		assertEquals(0, controller.read);
		assertEquals(0, controller.create);
		assertEquals(0, controller.update);
		assertEquals(1, controller.options);
		assertEquals(1, controller.head);
		assertEquals(1, controller.patch);
		patch.releaseConnection();

		re.shutdown();
	}

	@Test
	public void shouldCallAltNamedMethods()
	throws ClientProtocolException, IOException
	{
		RestExpress re = new RestExpress();
		AltController controller = new AltController();
		re.uri(TEST_PATH, controller)
			.action("altHead", HttpMethod.HEAD)
			.action("altOptions", HttpMethod.OPTIONS)
			.action("altPatch", HttpMethod.PATCH);
		re.bind(TEST_PORT);
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(TEST_URL);
		HttpResponse response = (HttpResponse) client.execute(get);
		assertEquals(405, response.getStatusLine().getStatusCode());
		get.releaseConnection();

		HttpOptions options = new HttpOptions(TEST_URL);
		response = (HttpResponse) client.execute(options);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(1, controller.options);
		assertEquals(0, controller.head);
		assertEquals(0, controller.patch);
		options.releaseConnection();

		HttpHead head = new HttpHead(TEST_URL);
		response = (HttpResponse) client.execute(head);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(1, controller.options);
		assertEquals(1, controller.head);
		assertEquals(0, controller.patch);
		head.releaseConnection();

		HttpPatch patch = new HttpPatch(TEST_URL);
		response = (HttpResponse) client.execute(patch);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(1, controller.options);
		assertEquals(1, controller.head);
		assertEquals(1, controller.patch);
		patch.releaseConnection();

		re.shutdown();
	}

	@Test
	public void shouldSetOutputMediaType()
	throws ClientProtocolException, IOException
	{
		RestExpress re = new RestExpress();
		NoopController controller = new NoopController();
		re.uri(TEST_PATH, controller);
		re.bind(TEST_PORT);

		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost(TEST_URL);
		post.addHeader(HttpHeaders.Names.ACCEPT, "application/json");
		HttpResponse response = (HttpResponse) client.execute(post);
		assertEquals(201, response.getStatusLine().getStatusCode());
		assertEquals(ContentType.JSON, controller.outputMediaType);
		post.releaseConnection();

		HttpGet get = new HttpGet(TEST_URL);
		get.addHeader(HttpHeaders.Names.ACCEPT, "application/json");
		response = (HttpResponse) client.execute(get);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(ContentType.JSON, controller.outputMediaType);
		get.releaseConnection();

		HttpPut put = new HttpPut(TEST_URL);
		put.addHeader(HttpHeaders.Names.ACCEPT, "application/json");
		response = (HttpResponse) client.execute(put);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(ContentType.JSON, controller.outputMediaType);
		put.releaseConnection();

		HttpDelete delete = new HttpDelete(TEST_URL);
		delete.addHeader(HttpHeaders.Names.ACCEPT, "application/json");
		response = (HttpResponse) client.execute(delete);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(ContentType.JSON, controller.outputMediaType);
		delete.releaseConnection();

		re.shutdown();
	}

	@Test(expected=NoRoutesDefinedException.class)
	public void shouldThrowNoRoutesDefinedException()
	{
		RestExpress re = null;

		try
		{
			re = new RestExpress();
			re.bind(TEST_PORT);
		}
		finally
		{
			re.shutdown();
		}
	}

	public class NoopController
    {
		int create, read, update, delete, options, head, patch = 0;
		String outputMediaType;

		public void create(Request req, Response res)
		{
			++create;
			res.setResponseCreated();
			outputMediaType = res.getSerializationSettings().getMediaType();
		}

		public void read(Request req, Response res)
		{
			++read;
			outputMediaType = res.getSerializationSettings().getMediaType();
		}

		public void update(Request req, Response res)
		{
			++update;
			outputMediaType = res.getSerializationSettings().getMediaType();
		}

		public void delete(Request req, Response res)
		{
			++delete;
			outputMediaType = res.getSerializationSettings().getMediaType();
		}

		public void options(Request req, Response res)
		{
			++options;
		}

		public void head(Request req, Response res)
		{
			++head;
		}

		public void patch(Request req, Response res)
		{
			++patch;
		}
    }

	public class AltController
    {
		int options, head, patch = 0;
		String outputMediaType;

		public void altOptions(Request req, Response res)
		{
			++options;
		}

		public void altHead(Request req, Response res)
		{
			++head;
		}

		public void altPatch(Request req, Response res)
		{
			++patch;
		}
    }
}
