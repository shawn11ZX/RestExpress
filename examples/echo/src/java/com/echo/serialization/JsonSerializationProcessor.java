package com.echo.serialization;

import java.util.Date;

import com.google.gson.GsonBuilder;
import com.strategicgains.restexpress.serialization.json.DefaultJsonProcessor;
import com.strategicgains.restexpress.serialization.json.GsonTimestampSerializer;
import com.strategicgains.util.date.DateAdapterConstants;

/**
 * @author toddf
 * @since Oct 10, 2011
 */
public class JsonSerializationProcessor
extends DefaultJsonProcessor
{

	public JsonSerializationProcessor()
    {
	    super(
	    	new GsonBuilder()
			.disableHtmlEscaping()
			.registerTypeAdapter(Date.class, new GsonTimestampSerializer())
//			.registerTypeAdapter(ObjectId.class, new GsonObjectIdSerializer())
			.setDateFormat(DateAdapterConstants.TIMESTAMP_OUTPUT_FORMAT)
			.create()
	    );
    }
}
