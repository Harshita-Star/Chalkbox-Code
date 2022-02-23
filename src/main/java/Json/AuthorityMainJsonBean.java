package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="authorityMainJson")
@ViewScoped

public class AuthorityMainJsonBean implements Serializable
{
	String json="";
	ArrayList<ClassInfo> classList,allClassList;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public AuthorityMainJsonBean()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			classList = new ArrayList<>();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schoolid");
			String session=DBM.selectedSessionDetails(schid, conn);
			
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();
			
			if(checkRequest)
			{
				boolean checkAtt,check;
				Date d=new  Date();
				int year1=d.getYear()+1900;
				String selectYear=String.valueOf(year1);
				String absentStudent=DBM.allNewStudentEnquirycount(schid, selectYear,conn);
				String leaveStudent=DBM.allLeaveStudent(schid,selectYear,conn);
				String messageStduent=DBM.todayMessage(schid, conn);
				String totalCollection=DBM.todaysCollection(schid,"authJson",conn);

				String strDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				allClassList=DBJ.schoolSectionList(schid,conn);
				if(allClassList.size()>0)
				{
					for(ClassInfo cc : allClassList)
					{
						checkAtt = DBM.checkWhetherAttendanceMarked(cc.getSectionId(),strDate,schid,conn);
						if(checkAtt==false)
						{
							check = DBJ.checkSectionHolidayForAttendanceJSON(strDate, conn, schid, cc.getSectionId());
							if(check==false)
							{

								classList.add(cc);
							}
						}
					}
				}

				String unmarked=String.valueOf(classList.size());
				String newAdm = DBM.allStudentcount(schid,"newAddmission","",session,"",conn);
				String pendingEnq = DBJ.totalPendingEnquiry(schid,conn);

				JSONObject obj = new JSONObject();

				obj.put("collection", totalCollection);
				obj.put("sms", messageStduent);
				obj.put("leave", leaveStudent);
				obj.put("absent", absentStudent);
				obj.put("unmark", unmarked);
				obj.put("newAdm", newAdm);
				obj.put("pending", pendingEnq);
				arr.put(obj);
			}

			json=arr.toString();
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
