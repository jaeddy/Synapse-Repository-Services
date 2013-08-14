package org.sagebionetworks.repo.web;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.repo.manager.UserManager;
import org.sagebionetworks.repo.model.AuthorizationConstants;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit test for AccessInterceptor.
 * 
 * @author jmhill
 *
 */
public class AccessInterceptorTest {
	
	HttpServletRequest mockRequest;
	HttpServletResponse mockResponse;
	Object mockHandler;
	UserInfo mockUserInfo;
	StubAccessRecorder stubRecorder;
	UserManager mockUserManager;
	AccessInterceptor interceptor;
	String userName;

	@Before
	public void before() throws Exception {
		userName = "user@users.r.us.gov";
		mockRequest = Mockito.mock(HttpServletRequest.class);
		mockResponse = Mockito.mock(HttpServletResponse.class);
		mockHandler = Mockito.mock(Object.class);
		mockUserManager = Mockito.mock(UserManager.class);
		mockUserInfo = new UserInfo(false);
		mockUserInfo.setIndividualGroup(new UserGroup());
		mockUserInfo.getIndividualGroup().setId("123");
		stubRecorder = new StubAccessRecorder();
		interceptor = new AccessInterceptor();
		ReflectionTestUtils.setField(interceptor, "accessRecorder", stubRecorder);
		ReflectionTestUtils.setField(interceptor, "userManager", mockUserManager);
		// Setup the happy mock
		when(mockUserManager.getUserInfo(userName)).thenReturn(mockUserInfo);
		when(mockRequest.getParameter(AuthorizationConstants.USER_ID_PARAM)).thenReturn(userName);
		when(mockRequest.getRequestURI()).thenReturn("/entity/syn789");
		when(mockRequest.getMethod()).thenReturn("DELETE");
		when(mockRequest.getHeader("Host")).thenReturn("localhost8080");
		when(mockRequest.getHeader("User-Agent")).thenReturn("HAL 2000");
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("moon.org");
		when(mockRequest.getHeader("Origin")).thenReturn("http://www.example-social-network.com");
		when(mockRequest.getHeader("Via")).thenReturn("1.0 fred, 1.1 example.com");
	}
	
	
	
	@Test
	public void testHappyCase() throws Exception{
		long start = System.currentTimeMillis();
		// Start
		interceptor.preHandle(mockRequest, mockResponse, mockHandler);
		// Wait to add some elapse time
		Thread.sleep(100);
		// finish the call
		Exception exception = null;
		interceptor.afterCompletion(mockRequest, mockResponse, mockHandler, exception);
		// Now get the results from the stub
		assertNotNull(stubRecorder.getSavedRecords());
		assertEquals(1, stubRecorder.getSavedRecords().size());
		AccessRecord result = stubRecorder.getSavedRecords().get(0);
		assertNotNull(result);
		assertTrue(result.getTimestamp() >= start);
		assertTrue(result.getElapseMS() > 99);
		assertTrue(result.getSuccess());
		assertNotNull(result.getSessionId());
		assertEquals("/entity/syn789", result.getRequestURL());
		assertEquals("DELETE", result.getMethod());
		assertEquals("localhost8080", result.getHost());
		assertEquals("HAL 2000", result.getUserAgent());
		assertEquals("moon.org", result.getXForwardedFor());
		assertEquals("http://www.example-social-network.com", result.getOrigin());
		assertEquals("1.0 fred, 1.1 example.com", result.getVia());
		assertEquals(new Long(Thread.currentThread().getId()), result.getThreadId());
	}
	
	@Test
	public void testHappyCaseWithException() throws Exception{
		long start = System.currentTimeMillis();
		// Start
		interceptor.preHandle(mockRequest, mockResponse, mockHandler);
		// Wait to add some elapse time
		Thread.sleep(100);
		// finish the call
		String error = "Something went horribly wrong!!!";
		Exception exception = new IllegalArgumentException(error);
		interceptor.afterCompletion(mockRequest, mockResponse, mockHandler, exception);
		// Now get the results from the stub
		assertNotNull(stubRecorder.getSavedRecords());
		assertEquals(1, stubRecorder.getSavedRecords().size());
		AccessRecord result = stubRecorder.getSavedRecords().get(0);
		assertNotNull(result);
		assertTrue(result.getTimestamp() >= start);
		assertTrue(result.getElapseMS() > 99);
		assertFalse(result.getSuccess());
	}
}
