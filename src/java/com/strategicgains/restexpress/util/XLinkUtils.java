/*
    Copyright 2010, Strategic Gains, Inc.

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
package com.strategicgains.restexpress.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.strategicgains.restexpress.domain.XLink;

/**
 * Non-instantiable class with foreign methods to create and manipulate XLink instances.
 * 
 * @author toddf
 * @since July 29, 2010
 */
public abstract class XLinkUtils
{
	private static final MapStringFormat formatter = new MapStringFormat();

	private XLinkUtils()
	{
		// This constructor prevents instantiation.
	}	

	/**
	 * Creates a List of XLink instances where the resulting path is the passed-in urlPath suffixed with
	 * one of the ids.
	 *  
	 * @param ids a Collection of identifiers to create XLink instances for.
	 * @param rel the XLink relationship from the referring object to the referred object (e.g. "self" or "related"). May be null.
	 * @param paramName the URL parameter (e.g. {personId}) that these identifiers represent.
	 * @param urlPath the URL that will retrieve individual ids.  There should be a parameter in it that matches the paramName, above.
	 * @param nameValuePairs is a sequence of name/value pairs, where the name matches parameters in urlPath and the value is what gets substituted.
	 * @return a List of XLink instances.
	 */
	public static List<XLink> asXLinks(Collection<String> ids, String rel, String paramName, String urlPath, String... nameValuePairs)
	{
		Map<String, String> parameters = MapStringFormat.toMap(nameValuePairs);
		List<XLink> results = new ArrayList<XLink>(ids.size());

		for (String id : ids)
		{
			parameters.put(paramName, id);
			results.add(new XLink(id, rel, formatter.format(urlPath, parameters)));
		}

		return results;
	}

	/**
	 * Create a Location header-style URL from an URL pattern, replacing parameters (of the form '{name}')
	 * with actual values.
	 * <p/>
	 * Example usage:<br/>
	 * String urlPattern = request.getNamedUrl(HttpMethod.GET, "order.crud.route");<br/>
	 * String locationUrl = XLinkUtils.asLocationUrl(locationUrl, "orderId", "1234");<br/>
	 * response.addLocationHeader(locationUrl);
	 * 
	 * @param urlPattern
	 * @param nameValuePairs string name/value pairs where name matches parameters in the URL.
	 * @return a String with parameters replaced with values.
	 */
	public static String asLocationUrl(String urlPattern, String... nameValuePairs)
	{
		Map<String, String> parameters = MapStringFormat.toMap(nameValuePairs);
		return formatter.format(urlPattern, parameters);
	}
}
