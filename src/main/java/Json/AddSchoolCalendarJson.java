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

import org.json.simple.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="addSchoolCalendarJson")
@ViewScoped
public class AddSchoolCalendarJson implements Serializable
{
	ArrayList<SelectItem>eventList;
	String json="",eventValue="",eventName="";
	SimpleDateFormat ss=new SimpleDateFormat("dd/MM/yyyy");
	DatabaseMethods1 dbm=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public AddSchoolCalendarJson()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid = params.get("Schoolid");
			String date=params.get("date");
			String endDate=params.get("endDate");
			String desc=params.get("description");
			String startTime=params.get("startTime");
			String endTime=params.get("endTime");
			String status=params.get("eventStatus");
			String category=params.get("category");
			String holidayFor=params.get("eventFor");
			String classSection=params.get("classes");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONObject obj = new JSONObject();
			
			if(checkRequest)
			{
				if(endDate==null)
				{
					endDate=new SimpleDateFormat("dd/MM/yyyy").format(new Date())	;
				}
				
				if(holidayFor == null || holidayFor.equalsIgnoreCase(""))
				{
					holidayFor="all";
				}
				
				ArrayList<ClassInfo> selectedClassList = new ArrayList<>();
				if(holidayFor.equalsIgnoreCase("particular"))
				{
					
					String clsSecArr[] = classSection.split(",");
					String cls = "";
					String sec = "";
					for(int x=0;x<clsSecArr.length;x++)
					{
						String tempArr[] = clsSecArr[x].split("-");
						cls = tempArr[0];
						sec = tempArr[1];
						ClassInfo cc = new ClassInfo();
						cc.setClassid(cls);
						cc.setSectionId(sec);
						selectedClassList.add(cc);
					}
				}
				
				if(selectedClassList.size()<=0)
				{
					holidayFor="all";
				}

				Date dt=null;
				Date end_Date=null;
				try {
					dt=ss.parse(date);
					end_Date=ss.parse(endDate);


				} catch (ParseException e) {
					e.printStackTrace();
				}

				ArrayList<Date> dates = new ArrayList<>();
				long interval = 24*1000 * 60 * 60; // 1 hour in millis
				long endTime1 =end_Date.getTime() ; // create your endtime here, possibly using Calendar or Date
				long curTime = dt.getTime();
				while (curTime <= endTime1) {
					dates.add(new Date(curTime));
					curTime += interval;
				}
				int j=0;
				for(int i=0;i<dates.size();i++){
					Date lDate =dates.get(i);
					j =DBJ.addEvent(schid,lDate,desc,startTime,endTime,status,category,holidayFor,conn);
					if(j>0)
					{
						if(category.equalsIgnoreCase("Holiday") && holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
						{
							String strdt = new SimpleDateFormat("yyyy-MM-dd").format(dates.get(i));
							dbm.addClassHoliday(selectedClassList,String.valueOf(j),schid,strdt,conn);
						}
						else if(!category.equalsIgnoreCase("Holiday") && holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
						{
							String strdt = new SimpleDateFormat("yyyy-MM-dd").format(dates.get(i));
							dbm.addClassEvents(selectedClassList,String.valueOf(j),schid,strdt,conn);
						}
					}
					
				}

				
				if(j>0)
				{
					ArrayList<String> messageSetting=DBJ.checkmessageSetting(conn,schid);
					String notify=messageSetting.get(6);
					if(notify.equals("true"))
					{
						String message = "";
						String showDt = date.replaceAll("/", "-");
						String showEndDt = endDate.replaceAll("/", "-");
						String eventTime = "";
						String eventDate = "";

						if(status.equalsIgnoreCase("FullDay"))
						{
							eventTime = "Full Day";
						}
						else
						{
							eventTime = startTime+" to "+endTime;
						}

						if(showDt.equalsIgnoreCase(showEndDt))
						{
							eventDate = showDt;
						}
						else
						{
							eventDate = "From "+showDt+" to "+showEndDt;
						}

						message = category.toUpperCase()+"\n"+"Date : "+eventDate+"\nTime : "+eventTime+"\n"+desc;
						
						if(holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
						{
							for(ClassInfo cc : selectedClassList)
							{
								DBJ.notification("School Calendar",message,cc.getSectionId()+"-"+cc.getClassid()+"-"+schid,schid,"",conn);
							}
						}
						else
						{
							DBJ.notification("School Calendar",message,schid,schid,"",conn);
						}
					}
					
					/*if(!category.equalsIgnoreCase("Exam"))
					{
						message = category.toUpperCase()+"\n"+"Date : "+eventDate+"\nTime : "+eventTime+"\n"+desc;
						DBJ.notification("School Calendar",message,schid,schid,"",conn);
					}*/

					obj.put("Msg", "School Calendar Added Successfully");
				}
				else
				{
					obj.put("Msg", "Error Occured");
				}
			}
			else
			{
				obj.put("Msg", "Error Occured");
			}

			json=obj.toJSONString();

		} catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
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
