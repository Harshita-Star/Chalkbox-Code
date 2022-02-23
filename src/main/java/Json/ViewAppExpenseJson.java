package Json;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ViewScoped
@ManagedBean(name="viewAppExpenseJson")
public class ViewAppExpenseJson
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public ViewAppExpenseJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String startDate=params.get("startDate");
			String endDate=params.get("endDate");
			String type=params.get("type");
			String schid = params.get("schid");
			Date sDate=null,eDate=null;
			try {
				sDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
				eDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<StudentInfo> list=DBJ.viewAllStudentExpense(sDate,eDate,type,schid,conn);
				String userWiseTotal="";
				if(type.equalsIgnoreCase("all"))
				{
					userWiseTotal = DBJ.userWiseTotalExpense(sDate,eDate,schid,conn);
				}

				for(StudentInfo ls:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("amount",ls.getExpenseAmount());
					obj.put("remark",ls.getRemark());
					obj.put("expDate",ls.getStartingDateStr());
					obj.put("username", ls.getUsername());
					obj.put("total", userWiseTotal);
					arr.add(obj);
				}
			}
			
			json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}



}
