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

@ManagedBean(name="printCoFormatMarksheet")
@ViewScoped
public class PrintCoFormatMarksheetBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	StudentInfo studentList;
	ArrayList<StudentInfo> listOfStudent;
	ArrayList<SubjectInfo> markListCoscholastic;
	String fName,fatherName,sno,session,motherName,dobString,headerImagePath,dateEntry,schoolId,rollNo;
	SchoolInfoList info;
	DataBaseMethodsBLMExam objBLM=new DataBaseMethodsBLMExam();
	DatabaseMethods1 obj=new DatabaseMethods1();

	public PrintCoFormatMarksheetBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsBLMExam de=new DataBaseMethodsBLMExam();
		String savePath="";

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session=(String) ss.getAttribute("session");
		
		Date date=(Date) ss.getAttribute("dateOfEntry");
		dateEntry=new SimpleDateFormat("dd-MM-yyyy").format(date);
		listOfStudent=(ArrayList<StudentInfo>) ss.getAttribute("StudentList");
		markListCoscholastic=(ArrayList<SubjectInfo>) ss.getAttribute("markListCoscholastic");
		rollNo=(String) ss.getAttribute("rollNo");
        schoolId= obj.schoolId();
		info=obj.fullSchoolInfo(conn);
		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		headerImagePath=savePath+info.getMarksheetHeader();

		String classid="",sectionid = "";
		for(StudentInfo allInfo : listOfStudent)
		{
			allInfo.setSession(session);

			classid=allInfo.getClassId();
			sectionid=allInfo.getSectionid();

			fName=allInfo.getFname();
			fatherName=allInfo.getFathersName();
			motherName=allInfo.getMotherName();
			dobString=allInfo.getDobString();
			sno=String.valueOf(allInfo.getAddNumber());

			//markListCoscholastic=de.subjectListForStudent(classid, session, sno, sectionid, "coscholastic",conn);
			allInfo.setMarkListCoscholastic(markListCoscholastic);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
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
	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public StudentInfo getStudentList() {
		return studentList;
	}
	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public ArrayList<SubjectInfo> getMarkListCoscholastic() {
		return markListCoscholastic;
	}

	public void setMarkListCoscholastic(ArrayList<SubjectInfo> markListCoscholastic) {
		this.markListCoscholastic = markListCoscholastic;
	}

	public String getDobString() {
		return dobString;
	}

	public void setDobString(String dobString) {
		this.dobString = dobString;
	}

	public ArrayList<StudentInfo> getListOfStudent() {
		return listOfStudent;
	}

	public void setListOfStudent(ArrayList<StudentInfo> listOfStudent) {
		this.listOfStudent = listOfStudent;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
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
}
