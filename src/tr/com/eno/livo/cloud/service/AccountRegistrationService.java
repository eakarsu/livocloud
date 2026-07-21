package tr.com.eno.livo.cloud.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.sql.DataSource;

import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.security.AccountInputValidator;
import tr.com.eno.livo.cloud.security.PasswordHasher;
import tr.com.eno.livo.cloud.security.VerificationTokenGenerator;
import tr.com.eno.livo.cloud.utility.AppConstants;

/**
 * Transaction boundary for the persisted account-registration journey.
 * Raw passwords and raw verification tokens never cross into database storage.
 */
public class AccountRegistrationService {

	private static final long VERIFICATION_TTL_MILLIS = 24L * 60L * 60L * 1000L;
	private static final long REGISTRATION_WINDOW_MILLIS = 15L * 60L * 1000L;
	private static final int EMAIL_ATTEMPT_LIMIT = 5;
	private static final int CLIENT_ATTEMPT_LIMIT = 20;

	private DataSource dataSource;
	private PasswordHasher passwordHasher;
	private VerificationTokenGenerator tokenGenerator;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setPasswordHasher(PasswordHasher passwordHasher) {
		this.passwordHasher = passwordHasher;
	}

	public void setTokenGenerator(VerificationTokenGenerator tokenGenerator) {
		this.tokenGenerator = tokenGenerator;
	}

	public User startRegistration(String submittedName, String submittedEmail)
			throws SQLException, AccountAlreadyExistsException, RegistrationRateLimitException {
		return startRegistration(submittedName, submittedEmail, "unspecified-client");
	}

