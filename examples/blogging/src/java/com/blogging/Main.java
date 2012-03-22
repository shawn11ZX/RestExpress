package com.blogging;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.blogging.postprocessor.LastModifiedHeaderPostprocessor;
import com.blogging.serialization.BlogJsonProcessor;
import com.blogging.serialization.BlogXmlProcessor;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.Parameters;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.exception.BadRequestException;
import com.strategicgains.restexpress.exception.ConflictException;
import com.strategicgains.restexpress.exception.NotFoundException;
import com.strategicgains.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import com.strategicgains.restexpress.plugin.CacheControlPlugin;
import com.strategicgains.restexpress.plugin.RoutesMetadataPlugin;
import com.strategicgains.restexpress.util.Environment;
import com.strategicgains.syntaxe.ValidationException;

/**
 * The main entry-point into RestExpress for the example blog services.
 * 
 * @author toddf
 * @since Aug 31, 2009
 */
public class Main
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	throws Exception
	{
		Configuration config = loadEnvironment(args);
		RestExpress server = new RestExpress(new Routes(config.getBlogRespository(),
			config.getEntriesRespository(), config.getCommentsRespository()))
		    .setName(config.getName())
		    .setPort(config.getPort())
		    .putSerializationProcessor(Format.JSON, new BlogJsonProcessor())
		    .putSerializationProcessor(Format.XML, new BlogXmlProcessor())
		    .setDefaultFormat(config.getDefaultFormat())
		    .addMessageObserver(new SimpleConsoleLogMessageObserver())
		    .addPostprocessor(new LastModifiedHeaderPostprocessor());
		
		new RoutesMetadataPlugin()
			.register(server)
			.parameter(Parameters.Cache.MAX_AGE, 86400);	// 24 hours, in seconds.
		
		new CacheControlPlugin()
			.register(server);

		mapExceptions(server);
		server.bind();
		server.awaitShutdown();
	}

	/**
     * @param server
     */
    private static void mapExceptions(RestExpress server)
    {
    	server
    	.mapException(ItemNotFoundException.class, NotFoundException.class)
    	.mapException(DuplicateItemException.class, ConflictException.class)
    	.mapException(ValidationException.class, BadRequestException.class);
    }

	private static Configuration loadEnvironment(String[] args)
    throws FileNotFoundException, IOException
    {
	    if (args.length > 0)
		{
			return Environment.from(args[0], Configuration.class);
		}

	    return Environment.fromDefault(Configuration.class);
    }
}
