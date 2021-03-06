package org.sagebionetworks.repo.model.table.parser;

import org.sagebionetworks.repo.model.jdo.KeyFactory;

public class EntityIdParser extends AbstractValueParser {

	@Override
	public Object parseValueForDatabaseWrite(String value) throws IllegalArgumentException {
		if(value == null){
			throw new IllegalArgumentException("Value cannot be null");
		}
		return KeyFactory.stringToKey(value);
	}
	
	@Override
	public String parseValueForDatabaseRead(String value)
			throws IllegalArgumentException {
		return KeyFactory.keyToString(Long.parseLong(value));
	}

}
