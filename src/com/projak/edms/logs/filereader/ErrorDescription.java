package com.projak.edms.logs.filereader;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.projak.edms.ResourceData;
import com.projak.edms.DBConnection;

/*
 * @author Bharathkumar Reddy Chitteti - Projak Infotech Private Limited, Mumbai
 * @version 3.0.0
 */

public class ErrorDescription {
	
	private static final Logger logger = Logger.getLogger(ErrorDescription.class);
	
	static {
        PropertyConfigurator.configure("D:\\EDMS\\ErrorDescription\\configurations\\log4j.properties");
    }

	final static ResourceBundle rsbundle = ResourceData.getResourceBundle();
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String dictionaryPath = rsbundle.getString("dictionaryPath");
	public static final String query = rsbundle.getString("query");
	public static final String casaDocFlagQuery = rsbundle.getString("casaDocFlagQuery");
//	public static final String errorClearQuery = rsbundle.getString("errorClearQuery");
	
    private static Connection establishDatabaseConnection(String dbUrl, String dbUsername, String dbPassword) throws SQLException {
        Connection connection = null;
        try {
            DBConnection dbConnection = new DBConnection();
            connection = dbConnection.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (IOException e) {
        	e.getMessage();
            e.printStackTrace();
            logger.error("Exception in establishDatabaseConnection() ===> " + e.getMessage());
        }
        return connection;
    }
    
    public static int getCurrentPID() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        int currentPID = Integer.parseInt(jvmName.split("@")[0]);
        return currentPID;
    }
    
