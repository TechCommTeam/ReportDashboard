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
	public static Map <String,Object[]> getMachineData(String versionBuild){
		Map <String,Object[]> mp=new LinkedHashMap<String,Object[]>();
		Map <String,Long> timeStamp=new LinkedHashMap<String,Long>();
		Date d=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		try{
			String [] tempList=versionBuild.split("\\.");
			String version=tempList[0];
			String build=tempList[tempList.length-1];

			String dbQuery="select distinct(owner),result,count(id) as resultNum,max(ts) as timeStamp "
					+ "from faterun.regrun where buildid="+build+" and version='"+version+"' group by result, "
					+ "owner order by owner";
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			PreparedStatement ps=con.prepareStatement(dbQuery);
			ResultSet rs=ps.executeQuery();

			while(rs.next()){
				Object values[]={0,0,""};
				String key=rs.getString("owner");

				//Adding unique timeStamp in result map for a particular build
				java.sql.Timestamp dbTime=rs.getTimestamp("timeStamp");
				Long dbTimeTemp=dbTime.getTime();

				if(timeStamp.containsKey(key)){
					if(dbTimeTemp>timeStamp.get(key)){
						timeStamp.put(key, dbTimeTemp);
					}
				}else{
					timeStamp.put(key, dbTimeTemp);
				}


				//Adding result for all build in map
				if(rs.getString("result").equalsIgnoreCase("fail")){
					if(mp.containsKey(key))
					{
						Object temp[]=mp.get(key);
						Integer convTemp=(Integer)temp[0];
						convTemp+=rs.getInt("resultNum");
						temp[0]=convTemp;
						convTemp=(Integer)temp[1];
						convTemp+=rs.getInt("resultNum");
						temp[1]=convTemp;
					}
					else{
						values[0]=rs.getInt("resultNum");
						values[1]=rs.getInt("resultNum");
						mp.put(key, values);
					}
				}else{
					if(mp.containsKey(key))
					{
						Object temp[]=mp.get(key);
						Integer convTemp=(Integer)temp[0];
						convTemp+=rs.getInt("resultNum");
						temp[0]=convTemp;
					}
					else{
						values[0]=rs.getInt("resultNum");
						mp.put(key, values);
					}
				}
			}
			// Adding Formatted time for all respective run 
			for(String key:timeStamp.keySet()){
				Date date=new Date(timeStamp.get(key));
				mp.get(key)[2]=dateFormat.format(date);
			}
			//Closing DB connection 
			con.close();

		}catch(ArrayIndexOutOfBoundsException xa){
			CommonLogs.writeServerLogFile("Can't  find Version or Build number");
		}catch(Exception x){
			CommonLogs.writeServerLogFile("Can't find Required field from DB for machines "+d);
		}
		return mp;
	}
	
	// Get all information related to test case machine
	public static Map <String,String[]> getTestCaseData(String machine,String versionBuild){
		Map <String,String[]> mp=new LinkedHashMap<String,String[]>();
		Date d=new Date();
		try{
			String [] tempList=versionBuild.split("\\.");
			String version=tempList[0];
			String build=tempList[tempList.length-1];
			//get all machine  and build specific data
			String dbQuery="select id,result as status,comments  from faterun.regrun where buildid="+build+ 
					" and version='"+version+"' and owner='"+machine+"' order by id";
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			PreparedStatement ps=con.prepareStatement(dbQuery);
			ResultSet rs=ps.executeQuery();

			while(rs.next()){
				String values[]={"",""};
				String key=rs.getString("id");
				if(key.contains(".")){
					int tempKey=key.indexOf(".",key.indexOf(".")+1);
					key=key.substring(tempKey+1);
					key=key.replace(".", "\\");
				}
				values[0]=rs.getString("status");
				values[1]=rs.getString("comments");
				mp.put(key, values);
			}
			//Closing DB connection 
			con.close();

		}catch(ArrayIndexOutOfBoundsException xa){
			CommonLogs.writeServerLogFile("Can't  find Version or Build number"+d);
		}catch(Exception x){
			CommonLogs.writeServerLogFile("Can't find Required field from DB for machines "+d);
		}
		return mp;
	}

}
