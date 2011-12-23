package com.echo;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.echo.serialization.JsonSerializationProcessor;
import com.echo.serialization.XmlSerializationProcessor;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.Parameters;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.plugin.RoutesMetadataPlugin;
import com.strategicgains.restexpress.util.Environment;

/**
 * The main entry-point into the RestExpress Echo example services.
 * 
 * @author toddf
 * @since Aug 31, 2009
 */
public class Main
{
	public static void main(String[] args) throws Exception
	{
		Configuration config = loadEnvironment(args);
		RestExpress server = new RestExpress(new Routes(config))
		    .setName(config.getName())
		    .setPort(config.getPort())
		    .setDefaultFormat(config.getDefaultFormat())
		    
		    // Use JSEND-style (wrapped) responses:
		    .useWrappedResponses()
		    .putSerializationProcessor(Format.JSON, new JsonSerializationProcessor())
		    .putSerializationProcessor(Format.XML, new XmlSerializationProcessor());

			// Since this application is primarily for performance testing of RESTExpress, 
			// we're turning off all the output and cache controls.
//		    .addMessageObserver(new SimpleConsoleLogMessageObserver())
//		    .addPostprocessor(new DateHeaderPostprocessor())
//		    .addPostprocessor(new CacheHeaderPostprocessor());

		new RoutesMetadataPlugin().register(server)
			.parameter(Parameters.Cache.MAX_AGE, 86400);	// Cache for 1 day (24 hours).

		mapExceptions(server);
		server.bind();
		server.awaitShutdown();
	}

	/**
     * @param server
     */
    private static void mapExceptions(RestExpress server)
    {
//    	server
//    	.mapException(ItemNotFoundException.class, NotFoundException.class)
//    	.mapException(DuplicateItemException.class, ConflictException.class)
//    	.mapException(ValidationException.class, BadRequestException.class);
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
