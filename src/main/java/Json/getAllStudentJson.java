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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.Category;
import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="allstudentjson")
@ViewScoped
public class getAllStudentJson implements Serializable
{
	String json;
	ArrayList<ClassInfo> classSection;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public getAllStudentJson()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schid=params.get("Schoolid");
			String type=params.get("type");
			String username=params.get("username");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			if(checkRequest)
			{
				if(type.equalsIgnoreCase("admin")
						|| type.equalsIgnoreCase("authority")
						|| type.equalsIgnoreCase("principal")
						|| type.equalsIgnoreCase("vice principal")
						|| type.equalsIgnoreCase("front office")
						|| type.equalsIgnoreCase("office staff") 
						|| type.equalsIgnoreCase("Librarian")
						|| type.equalsIgnoreCase("Driver")
						|| type.equalsIgnoreCase("Conductor")
						|| type.equalsIgnoreCase("Attendant")
						|| type.equalsIgnoreCase("Security")
						|| type.equalsIgnoreCase("Tranporter")
						|| type.equalsIgnoreCase("Office Staff")
						|| type.equalsIgnoreCase("Accounts")
						|| type.equalsIgnoreCase("Sports Department")
						|| type.equalsIgnoreCase("Others"))
				{
					classSection=DBJ.allClassList(schid,"","Admin",conn);
				}
				else if(type.equalsIgnoreCase("academic coordinator"))
				{
					String empid=DBJ.employeeIdbyEmployeeName(username, schid,conn);
					classSection=DBJ.allClassList(schid,empid,"academic coordinator",conn);
				}
				else
				{
					String empid=DBJ.employeeIdbyEmployeeName(username, schid,conn);
					classSection=DBJ.allClassList(schid,empid,type,conn);
				}
				SchoolInfoList list=DBJ.fullSchoolInfo(schid, conn);
				
				for(ClassInfo ls:classSection)
				{
					JSONObject obj = new JSONObject();
					obj.put("ClassName",ls.getClassName());
					obj.put("ClassID", ls.getGroupid());
					JSONArray arr1=new JSONArray();
					for(Category ls1:ls.getCategoryList())
					{
						JSONObject obj1 = new JSONObject();
						obj1.put("Section_name",ls1.getCategory()+" Total Students -"+ls1.getList().size());
						obj1.put("Section_Id", ls1.getId());
						JSONArray arr2=new JSONArray();

						for(StudentInfo ls2:ls1.getList())
						{
							JSONObject obj2 = new JSONObject();

							obj2.put("Student_Name",ls2.getFullName());
							obj2.put("Fathers_Name", ls2.getFname());
							obj2.put("Fathers_Phone", ls2.getFathersPhone());
							obj2.put("Mothers_Phone", ls2.getMothersPhone());
							obj2.put("Student_Phone", ls2.getStudentPhone());
							obj2.put("Add_Number", ls2.getAddNumber());
							obj2.put("sr_no", ls2.getSrNo());
							obj2.put("url", list.getDownloadpath());
							obj2.put("path", ls2.getStudent_image());
							obj2.put("rollno", ls2.getRollNo());
							if(ls2.getStudent_image()==null || ls2.getStudent_image().equals("") || ls2.getStudent_image().equalsIgnoreCase("null"))
							{
								obj2.put("image", list.getDownloadpath()+ls2.getStudent_image());
							}
							else
							{
								obj2.put("image", list.getDownloadpath()+ls2.getStudent_image());
							}


							arr2.add(obj2);

						}


						obj1.put("Students", arr2);

						arr1.add(obj1);

					}
					obj.put("sections",arr1);

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
