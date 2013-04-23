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

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.common.query.QueryOrder;

/**
 * A factory for RestExpress-Common QueryOrder instances, parsing them from a Request.
 * 
 * @author toddf
 * @since Apr 12, 2011
 * @see com.strategicgains.restexpress.common.query.QueryOrder
 */
public abstract class QueryOrders
{
	private static final String SORT_HEADER_NAME = "sort";
	private static final String SORT_SEPARATOR = "\\|";

	/**
	 * Create a QueryOrder instance from the RestExpress request.
	 * 
	 * @param request the current request
	 * @return a QueryOrder instance
	 */
	public static QueryOrder parseFrom(Request request)
	{
		String sortString = request.getHeader(SORT_HEADER_NAME);

		if (sortString == null || sortString.trim().isEmpty())
		{
			return new QueryOrder();
		}
		
		String[] strings = sortString.split(SORT_SEPARATOR);
		return new QueryOrder(strings);
	}
}
