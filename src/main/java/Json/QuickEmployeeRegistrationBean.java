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

@ManagedBean(name="quickEmployeeRegistrationBean")
@ViewScoped
public class QuickEmployeeRegistrationBean implements Serializable
{
	String json="";
	String employeename="",employeeid="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public QuickEmployeeRegistrationBean() {

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String name=params.get("name");
			String username=params.get("username");
			String category=params.get("category");
			String gender=params.get("gender");
			String mobile=params.get("mobile");
			String schoolid=params.get("Schoolid");
			String pwd=params.get("password");

			JSONObject obj = new JSONObject();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i=DBJ.employeecheckUserName(conn,username);
				if(i==1)
				{
					obj.put("msg","UserName Not Valid Please Choose Another");
				}
				else
				{
					int j=DBJ.employeeRegistration(schoolid, conn,name,username,category,gender,mobile);
					if(j>0)
					{
						String categoryname=DBJ.employeeCategoryById(category, schoolid,conn);
						DBJ.addUserName(username, pwd,categoryname, schoolid,conn);
						obj.put("msg","Employee Register Successfully");
					}
					else
					{
						obj.put("msg","Error Occur please try Again");
					}
				}
			}
			else
			{
				obj.put("msg","Error Occur please try Again");
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
