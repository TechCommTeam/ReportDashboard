package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
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

	public static Map <String,Integer> getPieChartData() {
		Map <String,Integer> mp=new LinkedHashMap<String,Integer>();
		int totalCount=0;
		int fail=0;
		int total=0;
		Date d=new Date();
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
						+ "table"+d);
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
						+ "table"+d);
			}

			//calculating total number of test cases exists in the system
			ps=con.prepareStatement("select testcase from faterun.testcount where Mtype='total'");
			rs=ps.executeQuery();
			if(rs.next()){
				total=rs.getInt(1);
				mp.put("total", total);
			}else{
				CommonLogs.writeServerLogFile("No field selected for total  no of testcase from faterun.totalCount "
						+ "table"+d);	
			}
			con.close();

		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database :: "+d);
		}
		return mp;
	}


	//Getting result or bar chart 
	public static Map <String,Integer[]> getBarChartData() {
		Map <String,Integer[]> mp=new LinkedHashMap<String,Integer[]>();
		Date d=new Date();
		try {

			// To get total Number of test cases run on a  Particular build (previous run)
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			PreparedStatement ps = con.prepareStatement("select version,buildid, count(id) as resultNum,result from faterun.regrun "
					+ "group by version,buildid,result  order  by ts desc LIMIT 0, 20");
			ResultSet rs1 = ps.executeQuery();

			while(rs1.next()){
				Integer values[]={0,0};
				String key=rs1.getString("version")+".0."+rs1.getInt("buildid");
				if(rs1.getString("result").equalsIgnoreCase("fail")){
					if(mp.containsKey(key))
					{
						Integer temp[]=mp.get(key);
						temp[0]+=rs1.getInt("resultNum");
						temp[1]+=rs1.getInt("resultNum");
					}
					else{
						values[0]=rs1.getInt("resultNum");
						values[1]=rs1.getInt("resultNum");
						mp.put(key, values);
					}
				}else{
					if(mp.containsKey(key))
					{
						Integer temp[]=mp.get(key);
						temp[0]+=rs1.getInt("resultNum");
					}
					else{
						values[0]=rs1.getInt("resultNum");
						mp.put(key, values);
					}
				}
			}

			//Closing DB connection 
			con.close();

		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database :: "+d);
		}
		return mp;
	}

}
