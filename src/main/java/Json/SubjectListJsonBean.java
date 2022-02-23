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
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ManagedBean(name="subjectListJSON")
@ViewScoped

public class SubjectListJsonBean implements Serializable
{
	String json;
	String selectedCLassSection,selectedSection;
	StudentInfo selectedStudent;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public SubjectListJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid=params.get("studentid");
			String schoolid=params.get("Schoolid");

			selectedStudent=DBJ.studentDetailslistByAddNo(studentid,schoolid,conn);
			selectedSection=selectedStudent.getSectionid();
			selectedCLassSection=selectedStudent.getClassId();

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<SelectItem> subjectList=DBJ.allSubjectClassWise(selectedCLassSection,conn,schoolid);
				
				for(SelectItem ls:subjectList)
				{
					JSONObject obj = new JSONObject();
					obj.put("subjectId",ls.getValue());
					obj.put("subjectName",ls.getLabel());
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
