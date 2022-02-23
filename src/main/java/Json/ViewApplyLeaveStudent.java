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
@ManagedBean(name="viewApplyLeaveStudent")
@ViewScoped
public class ViewApplyLeaveStudent implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewApplyLeaveStudent() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schoolId=params.get("Schoolid");
			String studentid=params.get("studentid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<ApplyLeaveJson> list=DBJ.viewStudentLeaveforStudent(studentid,schoolId,conn);
				
				for(ApplyLeaveJson ls:list)
				{
					//StudentInfo lss=DBJ.studentDetailslistByAddNo(ls.getStudentid(),schoolId,conn);
					JSONObject obj = new JSONObject();

					obj.put("sname",ls.getFname());
					obj.put("class",ls.getClassName()+"-"+ls.getSectionName());
					obj.put("sdate",ls.getStartDate() );
					obj.put("edate", ls.getEndDate());
					obj.put("reason",ls.getReason());
					obj.put("remark", ls.getRemark());
					if(ls.getStatus().equals("0"))
					{
						obj.put("status","Pending");

					}
					else if(ls.getStatus().equals("1"))
					{
						obj.put("status","Approved");

					}
					else
					{
						obj.put("status","Denied");
					}
					obj.put("id",ls.getId());

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
