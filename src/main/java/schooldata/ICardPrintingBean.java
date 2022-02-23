package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.RequestContext;
import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;

@ManagedBean(name="icardPrinting")
@ViewScoped
public class ICardPrintingBean implements Serializable
{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> sectionList;
	String selectedSection;
	StudentInfo selectedStudent;
	String studentImage,studentImage1,studentImage2,studentImage3,studentImage4,studentImage5,studentImage6,studentImage7,studentImage8,studentImage9;
	boolean showImage,showImage1,showImage2,showImage3,showImage4,showImage5,showImage6,showImage7,showImage8,showImage9;
	String fname4,className4,fathersName4,currentAddress4,fathersPhone4;
	String fname3,className3,fathersName3,currentAddress3,fathersPhone3;
	String fname2,className2,fathersName2,currentAddress2,fathersPhone2;
	String fname1,className1,fathersName1,currentAddress1,fathersPhone1;
	String fname5,className5,fathersName5,currentAddress5,fathersPhone5;
	String fname6,className6,fathersName6,currentAddress6,fathersPhone6;
	String fname7,className7,fathersName7,currentAddress7,fathersPhone7;
	String fname8,className8,fathersName8,currentAddress8,fathersPhone8;
	String fname9,className9,fathersName9,currentAddress9,fathersPhone9;
	String srNo1,srNo2,srNo3,srNo4,srNo5,srNo6,srNo7,srNo8,srNo9;
	String name;
	String session;
	String border;
	String header;
	String examTerm;
	boolean showforoak,normal;

