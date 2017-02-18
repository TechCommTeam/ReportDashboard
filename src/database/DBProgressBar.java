package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
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
		Map <String,Integer> testCaseCountMp=new LinkedHashMap<String,Integer>();
		java.sql.Timestamp mySqlTime=null; 
		Date d=new Date();
		//		SimpleDateFormat sm=new SimpleDateFormat("");

		SimpleDateFormat format = new SimpleDateFormat("DD-MM-YYYY HH:mm:ss");
		Date mUtilDate=null;
		try {

			// To get total Number of test cases run
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			PreparedStatement ps = con.prepareStatement("select distinct(owner),result,count(id)  as resultNum,  max(ts) as timeStamp "
					+ "from faterun.regrun where buildid=(select max(buildid) from faterun.regrun " 
					+"where version=(select max(version) from faterun.regrun))  group by result,owner "
					+ "order by owner");
			ResultSet rs = ps.executeQuery();

			//Machine status from testCount table 
			ps=con.prepareStatement("select * from faterun.testcount");
			ResultSet rsTestCases=ps.executeQuery();
			while(rsTestCases.next()){
				testCaseCountMp.put(rsTestCases.getString("Mtype"), rsTestCases.getInt("testcase"));
			}

			//Storing all result back to the map
			Map <String,Long> tempTime=new LinkedHashMap<String,Long>();
			while(rs.next()){
				Integer values[]={0,0,0,0};
				String key=rs.getString("owner");

				//Getting result for no of test cases to be executed on a particular machine
				if(testCaseCountMp.containsKey(key)){
					values[2]=testCaseCountMp.get(key);
				}else{
					values[2]=-1;
				}

				//Setting up machine status for execution
				mySqlTime=rs.getTimestamp("timeStamp");
				mUtilDate=new Date(mySqlTime.getTime());
				long timeStatus=(d.getTime()-mUtilDate.getTime())/(1000*60);
				if(tempTime.containsKey(key)){
					if(tempTime.get(key)>timeStatus){
						tempTime.put(key, timeStatus);
					}
				}else{
					tempTime.put(key, timeStatus);
				}

				if(mp.containsKey(key)){
					if(rs.getString("result").equalsIgnoreCase("fail")){
						Integer []temp=mp.get(key);
						temp[1]=rs.getInt("resultNum");
					}else{
						Integer[] temp=mp.get(key);
						temp[0]=rs.getInt("resultNum");
					}
				}else{
					if(rs.getString("result").equalsIgnoreCase("fail")){
						values[1]=rs.getInt("resultNum");
					}else{
						values[0]=rs.getInt("resultNum");
					}
					mp.put(key, values);
				}
			}

			//DB Connection closed
			con.close();

			//Setting Up values for Machine Status
			try{
				for(String key:mp.keySet()){
					if(mp.containsKey(key)){
						Integer total=mp.get(key)[0]+mp.get(key)[1];
						if(total.equals(mp.get(key)[2])){
							mp.get(key)[3]=0;
						}else{
							if(tempTime.get(key)/(60*24)>2){
								mp.get(key)[3]=-2;
							}else{
								if(tempTime.get(key)>10){
									mp.get(key)[3]=-1;
								}else{
									mp.get(key)[3]=1;
								}
							}
						}
					}else{
						mp.get(key)[3]=-3;
					}
				}
			}catch(ArrayIndexOutOfBoundsException xa){
				CommonLogs.writeServerLogFile("Array Index out of bound error for progress Bar :: "+d);
			}

		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database for progress Bar :: "+d);
		}
		return mp;
	}

}
