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

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;

@ViewScoped
@ManagedBean(name="AttendaceListDateWiseJson")
public class AttendaceListDateWiseJson implements Serializable
{


	String json;
	ArrayList<ClassInfo> classSection;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public AttendaceListDateWiseJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId=params.get("Schoolid");
			String date=	params.get("date");

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			boolean check=true;
			if(checkRequest)
			{
				
				Date d=null;
				try {
					d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				
				if(d.getDay()==0)
				{
					json="holiday";
				}
				else
				{
					String strDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
					check=DBJ.checkSectionHolidayForAttendanceJSON(strDate, conn,schId,"");
					if(check==false)
					{
						json=DBJ.sectionAttendance(d,schId,conn);
					}
					else
					{
						json="holiday";
					}

				}
			}
			else
			{
				json="holiday";
			}
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
