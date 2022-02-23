package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import session_work.RegexPattern;

@ManagedBean(name = "finalBLMMarksheet")
@ViewScoped
public class FinalBLMMarkSheetBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String regex = RegexPattern.REGEX;
	boolean checkVal;
	String name, termId,headerImagePath, selTermInitium, termName, examId, examName, examID = "";
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	ArrayList<StudentInfo> selectedStudentList = new ArrayList<StudentInfo>();
	boolean show, showFill, showProceed, printBtnForInitium, printBtnForOther, showExam, showExamAdd, showExamPeriodic;
	Date dateOfEntry;
	ArrayList<String> selectedTerm = new ArrayList<>();
	ArrayList<String> selectdExamAdditional;
	ArrayList<SelectItem> classSection, sectionList, termList, allExamList, allExamListForAdditional;
	ArrayList<String> selectdExam;
	int size = 1;

	ArrayList<SubjectInfo> markListCoscholastic;
	String selectedClassSection, selectedSection, description, session, schid, comeTestType = "all", rollNo, username, userType;
	StudentInfo selectedStudent;
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	boolean showCoButton;
	DataBaseMethodsBLMExam de = new DataBaseMethodsBLMExam();
	DataBaseMethodsExam objExam = new DataBaseMethodsExam();

	public void periodicValueSet() {
		comeTestType = "periodic";
	}

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();

		termList = obj1.selectedTermOfClass(selectedClassSection, conn, session, schid);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=new DatabaseMethods1().allSection(selectedClassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClassSection,conn);
		}
		show = false;
		studentList = new ArrayList<StudentInfo>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public FinalBLMMarkSheetBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = obj1.schoolId();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		boolean check = (boolean) ss.getAttribute("checkstu");
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		session = obj1.selectedSessionDetails(schid, conn);
		classSection = obj1.allClass(conn);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classSection=obj1.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection=obj1.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection=obj1.allClassListForClassTeacher(empid,schid,conn);
		}

		if (check == true) {
			String addNumber = (String) ss.getAttribute("username");
			StudentInfo sn = objExam.studentBasicDetailsForMarksheet(schid, addNumber, conn, "singleStudent", "")
					.get(0);
			studentList.add(sn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query) {
		Connection conn = DataBaseConnection.javaConnection();
		studentList = objExam.studentBasicDetailsForMarksheet(schid, "", conn, "like", query);
		List<String> studentListt = new ArrayList<>();
		for (StudentInfo info : studentList) {
			studentListt.add(info.getFname() + " / " + info.getFathersName() + " / " + info.getSrNo() + "-"
					+ info.getClassName() + "-" + info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudentByName() {
		Connection conn = DataBaseConnection.javaConnection();
		int index = name.lastIndexOf("-") + 1;
		String id = name.substring(index);
		if (index != 0) {
			String selClass = objExam.studentBasicDetailsForMarksheet(schid, id, conn, "singleStudent", "").get(0)
					.getClassId();
			selectedClassSection = selClass;
			ExamSettingInfo examInfo = objExam.examSettingDetail(selectedClassSection, conn);
			termList = obj1.selectedTermOfClass(selClass, conn, session, schid);

			for (StudentInfo info : studentList) {
				if (String.valueOf(info.getAddNumber()).equals(id)) {
					studentList = new ArrayList<>();
					if (examInfo != null) {
						markListCoscholastic = de.subjectListForStudent(selectedClassSection, session, info.getSrNo(),
								info.getSectionid(), "coscholastic", conn);
						studentList.add(info);
						renderingMethod();
						break;
					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage("Please Add Exam Setting First For Related Class"));
					}
				}
			}
		} else {
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Note: Please Select Student Name From Autocomplete List"));
		}

		if (studentList.size() <= 0) {
			show = false;
			showProceed = false;
			showFill = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allExamListForPeriodic() {
		Connection conn = DataBaseConnection.javaConnection();
		int on = 0;
		if (selectedTerm.size() > 0) {
			allExamList = new ArrayList<>();
			size += selectedTerm.size();
			for (String term : selectedTerm) {
				termId = term;
				termName = new DatabaseMethods1().semesterNameFromid(term, conn);
				ArrayList<ExamInfo> examInformation = objExam.getAllExamsForClass(selectedClassSection, "scholastic",
						schid, session, termId, conn);
				if (examInformation.size() > 0) {
					for (ExamInfo exam : examInformation) {
						if (exam.getExamTaken().equalsIgnoreCase("periodicTest")) {
							if (exam.getInclude().equalsIgnoreCase("yes")) {
								on = 1;
								examId = String.valueOf(exam.getId());
								examName = exam.getExamName();
								SelectItem ll = new SelectItem();
								ll.setLabel(termName + " - " + examName);
								ll.setValue(termId + " - " + examId);
								allExamList.add(ll);
							}
						}
					}
					
				}
			}
			if(on == 0) {
				showExamPeriodic = false;
			}else {
				showExamPeriodic = true;
			}
		} else {
			showExamPeriodic = false;
			allExamList = new ArrayList<>();
			size = 1;
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void selectAll() {
		if(checkVal==false) {
			selectedTerm = new ArrayList<>();
			for(int i=0;i<termList.size();i++) {
				selectedTerm.add(i, termList.get(i).getValue().toString());
			}
			getExams();
		}else {
			selectedTerm = new ArrayList<>();
			showExamAdd=false;
			showExam=false;
		}
		
	}

	
	public void unselect() {
		checkVal=false;
		 // System.out.println("in un select");
	}
	
	public void getExams() {
		int count = 0;
		int on = 0;
		int on2= 0;
		allExamList = new ArrayList<>();
		allExamListForAdditional = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		if (selectedTerm.size() > 0) {
			size += selectedTerm.size();
			for (String term : selectedTerm) {
				count = 0;
				termId = term;
				termName = new DatabaseMethods1().semesterNameFromid(term, conn);
				ArrayList<ExamInfo> examInformation = objExam.getAllExamsForClass(selectedClassSection, "scholastic",
						schid, session, termId, conn);
				if (examInformation.size() > 0) {
					StringBuilder str = new StringBuilder();
					for (ExamInfo exam : examInformation) {
						if (exam.getExamTaken().equalsIgnoreCase("periodicTest")) {
							if (exam.getInclude().equalsIgnoreCase("yes")) {
								on = 1;
								count = 1;
								str.append(exam.getId() + "','");
							}
						}
					}
					if(count==1) {
						examId = str.toString().substring(0, str.toString().length() - 3);
						SelectItem ll = new SelectItem();
						ExamSettingInfo examInfo = objExam.examSettingDetail(selectedClassSection, conn);
						examName = examInfo.getExamName();
						ll.setLabel(termName + " - " + examName);
						ll.setValue(termId + " - " + examId);
						allExamList.add(ll);
					}
					for (ExamInfo exam : examInformation) {
						examId = String.valueOf(exam.getId());
						if (exam.getId() > 0) {
							if (!exam.getExamTaken().equalsIgnoreCase("periodicTest")) {
								if (exam.getInclude().equalsIgnoreCase("yes")) {
									on = 1;
									examName = exam.getExamName();
									SelectItem al = new SelectItem();
									al.setLabel(termName + " - " + examName);
									al.setValue(termId + " - " + examId);
									allExamList.add(al);
								}
							}
						}
					}
				} 
				ArrayList<ExamInfo> examInformationForAdditional = objExam.getAllExamsForClass(selectedClassSection,
						"additional", schid, session, termId, conn);
				if (examInformationForAdditional.size() > 0) {
					
					showExamAdd = true;
					for (ExamInfo exmAdd : examInformationForAdditional) {
						if (exmAdd.getInclude().equalsIgnoreCase("yes")) {
							on2 = 1;
							SelectItem al = new SelectItem();
							al.setLabel(termName + " - " + exmAdd.getExamName());
							al.setValue(termId + " - " + exmAdd.getId());
							allExamListForAdditional.add(al);
						}
					}
				}
			}
		} else {
			showExam = false;
			showExamAdd = false;
			allExamList = new ArrayList<>();
			allExamListForAdditional = new ArrayList<>();
			size = 1;
		}
		if(on2==0) {
			showExamAdd = false;
		}else {
			showExamAdd = true;
		}
		
		if(on==0) {
			showExam = false;
		}else {
			 // System.out.println("show exam");
			showExam = true;
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchStudentByClassSection() {
		Connection conn = DataBaseConnection.javaConnection();
		ExamSettingInfo examInfo = objExam.examSettingDetail(selectedClassSection, conn);
		if (examInfo != null) {
			studentList = objExam.studentBasicDetailsForMarksheet(schid, "", conn, "byClass", selectedSection);
			if (studentList.size() > 0) {

				renderingMethod();

			} else {
				show = false;
				showProceed = false;
				showFill = false;
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("No Student Found! Please Try Again."));
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please Add Exam Setting First For Related Class"));
		}

	}

	public void common() {
		PrimeFaces.current().executeInitScript("PF('printDialog').hide()");
		PrimeFaces.current().ajax().update(":printForm");

		PrimeFaces.current().executeInitScript("PF('printDialog2').hide()");
		PrimeFaces.current().ajax().update(":printForm2");

		PrimeFaces.current().executeInitScript("PF('fillDialog').hide()");
		PrimeFaces.current().ajax().update(":fillForm");

		PrimeFaces.current().executeInitScript("PF('markFillDialog').hide()");
		PrimeFaces.current().ajax().update(":markFillForm");

		comeTestType = "all";
	}

	public void renderingMethod() {
		show = true;
		if ((selectedClassSection.equals("1") || selectedClassSection.equals("2") || selectedClassSection.equals("3"))
				&& schid.equals("252")) {
			showProceed = false;
			showFill = true;
		} else {
			showProceed = true;
			showFill = false;
		}

		if (schid.equals("251") /* ||schid.equals("252") */)
			showCoButton = true;
		else
			showCoButton = false;

//		if(schid.equals("289"))
//		{
//			printBtnForInitium=true;printBtnForOther=false;
//		}
//		else
//		{
		printBtnForOther = true;
		printBtnForInitium = false;
//		}
	}

	public void proceed() {
		Connection conn = DataBaseConnection.javaConnection();
		if (selectedStudentList.isEmpty()) {
			common();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Student"));
		} else if (selectedTerm == null || selectedTerm.size() == 0) {
			common();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Term Or Create a Term for printing marksheet"));
		} else if (selectdExam == null || selectdExam.size() == 0) {
			common();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Exam Or Create an Exam for printing marksheet"));
		} else {

			studentList = selectedStudentList;

			boolean checkExamSetting = obj1.checkExamSettingMade(studentList.get(0).getClassId(), session, schid, conn);
			if (checkExamSetting) {
				if (dateOfEntry != null) {
					String termId = "", classid = "", sectionid = "", examID = "", addExamId = "", addTerm = "";
					classid = studentList.get(0).getClassId();
					sectionid = studentList.get(0).getSectionid();
					String pageName = objExam.marksheetPageName(schid, classid, conn);
//					if(schid.equals("289"))
//						termId=selTermInitium;
//					else
//					{
//						for(String term:selectedTerm)
//						{
//							termId+=term+"','";
//						}
//						termId=termId.substring(0,termId.lastIndexOf("','"));
//					}

					for (String term : selectdExam) {
						String[] temp = term.split("-");
						if (!termId.contains(temp[0].trim())) {
							termId += temp[0].trim() + "','";
						}
						examID += temp[1].trim() + "','";
					}
					termId = termId.substring(0, termId.lastIndexOf("','"));
					examID = examID.substring(0, examID.lastIndexOf("','"));

					
					
					if(selectdExamAdditional!=null) {
						if (selectdExamAdditional.size() > 0) {
							for (String x : selectdExamAdditional) {
								String tmp[] = x.split("-");
								if (!addTerm.contains(tmp[0].trim())) {
									addTerm += tmp[0].trim() + "','";
								}
								addExamId += tmp[1].trim() + "','";
							}
							addTerm = addTerm.substring(0, addTerm.lastIndexOf("','"));
							addExamId = addExamId.substring(0, addExamId.lastIndexOf("','"));
						}
					}

					
					HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
							.getSession(false);
					ss.setAttribute("StudentList", studentList);
					ss.setAttribute("comeTestType", comeTestType);
					ss.setAttribute("session", DatabaseMethods1.selectedSessionDetails(schid, conn));
					ss.setAttribute("examId", examID);
					ss.setAttribute("additional", addExamId);
					ss.setAttribute("addTerm", addTerm);
					ss.setAttribute("dateOfEntry", dateOfEntry);
					ss.setAttribute("termId", termId);
					ss.setAttribute("classid", classid);
					ss.setAttribute("sectionid", sectionid);

					selectedTerm = null;
					selTermInitium = "";

					if (pageName.equals("no_page")) {
						if (comeTestType.equals("periodic"))
							PrimeFaces.current().
									executeScript("window.open('printPeriodicTestProgressReport.xhtml')");
						else
							PrimeFaces.current().executeInitScript("window.open('printFinalMarksheet.xhtml')");
					} else {
						if (/* schid.equals("251") && */comeTestType.equals("periodic"))
							PrimeFaces.current().
									executeScript("window.open('printPeriodicTestProgressReport.xhtml')");
						else
							PrimeFaces.current().executeInitScript("window.open('" + pageName + "')");
					}
					show = showProceed = showFill = false;
					name = selectedClassSection = null;
					selectedStudent = null;
					selectdExam = null;
					allExamList = null;
					selectdExamAdditional = null;
					showExam=false;
					showExamAdd=false;
					showExamPeriodic=false;
					common();
				} else {
					common();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Date"));
				}
			} else {
				common();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Please Add Exam Setting for selected class to proceed further"));
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void proceedCo() {
		Connection conn = DataBaseConnection.javaConnection();

		if (selectedStudentList.isEmpty()) {
			common();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Student"));
		} else {

			studentList = selectedStudentList;

			boolean checkExamSetting = obj1.checkExamSettingMade(studentList.get(0).getClassId(), session, schid, conn);
			if (checkExamSetting) {

				if (dateOfEntry != null) {
					HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
							.getSession(false);
					ss.setAttribute("StudentList", studentList);
					ss.setAttribute("session", DatabaseMethods1.selectedSessionDetails(schid, conn));
					ss.setAttribute("dateOfEntry", dateOfEntry);
					ss.setAttribute("markListCoscholastic", markListCoscholastic);
					ss.setAttribute("rollNo", rollNo);

					if (schid.equals("251") || schid.equals("252"))
						PrimeFaces.current().executeInitScript("window.open('printCoFormatMarksheet.xhtml')");

					show = showProceed = showFill = false;
					name = selectedClassSection = null;
					selectedStudent = null;
					common();
				} else {
					common();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Date"));
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Please Add Exam Setting for selected class to proceed further"));
				common();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fillMarks() {

		if (selectedStudentList.isEmpty()) {
			common();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Student"));
		} else {

			studentList = selectedStudentList;

			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("StudentList", studentList);
			ss.setAttribute("dateOfEntry", dateOfEntry);
			ss.setAttribute("termId", termId);

			selectedTerm = null;
			if (schid.equals("252"))
				PrimeFaces.current().executeInitScript("window.open('fillBLM_PreClassMarks.xhtml')");
			show = showProceed = showFill = false;
			name = selectedClassSection = null;
			selectedStudent = null;
			common();
		}
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
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

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getSelectedClassSection() {
		return selectedClassSection;
	}

	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}

	public Date getDateOfEntry() {
		return dateOfEntry;
	}

	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public ArrayList<String> getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(ArrayList<String> selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public boolean isShowFill() {
		return showFill;
	}

	public void setShowFill(boolean showFill) {
		this.showFill = showFill;
	}

	public boolean isShowProceed() {
		return showProceed;
	}

	public void setShowProceed(boolean showProceed) {
		this.showProceed = showProceed;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public boolean isShowCoButton() {
		return showCoButton;
	}

	public void setShowCoButton(boolean showCoButton) {
		this.showCoButton = showCoButton;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}

	public ArrayList<SubjectInfo> getMarkListCoscholastic() {
		return markListCoscholastic;
	}

	public void setMarkListCoscholastic(ArrayList<SubjectInfo> markListCoscholastic) {
		this.markListCoscholastic = markListCoscholastic;
	}

	public boolean isPrintBtnForInitium() {
		return printBtnForInitium;
	}

	public void setPrintBtnForInitium(boolean printBtnForInitium) {
		this.printBtnForInitium = printBtnForInitium;
	}

	public String getSelTermInitium() {
		return selTermInitium;
	}

	public void setSelTermInitium(String selTermInitium) {
		this.selTermInitium = selTermInitium;
	}

	public boolean isPrintBtnForOther() {
		return printBtnForOther;
	}

	public void setPrintBtnForOther(boolean printBtnForOther) {
		this.printBtnForOther = printBtnForOther;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public ArrayList<SelectItem> getAllExamList() {
		return allExamList;
	}

	public void setAllExamList(ArrayList<SelectItem> allExamList) {
		this.allExamList = allExamList;
	}

	public boolean isShowExam() {
		return showExam;
	}

	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}

	public ArrayList<String> getSelectdExam() {
		return selectdExam;
	}

	public void setSelectdExam(ArrayList<String> selectdExam) {
		this.selectdExam = selectdExam;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<String> getSelectdExamAdditional() {
		return selectdExamAdditional;
	}

	public void setSelectdExamAdditional(ArrayList<String> selectdExamAdditional) {
		this.selectdExamAdditional = selectdExamAdditional;
	}

	public ArrayList<SelectItem> getAllExamListForAdditional() {
		return allExamListForAdditional;
	}

	public void setAllExamListForAdditional(ArrayList<SelectItem> allExamListForAdditional) {
		this.allExamListForAdditional = allExamListForAdditional;
	}

	public boolean isShowExamAdd() {
		return showExamAdd;
	}

	public void setShowExamAdd(boolean showExamAdd) {
		this.showExamAdd = showExamAdd;
	}

	public boolean isShowExamPeriodic() {
		return showExamPeriodic;
	}

	public void setShowExamPeriodic(boolean showExamPeriodic) {
		this.showExamPeriodic = showExamPeriodic;
	}

	public boolean isCheckVal() {
		return checkVal;
	}

	public void setCheckVal(boolean checkVal) {
		this.checkVal = checkVal;
	}
}