package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.ExtraFieldInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;

@ManagedBean(name="printOtherStdMarksheet")
@ViewScoped
public class PrintOtherMarksheetBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem> termList;
	String selectedTerm,session,classId,sectionId,schid;
	ArrayList<SubjectInfo> englistList,hindiList,mathList,otherActList,drawingList,musicList,sportList,personalList,workingList;
	ArrayList<String> scholasticColumnsList = new ArrayList<>(),signatureList,extraColumnsList=new ArrayList<String>();
	Date date;
	String headerImage;
	DataBaseMethodsBLMExam objBLMExam=new DataBaseMethodsBLMExam();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsOasisExam objExam=new DataBaseMethodsOasisExam();


	public PrintOtherMarksheetBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList=(ArrayList<StudentInfo>) ss.getAttribute("StudentList");
		selectedTerm=(String) ss.getAttribute("termId");
		classId=(String) ss.getAttribute("classId");
		sectionId=(String) ss.getAttribute("sectionId");
		schid=obj.schoolId();
		date=new Date();

		Connection conn=DataBaseConnection.javaConnection();
		session=obj.selectedSessionDetails(schid,conn);
		SchoolInfoList info=obj.fullSchoolInfo(conn);
		String savePath="";
		if(info.getProjecttype().equals("online"))
		{
			savePath=info.getDownloadpath();
		}
		headerImage=savePath+info.getMarksheetHeader();
		termList= objBLMExam.termListByClassTermIdSelectItem(selectedTerm,classId,session,conn);
		
		if(schid.equals("216") || schid.equals("221"))
		{
			parameterList();
			fillDataForEvergreen();
			scholasticColumnsList.clear();
			extraColumnsList.clear();
			for(SelectItem term : termList)
			{
				scholasticColumnsList.add(String.valueOf(term.getValue())+"termI");
				scholasticColumnsList.add(String.valueOf(term.getValue())+"termO");
				scholasticColumnsList.add(String.valueOf(term.getValue())+"termC");
				extraColumnsList.add(String.valueOf(term.getValue())+"term");
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}

	public void parameterList() 
	{
		signatureList=new ArrayList<String>();
		signatureList.add("Class Teacher");
		signatureList.add("Vice Principal");
		signatureList.add("Principal");
	}
	
	public void fillDataForEvergreen()
	{
		Connection conn=DataBaseConnection.javaConnection();

		ArrayList<SubjectInfo> mainSubList=objExam.subjectListFor1To3Class(classId,conn);
		
		String subjectName="";
		for(SubjectInfo subInfo:mainSubList)
		{
			subjectName=subInfo.getSubjectName();
			if(subjectName.equalsIgnoreCase("ENGLISH"))
			{
				englistList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print",conn);
			}
			else if(subjectName.equalsIgnoreCase("HINDI"))
			{
				hindiList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print", conn);
			}
			else if(subjectName.equalsIgnoreCase("MATHEMATICS"))
			{
				mathList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print", conn);
			}
			else if(subjectName.equalsIgnoreCase("DRAWING"))
			{
				drawingList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print", conn);
			}
			else if(subjectName.equalsIgnoreCase("MUSIC/DANCE"))
			{
				 // System.out.println("CB Testing Print other Marksheet Schid"+schid);
				 // System.out.println("CB Testing Print other Marksheet Subject"+subInfo.getSubjectId());
				musicList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print",conn);
			}
			else if(subjectName.equalsIgnoreCase("SPORTS"))
			{
				sportList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print", conn);
			}
			else if(subjectName.equalsIgnoreCase("MORE ABOUT MYSELF"))
			{
				otherActList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print", conn);
			}
			else if(subjectName.equalsIgnoreCase("PERSONAL SKILLS"))
			{
				personalList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print", conn);
			}
			else if(subjectName.equalsIgnoreCase("WORKING SKILLS"))
			{
				workingList=objExam.markSheetSubjectList(subInfo.getSubjectId(),"print",conn);
			}
		}
		
		String addNo="";
		ArrayList<ExtraFieldInfo> list,tempList;
		for(StudentInfo allInfo:studentList)
		{
			sectionId=allInfo.getSectionid();
			addNo=allInfo.getAddNumber();

			objExam.fillMarksForClassOther(sectionId,englistList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,hindiList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,mathList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,drawingList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,musicList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,sportList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,otherActList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,personalList,addNo,termList,conn);
			objExam.fillMarksForClassOther(sectionId,workingList,addNo,termList,conn);

			allInfo.setEnglistList(englistList);
			allInfo.setHindiList(hindiList);
			allInfo.setMathList(mathList);
			allInfo.setDrawingList(drawingList);
			allInfo.setIndianMusicList(musicList);
			allInfo.setSportsList(sportList);
			allInfo.setOtherSubjectlist(otherActList);
			allInfo.setPersonalList(personalList);
			allInfo.setWorkingList(workingList);
			
			list=objBLMExam.extraFieldValueForStudentOther(addNo, session, sectionId, termList, conn);
			tempList=new ArrayList<>();
			if(schid.equals("216") || schid.equals("221"))
			{
				tempList.add(list.get(3));
			}
			else
			{
				tempList.add(list.get(0));
				tempList.add(list.get(1));
				tempList.add(list.get(2));
				tempList.add(list.get(3));
			}
			
			allInfo.setExtraFieldList(tempList);
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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}


	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public ArrayList<String> getScholasticColumnsList() {
		return scholasticColumnsList;
	}

	public void setScholasticColumnsList(ArrayList<String> scholasticColumnsList) {
		this.scholasticColumnsList = scholasticColumnsList;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;

	}

	public ArrayList<String> getSignatureList() {
		return signatureList;
	}

	public void setSignatureList(ArrayList<String> signatureList) {
		this.signatureList = signatureList;
	}

	public ArrayList<String> getExtraColumnsList() {
		return extraColumnsList;
	}

	public void setExtraColumnsList(ArrayList<String> extraColumnsList) {
		this.extraColumnsList = extraColumnsList;
	}
}
