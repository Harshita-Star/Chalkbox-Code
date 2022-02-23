package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="oassisPrintMarksheetBean")
@ViewScoped
public class OassisPrintMarksheetBean implements Serializable
{


	StudentInfo list;
	String termname,examname,classname,termid,examid,classid,headerImage,startSession;
	ArrayList<SubjectInfo>markslist=new ArrayList<>();
	ArrayList<SubjectInfo>coscholasticMarklist=new ArrayList<>();
	ArrayList<StudentInfo>studentlist=new ArrayList<>();
	boolean showco,show,showFooter;
	public OassisPrintMarksheetBean() {

		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentlist=(ArrayList<StudentInfo>) ss.getAttribute("studendeList");

		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(conn);
		String savePath="";
		if(info.getProjecttype().equals("online"))
		{
			savePath=info.getDownloadpath();
		}
		headerImage=savePath+info.getImagePath();
		startSession = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn).split("-")[0];

		String schId=new DatabaseMethods1().schoolId();
		if(schId.equals("4"))
		{
			show=false;
			showFooter = true;
		}
		else
		{
			show=true;
			showFooter = false;
		}

		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public StudentInfo getList() {
		return list;
	}

	public void setList(StudentInfo list) {
		this.list = list;
	}

	public String getTermname() {
		return termname;
	}

	public void setTermname(String termname) {
		this.termname = termname;
	}

	public String getExamname() {
		return examname;
	}

	public void setExamname(String examname) {
		this.examname = examname;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getTermid() {
		return termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

	public String getExamid() {
		return examid;
	}

	public void setExamid(String examid) {
		this.examid = examid;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public ArrayList<SubjectInfo> getMarkslist() {
		return markslist;
	}

	public void setMarkslist(ArrayList<SubjectInfo> markslist) {
		this.markslist = markslist;
	}

	public ArrayList<SubjectInfo> getCoscholasticMarklist() {
		return coscholasticMarklist;
	}

	public void setCoscholasticMarklist(ArrayList<SubjectInfo> coscholasticMarklist) {
		this.coscholasticMarklist = coscholasticMarklist;
	}

	public boolean isShowco() {
		return showco;
	}

	public void setShowco(boolean showco) {
		this.showco = showco;
	}

	public ArrayList<StudentInfo> getStudentlist() {
		return studentlist;
	}

	public void setStudentlist(ArrayList<StudentInfo> studentlist) {
		this.studentlist = studentlist;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getStartSession() {
		return startSession;
	}

	public void setStartSession(String startSession) {
		this.startSession = startSession;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isShowFooter() {
		return showFooter;
	}

	public void setShowFooter(boolean showFooter) {
		this.showFooter = showFooter;
	}


}
