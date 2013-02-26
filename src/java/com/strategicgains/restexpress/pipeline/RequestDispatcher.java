/*
 * Copyright 2009, Strategic Gains, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.strategicgains.restexpress.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.exception.ExceptionMapping;
import com.strategicgains.restexpress.exception.ServiceException;
import com.strategicgains.restexpress.request.RequestProcessor;
import com.strategicgains.restexpress.response.ResponseProcessor;
import com.strategicgains.restexpress.response.ResponseProcessorResolver;
import com.strategicgains.restexpress.route.Action;
import com.strategicgains.restexpress.route.RouteResolver;
import com.strategicgains.restexpress.serialization.SerializationProcessor;

/**
 * @author toddf
 * @since Nov 13, 2009
 */
@Sharable
public class RequestDispatcher
extends SimpleChannelUpstreamHandler
{
	// SECTION: INSTANCE VARIABLES

	private RouteResolver routeResolver;
	private ResponseProcessorResolver responseProcessorResolver;

	private List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
	private List<Postprocessor> postprocessors = new ArrayList<Postprocessor>();
	private List<Postprocessor> finallyProcessors = new ArrayList<Postprocessor>();
	private ExceptionMapping exceptionMap = new ExceptionMapping();
	private MessageObservers messageObservers = new MessageObservers();
	private ExecutorService executor = null;

	// SECTION: CONSTRUCTORS

	public RequestDispatcher()
	{
		super();
	}

	public RequestDispatcher(RouteResolver routeResolver, ResponseProcessorResolver responseProcessorResolver)
    {
		super();
		setRouteResolver(routeResolver);
		setResponseProcessorResolver(responseProcessorResolver);
    }


	// SECTION: MUTATORS

	public void setRouteResolver(RouteResolver resolver)
	{
		this.routeResolver = resolver;
	}

	public void setResponseProcessorResolver(ResponseProcessorResolver resolver)
	{
		this.responseProcessorResolver = resolver;
	}
	
	public void setExecutorPoolSize(int size)
	{
		if (size == 0)
		{
			executor = null;
		}
		else
		{
			executor = Executors.newFixedThreadPool(size);
		}
	}

	public void addMessageObserver(MessageObserver... observers)
	{
		messageObservers.add(observers);
	}

	public <T extends Throwable, U extends ServiceException> RequestDispatcher mapException(Class<T> from, Class<U> to)
	{
		exceptionMap.map(from, to);
		return this;
	}
	
	public RequestDispatcher setExceptionMap(ExceptionMapping map)
	{
		this.exceptionMap = map;
		return this;
	}


	// SECTION: SIMPLE-CHANNEL-UPSTREAM-HANDLER

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
	throws Exception
	{
		//TODO: create an executor/callback chain containing:
		// setup/resolution, preprocessors, dispatcher, postprocessors, finallyprocessors, response callback (writer)

		// No exceptions thrown here...
		Request request = new Request((HttpRequest) event.getMessage());
		Response response = new Response();
		messageObservers.notifyReceived(request, response);
		
		// can throw an exception...
		Action action = routeResolver.resolve(request);
		request.addAllHeaders(action.getParameters());
		request.setRouteResolver(routeResolver);
		request.setResolvedRoute(action.getRoute());
		response.setIsSerialized(action.shouldSerializeResponse());

		// Content-Type negotiation.
		// GET doesn't have a message body, so no need to negotiate request content type.
		if (!HttpMethod.GET.equals(request.getEffectiveHttpMethod()))
		{
			// resolve request content type.  Can throw an exception...
			request.setSerializationProcessor(resolveRequestContentType(request));
		}

		// If the response is not serialized, no need to resolve the response content type.
		if (response.isSerialized())
		{
			// resolve response accept header.  Can throw an exception...
			response.setResponseProcessor(resolveResponseContentType(request));
		}

		RequestProcessor task = new RequestProcessor(new ResponseCallback());

		if (executor == null)
		{
			task.run();
		}
		else
		{
			executor.execute(task);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event)
	throws Exception
	{
		try
		{
			MessageContext messageContext = (MessageContext) ctx.getAttachment();
			
			if (messageContext != null)
			{
				messageContext.setException(event.getCause());
				messageObservers.notifyException(messageContext);
			}
		}
		catch(Throwable t)
		{
			System.err.print("DefaultRequestHandler.exceptionCaught() threw an exception.");
			t.printStackTrace();
		}
		finally
		{
			event.getChannel().close();
		}
	}
	
	public void addPreprocessor(Preprocessor handler)
	{
		if (!preprocessors.contains(handler))
		{
			preprocessors.add(handler);
		}
	}

	public void addPostprocessor(Postprocessor handler)
	{
		if (!postprocessors.contains(handler))
		{
			postprocessors.add(handler);
		}
	}

	public void addFinallyProcessor(Postprocessor handler)
	{
		if (!finallyProcessors.contains(handler))
		{
			finallyProcessors.add(handler);
		}
	}

	private SerializationProcessor resolveRequestContentType(Request request)
    {
	    ResponseProcessor rp = responseProcessorResolver.resolveContentType(request);
	    return rp.getSerializer();
    }


	private ResponseProcessor resolveResponseContentType(Request request)
	{
		return responseProcessorResolver.resolveAccept(request);
	}
}
