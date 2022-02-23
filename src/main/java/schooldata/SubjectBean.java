package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;

@ManagedBean(name="subjectBean")
@ViewScoped
public class SubjectBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> classList;

	String type="scholastic",addInExam="yes";
	String selectedClass;
	String subjectName;

	String subType="Mandatory";
	ArrayList<SelectItem> sectionList,subjectTypeList;
	ArrayList<String> selectedClassList;
	ArrayList<String> selectedSection;
	ArrayList<StudentInfo> studentList=new ArrayList<>(),selectedStudentList=new ArrayList<>();
	boolean showSection,showStudent,showClass,showAllClass=true,showInstitute;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public SubjectBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		subjectTypeList=obj.subjectTypeList();
		String ctype=obj.checkClientType(conn);
		if(ctype.equalsIgnoreCase("institute"))
		{
			showInstitute=false;
		}
		else
		{
			showInstitute=true;
		}
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username=(String) ss.getAttribute("username");
		String userType=(String) ss.getAttribute("type");

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=new DatabaseMethods1().allClass(conn);
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList = obj.cordinatorClassList(empid, schoolId, conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkSubjectType()
	{
		selectedClass="";
		selectedSection=new ArrayList<>();
		selectedClassList=new ArrayList<>();
		if(subType.equalsIgnoreCase("Optional"))
		{
			//sectionList=new DatabaseMethods1().allSection(selectedClass);
			showClass=true;
			showSection=true;
			showAllClass=false;
		}
		else
		{
			showAllClass=true;
			showClass=false;showSection=false;showStudent=false;
		}
	}

	public void checkSectionList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editSubject()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editSubjects.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public void editSubjectStudents()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editOptionalSubjectStudents.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public void checkSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList.clear();
		selectedStudentList=new ArrayList<>();
		if(selectedSection.contains("All"))
		{
			for(SelectItem section:sectionList)
			{
				studentList.addAll(obj.searchStudentListByClassSection(selectedClass, String.valueOf(section.getValue()),conn));
			}
		}
		else
		{
			for(String section:selectedSection)
			{
				studentList.addAll(obj.searchStudentListByClassSection(selectedClass,section,conn));
			}
		}

		int count=1;
		for(StudentInfo info:studentList)
		{
			info.setSno(count++);
		}
		showStudent=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
	
		String className=obj.classname(selectedClass, schoolId, conn);

		language = subjectName +" --- "+className+" --- "+" --- "+selectedSection+" --- "+type+" --- "+addInExam+" --- "+subType;
		
		for(StudentInfo  ss :selectedStudentList)
		{	
		   value += "("+ss.getAddNumber()+")";
		}
		String refNo = workLg.saveWorkLogMehod(language,"Add Subjects Optional","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn,String clasId)
	{
	    String value = "";
		String language= "";
		
		String className=obj.classname(clasId, schoolId, conn);
		
		language = subjectName +" --- "+type+" --- "+addInExam+" --- "+subType+" --- "+selectedClassList;
		value = subjectName +" --- "+className+" --- "+type+" --- "+addInExam+" --- "+subType;
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Subjects","WEB",value,conn);
		return refNo;
	}


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public ArrayList<SelectItem> getClassList() {

		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<String> getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(ArrayList<String> selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public boolean isShowSection() {
		return showSection;
	}

	public void setShowSection(boolean showSection) {
		this.showSection = showSection;
	}

	public boolean isShowStudent() {
		return showStudent;
	}

	public void setShowStudent(boolean showStudent) {
		this.showStudent = showStudent;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}


	public String addSubjects()
	{
		int flag=0;
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseValidator objValidate=new DataBaseValidator();

		if(subType.equals("Optional"))
		{
			/*int st=objValidate.allowAddSubject(selectedClass,conn);
			st=0;
			if(st==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Exam has been added in this class,so you can't add more subjects.", "Validation error"));
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note- To add more subjects, delete the all the exams from this class.", "Validation error"));
			}
			else
			{*/
				if(flag==0)
				{

					int status=objValidate.duplicateSubject(sessionValue,selectedClass, subjectName,type,schoolId,conn);
					if(status==0)
					{
						flag++;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
					}
				}
				if(flag==0)
				{
					try
					{
						int i=obj.addSubjects(subjectName, selectedClass,type,subType,selectedStudentList,addInExam,conn);
						if(i==1)
						{
							String refNo;
							try {
								refNo=addWorkLog(conn);
							} catch (Exception e) {
								// TODO: handle exception
							}
							FacesContext fc=FacesContext.getCurrentInstance();
							fc.addMessage(null, new FacesMessage("Subject added successfully"));
							addInExam="yes";
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			//}
			selectedClass=null;
			selectedSection=new ArrayList<>();
			selectedClassList=new ArrayList<>();
			selectedStudentList=new ArrayList<>();
			subjectName=null;
			type=null;subType="Mandatory";
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "addSubjects";
		}
		else
		{
			int j=0;
			for(String clsId:selectedClassList)
			{
				String clsName = obj.classNameFromidSchid(schoolId,clsId, sessionValue, conn);
				/*for(SelectItem si:classList)
				{
					if(clsId.equals(si.getValue()));
					{
						clsName=si.getLabel();
					}
				}*/


				/*int st=new DataBaseValidator().allowAddSubject(clsId,conn);
				st=0;
				if(st==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Exam has been added in "+clsName+" class,so you can't add more subjects.", "Validation error"));
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note- To add more subjects, delete the all the exams from this class.", "Validation error"));
				}
				else
				{*/
					if(flag==0)
					{

						int status=objValidate.duplicateSubject(sessionValue,clsId, subjectName,type,schoolId,conn);
						if(status==0)
						{
							flag++;
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate subject found in class "+clsName+" ,try a different one", "Validation error"));
						}
					}
					if(flag==0)
					{
						try
						{
							j=obj.addSubjects(subjectName, clsId,type,subType,selectedStudentList,addInExam,conn);
							if(j==1)
							{
								String refNo2;
								try {
									refNo2=addWorkLog2(conn,clsId);
								} catch (Exception e) {
									// TODO: handle exception
								}
								FacesContext fc=FacesContext.getCurrentInstance();
								fc.addMessage(null, new FacesMessage("Subject added successfully in Class "+clsName));

							}
							else
							{
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
							}
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
					}

				//}
			}


			selectedClass=null;
			selectedSection=new ArrayList<>();
			selectedClassList=new ArrayList<>();
			selectedStudentList=new ArrayList<>();
			subjectName=null;
			type=null;subType="Mandatory";

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "addSubjects";
		}



	}

	public void addSubjectSubMethod()
	{

	}
	public String no()
	{
		return "AddClassAndSectionView";
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public boolean isShowAllClass() {
		return showAllClass;
	}

	public void setShowAllClass(boolean showAllClass) {
		this.showAllClass = showAllClass;
	}

	public ArrayList<String> getSelectedClassList() {
		return selectedClassList;
	}

	public void setSelectedClassList(ArrayList<String> selectedClassList) {
		this.selectedClassList = selectedClassList;
	}

	public boolean isShowInstitute() {
		return showInstitute;
	}

	public void setShowInstitute(boolean showInstitute) {
		this.showInstitute = showInstitute;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public String getAddInExam() {
		return addInExam;
	}

	public void setAddInExam(String addInExam) {
		this.addInExam = addInExam;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
