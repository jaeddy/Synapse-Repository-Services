package org.sagebionetworks.acl.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.asynchronous.workers.sqs.MessageUtils;
import org.sagebionetworks.audit.dao.AclRecordDAO;
import org.sagebionetworks.audit.dao.ResourceAccessRecordDAO;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.AccessControlListDAO;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.audit.AclRecord;
import org.sagebionetworks.repo.model.audit.ResourceAccessRecord;
import org.sagebionetworks.repo.model.message.ChangeMessage;
import org.sagebionetworks.repo.model.message.ChangeType;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.sqs.model.Message;

/**
 * This worker writes ACL change messages to a file, and put the file to S3 
 */
public class AclSnapshotWorker implements MessageDrivenRunner {

	static private Logger log = LogManager.getLogger(AclSnapshotWorker.class);
	@Autowired
	private AclRecordDAO aclRecordDao;
	@Autowired
	private ResourceAccessRecordDAO resourceAccessRecordDao;
	@Autowired
	private AccessControlListDAO accessControlListDao;

	public AclSnapshotWorker() {
	}

	// for unit test only
	AclSnapshotWorker(AclRecordDAO aclRecordDao, ResourceAccessRecordDAO resourceAccessRecordDao,
			AccessControlListDAO accessControlListDao) {
		this.aclRecordDao = aclRecordDao;
		this.resourceAccessRecordDao = resourceAccessRecordDao;
		this.accessControlListDao = accessControlListDao;
	}


	@Override
	public void run(ProgressCallback<Message> progressCallback, Message message) throws IOException{
		// Keep this message invisible
		progressCallback.progressMade(message);

		ChangeMessage changeMessage = MessageUtils.extractMessageBody(message);
		if (changeMessage.getObjectType() != ObjectType.ACCESS_CONTROL_LIST) {
			// do nothing when receive a non-acl message
			return;
		}

		if (changeMessage.getChangeType() == ChangeType.DELETE) {
			AclRecord aclRecord = buildAclRecord(changeMessage);
			aclRecordDao.saveBatch(Arrays.asList(aclRecord));
			return;
		}

		AccessControlList acl = null;
		try {
			acl = accessControlListDao.get(Long.parseLong(changeMessage.getObjectId()));
			if (!acl.getEtag().equals(changeMessage.getObjectEtag())) {
				log.info("Ignoring old message.");
				return;
			}
		} catch (NotFoundException e) {
			log.error("Cannot find acl for a " + changeMessage.getChangeType() + " message: " + changeMessage.toString(), e) ;
			return;
		}

		try {
			AclRecord aclRecord = buildAclRecord(changeMessage, acl);
			aclRecordDao.saveBatch(Arrays.asList(aclRecord));
			List<ResourceAccessRecord> resourceAccessRecords = buildResourceAccessRecordList(changeMessage, acl);
			if (!resourceAccessRecords.isEmpty()) {
				resourceAccessRecordDao.saveBatch(resourceAccessRecords);
			}
			return;
		} catch (NotFoundException e) {
			log.info("Could not find ownerType for an acl record.");
			return;
		}
	}

	private AclRecord buildAclRecord(ChangeMessage message) {
		AclRecord record = new AclRecord();
		record.setAclId(message.getObjectId());
		record.setChangeNumber(message.getChangeNumber());
		record.setChangeType(message.getChangeType());
		record.setTimestamp(message.getTimestamp().getTime());
		record.setEtag(message.getObjectEtag());
		return record;
	}

	protected List<ResourceAccessRecord> buildResourceAccessRecordList(ChangeMessage message, AccessControlList acl) {
		List<ResourceAccessRecord> records = new ArrayList<ResourceAccessRecord>();
		Set<ResourceAccess> resourceAccessSet = acl.getResourceAccess();
		if (resourceAccessSet == null) {
			return records;
		}
		for (ResourceAccess resourceAccess : resourceAccessSet) {
			Set<ACCESS_TYPE> accessTypeSet = resourceAccess.getAccessType();
			if (accessTypeSet != null) {
				for (ACCESS_TYPE accessType : accessTypeSet) {
					ResourceAccessRecord record = new ResourceAccessRecord();
					record.setChangeNumber(message.getChangeNumber());
					record.setPrincipalId(resourceAccess.getPrincipalId());
					record.setAccessType(accessType);
					records.add(record);
				}
			}
		}
		return records;
	}

	protected AclRecord buildAclRecord(ChangeMessage message, AccessControlList acl) throws NotFoundException{
		AclRecord record = buildAclRecord(message);
		record.setOwnerId(acl.getId());
		record.setCreationDate(acl.getCreationDate());
		record.setOwnerType(accessControlListDao.getOwnerType(Long.parseLong(message.getObjectId())));
		return record;
	}
	
}
