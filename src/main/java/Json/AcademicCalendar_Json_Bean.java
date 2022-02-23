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




@ManagedBean(name="academicjsonbean")
@ViewScoped
public class AcademicCalendar_Json_Bean implements Serializable {


	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public AcademicCalendar_Json_Bean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String limit=params.get("limit");
			String startingpoint=params.get("startpoint");
			String schoolid=params.get("Schoolid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			if(checkRequest)
			{
				selectedStudent=DBJ.studentDetailslistByAddNo(studentid,schoolid,conn);
				selectedSection=selectedStudent.getSectionid();
				selectedCLassSection=selectedStudent.getClassId();

				ArrayList<AcademicCalenderInfo> info=DBJ.allCalenderDetails(selectedCLassSection,limit,startingpoint,schoolid,conn);

				for(AcademicCalenderInfo ss:info)
				{
					JSONObject obj = new JSONObject();


					obj.put("Examname",ss.getExamName());
					obj.put("Examdetail", ss.getDesc());

					arr.add(obj);

				}
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
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
