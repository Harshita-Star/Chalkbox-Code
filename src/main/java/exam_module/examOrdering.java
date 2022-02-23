package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;

@ManagedBean(name="examRank")
@ViewScoped
public class examOrdering implements Serializable{
	
	ArrayList<SelectItem> classList,sectionList,termList;
	ArrayList<ExamInfo> examList = new ArrayList<>();
	ArrayList<SelectItem> typeList = new ArrayList<>();
	String selectedClassSection,selectedSection,rank,schoolId,sessionValue,selectedType,
	selectedterm,username,userType;
	DataBaseMethodsExam dbmExam = new DataBaseMethodsExam();
	boolean show;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	
	public examOrdering() {
		Connection conn = DataBaseConnection.javaConnection();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=dbm.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList=dbm.cordinatorClassList(empid, schoolId, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList=dbm.allClassListForClassTeacher(empid,schoolId,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getType() {
		if(!selectedClassSection.equals("")) {
			typeList = new ArrayList<>();
			SelectItem s1 = new SelectItem();
			SelectItem s2 = new SelectItem();
			s1.setLabel("Scholastic");
			s1.setValue("scholastic");
			s2.setLabel("Additional");
			s2.setValue("additional");
			typeList.add(s1);
			typeList.add(s2);
		}
	}
	
	public void getTerm() {
		Connection conn = DataBaseConnection.javaConnection();
		ExamSettingInfo examInfo=dbmExam.examSettingDetail(selectedClassSection, conn);
		if(examInfo!=null)
		{
			termList=dbm.selectedTermOfClass(selectedClassSection,conn,sessionValue,schoolId);
		}
		else
		{
			selectedClassSection="";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add Exam Setting First For Related Class"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search() {
		Connection conn = DataBaseConnection.javaConnection();
		examList = dbmExam.getAllExamsForClass(selectedClassSection,selectedType,schoolId,sessionValue,selectedterm,conn);
		if(examList.size()>0) {
			show = true;
		}else {
			show = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("There is no Exam in this Type."));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void submit() {
		Connection conn = DataBaseConnection.javaConnection();
		int status = dbmExam.setExamOrdering(examList,conn);
		if(status>0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exam Order is Updated."));
			examList = dbmExam.getAllExamsForClass(selectedClassSection,selectedType,schoolId,sessionValue,selectedterm,conn);
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public String getSelectedClassSection() {
		return selectedClassSection;
	}

	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getSessionValue() {
		return sessionValue;
	}

	public void setSessionValue(String sessionValue) {
		this.sessionValue = sessionValue;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<ExamInfo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<ExamInfo> examList) {
		this.examList = examList;
	}

	public ArrayList<SelectItem> getTypeList() {
		return typeList;
	}

	public void setTypeList(ArrayList<SelectItem> typeList) {
		this.typeList = typeList;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public String getSelectedterm() {
		return selectedterm;
	}

	public void setSelectedterm(String selectedterm) {
		this.selectedterm = selectedterm;
	}
	
}
