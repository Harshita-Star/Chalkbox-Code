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

@ManagedBean(name="subjectforTeacher")
@ViewScoped
public class SubjectForTeacherJSon implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public SubjectForTeacherJSon()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String id = params.get("id");
			String schid=params.get("Schoolid");
			String username=params.get("username");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String empid=DBJ.employeeIdbyEmployeeName(username,schid,conn);
				ArrayList<SelectItem>list=DBJ.AllEMployeeSubject(empid,id,schid,conn);
				String sectionid="select id",sectionname="Select Subject";
				for(SelectItem ss:list)
				{
					sectionid=sectionid+","+ss.getValue();
					sectionname=sectionname+","+ss.getLabel();
				}

				JSONObject obj = new JSONObject();
				obj.put("SubjectName", sectionname);
				obj.put("Subjectid", sectionid);

				arr.add(obj);
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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

