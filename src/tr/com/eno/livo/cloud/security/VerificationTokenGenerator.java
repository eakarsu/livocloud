package tr.com.eno.livo.cloud.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/** Generates capability tokens and one-way digests suitable for persistence. */
public class VerificationTokenGenerator {

	private static final int TOKEN_BYTES = 32;
	private final SecureRandom secureRandom;

	public VerificationTokenGenerator() {
		this(new SecureRandom());
	}

	VerificationTokenGenerator(SecureRandom secureRandom) {
		this.secureRandom = secureRandom;
	}

	public String create() {
		byte[] token = new byte[TOKEN_BYTES];
		secureRandom.nextBytes(token);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
	}

	public String digest(String token) {
		if (token == null || token.length() < 32 || token.length() > 128) {
			throw new IllegalArgumentException("Verification token is malformed");
		}
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
					.digest(token.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder(digest.length * 2);
			for (byte value : digest) {
				hex.append(String.format("%02x", value & 0xff));
			}
			return hex.toString();
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 is unavailable", ex);
		}
	}
}
