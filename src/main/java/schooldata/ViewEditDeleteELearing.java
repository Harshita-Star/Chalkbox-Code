package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="viewEditDeleteELearing")
@ViewScoped
public class ViewEditDeleteELearing implements Serializable{

	
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	String selectedCLassSection,selectedSection,userType,schoolid,assignmentPhotoPath1="",assignmentPhotoPath2="",assignmentPhotoPath3="",assignmentPhotoPath4="",assignmentPhotoPath5="",assignmentPhotoPath6="",assignmentName, staff, subject, type, label, des,assignmentPhoto="";
	Date assShowDate=new Date(), pDate, hDate=new Date();
	transient
	UploadedFile assignmentPhoto1,assignmentPhoto2,assignmentPhoto3,assignmentPhoto4,assignmentPhoto5,assignmentPhoto6;
	ArrayList<StudentInfo>list,selectedList=new ArrayList<>();
	StudentInfo selectedActivity;
	boolean show=false;
	Date startDt,endDt;
	
	String classid,sectionid,subjectid,title,description,link,file1,file2,file3,file4,file5,file6;
	
	public ViewEditDeleteELearing() {
	
		DatabaseMethods1 obj=new DatabaseMethods1();
	 	
       Connection conn=DataBaseConnection.javaConnection();
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
 		schoolid=(String) ses.getAttribute("schoolid");
 		startDt = new Date();
 		endDt = new Date();

		try
		{
			if(userType.equalsIgnoreCase("admin")
 					|| userType.equalsIgnoreCase("authority")
 					|| userType.equalsIgnoreCase("principal")
 					|| userType.equalsIgnoreCase("vice principal")
 					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
 					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection = new ArrayList<SelectItem>();
				ArrayList<SelectItem> tempClassSection = new ArrayList<SelectItem>();
				//classList=obj.allClass(conn);
				SelectItem si = new SelectItem();
				si.setLabel("All");
				si.setValue("All");
				classSection.add(si);
				
				tempClassSection=obj.allClass(conn);
				classSection.addAll(tempClassSection);

 			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
 					|| userType.equalsIgnoreCase("Administrative Officer"))
 			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection = new ArrayList<SelectItem>();
				classSection = obj.cordinatorClassList(empid, schoolid, conn);
 			}
 			else
 			{
 				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
 				classSection = new ArrayList<SelectItem>();
 				classSection=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
 				

 			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
	
	
	
	
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			if(selectedCLassSection.equalsIgnoreCase("All"))
			{
				sectionList = new ArrayList<SelectItem>();
				SelectItem section = new SelectItem();
				section.setLabel("All");
				section.setValue("All");
				sectionList.add(section); 
				selectedSection = "All";
			}
			else
			{
				sectionList=obj.allSectionWithAllOption(selectedCLassSection,conn);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedCLassSection, empid,conn);
		}
		//subjectList=new DatabaseMethods1().allSubjectClassWise(selectedCLassSection,conn);
		//sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void allSubjects()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson objJson=new DataBaseMeathodJson();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			subjectList=new DatabaseMethods1().allSubjectClassWise(selectedCLassSection,conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(staff,schoolid,conn);
			subjectList=objJson.AllEMployeeSubject(empid,selectedSection,schoolid,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(startDt.after(endDt))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Dates Properly"));
		}
		else
		{
		 list=new DataBaseMeathodJson().viewSubjectOnlineLecturejson(selectedSection, new DatabaseMethods1().schoolId(), conn, selectedCLassSection, subject,startDt,endDt);
		 show=true;
		} 
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void deleteNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.deleteOnlineLecture(String.valueOf(selectedActivity.getId()),conn);
		if(i>=1)
		{
			list=new DataBaseMeathodJson().viewSubjectOnlineLecturejson(selectedSection, new DatabaseMethods1().schoolId(), conn, selectedCLassSection, subject,startDt,endDt);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected online lecture Deleted Successfully"));
			selectedCLassSection="";
			selectedSection="";
			subject="";
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
	
	public void deleteMultiple()
	{
	   Connection conn=DataBaseConnection.javaConnection();
	   DatabaseMethods1 obj=new DatabaseMethods1();
	   
	  if(selectedList.size()==0)
	  {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select a Lecture"));
	  }
	  else
	  {   
	   int i= 0;
	   for(StudentInfo sc: selectedList)
	   {	
		i=obj.deleteOnlineLecture(String.valueOf(sc.getId()),conn);
	   }	
		if(i>=1)
		{
			list=new DataBaseMeathodJson().viewSubjectOnlineLecturejson(selectedSection, new DatabaseMethods1().schoolId(), conn, selectedCLassSection, subject,startDt,endDt);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected online lecture Deleted Successfully"));
			selectedCLassSection="";
			selectedSection="";
			subject="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}
	   }
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	public void edit()
	{
		classid=selectedActivity.getClassFromId();
		sectionid=selectedActivity.getSectionid();
		subjectid=selectedActivity.getSubjectStudied();
		title=selectedActivity.getTitle();
		description=selectedActivity.getDescription();
		link=selectedActivity.getLink();
		file1=selectedActivity.getFile1();
		file2=selectedActivity.getFile2();
		file3=selectedActivity.getFile3();
		file4=selectedActivity.getFile4();
		file5=selectedActivity.getFile5();
		file6=selectedActivity.getFile6();
		assShowDate=selectedActivity.getDate();
		assignmentPhotoPath1=selectedActivity.getFileName1();
		assignmentPhotoPath2=selectedActivity.getFileName2();
		assignmentPhotoPath3=selectedActivity.getFileName3();
		assignmentPhotoPath4=selectedActivity.getFileName4();
		assignmentPhotoPath5=selectedActivity.getFileName5();
		assignmentPhotoPath6=selectedActivity.getFileName6();
		//assignmentPhoto=selectedActivity.getLactureImage();
	}
	
	public void viewFile(String file)
	{
		PrimeFaces.current().executeInitScript("window.open('"+file+"')");
	}

	public void update()
	{
		  boolean extensionChecker = false;
		  if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
		  {
			String ext1 = FilenameUtils.getExtension(assignmentPhoto1.getFileName());
			if(ext1.equalsIgnoreCase("jpg")||ext1.equalsIgnoreCase("jpeg")||ext1.equalsIgnoreCase("pdf")||ext1.equalsIgnoreCase("doc")||ext1.equalsIgnoreCase("docx")||ext1.equalsIgnoreCase("png")) {
			        
			}
			else
			{
			        extensionChecker = true;
			}
		  }
		  
		  if (assignmentPhoto3 != null && assignmentPhoto3.getSize() > 0)
		  {
			String ext1 = FilenameUtils.getExtension(assignmentPhoto3.getFileName());
			if(ext1.equalsIgnoreCase("jpg")||ext1.equalsIgnoreCase("jpeg")||ext1.equalsIgnoreCase("pdf")||ext1.equalsIgnoreCase("doc")||ext1.equalsIgnoreCase("docx")||ext1.equalsIgnoreCase("png")) {
			       
			}
			else
			{
			        extensionChecker = true;
			}
		  }
		  
		  if (assignmentPhoto4 != null && assignmentPhoto4.getSize() > 0)
		  {
			String ext1 = FilenameUtils.getExtension(assignmentPhoto4.getFileName());
			if(ext1.equalsIgnoreCase("jpg")||ext1.equalsIgnoreCase("jpeg")||ext1.equalsIgnoreCase("pdf")||ext1.equalsIgnoreCase("doc")||ext1.equalsIgnoreCase("docx")||ext1.equalsIgnoreCase("png")) {
			        
			}
			else
			{
			        extensionChecker = true;
			}
		  }
		  
		  if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
		  {
			String ext1 = FilenameUtils.getExtension(assignmentPhoto5.getFileName());
			if(ext1.equalsIgnoreCase("jpg")||ext1.equalsIgnoreCase("jpeg")||ext1.equalsIgnoreCase("pdf")||ext1.equalsIgnoreCase("doc")||ext1.equalsIgnoreCase("docx")||ext1.equalsIgnoreCase("png")) {
			       
			}
			else
			{
			        extensionChecker = true;
			}
		  }
		  
		  if (assignmentPhoto6 != null && assignmentPhoto6.getSize() > 0)
		  {
			String ext1 = FilenameUtils.getExtension(assignmentPhoto6.getFileName());
			if(ext1.equalsIgnoreCase("jpg")||ext1.equalsIgnoreCase("jpeg")||ext1.equalsIgnoreCase("pdf")||ext1.equalsIgnoreCase("doc")||ext1.equalsIgnoreCase("docx")||ext1.equalsIgnoreCase("png")) {
			       
			}
			else
			{
			        extensionChecker = true;
			}
		  }
		  
		  if (assignmentPhoto2 != null && assignmentPhoto2.getSize() > 0)
		  {
			String ext1 = FilenameUtils.getExtension(assignmentPhoto2.getFileName());
			
			if(ext1.equalsIgnoreCase("mp3")||ext1.equalsIgnoreCase("ogg")||ext1.equalsIgnoreCase("aac")||ext1.equalsIgnoreCase("m4a")||ext1.equalsIgnoreCase("mp4")||ext1.equalsIgnoreCase("mpeg")) {
			       
			}
			else
			{
			        extensionChecker = true;
			}
		  }
			
			
		  if(extensionChecker)
		  {
			  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Files of Specified Type Only"));
		  }
		  else if(title==null||title.equals("")) {
			  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter title/Topic"));
		  }
		  else
		  {
		
		assignmentPhoto="";
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		
		pDate=new Date();
		String dt=new SimpleDateFormat("yMdhms").format(pDate);
		if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(pDate);
			assignmentPhotoPath1 = assignmentPhoto1.getFileName();
			String exten[]=assignmentPhotoPath1.split("\\.");
			assignmentPhotoPath1=staff+"_"+subject+"_"+dt+"_1_"+selectedSection+"."+exten[exten.length-1];
			if(assignmentPhoto.equals(""))
			{
				assignmentPhoto=assignmentPhotoPath1;
			}
			else
			{
				assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath1;
			}
			DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto1,assignmentPhotoPath1,conn);
		}
		else
		{
			if(!assignmentPhotoPath1.equals(""))
			{
				if(assignmentPhoto.equals(""))
				{
					assignmentPhoto=assignmentPhotoPath3;
				}
				else
				{
					assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath3;
				}
			}
		}

		
		if (assignmentPhoto3 != null && assignmentPhoto3.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(pDate);
			assignmentPhotoPath3 = assignmentPhoto3.getFileName();
			String exten[]=assignmentPhotoPath3.split("\\.");
			assignmentPhotoPath3=staff+"_"+subject+"_"+dt+"_3_"+selectedSection+"."+exten[exten.length-1];
			if(assignmentPhoto.equals(""))
			{
				assignmentPhoto=assignmentPhotoPath3;
			}
			else
			{
				assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath3;
			}
			DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto3,assignmentPhotoPath3,conn);
		}
		else
		{
			if(!assignmentPhotoPath3.equals(""))
			{
				if(assignmentPhoto.equals(""))
				{
					assignmentPhoto=assignmentPhotoPath3;
				}
				else
				{
					assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath3;
				}
			}
		}
		
		
		if (assignmentPhoto4 != null && assignmentPhoto4.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(pDate);
			assignmentPhotoPath4 = assignmentPhoto4.getFileName();
			String exten[]=assignmentPhotoPath4.split("\\.");
			assignmentPhotoPath4=staff+"_"+subject+"_"+dt+"_4_"+selectedSection+"."+exten[exten.length-1];
			if(assignmentPhoto.equals(""))
			{
				assignmentPhoto=assignmentPhotoPath4;
			}
			else
			{
				assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath4;
			}
			DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto4,assignmentPhotoPath4,conn);
		}
		else
		{
			if(!assignmentPhotoPath4.equals(""))
			{
				if(assignmentPhoto.equals(""))
				{
					assignmentPhoto=assignmentPhotoPath4;
				}
				else
				{
					assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath4;
				}
			}
		}
		
		
		if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(pDate);
			assignmentPhotoPath5 = assignmentPhoto5.getFileName();
			String exten[]=assignmentPhotoPath5.split("\\.");
			assignmentPhotoPath5=staff+"_"+subject+"_"+dt+"_5_"+selectedSection+"."+exten[exten.length-1];
			if(assignmentPhoto.equals(""))
			{
				assignmentPhoto=assignmentPhotoPath5;
			}
			else
			{
				assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath5;
			}
			DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto5,assignmentPhotoPath5,conn);
		}
		else
		{
			if(!assignmentPhotoPath5.equals(""))
			{
				if(assignmentPhoto.equals(""))
				{
					assignmentPhoto=assignmentPhotoPath5;
				}
				else
				{
					assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath5;
				}
			}
		}
		
		
		if (assignmentPhoto6 != null && assignmentPhoto6.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(pDate);
			assignmentPhotoPath6 = assignmentPhoto6.getFileName();
			String exten[]=assignmentPhotoPath6.split("\\.");
			assignmentPhotoPath6=staff+"_"+subject+"_"+dt+"_6_"+selectedSection+"."+exten[exten.length-1];
			if(assignmentPhoto.equals(""))
			{
				assignmentPhoto=assignmentPhotoPath6;
			}
			else
			{
				assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath6;
			}
			DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto6,assignmentPhotoPath6,conn);
		}
		else
		{
			if(!assignmentPhotoPath6.equals(""))
			{
				if(assignmentPhoto.equals(""))
				{
					assignmentPhoto=assignmentPhotoPath6;
				}
				else
				{
					assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath6;
				}
			}
		}
		
		
		if (assignmentPhoto2 != null && assignmentPhoto2.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(pDate);
			assignmentPhotoPath2 = assignmentPhoto2.getFileName();
			String exten[]=assignmentPhotoPath2.split("\\.");
			assignmentPhotoPath2=staff+"_"+subject+"_"+dt+"_2_"+selectedSection+"."+exten[exten.length-1];
			if(assignmentPhoto.equals(""))
			{
				assignmentPhoto=assignmentPhotoPath2;
			}
			else
			{
				assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath2;
			}
			
			DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto2,assignmentPhotoPath2,conn);
		}
		else
		{
			if(!assignmentPhotoPath2.equals(""))
			{
				if(assignmentPhoto.equals(""))
				{
					assignmentPhoto=assignmentPhotoPath2;
				}
				else
				{
					assignmentPhoto=assignmentPhoto+","+assignmentPhotoPath2;
				}
			}
		}
		
		
		
		int i = DBJ.updateElearning(title,description,link,assignmentPhoto,String.valueOf(selectedActivity.getId()),assShowDate,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Online Lecture Updated Sucessfully"));
			list=new DataBaseMeathodJson().viewSubjectOnlineLecturejson(selectedSection, new DatabaseMethods1().schoolId(), conn, selectedCLassSection, subject,startDt,endDt);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	  }	
	}
	
	public void viewAssignment()
	{
		
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
 		ses.setAttribute("lectureId", String.valueOf(selectedActivity.getId()));
 	
 		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		try
		{
			cc.redirect("ViewLectureAssigment.xhtml");
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getAssignmentPhotoPath1() {
		return assignmentPhotoPath1;
	}

	public void setAssignmentPhotoPath1(String assignmentPhotoPath1) {
		this.assignmentPhotoPath1 = assignmentPhotoPath1;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getAssignmentPhoto() {
		return assignmentPhoto;
	}

	public void setAssignmentPhoto(String assignmentPhoto) {
		this.assignmentPhoto = assignmentPhoto;
	}

	public Date getAssShowDate() {
		return assShowDate;
	}

	public void setAssShowDate(Date assShowDate) {
		this.assShowDate = assShowDate;
	}

	public Date getpDate() {
		return pDate;
	}

	public void setpDate(Date pDate) {
		this.pDate = pDate;
	}

	public Date gethDate() {
		return hDate;
	}

	public void sethDate(Date hDate) {
		this.hDate = hDate;
	}

	public UploadedFile getAssignmentPhoto1() {
		return assignmentPhoto1;
	}

	public void setAssignmentPhoto1(UploadedFile assignmentPhoto1) {
		this.assignmentPhoto1 = assignmentPhoto1;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public StudentInfo getSelectedActivity() {
		return selectedActivity;
	}

	public void setSelectedActivity(StudentInfo selectedActivity) {
		this.selectedActivity = selectedActivity;
	}
	
	public String getAssignmentPhotoPath2() {
		return assignmentPhotoPath2;
	}

	public void setAssignmentPhotoPath2(String assignmentPhotoPath2) {
		this.assignmentPhotoPath2 = assignmentPhotoPath2;
	}

	public UploadedFile getAssignmentPhoto2() {
		return assignmentPhoto2;
	}

	public void setAssignmentPhoto2(UploadedFile assignmentPhoto2) {
		this.assignmentPhoto2 = assignmentPhoto2;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getSectionid() {
		return sectionid;
	}

	public void setSectionid(String sectionid) {
		this.sectionid = sectionid;
	}

	public String getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getFile2() {
		return file2;
	}

	public void setFile2(String file2) {
		this.file2 = file2;
	}

	public String getAssignmentPhotoPath3() {
		return assignmentPhotoPath3;
	}

	public void setAssignmentPhotoPath3(String assignmentPhotoPath3) {
		this.assignmentPhotoPath3 = assignmentPhotoPath3;
	}

	public String getAssignmentPhotoPath4() {
		return assignmentPhotoPath4;
	}

	public void setAssignmentPhotoPath4(String assignmentPhotoPath4) {
		this.assignmentPhotoPath4 = assignmentPhotoPath4;
	}

	public String getAssignmentPhotoPath5() {
		return assignmentPhotoPath5;
	}

	public void setAssignmentPhotoPath5(String assignmentPhotoPath5) {
		this.assignmentPhotoPath5 = assignmentPhotoPath5;
	}

	public String getAssignmentPhotoPath6() {
		return assignmentPhotoPath6;
	}

	public void setAssignmentPhotoPath6(String assignmentPhotoPath6) {
		this.assignmentPhotoPath6 = assignmentPhotoPath6;
	}

	public String getFile3() {
		return file3;
	}

	public void setFile3(String file3) {
		this.file3 = file3;
	}

	public String getFile4() {
		return file4;
	}

	public void setFile4(String file4) {
		this.file4 = file4;
	}

	public String getFile5() {
		return file5;
	}

	public void setFile5(String file5) {
		this.file5 = file5;
	}

	public String getFile6() {
		return file6;
	}

	public void setFile6(String file6) {
		this.file6 = file6;
	}

	public UploadedFile getAssignmentPhoto3() {
		return assignmentPhoto3;
	}

	public void setAssignmentPhoto3(UploadedFile assignmentPhoto3) {
		this.assignmentPhoto3 = assignmentPhoto3;
	}

	public UploadedFile getAssignmentPhoto4() {
		return assignmentPhoto4;
	}

	public void setAssignmentPhoto4(UploadedFile assignmentPhoto4) {
		this.assignmentPhoto4 = assignmentPhoto4;
	}

	public UploadedFile getAssignmentPhoto5() {
		return assignmentPhoto5;
	}

	public void setAssignmentPhoto5(UploadedFile assignmentPhoto5) {
		this.assignmentPhoto5 = assignmentPhoto5;
	}

	public UploadedFile getAssignmentPhoto6() {
		return assignmentPhoto6;
	}

	public void setAssignmentPhoto6(UploadedFile assignmentPhoto6) {
		this.assignmentPhoto6 = assignmentPhoto6;
	}

	public Date getStartDt() {
		return startDt;
	}

	public void setStartDt(Date startDt) {
		this.startDt = startDt;
	}

	public Date getEndDt() {
		return endDt;
	}

	public void setEndDt(Date endDt) {
		this.endDt = endDt;
	}

	public ArrayList<StudentInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<StudentInfo> selectedList) {
		this.selectedList = selectedList;
	}
	
	
}
