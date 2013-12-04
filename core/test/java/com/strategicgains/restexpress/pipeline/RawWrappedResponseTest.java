/*
    Copyright 2010, Strategic Gains, Inc.

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
package com.strategicgains.restexpress.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import io.netty.handler.codec.http.HttpMethod;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.route.RouteDeclaration;
import com.strategicgains.restexpress.serialization.DefaultSerializationProvider;
import com.strategicgains.restexpress.serialization.SerializationProvider;


/**
 * @author toddf
 * @since Dec 15, 2010
 */
public class RawWrappedResponseTest
{
	private static final int SERVER_PORT = 4489;
	private static final String HOST = "http://localhost:" + SERVER_PORT;

	private static RestExpress server = new RestExpress();
	private HttpClient http = new DefaultHttpClient();
	private static WrappedResponseObserver observer;
    private StringBuffer httpResponse;
	
	@BeforeClass
	public static void initialize()
	throws Exception
	{
		SerializationProvider provider = new DefaultSerializationProvider();
//		provider.put(Format.JSON, new ResponseProcessor(new JacksonJsonProcessor(), new RawResponseWrapper()));
//		resproviderolver.put(Format.XML, new ResponseProcessor(new XstreamXmlProcessor(), new RawResponseWrapper()));
//		provider.setDefaultFormat(Format.JSON);
		
		RestExpress.setSerializationProvider(provider);
		
		DummyRoutes routes = new DummyRoutes();
		routes.defineRoutes(server);
		observer = new WrappedResponseObserver();
		server.addMessageObserver(observer);
		server.bind(SERVER_PORT);
		Thread.sleep(1000);
	}

	@After
	public void reset()
	{
		observer.reset();
	}

	@AfterClass
	public static void shutdown()
	{
		server.shutdown();
	}

	@Test
	public void shouldWrapGetInRawJson()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal GET action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapGetInRawJsonUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal GET action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapGetInRawXml()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal GET action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapGetInRawXmlUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal GET action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapPutInRawJson()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal PUT action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapPutInRawJsonUsingQueryString()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal PUT action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapPutInRawXml()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal PUT action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapPutInRawXmlUsingQueryString()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal PUT action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapPostInRawJson()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal POST action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapPostInRawJsonUsingQueryString()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal POST action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapPostInRawXml()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal POST action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapPostInRawXmlUsingQueryString()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal POST action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapDeleteInRawJson()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal DELETE action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapDeleteInRawJsonUsingQueryString()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal DELETE action\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapDeleteInRawXml()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal DELETE action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapDeleteInRawXmlUsingQueryString()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Normal DELETE action</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapNotFoundInRawJson()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Item not found\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapNotFoundInRawJsonUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Item not found\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapNotFoundInRawXml()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Item not found</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapNotFoundInRawXmlUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Item not found</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapNullPointerInRawJson()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Null and void\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapNullPointerInRawJsonUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Null and void\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapNullPointerInRawXml()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Null and void</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapNullPointerInRawXmlUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Null and void</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapInvalidUrlWithRawJson()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Unresolvable URL: " + HOST + "/xyzt.json\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapInvalidUrlWithRawJsonUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Unresolvable URL: " + HOST + "/xyzt?format=json\"", httpResponse.toString());
	}

	@Test
	public void shouldWrapInvalidUrlWithRawXml()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Unresolvable URL: " + HOST + "/xyzt.xml</string>", httpResponse.toString());
	}

	@Test
	public void shouldWrapInvalidUrlWithXmlUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("<string>Unresolvable URL: " + HOST + "/xyzt?format=xml</string>", httpResponse.toString());
	}

	@Test
	public void shouldDeleteWithoutContent()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/no_content_delete.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertNull(httpResponse);
	}

	@Test
	public void shouldThrowExceptionOnDeleteNoContentContainingBody()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/no_content_with_body_delete.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
	}

	@Test
	public void shouldDeleteIgnoringJsonp()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete.json?jsonp=jsonp_callback", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Normal DELETE action\"", httpResponse.toString());
	}

	private void sendEvent(HttpMethod method, String path, String body)
    {
		HttpRequestBase request = null;
		
		if (HttpMethod.GET.equals(method))
		{
			request = new HttpGet(path);
		}
		else if (HttpMethod.DELETE.equals(method))
		{
			request = new HttpDelete(path);
		}
		else if (HttpMethod.PUT.equals(method))
		{
			request = new HttpPut(path);
		}
		else if (HttpMethod.POST.equals(method))
		{
			request = new HttpPost(path);
		}

		if (body != null)
		{
			if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method))
			{
				try
                {
	                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
                }
                catch (UnsupportedEncodingException e)
                {
	                e.printStackTrace();
                }
			}
			else
			{
				fail("Only POST and PUT support request bodies.");
			}
		}
		
		try
		{
			HttpResponse response = (HttpResponse) http.execute(request);
			HttpEntity entity = response.getEntity();
			httpResponse = (entity == null ) ? null : new StringBuffer(EntityUtils.toString(entity));
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		finally
		{
			request.releaseConnection();
		}
    }
	
	public static class DummyRoutes
	extends RouteDeclaration
	{
		private Object controller = new WrappedResponseController();

        public void defineRoutes(RestExpress server)
        {
        	server.uri("/normal_get.{format}", controller)
        		.action("normalGetAction", HttpMethod.GET);

        	server.uri("/normal_put.{format}", controller)
    			.action("normalPutAction", HttpMethod.PUT);

        	server.uri("/normal_post.{format}", controller)
    			.action("normalPostAction", HttpMethod.POST);

        	server.uri("/normal_delete.{format}", controller)
    			.action("normalDeleteAction", HttpMethod.DELETE);

        	server.uri("/no_content_delete.{format}", controller)
    			.action("noContentDeleteAction", HttpMethod.DELETE);

        	server.uri("/no_content_with_body_delete.{format}", controller)
    			.action("noContentWithBodyDeleteAction", HttpMethod.DELETE);

        	server.uri("/not_found.{format}", controller)
        		.action("notFoundAction", HttpMethod.GET);

        	server.uri("/null_pointer.{format}", controller)
        		.action("nullPointerAction", HttpMethod.GET);
        }
	}
}
