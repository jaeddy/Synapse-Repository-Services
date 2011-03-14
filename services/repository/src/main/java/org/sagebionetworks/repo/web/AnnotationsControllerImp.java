package org.sagebionetworks.repo.web;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.schema.JsonSchema;
import org.sagebionetworks.repo.model.AnnotatableDAO;
import org.sagebionetworks.repo.model.AnnotationDAO;
import org.sagebionetworks.repo.model.Annotations;
import org.sagebionetworks.repo.model.Base;
import org.sagebionetworks.repo.model.BaseDAO;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.util.SchemaHelper;

/**
 * Implementation for REST controller for CRUD operations on Annotation DTOs and
 * DAOs
 * <p>
 * 
 * This class performs the basic CRUD operations for all our DAO-backed
 * annotatable model objects. See controllers specific to particular models for
 * any special handling.
 * <p>
 * 
 * TODO this patient is still lying open on the operating table, don't CR it yet
 * 
 * @author deflaux
 * @param <T>
 *            the particular type of entity the controller is managing
 */
public class AnnotationsControllerImp<T extends Base> implements
		AnnotationsController<T> {

	private static final Logger log = Logger
			.getLogger(AnnotationsControllerImp.class.getName());

	private AnnotatableDAO<T> annotatableDao;

	@Override
	public void setDao(BaseDAO<T> dao) {
		annotatableDao = (AnnotatableDAO<T>) dao;
	}

	@Override
	public Annotations getEntityAnnotations(String userId, String id,
			HttpServletRequest request) throws NotFoundException,
			DatastoreException, UnauthorizedException {

		String entityId = UrlHelpers.getEntityIdFromUriId(id);

		Annotations annotations = annotatableDao.getAnnotations(entityId);
		if (null == annotations) {
			throw new NotFoundException("no entity with id " + entityId
					+ " exists");
		}

		addServiceSpecificMetadata(id, annotations, request);

		return annotations;
	}

	@Override
	public Annotations updateEntityAnnotations(String userId, String id,
			Integer etag, Annotations updatedAnnotations,
			HttpServletRequest request) throws NotFoundException,
			ConflictingUpdateException, DatastoreException,
			UnauthorizedException {

		String entityId = UrlHelpers.getEntityIdFromUriId(id);

		Annotations annotations = annotatableDao.getAnnotations(entityId);
		if (null == annotations) {
			throw new NotFoundException("no entity with id " + entityId
					+ " exists");
		}

		if (etag != annotations.hashCode()) {
			throw new ConflictingUpdateException(
					"annotations for entity with id "
							+ entityId
							+ "were updated since you last fetched them, retrieve them again and reapply the update");
		}

		// dao.update(updatedAnnotations);

		// TODO this isn't how we want to do this for real
		// TODO this is currently additive but it should be overwriting
		// Developer Note: yes, nested loops are evil when N is large

		AnnotationDAO<T, String> stringAnnotationDAO = annotatableDao
				.getStringAnnotationDAO(entityId);
		AnnotationDAO<T, Double> doubleAnnotationDAO = annotatableDao
				.getDoubleAnnotationDAO(entityId);
		AnnotationDAO<T, Long> longAnnotationDAO = annotatableDao
				.getLongAnnotationDAO(entityId);
		AnnotationDAO<T, Date> dateAnnotationDAO = annotatableDao
				.getDateAnnotationDAO(entityId);

		Map<String, Collection<String>> updatedStringAnnotations = updatedAnnotations
				.getStringAnnotations();
		for (Map.Entry<String, Collection<String>> updatedAnnotation : updatedStringAnnotations
				.entrySet()) {
			for (String value : updatedAnnotation.getValue()) {
				stringAnnotationDAO.addAnnotation(updatedAnnotation.getKey(),
						value);
			}
		}

		Map<String, Collection<Double>> updatedDoubleAnnotations = updatedAnnotations
				.getDoubleAnnotations();
		for (Map.Entry<String, Collection<Double>> updatedAnnotation : updatedDoubleAnnotations
				.entrySet()) {
			for (Double value : updatedAnnotation.getValue()) {
				doubleAnnotationDAO.addAnnotation(updatedAnnotation.getKey(),
						value);
			}
		}

		Map<String, Collection<Long>> updatedLongAnnotations = updatedAnnotations
				.getLongAnnotations();
		for (Map.Entry<String, Collection<Long>> updatedAnnotation : updatedLongAnnotations
				.entrySet()) {
			for (Long value : updatedAnnotation.getValue()) {
				longAnnotationDAO.addAnnotation(updatedAnnotation.getKey(),
						value);
			}
		}

		Map<String, Collection<Date>> updatedDateAnnotations = updatedAnnotations
				.getDateAnnotations();
		for (Map.Entry<String, Collection<Date>> updatedAnnotation : updatedDateAnnotations
				.entrySet()) {
			for (Date value : updatedAnnotation.getValue()) {
				dateAnnotationDAO.addAnnotation(updatedAnnotation.getKey(),
						value);
			}
		}

		addServiceSpecificMetadata(id, updatedAnnotations, request);

		return updatedAnnotations;
	}

	@Override
	public JsonSchema getEntityAnnotationsSchema() throws DatastoreException {
		return SchemaHelper.getSchema(Annotations.class);
	}

	private void addServiceSpecificMetadata(String id, Annotations annotations,
			HttpServletRequest request) {
		annotations.setId(id); // the NON url-encoded id
		annotations.setUri(UrlHelpers.makeEntityPropertyUri(request));
		annotations.setEtag(UrlHelpers.makeEntityEtag(annotations));
	}
}
