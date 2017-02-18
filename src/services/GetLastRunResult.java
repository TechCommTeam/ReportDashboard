package services;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import commonLib.CommonLogs;
import database.DBSelectedVersionResult;

/**
 * Servlet implementation class GetLastRunResult
 */
@WebServlet("/GetLastRunResult")
public class GetLastRunResult extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetLastRunResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map <String,Object[]> map=null;
		Map <String,Boolean[]> buttonMap=null;
		JSONObject jsonObject=new JSONObject();
		String json=null;

		//Get attribute value and data for selected Version 
		String state=request.getParameter("state").toString();
		String version=request.getParameter("version").toString();
		map=DBSelectedVersionResult.getVersionData(state,version);

		//Find values for button status for prediction 
		String nVersion="";

		try{
			for(String key:map.keySet()){
				if(nVersion.equals("")){
					nVersion=key.toString().substring(0, 2);
					break;
				}
			}
		}catch(ArrayIndexOutOfBoundsException xa){
			CommonLogs.writeServerLogFile("Can't find next or previous version for button status ");
		}
		buttonMap=DBSelectedVersionResult.getButtonState(state, nVersion);

		//put map into JSONObject
		jsonObject.put("allRun",map);
		jsonObject.put("buttonStatus", buttonMap);
		json=new Gson().toJson(jsonObject);


		//Set content type for response
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
