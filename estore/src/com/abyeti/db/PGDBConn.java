package com.abyeti.db;
import java.sql.Connection;
import java.sql.DriverManager;

public class PGDBConn {
	
	public static String DRIVER_NAME = "org.postgresql.Driver";
	public static String URL = "jdbc:postgresql://localhost:5432/estoredb";
	public static String USERNAME = "postgres";
	public static String PASSWORD = "654321";
	public static Connection conn = null;

    public static Connection dbConnection() throws Exception {
		
		try{
		    Class.forName(DRIVER_NAME);
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
			if(conn != null)
				return conn;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
