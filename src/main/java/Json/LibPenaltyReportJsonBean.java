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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import library_module.BookInfo;
import library_module.DataBaseMethodsLibraryModule;
import schooldata.DataBaseConnection;

@ManagedBean(name="libPenaltyReportJson")
@ViewScoped

public class LibPenaltyReportJsonBean implements Serializable
{
	String json;
	Date startDate,endDate;
	ArrayList<BookInfo> bookList;
	SimpleDateFormat ss=new SimpleDateFormat("dd/MM/yyyy");
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public LibPenaltyReportJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schid");
			String sDate = params.get("startDate");
			String eDate = params.get("endDate");

			try {
				startDate=ss.parse(sDate);
				endDate=ss.parse(eDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			//// // System.out.println("Lib Json User Agent : "+userAgent);
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				bookList=new DataBaseMethodsLibraryModule().libraryFeeCollectionReport(schId,startDate,endDate,conn);
				for(BookInfo ss:bookList)
				{
					JSONObject obj = new JSONObject();
					obj.put("type",ss.getType());
					obj.put("name", ss.getStudentName());
					obj.put("classname",ss.getClassSection());
					obj.put("date", ss.getReceiveDateStr());
					obj.put("penalty",ss.getPenaltyAmount());
					obj.put("damage", ss.getDamageAmount());
					obj.put("discount", ss.getDiscount());
					

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
}
