package com.blogging;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.blogging.controller.BlogController;
import com.blogging.controller.BlogEntryController;
import com.blogging.controller.CommentController;
import com.blogging.postprocessor.LastModifiedHeaderPostprocessor;
import com.blogging.serialization.ResponseProcessors;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
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
 * <p/>
 * This Blogging example is a full-up project with MongoDB persistence. It supports
 * raw JSON and XML responses as well as wrapped XML and JSON responses.
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
		RestExpress server = new RestExpress()
		    .setName(config.getName())
		    .setPort(config.getPort())
		    .putResponseProcessor(Format.JSON, ResponseProcessors.json())
		    .putResponseProcessor(Format.XML, ResponseProcessors.xml())
		    .putResponseProcessor(Format.WRAPPED_JSON, ResponseProcessors.wrappedJson())
		    .putResponseProcessor(Format.WRAPPED_XML, ResponseProcessors.wrappedXml())
		    .setDefaultFormat(config.getDefaultFormat())
		    .addMessageObserver(new SimpleConsoleLogMessageObserver())
		    .addPostprocessor(new LastModifiedHeaderPostprocessor());
		
		defineRoutes(server, config);
		
		new RoutesMetadataPlugin()
			.register(server)
			.parameter(Parameters.Cache.MAX_AGE, 86400);	// 24 hours, in seconds.
		
		new CacheControlPlugin()
			.register(server);

		mapExceptions(server);
		server.bind();
		server.awaitShutdown();
	}

	private static void defineRoutes(RestExpress server, Configuration config)
	{
		BlogController blogController = config.getBlogController();
		BlogEntryController entryController = config.getEntryController();
		CommentController commentController = config.getCommentController();

		//Create a new blog.  Auto-assigned ID.
		server.uri("/blogs.{format}", blogController)
			.method(HttpMethod.POST);
		
		server.uri("/blogs/authors/{author}.{format}", blogController)
			.action("readOwnedBlogs", HttpMethod.GET);

		// Read, update, delete a blog.
		server.uri("/blogs/{blogId}.{format}", blogController)
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
			.name(Constants.BLOG_URL_NAME);

		// List the blog entries.
		server.uri("/blogs/{blogId}/entries.{format}", entryController)
			.action("list", HttpMethod.GET)
			.method(HttpMethod.POST);

		// Read, update, delete a blog entry.
		server.uri("/blogs/{blogId}/entries/{entryId}.{format}", entryController)
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
			.name("BlogEntryUri");

		// List the comments for an entry.
		server.uri("/blogs/{blogId}/entries/{entryId}/comments.{format}", commentController)
			.action("list", HttpMethod.GET)
			.method(HttpMethod.POST);

		// Read, update, delete a blog entry comment.
		server.uri("/blogs/{blogId}/entries/{entryId}/comments/{commentId}.{format}", commentController)
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE);
	}

	/**
     * @param server
     */
    private static void mapExceptions(RestExpress server)
    {
    	server
    	.mapException(ItemNotFoundException.class, NotFoundException.class)
    	.mapException(DuplicateItemException.class, ConflictException.class)
    	.mapException(ValidationException.class, BadRequestException.class)
    	.mapException(InvalidObjectIdException.class, BadRequestException.class);
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
