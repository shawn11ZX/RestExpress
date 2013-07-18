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
import java.util.Collections;
import java.util.List;

/**
 * @author toddf
 * @since Jul 18, 2013
 */
public abstract class AbstractSerializationProcessor
implements SerializationProcessor
{
	private List<String> supportedFormats = new ArrayList<String>();

	public AbstractSerializationProcessor()
	{
		super();
	}

	public AbstractSerializationProcessor(List<String> supportedFormats)
	{
		super();
		this.supportedFormats = new ArrayList<String>(supportedFormats);
	}
	
	public AbstractSerializationProcessor addSupportedFormat(String format)
	{
		if (!supportedFormats.contains(format))
		{
			supportedFormats.add(format);
		}

		return this;
	}

	@Override
    public List<String> getSupportedFormats()
    {
		return Collections.unmodifiableList(supportedFormats);
    }
}
