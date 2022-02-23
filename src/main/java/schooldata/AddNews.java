package schooldata;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="addNews")
@ViewScoped
public class AddNews implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<Event>list=new ArrayList<>();
	
	ArrayList<ClassInfo> classSectionList,selectedClassList;
	Event selectedActivity;
	String message,type,language,fileUrl,fnm;
	transient UploadedFile file;
	boolean showTable,englishShow,hindiShow;
	SchoolInfoList ls = new SchoolInfoList();

	public AddNews()
	{
		fnm="";
		type="all";
		hindiShow=false;
		language="english";
		englishShow=true;
		fileUrl = "NA";
		Connection conn = DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().viewNews(conn);
		ls=new DatabaseMethods1().fullSchoolInfo(conn);
	
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().viewNews(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	
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
	
	public void view1()
	{
		if(selectedActivity.getFileName().equalsIgnoreCase("NA"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Attachment Available"));
		}
		else
		{
			Connection conn = DataBaseConnection.javaConnection();
			SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(new DatabaseMethods1().schoolId(),conn);
			PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selectedActivity.getFileName()+"')");
	
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void checkType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedClassList=new ArrayList<>();
		classSectionList=new ArrayList<>();

		if(type.equals("all"))
		{
			showTable=false;
		}
		else
		{
			showTable=true;
			classSectionList=new DatabaseMethods1().allSectionList(conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void viewFile(String link)
	{
		PrimeFaces.current().executeInitScript("window.open('"+link+"')");
	}
	
	public void fileUpload(FileUploadEvent event)
	{
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
			if(type.equalsIgnoreCase("classwise") && selectedClassList.size()<=0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select class(es)"));
			}
			else
			{
				/*String name = "";
				if (file != null && file.getSize() > 0)
				{
					int rendomNumber=(int)(Math.random()*9000)+1000;
					name = file.getFileName();
					String exten[]=name.split("\\.");
					name="news"+String.valueOf(rendomNumber)+"."+exten[exten.length-1];

					obj.makeProfileSchid(obj.schoolId(),file,name,conn);
				}*/

				int i=obj.news(message,obj.schoolId(),fnm,conn,type,selectedClassList);
				if(i>0)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String concernnnn = "";
					FacesContext context1 = FacesContext.getCurrentInstance();
					context1.addMessage(null, new FacesMessage("News Added successfully "));
					if(language.equalsIgnoreCase("hindi"))
					{
						concernnnn="à¤ªà¥�à¤°à¤¿à¤¯ à¤…à¤­à¤¿à¤­à¤¾à¤µà¤•,\n"+message+"\nà¤¸à¤¾à¤¦à¤°,\n"+ls.getHindiName();
					}
					else
					{
						concernnnn = message;
						/*concernnnn = "Dear Parent,\n"+message+"\nRegards,"+ls.getSmsSchoolName();*/
					}
					
					if(type.equals("all"))
					{
						obj.notification(obj.schoolId(),"News",concernnnn,obj.schoolId(),conn);
					}
					else
					{
						for(ClassInfo cc : selectedClassList)
						{
							obj.notification(obj.schoolId(),"News",concernnnn,cc.getSectionId()+"-"+cc.getClassid()+"-"+obj.schoolId(),conn);
						}
					}

					language = "english";
					englishShow=true;
					hindiShow=false;
					message=null;
					type="all";
					fileUrl = "NA";
					fnm="";
					selectedClassList=new ArrayList<>();
					classSectionList=new ArrayList<>();
					showTable=false;
					file=null;
					list=obj.viewNews(conn);

					//return "addNews.xhtml";
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
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String st = formater.format(new Date()); 
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		
		language = "Type-"+type+" --Message-"+message+" --Filename-"+fnm+" --Date"+st;
		value = "Selected classes-";
		
		if(!type.equalsIgnoreCase("all"))
		{	
		for(ClassInfo ci :selectedClassList)
		{
			
			String className=obj.classname(ci.getClassid(), schid, conn);
			String sectionname =obj.sectionNameByIdSchid(schid,ci.getSectionId(), conn);
			value += "(Class-"+className+"  Section-"+sectionname+")";
		}
		}
		else
		{
			value += "All";
		}
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add News","WEB",value,conn);
		return refNo;
	}
	
	
	
public void deleteMultiple()
{
	Connection conn=DataBaseConnection.javaConnection();
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList info = obj.fullSchoolInfo(conn);
		
	int checkSelections=0;
	for(Event ff: list)
	{
	   if(ff.getSelection().equals(true))
	  {
		checkSelections=1;		
	  }
	}	
	
	if(checkSelections==1)
	{	
		
		int errorCount=0;
		
		for(Event ff: list)
		{	
		 if(ff.getSelection().equals(true))
		 {
		  int checkFile = obj.checkNewsPhotoPresent(ff.getFileName(),conn);	  
		  int i=obj.deleteNews(ff.getId(),conn);
		  if(i>=1)
		  {
			  String refNo3;
				try {
					refNo3=addWorkLog3(conn,ff.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(checkFile ==1)
				{
					
				}
				else
				{
					File file = new File(info.getUploadpath()+ff.getFileName());
					file.delete();	
				}
		

		
		 }
		 else
		 {
			 errorCount =1;
		 }
		} 
	  }
		
		if(errorCount ==0)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected News Deleted Successfully"));
			list=obj.viewNews(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
	
		}
		}
    else
	{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select AtLeast 1 News"));

	}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

public String addWorkLog3(Connection conn,int id)
{
    String value = "";
	String language= "";
	
	value = "Selected Id-"+id;

	String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete News in Multiple","WEB",value,conn);
	return refNo;
}


	public void deleteNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SchoolInfoList info = obj.fullSchoolInfo(conn);
		int checkFile = obj.checkNewsPhotoPresent(selectedActivity.getFileName(),conn);
		int i=obj.deleteNews(selectedActivity.getId(),conn);
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(checkFile ==1)
			{
				
			}
			else
			{
				File file = new File(info.getUploadpath()+selectedActivity.getFileName());
				file.delete();	
			}
			
			list=obj.viewNews(conn);

			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected News Deleted Successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Selected Id-"+selectedActivity.getId();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete News","WEB",value,conn);
		return refNo;
	}
	
	public void editNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int i=obj.updateNews(message,selectedActivity.getId(),conn);
		if(i==1)
		{

			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Details Updated Successfully"));
			obj.notification(obj.schoolId(),"News",message,obj.schoolId(),conn);
			list=obj.viewNews(conn);
			message=null;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void editActivityDetails()
	{
		message=selectedActivity.getNews();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList<Event> getList() {
		return list;
	}
	public void setList(ArrayList<Event> list) {
		this.list = list;
	}
	public Event getSelectedActivity() {
		return selectedActivity;
	}
	public void setSelectedActivity(Event selectedActivity) {
		this.selectedActivity = selectedActivity;
	}
	public UploadedFile getFile() {
		return file;
	}
	public void setFile(UploadedFile file) {
		this.file = file;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}


}
