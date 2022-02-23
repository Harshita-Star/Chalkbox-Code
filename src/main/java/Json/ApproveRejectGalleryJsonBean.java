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
import schooldata.NotificationInfo;

@ManagedBean(name="approveRejectGalleryJson")
@ViewScoped

public class ApproveRejectGalleryJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	
	public ApproveRejectGalleryJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schoolid=params.get("schid");
			String id=params.get("id");
			String status=params.get("status");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block

			JSONArray ary=new JSONArray();
			JSONObject obj = new JSONObject();
			
			String sts = "failed";
			
			if(checkRequest)
			{
				
				ArrayList<String> clist = new ArrayList<>();
				
				if(id.contains(","))
				{
					String arr[] = id.split(",");
					int j=0;
					String galname="";
					for(int i=0;i<arr.length;i++)
					{
						NotificationInfo info = DBJ.pendingGalleryInfo(arr[i],conn);
						DBJ.updateGalleryStatus(arr[i],status,conn);
						if(status.equalsIgnoreCase("approved"))
						{
							galname = info.getFilename()+"@CB@"+info.getClassid();
							
							if(info.getType().equalsIgnoreCase("new"))
							{
								j=DBJ.addGalleryImages(info.getMessage(), info.getFilename(), info.getSchid(), conn, info.getClassid(),"no");
								DBJ.updatePendingGalleryTypeAndId(info.getFilename(),info.getSchid(),info.getClassid(),j,"old",conn);
							}
							else
							{
								j=DBJ.updateGalleryImages(info.getMessage(), info.getFilename(), info.getSchid(), conn, info.getTagid(), info.getClassid(), "no");
							}
							
							if(!clist.contains(galname))
							{
								clist.add(galname);
							}
						}
					}
					
					if(status.equalsIgnoreCase("approved"))
					{
						String tag = "", cls = "";
						for(String ss : clist)
						{
							String notarr[] = ss.split("@CB@");
							tag = notarr[0];
							cls = notarr[1];
							if(cls.equalsIgnoreCase("All"))
							{
								DBJ.notification("Gallery",tag + "\n" + "Some New Images Added In Your Gallery",schoolid,schoolid,"",conn);
							}
							else
							{
								DBJ.notification("Gallery",tag + "\n" + "Some New Images Added In Your Gallery",cls+"-"+schoolid,schoolid,"",conn);
							}
						}
					}
				}
				else
				{
					NotificationInfo info = DBJ.pendingGalleryInfo(id,conn);
					DBJ.updateGalleryStatus(id,status,conn);
					if(status.equalsIgnoreCase("approved"))
					{
						if(info.getType().equalsIgnoreCase("new"))
						{
							int j=DBJ.addGalleryImages(info.getMessage(), info.getFilename(), schoolid, conn, info.getClassid(),"yes");
							DBJ.updatePendingGalleryTypeAndId(info.getFilename(),schoolid,info.getClassid(),j,"old",conn);
						}
						else
						{
							DBJ.updateGalleryImages(info.getMessage(), info.getFilename(), schoolid, conn, info.getTagid(), info.getClassid(), "yes");
						}
					}
				}
				
				sts="success";
				
				
			}
			
			obj.put("status",sts);
			ary.add(obj);
			json=ary.toJSONString();
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
