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

import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.serialization.SerializationProcessor;
import com.strategicgains.restexpress.serialization.json.DefaultJsonProcessor;
import com.strategicgains.restexpress.serialization.text.DefaultTxtProcessor;
import com.strategicgains.restexpress.serialization.xml.DefaultXmlProcessor;

/**
 * @author toddf
 * @since May 14, 2012
 */
public class ResponseProcessor
{
	private SerializationProcessor serializer;
	private ResponseWrapper wrapper;

	public ResponseProcessor(SerializationProcessor serializer, ResponseWrapper wrapper)
	{
		super();
		this.serializer = serializer;
		this.wrapper = wrapper;
	}
	
	public SerializationProcessor getSerializer()
	{
		return serializer;
	}
	
	public ResponseWrapper getWrapper()
	{
		return wrapper;
	}

	public String process(Response response)
	{
		Object wrapped = wrapper.wrap(response);
		response.setContentType(serializer.getResultingContentType());
		
		if (wrapped != null)
		{
			return serializer.serialize(wrapped);
		}
		
		return null;
	}
	

	// SECTION: FACTORY

	public static ResponseProcessor defaultJsonProcessor()
	{
		return newJsonProcessor(new RawResponseWrapper());
	}

	public static ResponseProcessor newJsonProcessor(ResponseWrapper wrapper)
	{
		return new ResponseProcessor(new DefaultJsonProcessor(), wrapper);
	}

	public static ResponseProcessor defaultXmlProcessor()
	{
		return newXmlProcessor(new RawResponseWrapper());
	}

	public static ResponseProcessor newXmlProcessor(ResponseWrapper wrapper)
	{
		return new ResponseProcessor(new DefaultXmlProcessor(), wrapper);
	}

	public static ResponseProcessor defaultTxtProcessor()
	{
		return newTxtProcessor(new RawResponseWrapper());
	}
	
	public static ResponseProcessor newTxtProcessor(ResponseWrapper wrapper)
	{
		return new ResponseProcessor(new DefaultTxtProcessor(), wrapper);		
	}
}
