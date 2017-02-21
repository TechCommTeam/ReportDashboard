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
import database.DBMachineData;

/**
 * Servlet implementation class GetTestCaseDataServlet
 */
@WebServlet("/GetTestCaseDataServlet")
public class GetTestCaseDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetTestCaseDataServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Map <String,String[]> map=null;
		JSONObject jsonObject=new JSONObject();
		String json=null;
		String versionBuild=request.getParameter("versionBuild");
		String machine=request.getParameter("machine");
		map=DBMachineData.getTestCaseData(machine, versionBuild);

		//put map into JSONObject
		jsonObject.put("testCase",map);
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
