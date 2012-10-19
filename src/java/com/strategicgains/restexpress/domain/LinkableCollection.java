/*
    Copyright 2012, Strategic Gains, Inc.

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A wrapper class that enables attaching links (XLink instances) to a Collection before
 * serialization to the client.
 * 
 * @author toddf
 * @since Oct 19, 2012
 */
public class LinkableCollection<E>
implements Linkable
{
	/**
	 * The collection that is being wrapped.
	 */
	private Collection<E> items;
	
	/**
	 * The XLink instance associated with the collection.
	 */
	private List<XLink> links;

	public LinkableCollection(Collection<E> items)
	{
		super();
		setItems(items);
	}

	/**
	 * Returns an unmodifiable List of the links.
	 */
	public List<XLink> getLinks()
	{
		return Collections.unmodifiableList(links);
	}

	/**
	 * Sets the links of links as a whole on the collection.
	 * 
	 * @param links a list of XLink instances to assign to the colleciton.
	 */
	public void setLinks(List<XLink> links)
	{
		this.links = new ArrayList<XLink>(links);
	}

	/**
	 * Attach a link to the collection.
	 * 
	 * @param link an XLink instance.
	 */
	public void addLink(XLink link)
	{
		if (links == null)
		{
			links = new ArrayList<XLink>();
		}

		links.add(new XLink(link));
	}

	/**
	 * Get the items in the underlying collection.  The returned collection is 
	 * unmodifiable.
	 * 
	 * @return the items in the collection.
	 */
	public Collection<E> getItems()
	{
		return Collections.unmodifiableCollection(items);
	}

	/**
	 * Assigns the items collection to the underlying collection.  Note, there
	 * is no copying occurring in this operation, so it is possible for a caller
	 * to change the underlying collection by changing the original collection.
	 * 
	 * @param items
	 */
	private void setItems(Collection<E> items)
	{
		this.items = items;
	}
}
