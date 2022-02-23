package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="viewLectureAssigmentBeanJson")
@ViewScoped
public class ViewLectureAssigmentBeanJson implements Serializable
{
	
	
ArrayList<StudentInfo>list=new ArrayList<>();
    
	StudentInfo selected;
	ArrayList<StudentInfo> imageList;
	SchoolInfoList lst=null;
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	
    public ViewLectureAssigmentBeanJson() {
    	
		Connection conn=DataBaseConnection.javaConnection();
		try {
			DatabaseMethods1 obj1=new DatabaseMethods1();
	 		
	 		Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			
			String schoolid=params.get("Schoolid");
			String lectureId = params.get("lectureId");
		 	
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
		
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();
			
	 		if(checkRequest)
	 		{
	 			 lst=obj1.fullSchoolInfo(schoolid, conn);
	 	 		
	 			list=obj1.allLactureAssignment(lectureId,schoolid,conn);
	 			for(StudentInfo ls:list)
	 			{
	 				JSONObject obj = new JSONObject();
	 				obj.put("userid", ls.getAddNumber());
	 				obj.put("studentName",ls.getName());
	 				obj.put("fatherName",ls.getFatherName());
	 				obj.put("className",ls.getClassName());
	 				obj.put("image", ls.getLactureImage());
	 				obj.put("upload_date", new SimpleDateFormat("dd/MM/yyyy").format(ls.getDate()));
	 				obj.put("desc", ls.getDescription());
	 				obj.put("path",lst.getDownloadpath());
	 				arr.put(obj);
	 	 				
	 			}
	 			
	 			
	 			
	 		}
	 		json=arr.toString();
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
}
