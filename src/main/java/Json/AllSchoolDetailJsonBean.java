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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;
@ManagedBean(name="viewEditDeleteSchoolJson")
@ViewScoped
public class AllSchoolDetailJsonBean implements Serializable
{
	ArrayList<SchoolInfo> dataList = new ArrayList<>();
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM= new DatabaseMethods1();

	public AllSchoolDetailJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			FacesContext.getCurrentInstance().
			getExternalContext().getRequestParameterMap();
			JSONArray jarray = new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block

			
			
			if(checkRequest)
			{
				dataList=DBM.allSchoolListChalkbox("All",conn);
				for(SchoolInfo mm:dataList)
				{
					JSONObject jobj = new JSONObject();

					jobj.put("schoolName", mm.getSchoolName());
					jobj.put("ContactNo", mm.getContactNo());
					jobj.put("Username", mm.getUsername());
					jobj.put("Password", mm.getPassword());
					jobj.put("ExpireDateStr", mm.getExpireDateStr());
					jobj.put("LastLogin", mm.getLastLogin());
					jobj.put("schoolId", mm.getId());


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
	public ArrayList<SchoolInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<SchoolInfo> dataList) {
		this.dataList = dataList;
	}




}
