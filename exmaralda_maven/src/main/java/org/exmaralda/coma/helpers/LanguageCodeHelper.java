package org.exmaralda.coma.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import org.exmaralda.coma.resources.ResourceHandler;

public class LanguageCodeHelper {
	private static PreparedStatement preparedStatement;
	private static Connection connection;

	/**
	 * 
	 */

	/**
	 * @param args
	 */
	static {
		try {
			connection = getConnection();
			checkExist(connection);
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Connection getConnection() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Properties properties = new Properties();
			properties.put("user", "exmaralda");
			properties.put("password", "exmaralda");
			connection = DriverManager.getConnection(
					"jdbc:derby:exmaralda/languagecodes;create=true",
					properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static String getLanguageCode(String langName) {
		return "FU!";
	}

	public static Vector<String> getLanguageNames() {
		connection = getConnection();
		Statement statement;
		Vector<String> names = new Vector<String>();
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM LANGUAGECODES");// WHERE
			// REF_NAME
			// =
			// '%"+substr+"%'");
			while (resultSet.next()) {
				names.add(resultSet.getString(7));
			}
			return names;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return names;
	}

	private static void checkExist(Connection connection) throws SQLException {
		ResultSet resultSet = connection.getMetaData().getTables("%", "%", "%",
				new String[] { "TABLE" });
		int columnCnt = resultSet.getMetaData().getColumnCount();
		boolean shouldCreateTable = true;
		while (resultSet.next() && shouldCreateTable) {
			if (resultSet.getString("TABLE_NAME").equalsIgnoreCase(
					"LANGUAGECODES")) {
				shouldCreateTable = false;
			}
		}
		resultSet.close();
		if (shouldCreateTable) {
			Statement statement = connection.createStatement();
			String createString = "CREATE TABLE LANGUAGECODES"
					+ "(ID VARCHAR(3) NOT NULL, PART2B VARCHAR(3),  "
					+ "PART2T VARCHAR(3), PART1 VARCHAR(2), "
					+ "SCOPE VARCHAR(1) NOT NULL, "
					+ "TYPE VARCHAR(1) NOT NULL, "
					+ "REF_NAME VARCHAR(150) NOT NULL, "
					+ "COMMENT VARCHAR(150))";
			statement.execute(createString);
			statement.close();
			boolean shouldPopulateTable = true;
			System.out.println("Populating Language Code table...");
			try {
				BufferedReader input = new BufferedReader(
						new InputStreamReader(new ResourceHandler()
								.languageCodes()));
				String line = input.readLine(); // first line contains table
				// headers
				while ((line = input.readLine()) != null) {
					String[] cols = line.split("\\t");
					Vector<String> colVector = new Vector<String>(Arrays
							.asList(cols));
					if (colVector.size() == 7)
						colVector.add("");
					preparedStatement = connection
							.prepareStatement("INSERT INTO languagecodes VALUES (?,?,?,?,?,?,?,?)");
					int col = 0;
					for (String s : colVector) {
						col++;
						preparedStatement.setString(col, s);
					}
					preparedStatement.execute();
				}
				preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
				// egal!
			}

		}
	}
}
