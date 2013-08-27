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
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.strategicgains.restexpress.ContentType;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.exception.BadRequestException;
import com.strategicgains.restexpress.response.JsendResponseWrapper;
import com.strategicgains.restexpress.response.RawResponseWrapper;
import com.strategicgains.restexpress.serialization.NullSerializationProvider;
import com.strategicgains.restexpress.serialization.SerializationProvider;
import com.strategicgains.restexpress.serialization.json.JacksonJsonProcessor;
import com.strategicgains.restexpress.serialization.xml.XstreamXmlProcessor;


/**
 * @author toddf
 * @since Dec 15, 2010
 */
public class DefaultRequestHandlerTest
{
	private static final int SERVER_PORT = 4444;
	private RestExpress server = new RestExpress();
	private HttpClient http = new DefaultHttpClient();
	private DummyObserver observer;
	private StringBuffer responseBody;
	private Map<String, List<String>> responseHeaders;
	
	@Before
	public void initialize()
	throws Exception
	{
		SerializationProvider provider = new NullSerializationProvider();
		provider.add(new JacksonJsonProcessor(Format.JSON), new RawResponseWrapper());
		provider.add(new JacksonJsonProcessor(Format.WRAPPED_JSON), new JsendResponseWrapper());
		provider.add(new XstreamXmlProcessor(Format.XML), new JsendResponseWrapper());
		provider.alias("dated", Dated.class);
		provider.setDefaultFormat(Format.WRAPPED_JSON);
		RestExpress.setSerializationProvider(provider);
		
		DummyRoutes routes = new DummyRoutes();
		routes.defineRoutes(server);
		observer = new DummyObserver();
		server.addMessageObserver(observer);
		responseBody = new StringBuffer();
		responseHeaders = new HashMap<String, List<String>>();
	}

	@After
	public void shutdown()
	{
		server.shutdown();
	}

	@Test
	public void shouldReturnTextPlainContentTypeByDefault()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/unserializedDefault");
		assertEquals(0, observer.getExceptionCount());
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
//		System.out.println(responseBody.toString());
		assertEquals("should be text plain, here", responseBody.toString());
		assertTrue(responseHeaders.containsKey("Content-Type"));
		List<String> contentTypes = responseHeaders.get(HttpHeaders.Names.CONTENT_TYPE);
		assertEquals(1, contentTypes.size());
		assertEquals("text/plain; charset=UTF-8", contentTypes.get(0));
	}

	@Test
	public void shouldAllowSettingOfContentType()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/unserialized");
		assertEquals(0, observer.getExceptionCount());
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
//		System.out.println(responseBody.toString());
		assertEquals("<html><body>Some kinda wonderful!</body></html>", responseBody.toString());
		assertTrue(responseHeaders.containsKey("Content-Type"));
		List<String> contentTypes = responseHeaders.get(HttpHeaders.Names.CONTENT_TYPE);
		assertEquals(1, contentTypes.size());
		assertEquals("text/html", contentTypes.get(0));
	}

	@Test
	public void shouldAllowSettingOfContentTypeViaHeader()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/unserializedToo");
		assertEquals(0, observer.getExceptionCount());
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
//		System.out.println(responseBody.toString());
		assertEquals("<html><body>Wow! What a fabulous HTML body...</body></html>", responseBody.toString());
		List<String> contentTypes = responseHeaders.get(HttpHeaders.Names.CONTENT_TYPE);
		assertEquals(1, contentTypes.size());
		assertEquals("text/html", contentTypes.get(0));
	}

	@Test
	public void shouldAllowSettingOfArbitraryBody()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/setBodyAction.html");
		assertEquals(0, observer.getExceptionCount());
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
//		System.out.println(responseBody.toString());
		assertEquals("<html><body>Arbitrarily set HTML body...</body></html>", responseBody.toString());
		List<String> contentTypes = responseHeaders.get(HttpHeaders.Names.CONTENT_TYPE);
		assertEquals(1, contentTypes.size());
		assertEquals(ContentType.HTML, contentTypes.get(0));
	}

	@Test
	public void shouldNotifyObserverOnSuccess()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/foo");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\"}", responseBody.toString());
	}

	@Test
	public void shouldUrlDecodeUrlParameters()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/foo/Todd%7CFredrich%2Bwas%20here.json");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("\"Todd|Fredrich+was here\"", responseBody.toString());
	}

	@Test
	public void shouldNotifyObserverOnError()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/bar");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getExceptionCount());
		assertEquals(0, observer.getSuccessCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":400,\"status\":\"error\",\"message\":\"foobar'd\",\"data\":\"BadRequestException\"}", responseBody.toString());
	}

	@Test
	public void shouldHandleNonDecodableValueInQueryString()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/bar?value=%target");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getExceptionCount());
		assertEquals(0, observer.getSuccessCount());
