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
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
@ManagedBean(name="viewHoeWorkBean")
@ViewScoped
public class ViewHomeWorkBean implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection,selectedSection="",subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewHomeWorkBean() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String date = params.get("date");
			String schid=params.get("Schoolid");
			String type=params.get("usertype");
			String offcet=params.get("offset");
			String count=params.get("count");
			String off=params.get("off");
			Date newDate=null;
			try {
				newDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(type==null)
				{
					selectedStudent=DBJ.studentDetailslistByAddNo(studentid, schid,conn);
					selectedSection=selectedStudent.getSectionid();
					selectedCLassSection=selectedStudent.getClassId();
					list=DBJ.viewAssignmentjson(selectedSection,newDate,schid,conn,selectedCLassSection,offcet,count,off);
				}
				else
				{
					if(type.equalsIgnoreCase("admin")
							|| type.equalsIgnoreCase("academic coordinator")
							|| type.equalsIgnoreCase("principal")
							|| type.equalsIgnoreCase("vice principal")
							|| type.equalsIgnoreCase("authority")
							|| type.equalsIgnoreCase("Accounts"))
					{
						list=DBJ.viewAssignmentjson("",newDate,schid,conn,"",offcet,count,off);
					}
					else
					{
						selectedStudent=DBJ.studentDetailslistByAddNo(studentid, schid,conn);
						selectedSection=selectedStudent.getSectionid();
						selectedCLassSection=selectedStudent.getClassId();
						list=DBJ.viewAssignmentjson(selectedSection,newDate,schid,conn,selectedCLassSection,offcet,count,off);
					}
				}

				SchoolInfoList list1=DBJ.fullSchoolInfo(schid,conn);

				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					boolean check=false;
					if(ss.getSubjectStudied().equalsIgnoreCase("all"))
					{
						check=true;
						obj.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
						obj.put("Subject", ss.getAss3());
						obj.put("desc", ss.getDescription());
						obj.put("images", ss.getAss1());
						obj.put("path",list1.getDownloadpath());
						obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
					}
					else
					{
						String subjectDetails=DBJ.subjectNameAndTypeFromidwithoutStatus(ss.getSubjectStudied(), schid, conn);

						String[] temp=subjectDetails.split(",");
						if(temp[1].equalsIgnoreCase("Mandatory"))
						{
							check=true;
							obj.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
							obj.put("Subject", ss.getAss3());
							obj.put("desc", ss.getDescription());
							obj.put("images", ss.getAss1());
							obj.put("path",list1.getDownloadpath());
							obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
						}
						else
						{
							check=DBJ.checkOptionalDetails(studentid,ss.getSubjectStudied(),schid,conn);
							obj.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
							obj.put("Subject", ss.getAss3());
							obj.put("desc", ss.getDescription());
							obj.put("images", ss.getAss1());
							obj.put("path",list1.getDownloadpath());
							obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
						}
					}

					if(type==null)
					{
						if(check==true)
						{
							arr.add(obj);
						}
					}
					else
					{
						if(type.equalsIgnoreCase("admin")
								|| type.equalsIgnoreCase("academic coordinator")
								|| type.equalsIgnoreCase("principal")
								|| type.equalsIgnoreCase("vice principal")
								|| type.equalsIgnoreCase("authority")
								|| type.equalsIgnoreCase("Accounts"))
						{
							arr.add(obj);
						}
						else
						{
							if(check==true)
							{
								arr.add(obj);
							}
						}
					}
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
