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
@ManagedBean(name="senderIdJson")
@ViewScoped
public class SenderIdJsonBean implements Serializable
{
	String json="";
	ArrayList<SchoolInfo> dataList;
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DataBaseMeathodJson obj=new DataBaseMeathodJson();

	public SenderIdJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			FacesContext.getCurrentInstance().
			getExternalContext().getRequestParameterMap();

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = obj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				dataList=obj1.selectAllDataListFromSchoolInfoTable(conn,"school");
				for(SchoolInfo ss:dataList)
				{
					JSONObject obj = new JSONObject();

					obj.put("id", ss.getId());
					obj.put("schoolName", ss.getSchoolName());
					obj.put("expiredatestr", ss.getExpireDateStr());
					obj.put("status", ss.getStatus());
					obj.put("contactNo", ss.getContactNo());
					obj.put("userName",ss.getUsername());
					obj.put("password",ss.getPassword());
					obj.put("lastLogin",ss.getLastLogin());
					obj.put("senderid", ss.getSenderid());

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
