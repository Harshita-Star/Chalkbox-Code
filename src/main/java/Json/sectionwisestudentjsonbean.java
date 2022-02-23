package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
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
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;
@ManagedBean(name="sectionwisetsudentbean")
@ViewScoped
public class sectionwisestudentjsonbean implements Serializable{

	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	String json;
	public sectionwisestudentjsonbean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
        try {
        	Map<String, String> params =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestParameterMap();

    		String sectionid=params.get("sectionid");
    		String schid=params.get("Schoolid");

    		JSONObject mainobj = new JSONObject();
    		JSONArray arr=new JSONArray();
    		
    		Map<String, String> sysParams =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestHeaderMap();
    		String userAgent = sysParams.get("User-Agent");
    		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
    		DataBaseMethodStudent objStd=new DataBaseMethodStudent();
    		ArrayList<String> stdColumnList=objStd.attendanceFieldList();
    		String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
    		String classid=new DatabaseMethods1().classSectionNameFromidSchid(schid, sectionid, conn);
    		
    		if(checkRequest)
    		{
    			ArrayList<StudentInfo>	studentDetails=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, conn);
    			studentDetails=DBJ.allAttendance(sectionid, new Date(), studentDetails,schid,conn);
    			for(StudentInfo ss:studentDetails)
    			{
    				JSONObject obj = new JSONObject();
    				obj.put("addNumber",ss.getAddNumber());
    				obj.put("phoneNo", ss.getFathersPhone());
    				obj.put("Studentname", ss.getFullName());
    				obj.put("status",ss.getAttendance());

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
	public String getJson() {
		return json;
	}


	public void setJson(String json) {
		this.json = json;
	}
}
