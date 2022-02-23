package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import dlt_template.DltDatabaseMethod;
import dlt_template.DltTemplateInfo;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name = "studentWiseSMS")
@ViewScoped

public class StudentWiseSMSBean implements Serializable {
	String typeMessage, language, balMsg, userType;
	StudentInfo selectedStudent;
	String name;
	ArrayList<StudentInfo> studentList;
	boolean show, hindiShow, englishShow, messageParents = false, messageStudents = false, showInstitute = false, sms,
			notification;
	SchoolInfoList ls;
	double smsLimit;

	String keyWord, dltId;
	ArrayList<SelectItem> allKeywords = new ArrayList<>();
	ArrayList<DltTemplateInfo> allTemplates = new ArrayList<>();
	DltTemplateInfo selectedTemp;
	boolean editable;

	public void checklang() {
		Connection conn = DataBaseConnection.javaConnection();
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(language, conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1", language, new DatabaseMethods1().schoolId(), conn);
		keyWord = "-1";
		checkLanguage();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showData() {
		Connection conn = DataBaseConnection.javaConnection();

		allTemplates = new DltDatabaseMethod().getAllTemplates(keyWord, language, new DatabaseMethods1().schoolId(),
				conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkGroupSeelcted() {

		Connection conn = DataBaseConnection.javaConnection();
		typeMessage = selectedTemp.getContent();
		dltId = selectedTemp.getDltId();
		if (selectedTemp.getType().equalsIgnoreCase("dynamic")) {
			editable = true;
		} else {
			editable = false;
		}

		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public StudentWiseSMSBean() {
		hindiShow = false;
		language = "english";
		englishShow = true;
		sms = true;
		notification = false;

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		smsLimit = DBM.smsLimitReminder(DBM.schoolId(), conn);
		ls = DBM.fullSchoolInfo(conn);
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) ss.getAttribute("type");

		String ctype = DBM.checkClientType(conn);

		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(language, conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1", language, new DatabaseMethods1().schoolId(), conn);

		if (ctype.equalsIgnoreCase("institute")) {
			showInstitute = true;
		} else {
			showInstitute = false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkStudentSelected() {
		int index = name.lastIndexOf(":") + 1;
//		String id=name.substring(index);

		if (index != 0) {
			checkGroupSeelcted();
			// PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));

		}

	}

	public void checkLanguage() {
		typeMessage = "";
		if (language.equalsIgnoreCase("english")) {
			englishShow = true;
			hindiShow = false;
		} else {
			// language = "english";
			englishShow = false;
			hindiShow = true;
			// PrimeFaces.current().executeInitScript("PF('hindiDi').show();");

		}
	}

	public List<String> autoCompleteStudentInfo(String query) {
		Connection conn = DataBaseConnection.javaConnection();
		studentList = new DatabaseMethods1().searchStudentList(query, conn);
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList) {
			studentListt.add(info.getFname() + " / " + info.getFathersName() + " / " + info.getSrNo() + "-"
					+ info.getClassName() + "-:" + info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public String searchStudentByName() {
		Connection conn = DataBaseConnection.javaConnection();
		if (ls.getCountry().equalsIgnoreCase("India")) {
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if (balance > 0 && balance <= smsLimit) {
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (userType.equalsIgnoreCase("admin")) {
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
					return "";
				}
			} else if (balance <= 0) {
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if (userType.equalsIgnoreCase("admin")) {
					PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
					PrimeFaces.current().ajax().update("MsgOverForm");
				} else {
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

					PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
					PrimeFaces.current().ajax().update("MsgOtherForm");
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
				return "";
			}
		}

		int index = name.lastIndexOf(":") + 1;
		String id = name.substring(index);
		new DatabaseMethods1().fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		if (index != 0) {
			for (StudentInfo info : studentList) {
				if (String.valueOf(info.getAddNumber()).equals(id)) {
					try {
						selectedStudent = info;
						sendMessageStudent();

						break;
						// return "studentFeeCollection";
						// show=true;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		return "";
	}

	public void closeConfirmDialog() {
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");

	}

	public void sendMsg() {
		Connection conn = DataBaseConnection.javaConnection();

		int index = name.lastIndexOf(":") + 1;
		String id = name.substring(index);
		new DatabaseMethods1().fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		if (index != 0) {
			for (StudentInfo info : studentList) {
				if (String.valueOf(info.getAddNumber()).equals(id)) {
					try {
						selectedStudent = info;
						sendMessageStudent();

						break;
						// return "studentFeeCollection";
						// show=true;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}

	public void sendMessageStudent() throws UnsupportedEncodingException {
		if (sms == false && notification == false) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		} else {
			if (showInstitute == false) {
				messageStudentSchool();
			} else {
				if (messageParents == false && messageStudents == false) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Please Select Receivers : Parents/Students"));
				} else {
					messageStudentInstitute();
				}
			}
		}
	}

	public void messageStudentSchool() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		ls = DBM.fullSchoolInfo(conn);
		// int counter=1;
		// int secondCounter=0;
		String contactNumber = "";
		// Date date=new Date();
		String addNumber = "";

//		String messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
//		if(language.equalsIgnoreCase("hindi"))
//		{
//			messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//		}

		// For Dlt Template

		String messageTemp = typeMessage + "\nRegards,\n" + ls.getSmsSchoolName();
		if (language.equalsIgnoreCase("hindi")) {
			messageTemp = typeMessage + "\nसादर,\n" + ls.getHindiName();
		}
		// typeMessage=URLEncoder.encode(typeMessage,"UTF-8");

		/*
		 * for(StudentInfo info : selectedStudentList) { x++;
		 */
		// secondCounter++;
		if (ls.getCountry().equalsIgnoreCase("India")) {
			if (String.valueOf(selectedStudent.getFathersPhone()).length() == 10
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("2222222222")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("9999999999")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1111111111")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1234567890")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("0123456789")) {
				if (contactNumber.equals("")) {
					contactNumber = String.valueOf(selectedStudent.getFathersPhone());
					addNumber = selectedStudent.getAddNumber();
				} else {
					contactNumber = contactNumber + "," + String.valueOf(selectedStudent.getFathersPhone());
					addNumber = addNumber + "," + selectedStudent.getAddNumber();
				}

			}
		}

		if (notification) {
			//// // System.out.println("hindi message : "+messageTemp);
			if (selectedStudent.getFathersPhone() == selectedStudent.getMothersPhone()) {
				DBM.notification(ls.getSchid(), "Message", messageTemp,
						selectedStudent.getFathersPhone() + "-" + selectedStudent.getAddNumber() + "-" + ls.getSchid(),
						conn);
			} else {
				DBM.notification(ls.getSchid(), "Message", messageTemp,
						selectedStudent.getFathersPhone() + "-" + selectedStudent.getAddNumber() + "-" + ls.getSchid(),
						conn);
				DBM.notification(ls.getSchid(), "Message", messageTemp,
						selectedStudent.getMothersPhone() + "-" + selectedStudent.getAddNumber() + "-" + ls.getSchid(),
						conn);
			}
		}

		// }

		// if(x>0)
		// {
		if (sms) {
			if (ls.getCountry().equalsIgnoreCase("India")) {
//				messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
//				if(language.equalsIgnoreCase("hindi"))
//				{
//					messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();

				// For Dlt Template

				messageTemp = typeMessage + "\nRegards,\n" + ls.getSmsSchoolName();
				if (language.equalsIgnoreCase("hindi")) {
					messageTemp = typeMessage + "\nसादर,\n" + ls.getHindiName();
					DBM.messageurlHindi(contactNumber, messageTemp, addNumber, conn, dltId);
				} else {
					DBM.messageurl1(contactNumber, messageTemp, addNumber, conn, DBM.schoolId(), dltId);
				}
			} else {
				String heading = "<center class=\"red\">Message From " + ls.getSchoolName() + "!</center>";
				String subject = "Message From " + ls.getSchoolName();
				String msgss = "<center>Dear Parent,<br></br>" + typeMessage + "<br></br>Regards,<br></br>"
						+ ls.getSmsSchoolName() + "</center>";
				if (selectedStudent.getActionBy().equalsIgnoreCase("father")) {
					new DataBaseOnlineAdm().doMail(selectedStudent.getFatherEmail(), subject, heading, msgss);
				} else if (selectedStudent.getActionBy().equalsIgnoreCase("mother")) {
					new DataBaseOnlineAdm().doMail(selectedStudent.getMotherEmail(), subject, heading, msgss);
				} else if (selectedStudent.getActionBy().equalsIgnoreCase("both")) {
					if (selectedStudent.getFatherEmail().equalsIgnoreCase(selectedStudent.getMotherEmail())) {
						new DataBaseOnlineAdm().doMail(selectedStudent.getFatherEmail(), subject, heading, msgss);
					} else {
						new DataBaseOnlineAdm().doMail(selectedStudent.getFatherEmail(), subject, heading, msgss);
						new DataBaseOnlineAdm().doMail(selectedStudent.getMotherEmail(), subject, heading, msgss);
					}
				} else {
					new DataBaseOnlineAdm().doMail(selectedStudent.getFatherEmail(), subject, heading, msgss);
				}
			}

		}

		// DBM.messageurl1(contactNumber, messageTemp,addNumber,conn);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Message has been sent"));
		/*
		 * } else { FacesContext fc=FacesContext.getCurrentInstance();
		 * fc.addMessage(null, new FacesMessage("Please select a student!")); }
		 */

		language = "english";
		englishShow = true;
		hindiShow = false;
		studentList = null;
		typeMessage = null;
		name = "";
		show = false;
		sms = true;
		notification = false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void messageStudentInstitute() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		ls = DBM.fullSchoolInfo(conn);
		// int counter=1;
		// int secondCounter=0;
		String contactNumber = "";
		// Date date=new Date();
		String addNumber = "";
		String messageTemp = "";

//		if(messageParents==true && messageStudents==false)
//		{
//			messageTemp="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//			if(language.equalsIgnoreCase("hindi"))
//			{
//				messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//			}
//		}
//		else if(messageParents==false && messageStudents==true)
//		{
//			messageTemp="Dear Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//			if(language.equalsIgnoreCase("hindi"))
//			{
//				messageTemp="प्रिय विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//			}
//		}
//		else if(messageParents==true && messageStudents==true)
//		{
//			messageTemp="Dear Parent/Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//			if(language.equalsIgnoreCase("hindi"))
//			{
//				messageTemp="प्रिय अभिभावक/विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//			}
//		}

		// For Dlt Template

		if (messageParents == true && messageStudents == false) {
			messageTemp = typeMessage + " \nRegards,\n" + ls.getSmsSchoolName();
			if (language.equalsIgnoreCase("hindi")) {
				messageTemp = typeMessage + "\nसादर,\n" + ls.getHindiName();
			}
		} else if (messageParents == false && messageStudents == true) {
			messageTemp = typeMessage + " \nRegards,\n" + ls.getSmsSchoolName();
			if (language.equalsIgnoreCase("hindi")) {
				messageTemp = typeMessage + "\nसादर,\n" + ls.getHindiName();
			}
		} else if (messageParents == true && messageStudents == true) {
			messageTemp = typeMessage + " \nRegards,\n" + ls.getSmsSchoolName();
			if (language.equalsIgnoreCase("hindi")) {
				messageTemp = typeMessage + "\nसादर,\n" + ls.getHindiName();
			}
		}

		// typeMessage=URLEncoder.encode(typeMessage,"UTF-8");

		// for(StudentInfo info : selectedStudentList)
		// {
		// x++;
		// secondCounter++;
		if (messageParents == true) {
			if (String.valueOf(selectedStudent.getFathersPhone()).length() == 10
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("2222222222")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("9999999999")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1111111111")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1234567890")
					&& !String.valueOf(selectedStudent.getFathersPhone()).equals("0123456789")) {
				if (contactNumber.equals("")) {
					contactNumber = String.valueOf(selectedStudent.getFathersPhone());
					addNumber = selectedStudent.getAddNumber();
				} else {
					contactNumber = contactNumber + "," + String.valueOf(selectedStudent.getFathersPhone());
					addNumber = addNumber + "," + selectedStudent.getAddNumber();
				}

			}
		}

		if (messageStudents == true) {
			if (selectedStudent.getStudentPhone().length() == 10
					&& !String.valueOf(selectedStudent.getStudentPhone()).equals("2222222222")
					&& !String.valueOf(selectedStudent.getStudentPhone()).equals("9999999999")
					&& !String.valueOf(selectedStudent.getStudentPhone()).equals("1111111111")
					&& !String.valueOf(selectedStudent.getStudentPhone()).equals("1234567890")
					&& !String.valueOf(selectedStudent.getStudentPhone()).equals("0123456789")) {
				if (contactNumber.equals("")) {
					contactNumber = selectedStudent.getStudentPhone();
					addNumber = String.valueOf(selectedStudent.getAddNumber());
				} else {
					contactNumber = contactNumber + "," + selectedStudent.getStudentPhone();
					addNumber = addNumber + "," + String.valueOf(selectedStudent.getAddNumber());
				}
			}
		}

		if (notification) {
			DBM.notification(ls.getSchid(), "Message", messageTemp,
					selectedStudent.getFathersPhone() + "-" + selectedStudent.getAddNumber() + "-" + ls.getSchid(),
					conn);
			DBM.notification(ls.getSchid(), "Message", messageTemp,
					selectedStudent.getMothersPhone() + "-" + selectedStudent.getAddNumber() + "-" + ls.getSchid(),
					conn);
		}
		// }

		// if(x>0)
		// {

		// //// // System.out.println(addNumber);
		// //// // System.out.println(contactNumber.split(",").length);
		if (sms) {
			if (language.equalsIgnoreCase("hindi")) {
				DBM.messageurlHindi(contactNumber, messageTemp, addNumber, conn, dltId);
			} else {
				DBM.messageurl1(contactNumber, messageTemp, addNumber, conn, DBM.schoolId(), dltId);
			}
		}

		/*
		 * if(messageParents==true && messageStudents==false) {
		 * messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName()
		 * ; } else if(messageParents==false && messageStudents==true) {
		 * messageTemp="Dear Student,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName(
		 * ); } else if(messageParents==true && messageStudents==true) {
		 * messageTemp="Dear Parent/Student,\n"+typeMessage+"\nRegards,\n"+ls.
		 * getSmsSchoolName(); }
		 * 
		 * // //// // System.out.println(contactNumber); // //// //
		 * System.out.println(messageTemp); DBM.messageurl1(contactNumber,
		 * messageTemp,addNumber,conn);
		 */

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Message has been sent"));
		/*
		 * } else { FacesContext fc=FacesContext.getCurrentInstance();
		 * fc.addMessage(null, new FacesMessage("Please select atleast one student!"));
		 * }
		 */

		language = "english";
		englishShow = true;
		hindiShow = false;
		studentList = null;
		typeMessage = null;
		show = messageParents = messageStudents = false;
		name = "";

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getTypeMessage() {
		return typeMessage;
	}

	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isHindiShow() {
		return hindiShow;
	}

	public void setHindiShow(boolean hindiShow) {
		this.hindiShow = hindiShow;
	}

	public boolean isEnglishShow() {
		return englishShow;
	}

	public void setEnglishShow(boolean englishShow) {
		this.englishShow = englishShow;
	}

	public boolean isMessageParents() {
		return messageParents;
	}

	public void setMessageParents(boolean messageParents) {
		this.messageParents = messageParents;
	}

	public boolean isMessageStudents() {
		return messageStudents;
	}

	public void setMessageStudents(boolean messageStudents) {
		this.messageStudents = messageStudents;
	}

	public boolean isShowInstitute() {
		return showInstitute;
	}

	public void setShowInstitute(boolean showInstitute) {
		this.showInstitute = showInstitute;
	}

	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public ArrayList<SelectItem> getAllKeywords() {
		return allKeywords;
	}

	public void setAllKeywords(ArrayList<SelectItem> allKeywords) {
		this.allKeywords = allKeywords;
	}

	public ArrayList<DltTemplateInfo> getAllTemplates() {
		return allTemplates;
	}

	public void setAllTemplates(ArrayList<DltTemplateInfo> allTemplates) {
		this.allTemplates = allTemplates;
	}

	public DltTemplateInfo getSelectedTemp() {
		return selectedTemp;
	}

	public void setSelectedTemp(DltTemplateInfo selectedTemp) {
		this.selectedTemp = selectedTemp;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
