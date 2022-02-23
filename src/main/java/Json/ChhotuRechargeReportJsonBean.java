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
import schooldata.MessagePackInfo;

@ManagedBean(name="chhotuRechargeReportJson")
@ViewScoped
public class ChhotuRechargeReportJsonBean implements Serializable
{
	String json;
	String sectionid,message;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ChhotuRechargeReportJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			ArrayList<MessagePackInfo> schoolList = new ArrayList<>();

			//String SchoolId=params.get("Schoolid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				schoolList=DBM.selectChhotuReport(conn);

				for(MessagePackInfo ls2:schoolList)
				{
					JSONObject obj2 = new JSONObject();

					obj2.put("Pack_Name",ls2.getPackName());
					obj2.put("SchoolId", ls2.getSchoolId());
					obj2.put("SchoolName", ls2.getSchoolName());
					obj2.put("date", ls2.getDate());
					arr.add(obj2);
				}
			}

			json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

	}


	public String getJson() {
		return json;
	}


	public void setJson(String json) {
		this.json = json;
	}


	public String getSectionid() {
		return sectionid;
	}


	public void setSectionid(String sectionid) {
		this.sectionid = sectionid;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
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
