package tr.com.eno.livo.cloud.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordHasherTest {

	private final PasswordHasher hasher = new PasswordHasher();

	@Test
	public void hashesUseUniqueSaltsAndMatchOnlyTheOriginalPassword() {
		String first = hasher.hash("correct horse battery staple");
		String second = hasher.hash("correct horse battery staple");

		assertNotEquals(first, second);
		assertTrue(hasher.matches("correct horse battery staple", first));
		assertFalse(hasher.matches("incorrect", first));
		assertFalse(hasher.matches("correct horse battery staple", "plaintext"));
	}
}
