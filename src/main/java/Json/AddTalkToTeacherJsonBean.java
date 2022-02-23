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
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
@ManagedBean(name="addTalkTOTeacher")
@ViewScoped
public class AddTalkToTeacherJsonBean implements Serializable
{


	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AddTalkToTeacherJsonBean()
	{

		Connection conn=DataBaseConnection.javaConnection();
	
		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();


			String message = params.get("message");
			String from = params.get("from");
			String to = params.get("to");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			
			String status = "not updated";
			
			if(checkRequest)
			{
				//new DatabaseMethods1().submitanswer(studentid,qid,answerstatus,answer);

				int i=DBM.submitmessage(message,from,to,conn);

				if(i>0)
				{
					status="updated";
				}
				else
				{
					status="not updated";
				}
			}

			obj.put("status",status);

			arr.add(obj);


			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
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
