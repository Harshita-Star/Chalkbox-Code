package Json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import sun.misc.BASE64Decoder;
@ManagedBean(name="broadcastingMessageJson")
@ViewScoped
public class BroadcastingMessageJson implements Serializable
{
	String json,groupId,message,type,messageId,schid,studentId;
	int j=0;	
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public BroadcastingMessageJson()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			groupId=params.get("groupid");
			message=params.get("msg");
			type=params.get("type");
			messageId=groupId+type+studentId+message+(int)(Math.random()*9000)+1000;
			schid=params.get("Schoolid");
			studentId=params.get("studentId");
			String image1=params.get("image");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int rendomNumber=0;
				String name="";
				if(!image1.equals(""))
				{

					byte[] imageByte = null;
					BASE64Decoder decoder = new BASE64Decoder();
					try {
						imageByte = decoder.decodeBuffer(image1);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);


					rendomNumber=(int)(Math.random()*9000)+1000;
					name="broadcast"+String.valueOf(rendomNumber)+".jpg";
					DBJ.makeScanPath11(bis,name,schid,conn);

				}

				SchoolInfoList ls=DBJ.fullSchoolInfo(schid, conn);
				String groupname=DBJ.getGroupName(groupId,schid,conn);
				if(type.equals("admin"))
				{
					String studentId=DBJ.getGroupStudent(groupId,schid,"student",conn);
					String[] student=studentId.split(",");
					for(int i=0;i<student.length;i++)
					{
						StudentInfo info=DBJ.studentDetailslistByAddNo(student[i],schid,conn);

						j=DBJ.insertBroadcastDetails(groupId, message,type, messageId, schid, student[i],name,conn);
						DBJ.notification("Broadcast",groupname+"-New Message Received",info.getFathersPhone()+"-"+student[i]+"-"+schid,schid,"",conn);
					}

				}
				else
				{
					j=DBJ.insertBroadcastDetails(groupId, message,type, messageId, schid, studentId,name,conn);
					String tp = "GroupStudentBroadcast-"+groupId;
					DBJ.adminnotification("Broadcast",groupname+"-New Message Received","admin-"+schid,schid,tp,conn);

				}
				/*
						JSONObject mainobj = new JSONObject();
						 JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();
						if(i>=1)
						{
				 */		json= "Message Send Successfully";
				 /*	}
						else
						{
							obj.put("Msg", "Error Occured");
						}
				  *///	json=obj.toJSONString();
			}
			else
			{
				json= "Something Went Wrong";
			}
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

	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}


}
