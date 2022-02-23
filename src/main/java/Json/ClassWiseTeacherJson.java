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
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ManagedBean(name="ClassWiseTeacherJson")
@ViewScoped
public class ClassWiseTeacherJson implements Serializable {


	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ClassWiseTeacherJson()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("Schoolid");
			String section=params.get("section");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				StudentInfo	selectedStudent=DBJ.studentDetailslistByAddNo(section,schid,conn);
				String	selectedSection=selectedStudent.getSectionid();
				
				ArrayList<SelectItem>list=DBJ.AllEployeeSubjectForTeacherNysection(selectedSection,schid,conn);
				for(SelectItem ss:list)
				{

					new JSONObject();

					JSONObject obj = new JSONObject();

					obj.put("teachername", ss.getLabel());
					obj.put("subject", ss.getValue());
					obj.put("img", ss.getDescription());
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}

}
