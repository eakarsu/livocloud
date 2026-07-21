package tr.com.eno.livo.cloud.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CsrfTokenInterceptorTest {

	private final CsrfTokenInterceptor interceptor = new CsrfTokenInterceptor();

	@Test
	public void safeRequestCreatesSessionTokenAndSecurityHeaders() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
		MockHttpServletResponse response = new MockHttpServletResponse();

		assertTrue(interceptor.preHandle(request, response, new Object()));
		assertNotNull(request.getSession().getAttribute(CsrfTokenInterceptor.SESSION_ATTRIBUTE));
		assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
		assertEquals("DENY", response.getHeader("X-Frame-Options"));
	}

	@Test
	public void unsafeRequestRejectsMissingToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/signUp");
		MockHttpServletResponse response = new MockHttpServletResponse();

		assertFalse(interceptor.preHandle(request, response, new Object()));
		assertEquals(403, response.getStatus());
	}

	@Test
	public void healthCheckDoesNotCreateAProbeSession() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/LivoCloud/health");
		assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
		assertEquals(null, request.getSession(false));
	}

	@Test
	public void unsafeRequestAcceptsMatchingSessionToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/signUp");
		request.getSession().setAttribute(CsrfTokenInterceptor.SESSION_ATTRIBUTE, "known-token");
		request.addParameter(CsrfTokenInterceptor.PARAMETER, "known-token");

		assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
	}
}
