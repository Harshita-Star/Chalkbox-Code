package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import schooldata.TermInfo;

@ManagedBean(name="printBLMPreMarksheet")
@ViewScoped
public class PrintBLMPreFinalMarksheetBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	StudentInfo studentList;
	ArrayList<StudentInfo> listOfStudent;
	String dateEntry,rollNo;
	ArrayList<SubjectInfo> iqSubjectList,eqSubList,sqSubList,pmdSubList,lifeScienceSubList,csSubList,csoeSubList,otherSubjectlist;
	ArrayList<TermInfo> iq_PMD_TermList,eq_CSOE_TermList,sq_lifescience_cs_TermList;
	String headerImagePath;
	String fName,fatherName,className,sno,session,motherName,dobString,fathersphone,schoolId;
	ArrayList<String> IQColumnsList = new ArrayList<>(),EQColumnsList=new ArrayList<>(),lifeScienceColumnsList=new ArrayList<>();
	SchoolInfoList info;
	String tickImagePath="";
	DataBaseMethodsBLMExam de=new DataBaseMethodsBLMExam();
	DatabaseMethods1 obj=new DatabaseMethods1();


	public PrintBLMPreFinalMarksheetBean()
	{
		tickImagePath="rightTick.png";
		Connection conn=DataBaseConnection.javaConnection();
		
		String savePath="";

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId= obj.schoolId();
		session=obj.selectedSessionDetails(schoolId,conn);
		listOfStudent=(ArrayList<StudentInfo>) ss.getAttribute("StudentList");
		String termId=(String) ss.getAttribute("termId");

		info=obj.fullSchoolInfo(conn);
		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		headerImagePath=savePath+info.getMarksheetHeader();

		Date date=(Date) ss.getAttribute("dateOfEntry");

		dateEntry=new SimpleDateFormat("dd-MM-yyyy").format(date);

		String sectionid = "";
		for(StudentInfo allInfo : listOfStudent)
		{
			allInfo.setSession(session);

			allInfo.getClassId();
			sectionid=allInfo.getSectionid();

			fName=allInfo.getFname();
			fatherName=allInfo.getFathersName();
			className=allInfo.getClassName()+"-"+allInfo.getSectionName();
			motherName=allInfo.getMotherName();
			dobString=allInfo.getDobString();
			sno=String.valueOf(allInfo.getAddNumber());
			rollNo=allInfo.getRollNo();
			fathersphone=String.valueOf(allInfo.getFathersPhone());
			iq_PMD_TermList=de.termListForPreClass("IQ",conn);
			eq_CSOE_TermList=de.termListForPreClass("EQ",conn);
			sq_lifescience_cs_TermList=de.termListForPreClass("SQ",conn);

			IQColumnsList.clear();
			for(TermInfo term : iq_PMD_TermList)
			{
				IQColumnsList.add(term.getTermId());
			}

			EQColumnsList.clear();
			for(TermInfo term : eq_CSOE_TermList)
			{
				EQColumnsList.add(term.getTermId());
			}

			lifeScienceColumnsList.clear();
			for(TermInfo term : sq_lifescience_cs_TermList)
			{
				lifeScienceColumnsList.add(term.getTermId());
			}


			iqSubjectList=de.subjectListForPreClass("I.Q.",conn);
			eqSubList=de.subjectListForPreClass("E.Q.",conn);
			sqSubList=de.subjectListForPreClass("SQ",conn);
			pmdSubList=de.subjectListForPreClass("PMD",conn);
			lifeScienceSubList=de.subjectListForPreClass("LifeScience",conn);
			csSubList=de.subjectListForPreClass("CS",conn);
			csoeSubList=de.subjectListForPreClass("CS_/",conn);
			otherSubjectlist=de.subjectListForPreClass("other", conn);

			allInfo.setIqSubjectList(iqSubjectList);
			allInfo.setEqSubList(eqSubList);
			allInfo.setSqSubList(sqSubList);
			allInfo.setLifeScienceSubList(lifeScienceSubList);
			allInfo.setPmdSubList(pmdSubList);
			allInfo.setCsSubList(csSubList);
			allInfo.setCsoeSubList(csoeSubList);
			allInfo.setOtherSubjectlist(otherSubjectlist);

			de.fillStudentMarksForPre(sectionid,sno,allInfo.getIqSubjectList(),termId,session,conn);
			de.fillStudentMarksForPre(sectionid,sno,allInfo.getEqSubList(),termId,session,conn);
			de.fillStudentMarksForPre(sectionid,sno,allInfo.getSqSubList(),termId,session,conn);
			de.fillStudentMarksForPre(sectionid,sno,allInfo.getLifeScienceSubList(),termId,session,conn);
			de.fillStudentMarksForPre(sectionid,sno,allInfo.getPmdSubList(),termId,session,conn);
			de.fillStudentMarksForPre(sectionid,sno,allInfo.getCsSubList(),termId,session,conn);
			de.fillStudentMarksForPre(sectionid,sno,allInfo.getCsoeSubList(),termId,session,conn);
			de.fillStudentMarksForPre(sectionid,sno,allInfo.getOtherSubjectlist(),termId,session,conn);

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

	public ArrayList<TermInfo> getIq_PMD_TermList() {
		return iq_PMD_TermList;
	}

	public void setIq_PMD_TermList(ArrayList<TermInfo> iq_PMD_TermList) {
		this.iq_PMD_TermList = iq_PMD_TermList;
	}

	public ArrayList<TermInfo> getEq_CSOE_TermList() {
		return eq_CSOE_TermList;
	}

	public void setEq_CSOE_TermList(ArrayList<TermInfo> eq_CSOE_TermList) {
		this.eq_CSOE_TermList = eq_CSOE_TermList;
	}

	public ArrayList<TermInfo> getSq_lifescience_cs_TermList() {
		return sq_lifescience_cs_TermList;
	}

	public void setSq_lifescience_cs_TermList(ArrayList<TermInfo> sq_lifescience_cs_TermList) {
		this.sq_lifescience_cs_TermList = sq_lifescience_cs_TermList;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
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

	public ArrayList<String> getIQColumnsList() {
		return IQColumnsList;
	}

	public void setIQColumnsList(ArrayList<String> iQColumnsList) {
		IQColumnsList = iQColumnsList;
	}

	public ArrayList<String> getEQColumnsList() {
		return EQColumnsList;
	}

	public void setEQColumnsList(ArrayList<String> eQColumnsList) {
		EQColumnsList = eQColumnsList;
	}

	public ArrayList<String> getLifeScienceColumnsList() {
		return lifeScienceColumnsList;
	}

	public void setLifeScienceColumnsList(ArrayList<String> lifeScienceColumnsList) {
		this.lifeScienceColumnsList = lifeScienceColumnsList;
	}
	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public ArrayList<SubjectInfo> getCsoeSubList() {
		return csoeSubList;
	}

	public void setCsoeSubList(ArrayList<SubjectInfo> csoeSubList) {
		this.csoeSubList = csoeSubList;
	}

	public String getTickImagePath() {
		return tickImagePath;
	}

	public void setTickImagePath(String tickImagePath) {
		this.tickImagePath = tickImagePath;
	}

}
