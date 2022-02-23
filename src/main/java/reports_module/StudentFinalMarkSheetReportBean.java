package reports_module;

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

import exam_module.DataBaseMethodsBLMExam;
import exam_module.DataBaseMethodsExam;
import exam_module.ExamInfo;
import exam_module.SubExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import schooldata.TermInfo;
import session_work.QueryConstants;

@ManagedBean(name="finalMarksheetReport")
@ViewScoped
public class StudentFinalMarkSheetReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList=new ArrayList<>();

	boolean show,disableSubject,showSubExamRow;
	ArrayList<SelectItem> classSection,sectionList,subjectList,termNewList,subjectTypeList;
	ArrayList<String> scholasticColumnsList = new ArrayList<>(),selectedTerm=new ArrayList<>();
	String selectedClassSection,selectedSection,selectedSubject,subjectType;
	String className,sectionName,subjectName,schid,session;
	StudentInfo selectedStudent;
	ArrayList<TermInfo> termList;
	int termSize=0;
	ExamSettingInfo examSetting=new ExamSettingInfo();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsBLMExam objBLM=new DataBaseMethodsBLMExam();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();

	public StudentFinalMarkSheetReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		boolean check=(boolean) ss.getAttribute("checkstu");
		String addNumber=(String) ss.getAttribute("username");
		schid=obj1.schoolId();
		session=obj1.selectedSessionDetails(schid, conn);
		subjectTypeList=obj1.subjectTypeList();
		if(check==false)
		{
			classSection=obj1.allClass(conn);
		}
		else
		{
			ArrayList<String> list=objStd.basicFieldsForStudentList();
			StudentInfo sn=objStd.studentDetail(addNumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schid, list, conn).get(0);
			
			studentList.add(sn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=obj1.allSection(selectedClassSection,conn);
		termNewList=obj1.selectedTermOfClass(selectedClassSection,conn,session,schid);
		subjectType="";selectedSubject="";
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void allSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(subjectType.equals("coscholastic"))
		{
			disableSubject=true;
		}
		else
		{
			disableSubject=false;
			subjectList=objExam.subjectListBySubjectTypeForExamSelectItem(selectedClassSection,subjectType, conn,session,schid);
		}
		selectedSubject="";

		try{
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public void searchReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=objExam.studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
		examSetting=objExam.examSettingDetail(selectedClassSection, conn);
		if(studentList.size()>0)
		{
			show=true;
			className=studentList.get(0).getClassName();
			sectionName=studentList.get(0).getSectionName();

			String termId="";
			for(String term:selectedTerm)
			{
				termId+=term+"','";
			}
			termId=termId.substring(0,termId.lastIndexOf("','"));
			termList=objBLM.termListByClassTermId(termId, selectedClassSection, session, subjectType, conn, "all","","","","","yes","FINAL MARKS","","","yes","PERCENT","","","",schid);

			ArrayList<SubjectInfo> subjectList=new ArrayList<>();
			if(subjectType.equals("scholastic")||subjectType.equals("additional"))
			{
				subjectName=obj1.subjectNameFromid(selectedSubject, conn);
				SubjectInfo item=new SubjectInfo();
				item.setSubjectId(selectedSubject);
				item.setSubjectName(subjectName);
				item.setHeader(true);
				item.setDescription("single");
				subjectList.add(item);
			}
			else
			{
				subjectList=objExam.subjectListBySubjectTypeForExam(selectedClassSection, subjectType, conn);
			}
			makeScholasticColumnList(subjectList);
			String comeTestType="all";
			for(StudentInfo info: studentList)
			{
				if(subjectType.equals("scholastic"))
				{
					objExam.fillStudentMarks(selectedSection, info.getAddNumber(), subjectList, session, termList, conn, info, scholasticColumnsList, termSize, subjectType, subjectList.size(), examSetting, "fullreport",comeTestType,schid);
				}
				else
				{
					objBLM.fillMarksForCoscholasticSubject(selectedSection, subjectList, info.getAddNumber(), session, termList,subjectType, conn, info,"fullreport");
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}

		try
		{
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void makeScholasticColumnList(ArrayList<SubjectInfo> subjectList)
	{
		int ptFlag=0,count=0;
		scholasticColumnsList.clear();
		ArrayList<SubjectInfo> tempSubjectList=new ArrayList<>();
		for(TermInfo term : termList)
		{
			ptFlag=0;
			if(Integer.parseInt(term.getTermId())>0)
			{
				termSize++;
				term.setSubjectList(subjectList);
				for(ExamInfo exam : term.getExamInfoList())
				{
					if(exam.getInclude().equals("Yes"))
					{
						if(exam.getSubExamList()!=null)
						{
							if(exam.getExamTaken().equals("other"))
							{
								for(SubExamInfo subExam : exam.getSubExamList())
								{
									if(!subExam.getSubExamUpperCase().trim().equals(""))
										count++;
									for(SubjectInfo subject:subjectList)
										scholasticColumnsList.add("term-"+term.getTermId()+"-mainExam-"+subExam.getMainExamId()+"-subExam-"+subExam.getSubExamName()+"-subject-"+subject.getSubjectId());
								}
							}
							else
							{
								if(ptFlag==0)
								{
									for(SubjectInfo subject:subjectList)
									{
										scholasticColumnsList.add("term-"+term.getTermId()+"-mainExam-P.T.-subExam-"+examSetting.getExamName()+"-subject-"+subject.getSubjectId());
										ptFlag=1;
									}
								}
							}
						}
						else
						{
							for(SubjectInfo subject:subjectList)
							{
								scholasticColumnsList.add("term-"+term.getTermId()+"-mainExam--subExam--subject-"+subject.getSubjectId());
							}
						}
					}
				}
			}
			else if(term.getTermId().equals("-9"))
			{
				tempSubjectList.clear();
				SubjectInfo sub=new SubjectInfo();
				sub.setSubjectName("");
				sub.setDescription("");
				sub.setHeader(true);
				sub.setSubjectId("finalTotal");
				tempSubjectList.add(sub);
				term.setSubjectList(tempSubjectList);
				scholasticColumnsList.add("-9-finalTotal");
			}
			else if(term.getTermId().equals("-11"))
			{
				tempSubjectList.clear();
				SubjectInfo sub=new SubjectInfo();
				sub.setSubjectName("");
				sub.setDescription("");
				sub.setHeader(true);
				sub.setSubjectId("finalPercent");
				tempSubjectList.add(sub);
				term.setSubjectList(tempSubjectList);
				scholasticColumnsList.add("-11-finalPercent");
			}
			
		}
		if(count!=0)
			showSubExamRow=true;
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
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getScholasticColumnsList() {
		return scholasticColumnsList;
	}

	public void setScholasticColumnsList(ArrayList<String> scholasticColumnsList) {
		this.scholasticColumnsList = scholasticColumnsList;
	}

	public String getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}

	public ArrayList<TermInfo> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<TermInfo> termList) {
		this.termList = termList;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}


	public boolean isDisableSubject() {
		return disableSubject;
	}

	public void setDisableSubject(boolean disableSubject) {
		this.disableSubject = disableSubject;
	}

	public boolean isShowSubExamRow() {
		return showSubExamRow;
	}

	public void setShowSubExamRow(boolean showSubExamRow) {
		this.showSubExamRow = showSubExamRow;
	}

	public ArrayList<SelectItem> getTermNewList() {
		return termNewList;
	}

	public void setTermNewList(ArrayList<SelectItem> termNewList) {
		this.termNewList = termNewList;
	}

	public ArrayList<String> getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(ArrayList<String> selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}
}
