package Json;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;

@ManagedBean(name="deleteGalleryImageJson")
@ViewScoped

public class DeleteGalleryImageJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	
	public DeleteGalleryImageJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String imageList = params.get("imageList");
			String schoolid = params.get("Schoolid");
			String tagId = params.get("tagid");

			int i=0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList info = DBJ.fullSchoolInfo(schoolid,conn);
				String oldImages = DBJ.galleryImagesByTagId(tagId, conn);

				if(imageList.equals("") || imageList==null)
				{
					i=DBJ.deleteGallery(tagId,conn);
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
					i=DBJ.deleteGalleryImages(imageList,schoolid,conn,tagId);
					if(imageList.contains(","))
					{
						String newArr[] = imageList.split(",");
						String oldArr[] = oldImages.split(",");
						for(int x = 0; x < oldArr.length; x++)
						{
							if(!ArrayUtils.contains(newArr, oldArr[x]))
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

					}
					else
					{
						if(oldImages.contains(","))
						{
							String oldArr[] = oldImages.split(",");
							for(int x = 0; x < oldArr.length; x++)
							{
								if(!oldArr[x].equals(imageList))
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
						}
					}
				}
			}

			JSONObject obj = new JSONObject();
			JSONArray arr=new JSONArray();
			String status="";
			if(i>0)
			{
				status="Gallery Updated Successfully";
			}
			else
			{
				status="not updated";
			}

			////// // System.out.println(status);
			obj.put("status",status);

			arr.add(obj);

			json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
		try {
			conn.close();
		} catch (Exception e2) {
			// TODO: handle exception
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
