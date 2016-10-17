package tk.khumps.ps2census;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class DBConnect {
	public Connection con;

	public DBConnect(String host, String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(host, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public Statement createStatement() {
		try {
			return con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to create statement");
			return null;
		}
	}

	public ResultSet query(Statement s, String query) {
		try {
			return s.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("Failed to execute");
			return null;
		}

	}

	public void printQuery(ResultSet rs) {
		ResultSetMetaData rsmd = null;
		try {
			rsmd = rs.getMetaData();

			if (rsmd != null)
				System.out.println(Arrays.toString(getHeader(rs)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String[]> table = getResults(rs);
		for (String[] s : table) {
			System.out.println(Arrays.toString(s));
		}
	}

	public String[] getHeader(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			String[] headers = new String[rsmd.getColumnCount()];
			if (rsmd != null)
				for (int i = 1; i <= headers.length; i++) {
					System.out.println("i: " + (i - 1));
					headers[i - 1] = rsmd.getColumnName(i);
				}
			return headers;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new String[1];
	}

	public ArrayList<String[]> getResults(ResultSet rs) {
		ArrayList<String[]> table = new ArrayList<String[]>();
		try {
			ResultSetMetaData rsmd;
			while (rs.next()) {
				rsmd = rs.getMetaData();
				String[] row = new String[rsmd.getColumnCount()];
				for (int i = 1; i <= row.length; i++) {
					row[i - 1] = rs.getString(i);
				}

				table.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return table;
	}

	public static String[][] toArray(ArrayList<String[]> list) {
		if (!list.isEmpty()) {
			String[][] array = new String[list.size()][list.get(0).length];
			for (int i = 0; i < list.size(); i++) {
				array[i] = list.get(i);
			}
			return array;
		}
		return new String[1][1];
	}

	public static void main(String[] args) {
		DBConnect c = new DBConnect("jdbc:mysql://ts.khumps.tk:3306", "query_user", "planetside");
		Statement s = c.createStatement();
		ResultSet set = c.query(s, "SELECT * FROM ps2_census.gainexperience limit 0,10;");
		c.printQuery(set);

	}
}
