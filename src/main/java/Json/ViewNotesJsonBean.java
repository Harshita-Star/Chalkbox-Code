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
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="viewNotesJsonBean")
@ViewScoped

public class ViewNotesJsonBean implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection,selectedSection="",subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	public ViewNotesJsonBean() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schid=params.get("schid");
			String type=params.get("usertype");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(type==null || type.equalsIgnoreCase("") || type.equalsIgnoreCase("student"))
				{
					selectedStudent=DBJ.studentDetailslistByAddNo(studentid, schid,conn);
					selectedSection=selectedStudent.getSectionid();
					selectedCLassSection=selectedStudent.getClassId();
					list=DBJ.viewStudentAssignmentNotes(selectedSection,schid,conn,studentid,selectedCLassSection,"student");
				}
				else
				{
					list=DBJ.viewStudentAssignmentNotes("",schid,conn,studentid,"",type);
				}

				SchoolInfoList list1=DBJ.fullSchoolInfo(schid,conn);

				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("date", ss.getStartDate());
					obj.put("subject", ss.getAss3());
					obj.put("desc", ss.getDescription());
					if(ss.isShowFile())
					{
						obj.put("images", ss.getAss1());
					}
					else
					{
						obj.put("images", "");
					}
					obj.put("path",list1.getDownloadpath());
					obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
					
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
