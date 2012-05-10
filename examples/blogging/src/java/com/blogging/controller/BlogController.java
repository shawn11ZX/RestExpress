package com.blogging.controller;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;

import com.blogging.Constants;
import com.blogging.domain.Blog;
import com.blogging.persistence.BlogRepository;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.util.XLinkUtils;

/**
 * @author toddf
 * @since Aug 31, 2010
 */
public class BlogController
{
	private BlogRepository repo;
	
	public BlogController(BlogRepository repository)
	{
		super();
		this.repo = repository;
	}

	public void create(Request request, Response response)
	{
		Blog blog = request.getBodyAs(Blog.class, "Blog data not provided.");
		blog.validate();
		Blog result = repo.create(blog);
		String linkUrl = request.getNamedUrl(HttpMethod.GET, Constants.BLOG_URL_NAME);
		response.addHeader(HttpHeaders.Names.LOCATION, XLinkUtils.asLocationUrl(result.getId(), "blogId", linkUrl));
		response.setResponseCreated();
	}

	public Blog read(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.BLOG_ID_HEADER, "Blog ID not provided.");
		return repo.read(id);
	}

	public void update(Request request, Response response)
	{
		Blog blog = request.getBodyAs(Blog.class, "Blog data not provided.");
		blog.validate();
		repo.update(blog);
	}

	public void delete(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.BLOG_ID_HEADER, "Blog ID not provided.");
		repo.delete(id);
	}
}
