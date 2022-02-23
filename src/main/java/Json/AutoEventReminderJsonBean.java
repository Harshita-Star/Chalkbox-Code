package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolCalenderInfo;
import schooldata.SchoolInfo;
import trigger_module.DataBaseMethods;

@ManagedBean(name="autoEventReminderJsonBean")
@ViewScoped
public class AutoEventReminderJsonBean implements Serializable
{
	
	String json="";
	DatabaseMethods1 obj = new DatabaseMethods1();
	DataBaseMethods DBM = new DataBaseMethods();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
   public AutoEventReminderJsonBean() 
   {
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Date tdt = new Date();

			Date tmrDt = new Date();
			tmrDt.setDate(tdt.getDate()+1);

			String strDate = new SimpleDateFormat("yyyy-MM-dd").format(tmrDt);
			String showDt = new SimpleDateFormat("dd-MM-yyyy").format(tmrDt);
			ArrayList<SchoolInfo> dataList=new ArrayList<>();
			ArrayList<ClassInfo> classSectionList=new ArrayList<>();
			dataList=obj.allSchoolListForEventReminder(conn);
			if(dataList.size()>0)
			{
				ArrayList<SchoolCalenderInfo> eventlist = new ArrayList<>();
				String message = "";
				for(SchoolInfo ss : dataList)
				{
					ArrayList<String> messageSetting=DBJ.checkmessageSetting(conn,ss.getId());
					String notify=messageSetting.get(6);
					if(notify.equals("true"))
					{
//						if(ss.getId().equals("3"))
						//				{
						eventlist = DBM.viewEventList(ss.getId(), strDate,"trigger", conn);
						if(eventlist.size()>0)
						{
							for(SchoolCalenderInfo cc : eventlist)
							{
								message = cc.getEvent().toUpperCase()+"\n"+"Date : "+showDt+"\nTime : "+cc.getEventTime()+"\n"+cc.getDesc();
								if(cc.getHolidayFor().equalsIgnoreCase("particular"))
								{
									classSectionList=new DataBaseMeathodJson().allClassSectionListForEvent(cc.getEvent(),ss.getId(),String.valueOf(cc.getId()),conn);
									for(ClassInfo ci : classSectionList)
									{
										DBJ.notification("Reminder",message,ci.getSectionId()+"-"+ci.getClassid()+"-"+ss.getId(),ss.getId(),"",conn);
									}
								}
								else
								{
									DBJ.notification("Reminder",message,ss.getId(),ss.getId(),"",conn);
								}
								
							}
						}
						//}
					}
				}
			}

			json="done";
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
	
}
