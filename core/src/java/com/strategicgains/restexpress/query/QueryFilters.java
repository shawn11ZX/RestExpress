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

import java.util.ArrayList;
import java.util.List;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.common.query.FilterComponent;
import com.strategicgains.restexpress.common.query.FilterOperator;
import com.strategicgains.restexpress.common.query.QueryFilter;

/**
 * A factory for RestExpress-Common QueryFilter instance, parsing from a Request.
 * 
 * @author toddf
 * @since Apr 12, 2011
 * @see com.strategicgains.restexpress.common.query.QueryFilter
 */
public abstract class QueryFilters
{
	private static final String FILTER_HEADER_NAME = "filter";
	private static final String FILTER_SEPARATOR = "\\|";
	private static final String NAME_VALUE_SEPARATOR = "::";
	
	
	// SECTION: FACTORY

	/**
	 * Create an instance of QueryFilter from the RestExpress request.
	 * 
	 * @param request the current request
	 */
	public static QueryFilter parseFrom(Request request)
	{
		String filterString = request.getHeader(FILTER_HEADER_NAME);
		
		if (filterString == null || filterString.trim().isEmpty())
		{
			return new QueryFilter();
		}
		
		String[] nameValues = filterString.split(FILTER_SEPARATOR);

		if (nameValues == null || nameValues.length == 0)
		{
			return new QueryFilter();
		}

		String[] nameValuePair;
		List<FilterComponent> filters = new ArrayList<FilterComponent>();
		
		for (String nameValue : nameValues)
		{
			nameValuePair = nameValue.split(NAME_VALUE_SEPARATOR);
			
			if (nameValuePair.length == 1)
			{
				filters.add(new FilterComponent(nameValuePair[0], FilterOperator.CONTAINS, ""));
			}
			else
			{
				filters.add( new FilterComponent(nameValuePair[0], FilterOperator.CONTAINS, nameValuePair[1]));
			}
		}

		return new QueryFilter(filters);
	}
}
