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
@ManagedBean(name="dailyImage")
@ViewScoped
public class DailyImageBean implements Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String principalMessage,chairManMessage,schoolmeassage;
	transient
	UploadedFile principalPicture,chairmanPicture,schoolPicture,pictue1,picture2,picture3,picture4,picture5;

	ArrayList<AboutUsInfo> informationList;

	public DailyImageBean(){
		Connection conn = DataBaseConnection.javaConnection();
		informationList =new DatabaseMethods1().informationOfDailyImagewithoyJson(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String insertAboutUsDeatils()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		if(informationList.size()==0)
		{
			String aboutUs=DBM.insertDailyImage(pictue1,picture2,picture3,picture4,picture5,/*informationList,*/conn);
			if(aboutUs.equals("i"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Images Added Successfullly"));
				informationList =DBM.informationOfDailyImagewithoyJson(conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}
		else
		{
			String aboutUs=DBM.updateDailyImage(pictue1,picture2,picture3,picture4,picture5,informationList,conn);
			if(aboutUs.equals("i"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Images Added Successfullly"));
				informationList =DBM.informationOfDailyImagewithoyJson(conn);

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

	public UploadedFile getPictue1() {
		return pictue1;
	}

	public void setPictue1(UploadedFile pictue1) {
		this.pictue1 = pictue1;
	}

	public UploadedFile getPicture2() {
		return picture2;
	}

	public void setPicture2(UploadedFile picture2) {
		this.picture2 = picture2;
	}

	public UploadedFile getPicture3() {
		return picture3;
	}

	public void setPicture3(UploadedFile picture3) {
		this.picture3 = picture3;
	}

	public UploadedFile getPicture4() {
		return picture4;
	}

	public void setPicture4(UploadedFile picture4) {
		this.picture4 = picture4;
	}

	public UploadedFile getPicture5() {
		return picture5;
	}

	public void setPicture5(UploadedFile picture5) {
		this.picture5 = picture5;
	}
}
