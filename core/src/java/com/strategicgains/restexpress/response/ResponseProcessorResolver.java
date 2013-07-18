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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.contenttype.MediaRange;
import com.strategicgains.restexpress.contenttype.MediaTypeParser;

/**
 * @author toddf
 * @since May 14, 2012
 */
public class ResponseProcessorResolver
{
	private Map<String, ResponseProcessor> processors = new HashMap<String, ResponseProcessor>();
	private Map<String, ResponseProcessor> processorsByMediaType = new HashMap<String, ResponseProcessor>();
	private List<MediaRange> supportedRanges = new ArrayList<MediaRange>();
	private String defaultFormat;
	
	public ResponseProcessorResolver()
	{
		super();
	}
	
	public ResponseProcessorResolver(Map<String, ResponseProcessor> processors, String defaultFormat)
	{
		super();
		this.processors.putAll(processors);
		this.defaultFormat = defaultFormat;
		buildProcessorsByMediaType();
	}

	public ResponseProcessor put(String format, ResponseProcessor processor)
	{
		return processors.put(format, processor);
	}
	
	public void setDefaultFormat(String format)
	{
		this.defaultFormat = format;
	}

	public ResponseProcessor resolve(Request request)
	{
		String requestFormat = request.getFormat();
		
		if (requestFormat == null)
		{
			requestFormat = parseRequestedFormatFromUrl(request);
		}

		if (requestFormat == null || requestFormat.trim().isEmpty())
		{
			ResponseProcessor rp = resolveViaAcceptHeader(request.getHeader(HttpHeaders.Names.ACCEPT));
			
			if (rp != null) return rp;

			return getDefault();
		}

		return resolveViaSpecifiedFormat(requestFormat);
	}

    private ResponseProcessor resolveViaAcceptHeader(String header)
    {
	    if (header == null) return null;
	    
	    List<MediaRange> requested = MediaTypeParser.parse(header);
	    return processorsByMediaType.get(MediaTypeParser.getBestMatch(supportedRanges, requested));
    }

	public ResponseProcessor getDefault()
    {
		return resolveViaSpecifiedFormat(defaultFormat);
    }
	
	private ResponseProcessor resolveViaSpecifiedFormat(String format)
	{
		if (format == null || format.trim().isEmpty())
		{
			return null;
		}
		
		return processors.get(format);
	}

	/**
     * @return
     */
    public Collection<String> getSupportedFormats()
    {
    	return processors.keySet();
    }
	
	private void buildProcessorsByMediaType()
    {
		processorsByMediaType.clear();
		supportedRanges.clear();

		for (ResponseProcessor processor : processors.values())
		{
			for (MediaRange mediaRange : processor.getSerializer().getSupportedMediaRanges())
			{
				supportedRanges.add(mediaRange);
				processorsByMediaType.put(mediaRange.asMediaType(), processor);
			}
		}
    }

	private String parseRequestedFormatFromUrl(Request request)
    {
    	String uri = request.getUrl();
		int queryDelimiterIndex = uri.indexOf('?');
		String path = (queryDelimiterIndex > 0 ? uri.substring(0, queryDelimiterIndex) : uri);
    	int formatDelimiterIndex = path.lastIndexOf('.');
    	return (formatDelimiterIndex > 0 ? path.substring(formatDelimiterIndex + 1) : null);
    }
}
