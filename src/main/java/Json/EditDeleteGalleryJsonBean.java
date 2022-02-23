package Json;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import schooldata.SchoolInfoList;

@ManagedBean(name="editDeleteGalleryJson")
@ViewScoped


public class EditDeleteGalleryJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson obj1=new DataBaseMeathodJson();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public EditDeleteGalleryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String id=params.get("id");
			String name=params.get("name");

			String status = "";
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			int i = 0 ;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String schoolid = obj1.schoolIdByGalleryId(id,conn);

				SchoolInfoList info = obj1.fullSchoolInfo(schoolid,conn);

				if(name.equalsIgnoreCase("delete this gallery"))
				{
					String oldImages = obj1.galleryImagesByTagId(id,conn);
					i=obj1.deleteGallery(id,conn);

					if(oldImages.contains(","))
					{
						String oldArr[] = oldImages.split(",");
						for(int x=0;x<oldArr.length;x++)
						{
							try
							{
								Files.deleteIfExists(Paths.get(info.getUploadpath()+oldArr[x]));
							}
							catch(Exception e)
							{
								//not deleted from server
							}
						}
					}
					else if(!oldImages.equals(""))
					{
						try
						{
							Files.deleteIfExists(Paths.get(info.getUploadpath()+oldImages));
						}
						catch(Exception e)
						{
							//not deleted from server
						}
					}

				}
				else
				{
					i=obj1.editGalleryName(id,name,conn);
				}
			}

			if(i>=1)
			{
				status="success";
			}
			else
			{
				status="error";
			}
			mainobj.put("status", status);
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
}
