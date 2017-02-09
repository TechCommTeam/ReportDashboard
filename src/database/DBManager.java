package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import commonLib.CommonLogs;

public class DBManager {

	private static String DBUrl = "jdbc:mysql://localhost:3306/faterun";
	private static String DBCon = "com.mysql.jdbc.Driver";
	private static String DbUserName = "root";
	private static String DbUserPassword = "";

	static {
		try {
			Class.forName(DBCon);
		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Unable to Load JDBC Driver ");
		}
	}

	public static boolean getPiChartData() {
		boolean flag = false;
		int totalCount=0;
		int fail=0;
		int pass=0;
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
			}else{
				CommonLogs.writeServerLogFile("No field selected for total no of test cases from regrun "
						+ "table"+dateFormat.format(d.getDate()));
			}

			// To get total number of failures 
			ps=con.prepareStatement("select count(id) from faterun.regrun"
					+ " where buildid=(select max(buildid) from faterun.regrun "
					+ "where version=(select max(version) from faterun.regrun)) and result='fail'");
			rs=ps.executeQuery();
			if(rs.next()){
				fail=rs.getInt(1); 
			}else{
				CommonLogs.writeServerLogFile("No field selected for total  no of failures from regrun "
						+ "table"+dateFormat.format(d.getDate()));
			}

			//To get total number of passed test case from last run
			pass=totalCount-fail; 

		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database :: "+dateFormat.format(d.getDate()));
		}
		return flag;
	}
}
