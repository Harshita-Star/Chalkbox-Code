package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import session_work.RegexPattern;

@ManagedBean(name="stdOtherPerform")
@ViewScoped
public class StudentPerformanceOtherBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo> studentList;
	String selectedTerm,termName,schid,session;
	ArrayList<SubjectInfo> englistList,hindiList,mathList,otherActList,drawingList;
	ArrayList<SubjectInfo> musicList,sportList,personalList,workingList;
	Date date=new Date();
	boolean forEvergreen,forInitium;
	String classId;
	DataBaseMethodsExam obExm=new DataBaseMethodsExam();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsOasisExam objExam=new DataBaseMethodsOasisExam();

	public StudentPerformanceOtherBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		date=new Date();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList=(ArrayList<StudentInfo>) ss.getAttribute("StudentList");
		selectedTerm=(String) ss.getAttribute("termId");
		classId=studentList.get(0).getClassId();
		termName=obj.semesterNameFromid(selectedTerm, conn);
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		
		if(schid.equals("216") || schid.equals("221"))
		{
			fillDataForEvergreen();
			forEvergreen=true;
		}
		
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	public void fillDataForEvergreen()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		String sectionId="",addNo="";
		
		ArrayList<SubjectInfo> mainSubList=objExam.subjectListFor1To3Class(classId,conn);
		
		String subjectName="";
		for(SubjectInfo subInfo:mainSubList)
		{
			subjectName=subInfo.getSubjectName();
			if(subjectName.equalsIgnoreCase("ENGLISH"))
			{
				englistList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks",conn);
			}
			else if(subjectName.equalsIgnoreCase("HINDI"))
			{
				hindiList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks", conn);
			}
			else if(subjectName.equalsIgnoreCase("MATHEMATICS"))
			{
				mathList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks", conn);
			}
			else if(subjectName.equalsIgnoreCase("DRAWING"))
			{
				drawingList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks", conn);
			}
			else if(subjectName.equalsIgnoreCase("MUSIC/DANCE"))
			{
				musicList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks",conn);
			}
			else if(subjectName.equalsIgnoreCase("SPORTS"))
			{
				sportList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks", conn);
			}
			else if(subjectName.equalsIgnoreCase("MORE ABOUT MYSELF"))
			{
				otherActList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks", conn);
			}
			else if(subjectName.equalsIgnoreCase("PERSONAL SKILLS"))
			{
				personalList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks", conn);
			}
			else if(subjectName.equalsIgnoreCase("WORKING SKILLS"))
			{
				workingList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"fillMarks",conn);
			}
		}
		for(StudentInfo allInfo:studentList)
		{
			sectionId=allInfo.getSectionid();
			addNo=allInfo.getAddNumber();
			objExam.checkMarksForClass(sectionId,englistList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,hindiList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,mathList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,drawingList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,musicList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,sportList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,otherActList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,personalList,addNo,selectedTerm,conn);
			objExam.checkMarksForClass(sectionId,workingList,addNo,selectedTerm,conn);
			
			allInfo.setEnglistList(englistList);
			allInfo.setHindiList(hindiList);
			allInfo.setMathList(mathList);
			allInfo.setIndianMusicList(musicList);
			allInfo.setDrawingList(drawingList);
			allInfo.setSportsList(sportList);
			allInfo.setOtherSubjectlist(otherActList);
			allInfo.setPersonalList(personalList);
			allInfo.setWorkingList(workingList);
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void submitMarksEvergreen()
	{
		Connection conn=DataBaseConnection.javaConnection();
	
		boolean i = false;
		String sectionid;
		for(StudentInfo allInfo: studentList)
		{
			sectionid=allInfo.getSectionid();
			
			i=objExam.addStudentPerformance(sectionid, allInfo, englistList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, hindiList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, mathList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, drawingList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, musicList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, sportList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, otherActList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, personalList, selectedTerm, conn,date);
			i=objExam.addStudentPerformance(sectionid, allInfo, workingList, selectedTerm, conn,date);
		}
		if(i==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Sucessfully"));
			try
			{
				selectedTerm="";
			} catch (Exception e) {
				
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

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public boolean isForEvergreen() {
		return forEvergreen;
	}

	public boolean isForInitium() {
		return forInitium;
	}


	public void setForInitium(boolean forInitium) {
		this.forInitium = forInitium;
	}


	public void setForEvergreen(boolean forEvergreen) {
		this.forEvergreen = forEvergreen;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
