package student_module;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.Subjects;

@ManagedBean(name="studentViewDailyLecture")
@ViewScoped

public class StudentViewDailyLectureBean implements Serializable
{
	Date date;
	String subjectid,classid,sectionid,schid;
	ArrayList<Subjects> subjectList = new ArrayList<>();
	ArrayList<LectureInfo> list = new ArrayList<>();
	LectureInfo selected = new LectureInfo();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	StreamedContent file;
	
	public StudentViewDailyLectureBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
		schid = dbm.schoolId();

		StudentInfo info =dbj.studentDetailslistByAddNo(studentid, schid,conn);
		sectionid=info.getSectionid();
		classid=info.getClassId();
		
		subjectList = dbj.studentWiseSubjectList(classid,sectionid,studentid,schid,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
		list = dbj.dailyLecturePlanList(classid,sectionid,subjectid,dateStr,schid,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=dbj.fullSchoolInfo(schid,conn);
		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getStrfile()+"')");

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void backUpMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int first=selected.getStrfile().lastIndexOf(".")+1;
		int last=selected.getStrfile().length();
		String ext=selected.getStrfile().substring(first, last);
		
		String ff=selected.getStrfile();
		
		
		SchoolInfoList info=dbj.fullSchoolInfo(schid, conn);
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
		
		String realPath=info.getUploadpath();  //online
		try
		{
//			//// // System.out.println(ext);
//			//// // System.out.println(realPath+ff);
			InputStream stream = new FileInputStream(new File(realPath+ff));
//			file = new DefaultStreamedContent(stream, "file/"+ext, ff);
			
			file = new DefaultStreamedContent().builder().contentType("file/"+ext).name(ff).stream(()->stream).build();
			
			/*if(ext.equals("docx"))
			{
				file = new DefaultStreamedContent(stream, "file/docx", ff);
			}
			else if(ext.equals("doc"))
			{
				file = new DefaultStreamedContent(stream, "file/doc", ff);
			}
			else if(ext.equals("txt"))
			{
				file = new DefaultStreamedContent(stream, "file/txt", ff);
			}
			else if(ext.equals("xlsx"))
			{
				file = new DefaultStreamedContent(stream, "file/xlsx", ff);
			}
			else if(ext.equals("xls"))
			{
				file = new DefaultStreamedContent(stream, "file/xls", ff);
			}
			else if(ext.equals("mp4"))
			{
				file = new DefaultStreamedContent(stream, "file/mp4", ff);
			}
			else if(ext.equals("avi"))
			{
				file = new DefaultStreamedContent(stream, "file/avi", ff);
			}
			else if(ext.equals("flv"))
			{
				file = new DefaultStreamedContent(stream, "file/flv", ff);
			}*/
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public ArrayList<Subjects> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<Subjects> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<LectureInfo> getList() {
		return list;
	}

	public void setList(ArrayList<LectureInfo> list) {
		this.list = list;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public LectureInfo getSelected() {
		return selected;
	}

	public void setSelected(LectureInfo selected) {
		this.selected = selected;
	}
}
