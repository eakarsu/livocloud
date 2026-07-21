package tr.com.eno.livo.cloud.security;

import java.util.Locale;
import java.util.regex.Pattern;

public final class AccountInputValidator {

	private static final Pattern EMAIL = Pattern.compile(
			"^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$",
			Pattern.CASE_INSENSITIVE);

	private AccountInputValidator() {
	}

	public static String email(String value) {
		String normalized = text(value, "Email", 254).toLowerCase(Locale.ROOT);
		if (!EMAIL.matcher(normalized).matches()) {
			throw new IllegalArgumentException("Enter a valid email address");
		}
		return normalized;
	}

	public static String name(String value) {
		return text(value, "Name", 120);
	}

	public static String company(String value) {
		return text(value, "Company name", 160);
	}

	public static String password(String value) {
		if (value == null || value.length() < 12 || value.length() > 128) {
			throw new IllegalArgumentException("Password must be between 12 and 128 characters");
		}
		return value;
	}

	private static String text(String value, String label, int maxLength) {
		String normalized = value == null ? "" : value.trim().replaceAll("\\s+", " ");
		if (normalized.isEmpty() || normalized.length() > maxLength) {
			throw new IllegalArgumentException(label + " is required and must be at most " + maxLength + " characters");
		}
		for (int index = 0; index < normalized.length(); index++) {
			if (Character.isISOControl(normalized.charAt(index))) {
				throw new IllegalArgumentException(label + " contains unsupported characters");
			}
		}
		return normalized;
	}
}
