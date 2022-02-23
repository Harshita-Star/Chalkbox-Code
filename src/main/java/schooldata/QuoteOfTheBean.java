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

@ManagedBean(name = "quote")
@ViewScoped
public class QuoteOfTheBean implements Serializable {
	String principalMessage, chairManMessage, schoolmeassage, emailId, contactNo, website, QuoteOfTheWeek;
	UploadedFile principalPicture, chairmanPicture, schoolPicture;
	ArrayList<AboutUsInfo> informationList;

	public QuoteOfTheBean() {
		Connection conn = DataBaseConnection.javaConnection();
		informationList = new DatabaseMethods1().informationOfQuoteOfTheWeekwithoutJson(conn);
		for (AboutUsInfo tt : informationList) {
			QuoteOfTheWeek = tt.getQuoteOfTheweek();

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String insertDeatils() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		informationList = obj.informationOfQuoteOfTheWeekwithoutJson(conn);
		if (informationList.size() == 0) {
			String aboutUs = obj.insertDetailswithoutJson(QuoteOfTheWeek, conn);
			if (aboutUs.equals("i")) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				informationList = obj.informationOfQuoteOfTheWeekwithoutJson(conn);
				for (AboutUsInfo tt : informationList) {
					QuoteOfTheWeek = tt.getQuoteOfTheweek();

				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		} else {
			String aboutUs = obj.updateDetailswithoutJson(QuoteOfTheWeek, conn);
			if (aboutUs.equals("i")) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				informationList = obj.informationOfQuoteOfTheWeekwithoutJson(conn);
				for (AboutUsInfo tt : informationList) {
					QuoteOfTheWeek = tt.getQuoteOfTheweek();
				}
			} else {
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

	public String getQuoteOfTheWeek() {
		return QuoteOfTheWeek;
	}

	public void setQuoteOfTheWeek(String quoteOfTheWeek) {
		QuoteOfTheWeek = quoteOfTheWeek;
	}

	public ArrayList<AboutUsInfo> getInformationList() {
		return informationList;
	}

	public void setInformationList(ArrayList<AboutUsInfo> informationList) {
		this.informationList = informationList;
	}
}
