package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
@ManagedBean(name="viewSchoolfeedback")
@ViewScoped
public class viewSchoolFeebackBean implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public viewSchoolFeebackBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid=params.get("id");
			String school=params.get("Schoolid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list =DBJ.SchoolTotalfeedback(studentid,school,conn);
				SchoolInfoList ls=DBJ.fullSchoolInfo(school,conn);
				
				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("Date", new SimpleDateFormat("dd-MMM-yyyy").format(ss.getDate()));
					obj.put("review", ss.getDescription());
					obj.put("studentname", ss.getStudentName());
					obj.put("status", ss.getStatus());
					obj.put("remark", ss.getRemark());
					obj.put("id", String.valueOf(ss.getId()));
					if(ss.getSignImage().equals(""))
					{
						obj.put("file","");

					}
					else
					{
						obj.put("file",ls.getDownloadpath()+ss.getSignImage());

					}
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
