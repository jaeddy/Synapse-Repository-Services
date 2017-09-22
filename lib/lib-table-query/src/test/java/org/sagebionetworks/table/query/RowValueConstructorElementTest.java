package org.sagebionetworks.table.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.table.query.model.RowValueConstructorElement;

public class RowValueConstructorElementTest {

	@Test
	public void testRowValueConstructorElementToSQL() throws ParseException{
		RowValueConstructorElement element = new TableQueryParser("bar").rowValueConstructorElement();
		assertEquals("bar", element.toString());
	}
	
	@Test
	public void testRowValueConstructorElementToSQLNull() throws ParseException{
		RowValueConstructorElement element = new TableQueryParser("null").rowValueConstructorElement();
		assertEquals("NULL", element.toString());
	}
	
	@Test
	public void testRowValueConstructorElementToSQLDefault() throws ParseException{
		RowValueConstructorElement element = new TableQueryParser("default").rowValueConstructorElement();
		assertEquals("DEFAULT", element.toString());
	}
	
	@Test
	public void testRowValueConstructorElementTrue() throws ParseException{
		RowValueConstructorElement element = new TableQueryParser("true").rowValueConstructorElement();
		assertEquals("TRUE", element.toString());
	}
	
	@Test
	public void testRowValueConstructorElementFalse() throws ParseException{
		RowValueConstructorElement element = new TableQueryParser("false").rowValueConstructorElement();
		assertEquals("FALSE", element.toString());
	}
}
