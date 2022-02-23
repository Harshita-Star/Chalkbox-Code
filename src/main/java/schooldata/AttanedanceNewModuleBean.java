package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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

@ManagedBean(name = "attndance")
@ViewScoped
public class AttanedanceNewModuleBean implements Serializable {

	ArrayList<StudentInfo> info = new ArrayList<>();
	String message, mainOption, mainOptionEmp, balMsg;
	SchoolInfoList ls;
	ArrayList<Category> selectedCategory;
	StudentInfo in;
	String[] selectedEmp;
	int i, j, k;
	double smsLimit;
	boolean show = false, showEmp = false, messageParents = false, messageStudents = false;
	ArrayList<SelectItem> sectionList, classList;
	String selectedCLassSection, selectedSection, includeStaff;
	boolean renderShowTable = true, showStudentRecord, showInstitute = false;
	Date selectedDay = new Date();
	ArrayList<StudentInfo> studentDetails;
	ArrayList<ClassInfo> classSection;
	ArrayList<ClassInfo> list;
	ArrayList<EmployeeInfo> selectedEmpCat;
	ArrayList<SelectItem> empList;
	String lang, uType;
	boolean showEnglish, showHindi, sms, notification;

	int check = 0, checkGroup = 0, checkGroupIns = 0;

	int count = 0;

	String keyWord,dltId;
	ArrayList<SelectItem> allKeywords = new ArrayList<>();
	ArrayList<DltTemplateInfo> allTemplates = new ArrayList<>();
	DltTemplateInfo selectedTemp;
	boolean editable;

	public void chckforstudentatndnce() {
		i = j = k = 0;
		for (ClassInfo ls : classSection) {

			for (Category ls1 : ls.getCategoryList()) {
				for (StudentInfo ls2 : ls1.getList()) {
					String we = ls2.getAttendance();
					if (we.equals("A")) {
						i++;
					}
					if (we.equals("P")) {
						j++;
					}
					if (we.equals("L")) {
						k++;
					}
				}
			}

		}

	}

	public void checking() {
		count = 0;
		if (ls.getCountry().equalsIgnoreCase("India")) {
			for (ClassInfo ls1 : classSection) {
				for (Category lst : ls1.getCategoryList()) {
					for (StudentInfo ls11 : lst.getList()) {
						if (ls11.isSendmessage()) {
							count = count + 1;
						}
					}
				}

			}
		}
	}

