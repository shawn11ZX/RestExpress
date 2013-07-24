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
package com.strategicgains.restexpress.serialization.xml;

import java.util.Arrays;
import java.util.List;

import com.strategicgains.restexpress.ContentType;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.common.util.StringUtils;
import com.strategicgains.restexpress.contenttype.MediaRange;
import com.strategicgains.restexpress.contenttype.MediaTypeParser;
import com.strategicgains.restexpress.serialization.AbstractSerializationProcessor;
import com.strategicgains.restexpress.serialization.AliasingSerializationProcessor;

/**
 * @author toddf
 * @since Jul 18, 2013
 */
public abstract class XmlSerializationProcessor
extends AbstractSerializationProcessor
implements AliasingSerializationProcessor
{
	private static final String SUPPORTED_MEDIA_TYPES = StringUtils.join(",",
		ContentType.XML,
		"text/xml; charset=" + ContentType.ENCODING);
	private static List<MediaRange> SUPPORTED_MEDIA_RANGES = MediaTypeParser.parse(SUPPORTED_MEDIA_TYPES);

	public XmlSerializationProcessor()
	{
		this(Arrays.asList(Format.XML));
	}
	
	public XmlSerializationProcessor(String format)
	{
		this(Arrays.asList(format));
	}

	public XmlSerializationProcessor(List<String> supportedFormats)
	{
		super(supportedFormats);
	}

	@Override
    public List<MediaRange> getSupportedMediaRanges()
    {
		return SUPPORTED_MEDIA_RANGES;
    }
}
