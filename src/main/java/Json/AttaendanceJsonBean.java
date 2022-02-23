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

@ManagedBean(name="attdance")
@ViewScoped
public class AttaendanceJsonBean implements Serializable
{

	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	
	public AttaendanceJsonBean(){

		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId=params.get("Schoolid");
			String studentid = params.get("studentid");
			String year = params.get("year");
			String month = params.get("month");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			String detail="";
			
			if(checkRequest)
			{
				int mon=DBM.monthNumberByName(month);

				selectedStudent=DBJ.studentDetailslistByAddNo(studentid,schId,conn);
				selectedSection=selectedStudent.getSectionid();
				selectedCLassSection=selectedStudent.getClassId();

				ArrayList<String> info=DBJ.allAttendanceForRecordStudentforApp(studentid,String.valueOf(mon),year,schId,selectedSection,conn);

				for(String ss:info)
				{
					if(detail.equals(""))
					{
						detail=ss;
					}
					else
					{
						detail=detail+","+ss;
					}
				}
			}

			JSONObject obj = new JSONObject();

			obj.put("DateStatus",detail);

			arr.add(obj);

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();	
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
