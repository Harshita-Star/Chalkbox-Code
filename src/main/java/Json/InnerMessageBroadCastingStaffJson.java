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
import schooldata.SchoolInfoList;
@ManagedBean(name="innermessageBroadStaff")
@ViewScoped

public class InnerMessageBroadCastingStaffJson implements Serializable {


	String json;
	ArrayList<MessageInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public InnerMessageBroadCastingStaffJson()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schid=params.get("Schoolid");
			String groupid=params.get("groupid");
			String studentid=params.get("studentid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list=DBJ.viewInnerBroadcastMessageSatff(schid,groupid,studentid,conn);
				if(list.size()>0)
				{
					JSONArray arr=new JSONArray();
					SchoolInfoList ls1=DBJ.fullSchoolInfo(schid,conn);

					for(MessageInfo ll:list)
					{
						JSONObject obj = new JSONObject();
						obj.put("id", ll.getId());
						obj.put("groupid",ll.getGroupId());
						obj.put("type",ll.getType());
						obj.put("time", ll.getCtime()+"\n"+ll.getCdate());
						obj.put("schid", ll.getSchid());
						if(ll.getType().equals("Staff"))
						{

							String ls=DBJ.employeeNameByUsername(ll.getStudentId(),ll.getSchid(),conn);
							obj.put("from",ls);

						}
						else{
							obj.put("from", ls1.getSchoolName());
						}
						if(ll.getImagepath().equals(""))
						{
							obj.put("message", ll.getMessage());

							obj.put("image",ll.getImagepath());
						}
						else
						{
							obj.put("message","");

							obj.put("image",ls1.getDownloadpath()+ll.getImagepath());
						}
						arr.add(obj);
					}
					json=arr.toJSONString();
				}
				else
				{
					JSONArray arr=new JSONArray();
					json=arr.toJSONString();
				}
			}
			else
			{
				JSONArray arr=new JSONArray();
				json=arr.toJSONString();
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

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}


}
