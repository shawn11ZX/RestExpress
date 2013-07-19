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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.contenttype.MediaRange;
import com.strategicgains.restexpress.contenttype.MediaTypeParser;
import com.strategicgains.restexpress.exception.NotAcceptableException;
import com.strategicgains.restexpress.response.ResponseProcessor;
import com.strategicgains.restexpress.response.ResponseWrapper;

/**
 * @author toddf
 * @since Jul 18, 2013
 */
public class AbstractSerializationProvider
implements SerializationProvider
{
	private Map<String, ResponseProcessor> processorsByFormat = new LinkedHashMap<String, ResponseProcessor>();
	private Map<String, ResponseProcessor> processorsByMimeType = new LinkedHashMap<String, ResponseProcessor>();
	private List<MediaRange> supportedMediaRanges = new ArrayList<MediaRange>();
	private ResponseProcessor defaultProcessor;

	/**
	 * Add a SerializationProcessor to this SerializationProvider, along with ResponseWrapper to use
	 * to alter/format responses.
	 * 
	 * @param processor
	 * @param wrapper
	 */
	public void add(SerializationProcessor processor, ResponseWrapper wrapper)
	{
		add(processor, wrapper, false);
	}

	/**
	 * Add a SerializationProcessor to this SerializationProvider, along with ResponseWrapper to use
	 * to alter/format responses.  If isDefault is true, this SerializationProcessor is used when
	 * Content-Type negotiation fails or format is not specified in the URL.
	 * 
	 * @param processor
	 * @param wrapper
	 * @param isDefault
	 */
	public void add(SerializationProcessor processor, ResponseWrapper wrapper, boolean isDefault)
	{
		// TODO: this allows duplicate media ranges.  Fix... throw an exception on duplicates.
		supportedMediaRanges.addAll(processor.getSupportedMediaRanges());
		ResponseProcessor responseProcessor = new ResponseProcessor(processor, wrapper);

		for (String format : processor.getSupportedFormats())
		{
			processorsByFormat.put(format, responseProcessor);
		}
		
		for (MediaRange mediaRange : processor.getSupportedMediaRanges())
		{
			//TODO: put only if not already present.
			processorsByMimeType.put(mediaRange.asMediaType(), responseProcessor);
		}
		
		if (isDefault)
		{
			defaultProcessor = responseProcessor;
		}
	}

	@Override
	public <T> T deserialize(Request request, Class<T> type)
	{
		ResponseProcessor processor = null;
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

		return processor.deserialize(request, type);
	}

	@Override
    public void serialize(Request request, Response response)
    {
		String bestMatch = null;
		ResponseProcessor processor = null;
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

		response.setBody(processor.serialize(response));

		if (!response.hasHeader(HttpHeaders.Names.CONTENT_TYPE))
		{
			response.addHeader(HttpHeaders.Names.CONTENT_TYPE, bestMatch);
		}
    }
}