	public void checklang() {
		Connection conn = DataBaseConnection.javaConnection();
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(lang, conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1", lang, new DatabaseMethods1().schoolId(), conn);
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

		allTemplates = new DltDatabaseMethod().getAllTemplates(keyWord, lang, new DatabaseMethods1().schoolId(),conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AttanedanceNewModuleBean() {

		showEnglish = true;
		showHindi = false;
		sms = true;
		notification = false;

		lang = "english";

		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		classSection = DBM.allClassList("studentalso", conn);
		HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
				.getSession(false);
		uType = (String) httpSession.getAttribute("type");
		ls = DBM.fullSchoolInfo(conn);
		/*
		 * for (ClassInfo ls : classSection) { for (Category ls1 : ls.getCategoryList())
		 * { ls1.setList( DatabaseMethods1.allAttendance(String.valueOf(ls1.getId()),
		 * new Date(), ls1.getList(), conn)); // int //
		 * i=DatabaseMethods1.attendanceSection(String.valueOf(ls1.getId()),ls1.getList(
		 * ),new // Date(),conn); } }
		 */

		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(lang,conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1",lang,new DatabaseMethods1().schoolId(),conn);
		empList = DBM.employeeCategorySMS(conn);
		selectedCategory = new ArrayList<>();
		mainOption = "None";
		mainOptionEmp = "None";
		String ctype = DBM.checkClientType(conn);
		if (ctype.equalsIgnoreCase("institute")) {
			showInstitute = true;
		} else {
			showInstitute = false;
		}
		selectedEmpCat = new ArrayList<>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void saveNow() {

		Connection conn = DataBaseConnection.javaConnection();

		for (ClassInfo ls : classSection) {
			for (Category ls1 : ls.getCategoryList()) {
				DatabaseMethods1.attendanceSection(String.valueOf(ls1.getId()), ls1.getList(), new Date(), conn);
			}
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance Saved", "Alert"));

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkLanguage() {
		message = "";
		if (lang.equalsIgnoreCase("english")) {
			showEnglish = true;
			showHindi = false;
		} else {
			showEnglish = false;
			showHindi = true;

		}
	}
	
	public void matchFilter() {
		System.out.println("in matching fskjafncifdkjcm");
	}

	public String sendMessage() throws UnsupportedEncodingException {

		Connection conn = DataBaseConnection.javaConnection();
		if (ls.getCountry().equalsIgnoreCase("India")) {
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if (balance > 0 && balance <= smsLimit) {
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (uType.equalsIgnoreCase("admin")) {
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
				if (uType.equalsIgnoreCase("admin")) {
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

		check = 0;
		// ("sm : "+sms+"....notify : "+notification);
		if (sms == false && notification == false) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		} else {
			if (showInstitute == false) {
				messageToSchool();
			} else {
				if (messageParents == false && messageStudents == false) {
					// messageToParents();
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Please Select Receivers : Parents/Students"));
				} else {
					messageToInstitute();
				}
				/*
				 * else if(messageParents==true && messageStudents==false) {
				 *
				 * } else if(messageParents==false && messageStudents==true) {
				 * //messageToStudents(); } else if(messageParents==true &&
				 * messageStudents==true) { //messageToStudents(); }
				 */
			}

			if (check <= 0) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Please Select atleast one student"));
			}
		}
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		return "";
	}

	public void closeConfirmDialog() {
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");

	}

	public void checkSelection() {
		//// // System.out.println("saf");
		if (sms == false && notification == false) {
			//// // System.out.println("sadf");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		}

		else {
			// // // System.out.println("ds");
			//PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
			
			checkGroupSeelcted();
			
		}
	}

	public void randomMsg() throws UnsupportedEncodingException {
		check = 0;
		// ("sm : "+sms+"....notify : "+notification);
		if (sms == false && notification == false) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		} else {
			if (showInstitute == false) {
				messageToSchool();
			} else {
				if (messageParents == false && messageStudents == false) {
					// messageToParents();
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Please Select Receivers : Parents/Students"));
				} else {
					messageToInstitute();
				}
				/*
				 * else if(messageParents==true && messageStudents==false) {
				 *
				 * } else if(messageParents==false && messageStudents==true) {
				 * //messageToStudents(); } else if(messageParents==true &&
				 * messageStudents==true) { //messageToStudents(); }
				 */
			}

			if (check <= 0) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Please Select atleast one student"));
			}
		}
	}

	public void messageToSchool() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		ls = obj.fullSchoolInfo(conn);
		String contactno = "", addNumber = "";
//		String msg = "Dear Parent,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName();
//		if (lang.equalsIgnoreCase("hindi")) {
//			msg = "प्रिय अभिभावक,\n" + message + "\nसादर,\n" + ls.getHindiName();
//		}
		
		String msg =  message + "\nRegards,\n" + ls.getSmsSchoolName() ;
		if (lang.equalsIgnoreCase("hindi")) {
			msg =  message + "\nसादर,\n" + ls.getHindiName() ;
		}

		if (ls.getCountry().equalsIgnoreCase("India")) {
			for (ClassInfo ls1 : classSection) {
				for (Category lst : ls1.getCategoryList()) {
					for (StudentInfo ls11 : lst.getList()) {
						if (ls11.isSendmessage()) {
							check++;
							if (String.valueOf(ls11.getFathersPhone()).length() == 10
									&& !String.valueOf(ls11.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(ls11.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(ls11.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(ls11.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(ls11.getFathersPhone()).equals("0123456789")) {
								if (contactno.equals("")) {
									addNumber = ls11.getAddNumber();
									contactno = String.valueOf(ls11.getFathersPhone());
								} else {
									if (!contactno.contains(String.valueOf(ls11.getFathersPhone()))) {
										addNumber = addNumber + "," + ls11.getAddNumber();
										contactno = contactno + "," + String.valueOf(ls11.getFathersPhone());
									}
								}

							}

							if (notification) {
								obj.notification(ls.getSchid(), "Message", msg,
										ls11.getFathersPhone() + "-" + ls11.getAddNumber() + "-" + ls.getSchid(), conn);
								obj.notification(ls.getSchid(), "Message", msg,
										ls11.getMothersPhone() + "-" + ls11.getAddNumber() + "-" + ls.getSchid(), conn);
							}

						}
					}
				}

			}
		} else {
			check++;
			String tp = message;
			String msgs = "Dear Parent,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName();
			Runnable r = new Runnable() {
				public void run() {
					Connection con = DataBaseConnection.javaConnection();
					String heading = "<center class=\"red\">Message From " + ls.getSchoolName() + "!</center>";
					String subject = "Message From " + ls.getSchoolName();
					String msg = "<center>Dear Parent,<br></br>" + tp + " <br></br>Regards,<br></br>"
							+ ls.getSmsSchoolName() + "</center>";

					for (ClassInfo ls1 : classSection) {
						for (Category lst : ls1.getCategoryList()) {
							for (StudentInfo ls11 : lst.getList()) {
								if (ls11.isSendmessage()) {
									if (sms) {
										if (ls11.getActionBy().equalsIgnoreCase("father")) {
											new DataBaseOnlineAdm().doMail(ls11.getFatherEmail(), subject, heading,
													msg);
										} else if (ls11.getActionBy().equalsIgnoreCase("mother")) {
											new DataBaseOnlineAdm().doMail(ls11.getMotherEmail(), subject, heading,
													msg);
										} else if (ls11.getActionBy().equalsIgnoreCase("both")) {
											if (ls11.getFatherEmail().equalsIgnoreCase(ls11.getMotherEmail())) {
												new DataBaseOnlineAdm().doMail(ls11.getFatherEmail(), subject, heading,
														msg);
											} else {
												new DataBaseOnlineAdm().doMail(ls11.getFatherEmail(), subject, heading,
														msg);
												new DataBaseOnlineAdm().doMail(ls11.getMotherEmail(), subject, heading,
														msg);
											}
										} else {
											new DataBaseOnlineAdm().doMail(ls11.getFatherEmail(), subject, heading,
													msg);
										}

									}

									if (notification) {
										obj.notification(ls.getSchid(), "Message", msgs, ls11.getFathersPhone() + "-"
												+ ls11.getAddNumber() + "-" + ls.getSchid(), con);
										obj.notification(ls.getSchid(), "Message", msgs, ls11.getMothersPhone() + "-"
												+ ls11.getAddNumber() + "-" + ls.getSchid(), con);
									}
								}
							}
						}
					}

					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			};
			new Thread(r).start();
		}

		if (check > 0) {
			if (sms) {
				if (ls.getCountry().equalsIgnoreCase("India")) {
//					msg = "Dear Parent,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName();
//					if (lang.equalsIgnoreCase("hindi")) {
//						msg = "प्रिय अभिभावक,\n" + message + "\nसादर,\n" + ls.getHindiName();
					msg =  message + "\nRegards,\n" + ls.getSmsSchoolName() ;
					if (lang.equalsIgnoreCase("hindi")) {
						msg =  message + "\nसादर,\n" + ls.getHindiName() ;
						// (msg.length());
						obj.messageurlHindi(contactno, msg, addNumber, conn,dltId);
					} else {
						obj.messageurl1(contactno, msg, addNumber, conn, obj.schoolId(),dltId);
					}
				}
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message sent successfully!"));
			// obj.messageurl1(contactno, msg, addNumber, conn);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void messageToInstitute() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		ls = obj.fullSchoolInfo(conn);
		String msg = "";

//		if (messageParents == true && messageStudents == false) {
//			msg = "Dear Parent,\n" + message + " \nRegards,\n" + ls.getSmsSchoolName();
//			if (lang.equalsIgnoreCase("hindi")) {
//				msg = "प्रिय अभिभावक,\n" + message + "\nसादर,\n" + ls.getHindiName();
//			}
//		} else if (messageParents == false && messageStudents == true) {
//			msg = "Dear Student,\n" + message + " \nRegards,\n" + ls.getSmsSchoolName();
//			if (lang.equalsIgnoreCase("hindi")) {
//				msg = "प्रिय विद्यार्थी,\n" + message + "\nसादर,\n" + ls.getHindiName();
//			}
//		} else if (messageParents == true && messageStudents == true) {
//			msg = "Dear Parent/Student,\n" + message + " \nRegards,\n" + ls.getSmsSchoolName();
//			if (lang.equalsIgnoreCase("hindi")) {
//				msg = "प्रिय अभिभावक/विद्यार्थी,\n" + message + "\nसादर,\n" + ls.getHindiName();
//			}
//		}
		
		
		if (messageParents == true && messageStudents == false) {
			msg =  message + " \nRegards,\n" + ls.getSmsSchoolName() ;
			if (lang.equalsIgnoreCase("hindi")) {
				msg =  message + "\nसादर,\n" + ls.getHindiName() ;
			}
		} else if (messageParents == false && messageStudents == true) {
			msg =  message + " \nRegards,\n" + ls.getSmsSchoolName() ;
			if (lang.equalsIgnoreCase("hindi")) {
				msg =  message + "\nसादर,\n" + ls.getHindiName() ;
			}
		} else if (messageParents == true && messageStudents == true) {
			msg =  message + " \nRegards,\n" + ls.getSmsSchoolName() ;
			if (lang.equalsIgnoreCase("hindi")) {
				msg =  message + "\nसादर,\n" + ls.getHindiName() ;
			}
		}
		
		String contactno = "", addNumber = "";

		for (ClassInfo ls1 : classSection) {
			for (Category lst : ls1.getCategoryList()) {
				for (StudentInfo ls11 : lst.getList()) {
					if (ls11.isSendmessage()) {
						check++;
						if (messageParents == true) {
							if (String.valueOf(ls11.getFathersPhone()).length() == 10
									&& !String.valueOf(ls11.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(ls11.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(ls11.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(ls11.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(ls11.getFathersPhone()).equals("0123456789")) {
								if (contactno.equals("")) {
									contactno = String.valueOf(ls11.getFathersPhone());
									addNumber = String.valueOf(ls11.getAddNumber());
								} else {

									contactno = contactno + "," + String.valueOf(ls11.getFathersPhone());
									addNumber = addNumber + "," + String.valueOf(ls11.getAddNumber());
								}
							}
						}

						if (messageStudents == true) {
							if (ls11.getStudentPhone().length() == 10
									&& !String.valueOf(ls11.getStudentPhone()).equals("2222222222")
									&& !String.valueOf(ls11.getStudentPhone()).equals("9999999999")
									&& !String.valueOf(ls11.getStudentPhone()).equals("1111111111")
									&& !String.valueOf(ls11.getStudentPhone()).equals("1234567890")
									&& !String.valueOf(ls11.getStudentPhone()).equals("0123456789")) {
								if (contactno.equals("")) {
									contactno = ls11.getStudentPhone();
									addNumber = String.valueOf(ls11.getAddNumber());
								} else {
									contactno = contactno + "," + ls11.getStudentPhone();
									addNumber = addNumber + "," + String.valueOf(ls11.getAddNumber());
								}
							}
						}

						if (notification) {
							obj.notification(ls.getSchid(), "Message", msg,
									ls11.getFathersPhone() + "-" + ls11.getAddNumber() + "-" + ls.getSchid(), conn);
							obj.notification(ls.getSchid(), "Message", msg,
									ls11.getMothersPhone() + "-" + ls11.getAddNumber() + "-" + ls.getSchid(), conn);
						}

					}
				}
			}

		}

		// //(contactno);
		if (check > 0) {

			/*
			 * if (messageParents == true && messageStudents == false) { msg =
			 * "Dear Parent,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName(); } else
			 * if (messageParents == false && messageStudents == true) { msg =
			 * "Dear Student,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName(); } else
			 * if (messageParents == true && messageStudents == true) { msg =
			 * "Dear Parent/Student,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName();
			 * }
			 */
			if (sms) {
				if (lang.equalsIgnoreCase("hindi")) {
					obj.messageurlHindi(contactno, msg, addNumber, conn,dltId);
				} else {
					obj.messageurl1(contactno, msg, addNumber, conn, obj.schoolId(),dltId);
				}
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message sent successfully!"));
			// obj.messageurl1(contactno, msg, addNumber, conn);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectAllMethod() {
		if (mainOption.equalsIgnoreCase("All")) {
			for (ClassInfo cc : classSection) {
				if (cc.getSectionList().size() > 0) {
					int[] temp = new int[100];

					for (int i = 0; i < cc.getSectionList().size(); i++) {

						temp[i] = (int) cc.getSectionList().get(i).getValue();
						// cc.selectedSections[i] = (int) cc.getSectionList().get(i).getValue();
					}

					cc.setSelectedSections(temp);
				}
			}

			show = true;
		} else {
			show = false;
			for (ClassInfo cc : classSection) {
				/*
				 * for(int i=0;i<cc.getSectionList().size();i++) {
				 */
				cc.setSelectedSections(null);
				// }
			}
		}
	}

	public void selectAllEmpMethod() {
		int i = 0;
		if (mainOptionEmp.equalsIgnoreCase("All")) {
			if (empList.size() > 0) {
				selectedEmp = new String[empList.size()];
				for (SelectItem cc : empList) {
					selectedEmp[i] = (String) cc.getValue();
					i++;
				}

				showEmp = true;
			}
		} else {
			selectedEmp = new String[0];
			showEmp = false;
		}
	}

	public String sendMessage1() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		if (ls.getCountry().equalsIgnoreCase("India")) {
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if (balance > 0 && balance <= smsLimit) {
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (uType.equalsIgnoreCase("admin")) {
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
				if (uType.equalsIgnoreCase("admin")) {
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

		checkGroup = 0;
		int x = 0;
		if (showInstitute == false) {
			groupMessageSchool();
		} else {
			for (ClassInfo cc : classSection) {
				if (cc.getSelectedSections().length > 0) {
					x++;
				}
			}

			if (x > 0) {
				if (messageParents == false && messageStudents == false) {
					// messageToParents();
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Please Select Receivers : Parents/Students"));
				} else {
					groupMessageInstitute();
				}
				/*
				 * else if(messageParents==true && messageStudents==false) {
				 * groupMessageParents(); } else if(messageParents==false &&
				 * messageStudents==true) { groupMessageStudents(); } else
				 * if(messageParents==true && messageStudents==true) { groupMessageParents();
				 * groupMessageStudents(); }
				 */
			}

		}

		if (selectedEmp.length > 0) {
			checkGroup++;
			messageToStaff();
		}

		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		return "";

	}

	public void checkGroupSeelcted() {
		
		Connection conn = DataBaseConnection.javaConnection();
		message = selectedTemp.getContent();
		dltId = selectedTemp.getDltId();
		if(selectedTemp.getType().equalsIgnoreCase("dynamic")) {
			editable=true;
		}else {
			editable= false;
		}
		
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void groupMsg() throws UnsupportedEncodingException {
		checkGroup = 0;
		int x = 0;
		if (showInstitute == false) {
			groupMessageSchool();
		} else {
			for (ClassInfo cc : classSection) {
				if (cc.getSelectedSections().length > 0) {
					x++;
				}
			}

			if (x > 0) {
				if (messageParents == false && messageStudents == false) {
					// messageToParents();
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Please Select Receivers : Parents/Students"));
				} else {
					groupMessageInstitute();
				}
				/*
				 * else if(messageParents==true && messageStudents==false) {
				 * groupMessageParents(); } else if(messageParents==false &&
				 * messageStudents==true) { groupMessageStudents(); } else
				 * if(messageParents==true && messageStudents==true) { groupMessageParents();
				 * groupMessageStudents(); }
				 */
			}

		}

		if (selectedEmp.length > 0) {
			checkGroup++;
			messageToStaff();
		}

		// String sid=new DatabaseMethods1().schoolId();

		if (checkGroup == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Please Select Atleast One Receiver From Class or Staff Categories.", "Alert"));

		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Message Sent SuccessFully.", "Alert"));
		}
	}

	public void groupMessageInstitute() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		ls = new DatabaseMethods1().fullSchoolInfo(conn);
		String contactNo = "", addNumber = "";

		for (ClassInfo cc : classSection) {
			if (cc.getSelectedSections().length > 0) {

				for (int i = 0; i < cc.getSelectedSections().length; i++) {
					ArrayList<StudentInfo> studentList = new DatabaseMethods1().searchStudentListByClassSection(
							cc.getGroupid(), String.valueOf(cc.getSelectedSections()[i]), conn);

					for (StudentInfo ls : studentList) {

						checkGroup++;
						if (messageParents == true) {
							if (String.valueOf(ls.getFathersPhone()).length() == 10
									&& !String.valueOf(ls.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(ls.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(ls.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(ls.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(ls.getFathersPhone()).equals("0123456789")) {
								if (contactNo.equals("")) {
									contactNo = String.valueOf(ls.getFathersPhone());
									addNumber = String.valueOf(ls.getAddNumber());
								} else {
									if (!contactNo.contains(String.valueOf(ls.getFathersPhone()))) {
										contactNo = contactNo + "," + String.valueOf(ls.getFathersPhone());
										addNumber = addNumber + "," + String.valueOf(ls.getAddNumber());
									}
								}
							}
						}

						if (messageStudents == true) {
							if (ls.getStudentPhone().length() == 10
									&& !String.valueOf(ls.getStudentPhone()).equals("2222222222")
									&& !String.valueOf(ls.getStudentPhone()).equals("9999999999")
									&& !String.valueOf(ls.getStudentPhone()).equals("1111111111")
									&& !String.valueOf(ls.getStudentPhone()).equals("1234567890")
									&& !String.valueOf(ls.getStudentPhone()).equals("0123456789")) {
								if (contactNo.equals("")) {
									contactNo = ls.getStudentPhone();
									addNumber = String.valueOf(ls.getAddNumber());
								} else {
									if (!contactNo.contains(String.valueOf(ls.getFathersPhone()))) {
										contactNo = contactNo + "," + ls.getStudentPhone();
										addNumber = addNumber + "," + String.valueOf(ls.getAddNumber());
									}
								}
							}
						}
					}
				}

			} else {

			}
		}

		if (checkGroup > 0) {
			String msg = "";
			
			if (messageParents == true && messageStudents == false) {
				msg = message + " \nRegards,\n" + ls.getSmsSchoolName() ;
				if (lang.equalsIgnoreCase("hindi")) {
					msg =  message + "\nसादर,\n" + ls.getHindiName() ;
				}
			} else if (messageParents == false && messageStudents == true) {
				msg =  message + " \nRegards,\n" + ls.getSmsSchoolName() ;
				if (lang.equalsIgnoreCase("hindi")) {
					msg = message + "\nसादर,\n" + ls.getHindiName() ;
				}
			} else if (messageParents == true && messageStudents == true) {
				msg =  message + " \nRegards,\n" + ls.getSmsSchoolName() ;
				if (lang.equalsIgnoreCase("hindi")) {
					msg =  message + "\nसादर,\n" + ls.getHindiName() ;
				}
			}
			
//			if (messageParents == true && messageStudents == false) {
//				msg = "Dear Parent,\n" + message + " \nRegards,\n" + ls.getSmsSchoolName();
//				if (lang.equalsIgnoreCase("hindi")) {
//					msg = "प्रिय अभिभावक,\n" + message + "\nसादर,\n" + ls.getHindiName();
//				}
//			} else if (messageParents == false && messageStudents == true) {
//				msg = "Dear Student,\n" + message + " \nRegards,\n" + ls.getSmsSchoolName();
//				if (lang.equalsIgnoreCase("hindi")) {
//					msg = "प्रिय विद्यार्थी,\n" + message + "\nसादर,\n" + ls.getHindiName();
//				}
//			} else if (messageParents == true && messageStudents == true) {
//				msg = "Dear Parent/Student,\n" + message + " \nRegards,\n" + ls.getSmsSchoolName();
//				if (lang.equalsIgnoreCase("hindi")) {
//					msg = "प्रिय अभिभावक/विद्यार्थी,\n" + message + "\nसादर,\n" + ls.getHindiName();
//				}
//			}
			/*
			 * if (messageParents == true && messageStudents == false) { msg =
			 * "Dear Parent,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName(); } else
			 * if (messageParents == false && messageStudents == true) { msg =
			 * "Dear Student,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName(); } else
			 * if (messageParents == true && messageStudents == true) { msg =
			 * "Dear Parent/Student,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName();
			 * }
			 */

			if (lang.equalsIgnoreCase("hindi")) {
				new DatabaseMethods1().messageurlHindi(contactNo, msg, addNumber, conn,dltId);
			} else {
				new DatabaseMethods1().messageurl1(contactNo, msg, addNumber, conn, new DatabaseMethods1().schoolId(),dltId);
			}
			// // // System.out.println("checkGroup"+checkGroup);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Message sent successfully To Students!"));
			// obj.messageurl1(contactno, msg, addNumber, conn);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student!"));
		}

		// new DatabaseMethods1().messageurl1(contactNo, msg, addNumber, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void groupMessageSchool() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		ls = new DatabaseMethods1().fullSchoolInfo(conn);
		String contactNo = "", addNumber = "";

		if (ls.getCountry().equalsIgnoreCase("India")) {
			for (ClassInfo cc : classSection) {
				if (cc.getSelectedSections().length > 0) {

					for (int i = 0; i < cc.getSelectedSections().length; i++) {
						ArrayList<StudentInfo> studentList = new DatabaseMethods1().searchStudentListByClassSection(
								cc.getGroupid(), String.valueOf(cc.getSelectedSections()[i]), conn);

						for (StudentInfo lss : studentList) {
							checkGroup++;
							if (String.valueOf(lss.getFathersPhone()).length() == 10
									&& !String.valueOf(lss.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(lss.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(lss.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(lss.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(lss.getFathersPhone()).equals("0123456789")) {
								if (contactNo.equals("")) {
									contactNo = String.valueOf(lss.getFathersPhone());
									addNumber = String.valueOf(lss.getAddNumber());
								} else {
									if (!contactNo.contains(String.valueOf(lss.getFathersPhone()))) {
										contactNo = contactNo + "," + String.valueOf(lss.getFathersPhone());
										addNumber = addNumber + "," + String.valueOf(lss.getAddNumber());
									}
								}
							}
						}
					}
				}
			}
		} else {
			checkGroup++;
			String tp = message;
			Runnable r = new Runnable() {
				public void run() {
					Connection con = DataBaseConnection.javaConnection();
					String heading = "<center class=\"red\">Message From " + ls.getSchoolName() + "!</center>";
					String subject = "Message From " + ls.getSchoolName();
					String msg = "<center>Dear Parent,<br></br>" + tp + " <br></br>Regards,<br></br>"
							+ ls.getSmsSchoolName() + "</center>";
					for (ClassInfo cc : classSection) {
						if (cc.getSelectedSections().length > 0) {

							for (int i = 0; i < cc.getSelectedSections().length; i++) {
								ArrayList<StudentInfo> studentList = new DatabaseMethods1()
										.searchStudentListByClassSectionSchid(ls.getSchid(),
												String.valueOf(cc.getSelectedSections()[i]), con);

								for (StudentInfo lss : studentList) {
									if (lss.getActionBy().equalsIgnoreCase("father")) {
										new DataBaseOnlineAdm().doMail(lss.getFatherEmail(), subject, heading, msg);
									} else if (lss.getActionBy().equalsIgnoreCase("mother")) {
										new DataBaseOnlineAdm().doMail(lss.getMotherEmail(), subject, heading, msg);
									} else if (lss.getActionBy().equalsIgnoreCase("both")) {
										if (lss.getFatherEmail().equalsIgnoreCase(lss.getMotherEmail())) {
											new DataBaseOnlineAdm().doMail(lss.getFatherEmail(), subject, heading, msg);
										} else {
											new DataBaseOnlineAdm().doMail(lss.getFatherEmail(), subject, heading, msg);
											new DataBaseOnlineAdm().doMail(lss.getMotherEmail(), subject, heading, msg);
										}
									} else {
										new DataBaseOnlineAdm().doMail(lss.getFatherEmail(), subject, heading, msg);
									}
								}
							}
						}
					}

					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			};
			new Thread(r).start();
		}

		if (checkGroup > 0) {

			if (ls.getCountry().equalsIgnoreCase("India")) {
				String msg =  message + "\nRegards,\n" + ls.getSmsSchoolName() ;
				if (lang.equalsIgnoreCase("hindi")) {
					msg =  message + "\nसादर,\n" + ls.getHindiName() ;
					// (msg.length());
					new DatabaseMethods1().messageurlHindi(contactNo, msg, addNumber, conn,dltId);
				} else {
					new DatabaseMethods1().messageurl1(contactNo, msg, addNumber, conn,
							new DatabaseMethods1().schoolId(),dltId);
				}
			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Message sent successfully To Students!"));
			// obj.messageurl1(contactno, msg, addNumber, conn);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student!"));
		}
		// new DatabaseMethods1().messageurl1(contactNo, msg, addNumber, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void messageToStaff() throws UnsupportedEncodingException {
		Connection conn = DataBaseConnection.javaConnection();
		String employeeContact = "", empployeeNumber = "";
		int x = 0;

		if (ls.getCountry().equalsIgnoreCase("India")) {
			for (int j = 0; j < selectedEmp.length; j++) {
				selectedEmpCat = new DatabaseMethods1()
						.searchEmployeebyCategorySchidd(new DatabaseMethods1().schoolId(), selectedEmp[j], conn);
				if (selectedEmpCat.size() > 0) {
					x++;
					for (EmployeeInfo ee : selectedEmpCat) {
						if (String.valueOf(ee.getMobile()).length() == 10
								&& !String.valueOf(ee.getMobile()).equals("2222222222")
								&& !String.valueOf(ee.getMobile()).equals("9999999999")
								&& !String.valueOf(ee.getMobile()).equals("1111111111")
								&& !String.valueOf(ee.getMobile()).equals("1234567890")
								&& !String.valueOf(ee.getMobile()).equals("0123456789")) {
							if (employeeContact.equals("")) {
								employeeContact = String.valueOf(ee.getMobile());
								empployeeNumber = String.valueOf(ee.getFname()) + "@CB@" + ee.getId();
							} else {
								employeeContact = employeeContact + "," + String.valueOf(ee.getMobile());
								empployeeNumber = empployeeNumber + "," + String.valueOf(ee.getFname()) + "@CB@"
										+ ee.getId();
							}
						}
					}
				}
			}
		} else {
			x++;
			String tp = message;
			Runnable r = new Runnable() {
				public void run() {
					Connection con = DataBaseConnection.javaConnection();
					String heading = "<center class=\"red\">Message From " + ls.getSchoolName() + "!</center>";
					String subject = "Message From " + ls.getSchoolName();
					String msg = "<center>Dear Staff Member,<br></br>" + tp + " <br></br>Regards,<br></br>"
							+ ls.getSmsSchoolName() + "</center>";

					for (int j = 0; j < selectedEmp.length; j++) {
						selectedEmpCat = new DatabaseMethods1().searchEmployeebyCategorySchidd(ls.getSchid(),
								selectedEmp[j], con);
						if (selectedEmpCat.size() > 0) {
							for (EmployeeInfo ee : selectedEmpCat) {
								if (ee.getEmail() != null && !ee.getEmail().equalsIgnoreCase("")
										&& !ee.getEmail().equalsIgnoreCase("null")) {
									new DataBaseOnlineAdm().doMail(ee.getEmail(), subject, heading, msg);
								}
							}
						}
					}

					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			};
			new Thread(r).start();
		}

		if (x > 0) {
			//String msg = "Dear Staff Member,\n" + message + "\nRegards,\n" + ls.getSmsSchoolName();
			String msg =  message + "\nRegards,\n" + ls.getSmsSchoolName() ;
			if (ls.getCountry().equalsIgnoreCase("India")) {
				if (lang.equalsIgnoreCase("hindi")) {
					//msg = "प्रिय कर्मचारी,\n" + message + "\nसादर,\n" + ls.getHindiName();
					msg =  message + "\nसादर,\n" + ls.getHindiName() ;
					new DatabaseMethods1().messageurlStaffHindi(employeeContact, msg, empployeeNumber, conn,dltId);
				} else {
					// msg="Dear Staff Member,\n"+message+"\nRegards,\n"+ls.getSmsSchoolName();
					new DatabaseMethods1().messageurlStaff(employeeContact, msg, empployeeNumber, conn,
							new DatabaseMethods1().schoolId(),dltId);
				}
			}
			// new DatabaseMethods1().messageurlStaff(employeeContact, msg, empployeeNumber,
			// conn);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Message Sent SuccessFully To Staff", "Alert"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Please Select Atleast One Receiver From Staff List.", "Alert"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void setClassSection(ArrayList<ClassInfo> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public boolean isRenderShowTable() {
		return renderShowTable;
	}

	public void setRenderShowTable(boolean renderShowTable) {
		this.renderShowTable = renderShowTable;
	}

	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}

	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}

	public Date getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(Date selectedDay) {
		this.selectedDay = selectedDay;
	}

	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}

	public String getIncludeStaff() {
		return includeStaff;
	}

	public void setIncludeStaff(String includeStaff) {
		this.includeStaff = includeStaff;
	}

	public String[] getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String[] selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	public String getMainOptionEmp() {
		return mainOptionEmp;
	}

	public void setMainOptionEmp(String mainOptionEmp) {
		this.mainOptionEmp = mainOptionEmp;
	}

	public ArrayList<EmployeeInfo> getSelectedEmpCat() {
		return selectedEmpCat;
	}

	public void setSelectedEmpCat(ArrayList<EmployeeInfo> selectedEmpCat) {
		this.selectedEmpCat = selectedEmpCat;
	}

	public ArrayList<SelectItem> getEmpList() {
		return empList;
	}

	public void setEmpList(ArrayList<SelectItem> empList) {
		this.empList = empList;
	}

	public boolean isShowEmp() {
		return showEmp;
	}

	public void setShowEmp(boolean showEmp) {
		this.showEmp = showEmp;
	}

	public boolean isShowInstitute() {
		return showInstitute;
	}

	public void setShowInstitute(boolean showInstitute) {
		this.showInstitute = showInstitute;
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

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getMainOption() {
		return mainOption;
	}

	public void setMainOption(String mainOption) {
		this.mainOption = mainOption;
	}

	public StudentInfo getIn() {
		return in;
	}

	public void setIn(StudentInfo in) {
		this.in = in;
	}

	public ArrayList<Category> getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(ArrayList<Category> selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public ArrayList<StudentInfo> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<StudentInfo> info) {
		this.info = info;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<ClassInfo> getClassSection() {
		return classSection;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isShowEnglish() {
		return showEnglish;
	}

	public void setShowEnglish(boolean showEnglish) {
		this.showEnglish = showEnglish;
	}

	public boolean isShowHindi() {
		return showHindi;
	}

	public void setShowHindi(boolean showHindi) {
		this.showHindi = showHindi;
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

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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
