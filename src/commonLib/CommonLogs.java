package commonLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommonLogs {

	public CommonLogs() {
		// TODO Auto-generated constructor stub
	}
	synchronized public static void writeServerLogFile(String log){
		try{
			File logFile= new File("C:\\ReportDashboard\\server.log");
			//Create a new Result Log File if Not exist
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			FileWriter file=new FileWriter("C:\\ReportDashboard\\server.log",true);
			log+="\n***********************************************************************\n";
			file.write(log);
			file.flush();
			file.close();
		}catch(IOException x){
			System.out.println("Problem in Reading  or writing Server.log  File");
		}
	}


}
