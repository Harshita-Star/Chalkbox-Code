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
import schooldata.SchoolInfoList;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="viewStudentPhotoListJson")
@ViewScoped
public class ViewStudentPhotoListJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewStudentPhotoListJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			DataBaseOnlineAdm obj=new DataBaseOnlineAdm();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid = params.get("schid");
			String studentid = params.get("studentid");

			JSONArray arr1=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList info =DBJ.fullSchoolInfo(schid, conn);

				JSONObject mainobj1 = new JSONObject();
				JSONObject mainobj2 = new JSONObject();
				JSONObject mainobj3 = new JSONObject();
				
				String photos=obj.selectPhotosByStudentId(conn,studentid,schid);
				if(photos==null || photos.equals(""))
				{

				}
				else
				{
					String arr[]=photos.split(",");
					mainobj1.put("type", "student");
					mainobj1.put("photo", info.getDownloadpath()+arr[0]);
					mainobj1.put("status", arr[3]);
					mainobj1.put("remark", arr[6]);

					mainobj2.put("type", "father");
					mainobj2.put("photo", info.getDownloadpath()+arr[1]);
					mainobj2.put("status", arr[4]);
					mainobj2.put("remark", arr[7]);

					mainobj3.put("type", "mother");
					mainobj3.put("photo",info.getDownloadpath()+ arr[2]);
					mainobj3.put("status", arr[5]);
					mainobj3.put("remark", arr[8]);
					/* sPhoto=arr[0];
		  				  fPhoto=arr[1];
		  				  mPhoto=arr[2];
		  				  sStatus=arr[3];
		  				  fStatus=arr[4];
		  				  mStatus=arr[5];*/
					arr1.add(mainobj1);
					arr1.add(mainobj2);
					arr1.add(mainobj3);
				}
			}

			json=arr1.toJSONString();
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