	ArrayList<StudentInfo> studentList,selectedStudentList;
	boolean show1,show2,show3,show4,show,show5,show6,show7,show8,show9;
	String selectedCLassSection,username,userType,schId;
	ArrayList<SelectItem> classSection;
	DatabaseMethods1 dd=new DatabaseMethods1();


	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=dd.allSection(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
			sectionList=dd.allSectionForTeacher(selectedCLassSection,empid,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=dd.searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public  ICardPrintingBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schId=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		if(schId.equals("339")) {
			
			showforoak = true;
			normal = false;
			header = "Admit card";
			session = new DatabaseMethods1().selectedSessionDetails(schId, conn);
			border = "Class Teacher";
			examTerm = "Term-II - PT-III";
		}else {
			showforoak = false;
			normal = true;
			header = "Identity Card";
		}
		try
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff"))
			{
				classSection=dd.allClass(conn);
			}
			else if(userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
				classSection=dd.cordinatorClassList(empid, schId, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
				classSection=dd.allClassListForClassTeacher(empid,schId,conn);
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
	public void searchStudentByName()
	{
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						studentList.add(info);

						show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}
	public String backToManageStudentDashboard()
	{

		show=false;
		name=null;
		selectedCLassSection=null;
		selectedStudent=null;

		return "manageStudentDashboard";
	}
	public void searchStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			studentList=new DatabaseMethods1().searchStudentList(name,conn);
			show=true;
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
	public void ss()
	{
		PrimeFaces.current().dialog().openDynamic("showExpense");
	}
	public void searchStudentByClassSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			studentList=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('dlg2').hide();");

			show=true;
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
	/*public void editNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
			if(selectedStudent!= null)
			{
				showImage=true;
				studentImage=new DatabaseMethods1().studentImage(selectedStudent.getAddNumber(),conn);
			}
			else
			{
				showImage=false;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a student from the list", "Validation error"));
			}

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	 */
	public void editNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		for(int i=0;i<selectedStudentList.size();i++)
		{
			if(i==0)
			{
				srNo1 = selectedStudentList.get(0).getSrNo();
				fname1=selectedStudentList.get(0).getFname();
				className1=selectedStudentList.get(0).getClassName();
				fathersName1=selectedStudentList.get(0).getFathersName();
				currentAddress1=selectedStudentList.get(0).getCurrentAddress();
				fathersPhone1=String.valueOf(selectedStudentList.get(0).getFathersPhone());
				showImage1=true;
				studentImage1=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(0).getAddNumber(),conn);
				show1=true;
			}
			else if(i==1)
			{
				srNo2 = selectedStudentList.get(1).getSrNo();
				fname2=selectedStudentList.get(1).getFname();
				className2=selectedStudentList.get(1).getClassName();
				fathersName2=selectedStudentList.get(1).getFathersName();
				currentAddress2=selectedStudentList.get(1).getCurrentAddress();
				fathersPhone2=String.valueOf(selectedStudentList.get(1).getFathersPhone());
				showImage2=true;
				studentImage2=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(1).getAddNumber(),conn);
				show2=true;
			}
			else if(i==2)
			{
				srNo3 = selectedStudentList.get(2).getSrNo();
				fname3=selectedStudentList.get(2).getFname();
				className3=selectedStudentList.get(2).getClassName();
				fathersName3=selectedStudentList.get(2).getFathersName();
				currentAddress3=selectedStudentList.get(2).getCurrentAddress();
				fathersPhone3=String.valueOf(selectedStudentList.get(2).getFathersPhone());
				showImage3=true;
				studentImage3=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(2).getAddNumber(),conn);
				show3=true;
			}
			else if(i==3)
			{
				srNo4 = selectedStudentList.get(3).getSrNo();
				fname4=selectedStudentList.get(3).getFname();
				className4=selectedStudentList.get(3).getClassName();
				fathersName4=selectedStudentList.get(3).getFathersName();
				currentAddress4=selectedStudentList.get(3).getCurrentAddress();
				fathersPhone4=String.valueOf(selectedStudentList.get(3).getFathersPhone());
				showImage4=true;
				studentImage4=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(3).getAddNumber(),conn);
				show4=true;
			}
			else if(i==4)
			{
				srNo5 = selectedStudentList.get(4).getSrNo();
				fname5=selectedStudentList.get(4).getFname();
				className5=selectedStudentList.get(4).getClassName();
				fathersName5=selectedStudentList.get(4).getFathersName();
				currentAddress5=selectedStudentList.get(4).getCurrentAddress();
				fathersPhone5=String.valueOf(selectedStudentList.get(4).getFathersPhone());
				showImage5=true;
				studentImage5=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(4).getAddNumber(),conn);
				show5=true;
			}
			else if(i==5)
			{
				srNo6 = selectedStudentList.get(5).getSrNo();
				fname6=selectedStudentList.get(5).getFname();
				className6=selectedStudentList.get(5).getClassName();
				fathersName6=selectedStudentList.get(5).getFathersName();
				currentAddress6=selectedStudentList.get(5).getCurrentAddress();
				fathersPhone6=String.valueOf(selectedStudentList.get(5).getFathersPhone());
				showImage6=true;
				studentImage6=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(5).getAddNumber(),conn);
				show6=true;
			}
			else if(i==6)
			{
				srNo7 = selectedStudentList.get(6).getSrNo();
				fname7=selectedStudentList.get(6).getFname();
				className7=selectedStudentList.get(6).getClassName();
				fathersName7=selectedStudentList.get(6).getFathersName();
				currentAddress7=selectedStudentList.get(6).getCurrentAddress();
				fathersPhone7=String.valueOf(selectedStudentList.get(6).getFathersPhone());
				showImage7=true;
				studentImage7=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(6).getAddNumber(),conn);
				show7=true;
			}
			else if(i==7)
			{
				srNo8 = selectedStudentList.get(7).getSrNo();
				fname8=selectedStudentList.get(7).getFname();
				className8=selectedStudentList.get(7).getClassName();
				fathersName8=selectedStudentList.get(7).getFathersName();
				currentAddress8=selectedStudentList.get(7).getCurrentAddress();
				fathersPhone8=String.valueOf(selectedStudentList.get(7).getFathersPhone());
				showImage8=true;
				studentImage8=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(7).getAddNumber(),conn);
				show8=true;
			}
			else if(i==8)
			{
				srNo9 = selectedStudentList.get(8).getSrNo();
				fname9=selectedStudentList.get(8).getFname();
				className9=selectedStudentList.get(8).getClassName();
				fathersName9=selectedStudentList.get(8).getFathersName();
				currentAddress9=selectedStudentList.get(8).getCurrentAddress();
				fathersPhone9=String.valueOf(selectedStudentList.get(8).getFathersPhone());
				showImage9=true;
				studentImage9=dd.studentImage(dd.schoolId(),"imageWithPath",selectedStudentList.get(8).getAddNumber(),conn);
				show9=true;
			}
		}
		
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	public void viewDetails() throws IOException
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", selectedStudent);

		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		cc.redirect("viewStudentDetails.xhtml");
	}

	public String getStudentImage() {
		return studentImage;
	}
	public void setStudentImage(String studentImage) {
		this.studentImage = studentImage;
	}
	public boolean isShowImage() {
		return showImage;
	}
	public void setShowImage(boolean showImage) {
		this.showImage = showImage;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public String getStudentImage1() {
		return studentImage1;
	}
	public void setStudentImage1(String studentImage1) {
		this.studentImage1 = studentImage1;
	}
	public String getStudentImage2() {
		return studentImage2;
	}
	public void setStudentImage2(String studentImage2) {
		this.studentImage2 = studentImage2;
	}
	public String getStudentImage3() {
		return studentImage3;
	}
	public void setStudentImage3(String studentImage3) {
		this.studentImage3 = studentImage3;
	}
	public String getStudentImage4() {
		return studentImage4;
	}
	public void setStudentImage4(String studentImage4) {
		this.studentImage4 = studentImage4;
	}
	public boolean isShowImage1() {
		return showImage1;
	}
	public void setShowImage1(boolean showImage1) {
		this.showImage1 = showImage1;
	}
	public boolean isShowImage2() {
		return showImage2;
	}
	public void setShowImage2(boolean showImage2) {
		this.showImage2 = showImage2;
	}
	public boolean isShowImage3() {
		return showImage3;
	}
	public void setShowImage3(boolean showImage3) {
		this.showImage3 = showImage3;
	}
	public boolean isShowImage4() {
		return showImage4;
	}
	public void setShowImage4(boolean showImage4) {
		this.showImage4 = showImage4;
	}
	public String getFname4() {
		return fname4;
	}
	public void setFname4(String fname4) {
		this.fname4 = fname4;
	}
	public String getClassName4() {
		return className4;
	}
	public void setClassName4(String className4) {
		this.className4 = className4;
	}
	public String getFathersName4() {
		return fathersName4;
	}
	public void setFathersName4(String fathersName4) {
		this.fathersName4 = fathersName4;
	}
	public String getCurrentAddress4() {
		return currentAddress4;
	}
	public void setCurrentAddress4(String currentAddress4) {
		this.currentAddress4 = currentAddress4;
	}
	public String getFathersPhone4() {
		return fathersPhone4;
	}
	public void setFathersPhone4(String fathersPhone4) {
		this.fathersPhone4 = fathersPhone4;
	}
	public String getFname3() {
		return fname3;
	}
	public void setFname3(String fname3) {
		this.fname3 = fname3;
	}
	public String getClassName3() {
		return className3;
	}
	public void setClassName3(String className3) {
		this.className3 = className3;
	}
	public String getFathersName3() {
		return fathersName3;
	}
	public void setFathersName3(String fathersName3) {
		this.fathersName3 = fathersName3;
	}
	public String getCurrentAddress3() {
		return currentAddress3;
	}
	public void setCurrentAddress3(String currentAddress3) {
		this.currentAddress3 = currentAddress3;
	}
	public String getFathersPhone3() {
		return fathersPhone3;
	}
	public void setFathersPhone3(String fathersPhone3) {
		this.fathersPhone3 = fathersPhone3;
	}
	public String getFname2() {
		return fname2;
	}
	public void setFname2(String fname2) {
		this.fname2 = fname2;
	}
	public String getClassName2() {
		return className2;
	}
	public void setClassName2(String className2) {
		this.className2 = className2;
	}
	public String getFathersName2() {
		return fathersName2;
	}
	public void setFathersName2(String fathersName2) {
		this.fathersName2 = fathersName2;
	}
	public String getCurrentAddress2() {
		return currentAddress2;
	}
	public void setCurrentAddress2(String currentAddress2) {
		this.currentAddress2 = currentAddress2;
	}
	public String getFathersPhone2() {
		return fathersPhone2;
	}
	public void setFathersPhone2(String fathersPhone2) {
		this.fathersPhone2 = fathersPhone2;
	}
	public String getFname1() {
		return fname1;
	}
	public void setFname1(String fname1) {
		this.fname1 = fname1;
	}
	public String getClassName1() {
		return className1;
	}
	public void setClassName1(String className1) {
		this.className1 = className1;
	}
	public String getFathersName1() {
		return fathersName1;
	}
	public void setFathersName1(String fathersName1) {
		this.fathersName1 = fathersName1;
	}
	public String getCurrentAddress1() {
		return currentAddress1;
	}
	public void setCurrentAddress1(String currentAddress1) {
		this.currentAddress1 = currentAddress1;
	}
	public String getFathersPhone1() {
		return fathersPhone1;
	}
	public void setFathersPhone1(String fathersPhone1) {
		this.fathersPhone1 = fathersPhone1;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public boolean isShow1() {
		return show1;
	}
	public void setShow1(boolean show1) {
		this.show1 = show1;
	}
	public boolean isShow2() {
		return show2;
	}
	public void setShow2(boolean show2) {
		this.show2 = show2;
	}
	public boolean isShow3() {
		return show3;
	}
	public void setShow3(boolean show3) {
		this.show3 = show3;
	}
	public boolean isShow4() {
		return show4;
	}
	public void setShow4(boolean show4) {
		this.show4 = show4;
	}
	public boolean isShowImage5() {
		return showImage5;
	}
	public void setShowImage5(boolean showImage5) {
		this.showImage5 = showImage5;
	}
	public boolean isShowImage6() {
		return showImage6;
	}
	public void setShowImage6(boolean showImage6) {
		this.showImage6 = showImage6;
	}
	public String getFname5() {
		return fname5;
	}
	public void setFname5(String fname5) {
		this.fname5 = fname5;
	}
	public String getClassName5() {
		return className5;
	}
	public void setClassName5(String className5) {
		this.className5 = className5;
	}
	public String getFathersName5() {
		return fathersName5;
	}
	public void setFathersName5(String fathersName5) {
		this.fathersName5 = fathersName5;
	}
	public String getCurrentAddress5() {
		return currentAddress5;
	}
	public void setCurrentAddress5(String currentAddress5) {
		this.currentAddress5 = currentAddress5;
	}
	public String getFathersPhone5() {
		return fathersPhone5;
	}
	public void setFathersPhone5(String fathersPhone5) {
		this.fathersPhone5 = fathersPhone5;
	}
	public String getFname6() {
		return fname6;
	}
	public void setFname6(String fname6) {
		this.fname6 = fname6;
	}
	public String getClassName6() {
		return className6;
	}
	public void setClassName6(String className6) {
		this.className6 = className6;
	}
	public String getFathersName6() {
		return fathersName6;
	}
	public void setFathersName6(String fathersName6) {
		this.fathersName6 = fathersName6;
	}
	public String getCurrentAddress6() {
		return currentAddress6;
	}
	public void setCurrentAddress6(String currentAddress6) {
		this.currentAddress6 = currentAddress6;
	}
	public String getFathersPhone6() {
		return fathersPhone6;
	}
	public void setFathersPhone6(String fathersPhone6) {
		this.fathersPhone6 = fathersPhone6;
	}
	public String getStudentImage5() {
		return studentImage5;
	}
	public void setStudentImage5(String studentImage5) {
		this.studentImage5 = studentImage5;
	}
	public String getStudentImage6() {
		return studentImage6;
	}
	public void setStudentImage6(String studentImage6) {
		this.studentImage6 = studentImage6;
	}
	public boolean isShow5() {
		return show5;
	}
	public void setShow5(boolean show5) {
		this.show5 = show5;
	}
	public boolean isShow6() {
		return show6;
	}
	public void setShow6(boolean show6) {
		this.show6 = show6;
	}
	public String getStudentImage7() {
		return studentImage7;
	}
	public void setStudentImage7(String studentImage7) {
		this.studentImage7 = studentImage7;
	}
	public String getStudentImage8() {
		return studentImage8;
	}
	public void setStudentImage8(String studentImage8) {
		this.studentImage8 = studentImage8;
	}
	public String getStudentImage9() {
		return studentImage9;
	}
	public void setStudentImage9(String studentImage9) {
		this.studentImage9 = studentImage9;
	}
	public boolean isShowImage7() {
		return showImage7;
	}
	public void setShowImage7(boolean showImage7) {
		this.showImage7 = showImage7;
	}
	public boolean isShowImage8() {
		return showImage8;
	}
	public void setShowImage8(boolean showImage8) {
		this.showImage8 = showImage8;
	}
	public boolean isShowImage9() {
		return showImage9;
	}
	public void setShowImage9(boolean showImage9) {
		this.showImage9 = showImage9;
	}
	public String getFname7() {
		return fname7;
	}
	public void setFname7(String fname7) {
		this.fname7 = fname7;
	}
	public String getClassName7() {
		return className7;
	}
	public void setClassName7(String className7) {
		this.className7 = className7;
	}
	public String getFathersName7() {
		return fathersName7;
	}
	public void setFathersName7(String fathersName7) {
		this.fathersName7 = fathersName7;
	}
	public String getCurrentAddress7() {
		return currentAddress7;
	}
	public void setCurrentAddress7(String currentAddress7) {
		this.currentAddress7 = currentAddress7;
	}
	public String getFathersPhone7() {
		return fathersPhone7;
	}
	public void setFathersPhone7(String fathersPhone7) {
		this.fathersPhone7 = fathersPhone7;
	}
	public String getFname8() {
		return fname8;
	}
	public void setFname8(String fname8) {
		this.fname8 = fname8;
	}
	public String getClassName8() {
		return className8;
	}
	public void setClassName8(String className8) {
		this.className8 = className8;
	}
	public String getFathersName8() {
		return fathersName8;
	}
	public void setFathersName8(String fathersName8) {
		this.fathersName8 = fathersName8;
	}
	public String getCurrentAddress8() {
		return currentAddress8;
	}
	public void setCurrentAddress8(String currentAddress8) {
		this.currentAddress8 = currentAddress8;
	}
	public String getFathersPhone8() {
		return fathersPhone8;
	}
	public void setFathersPhone8(String fathersPhone8) {
		this.fathersPhone8 = fathersPhone8;
	}
	public String getFname9() {
		return fname9;
	}
	public void setFname9(String fname9) {
		this.fname9 = fname9;
	}
	public String getClassName9() {
		return className9;
	}
	public void setClassName9(String className9) {
		this.className9 = className9;
	}
	public String getFathersName9() {
		return fathersName9;
	}
	public void setFathersName9(String fathersName9) {
		this.fathersName9 = fathersName9;
	}
	public String getCurrentAddress9() {
		return currentAddress9;
	}
	public void setCurrentAddress9(String currentAddress9) {
		this.currentAddress9 = currentAddress9;
	}
	public String getFathersPhone9() {
		return fathersPhone9;
	}
	public void setFathersPhone9(String fathersPhone9) {
		this.fathersPhone9 = fathersPhone9;
	}
	public boolean isShow7() {
		return show7;
	}
	public void setShow7(boolean show7) {
		this.show7 = show7;
	}
	public boolean isShow8() {
		return show8;
	}
	public void setShow8(boolean show8) {
		this.show8 = show8;
	}
	public boolean isShow9() {
		return show9;
	}
	public void setShow9(boolean show9) {
		this.show9 = show9;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getSrNo1() {
		return srNo1;
	}
	public void setSrNo1(String srNo1) {
		this.srNo1 = srNo1;
	}
	public String getSrNo2() {
		return srNo2;
	}
	public void setSrNo2(String srNo2) {
		this.srNo2 = srNo2;
	}
	public String getSrNo3() {
		return srNo3;
	}
	public void setSrNo3(String srNo3) {
		this.srNo3 = srNo3;
	}
	public String getSrNo4() {
		return srNo4;
	}
	public void setSrNo4(String srNo4) {
		this.srNo4 = srNo4;
	}
	public String getSrNo5() {
		return srNo5;
	}
	public void setSrNo5(String srNo5) {
		this.srNo5 = srNo5;
	}
	public String getSrNo6() {
		return srNo6;
	}
	public void setSrNo6(String srNo6) {
		this.srNo6 = srNo6;
	}
	public String getSrNo7() {
		return srNo7;
	}
	public void setSrNo7(String srNo7) {
		this.srNo7 = srNo7;
	}
	public String getSrNo8() {
		return srNo8;
	}
	public void setSrNo8(String srNo8) {
		this.srNo8 = srNo8;
	}
	public String getSrNo9() {
		return srNo9;
	}
	public void setSrNo9(String srNo9) {
		this.srNo9 = srNo9;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getBorder() {
		return border;
	}
	public void setBorder(String border) {
		this.border = border;
	}
	public String getExamTerm() {
		return examTerm;
	}
	public void setExamTerm(String examTerm) {
		this.examTerm = examTerm;
	}
	public boolean isShowforoak() {
		return showforoak;
	}
	public void setShowforoak(boolean showforoak) {
		this.showforoak = showforoak;
	}
	public boolean isNormal() {
		return normal;
	}
	public void setNormal(boolean normal) {
		this.normal = normal;
	}
	
	
	






}
