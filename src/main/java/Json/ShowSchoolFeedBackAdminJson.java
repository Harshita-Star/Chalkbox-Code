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

@ManagedBean(name="showSchoolFeedBackAdminJson")
@ViewScoped
public class ShowSchoolFeedBackAdminJson implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ShowSchoolFeedBackAdminJson() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid=params.get("status");
			String schid=params.get("Schoolid");
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list =DBJ.SchoolTotalfeedbackByStatusAll(studentid,schid,conn);
				SchoolInfoList ls=DBJ.fullSchoolInfo(schid,conn);

				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("id",ss.getId());
					obj.put("Date", new SimpleDateFormat("dd-MMM-yyyy").format(ss.getDate()));
					obj.put("classs", ss.getClassName());

					obj.put("review", ss.getDescription());
					obj.put("studentname", ss.getStudentName());
					obj.put("status", ss.getStatus());
					obj.put("remark", ss.getRemark());
					if(ss.getSignImage()==null || ss.getSignImage().equals(""))
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
			
			mainobj.put("SchoolJson", arr);
			json=mainobj.toJSONString();
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
