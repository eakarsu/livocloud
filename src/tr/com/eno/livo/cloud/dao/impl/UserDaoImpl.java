package tr.com.eno.livo.cloud.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import tr.com.eno.livo.cloud.dao.UserDao;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.security.AccountInputValidator;
import tr.com.eno.livo.cloud.security.PasswordHasher;
import tr.com.eno.livo.cloud.utility.AppConstants;

/** Legacy DAO retained for callers outside the transactional registration service. */
public class UserDaoImpl implements UserDao {

	private DataSource dataSource;
	private PasswordHasher passwordHasher;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setPasswordHasher(PasswordHasher passwordHasher) {
		this.passwordHasher = passwordHasher;
	}

	@Override
	public User getUserByEmail(String userMail) throws SQLException {
		String email = AccountInputValidator.email(userMail);
		String query = "SELECT * FROM users WHERE email=? ORDER BY user_id DESC LIMIT 1";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, email);
			try (ResultSet result = statement.executeQuery()) {
				return result.next() ? mapUser(result) : emptyUser();
			}
		}
	}

	@Override
	public User isValidUser(String emailValue, String password, int status) throws SQLException {
		String email = AccountInputValidator.email(emailValue);
		String query = "SELECT * FROM users WHERE email=? AND account_state=? ORDER BY user_id DESC LIMIT 1";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, email);
			statement.setInt(2, status);
			try (ResultSet result = statement.executeQuery()) {
				if (!result.next() || passwordHasher == null
						|| !passwordHasher.matches(password, result.getString("user_password"))) {
					return emptyUser();
				}
				return mapUser(result);
			}
		}
	}

	@Override
	public boolean insertUser(String nameValue, String emailValue) throws SQLException {
		String name = AccountInputValidator.name(nameValue);
		String email = AccountInputValidator.email(emailValue);
		String insert = "INSERT INTO users(name,email,account_state,account_type) VALUES (?,?,?,?)";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert)) {
			statement.setString(1, name);
			statement.setString(2, email);
			statement.setInt(3, AppConstants.UserState.SIGNIN.value);
			statement.setString(4, "developer");
			return statement.executeUpdate() == 1;
		}
	}

	@Override
	public boolean updateUser(User user) throws SQLException {
		if (passwordHasher == null) {
			throw new IllegalStateException("Password hasher is not configured");
		}
		String password = user.getUserPassword();
		String encoded = passwordHasher.isEncoded(password)
				? password : passwordHasher.hash(AccountInputValidator.password(password));
		String update = "UPDATE users SET name=?,user_password=?,account_state=?,company_name=?,"
				+ "address_country=?,billing_address=?,tax_number=?,updated_at=CURRENT_TIMESTAMP WHERE user_id=?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setString(1, AccountInputValidator.name(user.getName()));
			statement.setString(2, encoded);
			statement.setInt(3, user.getAccountState());
			statement.setString(4, user.getCompanyName());
			statement.setString(5, user.getAddressCountry());
			statement.setString(6, user.getBillingAddress());
			statement.setString(7, user.getTaxNumber());
			statement.setInt(8, user.getUserId());
			return statement.executeUpdate() == 1;
		}
	}

	@Override
	public boolean updateUserStateByEmail(String emailValue, int state) throws SQLException {
		String email = AccountInputValidator.email(emailValue);
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"UPDATE users SET account_state=?,updated_at=CURRENT_TIMESTAMP WHERE email=?")) {
			statement.setInt(1, state);
			statement.setString(2, email);
			return statement.executeUpdate() == 1;
		}
	}

	private User mapUser(ResultSet result) throws SQLException {
		User user = new User();
		user.setUserId(result.getInt("user_id"));
		user.setEmail(result.getString("email"));
		user.setAccountType(result.getString("account_type"));
		user.setCompanyName(result.getString("company_name"));
		user.setAddressCountry(result.getString("address_country"));
		user.setBillingAddress(result.getString("billing_address"));
		user.setTaxNumber(result.getString("tax_number"));
		user.setName(result.getString("name"));
		user.setAccountState(result.getInt("account_state"));
		return user;
	}

	private User emptyUser() {
		User user = new User();
		user.setUserId(0);
		return user;
	}
}