    public static void savePIDAndStartTimeToFile(String filePath, int pid) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            try (PrintWriter writer = new PrintWriter(file)) {
            	
                writer.println("Current running Process ID (PID): " + pid);
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
                String startTime = dateFormat.format(new Date());
                writer.println("Start Date & Time: " + startTime);
            } catch (FileNotFoundException e) {
            	e.getMessage();
                e.printStackTrace();
                logger.error("Exception in savePID method PrintWriter ===> " + e.getMessage());
            }
        } catch (IOException e) {
        	e.getMessage();
            e.printStackTrace();
            logger.error("Exception in savePIDAndStartTimeToFile() ===> " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
    	
    	String pidFilePath = "D:\\EDMS\\ErrorDescription\\configurations\\pid.txt";
    	
        int currentPID = getCurrentPID();
        System.out.println("Current Running process ID (PID): " + currentPID);
        
        savePIDAndStartTimeToFile(pidFilePath, currentPID);
    	
        int currentYear = Year.now().getValue();

        logger.info(ANSI_GREEN + "\r\n"
                + ".______   .______        ______          __       ___       __  ___     __  .__   __.  _______   ______   .___________. _______   ______  __    __  \r\n"
                + "|   _  \\  |   _  \\      /  __  \\        |  |     /   \\     |  |/  /    |  | |  \\ |  | |   ____| /  __  \\  |           ||   ____| /      ||  |  |  | \r\n"
                + "|  |_)  | |  |_)  |    |  |  |  |       |  |    /  ^  \\    |  '  /     |  | |   \\|  | |  |__   |  |  |  | `---|  |----`|  |__   |  ,----'|  |__|  | \r\n"
                + "|   ___/  |      /     |  |  |  | .--.  |  |   /  /_\\  \\   |    <      |  | |  . `  | |   __|  |  |  |  |     |  |     |   __|  |  |     |   __   | \r\n"
                + "|  |      |  |\\  \\----.|  `--'  | |  `--'  |  /  _____  \\  |  .  \\     |  | |  |\\   | |  |     |  `--'  |     |  |     |  |____ |  `----.|  |  |  | \r\n"
                + "| _|      | _| `._____| \\______/   \\______/  /__/     \\__\\ |__|\\__\\    |__| |__| \\__| |__|      \\______/      |__|     |_______| \\______||__|  |__| \r\n"
                + "                                                                                                                                                    \r\n"
                + ANSI_RESET + ANSI_RED
                + " <<< Building the Future of automation world   ||   EDMS - ERRORDESCRIPTION API v3.0 >>>\r\n"
                + ANSI_RESET + ANSI_GREEN + "     Copyright © " + currentYear
                + ". Punjab & Sind Bank. All rights reserved. \r\n" + ANSI_RESET);

        logger.info("Loading the properties from configuration file...");
        
        String databaseURL = rsbundle.getString("databaseURL");
        List<String> usernames = Arrays.asList(rsbundle.getString("usernames").split(","));
        List<String> passwords = Arrays.asList(rsbundle.getString("passwords").split(","));
        List<String> homeDirectories = Arrays.asList(rsbundle.getString("homeDirectories").split(","));
        String logFileNamesProperty = rsbundle.getString("logFileNames");
        String[] logFileNames = logFileNamesProperty.split(",");
        
        while (true) {
        	String oldLogFileName = "D:\\EDMS\\ErrorDescription\\logs\\ErrorDescription.log.1";
        	File oldLogFile = new File(oldLogFileName);
        	if (oldLogFile.exists()) {
        		if (oldLogFile.delete()) {
        			logger.info("log backup file deleted successfully.");
        		} else {
        			logger.error("Failed to delete log backup file.");
        		}
        	}
        	for (int i = 0; i < usernames.size(); i++) {
                String username = usernames.get(i);
                String password = passwords.get(i);
                String homeDirectory = homeDirectories.get(i);

                Connection connection = establishDatabaseConnection(databaseURL, username, password);
                String schemaName = connection.getSchema();
//                checkAndClearErrorDescription(connection, schemaName);
                if (connection != null) {
                    List<String> dictionary = readDictionary(dictionaryPath);
                    List<BatchInfo> batchInfoList = executeDbQuery(connection);

                    if (batchInfoList.isEmpty()) {
                        logger.info("No aborted tasks found in the database.");
                    } else {
                        logger.info("Aborted tasks found in the database. Processing log files:");
                        for (BatchInfo batchInfo : batchInfoList) {
                        	String batchId = batchInfo.getBatchId();
                            String taskName = batchInfo.getTaskName();
                            String taskStatus = batchInfo.getTaskStatus();
                            File logDirectory = new File(homeDirectory, batchId);
                            
                            if (logDirectory.exists() && logDirectory.isDirectory()) {
                                List<File> logFiles = searchLogFiles(logDirectory.getAbsolutePath(), logFileNames, schemaName);

                                if (!logFiles.isEmpty()) {
                                    logger.info("Found .log files for batchId: " + batchId);
                                    for (File logFile : logFiles) {
                                        logger.info("Log file: " + logFile.getAbsolutePath());
                                        List<String> errorLines = findErrorLines(logFile, dictionary, connection, schemaName, batchId, taskName);

                                        if (!errorLines.isEmpty()) {
                                            StringBuilder sb = new StringBuilder();
                                            int totalLength = 0;
                                            for (String line : errorLines) {
                                                if (totalLength + line.length() <= 500) {
                                                    sb.append(line).append(System.lineSeparator());
                                                    totalLength += line.length();
                                                } else {
                                                    int remainingCharacters = 500 - totalLength;
                                                    sb.append(line.substring(0, remainingCharacters))
                                                            .append(System.lineSeparator());
                                                    break;
                                                }
                                            }
                                            String allErrorLines = sb.toString().trim();

                                            logger.info("Error lines");
                                            logger.info(allErrorLines);

                                            insertErrorLinesToDatabase(batchId, allErrorLines, connection);
                                        }
                                    }
                                } else {
                                    logger.info("No .log files found for batchId:: " + batchId);
                                }
                            } else {
                                logger.info("Directory not found for batchId:: " + batchId);
                            }
                        }
                    }
                    connection.close();
                    logger.info("Connection Closed: " + connection.isClosed());
                }
            }
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
            	e.getMessage();
                e.printStackTrace();
                logger.error("Error in 1st sleep mode: " + e.getMessage());
                
                try {
                	TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException ex) {
                	ex.getMessage();
                    ex.printStackTrace();
                    logger.error("Error in 2nd sleep mode: " + e.getMessage());
                }
            }
        }
    }
    
    public static class BatchInfo {
	    private String batchId;
	    private String taskName;
	    private String taskStatus;

	    public BatchInfo(String batchId, String taskName, String taskStatus) {
	        this.batchId = batchId;
	        this.taskName = taskName;
	        this.taskStatus = taskStatus;
	    }

	    public String getBatchId() {
	        return batchId;
	    }

	    public String getTaskName() {
	        return taskName;
	    }
	    
	    public String getTaskStatus() {
	        return taskStatus;
	    }
	}
	
	private static List<BatchInfo> executeDbQuery(Connection connection) {
	    List<BatchInfo> batchInfoList = new ArrayList<>();

	    try {
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);

	        while (resultSet.next()) {
	            String batchId = resultSet.getString("TS_BATCHID");
	            String taskName = resultSet.getString("TS_TASKNAME");
	            String taskStatus = resultSet.getString("TS_STATUS");
	            
	            BatchInfo batchInfo = new BatchInfo(batchId, taskName, taskStatus);
	            batchInfoList.add(batchInfo);
	        }
	    } catch (SQLException e) {
	        e.getMessage();
	        e.printStackTrace();
	        logger.error("Exception in executeDbQuery() ===> " + e.getMessage());
	    }
	    return batchInfoList;
	}
	
	private static String getPB_DOCFLG(Connection connection, String batchId) {
	    String pbDocFlg = null;
	    String query = casaDocFlagQuery;

	    try {
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, batchId);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            pbDocFlg = resultSet.getString("PB_DOCFLG");
	        }
	    } catch (SQLException e) {
	    	e.getMessage();
	        e.printStackTrace();
	        logger.error("Exception in getPB_DOCFLG() ===> " + e.getMessage());
	    }

	    return pbDocFlg;
	}

	private static List<File> searchLogFiles(String directoryPath, String[] logFileNames, String schemaName) {
		List<File> logFiles = new ArrayList<>();

		File directory = new File(directoryPath);

		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						logFiles.addAll(searchLogFiles(file.getAbsolutePath(), logFileNames, schemaName));
					} else if (file.isFile()) {
						for (String logFileName : logFileNames) {
							if (file.getName().startsWith(logFileName) && file.getName().endsWith(".log")) {
								if ("CASAEng".equalsIgnoreCase(schemaName)) {
									logFiles.add(file);
		                            break;
								} else {
									if (file.getName().startsWith("navexport_rrs") && file.getName().endsWith(".log")) {
										logFiles.add(file);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return logFiles;
	}
	
	private static List<String> readDictionary(String dictionaryPath) {
		List<String> dictionary = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryPath))) {
			String word;
			while ((word = reader.readLine()) != null) {
				dictionary.add(word);
			}
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
			logger.error("Exception in readDictionary() ===> " + e.getMessage());
		}
		return dictionary;
	}

	private static List<String> findErrorLines(File file, List<String> dictionary, Connection connection, String schemaName, String batchId, String taskName) {
	    Set<String> uniqueErrorLinesWithPeriod = new HashSet<>();
	    Set<String> uniqueErrorLinesWithoutPeriod = new HashSet<>();
		int errorLineNumber = 1;
		boolean isMultipleErrors = false;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				for (String word : dictionary) {
					if (line.contains(word)) {
						if (line.contains("Error description")) {
							int startIndex = line.indexOf("Error description =") + "Error description =".length();
		                    String errorDescription = line.substring(startIndex);
		                    uniqueErrorLinesWithoutPeriod.add(errorDescription.trim());
		                    break;
						}
						if ("CASAEng".equalsIgnoreCase(schemaName)) {
							if ("NAVAcknowledgement".equalsIgnoreCase(taskName)) {
								if (line.contains("(str=\"@B.ACKAPIResponse\",str=\"API Failure\")") || line.contains("(str=\"@B.ACKAPIResponse\",str=\"@EMPTY\")")) {
									String errorDescription = "Acknowledgement API is down, please contact your system administrator";
									uniqueErrorLinesWithoutPeriod.add(errorDescription);
									break;
								}	
							}
							if ("NAVPageID".equalsIgnoreCase(taskName)) {
								if (line.contains("(str=\"@B.APIResponse\",str=\"@API Failure\")") || line.contains("(str=\"@B.APIResponse\",str=\"@EMPTY\")")) {
									String errorDescription = "Get Employee Detail API is down, please contact your system administrator";
									uniqueErrorLinesWithoutPeriod.add(errorDescription);
									break;
								}
							}
							if ("NAVExport".equalsIgnoreCase(taskName)) {
								if (line.contains("(str=\"@B.SIGNAPIResponse\",str=\"@EMPTY\")") || line.contains("(str=\"@B.SIGNAPIResponse\",str=\"API Failure\")")) {
									String errorDescription = "Signature API is down, please contact your system administrator";
									uniqueErrorLinesWithoutPeriod.add(errorDescription);
									break;
								}
								if (line.contains("(str=\"@B.PHOTOAPIResponse\",str=\"API Failure\")") || line.contains("(str=\"@B.PHOTOAPIResponse\",str=\"@EMPTY\")")) {
									String errorDescription = "Photograph API is down, please contact your system administrator";
									uniqueErrorLinesWithoutPeriod.add(errorDescription);
									break;
								}
							}
							if ("NAVFixUpProfiler1".equalsIgnoreCase(taskName)) {
								String pbDocFlg = getPB_DOCFLG(connection, batchId);
								logger.info("DOC FLAG: " + pbDocFlg);
								if ("N".equalsIgnoreCase(pbDocFlg)) {
									if (line.contains("(str=\"@B.FormDocPresent\",str=\"NO\")") || line.contains("(str=\"@B.KYCDoc1Present\",str=\"NO\")") || line.contains("(str=\"@B.KYCDoc2Present\",str=\"NO\")")) {
										String errorDescription = "Mandatory Form and KYC documents are missing, please verify";
										uniqueErrorLinesWithoutPeriod.add(errorDescription);
										break;
									}
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
			logger.error("Exception in findErrorLines() ===> " + e.getMessage());
		}
		if (uniqueErrorLinesWithoutPeriod.size() > 1) {
	        isMultipleErrors = true;
	    }
		List<String> errorLines = new ArrayList<>();
		for (String uniqueErrorLine : uniqueErrorLinesWithoutPeriod) {
	        String errorLineWithPeriod = addPeriodIfMissing(uniqueErrorLine);
	        if (isMultipleErrors) {
	            errorLines.add(errorLineNumber + ". " + errorLineWithPeriod);
	            errorLineNumber++;
	        } else {
	            errorLines.add(errorLineWithPeriod);
	        }
	        uniqueErrorLinesWithPeriod.add(errorLineWithPeriod);
		}
	    return errorLines;
	}
	
	private static String addPeriodIfMissing(String text) {
	    if (text.endsWith(".")) {
	        return text;
	    } else {
	        return text + ".";
	    }
	}
	
//	private static void checkAndClearErrorDescription(Connection connection, String schemaName) {
//        String clearQuery = errorClearQuery;
//        
//        try {
//        	PreparedStatement preparedStatement = connection.prepareStatement(clearQuery);
//            preparedStatement.setString(1, schemaName);
//            preparedStatement.setString(2, schemaName);
//            preparedStatement.executeUpdate();
//            preparedStatement.close();
//            logger.info("CLEAR ERROR UPDATE COUNT: " + preparedStatement.getUpdateCount());
//        } catch (SQLException e) {
//        	e.getMessage();
//            e.printStackTrace();
//            logger.error("Exception in checkAndClearErrorDescription() ===> " + e.getMessage());
//        }
//    }
	
	private static void insertErrorLinesToDatabase(String batchName, String errorLines, Connection connection) {
	    logger.info("on the insertErrorLinesToDatabase()");

	    if (errorLines.length() > 500) {
	        errorLines = errorLines.substring(0, 500);
	    }
	    try {
	    	String schemaName = connection.getSchema();
	    	logger.info("SCHEMA NAME ==> " + schemaName);
	    	String updateQuery = rsbundle.getString("updateQuery");
	    	updateQuery = updateQuery.replace("{SCHEMA_NAME}", schemaName);
	    	logger.info("UPDATE QUERY:: " + updateQuery);
	    	logger.info("Updating the Error's in Database...");
	        PreparedStatement insertStatement = connection.prepareStatement(updateQuery);
	        //insertStatement.setString(1, schemaName);
	        insertStatement.setString(1, errorLines);
	        insertStatement.setString(2, batchName);
	        insertStatement.executeUpdate();
	        logger.info("New batch:: " + batchName + " with error lines added to the database.");
	    } catch (SQLException e) {
	    	e.getMessage();
	        e.printStackTrace();
	        logger.error("Exception in insertErrorLinesToDatabase() ==> " + e.getMessage());
	    }
	}
}
