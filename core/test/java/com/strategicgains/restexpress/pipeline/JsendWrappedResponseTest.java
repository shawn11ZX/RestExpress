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
import static org.junit.Assert.assertTrue;
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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.response.JsendResponseWrapper;
import com.strategicgains.restexpress.route.RouteDeclaration;
import com.strategicgains.restexpress.serialization.NullSerializationProvider;
import com.strategicgains.restexpress.serialization.SerializationProvider;
import com.strategicgains.restexpress.serialization.json.JacksonJsonProcessor;
import com.strategicgains.restexpress.serialization.xml.XstreamXmlProcessor;


/**
 * @author toddf
 * @since Dec 15, 2010
 */
public class JsendWrappedResponseTest
{
	private static final int SERVER_PORT = 4488;
	private static final String HOST = "http://localhost:" + SERVER_PORT;

	private static RestExpress server = new RestExpress();
	private HttpClient http = new DefaultHttpClient();
	private static WrappedResponseObserver observer;
    private StringBuffer httpResponse;
	
	@BeforeClass
	public static void initialize()
	throws Exception
	{
		SerializationProvider provider = new NullSerializationProvider();
		provider.add(new JacksonJsonProcessor(), new JsendResponseWrapper(), true);
		provider.add(new XstreamXmlProcessor(), new JsendResponseWrapper());
		RestExpress.setSerializationProvider(provider);
		
		DummyRoutes routes = new DummyRoutes();
		routes.defineRoutes(server);
		observer = new WrappedResponseObserver();
		server.addMessageObserver(observer);
		server.bind(SERVER_PORT);
		Thread.sleep(1000);
	}

	@Before
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
	public void shouldWrapGetInJsendJson()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal GET action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapGetInJsendJsonUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal GET action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapGetInJsendXml()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal GET action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapGetInJsendXmlUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/normal_get?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal GET action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapPutInJsendJson()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal PUT action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapPutInJsendJsonUsingQueryString()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal PUT action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapPutInJsendXml()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal PUT action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapPutInJsendXmlUsingQueryString()
	{
		sendEvent(HttpMethod.PUT, HOST + "/normal_put?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal PUT action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapPostInJsendJson()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal POST action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapPostInJsendJsonUsingQueryString()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal POST action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapPostInJsendXml()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal POST action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapPostInJsendXmlUsingQueryString()
	{
		sendEvent(HttpMethod.POST, HOST + "/normal_post?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal POST action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapDeleteInJsendJson()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal DELETE action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapDeleteInJsendJsonUsingQueryString()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":\"Normal DELETE action\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapDeleteInJsendXml()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal DELETE action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapDeleteInJsendXmlUsingQueryString()
	{
		sendEvent(HttpMethod.DELETE, HOST + "/normal_delete?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>200</code>"));
		assertTrue(httpResponse.toString().contains("<status>success</status>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">Normal DELETE action</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapNotFoundInJsendJson()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":404,\"status\":\"error\",\"message\":\"Item not found\",\"data\":\"NotFoundException\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapNotFoundInJsendJsonUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":404,\"status\":\"error\",\"message\":\"Item not found\",\"data\":\"NotFoundException\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapNotFoundInJsendXml()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>404</code>"));
		assertTrue(httpResponse.toString().contains("<status>error</status>"));
		assertTrue(httpResponse.toString().contains("<message>Item not found</message>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">NotFoundException</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapNotFoundInJsendXmlUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/not_found?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>404</code>"));
		assertTrue(httpResponse.toString().contains("<status>error</status>"));
		assertTrue(httpResponse.toString().contains("<message>Item not found</message>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapNullPointerInJsendJson()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":500,\"status\":\"fail\",\"message\":\"Null and void\",\"data\":\"NullPointerException\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapNullPointerInJsendJsonUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":500,\"status\":\"fail\",\"message\":\"Null and void\",\"data\":\"NullPointerException\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapNullPointerInJsendXml()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>500</code>"));
		assertTrue(httpResponse.toString().contains("<status>fail</status>"));
		assertTrue(httpResponse.toString().contains("<message>Null and void</message>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">NullPointerException</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapNullPointerInJsendXmlUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/null_pointer?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>500</code>"));
		assertTrue(httpResponse.toString().contains("<status>fail</status>"));
		assertTrue(httpResponse.toString().contains("<message>Null and void</message>"));
		assertTrue(httpResponse.toString().contains("<data class=\"string\">NullPointerException</data>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapInvalidUrlWithJsonFormat()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt.json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":404,\"status\":\"error\",\"message\":\"Unresolvable URL: " + HOST + "/xyzt.json\",\"data\":\"NotFoundException\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapInvalidUrlWithJsonFormatUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt?format=json", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":404,\"status\":\"error\",\"message\":\"Unresolvable URL: " + HOST + "/xyzt?format=json\",\"data\":\"NotFoundException\"}", httpResponse.toString());
	}

	@Test
	public void shouldWrapInvalidUrlWithXmlFormat()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt.xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>404</code>"));
		assertTrue(httpResponse.toString().contains("<status>error</status>"));
		assertTrue(httpResponse.toString().contains("<message>Unresolvable URL: " + HOST + "/xyzt.xml</message>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
	}

	@Test
	public void shouldWrapInvalidUrlWithXmlFormatUsingQueryString()
	{
		sendEvent(HttpMethod.GET, HOST + "/xyzt?format=xml", null);
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(httpResponse.toString().startsWith("<response>"));
		assertTrue(httpResponse.toString().contains("<code>404</code>"));
		assertTrue(httpResponse.toString().contains("<status>error</status>"));
		assertTrue(httpResponse.toString().contains("<message>Unresolvable URL: " + HOST + "/xyzt?format=xml</message>"));
		assertTrue(httpResponse.toString().endsWith("</response>"));
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
			httpResponse = new StringBuffer(EntityUtils.toString(entity));
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

        	server.uri("/not_found.{format}", controller)
        		.action("notFoundAction", HttpMethod.GET);

        	server.uri("/null_pointer.{format}", controller)
        		.action("nullPointerAction", HttpMethod.GET);
        }
	}
}
