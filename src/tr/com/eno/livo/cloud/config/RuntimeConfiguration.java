package tr.com.eno.livo.cloud.config;

/**
 * Reads deployment configuration without placing secrets in source control.
 * Java system properties take precedence so tests and servlet containers can
 * override environment variables without mutating the process environment.
 */
public final class RuntimeConfiguration {

	private RuntimeConfiguration() {
	}

	public static String get(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (isBlank(value)) {
			value = System.getenv(name);
		}
		return isBlank(value) ? defaultValue : value.trim();
	}

	public static String required(String name) {
		String value = get(name, null);
		if (isBlank(value)) {
			throw new IllegalStateException("Required configuration is missing: " + name);
		}
		return value;
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
