package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.NotificationInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="GalleryJsonBean")
@ViewScoped
public class GalleryJsonBean implements Serializable
{


	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public GalleryJsonBean(){
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schoolid=params.get("Schoolid");
			String classid=params.get("classid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(classid==null || classid.equalsIgnoreCase("") || classid.equalsIgnoreCase("0"))
				{
					classid = "All";
				}

				ArrayList<NotificationInfo> info=DBJ.gallary(schoolid,conn,classid);
							Collections.sort(info, new MyRollNoComp());

				SchoolInfoList list=DBJ.fullSchoolInfo(schoolid,conn);

				for(NotificationInfo ss:info)
				{
					JSONObject obj = new JSONObject();
					obj.put("images", ss.getMessage());
					obj.put("url",list.getDownloadpath());
					obj.put("tag",ss.getFilename());
					obj.put("tagId",ss.getId());
					obj.put("date", ss.getStrUdate());
					obj.put("classid", ss.getClassid());
					obj.put("classname", ss.getClassName());
					arr.add(obj);
				}
			}

			
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
	
	
	class MyRollNoComp implements Comparator<NotificationInfo>
	{
		@Override
		public int compare(NotificationInfo e1, NotificationInfo e2)
		{
			
			Date d1=null;
			Date d2=null;
			try {
				d1 = new SimpleDateFormat("dd/MM/yyyy").parse(e1.getStrUdate());
			    d2=new SimpleDateFormat("dd/MM/yyyy").parse(e2.getStrUdate());
				
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			
			
			if (d1 == null || d2 == null)
		        return 0;
		      return d2.compareTo(d1);
			
			
			
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
