package org.sagebionetworks.repo.manager.discussion;

import java.io.IOException;

import org.sagebionetworks.reflection.model.PaginatedResults;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.discussion.CreateDiscussionReply;
import org.sagebionetworks.repo.model.discussion.DiscussionFilter;
import org.sagebionetworks.repo.model.discussion.DiscussionReplyBundle;
import org.sagebionetworks.repo.model.discussion.DiscussionReplyOrder;
import org.sagebionetworks.repo.model.discussion.MessageURL;
import org.sagebionetworks.repo.model.discussion.ReplyCount;
import org.sagebionetworks.repo.model.discussion.UpdateReplyMessage;

public interface DiscussionReplyManager {

	/**
	 * Creating a new reply
	 * 
	 * @param userInfo
	 * @param createReply
	 * @return
	 * @throws IOException 
	 */
	public DiscussionReplyBundle createReply(UserInfo userInfo, CreateDiscussionReply createReply) throws IOException;

	/**
	 * Get a reply by its ID
	 * 
	 * @param userInfo
	 * @param replyId
	 * @return
	 */
	public DiscussionReplyBundle getReply(UserInfo userInfo, String replyId);

	/**
	 * Update a reply message
	 * 
	 * @param userInfo
	 * @param replyId
	 * @param newMessage
	 * @return
	 * @throws IOException 
	 */
	public DiscussionReplyBundle updateReplyMessage(UserInfo userInfo, String replyId, UpdateReplyMessage newMessage) throws IOException;

	/**
	 * Mark a reply as deleted
	 * 
	 * @param userInfo
	 * @param replyId
	 */
	public void markReplyAsDeleted(UserInfo userInfo, String replyId);

	/**
	 * Get a thread's replies
	 * 
	 * @param userInfo
	 * @param threadId
	 * @param limit
	 * @param offset
	 * @param order
	 * @param ascending
	 * @param filter 
	 * @return
	 */
	public PaginatedResults<DiscussionReplyBundle> getRepliesForThread(UserInfo userInfo,
			String threadId, Long limit, Long offset, DiscussionReplyOrder order,
			Boolean ascending, DiscussionFilter filter);

	/**
	 * Get total number of replies for a given threadId
	 * 
	 * @param userInfo
	 * @param threadId
	 * @param filter 
	 * @return
	 */
	public ReplyCount getReplyCountForThread(UserInfo userInfo, String threadId, DiscussionFilter filter);

	/**
	 * Get message Url for a reply
	 * 
	 * @param user
	 * @param messageKey
	 */
	public MessageURL getMessageUrl(UserInfo user, String messageKey);

	/**
	 * Check to see if the user has the permission accessType on this replyId
	 * 
	 * @param userInfo
	 * @param replyId
	 * @param accessType
	 */
	public void checkPermission(UserInfo userInfo, String replyId, ACCESS_TYPE accessType);
}
