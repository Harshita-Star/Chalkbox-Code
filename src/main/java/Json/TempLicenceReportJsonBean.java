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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.MessagePackInfo;
@ManagedBean(name="tempLicenceReportJson")
@ViewScoped
public class TempLicenceReportJsonBean implements Serializable
{
	ArrayList<MessagePackInfo> schoolList = new ArrayList<>();
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DataBaseMethodsReports DBR=new DataBaseMethodsReports();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	
	public TempLicenceReportJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			FacesContext.getCurrentInstance().
			getExternalContext().getRequestParameterMap();
			JSONArray jarray = new JSONArray();

			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				schoolList=DBR.tempLicenceReport(conn);
				for(MessagePackInfo mm:schoolList)
				{
					JSONObject jobj = new JSONObject();
					jobj.put("schoolId", mm.getSchoolId());
					jobj.put("schoolName", mm.getSchoolName());
					jobj.put("Session", mm.getSession());
					jobj.put("StrLicencedate", mm.getStrLicDate());
					jobj.put("StrUpdateDate", mm.getStrUpdateDate());
					jarray.add(jobj);
				}
			}
			
			json=jarray.toJSONString();
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
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}

	public ArrayList<MessagePackInfo> getSchoolList() {
		return schoolList;
	}


	public void setSchoolList(ArrayList<MessagePackInfo> schoolList) {
		this.schoolList = schoolList;
	}


}