//		System.out.println(responseBody.toString());
		assertEquals("{\"code\":400,\"status\":\"error\",\"message\":\"foobar'd\",\"data\":\"BadRequestException\"}", responseBody.toString());
	}

	@Test
	public void shouldHandleUrlDecodeErrorInFormat()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/foo.%target");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getExceptionCount());
		assertEquals(0, observer.getSuccessCount());
//		System.out.println(httpResponse.toString());
//		assertEquals("{\"code\":400,\"status\":\"error\",\"message\":\"foobar'd\",\"data\":\"BadRequestException\"}", responseBody.toString());
		assertEquals("{\"code\":400,\"status\":\"error\",\"message\":\"Requested representation format not supported: %target. Supported formats: json, wjson, xml\",\"data\":\"BadRequestException\"}", responseBody.toString());
	}

	@Test
	public void shouldShouldThrowExceptionForErrorInFormat()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/foo.unsupported");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getExceptionCount());
		assertEquals(0, observer.getSuccessCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":400,\"status\":\"error\",\"message\":\"Requested representation format not supported: unsupported. Supported formats: json, wjson, xml\",\"data\":\"BadRequestException\"}", responseBody.toString());
	}

	@Test
	public void shouldHandleInvalidDecodeInQueryString()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/foo?value=%target");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\"}", responseBody.toString());
	}

	@Test
	public void shouldHandleUrlDecodeErrorInUrl()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/%bar");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getExceptionCount());
		assertEquals(0, observer.getSuccessCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":404,\"status\":\"error\",\"message\":\"Unresolvable URL: http://null/%bar\",\"data\":\"NotFoundException\"}", responseBody.toString());
	}

	@Test
	public void shouldParseTimepointJson()
	{
		server.bind(SERVER_PORT);
		sendPostEvent("/date.wjson", "{\"at\":\"2010-12-17T120000Z\"}");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":{\"at\":\"2010-12-17T12:00:00.000Z\"}}", responseBody.toString());
	}

	@Test
	public void shouldParseTimepointJsonUsingQueryString()
	{
		server.bind(SERVER_PORT);
		sendPostEvent("/date?format=wjson", "{\"at\":\"2010-12-17T120000Z\"}");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertEquals("{\"code\":200,\"status\":\"success\",\"data\":{\"at\":\"2010-12-17T12:00:00.000Z\"}}", responseBody.toString());
	}

	@Test
	public void shouldParseTimepointXml()
	{
		server.bind(SERVER_PORT);
		sendPostEvent("/date.xml", "<com.strategicgains.restexpress.pipeline.Dated><at>2010-12-17T120000Z</at></com.strategicgains.restexpress.pipeline.Dated>");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(responseBody.toString().startsWith("<response>"));
		assertTrue(responseBody.toString().contains("<code>200</code>"));
		assertTrue(responseBody.toString().contains("<data class=\"dated\">"));
		assertTrue(responseBody.toString().contains("<at>2010-12-17T12:00:00.000Z</at>"));
		assertTrue(responseBody.toString().contains("</data>"));
		assertTrue(responseBody.toString().endsWith("</response>"));
	}

	@Test
	public void shouldParseTimepointXmlUsingQueryString()
	{
		server.bind(SERVER_PORT);
		sendPostEvent("/date?format=xml", "<dated><at>2010-12-17T120000Z</at></dated>");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals(0, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(responseBody.toString().startsWith("<response>"));
		assertTrue(responseBody.toString().contains("<code>200</code>"));
		assertTrue(responseBody.toString().contains("<data class=\"dated\">"));
		assertTrue(responseBody.toString().contains("<at>2010-12-17T12:00:00.000Z</at>"));
		assertTrue(responseBody.toString().contains("</data>"));
		assertTrue(responseBody.toString().endsWith("</response>"));
	}

	@Test
	public void shouldThrowExceptionOnInvalidUrl()
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/xyzt.xml");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(responseBody.toString());
		assertTrue(responseBody.toString().startsWith("<response>"));
		assertTrue(responseBody.toString().contains("<code>404</code>"));
		assertTrue(responseBody.toString().contains("<status>error</status>"));
		assertTrue(responseBody.toString().contains("<message>Unresolvable URL: http://null/xyzt.xml</message>"));
		assertTrue(responseBody.toString().endsWith("</response>"));
	}

	@Test
	public void shouldThrowExceptionOnInvalidUrlWithXmlFormatQueryString()
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/xyzt?format=xml");
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(0, observer.getSuccessCount());
		assertEquals(1, observer.getExceptionCount());
