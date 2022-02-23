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

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ViewScoped
@ManagedBean(name="viewApplyAdminJson")
public class viewApplyAdminJson implements Serializable{

	String json;

	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public viewApplyAdminJson() {

		Connection conn=DataBaseConnection.javaConnection();
		try {

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schoolId=params.get("Schoolid");
			String username =params.get("username");
			String usertype=params.get("usertype");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<ApplyLeaveJson> list=new ArrayList<>();
				if(usertype==null&&username==null)
				{
					list=DBJ.viewStudentLeave(schoolId,conn);
				}
				else
				{
					if(usertype.equalsIgnoreCase("admin"))
					{
						list=DBJ.viewStudentLeave(schoolId,conn);
					}
					else if(usertype.equalsIgnoreCase("academic coordinator"))
					{
						String empid=DBJ.employeeIdbyEmployeeName(username, schoolId,conn);
						ArrayList<String> list1 = DBJ.cordinatorSectionList(empid,schoolId,conn);
						list=DBJ.viewStudentLeaveForTeacher(schoolId,list1,conn);
					}
					else
					{
						String empid=DBJ.employeeIdbyEmployeeName(username, schoolId,conn);
						ArrayList<String>list1=DBJ.allClassListForTeacher(schoolId,empid,conn);
						list=DBJ.viewStudentLeaveForTeacher(schoolId,list1,conn);
					}
				}
				
				for(ApplyLeaveJson ls:list)
				{
					//	StudentInfo lss=DBJ.studentDetailslistByAddNo(ls.getStudentid(),schoolId,conn);
					JSONObject obj = new JSONObject();
					if(ls.getFname()!=null)
					{
						obj.put("sname",ls.getFname());
					}
					else
					{
						obj.put("sname","NA");
					}

					if(ls.getFname()!=null)
					{
						obj.put("classname",ls.getClassName()+"-"+ls.getSectionName());
					}
					else
					{
						obj.put("classname","NA");
					}

					obj.put("sdate",ls.getStartDate() );
					obj.put("edate", ls.getEndDate());
					obj.put("reason",ls.getReason());
					obj.put("status",ls.getStatus());
					obj.put("remark", ls.getRemark());
					obj.put("apply_date",ls.getApplydate());
					obj.put("id",ls.getId());
					obj.put("actionBy", ls.getActionBy());
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
