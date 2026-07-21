package tr.com.eno.livo.cloud.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 password storage with a unique random salt for every password. */
public class PasswordHasher {

	private static final String PREFIX = "pbkdf2_sha256";
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 210000;
	private static final int SALT_BYTES = 16;
	private static final int KEY_BITS = 256;

	private final SecureRandom secureRandom;

	public PasswordHasher() {
		this(new SecureRandom());
	}

	PasswordHasher(SecureRandom secureRandom) {
		this.secureRandom = secureRandom;
	}

	public String hash(String password) {
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password is required");
		}
		byte[] salt = new byte[SALT_BYTES];
		secureRandom.nextBytes(salt);
		byte[] derived = derive(password, salt, ITERATIONS);
		return PREFIX + "$" + ITERATIONS + "$"
				+ Base64.getUrlEncoder().withoutPadding().encodeToString(salt) + "$"
				+ Base64.getUrlEncoder().withoutPadding().encodeToString(derived);
	}

	public boolean matches(String password, String encoded) {
		if (password == null || encoded == null) {
			return false;
		}
		String[] parts = encoded.split("\\$", -1);
		if (parts.length != 4 || !PREFIX.equals(parts[0])) {
			return false;
		}
		try {
			int iterations = Integer.parseInt(parts[1]);
			if (iterations < 100000 || iterations > 1000000) {
				return false;
			}
			byte[] salt = Base64.getUrlDecoder().decode(parts[2]);
			byte[] expected = Base64.getUrlDecoder().decode(parts[3]);
			byte[] actual = derive(password, salt, iterations);
			return MessageDigest.isEqual(expected, actual);
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	public boolean isEncoded(String value) {
		return value != null && value.startsWith(PREFIX + "$");
	}

	private byte[] derive(String password, byte[] salt, int iterations) {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
		try {
			return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException(ALGORITHM + " is unavailable", ex);
		} catch (InvalidKeySpecException ex) {
			throw new IllegalStateException("Unable to derive password hash", ex);
		} finally {
			spec.clearPassword();
		}
	}
}
