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

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
@ManagedBean(name="viewinstimetable")
@ViewScoped
public class View_INS_Time_Table_Json implements Serializable	{

	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	ArrayList<SubjectInfo> list;
	public View_INS_Time_Table_Json(){

		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String classid = params.get("class_id");
			String sectionid = params.get("section_id");
			String schoolid=params.get("Schoolid");
			
			String file="";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				file	=DBJ.viewInsTimeTable(classid,sectionid,schoolid,conn);
			}
			json=file;
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
