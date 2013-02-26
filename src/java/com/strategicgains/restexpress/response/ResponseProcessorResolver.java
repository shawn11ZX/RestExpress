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
package com.strategicgains.restexpress.response;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.exception.NotAcceptableException;
import com.strategicgains.restexpress.mediatype.MediaRange;
import com.strategicgains.restexpress.mediatype.MediaTypeParser;

/**
 * @author toddf
 * @since May 14, 2012
 */
public class ResponseProcessorResolver
{
	private Map<String, ResponseProcessor> processorsByFormatExtension = new LinkedHashMap<String, ResponseProcessor>();
	private Map<String, ResponseProcessor> processorsByMediaType = new LinkedHashMap<String, ResponseProcessor>();
	private String defaultFormat;
	private List<MediaRange> supported;
	
	public ResponseProcessorResolver()
	{
		super();
	}
	
	public ResponseProcessorResolver(Map<String, ResponseProcessor> processors, String defaultFormat)
	{
		super();
		this.processorsByFormatExtension.putAll(processors);
		this.defaultFormat = defaultFormat;
	}
	
	public void put(String format, ResponseProcessor processor)
	{
		processorsByFormatExtension.put(format, processor);
	}
	
	public void setDefaultFormat(String format)
	{
		this.defaultFormat = format;
	}

    public ResponseProcessor getDefault()
    {
		return resolveViaFormat(defaultFormat);
    }

	/**
     * @return
     */
    public Collection<String> getSupportedFormats()
    {
    	return processorsByFormatExtension.keySet();
    }

	public ResponseProcessor resolveContentType(Request request)
    {
		ResponseProcessor rp = resolveViaFormat(request.getFormat());
		
		if (rp == null)
		{
			return resolveViaHeader(request.getRawHeader(HttpHeaders.Names.CONTENT_TYPE));
		}

		return rp;
    }

	public ResponseProcessor resolveAccept(Request request)
    {
		ResponseProcessor rp = resolveViaFormat(request.getFormat());
		
		if (rp == null)
		{
			return resolveViaHeader(request.getRawHeader(HttpHeaders.Names.ACCEPT));
		}

		return rp;
    }

	private ResponseProcessor resolveViaFormat(String format)
	{
		if (format == null || format.trim().isEmpty())
		{
			return null;
		}
		
		return processorsByFormatExtension.get(format);
	}

	private ResponseProcessor resolveViaHeader(String rawHeader)
    {
		List<MediaRange> requested = MediaTypeParser.parse(rawHeader);
		String mediaType = MediaTypeParser.getBestMatch(supported, requested);
		
		if (mediaType == null)
		{
			throw new NotAcceptableException(rawHeader);
		}

		return processorsByMediaType.get(mediaType);
    }
}
