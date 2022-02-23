package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

@ManagedBean(name="classRfidReportJson")
@ViewScoped

public class ClassRfidReportJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	
	public ClassRfidReportJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			
			String schid=params.get("schid");
			String classid=params.get("classid");
			String sectionid=params.get("sectionid");
			String curdate=params.get("date");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date dd = new Date();
				
				try 
				{
					dd = new SimpleDateFormat("dd/MM/yyyy").parse(curdate);
				}
				catch (Exception e) 
				{
					dd = new Date();
				}
				
				String strdate = new SimpleDateFormat("yyyy-MM-dd").format(dd);
				
				String session=DBM.selectedSessionDetails(schid, conn);
				ArrayList<StudentInfo> slist=new ArrayList<>();
				ArrayList<String> list=new DataBaseMethodStudent().basicFieldsForStudentList();
				slist.addAll(new DataBaseMethodStudent().studentDetail("",sectionid, classid, QueryConstants.BY_CLASS_SECTION,QueryConstants.RFID_TRANSPORT, null, null,strdate,"","","", session, schid, list, conn));
				slist.addAll(new DataBaseMethodStudent().studentDetail("",sectionid, classid, QueryConstants.BY_CLASS_SECTION,QueryConstants.RFID_NO_TRANSPORT, null, null,strdate,"","","", session, schid, list, conn));
				
					
				for(StudentInfo ss : slist)
				{
					JSONObject obj = new JSONObject();
					
					obj.put("name",ss.getFullName());
					obj.put("classname", ss.getClassName()+" - "+ss.getSectionName());
					obj.put("pickhome",ss.getRfidDataInfo().getInBusMorn());
					obj.put("dropschool", ss.getRfidDataInfo().getOutBusMorn());
					obj.put("inschool", ss.getRfidDataInfo().getInSchool());
					obj.put("outschool", ss.getRfidDataInfo().getOutSchool());
					obj.put("pickschool", ss.getRfidDataInfo().getInBusEven());
					obj.put("drophome", ss.getRfidDataInfo().getOutBusEven());
					
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
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
