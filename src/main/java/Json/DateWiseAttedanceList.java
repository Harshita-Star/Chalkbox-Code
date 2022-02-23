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

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ManagedBean(name="attendanceJsonBean")
@ViewScoped
public class DateWiseAttedanceList implements Serializable
{

	String json;
	ArrayList<ClassInfo> classSection;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public DateWiseAttedanceList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		

	

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId=params.get("Schoolid");
			String date = params.get("date");

			String usertype=params.get("usertype");
			String userid=params.get("userid");

			JSONArray arr2=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			//boolean checkRequest = new DataBaseMeathodJson().checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			boolean checkRequest=true;
			if(checkRequest)
			{
				Date d=null;
				try {
					d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
				} catch (ParseException e) {
					d=new Date();
				}
				
				if(usertype==null||usertype.equals(""))
				{
					usertype="admin";
				}
				
				String section="";
				if(usertype.equalsIgnoreCase("admin")|| usertype.equalsIgnoreCase("principal")
						|| usertype.equalsIgnoreCase("vice principal")
						|| usertype.equalsIgnoreCase("authority"))
				{
					 section="";
				}
				else if(usertype.equalsIgnoreCase("academic coordinator"))
				{
					String tid=DBJ.teacherid(userid, schId, conn);
					ArrayList<String> list = DBJ.cordinatorSectionList(tid,schId,conn);
					for(String term:list)
					{
				    	section+=term+"','";
					}
				    section=section.substring(0,section.lastIndexOf("','"));
				}
				else
				{
					String tid=DBJ.teacherid(userid, schId, conn);
				    ArrayList<String>list=DBJ.classTeacherSectionlist(tid,schId,conn);
				    for(String term:list)
					{
				    	section+=term+"','";
					}
				    section=section.substring(0,section.lastIndexOf("','"));
					
				}
				ArrayList<StudentInfo>list=DBJ.allbasentAttendance(d,schId,section,usertype,conn);
				
				String student = "";
				if(list.size()>0)
				{
					for(StudentInfo ss : list)
					{
						
						if(student.equals(""))
						{
							student = ss.getAddNumber();
						}
						else
						{
							student = student + "','" + ss.getAddNumber();
						}
					}
				}


				String dt = new SimpleDateFormat("yyyy-MM-dd").format(d);
				ArrayList<StudentInfo> leavelist=DBJ.checkAppliedLeaveDateWise(student,dt,schId,conn);
				ArrayList<StudentInfo> tempList = new ArrayList<>();
				String checkAtt = "";
				for(StudentInfo ll : leavelist)
				{
					checkAtt = DBJ.checkClassAttendanceOnDate(ll.getSectionid(),dt, schId, conn);
					if(checkAtt.equalsIgnoreCase("no"))
					{
						tempList.add(ll);
					}
				}

				ArrayList<StudentInfo> newlist = new ArrayList<>();
				newlist.addAll(list);
				newlist.addAll(tempList);

				for(StudentInfo ls2:newlist)
				{
					JSONObject obj2 = new JSONObject();
					obj2.put("Add_Number", ls2.getAddNumber());
					obj2.put("status", ls2.getAttendance());
					obj2.put("messagestatus", ls2.getMessagesend());
					obj2.put("clname", ls2.getClassName());
					obj2.put("scname", ls2.getCategory());
					obj2.put("scid", ls2.getId());
					obj2.put("name",ls2.getFullName());
					obj2.put("pno",ls2.getFathersPhone());
					arr2.add(obj2);
				}
			}

			json=arr2.toJSONString();
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
