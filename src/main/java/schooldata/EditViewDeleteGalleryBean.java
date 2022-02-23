package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="editViewDeleteVideoGallery")
@ViewScoped

public class EditViewDeleteGalleryBean implements Serializable
{
	String select,videoTag,videoLink,size,videoId,selectedCLassSection, username, schoolid, userType;
	int i=1;
	ArrayList<galleryInfo> galleryList=new ArrayList<>();
	ArrayList<galleryInfo> galleryData=new ArrayList<>();
	ArrayList< galleryInfo> videoLinkList=new ArrayList<>();
	galleryInfo selected;
	galleryInfo selected2;
	boolean show,view;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	ArrayList<SelectItem> classList;
	
	public EditViewDeleteGalleryBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		
		galleryData=DBM.selectAllGalleryValues(conn,new DatabaseMethods1().schoolId());
		for(galleryInfo gg:galleryData)
		{
			galleryData.size();
			size=gg.getVideoLink();
			String arr[]=size.split(",");
			gg.setVideoLinkNo(arr.length);

		}


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

				ArrayList<SelectItem> temp =DBM.allClass(conn);

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
	public void datatable()
	{
		Connection conn = DataBaseConnection.javaConnection();
		videoLinkList=new ArrayList<>();
		videoTag=selected.getTagName();
		videoId=selected.getTagId();
		selectedCLassSection = selected.getClassId();
		galleryInfo linkList=DBM.selectAllLinks(conn,selected);
		String links=linkList.getVideoLink();
		String arr1[]=links.split(",");
		if(videoTag.equalsIgnoreCase("Random"))
		{
			show=true;
		}
		else
		{
			show=false;


		}
		for(int j=0;j<arr1.length;j++)
		{

			galleryInfo dd=new galleryInfo();
			dd.setLinks(arr1[j]);
			dd.setVid(getYoutubeVideoIdFromUrl(dd.getLinks()));
			dd.setThumb("https://img.youtube.com/vi/"+dd.getVid()+"/0.jpg");
			int k=j+1;
			dd.setSno(k);
			videoLinkList.add(dd);

		}

		view=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getYoutubeVideoIdFromUrl(String inUrl) {
		inUrl = inUrl.replace("&feature=youtu.be", "");
		if (inUrl.toLowerCase().contains("youtu.be")) {
			return inUrl.substring(inUrl.lastIndexOf("/") + 1);
		}
		String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(inUrl);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public void delete()
	{
		/*videoTag="";
		galleryList=new ArrayList<>();
		for(i=1;i<=5;i++)
		{
			galleryInfo gg=new galleryInfo();
			gg.setSno(i);
			galleryList.add(gg);
		}*/
		Connection conn = DataBaseConnection.javaConnection();
		videoLinkList.remove(selected2);
		String value="";
		for(galleryInfo ss:videoLinkList)
		{
			if(value.equals(""))
			{
				value=ss.getLinks();
			}
			else
			{
				value=value+","+ss.getLinks();
			}
		}
		int a=DBM.updateLinksAtId(conn,videoId,value,new DatabaseMethods1().schoolId());
		if(a>=1)
		{
			String refNo3;
			try {
				refNo3=addWorkLog3(conn,value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Deleted value is updated successfully"));
			if(value==null || value.equals(""))
			{
				DBM.deletedTagName(conn,DBM.schoolId(),videoId);
				String refNo4;
				try {
					refNo4=addWorkLog4(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			galleryData=DBM.selectAllGalleryValues(conn,new DatabaseMethods1().schoolId());
			
			selected.setVideoLink(value);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Sorry....Deleted value is not updated successfully"));
		}

//        // // System.out.println(videoLinkList.size()+"siz");
        if(videoLinkList.size()==0)
        {
        	try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("editViewDeleteVideoGallery.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
        }
        else
        {
        	datatable();	
        }
        
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog4(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = "Vedio Id-"+videoId;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Tag name","WEB",value,conn);
		return refNo;
	}
	
	
	public String addWorkLog3(Connection conn,String tgName)
	{
	    String value = "";
		String language= "";
		
		language = "Vedio Id - "+videoId;
		value = tgName;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Vedio Link","WEB",value,conn);
		return refNo;
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
		if(videoTag.equalsIgnoreCase(selected.getTagName()))
		{
			show=false;
			for(galleryInfo ss:galleryList)
			{
				if(ss.getVideoLink()==null || ss.getVideoLink().equals(""))
				{

				}
				else
				{
					if(tagName.equals(""))
					{
						tagName=ss.getVideoLink();
					}
					else
					{

						tagName=tagName+","+ss.getVideoLink();
					}
				}

			}
			String finaltag=selected.getVideoLink()+","+tagName;
			int k=DBM.updateAllSelectedValues(conn,videoTag,finaltag,videoId,selectedCLassSection);
			if(k>=1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog(conn,finaltag);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Gallery is submitted successfully!"));
				galleryData=DBM.selectAllGalleryValues(conn,new DatabaseMethods1().schoolId());
				selected.setVideoLink(finaltag);
				datatable();
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
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please reenter again!"));
			}

		}
		else
		{
			show=false;
			int j=DBM.checkDuplicacyFromGalleryTable(conn,videoTag,new DatabaseMethods1().schoolId());
			if(j>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Gallery tag alredy exist!Please enter new gallery tag"));
			}
			else
			{

				for(galleryInfo ss:galleryList)
				{
					if(ss.getVideoLink()==null || ss.getVideoLink().equals(""))
					{

					}
					else
					{
						if(tagName.equals(""))
						{
							tagName=ss.getVideoLink();
						}
						else
						{
							tagName=tagName+","+ss.getVideoLink();
						}
					}

				}
				String finaltag=selected.getVideoLink()+","+tagName;
				int k=DBM.updateAllSelectedValues(conn,videoTag,finaltag,videoId,selectedCLassSection);
				if(k>=1)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn,finaltag);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Gallery is submitted successfully!"));
					galleryData=DBM.selectAllGalleryValues(conn,new DatabaseMethods1().schoolId());
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
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please reenter again!"));
				}
			}
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn,String tgName)
	{
	    String value = "";
		String language= "";
		
		language = "Gallery Name-"+videoTag+" --Vedio Id"+videoId;
		value = tgName;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Vedio Gallery","WEB",value,conn);
		return refNo;
	}
	
	
	public void link()
	{
		String links=selected2.getLinks().replace("watch?", "").replace("=", "/");
		videoLink=links.replace("&", "&amp;")+"&amp;hl=en&amp;fs=1&amp;";
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
	public String getVideoLink() {
		return videoLink;
	}
	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}
	public ArrayList<galleryInfo> getGalleryData() {
		return galleryData;
	}
	public void setGalleryData(ArrayList<galleryInfo> galleryData) {
		this.galleryData = galleryData;
	}
	public galleryInfo getSelected() {
		return selected;
	}
	public void setSelected(galleryInfo selected) {
		this.selected = selected;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public ArrayList<galleryInfo> getVideoLinkList() {
		return videoLinkList;
	}
	public void setVideoLinkList(ArrayList<galleryInfo> videoLinkList) {
		this.videoLinkList = videoLinkList;
	}
	public galleryInfo getSelected2() {
		return selected2;
	}
	public void setSelected2(galleryInfo selected2) {
		this.selected2 = selected2;
	}
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public boolean isView() {
		return view;
	}
	public void setView(boolean view) {
		this.view = view;
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