	public User startRegistration(String submittedName, String submittedEmail, String clientIdentity)
			throws SQLException, AccountAlreadyExistsException, RegistrationRateLimitException {
		ensureConfigured();
		String name = AccountInputValidator.name(submittedName);
		String email = AccountInputValidator.email(submittedEmail);

		try (Connection connection = dataSource.getConnection()) {
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.setAutoCommit(false);
			try {
				recordRegistrationAttempt(connection, email, clientIdentity);
				if (findUserIdByEmail(connection, email) != 0L) {
					throw new AccountAlreadyExistsException();
				}
				long userId;
				String sql = "INSERT INTO users(name, email, account_state, account_type) VALUES (?,?,?,?)";
				try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
					statement.setString(1, name);
					statement.setString(2, email);
					statement.setInt(3, AppConstants.UserState.SIGNIN.value);
					statement.setString(4, "developer");
					statement.executeUpdate();
					try (ResultSet keys = statement.getGeneratedKeys()) {
						if (!keys.next()) {
							throw new SQLException("Database did not return the registered user identifier");
						}
						userId = keys.getLong(1);
					}
				}
				audit(connection, userId, "REGISTRATION_STARTED");
				connection.commit();

				User user = new User();
				user.setUserId(toInt(userId));
				user.setName(name);
				user.setEmail(email);
				user.setAccountState(AppConstants.UserState.SIGNIN.value);
				user.setAccountType("developer");
				return user;
			} catch (AccountAlreadyExistsException ex) {
				connection.commit();
				throw ex;
			} catch (RegistrationRateLimitException ex) {
				rollback(connection);
				throw ex;
			} catch (SQLException ex) {
				rollback(connection);
				if ("23505".equals(ex.getSQLState())) {
					throw new AccountAlreadyExistsException();
				}
				throw ex;
			} catch (RuntimeException ex) {
				rollback(connection);
				throw ex;
			}
		}
	}

	public VerificationChallenge completeRegistration(int userId, String submittedName,
			String submittedCompany, String submittedPassword) throws SQLException {
		ensureConfigured();
		String name = AccountInputValidator.name(submittedName);
		String company = AccountInputValidator.company(submittedCompany);
		String password = AccountInputValidator.password(submittedPassword);

		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				User user = lockUser(connection, userId);
				if (user == null || (user.getAccountState() != AppConstants.UserState.SIGNIN.value
						&& user.getAccountState() != AppConstants.UserState.UNVERIFIED.value)) {
					throw new IllegalStateException("Registration cannot be completed in the current account state");
				}

				String update = "UPDATE users SET name=?, company_name=?, user_password=?, account_state=?, updated_at=CURRENT_TIMESTAMP WHERE user_id=?";
				try (PreparedStatement statement = connection.prepareStatement(update)) {
					statement.setString(1, name);
					statement.setString(2, company);
					statement.setString(3, passwordHasher.hash(password));
					statement.setInt(4, AppConstants.UserState.UNVERIFIED.value);
					statement.setInt(5, userId);
					if (statement.executeUpdate() != 1) {
						throw new SQLException("Registration profile update did not affect exactly one account");
					}
				}

				VerificationChallenge challenge = issueChallenge(connection, userId, user.getEmail());
				audit(connection, userId, "VERIFICATION_ISSUED");
				connection.commit();
				return challenge;
			} catch (SQLException | RuntimeException ex) {
				rollback(connection);
				throw ex;
			}
		}
	}

	public VerificationChallenge resendVerification(int userId) throws SQLException {
		ensureConfigured();
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				User user = lockUser(connection, userId);
				if (user == null || user.getAccountState() != AppConstants.UserState.UNVERIFIED.value) {
					throw new IllegalStateException("Only an unverified account can request another link");
				}
				VerificationChallenge challenge = issueChallenge(connection, userId, user.getEmail());
				audit(connection, userId, "VERIFICATION_REISSUED");
				connection.commit();
				return challenge;
			} catch (SQLException | RuntimeException ex) {
				rollback(connection);
				throw ex;
			}
		}
	}

	public User verify(String submittedEmail, String rawToken)
			throws SQLException, InvalidVerificationTokenException {
		ensureConfigured();
		String email = AccountInputValidator.email(submittedEmail);
		String tokenDigest;
		try {
			tokenDigest = tokenGenerator.digest(rawToken);
		} catch (IllegalArgumentException ex) {
			throw new InvalidVerificationTokenException();
		}

		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				long requestId = 0L;
				int userId = 0;
				String select = "SELECT p.req_id, p.user_id FROM pending_requests p "
						+ "JOIN users u ON u.user_id=p.user_id "
						+ "WHERE p.email=? AND p.token=? AND p.status=1 AND p.token_expire_date>? "
						+ "AND u.account_state=? ORDER BY p.req_id DESC LIMIT 1 FOR UPDATE";
				try (PreparedStatement statement = connection.prepareStatement(select)) {
					statement.setString(1, email);
					statement.setString(2, tokenDigest);
					statement.setTimestamp(3, new Timestamp(currentTimeMillis()));
					statement.setInt(4, AppConstants.UserState.UNVERIFIED.value);
					try (ResultSet result = statement.executeQuery()) {
						if (result.next()) {
							requestId = result.getLong("req_id");
							userId = result.getInt("user_id");
						}
					}
				}
				if (requestId == 0L) {
					throw new InvalidVerificationTokenException();
				}

				try (PreparedStatement statement = connection.prepareStatement(
						"UPDATE users SET account_state=?, updated_at=CURRENT_TIMESTAMP WHERE user_id=? AND account_state=?")) {
					statement.setInt(1, AppConstants.UserState.VERIFIED_INACTIVE.value);
					statement.setInt(2, userId);
					statement.setInt(3, AppConstants.UserState.UNVERIFIED.value);
					if (statement.executeUpdate() != 1) {
						throw new InvalidVerificationTokenException();
					}
				}
				try (PreparedStatement statement = connection.prepareStatement(
						"UPDATE pending_requests SET status=2, consumed_at=CURRENT_TIMESTAMP WHERE req_id=? AND status=1")) {
					statement.setLong(1, requestId);
					if (statement.executeUpdate() != 1) {
						throw new InvalidVerificationTokenException();
					}
				}

				audit(connection, userId, "EMAIL_VERIFIED");
				User verified = lockUser(connection, userId);
				connection.commit();
				return verified;
			} catch (InvalidVerificationTokenException ex) {
				rollback(connection);
				throw ex;
			} catch (SQLException | RuntimeException ex) {
				rollback(connection);
				throw ex;
			}
		}
	}

	public User authenticate(String submittedEmail, String submittedPassword, int requiredState) throws SQLException {
		ensureConfigured();
		String email = AccountInputValidator.email(submittedEmail);
		if (submittedPassword == null || submittedPassword.length() > 128) {
			return null;
		}
		String sql = "SELECT * FROM users WHERE email=? AND account_state=? ORDER BY user_id DESC LIMIT 1";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, email);
			statement.setInt(2, requiredState);
			try (ResultSet result = statement.executeQuery()) {
				if (!result.next() || !passwordHasher.matches(submittedPassword, result.getString("user_password"))) {
					return null;
				}
				return mapUser(result);
			}
		}
	}

	private VerificationChallenge issueChallenge(Connection connection, int userId, String email) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(
				"UPDATE pending_requests SET status=0 WHERE user_id=? AND status=1")) {
			statement.setInt(1, userId);
			statement.executeUpdate();
		}

		String rawToken = tokenGenerator.create();
		Timestamp expiresAt = new Timestamp(currentTimeMillis() + VERIFICATION_TTL_MILLIS);
		String insert = "INSERT INTO pending_requests(user_id,email,token,status,token_expire_date) VALUES (?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(insert)) {
			statement.setInt(1, userId);
			statement.setString(2, email);
			statement.setString(3, tokenGenerator.digest(rawToken));
			statement.setInt(4, 1);
			statement.setTimestamp(5, expiresAt);
			statement.executeUpdate();
		}
		return new VerificationChallenge(email, rawToken, expiresAt);
	}

	private User lockUser(Connection connection, int userId) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(
				"SELECT * FROM users WHERE user_id=? FOR UPDATE")) {
			statement.setInt(1, userId);
			try (ResultSet result = statement.executeQuery()) {
				return result.next() ? mapUser(result) : null;
			}
		}
	}

	private long findUserIdByEmail(Connection connection, String email) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM users WHERE email=?")) {
			statement.setString(1, email);
			try (ResultSet result = statement.executeQuery()) {
				return result.next() ? result.getLong(1) : 0L;
			}
		}
	}

	private User mapUser(ResultSet result) throws SQLException {
		User user = new User();
		user.setUserId(result.getInt("user_id"));
		user.setName(result.getString("name"));
		user.setEmail(result.getString("email"));
		user.setAccountState(result.getInt("account_state"));
		user.setAccountType(result.getString("account_type"));
		user.setCompanyName(result.getString("company_name"));
		user.setAddressCountry(result.getString("address_country"));
		user.setBillingAddress(result.getString("billing_address"));
		user.setTaxNumber(result.getString("tax_number"));
		return user;
	}

	private void audit(Connection connection, long userId, String eventType) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO account_audit(user_id,event_type) VALUES (?,?)")) {
			statement.setLong(1, userId);
			statement.setString(2, eventType);
			statement.executeUpdate();
		}
	}

	private void recordRegistrationAttempt(Connection connection, String email, String clientIdentity)
			throws SQLException, RegistrationRateLimitException {
		String emailDigest = opaqueDigest(email);
		String normalizedClient = clientIdentity == null ? "unknown" : clientIdentity.trim();
		if (normalizedClient.isEmpty() || normalizedClient.length() > 255) {
			normalizedClient = "unknown";
		}
		String clientDigest = opaqueDigest(normalizedClient);
		Timestamp windowStart = new Timestamp(currentTimeMillis() - REGISTRATION_WINDOW_MILLIS);
		if (attemptCount(connection, "email_digest", emailDigest, windowStart) >= EMAIL_ATTEMPT_LIMIT
				|| attemptCount(connection, "client_digest", clientDigest, windowStart) >= CLIENT_ATTEMPT_LIMIT) {
			throw new RegistrationRateLimitException();
		}
		try (PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO registration_attempts(email_digest,client_digest,attempted_at) VALUES (?,?,?)")) {
			statement.setString(1, emailDigest);
			statement.setString(2, clientDigest);
			statement.setTimestamp(3, new Timestamp(currentTimeMillis()));
			statement.executeUpdate();
		}
	}

	private int attemptCount(Connection connection, String column, String digest, Timestamp windowStart)
			throws SQLException {
		String sql = "SELECT COUNT(*) FROM registration_attempts WHERE " + column + "=? AND attempted_at>=?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, digest);
			statement.setTimestamp(2, windowStart);
			try (ResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
	}

	private String opaqueDigest(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder(64);
			for (byte item : digest) {
				hex.append(String.format("%02x", item & 0xff));
			}
			return hex.toString();
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 is unavailable", ex);
		}
	}

	private void ensureConfigured() {
		if (dataSource == null || passwordHasher == null || tokenGenerator == null) {
			throw new IllegalStateException("Account registration service is not fully configured");
		}
	}

	protected long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	private int toInt(long value) throws SQLException {
		if (value > Integer.MAX_VALUE) {
			throw new SQLException("User identifier exceeds the supported range");
		}
		return (int) value;
	}

	private void rollback(Connection connection) {
		try {
			connection.rollback();
		} catch (SQLException ignored) {
			// Preserve the original failure; the connection will be closed immediately.
		}
	}

	public static final class VerificationChallenge {
		private final String email;
		private final String token;
		private final Timestamp expiresAt;

		VerificationChallenge(String email, String token, Timestamp expiresAt) {
			this.email = email;
			this.token = token;
			this.expiresAt = new Timestamp(expiresAt.getTime());
		}

		public String getEmail() {
			return email;
		}

		public String getToken() {
			return token;
		}

		public Timestamp getExpiresAt() {
			return new Timestamp(expiresAt.getTime());
		}
	}

	public static class AccountAlreadyExistsException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static class InvalidVerificationTokenException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public static class RegistrationRateLimitException extends Exception {
		private static final long serialVersionUID = 1L;
	}
}
