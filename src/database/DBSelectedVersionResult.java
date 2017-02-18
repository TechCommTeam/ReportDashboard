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

public class DBSelectedVersionResult {

	public DBSelectedVersionResult() {
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

	public static Map <String,Object[]> getVersionData(String state,String version) {
		Map <String,Object[]> mp=new LinkedHashMap<String,Object[]>();
		Map <String,Long> timeStamp=new LinkedHashMap<String,Long>();
		Date d=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		try {

			// To get total Number of test cases run
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			String dbQuery=null;
			if(version.equalsIgnoreCase("")){
				dbQuery="select version, buildid, result, feature as tag, max(ts) as timeStamp, count(id)  as resultNum from faterun.regrun where"
						+ " version=(select max(version)from faterun.regrun) group by result,buildid order "
						+ " by buildid desc"; 
			}else{
				if(state.equalsIgnoreCase("0")){
					dbQuery="select version, buildid, result, feature as tag, max(ts) as timeStamp, count(id)  as resultNum from faterun.regrun where "
							+ " version="+version+" group by result,buildid order "
							+ " by buildid desc"; 
				}else{
					if(state.equalsIgnoreCase("-1")){
						dbQuery="select version,buildid , result, feature as tag, max(ts) as timeStamp, count(id)  as resultNum from faterun.regrun "
								+ " where version=(select max(version) from faterun.regrun "
								+ " where version<"+version+") group by result,buildid order by buildid desc"; 
					}else{
						dbQuery="select version,buildid , result, feature as tag, max(ts) as timeStamp, count(id)  as resultNum from faterun.regrun "
								+ " where version=(select min(version) from faterun.regrun "
								+ " where version>"+version+") group by result,buildid order by buildid desc"; 
					}
				}
			}
			PreparedStatement ps=con.prepareStatement(dbQuery);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				Object values[]={0,0,"",""};
				String key=rs.getString("version")+".0."+rs.getInt("buildid");

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

				//Adding Build tag 
				values[2]=rs.getString("tag");

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
			con.close();
			// Adding Formatted time for all respective run 
			for(String key:timeStamp.keySet()){
				Date date=new Date(timeStamp.get(key));
				mp.get(key)[3]=dateFormat.format(date);
			}
		} catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database :: "+d);
		}
		return mp;
	}

	//Get state of button for next and previous move
	public static Map <String,Boolean[]> getButtonState(String state,String version) {
		Map <String,Boolean[]> mp=new LinkedHashMap<String,Boolean[]>();
		Date d=new Date();
		try {
			// To get total Number of test cases run
			Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
			String dbQuery=null;
			Boolean bStatus[]={true,false};

			//Create query to get availability of next button action
			boolean exist=false;
			ResultSet rs=null;
			PreparedStatement ps=null;
			if(state.equalsIgnoreCase("0")){
				dbQuery="select max(version) as version from faterun.regrun where version <'"+version+"'";
				ps=con.prepareStatement(dbQuery);
				rs = ps.executeQuery();
				exist=rs.next();
				if(((rs.getString("version"))==null))
					bStatus[0]=false;
				mp.put("buttonStatus", bStatus);

			}else{
				if(state.equalsIgnoreCase("-1")){
					dbQuery="select max(version) as version from faterun.regrun where version <'"+version+"'";
				}else{
					dbQuery="select min(version) as version from faterun.regrun where version >'"+version+"'";
				}
				ps=con.prepareStatement(dbQuery);
				rs = ps.executeQuery();
				exist=rs.next();
				//Set status for backward and forward button 

				if(((rs.getString("version"))==null)){
					if(state.equalsIgnoreCase("-1")){
						bStatus[0]=false;
						bStatus[1]=true;

					}else{
						bStatus[0]=true;
						bStatus[1]=false;
					}
				}
				else{
					bStatus[0]=true;
					bStatus[1]=true;
				}
			}

			mp.put("buttonStatus", bStatus);

			//Close connection  with Data base
			con.close();
		}catch (Exception xa) {
			CommonLogs.writeServerLogFile("Can't find Require Fields from Database :: "+d);
		}
		return mp;
	}
}
