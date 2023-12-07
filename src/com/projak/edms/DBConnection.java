package com.projak.edms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * @author Bharathkumar Reddy Chitteti - Projak Infotech Private Limited, Mumbai
 * @version 1.0.0
 */

public class DBConnection {
	
	final static ResourceBundle rsbundle = ResourceData.getResourceBundle();
	
	public Connection getConnection(String dbUrl, String dbUsername, String dbPassword) throws IOException {
		
		PropertyConfigurator.configure("D:\\EDMS\\ErrorDescription\\configurations\\log4j.properties");
		final Logger logger = Logger.getLogger(DBConnection.class);
		
		String driverClassName = rsbundle.getString("driverClassName");
		Connection connection = null;
		logger.info("Trying to connect with Database");
		
		try {
			Class.forName(driverClassName);
			logger.debug("Driver Class Name::: " + Class.forName(driverClassName));
			connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			logger.debug("URL::: " + dbUrl);
			logger.debug("Username::: " + dbUsername);
			StringBuilder maskedPassword = new StringBuilder();
			for (int i = 0; i < dbPassword.length(); i++) {
				maskedPassword.append("*");
	        }
			logger.info("Masked password::: " + maskedPassword.toString());
	        Arrays.fill(dbPassword.toCharArray(), '\0');
	        logger.info("<<< Successfully Connected to DataBase >>>");
	        logger.info("CONNECTED TO THE SCHEMA:: " + connection.getSchema());
			
		} catch (ClassNotFoundException exe) {
			exe.printStackTrace();
			logger.error(exe);
		} catch (SQLException exe) {
			exe.printStackTrace();
			logger.error(exe);
		}
		return connection;
	}
}