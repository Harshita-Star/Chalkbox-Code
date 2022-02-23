package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.file.UploadedFile;
@ManagedBean(name="contactus")
@ViewScoped

public class ContactUsBean implements Serializable
{
	String principalMessage,chairManMessage,schoolmeassage,emailId,contactNo,website;
	UploadedFile principalPicture,chairmanPicture,schoolPicture;
	ArrayList<AboutUsInfo> informationList;
	DatabaseMethods1 obj= new DatabaseMethods1();
	public ContactUsBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		informationList =obj.informationOfContactUswithoutJson(conn);
		for(AboutUsInfo tt:informationList)
		{
			contactNo=tt.getContactNum();
			emailId=tt.getEmailid();
			website=tt.getWebsite();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String insertDeatils()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(informationList.size()==0)
		{
			String aboutUs=obj.insertcontactDetailsWithoutJson(contactNo, emailId, website,conn);
			if(aboutUs.equals("i"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				informationList =obj.informationOfContactUswithoutJson(conn);
				for(AboutUsInfo tt:informationList)
				{
					contactNo=tt.getContactNum();
					emailId=tt.getEmailid();
					website=tt.getWebsite();

				}
			}
			else
			{

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}
		else
		{
			String aboutUs=obj.updatecontactDetailsForJson(contactNo, emailId, website,conn);
			if(aboutUs.equals("i"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				informationList =obj.informationOfContactUswithoutJson(conn);
				for(AboutUsInfo tt:informationList)
				{
					contactNo=tt.getContactNum();
					emailId=tt.getEmailid();
					website=tt.getWebsite();
				}
			}
			else
			{

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
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

	public UploadedFile getPrincipalPicture() {
		return principalPicture;
	}

	public void setPrincipalPicture(UploadedFile principalPicture) {
		this.principalPicture = principalPicture;
	}

	public UploadedFile getChairmanPicture() {
		return chairmanPicture;
	}

	public void setChairmanPicture(UploadedFile chairmanPicture) {
		this.chairmanPicture = chairmanPicture;
	}

	public UploadedFile getSchoolPicture() {
		return schoolPicture;
	}

	public void setSchoolPicture(UploadedFile schoolPicture) {
		this.schoolPicture = schoolPicture;
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

	public ArrayList<AboutUsInfo> getInformationList() {
		return informationList;
	}

	public void setInformationList(ArrayList<AboutUsInfo> informationList) {
		this.informationList = informationList;
	}
}
