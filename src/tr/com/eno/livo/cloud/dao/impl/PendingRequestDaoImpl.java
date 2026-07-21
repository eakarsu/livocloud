package tr.com.eno.livo.cloud.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import tr.com.eno.livo.cloud.dao.PendingRequestDao;
import tr.com.eno.livo.cloud.security.AccountInputValidator;
import tr.com.eno.livo.cloud.security.VerificationTokenGenerator;

/** Legacy token DAO; raw verification capabilities are never persisted. */
public class PendingRequestDaoImpl implements PendingRequestDao {

	private static final long TOKEN_TTL_MILLIS = 24L * 60L * 60L * 1000L;

	private DataSource dataSource;
	private VerificationTokenGenerator tokenGenerator;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setTokenGenerator(VerificationTokenGenerator tokenGenerator) {
		this.tokenGenerator = tokenGenerator;
	}

	@Override
	public boolean insertToken(String emailValue, String rawToken) throws SQLException {
		String email = AccountInputValidator.email(emailValue);
		String digest = tokenGenerator.digest(rawToken);
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				long userId = findUserId(connection, email);
				if (userId == 0L) {
					connection.rollback();
					return false;
				}
				try (PreparedStatement statement = connection.prepareStatement(
						"UPDATE pending_requests SET status=0 WHERE user_id=? AND status=1")) {
					statement.setLong(1, userId);
					statement.executeUpdate();
				}
				try (PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO pending_requests(user_id,email,token,status,token_expire_date) VALUES (?,?,?,?,?)")) {
					statement.setLong(1, userId);
					statement.setString(2, email);
					statement.setString(3, digest);
					statement.setInt(4, 1);
					statement.setTimestamp(5, new Timestamp(System.currentTimeMillis() + TOKEN_TTL_MILLIS));
					if (statement.executeUpdate() != 1) {
						connection.rollback();
						return false;
					}
				}
				connection.commit();
				return true;
			} catch (SQLException | RuntimeException ex) {
				connection.rollback();
				throw ex;
			}
		}
	}

	@Override
	public boolean isValidToken(String emailValue, String rawToken) throws SQLException {
		String email = AccountInputValidator.email(emailValue);
		String digest;
		try {
			digest = tokenGenerator.digest(rawToken);
		} catch (IllegalArgumentException ex) {
			return false;
		}
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				long requestId = 0L;
				String select = "SELECT req_id FROM pending_requests WHERE email=? AND token=? AND status=1 "
						+ "AND token_expire_date>? ORDER BY req_id DESC LIMIT 1 FOR UPDATE";
				try (PreparedStatement statement = connection.prepareStatement(select)) {
					statement.setString(1, email);
					statement.setString(2, digest);
					statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					try (ResultSet result = statement.executeQuery()) {
						if (result.next()) {
							requestId = result.getLong(1);
						}
					}
				}
				if (requestId == 0L) {
					connection.rollback();
					return false;
				}
				try (PreparedStatement statement = connection.prepareStatement(
						"UPDATE pending_requests SET status=2,consumed_at=CURRENT_TIMESTAMP WHERE req_id=? AND status=1")) {
					statement.setLong(1, requestId);
					if (statement.executeUpdate() != 1) {
						connection.rollback();
						return false;
					}
				}
				connection.commit();
				return true;
			} catch (SQLException | RuntimeException ex) {
				connection.rollback();
				throw ex;
			}
		}
	}

	@Override
	public boolean updateTokenStatus(String emailValue, String rawToken, int status) throws SQLException {
		String email = AccountInputValidator.email(emailValue);
		String digest = tokenGenerator.digest(rawToken);
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"UPDATE pending_requests SET status=?,consumed_at=CASE WHEN ?=2 THEN CURRENT_TIMESTAMP ELSE consumed_at END "
								+ "WHERE email=? AND token=? AND status=1")) {
			statement.setInt(1, status);
			statement.setInt(2, status);
			statement.setString(3, email);
			statement.setString(4, digest);
			return statement.executeUpdate() == 1;
		}
	}

	@Override
	public boolean deleteToken(String emailValue, String rawToken) throws SQLException {
		String email = AccountInputValidator.email(emailValue);
		String digest = tokenGenerator.digest(rawToken);
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"DELETE FROM pending_requests WHERE email=? AND token=?")) {
			statement.setString(1, email);
			statement.setString(2, digest);
			return statement.executeUpdate() == 1;
		}
	}

	private long findUserId(Connection connection, String email) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM users WHERE email=?")) {
			statement.setString(1, email);
			try (ResultSet result = statement.executeQuery()) {
				return result.next() ? result.getLong(1) : 0L;
			}
		}
	}
}
