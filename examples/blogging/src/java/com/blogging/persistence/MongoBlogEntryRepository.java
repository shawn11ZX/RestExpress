package com.blogging.persistence;

import java.util.List;

import org.bson.types.ObjectId;

import com.blogging.domain.BlogEntry;
import com.google.code.morphia.query.Query;
import com.mongodb.Mongo;

public class MongoBlogEntryRepository
extends AbstractMongoDbRepository<BlogEntry>
implements BlogEntryRepository
{
	public MongoBlogEntryRepository(Mongo mongo)
	{
		super(mongo, BlogEntry.class);
	}

	@Override
	public List<BlogEntry> readAll(String blogId)
	{
		Query<BlogEntry> query = getDataStore().find(BlogEntry.class, "blogId", new ObjectId(blogId)).order("-createdAt");
		return query.asList();
	}
}
