package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="videoGallery")
@ViewScoped


public class VideoGalleryBean implements Serializable
{
	String select,videoTag,videoLink,selectedCLassSection, username, userType, schoolid;
	int i=1;
	ArrayList<galleryInfo> galleryList=new ArrayList<>();
	boolean show;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	ArrayList<SelectItem> classList;
	
	public VideoGalleryBean()
	{
		select="New Album";
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		galleryList=new ArrayList<>();
		for(i=1;i<=5;i++)
		{
			galleryInfo gg=new galleryInfo();
			gg.setSno(i);
			galleryList.add(gg);
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
				classList = DBM.cordinatorClassList(empid, schoolid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classList=DBM.allClassListForClassTeacher(empid,schoolid,conn);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check()
	{
		if(select.equalsIgnoreCase("Random Video"))
		{
			show=true;
			videoTag="Random";
		}
		else
		{
			show=false;
			videoTag="";
		}
	}

	public void addmore()
	{

		for(int k=i;k<=i;k++)
		{
			galleryInfo gg=new galleryInfo();
			gg.setSno(k);
			galleryList.add(gg);
		}
		i++;

	}
	public void sendMessage()
	{
		String tagName="";
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			int j=DBM.checkDuplicacyFromGalleryTable(conn,videoTag,new DatabaseMethods1().schoolId());
			if(j>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Gallery tag alredy exist!Please enter new gallery tag"));
			}
			else
			{

				for(galleryInfo ss:galleryList)
				{
					//ss.setVideoLink(ss.getVideoLink().trim());
					ss.setVideoLink(ss.getVideoLink().replace(" ", ""));
					if(ss.getVideoLink()==null || ss.getVideoLink().equals(""))
					{

					}
					else
					{
						  String videoLink = ss.getVideoLink();
						  String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
						  if(videoLink.matches(pattern))
						  {
							  if(tagName.equals(""))
								{
									tagName=videoLink;
								}
								else
								{
									tagName=tagName+","+videoLink;
								}
						  }
					}
				}

				if(tagName.equals(""))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please enter atleast one video link!"));
				}
				else
				{

					int k=DBM.submitAllVideoLink(conn,videoTag,tagName,new DatabaseMethods1().schoolId(),selectedCLassSection);
					if(k>=1)
					{
						String refNo=addWorkLog(conn,tagName);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Gallery is submitted successfully!"));
						videoLink=videoTag=selectedCLassSection = "";
						select="New Album";
						check();
						galleryList=new ArrayList<>();
						for(i=1;i<=5;i++)
						{
							galleryInfo gg=new galleryInfo();
							gg.setSno(i);
							galleryList.add(gg);
						}
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong.please try again!"));
					}

				}

			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String addWorkLog(Connection conn,String tgName)
	{
	    String value = "";
		String language= "";
		
		language = "Gallery Name-"+videoTag;
		value = tgName;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Vedio Gallery","WEB",value,conn);
		return refNo;
	}
	

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getVideoTag() {
		return videoTag;
	}

	public void setVideoTag(String videoTag) {
		this.videoTag = videoTag;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public ArrayList<galleryInfo> getGalleryList() {
		return galleryList;
	}
	public void setGalleryList(ArrayList<galleryInfo> galleryList) {
		this.galleryList = galleryList;
	}
	public DatabaseMethods1 getDBM() {
		return DBM;
	}
	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
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
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getSchoolid() {
		return schoolid;
	}
	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	/*public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		//subjectList = DBM.subjectListOfParticularClass(selectedClass, conn);
}*/
}