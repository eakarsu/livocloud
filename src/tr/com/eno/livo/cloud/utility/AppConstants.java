package tr.com.eno.livo.cloud.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import tr.com.eno.livo.cloud.config.RuntimeConfiguration;
import tr.com.eno.livo.cloud.security.VerificationTokenGenerator;

public final class AppConstants {

	public static final String CLOUD_SERVER_ADDRESS = RuntimeConfiguration.get(
			"LIVOCLOUD_PUBLIC_BASE_URL", "http://localhost:8080/LivoCloud");

	private static final VerificationTokenGenerator TOKENS = new VerificationTokenGenerator();

	private AppConstants() {
	}

	public enum UserState {
		SIGNIN(0, "SIGNIN"), PENDING(1, "PENDING"), UNVERIFIED(11, "UNVERIFIED"),
		VERIFIED_INACTIVE(2, "VERIFIED_INACTIVE"), VERIFIED_ACTIVE(3, "VERIFIED_ACTIVE"),
		EXPIRED(31, "EXPIRED"), DELETED(32, "DELETED"), RENEWED(311, "RENEWED");

		public final int value;
		public final String text;

		UserState(int value, String text) {
			this.value = value;
			this.text = text;
		}
	}

	public enum EmailStatus {
		PASSIVE(0, "Passive"), ACTIVE(1, "Active");

		public final int value;
		public final String text;

		EmailStatus(int value, String text) {
			this.value = value;
			this.text = text;
		}
	}

	public enum PlatformType {
		DEV(0, "Developer"), PRO(1, "Professional"), ENTERPRISE(2, "Enterprise");

		public final int value;
		public final String text;

		PlatformType(int value, String text) {
			this.value = value;
			this.text = text;
		}
	}

	/** Retained for legacy callers; generated values are cryptographically random. */
	public static String createToken() {
		return TOKENS.create();
	}

	public static void sendVerificationMessage(String mailToAddress, String mailContent) {
		String apiKey = RuntimeConfiguration.required("LIVOCLOUD_MAILGUN_API_KEY");
		String apiUrl = RuntimeConfiguration.required("LIVOCLOUD_MAILGUN_API_URL");
		String mailFrom = RuntimeConfiguration.required("LIVOCLOUD_MAIL_FROM");

		Client client = Client.create();
		ClientResponse response = null;
		try {
			client.addFilter(new HTTPBasicAuthFilter("api", apiKey));
			WebResource webResource = client.resource(apiUrl);
			MultivaluedMapImpl formData = new MultivaluedMapImpl();
			formData.add("from", mailFrom);
			formData.add("to", mailToAddress);
			formData.add("subject", "Verify your LivoCloud account");
			formData.add("html", mailContent);
			response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
					.post(ClientResponse.class, formData);
			int status = response.getStatus();
			if (status < 200 || status >= 300) {
				throw new IllegalStateException("Verification email provider returned HTTP " + status);
			}
		} finally {
			if (response != null) {
				response.close();
			}
			client.destroy();
		}
	}

	public static String verificationEmailContent(String email, String token) {
		String verifyUrl = withoutTrailingSlash(CLOUD_SERVER_ADDRESS)
				+ "/verifyEmail?email=" + urlEncode(email) + "&token=" + urlEncode(token);
		return "<!doctype html><html><body>"
				+ "<h1>Verify your LivoCloud account</h1>"
				+ "<p>Finish registering " + html(email) + " by using this one-time link. "
				+ "The link expires in 24 hours.</p>"
				+ "<p><a href=\"" + html(verifyUrl) + "\">Verify account</a></p>"
				+ "<p>If you did not request this account, ignore this message.</p>"
				+ "</body></html>";
	}

	private static String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("UTF-8 is unavailable", ex);
		}
	}

	private static String withoutTrailingSlash(String value) {
		return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
	}

	private static String html(String value) {
		return value.replace("&", "&amp;").replace("<", "&lt;")
				.replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
	}
}
