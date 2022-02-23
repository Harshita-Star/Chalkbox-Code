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
@ManagedBean(name="aboutus")
@ViewScoped
public class AboutUsBean implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	String principalMessage,chairManMessage,schoolmeassage;
	transient
	UploadedFile  principalPicture,chairmanPicture,schoolPicture;
	ArrayList<AboutUsInfo> informationList;
	DatabaseMethods1 DBM= new DatabaseMethods1();
	

	public AboutUsBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		informationList =DBM.informationOfAboutUsForTest(conn);
		for(AboutUsInfo tt:informationList)
		{
			principalMessage=tt.getPrincipalMessage();
			chairManMessage=tt.getChairManMessage();
			schoolmeassage=tt.getSchoolmeassage();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String insertAboutUsDeatils()
	{
		Connection conn = DataBaseConnection.javaConnection();

		//informationList = DBM.informationOfAboutUsForTest(conn);
		if(informationList.size()==0)
		{
			String aboutUs=DBM.insertAbout(principalMessage,chairManMessage,schoolmeassage,principalPicture,chairmanPicture,schoolPicture/*,informationList*/,conn);
			if(aboutUs.equals("i"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				informationList =DBM.informationOfAboutUsForTest(conn);
				for(AboutUsInfo tt:informationList)
				{
					principalMessage=tt.getPrincipalMessage();
					chairManMessage=tt.getChairManMessage();
					schoolmeassage=tt.getSchoolmeassage();

				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}
		else
		{
			String aboutUs=DBM.insertAboutDetails(principalMessage,chairManMessage,schoolmeassage,principalPicture,chairmanPicture,schoolPicture,informationList,conn);
			if(aboutUs.equals("i"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				informationList =DBM.informationOfAboutUsForTest(conn);
				for(AboutUsInfo tt:informationList)
				{
					principalMessage=tt.getPrincipalMessage();
					chairManMessage=tt.getChairManMessage();
					schoolmeassage=tt.getSchoolmeassage();
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}

		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return "";
	}
	public String getPrincipalMessage()
	{
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
	public ArrayList<AboutUsInfo> getInformationList() {
		return informationList;
	}
	public void setInformationList(ArrayList<AboutUsInfo> informationList) {
		this.informationList = informationList;
	}
	
	
}
