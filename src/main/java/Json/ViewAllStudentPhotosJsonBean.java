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
import student_module.DataBaseOnlineAdm;
import student_module.StudentPhotoInfo;

@ManagedBean(name="viewAllStudentPhotosJson")
@ViewScoped

public class ViewAllStudentPhotosJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewAllStudentPhotosJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			DataBaseOnlineAdm dbo = new DataBaseOnlineAdm();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid = params.get("schid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList info =DBJ.fullSchoolInfo(schid, conn);

				ArrayList<StudentPhotoInfo> pendingList = new ArrayList<>();
				pendingList=dbo.selectAllPendingStudentPhotos(conn,schid);

				if(pendingList.size()>0)
				{
					for(StudentPhotoInfo ss : pendingList)
					{
						JSONObject obj = new JSONObject();

						obj.put("studentid", ss.getStudentId());
						obj.put("studentname", ss.getStudentName());
						obj.put("classname", ss.getClassName());
						obj.put("s_photo", info.getDownloadpath()+ss.getsPhoto());
						obj.put("f_photo", info.getDownloadpath()+ss.getfPhoto());
						obj.put("m_photo", info.getDownloadpath()+ss.getmPhoto());
						obj.put("s_status", ss.getsStatus());
						obj.put("f_status", ss.getfStatus());
						obj.put("m_status", ss.getmStatus());
						obj.put("s_show", ss.isShowSphoto());
						obj.put("f_show", ss.isShowFphoto());
						obj.put("m_show", ss.isShowMphoto());

						arr.add(obj);
					}
				}
			}

			json = arr.toJSONString();
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
