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
import schooldata.MessageHistory;
@ManagedBean(name="dashboardLibraryJson")
@ViewScoped
public class DashboardLibraryJsonBean implements Serializable
{
	String json="";
	ArrayList<MessageHistory>allDetails=new ArrayList<>();
	ArrayList<SelectItem> schoolList = new ArrayList<>();
	DataBaseMeathodJson obj1=new DataBaseMeathodJson();

	public DashboardLibraryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId = params.get("schoolId");

			String allBooks,allAssigned,allOverdue,penalty;
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = obj1.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				allBooks = obj1.allBooks(schId,conn);
				allAssigned=obj1.allAssigned(schId,conn);
				allOverdue=obj1.allOverdue(schId,conn);
				penalty=obj1.allPenalty(schId,conn);
				mainobj.put("books", allBooks);
				mainobj.put("assigned", allAssigned);
				mainobj.put("overdue", allOverdue);
				mainobj.put("penalty", penalty);

				arr.add(mainobj);
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
	public ArrayList<MessageHistory> getAllDetails() {
		return allDetails;
	}
	public void setAllDetails(ArrayList<MessageHistory> allDetails) {
		this.allDetails = allDetails;
	}
	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}




}
