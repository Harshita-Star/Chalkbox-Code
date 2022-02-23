package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
@ManagedBean(name="unreadNotification")
@ViewScoped
public class UnreadNotificationAndPermission implements Serializable
{
	String json;
	String sectionid,message;
	String AddmissionNo,schoolid;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public UnreadNotificationAndPermission()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			AddmissionNo=params.get("addNumber");
			schoolid=params.get("Schoolid");
			String type=params.get("type");
			String list="0";

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(type==null || type.equals(""))
				{
					type="admin";
				}

				if(type.equalsIgnoreCase("student"))
				{
					String notification1="",notification2="",notification3="",notification4="";
					DBJ.fullSchoolInfo(schoolid, conn);
					StudentInfo	selectedStudent=DBJ.studentDetailslistByAddNo(AddmissionNo,schoolid,conn);
					String classid=selectedStudent.getClassId();
					String	selectedSection=selectedStudent.getSectionid();
					notification1=classid+"-"+schoolid;
					notification2=selectedSection+"-"+classid+"-"+schoolid;
					notification3=String.valueOf(selectedStudent.getFathersPhone())+"-"+AddmissionNo+"-"+schoolid;
					notification4=schoolid;

					list=DBJ.allNotificationCount(notification1, notification2, notification3, notification4, schoolid, conn);

				}
				else if(type.equalsIgnoreCase("admin"))
				{
					String notification1="admin-"+schoolid;
					notification1="admin-"+schoolid;
					list=DBJ.allAdminNotificationCount(notification1,schoolid, conn);
				}
				else
				{
					String notification1="staff-"+AddmissionNo+"-"+schoolid;
					list=DBJ.allAdminNotificationCount(notification1,schoolid, conn);
				}
			}

			json=list;
			//+":"+ls.getStudent_app_permission();
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
