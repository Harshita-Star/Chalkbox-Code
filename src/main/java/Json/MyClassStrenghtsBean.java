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

@ManagedBean(name="myclassstrenghts")
@ViewScoped
public class MyClassStrenghtsBean implements Serializable
{

	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public MyClassStrenghtsBean()
	{

		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schoolid=params.get("Schoolid");
			/*String date = params.get("date");

	        Date newDate=null;
	        try {
			newDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			 */
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				selectedStudent=DBJ.studentDetailslistByAddNo(studentid,schoolid,conn);
				selectedSection=selectedStudent.getSectionid();
				selectedCLassSection=selectedStudent.getClassId();
				//	list=new DatabaseMethods1().viewAssignmentjson(selectedCLassSection,newDate);
				list=new DatabaseMethods1().getAllStudentStrentgthforpp(schoolid,selectedSection,conn);

				//  ArrayList<NotificationInfo> info=new DatabaseMethods1().allMessageforapp(studentid);

				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("Studentname", ss.getFullName());
					obj.put("img", ss.getStudent_image());
					arr.add(obj);
				}
			}

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
