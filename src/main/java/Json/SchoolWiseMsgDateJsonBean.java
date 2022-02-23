package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.MessageHistory;
@ManagedBean(name="schoolWiseMsgDateJson")
@ViewScoped
public class SchoolWiseMsgDateJsonBean implements Serializable
{
	String json="";
	ArrayList<MessageHistory>allDetails=new ArrayList<>();
	ArrayList<SelectItem> schoolList = new ArrayList<>();
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public SchoolWiseMsgDateJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();



		try 
         {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String dateStr = params.get("date"); //dd/MM/yyyy
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<SelectItem> schoolList = obj1.getAllSchool(conn);

				Date newDate=null;
				Date toDate=null;
				try {
					newDate= new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
					toDate= new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);

				} catch (ParseException e) {
					e.printStackTrace();
				}
				schoolList = obj1.getAllSchool(conn);
				allDetails = obj1.messageDebitWiseSchool(schoolList,newDate,toDate,conn);

				for(MessageHistory ss:allDetails)
				{
					JSONObject obj = new JSONObject();
					obj.put("description", ss.getDescription());
					if(ss.getDebit()==null || ss.getDebit().equalsIgnoreCase(""))
					{
						obj.put("debit", "0");
					}
					else
					{
						obj.put("Debit", ss.getDebit());
					}

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
