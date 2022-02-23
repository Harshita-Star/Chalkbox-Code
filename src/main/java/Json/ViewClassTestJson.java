package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.ClassTest;
import schooldata.DataBaseConnection;

@ManagedBean(name="viewClassTest")
@ViewScoped
public class ViewClassTestJson implements Serializable{



	String json;
	String sectionid,message;
	String AddmissionNo,schoolid;
	Connection conn=DataBaseConnection.javaConnection();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public ViewClassTestJson()
	{
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			AddmissionNo=params.get("addNumber");
			schoolid=params.get("Schoolid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<ClassTest>list=DBJ.allTestReport(AddmissionNo,schoolid,conn);
				
				 // System.out.println(list.size());
				
				
				for(ClassTest ls:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("date",ls.getDt());
					obj.put("subject",ls.getSubject());
					obj.put("test_Name",ls.getTestName());
					obj.put("test_Marks",ls.getTestMarks());

					
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}












}
