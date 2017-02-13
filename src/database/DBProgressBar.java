package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import commonLib.CommonLogs;

public class DBProgressBar {

	public DBProgressBar() {
		// TODO Auto-generated constructor stub
	}
	private static String DBUrl = "jdbc:mysql://localhost:3306/faterun";
	private static String DBCon = "com.mysql.jdbc.Driver";
	private static String DbUserName = "root";
	private static String DbUserPassword = "admin";

	static {
		try {
			Class.forName(DBCon);
			CommonLogs.writeServerLogFile("Connected to the database successfully for progress bar");
		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Unable to Load JDBC Driver for progress bar "+xa);
		}
	}

	public static Map <String,Integer[]> getProgressBarData() {
		Map <String,Integer[]> mp=new LinkedHashMap<String,Integer[]>();
		Date d=new Date();
		try {

			// To get total Number of test cases run
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			PreparedStatement ps = con.prepareStatement("select distinct(owner),result,count(id)  as resultNum "
					+ "from faterun.regrun where buildid=(select max(buildid) from faterun.regrun " 
					+"where version=(select max(version) from faterun.regrun))  group by result,owner "
					+ "order by owner");

			ResultSet rs = ps.executeQuery();

			//Storing all result back to the map
			while(rs.next()){
				Integer values[]={0,0};
				String key=rs.getString("owner");
				if(rs.getString("result").equalsIgnoreCase("fail")){
					if(mp.containsKey(key))
					{
						Integer temp[]=mp.get(key);
						temp[0]+=rs.getInt("resultNum");
						temp[1]+=rs.getInt("resultNum");
					}
					else{
						values[0]=rs.getInt("resultNum");
						values[1]=rs.getInt("resultNum");
						mp.put(key, values);
					}
				}else{
					if(mp.containsKey(key))
					{
						Integer temp[]=mp.get(key);
						temp[0]+=rs.getInt("resultNum");

					}
					else{
						values[0]=rs.getInt("resultNum");
						mp.put(key, values);
					}
				}
			}

			//Printing all values for map objects
			for(String key: mp.keySet()){
				System.out.println(key);
			}

			//DB Connection closed
			con.close();

		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database for progress Bar :: "+d);
		}
		return mp;
	}

}
