package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import schooldata.SchoolCalenderInfo;
import schooldata.StudentInfo;

@ManagedBean(name="schoolCalendraJsonAndroid")
@ViewScoped
public class SchoolCalendraJsonForAndroid implements Serializable{
	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public SchoolCalendraJsonForAndroid()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schoolid=params.get("Schoolid");
			String year=params.get("year");
			String month=params.get("month");
			String type=params.get("type");
			String sectionid=params.get("sectionid");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(type==null || type.equalsIgnoreCase(""))
				{
					type="admin";
				}
				
				if(sectionid==null || sectionid.equalsIgnoreCase(""))
				{
					sectionid="";
				}

				ArrayList<SchoolCalenderInfo>list=DBJ.viewEventListAll(schoolid,year,month,type,sectionid,conn);

				for(SchoolCalenderInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("date",new SimpleDateFormat("yyyy-MM-dd").format(ss.getDate()));
					obj.put("month", ss.getDate().getMonth()+1);
					obj.put("day", ss.getDate().getDate());
					obj.put("year", ss.getDate().getYear()+1900);

					//	 JSONObject obj1 = new JSONObject();
					obj.put("Type", ss.getEvent().equalsIgnoreCase("P.T.M") ? "PTM" : ss.getEvent());
					obj.put("desc", ss.getDesc());
					if(ss.getType().equalsIgnoreCase("Timing"))
					{
						obj.put("Time",ss.getHr()+" To "+ss.getMin());
					}
					else
					{
						obj.put("Time","");
					}
					obj.put("id",ss.getEventid());
					obj.put("eventid", String.valueOf(ss.getId()));
					obj.put("eventfor", ss.getHolidayFor());

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
