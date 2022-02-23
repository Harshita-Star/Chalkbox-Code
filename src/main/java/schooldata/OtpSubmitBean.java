package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name = "otpSubmitbean")
@ViewScoped
public class OtpSubmitBean implements Serializable {

	String regex=RegexPattern.REGEX;
	String validotp;
	String enterOtp;

	public OtpSubmitBean() {

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		validotp = (String) ss.getAttribute("otp");

	}

	public void checkOtp() {

		if (enterOtp.equals(validotp)) {

			HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			validotp = (String) ss1.getAttribute("otp");
			String studentid = (String) ss1.getAttribute("studentid");
			// String schid=(String) ss1.getAttribute("schid");

			Connection conn = DataBaseConnection.javaConnection();
			StudentInfo selectedStudent = new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),studentid, conn);

			try {
				conn.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", selectedStudent);

			ExternalContext cc = FacesContext.getCurrentInstance().getExternalContext();
			try {
				cc.redirect("printStudentDetails.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Sorry, Please Enter Correct OTP", "Validation error"));

		}

	}

	public String getValidotp() {
		return validotp;
	}

	public void setValidotp(String validotp) {
		this.validotp = validotp;
	}

	public String getEnterOtp() {
		return enterOtp;
	}

	public void setEnterOtp(String enterOtp) {
		this.enterOtp = enterOtp;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
