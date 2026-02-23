import java.sql.*;

/**
 * Demonstrates IDS00-J: Prevent SQL injection.
 * 
 * @author Nathan Hilbert
 */
public class AuthService {
	// Class level constants
	private static final String INSERT_QUERY = "INSERT INTO users VALUES (?,?)";
	private static final String SELECT_QUERY = "SELECT * FROM users WHERE username=? AND password=?";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "secret_password";

	// Instance level constants
	private final Connection connection;

	/**
	 * Initializes an h2 database connection, creates user table, then inserts a
	 * test user.
	 * 
	 * @throws SQLException if the database fails to be initialized.
	 */
	public AuthService() throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:h2:mem:test");

		try (Statement statement = this.connection.createStatement()) {
			statement.execute("CREATE TABLE users (username VARCHAR, password VARCHAR)");

			try (PreparedStatement insertUserStatement = this.connection.prepareStatement(INSERT_QUERY)) {
				insertUserStatement.setString(1, USERNAME);
				insertUserStatement.setString(2, PASSWORD);
				insertUserStatement.execute();
			}
		}
	}

	/**
	 * Uses {@value #SELECT_QUERY} to prepare a statement.
	 * A {@link PreparedStatement} ensures the database sees username and password
	 * as literal strings, and not as a part of the SQL query.
	 * 
	 * @param username is placed at the first '?' in the statement.
	 * @param password is placed at the second '?' in the statement.
	 * @throws SQLException      if a database error occurs.
	 * @throws SecurityException if there are no results from the user query.
	 */
	private void authenticateUser(String username, String password) throws SQLException, SecurityException {
		try (PreparedStatement statement = this.connection.prepareStatement(SELECT_QUERY)) {
			statement.setString(1, username);
			statement.setString(2, password);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new SecurityException("User name or password is incorrect.");
				}
			}
		}
	}

	/** Runs test cases. */
	public static void main(String[] args) {
		AuthService authService;
		try {
			authService = new AuthService();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}

		try {
			String sqlInjection = "' OR '1'='1";
			System.out.println("Try injecting SQL: {username=\"" + sqlInjection + "\", password=\"doesNotMatter\"}");
			authService.authenticateUser(sqlInjection, "couldBeWhatever");
			System.out.println("SUCCESS: User authenticated");
		} catch (Exception e) {
			System.out.println("FAILED: " + e.getMessage());
		}

		try {
			System.out.println("\nTry test user: {username=\"" + USERNAME + "\", password=\"" + PASSWORD + "\"}");
			authService.authenticateUser(USERNAME, PASSWORD);
			System.out.println("SUCCESS: User authenticated");
		} catch (Exception e) {
			System.err.println("FAILED: " + e.getMessage());
		}
	}
}