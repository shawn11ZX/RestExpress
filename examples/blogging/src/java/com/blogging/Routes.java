package com.blogging;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.blogging.controller.BlogController;
import com.blogging.controller.BlogEntryController;
import com.blogging.controller.CommentController;
import com.blogging.persistence.BlogEntryRepository;
import com.blogging.persistence.BlogRepository;
import com.blogging.persistence.CommentRepository;
import com.strategicgains.restexpress.route.RouteDeclaration;

/**
 * @author toddf
 * @since May 21, 2010
 */
public class Routes
extends RouteDeclaration
{
	private BlogController blogController;
	private BlogEntryController entryController;
	private CommentController commentController;
	
	public Routes(BlogRepository blogRepository, BlogEntryRepository entryRepository, CommentRepository commentRepository)
	{
		super();
		this.blogController = new BlogController(blogRepository);
		this.entryController = new BlogEntryController(entryRepository);
		this.commentController = new CommentController(commentRepository);
	}
	
	@Override
	protected void defineRoutes()
	{
		//Create a new blog.  Auto-assigned ID.
		uri("/blogs.{format}", blogController)
			.method(HttpMethod.POST);
		
		uri("/blogs/authors/{author}.{format}", blogController)
			.action("readOwnedBlogs", HttpMethod.GET);

		// Read, update, delete a blog.
		uri("/blogs/{blogId}.{format}", blogController)
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
			.name(Constants.BLOG_URL_NAME);

		// List the blog entries.
		uri("/blogs/{blogId}/entries.{format}", entryController)
			.action("list", HttpMethod.GET)
			.method(HttpMethod.POST);

		// Read, update, delete a blog entry.
		uri("/blogs/{blogId}/entries/{entryId}.{format}", entryController)
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
			.name("BlogEntryUri");

		// List the comments for an entry.
		uri("/blogs/{blogId}/entries/{entryId}/comments.{format}", commentController)
			.action("list", HttpMethod.GET)
			.method(HttpMethod.POST);

		// Read, update, delete a blog entry comment.
		uri("/blogs/{blogId}/entries/{entryId}/comments/{commentId}.{format}", commentController)
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE);
	}
}
