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
@ManagedBean(name="allSchoolSmsReportJson")
@ViewScoped
public class AllSchoolSmsReportJsonBean implements Serializable
{
	String json="";
	ArrayList<SchoolInfo>dataList=new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM= new DatabaseMethods1();
	public AllSchoolSmsReportJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String session = params.get("session");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();

			if(checkRequest)
			{
				dataList=DBM.allSchoolSMSChalkbox(session,conn);
				for(SchoolInfo ss:dataList)
				{
					JSONObject obj = new JSONObject();
					obj.put("id", ss.getId());
					obj.put("SchoolName", ss.getSchoolName());
					obj.put("Status",ss.getStatus());
					obj.put("ContactNo", ss.getContactNo());
					obj.put("Credit", ss.getCredit());
					obj.put("Debit", ss.getDebit());
					obj.put("Balance",ss.getBalance());
					obj.put("SenderId",ss.getSenderid());
					obj.put("SmsRemind",ss.getSmsRemind());
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
	public ArrayList<SchoolInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<SchoolInfo> dataList) {
		this.dataList = dataList;
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




}
