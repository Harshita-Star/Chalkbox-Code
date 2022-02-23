package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
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
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="studentListByMobileJSON")
@ViewScoped

public class StudentListByMobileJSONBean implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudentTemp, selectedAss;
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public StudentListByMobileJSONBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("id");
			String schid = params.get("Schoolid");
			String type = params.get("type");
			if(type==null || type.equals(""))
			{
				type="normal";
			}

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = new DataBaseMeathodJson().checkRequestAuthenticity(userAgent); // If true, proceed else block
			String session=DBM.selectedSessionDetails(schid,conn);
			DataBaseMethodStudent objStd=new DataBaseMethodStudent();
			if(checkRequest)
			{
				ArrayList<StudentInfo>  selectedStudent = new ArrayList<>();
				if(type.equalsIgnoreCase("normal"))
				{
					ArrayList<String> list=objStd.basicFieldsForStudentList();
					selectedStudent=objStd.studentDetail(studentid,"","",QueryConstants.MOBILE_NO,QueryConstants.MOBILE_NO,null,null,"","","","",session, schid, list, conn);
				}
				else if(type.equalsIgnoreCase("common_student"))
				{
					ArrayList<String> list=objStd.basicFieldsForStudentList();
					StudentInfo stinfo=objStd.studentDetail(studentid,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","",session, schid, list, conn).get(0);
					if(stinfo!=null)
					{
						String fathersMobile = String.valueOf(stinfo.getFathersPhone());
						if(fathersMobile.length()==10 
								&& !fathersMobile.equals("2222222222")
								&& !fathersMobile.equals("9999999999")
								&& !fathersMobile.equals("1111111111")
								&& !fathersMobile.equals("1234567890")
								&& !fathersMobile.equals("0123456789"))
						{
							selectedStudent=objStd.studentDetail(fathersMobile,"","",QueryConstants.MOBILE_NO,QueryConstants.MOBILE_NO,null,null,"","","","",session, schid, list, conn);
						}
					}
				}
				

				if(selectedStudent.size()==0)
				{

					//obj.put("otp","0");
					obj.put("studentid","");
					obj.put("schid","");
					obj.put("name","");
					obj.put("classname","");
					obj.put("upd", "");
					arr.add(obj);

					mainobj.put("SchoolJson", arr);

					json=mainobj.toJSONString();

				}
				else
				{
					DBJ.fullSchoolInfo(schid,conn);

					String name="",stutid="",classname="",pwd="",upwd="";
					for(StudentInfo ss:selectedStudent)
					{
						if(type.equalsIgnoreCase("common_student"))
						{
							String studentcheck=DBM.authenticationCheck(ss.getAddNumber(), conn);
							if(!studentcheck.equals(""))
							{
								pwd = DBJ.userPassword(ss.getAddNumber(), conn);
								
								if(name.equals(""))
								{
									name=ss.getFname();
									stutid=ss.getAddNumber();
									classname=ss.getClassName();
									upwd=pwd;
								}
								else
								{
									name=name+","+ss.getFname();
									stutid=stutid+","+ss.getAddNumber();
									classname=classname+","+ss.getClassName();
									upwd=upwd+","+pwd;
								}
							}
						}
						else
						{
							pwd = DBJ.userPassword(ss.getAddNumber(), conn);
							
							if(name.equals(""))
							{
								name=ss.getFname();
								stutid=ss.getAddNumber();
								classname=ss.getClassName();
								upwd=pwd;
							}
							else
							{
								name=name+","+ss.getFname();
								stutid=stutid+","+ss.getAddNumber();
								classname=classname+","+ss.getClassName();
								upwd=upwd+","+pwd;
							}
				
						}
					}


					obj.put("studentid", stutid);
					obj.put("upd", upwd);
					obj.put("schid",selectedStudent.get(0).getSchid());
					obj.put("name",name);
					obj.put("classname",classname);

					arr.add(obj);
					mainobj.put("SchoolJson", arr);
	                
					
						json=mainobj.toJSONString();
										
					
				}
			}
			else
			{
				obj.put("studentid","");
				obj.put("upd", "");
				obj.put("schid","");
				obj.put("name","");
				obj.put("classname","");

				arr.add(obj);

				mainobj.put("SchoolJson", arr);

				json=mainobj.toJSONString();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
