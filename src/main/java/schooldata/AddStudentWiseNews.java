package schooldata;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import session_work.DatabaseMethodSession;

@ManagedBean(name="addStudentWiseNews")
@ViewScoped
public class AddStudentWiseNews {
	
ArrayList<Event>list=new ArrayList<>();
	
	ArrayList<ClassInfo> classSectionList,selectedClassList;
	Event selectedActivity;
	String id;
	String message,type,language,fileUrl,fnm;
	transient UploadedFile file;
	String name;
	ArrayList<StudentInfo> stdList = new ArrayList<>();
	boolean showTable,englishShow,hindiShow;
	SchoolInfoList ls = new SchoolInfoList();
	DatabaseMethodSession objSession=new DatabaseMethodSession();

	public AddStudentWiseNews() {
		
		fnm="";
		hindiShow=false;
		language="english";
		englishShow=true;
		fileUrl = "NA";
		Connection conn = DataBaseConnection.javaConnection();
		ls = new DatabaseMethods1().fullSchoolInfo(conn);
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//list=new DatabaseMethods1().searchStudentList(query,conn);
		stdList=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : stdList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	
	public void checkstd() {
		int index=name.lastIndexOf(":")+1;
		id=name.substring(index);
	}
	
	
	public void checkLanguage()
	{
		message="";
		if(language.equalsIgnoreCase("english"))
		{
			englishShow=true;hindiShow=false;
		}
		else
		{
			englishShow=false;hindiShow=true;
		}
	}
	
	
	public void fileUpload(FileUploadEvent event)
	{
		System.out.println("file upload");
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		String dd="";
		fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "news"+dd+"."+exten[exten.length-1];
		fileUrl = ls.getDownloadpath()+fnm;
		obj.makeProfileSchid(obj.schoolId(),event.getFile(),fnm,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String sendMessage()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		
		try
		{
			if(name == null && name.equals(""))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Search any student first"));
			}
			else
			{
				int i=obj.addNewsStudent(message,obj.schoolId(),fnm,conn,type,id);
				if(i>0)
				{
					String refNo;
					try {
						//refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String concernnnn = "";
					FacesContext context1 = FacesContext.getCurrentInstance();
					context1.addMessage(null, new FacesMessage("News Added successfully "));
					if(language.equalsIgnoreCase("hindi"))
					{
						concernnnn="प्रिय अभिभावक,\n"+message+"\nसादर,\n"+ls.getHindiName();
					}
					else
					{
						concernnnn = message;
					}
					
					new SendingNotifications().sendNotifi(concernnnn, obj.schoolId(), "", "", obj.selectedSessionDetails(obj.schoolId(), conn), "Students",id, "News");

					language = "english";
					englishShow=true;
					hindiShow=false;
					message=null;
					fileUrl = "NA";
					name = "";
					fnm="";
					showTable=false;
					file=null;
				}
				else
				{
					FacesContext context1 = FacesContext.getCurrentInstance();
					context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));

				}
			}
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}
	
	

	public ArrayList<Event> getList() {
		return list;
	}

	public void setList(ArrayList<Event> list) {
		this.list = list;
	}

	public ArrayList<ClassInfo> getClassSectionList() {
		return classSectionList;
	}

	public void setClassSectionList(ArrayList<ClassInfo> classSectionList) {
		this.classSectionList = classSectionList;
	}

	public ArrayList<ClassInfo> getSelectedClassList() {
		return selectedClassList;
	}

	public void setSelectedClassList(ArrayList<ClassInfo> selectedClassList) {
		this.selectedClassList = selectedClassList;
	}

	public Event getSelectedActivity() {
		return selectedActivity;
	}

	public void setSelectedActivity(Event selectedActivity) {
		this.selectedActivity = selectedActivity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFnm() {
		return fnm;
	}

	public void setFnm(String fnm) {
		this.fnm = fnm;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public boolean isEnglishShow() {
		return englishShow;
	}

	public void setEnglishShow(boolean englishShow) {
		this.englishShow = englishShow;
	}

	public boolean isHindiShow() {
		return hindiShow;
	}

	public void setHindiShow(boolean hindiShow) {
		this.hindiShow = hindiShow;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ArrayList<StudentInfo> getStdList() {
		return stdList;
	}


	public void setStdList(ArrayList<StudentInfo> stdList) {
		this.stdList = stdList;
	}
	

	
}
