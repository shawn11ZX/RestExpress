package com.blogging.persistence;

import java.util.List;

import com.blogging.domain.Blog;
import com.mongodb.Mongo;

public class MongoBlogRepository
extends AbstractMongoDbRepository<Blog>
implements BlogRepository
{
    public MongoBlogRepository(Mongo mongo)
    {
	    super(mongo, Blog.class);
    }

    @Override
    public List<Blog> readOwnedBlogs(String author)
    {
    	return getDataStore().find(Blog.class, "author", author).asList();
    }
}
