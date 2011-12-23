package com.echo.controller;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

/**
 * @author toddf
 * @since Aug 31, 2010
 */
public class StatusController
{
	public Object create(Request request, Response response)
	{
		long delayms = delay(request);
		int status = Integer.valueOf(request.getRawHeader(Constants.STATUS_RESPONSE_HEADER));
		response.setResponseCode(status);
		return new DelayResponse("create", delayms);
	}

	public Object read(Request request, Response response)
	{
		long delayms = delay(request);
		int status = Integer.valueOf(request.getRawHeader(Constants.STATUS_RESPONSE_HEADER));
		response.setResponseCode(status);
		return new DelayResponse("read", delayms);
	}

	public Object update(Request request, Response response)
	{
		long delayms = delay(request);
		int status = Integer.valueOf(request.getRawHeader(Constants.STATUS_RESPONSE_HEADER));
		response.setResponseCode(status);
		return new DelayResponse("update", delayms);
	}

	public Object delete(Request request, Response response)
	{
		long delayms = delay(request);
		int status = Integer.valueOf(request.getRawHeader(Constants.STATUS_RESPONSE_HEADER));
		response.setResponseCode(status);
		return new DelayResponse("delete", delayms);
	}

	private long delay(Request request)
    {
	    long millis = Long.valueOf(request.getRawHeader(Constants.TIMEOUT_MILLIS_HEADER));
		try
        {
	        Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
	        e.printStackTrace();
        }
        return millis;
    }
}
