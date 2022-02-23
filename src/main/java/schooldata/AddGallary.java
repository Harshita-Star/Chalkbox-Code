package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
@ManagedBean(name="addGallery")
@ViewScoped
public class AddGallary implements Serializable{


	String message,filePath;
	UploadedFile file;
	Date startDate,endDate;
	ArrayList<HomeWorkInfo> newsList;
	HomeWorkInfo selectedNews;
	String gallaryName, username, schoolid, userType;
	String messageNew,filePathPre,selectedCLassSection;
	UploadedFile fileNew;
	ArrayList<SelectItem> classList;
	DatabaseMethods1 obj=new DatabaseMethods1();


	int j=0;
	public AddGallary()
	{
        Connection conn = DataBaseConnection.javaConnection();
        HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		
		newsList =new ArrayList<>();
		for(int i=1;i<=5;i++)
		{
			HomeWorkInfo in=new HomeWorkInfo();
			in.setsNo(String.valueOf(i)); 
			newsList.add(in);
			j=i;
		}

		try
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classList = new ArrayList<SelectItem>();
				SelectItem si = new SelectItem();
				si.setLabel("All");
				si.setValue("All");
				classList.add(si);

				ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

				if(temp.size()>0)
				{
					classList.addAll(temp);
				}
			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
						|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classList = obj.cordinatorClassList(empid, schoolid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classList=obj.allClassListForClassTeacher(empid,schoolid,conn);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

        try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addNewRow()
	{
		int k=j;
		for(int i=k+1;i==k+1;k++)
		{
			HomeWorkInfo in=new HomeWorkInfo();
			in.setsNo(String.valueOf(i));
			newsList.add(in);
			j=i;
		}


	}

	public String sendMessage()
	{
		/*String dt=new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		if (file != null && file.getSize() > 0)
		{
			filePath = file.getFileName();
			String exten[]=filePath.split("\\.");
			filePath="gallery_"+"_"+dt+"."+exten[exten.length-1];
			new DatabaseMethods1().makeProfile(file,filePath);
		}*/
	  if((gallaryName.trim().contains("'")&&gallaryName.trim().contains("--"))||(gallaryName.trim().contains("'")&&gallaryName.trim().contains("#")))
	  {
		  FacesContext context1 = FacesContext.getCurrentInstance();
		  context1.addMessage(null, new FacesMessage("Please Enter Proper Gallery Name"));
		  return "";
	  }
	  else
      {	
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		int i = 0;
		
		i=DBM.gallery(newsList,"",gallaryName,"",selectedCLassSection,conn);
		
		if(i>0)
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Gallery Added successfully"));

			message=filePath="";
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "addGallary.xhtml";
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
      }	
	}


	public UploadedFile getFile() {
		return file;
	}
	public void setFile(UploadedFile file) {
		this.file = file;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public ArrayList<HomeWorkInfo> getNewsList() {
		return newsList;
	}
	public void setNewsList(ArrayList<HomeWorkInfo> newsList) {
		this.newsList = newsList;
	}
	public HomeWorkInfo getSelectedNews() {
		return selectedNews;
	}
	public void setSelectedNews(HomeWorkInfo selectedNews) {
		this.selectedNews = selectedNews;
	}
	public String getMessageNew() {
		return messageNew;
	}
	public void setMessageNew(String messageNew) {
		this.messageNew = messageNew;
	}

	public UploadedFile getFileNew() {
		return fileNew;
	}

	public void setFileNew(UploadedFile fileNew) {
		this.fileNew = fileNew;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public String getGallaryName() {
		return gallaryName;
	}

	public void setGallaryName(String gallaryName) {
		this.gallaryName = gallaryName;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
