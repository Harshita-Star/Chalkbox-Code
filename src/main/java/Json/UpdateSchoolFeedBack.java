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

@ManagedBean(name="updateschoolfeedback")
@ViewScoped
public class UpdateSchoolFeedBack implements Serializable {


	String json;
	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public UpdateSchoolFeedBack() {

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String status=params.get("status");
			String id=params.get("id");
			String remark=params.get("remark");
			String schoolid=params.get("Schoolid");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i =DBJ.UpdateSchoolTotalfeedbackByStatus(id,status,schoolid,remark,conn);

				if(i>0)
				{
					String addNumber=DBJ.getFeedbackStudent(id,schoolid,conn);

					StudentInfo info=DBJ.studentDetailslistByAddNo(addNumber,schoolid,conn);

					if(status.equalsIgnoreCase("approve"))
					{
						DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been Responded", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolid,schoolid,"",conn);
						DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been Responded", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolid,schoolid,"",conn);

					}
					else
					{
						DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been  Disapproved", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolid,schoolid,"",conn);
						DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been  Disapproved", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolid,schoolid,"",conn);

					}


					JSONObject obj = new JSONObject();
					obj.put("status","update");
					arr.add(obj);

				}
				else
				{
					JSONObject obj = new JSONObject();
					obj.put("status","not update");
					arr.add(obj);

				}
			}
			else
			{
				JSONObject obj = new JSONObject();
				obj.put("status","not update");
				arr.add(obj);

			}
			
			mainobj.put("SchoolJson", arr);
			json=mainobj.toJSONString();
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
