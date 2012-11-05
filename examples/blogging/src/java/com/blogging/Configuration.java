/*
    Copyright 2011, Strategic Gains, Inc.

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
package com.blogging;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.blogging.controller.BlogController;
import com.blogging.controller.BlogEntryController;
import com.blogging.controller.CommentController;
import com.blogging.persistence.BlogEntryRepository;
import com.blogging.persistence.BlogRepository;
import com.blogging.persistence.CommentRepository;
import com.blogging.persistence.MongoBlogEntryRepository;
import com.blogging.persistence.MongoBlogRepository;
import com.blogging.persistence.MongoCommentRepository;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.exception.ConfigurationException;
import com.strategicgains.restexpress.util.Environment;

/**
 * @author toddf
 * @since Feb 10, 2011
 */
public class Configuration
extends Environment
{
	private static final String NAME_PROPERTY = "name";
	private static final String PORT_PROPERTY = "port";
	private static final String PERSISTENCE_PROPERTY = "persistence";
	private static final String DEFAULT_FORMAT_PROPERTY = "defaultFormat";
	private static final String MONGODB_PERSISTENCE = "mongodb";
	private static final String BOOTSTRAPS_PROPERTY = "mongodb.bootstraps";
	private static final int DEFAULT_MONGODB_PORT = 27017;

	private int port;
	private String name;
	private String persistence;
	private String defaultFormat;
	
	private BlogController blogController;
	private BlogEntryController entryController;
	private CommentController commentController;
	
	private BlogRepository blogRepository;
	private BlogEntryRepository entryRepository;
	private CommentRepository commentRepository;

	@Override
	protected void fillValues(Properties p)
	{
		this.name = p.getProperty(NAME_PROPERTY, RestExpress.DEFAULT_NAME);
		this.port = Integer.parseInt(p.getProperty(PORT_PROPERTY, String.valueOf(RestExpress.DEFAULT_PORT)));
		this.persistence = p.getProperty(PERSISTENCE_PROPERTY, MONGODB_PERSISTENCE);
		this.defaultFormat = p.getProperty(DEFAULT_FORMAT_PROPERTY, Format.JSON);
		String bootstrapString = p.getProperty(BOOTSTRAPS_PROPERTY);
		Mongo mongo = null;
		
		try
		{
			if (bootstrapString != null)
			{
		    	mongo = new Mongo(parseBootstraps(bootstrapString));
			}
			else
			{
				mongo = new Mongo();
			}
		}
		catch(UnknownHostException e)
		{
			throw new ConfigurationException(e);
		}
		
		initialize(mongo);
	}

    private void initialize(Mongo mongo)
    {
    	blogRepository = createBlogRespository(mongo);
    	entryRepository = createEntryRespository(mongo);
    	commentRepository = createCommentRespository(mongo);
    	blogController = createBlogController();
    	entryController = createEntryController();
    	commentController = createCommentController();
    }

	public int getPort()
	{
		return port;
	}

	public String getName()
	{
		return name;
	}
	
	public String getDefaultFormat()
	{
		return defaultFormat;
	}
	
	public BlogController getBlogController()
	{
		return blogController;
	}
	
	public BlogEntryController getEntryController()
	{
		return entryController;
	}
	
	public CommentController getCommentController()
	{
		return commentController;
	}
	
	private BlogController createBlogController()
	{
		return new BlogController(blogRepository);
	}
	
	private BlogEntryController createEntryController()
	{
		return new BlogEntryController(entryRepository);
	}
	
	private CommentController createCommentController()
	{
		return new CommentController(commentRepository);
	}

	private BlogRepository createBlogRespository(Mongo mongo)
	{
		if (MONGODB_PERSISTENCE.equals(persistence))
		{
			return new MongoBlogRepository(mongo);
		}

		return null;
	}

	private BlogEntryRepository createEntryRespository(Mongo mongo)
	{
		if (MONGODB_PERSISTENCE.equals(persistence))
		{
			return new MongoBlogEntryRepository(mongo);
		}

		return null;
	}

	private CommentRepository createCommentRespository(Mongo mongo)
	{
		if (MONGODB_PERSISTENCE.equals(persistence))
		{
			return new MongoCommentRepository(mongo);
		}

		return null;
	}

	/**
     * @param bootstrapString
     * @return
     */
    private List<ServerAddress> parseBootstraps(String bootstrapString)
    {
		String[] bs = bootstrapString.split(",");
		List<ServerAddress> results = new ArrayList<ServerAddress>();
		
		for(String server : bs)
		{
			String[] hostAndPort = server.split(":");
			String host = hostAndPort[0];
			int port = DEFAULT_MONGODB_PORT;

			if(hostAndPort.length > 1)
			{
				port = Integer.parseInt(hostAndPort[1]);
			}

			results.add(new ServerAddress(new InetSocketAddress(host, port)));
		}

		return results;
	}
}
