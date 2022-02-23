package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
@ManagedBean(name="notificationToStudentApp")
@ViewScoped
public class NotificationToStudentApp implements Serializable
{
	String notification,schNm,title,type;
//	ArrayList<SelectItem>schoolList;
	transient
	UploadedFile image;
	boolean showTable;
	ArrayList<SchoolInfo> schoolList,selectedList;

	public NotificationToStudentApp() {

		Connection conn = DataBaseConnection.javaConnection();
		//schoolList=new DatabaseMethods1().getAllSchool(conn);
		type="all";
		selectedList = new ArrayList<>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkType()
	{
		if(type.equalsIgnoreCase("all"))
		{
			showTable=false;
			schoolList = new ArrayList<>();
			selectedList = new ArrayList<>();
		}
		else
		{
			Connection conn=DataBaseConnection.javaConnection();
			DatabaseMethods1 obj = new DatabaseMethods1();
			showTable = true;
			selectedList = new ArrayList<>();
			schoolList = obj.allSchoolListForMasterNotification(conn);
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}



	public String sendnotification()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String imageurl = "";
		String fileName = "";
		
	   boolean extensionChecker = false;
	   if (image != null && image.getSize() > 0)
	   {
			String ext1 = FilenameUtils.getExtension(image.getFileName());
			if(ext1.equalsIgnoreCase("png")) {
				extensionChecker = false;  
			}
			else
			{
				extensionChecker = true;
			}
	   }
		  
		  
	  if(extensionChecker==false)
	  {	

		if (image != null && image.getSize() > 0) {
			String crdt = new SimpleDateFormat("yMdhms").format(new Date());
			String exten[]=image.getFileName().split("\\.");
			fileName = "notificationStudent_"+crdt+"."+exten[exten.length-1];
			new DatabaseMethods1().makeScanPathForMasterAdmin(image, fileName);

			imageurl = "http://www.chalkboxerp.in/CBXMASTER/"+fileName;
		}
		if(type.equalsIgnoreCase("all"))
		{  
			DatabaseMethods1 obj = new DatabaseMethods1();
			schoolList = obj.allSchoolListForMasterNotification(conn);
		    
			for(SchoolInfo ls:schoolList)
			{
				new DataBaseMeathodJson().notification(title,notification,ls.getId(),ls.getId(),imageurl,conn);
					
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Notification Send Sucessfully"));
			
		}
		else
		{
			for(SchoolInfo schinf : selectedList) {
				new DataBaseMeathodJson().notification(title,notification,schinf.getId(),schinf.getId(),imageurl,conn);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Notification Send Sucessfully"));
			
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "notificationtoStudentApp";
	  }
	  else
	  {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only PNG images are allowed")); 
	  }
	  return "";

	}



	public String getNotification() {
		return notification;
	}



	public void setNotification(String notification) {
		this.notification = notification;
	}



	public String getSchNm() {
		return schNm;
	}



	public void setSchNm(String schNm) {
		this.schNm = schNm;
	}




	public List<SchoolInfo> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SchoolInfo> schoolList) {
		this.schoolList = schoolList;
	}

	public List<SchoolInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<SchoolInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public UploadedFile getImage() {
		return image;
	}
	public void setImage(UploadedFile image) {
		this.image = image;
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




}
