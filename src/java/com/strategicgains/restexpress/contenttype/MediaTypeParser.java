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
package com.strategicgains.restexpress.contenttype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author toddf
 * @since Jan 18, 2013
 */
public class MediaTypeParser
{
	/**
	 * Parses a Content-Type or Accept header into an ordered List of MediaTypeSegment
	 * instances, which in turn can be used to determine which media type is most appropriate
	 * for serialization.
	 * 
	 * @param contentTypeHeader
	 * @return
	 */
	public static List<MediaRange> parse(String contentTypeHeader)
	{
		String[] segments = contentTypeHeader.split("\\s*,\\s*");
		List<MediaRange> items = new ArrayList<MediaRange>();

		for (String segment : segments)
		{
			items.add(MediaRange.parse(segment));
		}

		return items;
	}

	public static String getBestMatch(List<MediaRange> supported, List<MediaRange> requested)
	{
		List<WeightedMatch> matches = new ArrayList<WeightedMatch>();

		for (MediaRange target : supported)
		{
			WeightedMatch m = getWeightedMatch(target, requested);
			
			if (m != null)
			{
				matches.add(m);
			}
		}

		if (matches.isEmpty()) return null;
		if (matches.size() == 1) return matches.get(0).mediaRange.asMediaType();

		Collections.sort(matches);
		return matches.get(0).mediaRange.asMediaType();
	}

	public static WeightedMatch getWeightedMatch(MediaRange target, List<MediaRange> parsedRanges)
	{
		int maxRank = -1;
		MediaRange bestMatch = null;

		for (MediaRange parsed : parsedRanges)
		{
			int rank = target.rankAgainst(parsed);

			if (rank > maxRank)
			{
				maxRank = rank;
				bestMatch = target;
			}
		}

		return (maxRank == -1 ? null : new WeightedMatch(bestMatch, maxRank));
	}
	
	protected static class WeightedMatch
	implements Comparable<WeightedMatch>
	{
		MediaRange mediaRange;
		int rank;
		
		public WeightedMatch(MediaRange range, int rank)
		{
			this.mediaRange = range;
			this.rank = rank;
		}

		/**
		 * Reverse-rank natural ordering.
		 */
		@Override
        public int compareTo(WeightedMatch that)
        {
			int rankSign = (that.rank - this.rank);
			
			if (rankSign == 0)
			{
				return (int) ((that.mediaRange.qvalue - this.mediaRange.qvalue) * 10);
			}
			
			return rankSign;
        }
	}
}
