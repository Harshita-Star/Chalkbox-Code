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

import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ManagedBean(name="updatephoneNo")
@ViewScoped
public class UpdatePhoneNoJSon implements Serializable
{

	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public UpdatePhoneNoJSon(){
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String name=params.get("name");
			String fname=params.get("fname");
			String mName=params.get("mname");
			String dob=params.get("dob");

			String phoneno = params.get("phoneno");
			String schoolid=params.get("Schoolid");

			int i=0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				i=new DataBaseMeathodJson().updatePhoneNo(studentid,phoneno,schoolid,fname,mName,name,dob,conn);
			}
			
			JSONObject mainobj = new JSONObject();

			if(i>0)
			{
				mainobj.put("SchoolJson", "update");
			}
			else
			{
				mainobj.put("SchoolJson", "not update");
			}

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