//		System.out.println(httpResponse.toString());
		assertTrue(responseBody.toString().startsWith("<response>"));
		assertTrue(responseBody.toString().contains("<code>404</code>"));
		assertTrue(responseBody.toString().contains("<status>error</status>"));
		assertTrue(responseBody.toString().contains("<message>Unresolvable URL: http://null/xyzt?format=xml</message>"));
		assertTrue(responseBody.toString().endsWith("</response>"));
	}

	@Test
	public void shouldCallAllFinallyProcessors()
	{
		NoopPostprocessor p1 = new NoopPostprocessor();
		NoopPostprocessor p2 = new NoopPostprocessor();
		NoopPostprocessor p3 = new NoopPostprocessor();
		server.addFinallyProcessor(p1);
		server.addFinallyProcessor(p2);
		server.addFinallyProcessor(p3);
		server.bind(SERVER_PORT);
		sendGetEvent("/foo");
		assertEquals(1, p1.getCallCount());
		assertEquals(1, p2.getCallCount());
		assertEquals(1, p3.getCallCount());
	}

	@Test
	public void shouldCallAllFinallyProcessorsOnRouteException()
	{
		NoopPostprocessor p1 = new NoopPostprocessor();
		NoopPostprocessor p2 = new NoopPostprocessor();
		NoopPostprocessor p3 = new NoopPostprocessor();
		server.addFinallyProcessor(p1);
		server.addFinallyProcessor(p2);
		server.addFinallyProcessor(p3);
		server.bind(SERVER_PORT);
		sendGetEvent("/xyzt.html");
		assertEquals(1, p1.getCallCount());
		assertEquals(1, p2.getCallCount());
		assertEquals(1, p3.getCallCount());
	}

	@Test
	public void shouldCallAllFinallyProcessorsOnProcessorException()
	{
		NoopPostprocessor p1 = new ExceptionPostprocessor();
		NoopPostprocessor p2 = new ExceptionPostprocessor();
		NoopPostprocessor p3 = new ExceptionPostprocessor();
		server.addFinallyProcessor(p1);
		server.addFinallyProcessor(p2);
		server.addFinallyProcessor(p3);
		server.bind(SERVER_PORT);
		sendGetEvent("/foo");
		assertEquals(1, p1.getCallCount());
		assertEquals(1, p2.getCallCount());
		assertEquals(1, p3.getCallCount());
	}

	@Test
	public void shouldSetJSONContentType()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/serializedString.json?returnValue=raw string");
		assertEquals(0, observer.getExceptionCount());
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertEquals("\"raw string\"", responseBody.toString());
		assertTrue(responseHeaders.containsKey("Content-Type"));
		List<String> contentTypes = responseHeaders.get(HttpHeaders.Names.CONTENT_TYPE);
		assertEquals(1, contentTypes.size());
		assertEquals("application/json; charset=UTF-8", contentTypes.get(0));
	}

	@Test
	public void shouldSetJSONContentTypeOnNullReturn()
	throws Exception
	{
		server.bind(SERVER_PORT);
		sendGetEvent("/serializedString.json");
		assertEquals(0, observer.getExceptionCount());
		assertEquals(1, observer.getReceivedCount());
		assertEquals(1, observer.getCompleteCount());
		assertEquals(1, observer.getSuccessCount());
		assertTrue(responseHeaders.containsKey("Content-Type"));
		List<String> contentTypes = responseHeaders.get(HttpHeaders.Names.CONTENT_TYPE);
		assertEquals(1, contentTypes.size());
		assertEquals("application/json; charset=UTF-8", contentTypes.get(0));
		assertEquals("null", responseBody.toString());
	}

	private void sendGetEvent(String path)
    {
		HttpGet request = new HttpGet("http://localhost:" + SERVER_PORT + path);
		
		try
		{
			HttpResponse response = (HttpResponse) http.execute(request);
			HttpEntity entity = response.getEntity();
			responseBody.append(EntityUtils.toString(entity));
			
			for (Header header : response.getAllHeaders())
			{
				List<String> headers = responseHeaders.get(header.getName());
				
				if (headers == null)
				{
					headers = new ArrayList<String>();
					responseHeaders.put(header.getName(), headers);
				}
				
				headers.add(header.getValue());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			request.releaseConnection();
		}
    }

	private void sendPostEvent(String path, String body)
    {
		HttpPost request = new HttpPost("http://localhost:" + SERVER_PORT + path);
		
		try
		{
			request.setEntity(new StringEntity(body));
			HttpResponse response = (HttpResponse) http.execute(request);
			HttpEntity entity = response.getEntity();
			responseBody.append(EntityUtils.toString(entity));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			request.releaseConnection();
		}
    }
	
	public class DummyRoutes
	{
		private Object controller = new FooBarController();

        public void defineRoutes(RestExpress server)
        {
        	server.uri("/foo.{format}", controller)
        		.action("fooAction", HttpMethod.GET);

        	server.uri("/foo/{userPhrase}.{format}", controller)
    			.action("verifyUrlDecodedParameters", HttpMethod.GET);

        	server.uri("/bar.{format}", controller)
        		.action("barAction", HttpMethod.GET);

        	server.uri("/date.{format}", controller)
    			.action("dateAction", HttpMethod.POST);

        	server.uri("/unserializedDefault", controller)
        		.action("unserializedDefault", HttpMethod.GET);

        	server.uri("/unserialized", controller)
        		.action("unserializedAction", HttpMethod.GET);

        	server.uri("/unserializedToo", controller)
        		.action("contentHeaderAction", HttpMethod.GET);

        	server.uri("/serializedString.{format}", controller)
    		.action("serializedStringAction", HttpMethod.GET);

        	server.uri("/setBodyAction.html", controller)
        		.action("setBodyAction", HttpMethod.GET)
        		.format(Format.HTML);
        }
	}
	
	public class FooBarController
	{
		public void fooAction(Request request, Response response)
		{
			// do nothing.
		}
		
		public String verifyUrlDecodedParameters(Request request, Response response)
		{
			return request.getHeader("userPhrase");
		}
		
		public void barAction(Request request, Response response)
		{
			throw new BadRequestException("foobar'd");
		}

		public Object dateAction(Request request, Response response)
		{
			return request.getBodyAs(Dated.class);
		}

		public String unserializedDefault(Request request, Response response)
		{
			response.noSerialization();
			return "should be text plain, here";
		}

		public String unserializedAction(Request request, Response response)
		{
			response.setContentType("text/html");
			response.noSerialization();
			return "<html><body>Some kinda wonderful!</body></html>";
		}

		public String serializedStringAction(Request request, Response response)
		{
			return request.getHeader("returnValue");
		}

		public String contentHeaderAction(Request request, Response response)
		{
			response.addHeader("Content-Type", "text/html");
			response.noSerialization();
			return "<html><body>Wow! What a fabulous HTML body...</body></html>";
		}

		public void setBodyAction(Request request, Response response)
		{
			response.setContentType(ContentType.HTML);
			response.noSerialization();
			response.setBody("<html><body>Arbitrarily set HTML body...</body></html>");
		}
	}

	public class DummyObserver
	extends MessageObserver
	{
		private int receivedCount = 0;
		private int exceptionCount = 0;
		private int successCount = 0;
		private int completeCount = 0;

		@Override
        protected void onReceived(Request request, Response response)
        {
			++receivedCount;
        }

		@Override
        protected void onException(Throwable exception, Request request, Response response)
        {
			++exceptionCount;
        }

		@Override
        protected void onSuccess(Request request, Response response)
        {
			++successCount;
        }

		@Override
        protected void onComplete(Request request, Response response)
        {
			++completeCount;
        }

		public int getReceivedCount()
        {
        	return receivedCount;
        }

		public int getExceptionCount()
        {
        	return exceptionCount;
        }

		public int getSuccessCount()
        {
        	return successCount;
        }

		public int getCompleteCount()
        {
        	return completeCount;
        }
	}

	private class NoopPostprocessor
	implements Postprocessor
	{
		private int callCount = 0;

        @Override
        public void process(Request request, Response response)
        {
        	++callCount;
        }
        
        public int getCallCount()
        {
        	return callCount;
        }
	}

	private class ExceptionPostprocessor
	extends NoopPostprocessor
	{
        @Override
        public void process(Request request, Response response)
        {
        	super.process(request, response);
        	throw new RuntimeException("RuntimeException thrown...");
        }
	}
}
