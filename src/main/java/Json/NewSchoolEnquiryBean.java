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

import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;

@ManagedBean(name="newSchoolEnquiry")
@ViewScoped
public class NewSchoolEnquiryBean implements Serializable
{

	String json="";
	String employeename="",employeeid="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public NewSchoolEnquiryBean() {

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schoolname=params.get("schoolname");
			String contactperson=params.get("contactperson");
			String degination=params.get("degination");
			String address=params.get("address");
			String mobile=params.get("mobile");
			
			JSONObject obj = new JSONObject();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i=DBJ.addSchoolEnquiry(schoolname,contactperson,degination,address,mobile,conn);
				if(i>0)
				{
					
					obj.put("msg","1");
				}
				else
				{
					obj.put("msg","0");
				}
			}
			

			json=obj.toJSONString();
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
