package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="consentStatusChangeJson")
@ViewScoped
public class ConsentStatusChangeJsonBean implements Serializable
{
	String json;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ConsentStatusChangeJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String id = params.get("id");
			String remark = params.get("remark");
			String status = params.get("status");
			String schid = params.get("schid");
			String studentid = params.get("studentid");
			String type = params.get("type");
			String appdate = params.get("appdate");
			String apptime = params.get("apptime");

			JSONArray arr=new JSONArray();

			String value="Error Occur!";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i=obj.updateStatusByAdmin(conn,id,status,remark,studentid,"consent_table");

				
				if(i>0)
				{
					value="Status Updated Successfully!";

					StudentInfo info=DBJ.studentDetailslistByAddNo(studentid,schid,conn);


					String msg="";
					if(status.equalsIgnoreCase("approved"))
					{
						if(type.equalsIgnoreCase("Consent"))
						{
							msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has approved the consent. Please open the app to check the details.";
						}
						else
						{
							msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has accepted the meeting request for date "+appdate+ " "+apptime+ ". Please open the app to check the details.";
						}
						DBJ.adminnotification(type,msg,"admin-"+schid,schid,type+"Approve-"+studentid,conn);

					}
					else if(status.equalsIgnoreCase("rejected"))
					{
						if(type.equalsIgnoreCase("Consent"))
						{
							msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has rejected the consent. Please open the app to check the details.";
						}
						else
						{
							msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has rejected the meeting request for date "+appdate+ " "+apptime+ ". Please open the app to check the details.";
						}
						DBJ.adminnotification(type,msg,"admin-"+schid,schid,type+"Reject-"+studentid,conn);

					}

				}
				else
				{
					value="Error Occur!";
				}
			}

			JSONObject obj = new JSONObject();
			obj.put("status",value);

			arr.add(obj);

			// mainobj.put("SchoolJson", arr);

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
