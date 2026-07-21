package tr.com.eno.livo.cloud.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/** Synchronizer-token protection for all state-changing MVC requests. */
public class CsrfTokenInterceptor extends HandlerInterceptorAdapter {

	public static final String SESSION_ATTRIBUTE = "csrfToken";
	public static final String PARAMETER = "_csrf";
	private final SecureRandom secureRandom = new SecureRandom();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
		if (isSafe(request.getMethod()) && isSessionlessPath(request.getRequestURI())) {
			return true;
		}
		HttpSession session = request.getSession(true);
		String expected = (String) session.getAttribute(SESSION_ATTRIBUTE);
		if (expected == null) {
			expected = createToken();
			session.setAttribute(SESSION_ATTRIBUTE, expected);
		}

		if (isSafe(request.getMethod())) {
			return true;
		}

		String actual = request.getParameter(PARAMETER);
		if (actual == null || !MessageDigest.isEqual(
				expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
			return false;
		}
		return true;
	}

	private boolean isSafe(String method) {
		return "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);
	}

	private boolean isSessionlessPath(String requestUri) {
		return requestUri != null && (requestUri.endsWith("/health") || requestUri.contains("/resources/"));
	}

	private String createToken() {
		byte[] token = new byte[32];
		secureRandom.nextBytes(token);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
	}
}
