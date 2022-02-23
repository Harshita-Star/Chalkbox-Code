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
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="studentElerningBean")
@ViewScoped
public class StudentElerningBean implements Serializable
{
	transient
	UploadedFile assignmentPhoto1,assignmentPhoto2,assignmentPhoto3,assignmentPhoto4,assignmentPhoto5,assignmentPhoto6;
	
	String selectedCLassSection,selectedSection,userType,schoolid,assignmentPhotoPath1="",sms="",notification="",
			assignmentPhotoPath2="",assignmentPhotoPath3="",assignmentPhotoPath4="",assignmentPhotoPath5="",assignmentPhotoPath6="",assignmentName,
			staff, subject, type, label, des,balMsg;	
	String desc;
	String assigmentDescription;
	
	ArrayList<StudentInfo>list=new ArrayList<>();
	StudentInfo selectedInfo;
	StudentInfo selectedAttachment;
	String studentid,schid;
	ArrayList<StudentInfo> attachmentList=new ArrayList<>(),assignmentList=new ArrayList<>();
   public StudentElerningBean() 
   {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		 studentid = (String) ss.getAttribute("username");
		 schid = new DatabaseMethods1().schoolId();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

		StudentInfo info =DBJ.studentDetailslistByAddNo(studentid, schid,conn);
		String selectedSection=info.getSectionid();
		String selectedCLassSection=info.getClassId();
		list=new DataBaseMeathodJson().viewSubjectOnlineLecturejsonWithoutDate(selectedSection, new DatabaseMethods1().schoolId(), conn, selectedCLassSection, "all");
		if(schid.equals("216") || schid.equals("221")) {
			list = new DataBaseMeathodJson().checkForOptionSubjectsAllocation(list,studentid,schid,conn);
		}
		boolean newcheck=DBJ.checkOnlineAttendance(studentid,schid,conn);
		if(newcheck==false)
		{
			DBJ.addOnlineAttedance(studentid,schid,conn);
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
   }
   
   
   public void deleteAssignment()
   {
	   Connection conn = DataBaseConnection.javaConnection();
		
	   StudentInfo list=new DataBaseMeathodJson().submitAssignmentStudent(studentid,String.valueOf(selectedInfo.getId()),schid,conn);
	  SchoolInfoList list1=new DataBaseMeathodJson().fullSchoolInfo(schid,conn);
		  
	  int  i=new DataBaseMeathodJson().deleteAssignmentStudent(studentid,String.valueOf(selectedInfo.getId()),schid,conn);
	   if(i>0)
	   {
		 
		   String[] images=list.getLactureImage().split(",");
		   
		   for(int j=0;j<images.length;j++)
		   {
			   File file = new File(list1.getUploadpath()+images[j]);
				file.delete();
	    	
		   }
		   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Assignment Delete Sucessfully"));
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			
		}
   }
   
   public void check()
   {
	   Connection conn=DataBaseConnection.javaConnection();
	   boolean check=new DataBaseMeathodJson().checkAssigmentSubmit(String.valueOf(selectedInfo.getId()),studentid,conn);
		if(check)
		{
			
			assignmentList =new ArrayList<>();
			StudentInfo list=new DataBaseMeathodJson().submitAssignmentStudent(studentid, String.valueOf(selectedInfo.getId()), schid, conn);
			SchoolInfoList info=new DataBaseMeathodJson().fullSchoolInfo(schid, conn);
			assigmentDescription=list.getDescription();
			String [] images=list.getLactureImage().split(",");
			for(int i=0;i<images.length;i++)
			{
				StudentInfo ls=new StudentInfo();
				ls.setFile1("File" + String.valueOf(i+1) );
				ls.setFile2(info.getDownloadpath()+images[i]);
				assignmentList.add(ls);
				
			}
			
			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('viewAssignment').show();");
		}
		else
		{
			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('editDialog').show();");
		}
			
			
			try {
				
				conn.close();
			} catch (Exception e) {
               e.printStackTrace();	
		}
   }
   
   
   
   public void addAssignemnt()
   {
	   Connection conn=DataBaseConnection.javaConnection();
	   DatabaseMethods1 DBM=new DatabaseMethods1();
	   DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		
	   String assignmentPhoto="";
		Date pDate=new Date();
		String dt=new SimpleDateFormat("yMdhms").format(new Date());
		if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(new Date());
			assignmentPhotoPath1 = assignmentPhoto1.getFileName();
			String exten[]=assignmentPhotoPath1.split("\\.");
			assignmentPhotoPath1=studentid+"_"+selectedInfo.getId()+"_"+dt+"_1_."+exten[exten.length-1];
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
		
		
		
		if (assignmentPhoto3 != null && assignmentPhoto3.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(new Date());
			assignmentPhotoPath3 = assignmentPhoto3.getFileName();
			String exten[]=assignmentPhotoPath3.split("\\.");
			assignmentPhotoPath3=studentid+"_"+selectedInfo.getId()+"_"+dt+"_3_."+exten[exten.length-1];
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
		
		
		if (assignmentPhoto4 != null && assignmentPhoto4.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(new Date());
			assignmentPhotoPath4 = assignmentPhoto1.getFileName();
			String exten[]=assignmentPhotoPath4.split("\\.");
			assignmentPhotoPath4=studentid+"_"+selectedInfo.getId()+"_"+dt+"_4_."+exten[exten.length-1];
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
		
		if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(new Date());
			assignmentPhotoPath5 = assignmentPhoto5.getFileName();
			String exten[]=assignmentPhotoPath5.split("\\.");
			assignmentPhotoPath5=studentid+"_"+selectedInfo.getId()+"_"+dt+"_5_."+exten[exten.length-1];
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
		
		
		if (assignmentPhoto6 != null && assignmentPhoto6.getSize() > 0)
		{
			dt=new SimpleDateFormat("yMdhms").format(new Date());
			assignmentPhotoPath6 = assignmentPhoto1.getFileName();
			String exten[]=assignmentPhotoPath6.split("\\.");
			assignmentPhotoPath1=studentid+"_"+selectedInfo.getId()+"_"+dt+"_6_."+exten[exten.length-1];
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
		
		
		
		
		int i=DBJ.submitAssigment(studentid,String.valueOf(selectedInfo.getId()) ,pDate,assignmentPhoto, desc,schid,studentid,"student",conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Assignment Upload Sucessfully"));
					
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			
		}
		
		
		
   }
   
   public void attachment()
   {
	   attachmentList =new ArrayList<>();
	   if(!selectedInfo.getLink().equals(""))
	   {
		   
		  String[] list1=selectedInfo.getLink().split(",");
		  
		  for(int i=0;i<list1.length;i++)
		  {
			  int j=i+1;
			   StudentInfo info=new StudentInfo();
			   info.setFile1("Youtube "+j );
			   info.setFile2(list1[i]);
			   attachmentList.add(info);
			  
		  }
		 }
	   
	   
	   if(!selectedInfo.getFile2().equals(""))
	   {
		   StudentInfo info=new StudentInfo();
		   info.setFile1("Audio");
		   info.setFile2(selectedInfo.getFile2());
		   attachmentList.add(info);
	   }
	   
	   if(!selectedInfo.getFile1().equals(""))
	   {
		   StudentInfo info=new StudentInfo();
		   info.setFile1("File 1");
		   info.setFile2(selectedInfo.getFile1());
		   attachmentList.add(info);
	   }
	   
	   if(!selectedInfo.getFile3().equals(""))
	   {
		   StudentInfo info=new StudentInfo();
		   info.setFile1("File 2");
		   info.setFile2(selectedInfo.getFile3());
		   attachmentList.add(info);
	   }
	   
	   
	   if(!selectedInfo.getFile4().equals(""))
	   {
		   StudentInfo info=new StudentInfo();
		   info.setFile1("File 3");
		   info.setFile2(selectedInfo.getFile4());
		   attachmentList.add(info);
	   }
	   
	   
	   if(!selectedInfo.getFile5().equals(""))
	   {
		   StudentInfo info=new StudentInfo();
		   info.setFile1("File 4");
		   info.setFile2(selectedInfo.getFile5());
		   attachmentList.add(info);
	   }
	   
	   
	   if(!selectedInfo.getFile6().equals(""))
	   {
		   StudentInfo info=new StudentInfo();
		   info.setFile1("File 6");
		   info.setFile2(selectedInfo.getFile6());
		   attachmentList.add(info);
	   }
	   
	   
   }
   
   
   public void view1()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String url="";
		
		if(selectedAttachment.getFile1().equalsIgnoreCase("Youtube"))
		{
			url=selectedAttachment.getFile2();
		}
		else
		{
			url=selectedAttachment.getFile2();
					
		}
		PrimeFaces.current().executeInitScript("window.open('"+url+"')");
		
		

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
   
   
   
public ArrayList<StudentInfo> getList() {
	return list;
}
public void setList(ArrayList<StudentInfo> list) {
	this.list = list;
}


public UploadedFile getAssignmentPhoto1() {
	return assignmentPhoto1;
}


public void setAssignmentPhoto1(UploadedFile assignmentPhoto1) {
	this.assignmentPhoto1 = assignmentPhoto1;
}


public UploadedFile getAssignmentPhoto2() {
	return assignmentPhoto2;
}


public void setAssignmentPhoto2(UploadedFile assignmentPhoto2) {
	this.assignmentPhoto2 = assignmentPhoto2;
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


public String getAssignmentPhotoPath1() {
	return assignmentPhotoPath1;
}


public void setAssignmentPhotoPath1(String assignmentPhotoPath1) {
	this.assignmentPhotoPath1 = assignmentPhotoPath1;
}


public String getSms() {
	return sms;
}


public void setSms(String sms) {
	this.sms = sms;
}


public String getNotification() {
	return notification;
}


public void setNotification(String notification) {
	this.notification = notification;
}


public String getAssignmentPhotoPath2() {
	return assignmentPhotoPath2;
}


public void setAssignmentPhotoPath2(String assignmentPhotoPath2) {
	this.assignmentPhotoPath2 = assignmentPhotoPath2;
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


public String getBalMsg() {
	return balMsg;
}


public void setBalMsg(String balMsg) {
	this.balMsg = balMsg;
}


public String getDesc() {
	return desc;
}


public void setDesc(String desc) {
	this.desc = desc;
}


public String getStudentid() {
	return studentid;
}


public void setStudentid(String studentid) {
	this.studentid = studentid;
}


public String getSchid() {
	return schid;
}


public void setSchid(String schid) {
	this.schid = schid;
}


public StudentInfo getSelectedInfo() {
	return selectedInfo;
}


public void setSelectedInfo(StudentInfo selectedInfo) {
	this.selectedInfo = selectedInfo;
}

public ArrayList<StudentInfo> getAttachmentList() {
	return attachmentList;
}

public void setAttachmentList(ArrayList<StudentInfo> attachmentList) {
	this.attachmentList = attachmentList;
}

public StudentInfo getSelectedAttachment() {
	return selectedAttachment;
}

public void setSelectedAttachment(StudentInfo selectedAttachment) {
	this.selectedAttachment = selectedAttachment;
}

public String getAssigmentDescription() {
	return assigmentDescription;
}

public void setAssigmentDescription(String assigmentDescription) {
	this.assigmentDescription = assigmentDescription;
}

public ArrayList<StudentInfo> getAssignmentList() {
	return assignmentList;
}

public void setAssignmentList(ArrayList<StudentInfo> assignmentList) {
	this.assignmentList = assignmentList;
}
   
   
   
}
