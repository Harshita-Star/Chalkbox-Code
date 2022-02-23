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
import schooldata.SubjectInfo;

@ManagedBean(name="syllabusJson")
@ViewScoped
public class SyllabusJson implements Serializable{

	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	ArrayList<SubjectInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public SyllabusJson(){

		Connection conn=DataBaseConnection.javaConnection();
 
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schoolid=params.get("Schoolid");
//			String classid = params.get("classid");
//			String sectionid = params.get("sectionid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				StudentInfo lss=DBJ.studentDetailslistByAddNo(studentid,schoolid,conn);
				list	=DBJ.viewSyllabus(lss.getClassId(),lss.getSectionid(),schoolid,conn);
				
				for(SubjectInfo ss:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("file", ss.getAssignmentPhotoPath5());
					obj.put("type", ss.getSyllabusType());
					obj.put("syllabus_type", ss.getType());
					obj.put("subject", ss.getSubjectName());
					obj.put("description", ss.getDescription());
					obj.put("term", ss.getTermName());

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