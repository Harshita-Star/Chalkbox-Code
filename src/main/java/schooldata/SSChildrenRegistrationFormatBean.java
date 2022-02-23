package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import exam_module.DataBaseMethodsBLMExam;
import tc_module.DateToWords;

@ManagedBean(name="ssChildrenRegistrationFormat")
@ViewScoped
public class SSChildrenRegistrationFormatBean implements Serializable
{
	
	boolean classElevenRender,classNineRender;
	ArrayList<StudentInfo> listOfStudent;
	ArrayList<Subjects> subjectList;
	ArrayList<String> fakeList;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	DateToWords dtw = new DateToWords();
	String session="";
	public SSChildrenRegistrationFormatBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsBLMExam objBLM= new DataBaseMethodsBLMExam();
		
		session=dbm.selectedSessionDetails(dbm.schoolId(),conn);
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		listOfStudent=(ArrayList<StudentInfo>) ss.getAttribute("selectedStudent");
	
		String className = listOfStudent.get(0).getClassStrName();
		
		
		
		if(className.equalsIgnoreCase("IX"))
		{
			classNineRender = true; 
		}
		else if(className.equalsIgnoreCase("XI"))
		{
			classElevenRender = true;
			
			fakeList = new ArrayList<String>();
			fakeList.add("1");
			for(StudentInfo li : listOfStudent)
			{
				subjectList = new ArrayList<Subjects>();
				subjectList=dbm.allSubjectStudentWise(li.getSectionid(),li.getClassId(),conn,li.getAddNumber(),li.getSrNo());

				li.setSubjectsList(subjectList);

					
			}
		
		}
		else
		{
			
		}
		
			
		for(StudentInfo li : listOfStudent)
		{
			li.setDateToWord(dtw.DateToWordsConvertWithoutOf(sdf.format(li.getDob())));
				
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	
	
	public ArrayList<StudentInfo> getListOfStudent() {
		return listOfStudent;
	}

	public void setListOfStudent(ArrayList<StudentInfo> listOfStudent) {
		this.listOfStudent = listOfStudent;
	}

	

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public boolean isClassElevenRender() {
		return classElevenRender;
	}

	public void setClassElevenRender(boolean classElevenRender) {
		this.classElevenRender = classElevenRender;
	}

	public boolean isClassNineRender() {
		return classNineRender;
	}

	public void setClassNineRender(boolean classNineRender) {
		this.classNineRender = classNineRender;
	}

	public ArrayList<Subjects> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<Subjects> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getFakeList() {
		return fakeList;
	}

	public void setFakeList(ArrayList<String> fakeList) {
		this.fakeList = fakeList;
	}

	
}

