package DataMiningProject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnection {

	private static final String username = "nalinkum";
	private static final String password = "cse601";
	public static Connection conn;

	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:" + username + "/" + password + "@aos.acsu.buffalo.edu:1521/aos.buffalo.edu");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("ClassNotFoundException while loading JDBC driver:");
			System.exit(0);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException while initializing JDBC connection:");
			System.exit(0);
		}
	}
}
