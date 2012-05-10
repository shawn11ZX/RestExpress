package com.blogging.persistence;

import java.util.List;

import org.bson.types.ObjectId;

import com.blogging.domain.Comment;
import com.google.code.morphia.query.Query;
import com.mongodb.ServerAddress;

public class MongoCommentRepository
extends AbstractMongoDbRepository<Comment>
implements CommentRepository
{
	public MongoCommentRepository(List<ServerAddress> bootstraps)
	{
		super(bootstraps, Comment.class);
	}

	@Override
	public List<Comment> readAll(String blogEntryId)
	{
		Query<Comment> query = getDataStore().find(Comment.class, "blogEntryId", new ObjectId(blogEntryId)).order("createdAt");
		return query.asList();
	}
}
