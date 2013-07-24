/*
    Copyright 2013, Strategic Gains, Inc.

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

import java.util.ArrayList;
import java.util.List;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

/**
 * @author fredta2
 * @since Feb 20, 2013
 */
public class MessageObservers
{
	private List<MessageObserver> messageObservers = new ArrayList<MessageObserver>();

	public void add(MessageObserver... observers)
	{
		for (MessageObserver observer : observers)
		{
			if (!messageObservers.contains(observer))
			{
				messageObservers.add(observer);
			}
		}
	}

    /**
     * @param request
     * @param response
     */
    public void notifyReceived(Request request, Response response)
    {
    	for (MessageObserver observer : messageObservers)
    	{
    		observer.onReceived(request, response);
    	}
    }

	/**
     * @param request
     * @param response
     */
    public void notifyComplete(MessageContext context)
    {
    	for (MessageObserver observer : messageObservers)
    	{
    		observer.onComplete(context.getRequest(), context.getResponse());
    	}
    }

	/**
     * @param exception
     * @param request
     * @param response
     */
    public void notifyException(MessageContext context)
    {
    	Throwable exception = context.getException();

    	for (MessageObserver observer : messageObservers)
    	{
    		observer.onException(exception, context.getRequest(), context.getResponse());
    	}
    }

	/**
     * @param request
     * @param response
     */
    public void notifySuccess(MessageContext context)
    {
    	for (MessageObserver observer : messageObservers)
    	{
    		observer.onSuccess(context.getRequest(), context.getResponse());
    	}
    }
}
