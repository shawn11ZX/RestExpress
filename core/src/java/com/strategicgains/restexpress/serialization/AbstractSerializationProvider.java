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
package com.strategicgains.restexpress.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.contenttype.MediaRange;
import com.strategicgains.restexpress.contenttype.MediaTypeParser;
import com.strategicgains.restexpress.exception.NotAcceptableException;

/**
 * @author toddf
 * @since Jul 18, 2013
 */
public class AbstractSerializationProvider
implements SerializationProvider
{
	private Map<String, SerializationProcessor> processorsByFormat = new HashMap<String, SerializationProcessor>();
	private Map<String, SerializationProcessor> processorsByMimeType = new HashMap<String, SerializationProcessor>();
	private List<MediaRange> supportedMediaRanges = new ArrayList<MediaRange>();
	private SerializationProcessor defaultProcessor;

	public void addSerializationProcessor(SerializationProcessor processor)
	{
		addSerializationProcessor(processor, false);
	}

	public void addSerializationProcessor(SerializationProcessor processor, boolean isDefault)
	{
		supportedMediaRanges.addAll(processor.getSupportedMediaRanges());

		for (String format : processor.getSupportedFormats())
		{
			processorsByFormat.put(format, processor);
		}
		
		for (MediaRange mediaRange : processor.getSupportedMediaRanges())
		{
			processorsByMimeType.put(mediaRange.asMediaType(), processor);
		}
		
		if (isDefault)
		{
			defaultProcessor = processor;
		}
	}

	@Override
	public <T> T deserialize(Request request, Class<T> type)
	{
		SerializationProcessor processor = null;
	    String format = request.getFormat();

		if (format != null)
		{
			processor = processorsByFormat.get(format);

			if (processor == null)
			{
				throw new NotAcceptableException(format);
			}
		}

		if (processor == null)
		{
			List<MediaRange> requestedMediaRanges = MediaTypeParser.parse(request.getHeader(HttpHeaders.Names.CONTENT_TYPE));
			String bestMatch = MediaTypeParser.getBestMatch(supportedMediaRanges, requestedMediaRanges);
	
			if (bestMatch != null)
			{
				processor = processorsByMimeType.get(bestMatch);
			}
		}
		
		if (processor == null)
		{
			processor = defaultProcessor;
		}

		return processor.deserialize(request.getBody(), type);
	}

	@Override
    public void serialize(Request request, Response response)
    {
		String bestMatch = null;
		SerializationProcessor processor = null;
		String format = request.getFormat();

		if (format != null)
		{
			processor = processorsByFormat.get(format);

			if (processor == null)
			{
				throw new NotAcceptableException(format);
			}
			
			bestMatch = processor.getSupportedMediaRanges().get(0).asMediaType();
		}

		if (processor == null)
		{
			List<MediaRange> requestedMediaRanges = MediaTypeParser.parse(request.getHeader(HttpHeaders.Names.ACCEPT));
			bestMatch = MediaTypeParser.getBestMatch(supportedMediaRanges, requestedMediaRanges);
	
			if (bestMatch != null)
			{
				processor = processorsByMimeType.get(bestMatch);
			}
		}
		
		if (processor == null)
		{
			processor = defaultProcessor;
			bestMatch = processor.getSupportedMediaRanges().get(0).asMediaType();
		}

		response.setBody(processor.serialize(response.getBody()));
		
		if (!response.hasHeader(HttpHeaders.Names.CONTENT_TYPE))
		{
			response.addHeader(HttpHeaders.Names.CONTENT_TYPE, bestMatch);
		}
    }
}
