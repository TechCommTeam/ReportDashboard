package database;

import commonLib.CommonLogs;

public class DBMachineData {
	private static String DBUrl = "jdbc:mysql://localhost:3306/faterun";
	private static String DBCon = "com.mysql.jdbc.Driver";
	private static String DbUserName = "root";
	private static String DbUserPassword = "admin";

	static {
		try {
			Class.forName(DBCon);
			CommonLogs.writeServerLogFile("Connected to the database successfully");
		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Unable to Load JDBC Driver "+xa);
		}
	}
	public DBMachineData() {
		// TODO Auto-generated constructor stub
	}
	public static void getMachineData(String versionBuild){
		try{
		String [] tempList=versionBuild.split("\\.");
		String version=tempList[0];
		String build=tempList[tempList.length-1];
		}catch(ArrayIndexOutOfBoundsException xa){
			CommonLogs.writeServerLogFile("Can't  find Version or Build number");
		}
	}

}
