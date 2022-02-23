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

import schooldata.ComplaintInfo;
import schooldata.DataBaseConnection;
@ManagedBean(name="studentBehaviourJsonBean")
@ViewScoped
public class StudentBehaviourJsonBean implements Serializable {



	ArrayList<ComplaintInfo> complaintList = new ArrayList<>();
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public StudentBehaviourJsonBean()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String addmissionNumber = params.get("studentid");
			String schid=params.get("schid");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				complaintList = DBJ.studentComplaintDiary(addmissionNumber, "","all",schid, conn);
				for(ComplaintInfo in:complaintList)
				{

					JSONObject obj = new JSONObject();
					obj.put("type", in.getType());
					obj.put("date", in.getStrDate());
					obj.put("Description", in.getDescription());
					obj.put("added_by", in.getComplaintBy());
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
