package exam_module;

import java.io.IOException;
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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;

@ManagedBean(name="fillBLMPreMarks")
@ViewScoped
public class FillBLM_PreClassMarksBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	StudentInfo studentList;
	String termId;
	ArrayList<StudentInfo> listOfStudent ;
	ArrayList<SelectItem> eqValueList;
	String dateEntry,rollNo;
	ArrayList<SubjectInfo> iqSubjectList,eqSubList,sqSubList,pmdSubList,lifeScienceSubList,csSubList,csoeSubList,otherSubjectlist;
	String fName,fatherName,sectionid,className,sno,session,schoolId,motherName,dobString,fathersphone;
	DataBaseMethodsBLMExam de=new DataBaseMethodsBLMExam();
	DatabaseMethods1 obj1=new DatabaseMethods1();


	public FillBLM_PreClassMarksBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schoolId=obj1.schoolId();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session=DatabaseMethods1.selectedSessionDetails(obj1.schoolId(),conn);
		listOfStudent=(ArrayList<StudentInfo>) ss.getAttribute("StudentList");
		termId=(String) ss.getAttribute("termId");
		makeList();

		for(StudentInfo allInfo : listOfStudent)
		{
			allInfo.setSession(session);

			sectionid=allInfo.getSectionid();
			fName=allInfo.getFname();
			fatherName=allInfo.getFathersName();
			className=allInfo.getClassName()+"-"+allInfo.getSectionName();
			motherName=allInfo.getMotherName();
			dobString=allInfo.getDobString();
			sno=String.valueOf(allInfo.getAddNumber());
			rollNo=allInfo.getRollNo();
			fathersphone=String.valueOf(allInfo.getFathersPhone());

			iqSubjectList=de.subjectListForPreClass("I.Q.",conn);
			eqSubList=de.subjectListForPreClass("E.Q.",conn);
			sqSubList=de.subjectListForPreClass("SQ",conn);
			pmdSubList=de.subjectListForPreClass("PMD",conn);
			lifeScienceSubList=de.subjectListForPreClass("LifeScience",conn);
			csSubList=de.subjectListForPreClass("CS",conn);
			csoeSubList=de.subjectListForPreClass("CS_/",conn);
			otherSubjectlist=de.subjectListForPreClass("other", conn);


			de.checkMarksForPreClass(allInfo,iqSubjectList,termId,conn);
			de.checkMarksForPreClass(allInfo,eqSubList,termId,conn);
			de.checkMarksForPreClass(allInfo,sqSubList,termId,conn);
			de.checkMarksForPreClass(allInfo,lifeScienceSubList,termId,conn);
			de.checkMarksForPreClass(allInfo,pmdSubList,termId,conn);
			de.checkMarksForPreClass(allInfo,csSubList,termId,conn);
			de.checkMarksForPreClass(allInfo,csoeSubList,termId,conn);
			de.checkMarksForPreClass(allInfo,otherSubjectlist,termId,conn);

			allInfo.setIqSubjectList(iqSubjectList);
			allInfo.setEqSubList(eqSubList);
			allInfo.setSqSubList(sqSubList);
			allInfo.setLifeScienceSubList(lifeScienceSubList);
			allInfo.setPmdSubList(pmdSubList);
			allInfo.setCsSubList(csSubList);
			allInfo.setCsoeSubList(csoeSubList);
			allInfo.setOtherSubjectlist(otherSubjectlist);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void makeList()
	{
		eqValueList=new ArrayList<>();
		SelectItem ll=new SelectItem();
		ll.setLabel("JOY");
		ll.setValue("JOY");
		eqValueList.add(ll);

		ll=new SelectItem();
		ll.setLabel("JOY1");
		ll.setValue("JOY1");
		eqValueList.add(ll);

		ll=new SelectItem();
		ll.setLabel("JOY2");
		ll.setValue("JOY2");
		eqValueList.add(ll);
		ll=new SelectItem();
		ll.setLabel("JOY3");
		ll.setValue("JOY3");
		eqValueList.add(ll);

	}

	public void submitMarks()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		boolean i = false;
		for(StudentInfo allInfo: listOfStudent)
		{
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getIqSubjectList(), "-1",termId, conn);
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getEqSubList(), "-1", termId, conn);
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getSqSubList(), "-1", termId, conn);
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getLifeScienceSubList(), "-1", termId, conn);
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getPmdSubList(), "-1", termId, conn);
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getCsSubList(), "-1", termId, conn);
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getCsoeSubList(), "-1", termId, conn);
			i=de.addPreStudentPerformance(sectionid, "main", "sub", allInfo, "examType", allInfo.getOtherSubjectlist(), "-1", termId, conn);
		}
		if(i==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Sucessfully"));
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("finalBLMMarksheet.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public StudentInfo getStudentList() {
		return studentList;
	}

	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getListOfStudent() {
		return listOfStudent;
	}

	public void setListOfStudent(ArrayList<StudentInfo> listOfStudent) {
		this.listOfStudent = listOfStudent;
	}

	public String getDateEntry() {
		return dateEntry;
	}

	public void setDateEntry(String dateEntry) {
		this.dateEntry = dateEntry;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public ArrayList<SubjectInfo> getIqSubjectList() {
		return iqSubjectList;
	}

	public void setIqSubjectList(ArrayList<SubjectInfo> iqSubjectList) {
		this.iqSubjectList = iqSubjectList;
	}

	public ArrayList<SubjectInfo> getEqSubList() {
		return eqSubList;
	}

	public void setEqSubList(ArrayList<SubjectInfo> eqSubList) {
		this.eqSubList = eqSubList;
	}

	public ArrayList<SubjectInfo> getSqSubList() {
		return sqSubList;
	}

	public void setSqSubList(ArrayList<SubjectInfo> sqSubList) {
		this.sqSubList = sqSubList;
	}

	public ArrayList<SubjectInfo> getPmdSubList() {
		return pmdSubList;
	}

	public void setPmdSubList(ArrayList<SubjectInfo> pmdSubList) {
		this.pmdSubList = pmdSubList;
	}

	public ArrayList<SubjectInfo> getLifeScienceSubList() {
		return lifeScienceSubList;
	}

	public void setLifeScienceSubList(ArrayList<SubjectInfo> lifeScienceSubList) {
		this.lifeScienceSubList = lifeScienceSubList;
	}

	public ArrayList<SubjectInfo> getCsSubList() {
		return csSubList;
	}

	public void setCsSubList(ArrayList<SubjectInfo> csSubList) {
		this.csSubList = csSubList;
	}

	public ArrayList<SubjectInfo> getOtherSubjectlist() {
		return otherSubjectlist;
	}

	public void setOtherSubjectlist(ArrayList<SubjectInfo> otherSubjectlist) {
		this.otherSubjectlist = otherSubjectlist;
	}


	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getDobString() {
		return dobString;
	}

	public void setDobString(String dobString) {
		this.dobString = dobString;
	}

	public String getFathersphone() {
		return fathersphone;
	}

	public void setFathersphone(String fathersphone) {
		this.fathersphone = fathersphone;
	}

	public ArrayList<SubjectInfo> getCsoeSubList() {
		return csoeSubList;
	}

	public void setCsoeSubList(ArrayList<SubjectInfo> csoeSubList) {
		this.csoeSubList = csoeSubList;
	}

	public ArrayList<SelectItem> getEqValueList() {
		return eqValueList;
	}

	public void setEqValueList(ArrayList<SelectItem> eqValueList) {
		this.eqValueList = eqValueList;
	}

}
