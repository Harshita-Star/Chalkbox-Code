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

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.AboutUsInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="contactJson")
@ViewScoped
public class ContactUsJson implements Serializable
{
	String principalMessage,chairManMessage,schoolmeassage,emailId,contactNo,website,json;
	ArrayList<AboutUsInfo> informationList=new ArrayList<>();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public ContactUsJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schoolid=params.get("Schoolid");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				DBJ.fullSchoolInfo(schoolid,conn);
				informationList =DBM.informationOfContactUs(schoolid,conn);
				
				for(AboutUsInfo ls:informationList)
				{
					JSONObject obj = new JSONObject();
					obj.put("contactNo",ls.getContactNum());
					obj.put("email",ls.getEmailid());
					obj.put("website",ls.getWebsite());
					arr.put(obj);
				}
			}

			json=arr.toString();
			/*if(informationList.size()==0){


					String aboutUs=DBM.insertcontactDetails(contactNo,emailId,website,schoolid,conn);
				if(aboutUs.equals("i")){
					 JSONObject mainobj = new JSONObject();
					 JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();

					      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
					      obj.put("type", "student");
					      mainobj.put("SchoolJson", arr);

					      json=mainobj.toJSONString();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ADD DETAIL SUCCESSFULLY"));
					contactNo="";
					emailId="";
					website="";
					informationList =DBM.informationOfContactUs(schoolid,conn);

				}
				else{
					 JSONObject mainobj = new JSONObject();
					 JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();

					      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
					      obj.put("type", "student");
					      mainobj.put("SchoolJson", arr);
					      json=mainobj.toJSONString();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error occured try again !!"));
				}
				}
				else{
					 String aboutUs=DBM.updatecontactDetails(contactNo,emailId,website,schoolid,conn);
						if(aboutUs.equals("i")){
							 JSONObject mainobj = new JSONObject();
							 JSONArray arr=new JSONArray();

								JSONObject obj = new JSONObject();

							      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
							      obj.put("type", "student");
							      mainobj.put("SchoolJson", arr);
							      json=mainobj.toJSONString();
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ADD DETAIL SUCCESSFULLY"));
							contactNo="";
							emailId="";
							website="";
							informationList =DBM.informationOfContactUs(schoolid,conn);
						}
						else{
							 JSONObject mainobj = new JSONObject();
							 JSONArray arr=new JSONArray();

								JSONObject obj = new JSONObject();

							      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
							      obj.put("type", "student");
							      mainobj.put("SchoolJson", arr);
							      json=mainobj.toJSONString();
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error occured try again !!"));
						}
				}*/

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
	public String getPrincipalMessage() {
		return principalMessage;
	}
	public void setPrincipalMessage(String principalMessage) {
		this.principalMessage = principalMessage;
	}
	public String getChairManMessage() {
		return chairManMessage;
	}
	public void setChairManMessage(String chairManMessage) {
		this.chairManMessage = chairManMessage;
	}
	public String getSchoolmeassage() {
		return schoolmeassage;
	}
	public void setSchoolmeassage(String schoolmeassage) {
		this.schoolmeassage = schoolmeassage;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
}
