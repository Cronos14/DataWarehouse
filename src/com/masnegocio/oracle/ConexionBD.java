package com.masnegocio.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

	public static String host = "locahost";
	public static String puerto = "1521";
	public static String esquema = "xe";
	public static String usuario = "system";
	public static String password = "root";
	
	public static Connection getConnection(){
		
		

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return null;

		}

	
		Connection connection = null;

		try {
			//String url = "jdbc:oracle:thin:@"+host+":"+puerto+":"+esquema;
			//String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String url = "jdbc:oracle:thin:"+usuario+"/"+password+"@"+host+":"+puerto+":"+esquema;
			
			connection = DriverManager.getConnection(
					url);

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;

		}

		if (connection != null) {
			return connection;
		} else {
			System.out.println("Failed to make connection!");
			return null;
		}
	}
	
}
