package com.abyeti.db;
import java.sql.Connection;
import java.sql.DriverManager;

public class PGDBConn {

    public static Connection dbConnection() throws Exception {

    	String DRIVER_NAME = "org.postgresql.Driver";
    	String URL = "jdbc:postgresql://localhost:5432/PGDemo";
    	String USERNAME = "postgres";
    	String PASSWORD = "654321";
    	Connection conn = null;
		
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
