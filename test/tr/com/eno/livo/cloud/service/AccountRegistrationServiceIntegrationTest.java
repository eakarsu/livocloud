package tr.com.eno.livo.cloud.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;

import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.security.PasswordHasher;
import tr.com.eno.livo.cloud.security.VerificationTokenGenerator;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.AccountAlreadyExistsException;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.InvalidVerificationTokenException;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.RegistrationRateLimitException;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.VerificationChallenge;
import tr.com.eno.livo.cloud.utility.AppConstants;

public class AccountRegistrationServiceIntegrationTest {

	private JdbcDataSource dataSource;
	private AccountRegistrationService service;

	@Before
	public void setUp() {
		dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
		dataSource.setUser("sa");
		Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").load().migrate();

		service = new AccountRegistrationService();
		service.setDataSource(dataSource);
		service.setPasswordHasher(new PasswordHasher());
		service.setTokenGenerator(new VerificationTokenGenerator());
	}

	@Test
	public void registrationPersistsHashesAndVerificationIsSingleUse() throws Exception {
		User started = service.startRegistration("  Ada   Lovelace ", "ADA@Example.COM");
		assertEquals("Ada Lovelace", started.getName());
		assertEquals("ada@example.com", started.getEmail());

		VerificationChallenge challenge = service.completeRegistration(
				started.getUserId(), "Ada Lovelace", "Analytical Engines", "a sufficiently long password");
		String storedPassword = value("SELECT user_password FROM users WHERE user_id=" + started.getUserId());
		String storedToken = value("SELECT token FROM pending_requests WHERE status=1");
		assertTrue(storedPassword.startsWith("pbkdf2_sha256$"));
		assertNotEquals("a sufficiently long password", storedPassword);
		assertEquals(64, storedToken.length());
		assertNotEquals(challenge.getToken(), storedToken);

		User verified = service.verify(challenge.getEmail(), challenge.getToken());
		assertEquals(AppConstants.UserState.VERIFIED_INACTIVE.value, verified.getAccountState());
		assertEquals("2", value("SELECT status FROM pending_requests"));
		assertEquals("3", value("SELECT COUNT(*) FROM account_audit"));

		try {
			service.verify(challenge.getEmail(), challenge.getToken());
			fail("A consumed verification token must not be reusable");
		} catch (InvalidVerificationTokenException expected) {
			// expected
		}
	}

	@Test
	public void duplicateEmailIsRejectedAfterNormalization() throws Exception {
		service.startRegistration("First User", "person@example.com");
		try {
			service.startRegistration("Second User", "PERSON@example.com");
			fail("Case variants must not create duplicate accounts");
		} catch (AccountAlreadyExistsException expected) {
			assertEquals("1", value("SELECT COUNT(*) FROM users"));
		}
	}

	@Test
	public void repeatedRegistrationAttemptsAreThrottledWithoutStoringRawIdentifiers() throws Exception {
		service.startRegistration("Rate Limited", "rate-limit@example.com", "198.51.100.9");
		for (int attempt = 0; attempt < 4; attempt++) {
			try {
				service.startRegistration("Rate Limited", "RATE-LIMIT@example.com", "198.51.100.9");
				fail("Duplicate registration should not succeed");
			} catch (AccountAlreadyExistsException expected) {
				// The attempt is retained for throttling even though no account is added.
			}
		}

		try {
			service.startRegistration("Rate Limited", "rate-limit@example.com", "198.51.100.9");
			fail("The sixth email attempt inside the window must be throttled");
		} catch (RegistrationRateLimitException expected) {
			assertEquals("5", value("SELECT COUNT(*) FROM registration_attempts"));
			String emailDigest = value("SELECT email_digest FROM registration_attempts LIMIT 1");
			assertEquals(64, emailDigest.length());
			assertFalse(emailDigest.contains("rate-limit@example.com"));
		}
	}

	@Test
	public void resendInvalidatesTheEarlierChallenge() throws Exception {
		User user = service.startRegistration("Grace Hopper", "grace@example.com");
		VerificationChallenge first = service.completeRegistration(
				user.getUserId(), user.getName(), "Navy", "another long password");
		VerificationChallenge second = service.resendVerification(user.getUserId());
		assertNotEquals(first.getToken(), second.getToken());

		try {
			service.verify(first.getEmail(), first.getToken());
			fail("The replaced challenge must be invalid");
		} catch (InvalidVerificationTokenException expected) {
			// expected
		}
		assertNotNull(service.verify(second.getEmail(), second.getToken()));
	}

	@Test
	public void expiredChallengeDoesNotChangeAccountState() throws Exception {
		User user = service.startRegistration("Katherine Johnson", "katherine@example.com");
		VerificationChallenge challenge = service.completeRegistration(
				user.getUserId(), user.getName(), "NASA", "orbital mechanics password");
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"UPDATE pending_requests SET token_expire_date=? WHERE user_id=?")) {
			statement.setTimestamp(1, new Timestamp(System.currentTimeMillis() - 1000L));
			statement.setInt(2, user.getUserId());
			statement.executeUpdate();
		}

		try {
			service.verify(challenge.getEmail(), challenge.getToken());
			fail("An expired challenge must fail");
		} catch (InvalidVerificationTokenException expected) {
			assertEquals(String.valueOf(AppConstants.UserState.UNVERIFIED.value),
					value("SELECT account_state FROM users WHERE user_id=" + user.getUserId()));
		}
	}

	@Test
	public void authenticationRequiresCorrectHashAndActiveState() throws Exception {
		User user = service.startRegistration("Margaret Hamilton", "margaret@example.com");
		VerificationChallenge challenge = service.completeRegistration(
				user.getUserId(), user.getName(), "MIT", "software engineering password");
		service.verify(challenge.getEmail(), challenge.getToken());

		assertNull(service.authenticate(user.getEmail(), "software engineering password",
				AppConstants.UserState.VERIFIED_ACTIVE.value));
		try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.executeUpdate("UPDATE users SET account_state=" + AppConstants.UserState.VERIFIED_ACTIVE.value);
		}
		assertNull(service.authenticate(user.getEmail(), "wrong password",
				AppConstants.UserState.VERIFIED_ACTIVE.value));
		assertNotNull(service.authenticate(user.getEmail(), "software engineering password",
				AppConstants.UserState.VERIFIED_ACTIVE.value));
	}

	private String value(String sql) throws Exception {
		try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			assertTrue(result.next());
			return result.getString(1);
		}
	}
}
