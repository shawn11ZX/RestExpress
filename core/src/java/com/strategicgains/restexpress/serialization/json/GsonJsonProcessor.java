/*
 * Copyright 2010-2012, Strategic Gains, Inc.
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

package com.strategicgains.restexpress.serialization.json;

import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.strategicgains.restexpress.ContentType;
import com.strategicgains.restexpress.contenttype.MediaRange;
import com.strategicgains.restexpress.contenttype.MediaTypeParser;
import com.strategicgains.restexpress.serialization.SerializationProcessor;
import com.strategicgains.restexpress.util.StringUtils;
import com.strategicgains.util.date.DateAdapterConstants;

/**
 * A SerializationProcessor to handle JSON input/output using GSON. It anticipates ISO
 * 8601-compatible time points for date instances and outputs dates as ISO 8601
 * time points.
 * 
 * @author toddf
 * @since Mar 16, 2010
 */
public class GsonJsonProcessor
implements SerializationProcessor
{
	private static final String SUPPORTED_MEDIA_TYPES = StringUtils.join(",",
		ContentType.JSON,
		"application/javasctript; charset=" + ContentType.ENCODING,
		"text/javascript; charset=" + ContentType.ENCODING);
	private static List<MediaRange> SUPPORTED_MEDIA_RANGES = MediaTypeParser.parse(SUPPORTED_MEDIA_TYPES);

	private Gson gson;

	public GsonJsonProcessor()
	{
		super();
		gson = new GsonBuilder()
		    .disableHtmlEscaping()
		    .registerTypeAdapter(Date.class, new GsonTimepointSerializer())
		    .setDateFormat(DateAdapterConstants.TIMESTAMP_OUTPUT_FORMAT)
		    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		    .create();
	}

	public GsonJsonProcessor(Gson gson)
	{
		super();
		this.gson = gson;
	}


	// SECTION: SERIALIZATION PROCESSOR

	@Override
	public <T> T deserialize(String string, Class<T> type)
	{
		return gson.fromJson((String) string, type);
	}

	@Override
	public <T> T deserialize(ChannelBuffer buffer, Class<T> type)
	{
    	return gson.fromJson(new InputStreamReader(new ChannelBufferInputStream(buffer), ContentType.CHARSET), type);
	}

	@Override
	public String serialize(Object object)
	{
		if (object == null) return StringUtils.EMPTY_STRING;

		return gson.toJson(object);
	}

	@Override
    public List<MediaRange> getSupportedMediaRanges()
    {
	    return SUPPORTED_MEDIA_RANGES;
    }
}
