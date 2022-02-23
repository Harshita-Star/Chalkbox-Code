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

@ManagedBean(name="messageWindowJson")
@ViewScoped

public class MessageWindowJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

	public MessageWindowJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();


			String schid = params.get("schid");
			String username = params.get("id");
			String usertype = params.get("usertype");
			String mob = params.get("mob");
			String type = params.get("type");
			
			
			
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(type==null || type.equalsIgnoreCase(""))
				{
					type="sms";
				}
				
				//// // System.out.println(type);

				if(type.equalsIgnoreCase("sms"))
				{
					/*if(!usertype.equalsIgnoreCase("student"))
					{
						username = DBJ.employeeIdbyEmployeeUsernameName(username, schid, conn);
					}*/

					ArrayList<MessageInfo> newsList=DBJ.allMessageList(usertype,username,mob,schid,conn);


					for(MessageInfo nn : newsList)
					{
						JSONObject obj = new JSONObject();

						obj.put("msg", nn.getMessage());
						obj.put("msg_date", nn.getDatetime());


						arr.add(obj);
					}
				}
				else
				{
					String notification1="",notification2="",notification3="",notification4="";

					if(usertype.equalsIgnoreCase("student"))
					{
						StudentInfo	selectedStudent=DBJ.studentDetailslistByAddNo(username,schid,conn);
						String classid=selectedStudent.getClassId();
						String	selectedSection=selectedStudent.getSectionid();
						notification1=classid+"-"+schid;
						notification2=selectedSection+"-"+classid+"-"+schid;
						notification3=String.valueOf(selectedStudent.getFathersPhone())+"-"+username+"-"+schid;
						notification4=schid;

					}
					else if(usertype.equalsIgnoreCase("admin"))
					{
						notification1="admin-"+schid;
					}
					else
					{
						notification1="staff-"+username+"-"+schid;
					}

					ArrayList<MessageInfo>list=DBJ.smsNotificationMethod(notification1,notification2,notification3,notification4,schid,usertype,conn);

					for(MessageInfo ss:list)
					{
						JSONObject obj = new JSONObject();

						obj.put("msg", ss.getMessage());
						obj.put("msg_date", ss.getDatetime());

						arr.add(obj);
					}
				}
			}

			json=arr.toJSONString();
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

	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();
	}
}
