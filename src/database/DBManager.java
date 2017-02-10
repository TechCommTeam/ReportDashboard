package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.gson.Gson;
import commonLib.CommonLogs;

public class DBManager {

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

	public static String getPiChartData() {
		Map <String,Integer> mp=new LinkedHashMap<String,Integer>();
		String json=null;
		int totalCount=0;
		int fail=0;
		int total=0;
		Date d=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyy hh:mm:ss");
		try {

			// To get total Number of test cases run
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			PreparedStatement ps = con.prepareStatement("select count(id) from faterun.regrun "
					+ "where buildid=(select max(buildid) from faterun.regrun"
					+ " where version=(select max(version) from faterun.regrun))");
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				totalCount=rs.getInt(1);
				mp.put("totalRun", totalCount);
			}else{
				CommonLogs.writeServerLogFile("No field selected for total no of test cases from faterun.regrun "
						+ "table"+dateFormat.format(d.getDate()));
			}

			// To get total number of failures 
			ps=con.prepareStatement("select count(id) from faterun.regrun"
					+ " where buildid=(select max(buildid) from faterun.regrun "
					+ "where version=(select max(version) from faterun.regrun)) and result='fail'");
			rs=ps.executeQuery();
			if(rs.next()){
				fail=rs.getInt(1); 
				mp.put("failures", fail);
			}else{
				CommonLogs.writeServerLogFile("No field selected for total  no of failures from faterun.regrun. "
						+ "table"+dateFormat.format(d.getDate()));
			}

			//calculating total number of testcase exists in the system
			ps=con.prepareStatement("select testcase from faterun.testcount where Mtype='total'");
			rs=ps.executeQuery();
			if(rs.next()){
				total=rs.getInt(1);
				mp.put("total", total);
			}else{
				CommonLogs.writeServerLogFile("No field selected for total  no of testcase from faterun.totalCount "
						+ "table"+dateFormat.format(d.getDate()));	
			}
			con.close();
			//Creating Json object to send back to Ajax call
			json=new Gson().toJson(mp);

		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database :: "+dateFormat.format(d.getDate()));
		}
		return json;
	}
}
