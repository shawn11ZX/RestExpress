package com.blogging.persistence;

import java.util.List;

import com.blogging.domain.Blog;
import com.mongodb.ServerAddress;

public class MongoBlogRepository
extends AbstractMongoDbRepository<Blog>
implements BlogRepository
{
    public MongoBlogRepository(List<ServerAddress> bootstraps)
    {
	    super(bootstraps, Blog.class);
    }
}
