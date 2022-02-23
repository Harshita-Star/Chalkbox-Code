package schooldata;

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
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="uploadNotes")
@ViewScoped
public class UploadNotesBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	String selectedCLassSection,selectedSection,userType,schoolid,assignmentPhotoPath1="",sms="",notification="",
			assignmentPhotoPath2="",assignmentPhotoPath3="",assignmentPhotoPath4="",assignmentPhotoPath5="",assignmentName, staff, subject, type, label, des;
	Date assShowDate=new Date();
	Date pDate;
	transient
	UploadedFile assignmentPhoto1,assignmentPhoto2,assignmentPhoto3,assignmentPhoto4,assignmentPhoto5;


	public UploadNotesBean()
	{
		type="notes";
		sms="no";
		notification="yes";
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		schoolid=(String) ses.getAttribute("schoolid");
		try
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection=obj.allClass(conn);

			}
			else if(userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection=obj.cordinatorClassList(empid, schoolid, conn);

			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
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
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList = obj.allSection(selectedCLassSection,conn);
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
		if(userType.equalsIgnoreCase("admin"))
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

	public void checkType()
	{
		label=type;
	}

	public String upload()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		try
		{

			pDate=new Date();
			String dt=new SimpleDateFormat("yMdhms").format(pDate);
			if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(pDate);
				assignmentPhotoPath1 = assignmentPhoto1.getFileName();
				String exten[]=assignmentPhotoPath1.split("\\.");
				assignmentPhotoPath1=staff+"_"+subject+"_"+dt+"_1_"+selectedSection+"."+exten[exten.length-1];
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto1,assignmentPhotoPath1,conn);
			}

			if (assignmentPhoto2 != null && assignmentPhoto2.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(pDate);
				assignmentPhotoPath2 = assignmentPhoto2.getFileName();
				String exten[]=assignmentPhotoPath2.split("\\.");
				assignmentPhotoPath2=staff+"_"+subject+"_"+dt+"_2_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath2;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto2,assignmentPhotoPath2,conn);
			}

			if (assignmentPhoto3 != null && assignmentPhoto3.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(pDate);
				assignmentPhotoPath3 = assignmentPhoto3.getFileName();
				String exten[]=assignmentPhotoPath3.split("\\.");
				assignmentPhotoPath3=staff+"_"+subject+"_"+dt+"_3_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath3;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto3,assignmentPhotoPath3,conn);
			}

			if (assignmentPhoto4 != null && assignmentPhoto4.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(pDate);
				assignmentPhotoPath4 = assignmentPhoto4.getFileName();
				String exten[]=assignmentPhotoPath4.split("\\.");
				assignmentPhotoPath4=staff+"_"+subject+"_"+dt+"_4_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath4;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto4,assignmentPhotoPath4,conn);
			}

			if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(pDate);
				assignmentPhotoPath5 = assignmentPhoto5.getFileName();
				String exten[]=assignmentPhotoPath5.split("\\.");
				assignmentPhotoPath5=staff+"_"+subject+"_"+dt+"_5_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath5;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto5,assignmentPhotoPath5,conn);

			}

			HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			staff=(String) ses.getAttribute("username");

			int i=DBM.submitAssignment(selectedCLassSection, selectedSection, subject, staff, pDate, assShowDate, assignmentPhotoPath1, "", "",
					"", "", assignmentName, type, des,conn);
			if(i>=1)
			{
				String session = DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
				String subjectName = "";
				if(subject.equalsIgnoreCase("All"))
				{
					subjectName = "All";
				}
				else
				{
					subjectName = DBM.subjectNameFromid(subject, conn);
				}

				String className = DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection, session, conn);
				String sectionName = DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
				String clsSection = className+"-"+sectionName;

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Manual/Notes Uploaded Sucessfully"));
				ArrayList<StudentInfo> studentList = new ArrayList<>();

				String subType = "";
				if(subject.equalsIgnoreCase("All"))
				{
					subType = "All,Mandatory,All";
				}
				else
				{
					subType = DBM.subjectNameAndTypeFromid(subject, conn);
				}

				String[] temp=subType.split(",");
				if(temp[1].equalsIgnoreCase("Mandatory"))
				{
					if(notification.equalsIgnoreCase("yes"))
					{
						String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
						String hw = cls+"\n"+des;
						DBM.notification(DBM.schoolId(),"Manual/Notes",hw,selectedSection+"-"+selectedCLassSection+"-"+DBM.schoolId(),conn);
					}



					if(sms.equalsIgnoreCase("yes"))
					{
						SchoolInfoList info=DBM.fullSchoolInfo(conn);
						studentList=DBM.searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);

						if(studentList.size()>0)
						{
							String contactNumber = "";
							String addNumber="";
							int x=0;

							String typeMessage="Dear Parent,"+"\n"+"Manual/Notes for class:"+clsSection+" subject:"+subjectName+" are uploaded.\n"+"Regards\n"+info.getSchoolName();
							for(StudentInfo ss : studentList)
							{

								//secondCounter++;
								if (String.valueOf(ss.getFathersPhone()).length() == 10
										&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
										&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
										&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
										&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
										&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
								{
									x++;
									if(contactNumber.equals(""))
									{
										contactNumber=String.valueOf(ss.getFathersPhone());
										addNumber=ss.getAddNumber();
									}
									else
									{
										contactNumber=contactNumber+","+String.valueOf(ss.getFathersPhone());
										addNumber=addNumber+","+ss.getAddNumber();
									}
								}

							}


							if(x>0)
							{
								DBM.messageurl1(contactNumber, typeMessage,addNumber,conn,DBM.schoolId(),"");
							}
						}

					}
				}
				else
				{
					studentList=DBM.getAllStudentStrentgthForOptional(DBM.schoolId(),subject,selectedSection,selectedCLassSection,"fromJson",conn);

					if(notification.equalsIgnoreCase("yes"))
					{
						String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
						String hw = cls+"\n"+des;
						//DBM.notification("Home Work",hw,selectedSection+"-"+selectedCLassSection+"-"+DBM.schoolId(),conn);

						for(StudentInfo ss : studentList)
						{
							if(ss.getFathersPhone()==ss.getMothersPhone())
							{
								DBM.notification(DBM.schoolId(),"Manual/Notes",hw, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
							}
							else
							{
								DBM.notification(DBM.schoolId(),"Manual/Notes",hw, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
								DBM.notification(DBM.schoolId(),"Manual/Notes",hw, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
							}
						}	
					}

					if(sms.equalsIgnoreCase("yes"))
					{
						SchoolInfoList info=DBM.fullSchoolInfo(conn);

						if(studentList.size()>0)
						{
							String contactNumber = "";
							String addNumber="";
							int x=0;

							String typeMessage="Dear Parent,"+"\n"+"Manual/Notes for class:"+clsSection+" subject:"+subjectName+" are uploaded.\n"+des+"\n"+"Regards\n"+info.getSchoolName();
							for(StudentInfo ss : studentList)
							{

								//secondCounter++;
								if (String.valueOf(ss.getFathersPhone()).length() == 10
										&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
										&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
										&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
										&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
										&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
								{
									x++;
									if(contactNumber.equals(""))
									{
										contactNumber=String.valueOf(ss.getFathersPhone());
										addNumber=ss.getAddNumber();
									}
									else
									{
										contactNumber=contactNumber+","+String.valueOf(ss.getFathersPhone());
										addNumber=addNumber+","+ss.getAddNumber();
									}
								}

							}


							if(x>0)
							{
								DBM.messageurl1(contactNumber, typeMessage,addNumber,conn,DBM.schoolId(),"");
							}
						}

					}

				}

				return "uploadNotes.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}
	/*public String uploadSyllabus()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		try
		{
	     	pDate=new Date();
			String dt=new SimpleDateFormat("dd-MM-yyyy").format(pDate);

			if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
			{
				assignmentPhotoPath5 = assignmentPhoto5.getFileName();
				String exten[]=assignmentPhotoPath5.split("\\.");
				assignmentPhotoPath5=staff+"_"+subject+"_"+dt+"_5_"+selectedSection+"."+exten[exten.length-1];
				DBM.makeProfile(assignmentPhoto5,assignmentPhotoPath5,conn);
			}

			int i=DBM.uploadSyallbus(selectedCLassSection,assignmentPhotoPath5,conn);

			if(i>=1)
			{
				DBM.notification("Syallbus","Your Class Syllabus Is Added",selectedCLassSection+"-"+new DatabaseMethods1().schoolId(),conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Assignment Upload Sucessfully"));
				return "addSyllabus.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}*/





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

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
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







}
