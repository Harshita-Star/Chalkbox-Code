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
import schooldata.DatabaseMethods1;

@ManagedBean(name="updateVideoGalleryLinksJson")
@ViewScoped

public class UpadteVideoGalleryLinksJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public UpadteVideoGalleryLinksJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schid");
			String tagId=params.get("tagid");
			String values=params.get("value");
			JSONArray arr=new JSONArray();
			JSONObject mainobj=new JSONObject();
			DatabaseMethods1 DBM = new DatabaseMethods1();
			/*String arr1[]=values.split(",");
			ArrayList<galleryInfo>list=new ArrayList<>();
			list=new ArrayList<>();
			for(int i=0;i>=arr1.length;i++)
			{
				galleryInfo ss=new galleryInfo();
				ss.setVideoLink(arr1[i]);
				list.add(ss);
			}
			String linkValues="";
			for(galleryInfo ss:list)
			{
				if(linkValues.equals(""))
				{
					linkValues=ss.getVideoLink();
				}
				else
				{
					linkValues=linkValues+","+ss.getVideoLink();
				}
			}*/
			String status="";
			int i=0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			String type="Delete";
			if(checkRequest)
			{
				
				
				if(values==null || values.equals(""))
				{
					i=DBM.deletedTagName(conn,schId,tagId);
				}
				else
				{
					String video=DBM.selectAllLinksById(conn, tagId, schId);
					String[] previousFiles=video.split(",");
					String[] newvideofiles=values.split(",");
					if(previousFiles.length<newvideofiles.length)
					{
						type="updated";
					}
					
					i=DBM.updateLinksAtId(conn,tagId,values,schId);
					
				}
				
			}
			if(i>=1)
			{
				status="Links updated successfully";
				if(type.equalsIgnoreCase("updated"))
				{
					String tagName = DBM.tagNameById(tagId, conn);
					DBJ.notification("Video Gallery",tagName + "\n" + "Some New Videos Are Added In Your Video Gallery",schId,schId,"",conn);

				}
				
			}
			else
			{
				status="Not updated";
			}


			mainobj.put("Status", status);

			arr.add(mainobj);


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

	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}



}
