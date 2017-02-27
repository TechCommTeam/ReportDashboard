package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import commonLib.CommonLogs;

public class DBHistory {

	public DBHistory() {
		// TODO Auto-generated constructor stub
	}
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

	public static Map <String,String[]> getHistoryData(String testCaseId) {
		Map <String,String[]> mp=new LinkedHashMap<String,String[]>();
		Date d=new Date();
		String testCaseId1=testCaseId.replace("\\",".");
		String dbquery="select version,buildid,feature as tag,owner,result from faterun.regrun "
				+ "where id like '%"+testCaseId1+"%' order by ts desc";
		try {
			// To get the history for Selected Test Case
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			PreparedStatement ps = con.prepareStatement(dbquery);
			ResultSet rs = ps.executeQuery();
			rs=ps.executeQuery();
			
			while(rs.next()){
				String temp[]={"","",""};
				String key=rs.getString("version")+"."+rs.getString("buildid");
				temp[0]=rs.getString("tag");
				temp[1]=rs.getString("owner");
				temp[2]=rs.getString("result");
				mp.put(key,temp);
			}
			con.close();
		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database :: "+d);
		}
		return mp;
	}
}
