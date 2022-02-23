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
@ManagedBean(name="schoolcalendra")
@ViewScoped
public class SchoolCalendraJson implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public SchoolCalendraJson()
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

				ArrayList<SchoolCalenderInfo>list=DBJ.viewEventListGroupBy(schoolid,year,month,conn);

				for(SchoolCalenderInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("date",new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
					obj.put("month", ss.getDate().getMonth()+1);
					obj.put("day", ss.getDate().getDate());
					obj.put("year", ss.getDate().getYear()+1900);

					ArrayList<SchoolCalenderInfo>list1=DBJ.viewEventListByDate(ss.getDate(),schoolid,type,sectionid,conn);
					JSONArray arr2=new JSONArray();
					if(list1.size()>0)
					{
						for(SchoolCalenderInfo ss1:list1)
						{

							JSONObject obj1 = new JSONObject();
							obj1.put("Type", ss1.getEvent());
							obj1.put("desc", ss1.getDesc());
							obj.put("eventfor", ss1.getHolidayFor());
							if(ss1.getEvent().equalsIgnoreCase("Timing"))
							{

								obj1.put("Time",ss1.getHr()+":"+ss1.getMin());

							}
							else
							{
								obj1.put("Time","Full Day");

							}
							obj1.put("id",ss1.getEventid());

							arr2.add(obj1);

						}
						obj.put("details", arr2);

						arr.add(obj);
					}
				}
			}
			else
			{
				JSONObject obj = new JSONObject();
				JSONArray arr2=new JSONArray();
				obj.put("details", arr2);

				arr.add(obj);
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
