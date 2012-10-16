/*
 * Copyright 2011, Strategic Gains, Inc.
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
package com.strategicgains.restexpress.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.exception.BadRequestException;

/**
 * Supports the concept of 'pagination' via request 'Range' header or 'limit' and 'offset' parameters.
 * <p/>
 * Paging is accomplished using the Range and Content-Range HTTP headers or 'limit' and 'offset' query-string
 * parameters.
 * <p/>
 * The client can request a range of results by including the "Range" header with the request.
 * For example, to get the first 25 results:
 * <p/>
 * GET /many_things.json<br/>
 * HTTP/1.1<br/>
 * Host: example.com<br/>
 * Range: items=0-24<br/>
 * <p/>
 * To request the same using the 'limit' and 'offset' parameters, limit would be set to 25 with offset being
 * set to 0 (or empty).  For example, via the query-string: &limit=25 which is equivalent to &limit=25&offset=0.
 * <p/> 
 * When both 'Range' and 'limit' + 'offset are provided, the 'limit' and 'offset' parameters override the 'Range'
 * header.  In other words, the query-string parameters override the headers.
 * <p/>
 * The server will respond with a "Content-Range" header that includes the start and end of the range, as well
 * as a total count of all results.  For example, the response for the first 25 of 67 total results:
 * <p/>
 * HTTP/1.1 200 OK<br/>
 * Content-Type: application/json<br/>
 * Content-Range: items 0-24/67<br/>
 * 
 * @author toddf
 * @since Apr 11, 2011
 */
public class QueryRange
{
	// SECTION: CONSTANTS

	private static final String LIMIT_HEADER_NAME = "limit";
	private static final String OFFSET_HEADER_NAME = "offset";
	private static final String RANGE_HEADER_NAME = "Range";
	private static final String ITEMS_HEADER_REGEX = "items=(\\d+)-(\\d+)";
	private static final Pattern ITEMS_HEADER_PATTERN = Pattern.compile(ITEMS_HEADER_REGEX);


	// SECTION: INSTANCE VARIABLES

	private Long start = null;
	private Long end = null;

	
	// SECTION: CONSTRUCTORS

	public QueryRange()
	{
		super();
	}

	public QueryRange(long start, long end)
	{
		super();
		setStart(start);
		setEnd(end);
	}

	public QueryRange(long start, int limit)
	{
		super();

		if (limit == 0)
		{
			setStart(0);
			setEnd(0);
		}
		else
		{
			setStart(start);
			setLimit(limit);
		}
	}

	
	// SECTION: ACCESSORS / MUTATORS

	public int getLimit()
	{
		return (int) (getEnd() - getStart() + 1);
	}

	public boolean hasStart()
	{
		return (start != null);
	}

	public long getStart()
	{
		return (start == null ? 0 : start.intValue());
	}

	public void setStart(long value)
	{
		this.start = Long.valueOf(value);
	}

	public long getEnd()
	{
		return (end == null ? 0 : end.longValue());
	}

	public boolean hasEnd()
	{
		return (end != null);
	}

	public void setEnd(long value)
	{
		this.end = Long.valueOf(value);
	}
	
	public void setLimit(int value)
	{
		setEnd(getStart() + value - 1);
	}
	
	/**
	 * Return true if the range has start and end values.
	 * 
	 * @return true if the range is initialized (has start and end values)
	 */
	public boolean isInitialized()
	{
		return hasStart() && hasEnd();
	}

	/**
	 * Validates the range.
	 * 
	 * @return true if the range is valid
	 */
	public boolean isValid()
	{
		return (getStart() <= getEnd());
	}
	
	
	// SECTION: FACTORY


	/**
	 * Create a QueryRange instance from the current RestExpress request, providing a
	 * default maximum offset if the request contains no range criteria.
	 * 
	 * @param request the current request
	 * @param limit the default limit, used if the request contains no range criteria
	 * @return a QueryRange instance, defaulting to 0 to (limit - 1). Never null.
	 */
	public static QueryRange parseFrom(Request request, int limit)
	{
		QueryRange range = new QueryRange(0, limit);
		parseInto(request, range);
		return range;
	}

	/**
	 * Create a QueryRange instance from the current RestExpress request.
	 * 
	 * @param request the current request
	 * @return a QueryRange instance. Never null.
	 */
	public static QueryRange parseFrom(Request request)
	{
		QueryRange range = new QueryRange();
		parseInto(request, range);
		return range;
	}
	
	private static void parseInto(Request request, QueryRange range)
	{
		String limit = request.getUrlDecodedHeader(LIMIT_HEADER_NAME);
		String offset = request.getUrlDecodedHeader(OFFSET_HEADER_NAME);

		if (!parseLimitAndOffset(limit, offset, range))
		{
			parseRangeHeader(request, range);
		}
	}
	
	/**
     * @param limit
     * @param offset
     * @param range
     * @return
     */
    private static boolean parseLimitAndOffset(String limit, String offset, QueryRange range)
    {
    	boolean hasLimit = false;
    	boolean hasOffset = false;

    	if (limit != null && !limit.trim().isEmpty())
    	{
    		hasLimit = true;
    		range.setStart(Long.parseLong(offset));
    	}
    	
    	if (offset != null && !offset.trim().isEmpty())
    	{
    		hasOffset = true;
    		range.setLimit(Integer.parseInt(limit));
    	}
    	
    	if (hasLimit || hasOffset)
    	{
    		if (!range.isValid())
    		{
    			throw new BadRequestException("Invalid 'limit' and 'offset' parameters: limit=" + limit + " offset=" + offset);
    		}
    		
    		return true;
    	}
    	
    	return false;
    }

	private static void parseRangeHeader(Request request, QueryRange range)
    {
	    String rangeHeader = request.getUrlDecodedHeader(RANGE_HEADER_NAME);

		if (rangeHeader != null && !rangeHeader.trim().isEmpty())
		{
			Matcher matcher = ITEMS_HEADER_PATTERN.matcher(rangeHeader);

			if(!matcher.matches())
			{
				throw new BadRequestException("Unparseable 'Range' header.  Expecting items=[start]-[end] was: " + rangeHeader);
			}

			range.setStart(Long.parseLong(matcher.group(1)));
			range.setEnd(Long.parseLong(matcher.group(2)));
			
			if (!range.isValid())
			{
				throw new BadRequestException("Invalid 'Range' header.  Expecting items=[start]-[end]  was: " + rangeHeader);
			}
		}
    }

	@Override
	public String toString()
	{
		return assembleString().toString();
	}
	
	/**
	 * Creates a string in the form "items 0-24/66" using the values from this QueryRange
	 * along with the maximum number of items available.  This value is suitable for setting
	 * the Content-Range header on the response from Range requests.
	 * <p/>
	 * No range checking is performed.  It is therefore, the caller's responsibility to ensure
	 * that maxItems is greater-than the end value.
	 * 
	 * @param maxItems the maximum number of items available.
	 * @return a String of the form "items <first>-<last>/<max>"
	 */
	public String asContentRange(int maxItems)
	{
		return assembleString(maxItems)
			.append("/")
			.append(maxItems)
			.toString();
	}

	private StringBuffer assembleString()
	{
		return assembleString(null);
	}

	private StringBuffer assembleString(Integer max)
	{
		return new StringBuffer("items ")
			.append(getStart())
			.append("-")
			.append((max == null ? getEnd() : (getEnd() > max ? (max > 0 ? max - 1 : max) : getEnd())));
	}
}
