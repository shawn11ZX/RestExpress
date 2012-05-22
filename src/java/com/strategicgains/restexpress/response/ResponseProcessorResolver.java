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
import java.util.HashMap;
import java.util.Map;

/**
 * @author toddf
 * @since May 14, 2012
 */
public class ResponseProcessorResolver
{
	private Map<String, ResponseProcessor> processors = new HashMap<String, ResponseProcessor>();
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
	}
	
	public ResponseProcessor put(String format, ResponseProcessor processor)
	{
		return processors.put(format, processor);
	}
	
	public void setDefaultFormat(String format)
	{
		this.defaultFormat = format;
	}

	public ResponseProcessor resolve(String requestFormat)
	{
		if (requestFormat == null || requestFormat.trim().isEmpty())
		{
			return getDefault();
		}

		return resolveViaSpecifiedFormat(requestFormat);
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
}
