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
package com.strategicgains.restexpress.domain;

import java.util.UUID;

import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.exception.ExceptionUtils;
import com.strategicgains.restexpress.exception.ServiceException;

/**
 * This result wrapper should only be called to wrap the response if there is an exception.
 * 
 * @author toddf
 * @since Oct 8, 2013
 */
public class ErrorResult
{
	private String errorId;
	private int httpStatus;
	private String message;
	private String errorType;

	public ErrorResult(UUID errorId, int httpResponseCode, String errorMessage, String errorType)
	{
		super();
		this.errorId = (errorId == null ? null : errorId.toString());
		this.httpStatus = httpResponseCode;
		this.message = errorMessage;
		this.errorType = errorType;
	}

	public int getHttpStatus()
    {
    	return httpStatus;
    }
	
	public String getMessage()
	{
		return message;
	}

	public String getErrorId()
	{
		return errorId;
	}

	public String getErrorType()
	{
		return errorType;
	}
	
	
	// SECTION: FACTORY
	
	public static ErrorResult fromResponse(Response response)
	{
		if (response.hasException())
		{
			Throwable exception = response.getException();
			Throwable rootCause = ExceptionUtils.findRootCause(exception);
			String message = (rootCause != null ? rootCause.getMessage() : exception.getMessage());
			String causeName = (rootCause != null ? rootCause.getClass().getSimpleName() : exception.getClass().getSimpleName());

			if (ServiceException.isAssignableFrom(exception))
			{
				return new ErrorResult(((ServiceException) exception).getId(), response.getResponseStatus().code(), message, causeName);
			}

			return new ErrorResult(UUID.randomUUID(), response.getResponseStatus().code(), message, causeName);
		}

		return new ErrorResult(UUID.randomUUID(), response.getResponseStatus().code(), null, null);
	}
}
