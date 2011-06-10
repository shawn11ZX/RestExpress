package com.streaming.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.exception.NotFoundException;
import com.strategicgains.restexpress.url.UrlRegex;

/**
 * @author toddf
 * @since Aug 31, 2010
 */
public class StreamingController
{
	private static final String FILENAME_HEADER = UrlRegex.PARAMETER_PREFIX + "0";
	private static final String DATA_PATH = "data/video/";

	public File read(Request request, Response response)
	{
		String fileName = request.getUrlDecodedHeader(FILENAME_HEADER, "No filename provided.");
		response.setContentType("video/mpeg");
		return new File(DATA_PATH + fileName);
	}
	
	public List<String> directory(Request request, Response response)
	{
		File dir = new File(DATA_PATH);
		if (!dir.exists()) throw new NotFoundException("Streaming directory does not exist.");
		return new ArrayList<String>(Arrays.asList(dir.list()));
	}
	
	public void readFavIcon(Request request, Response response)
	{
		throw new NotFoundException("No favicon.ico present.");
	}
}
