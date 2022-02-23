package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;
@ManagedBean(name="allStudentReport")
@ViewScoped
public class AllStudentReport implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo>list=new ArrayList<>(),selStdList;
	boolean b;
	String name;
	String total,mobNo,pagename,searchBasis;
	StudentInfo selectedStudent;
	ArrayList<SelectItem> sectionList,classSection;
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	//DatabaseMethods1 dd=new DatabaseMethods1();
	String selectedSection;
	String selectedCLassSection;
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void printAll()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("studentPrintList", list);
		ss.setAttribute("totalStudents", total);
		PrimeFaces.current().executeInitScript("window.open('printAllStudentList.xhtml')");
	}
	
	
	public void goToRegistrationForm()
	{
		if(selStdList.size()>0)
		{
			Connection conn=DataBaseConnection.javaConnection();
			//selectedStudent=new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),selectedStudent.getAddNumber(),conn);
			SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
			try {
				conn.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", selStdList);
			if(ls.getBranch_id().equalsIgnoreCase("69"))
			{
				PrimeFaces.current().executeInitScript("window.open('printAjmaniStdFormat.xhtml')");
			}
			else if(ls.getBranch_id().equalsIgnoreCase("2"))
			{
				PrimeFaces.current().executeInitScript("window.open('newERAStdRegPrintFormat.xhtml')");
			}
			else if(ls.getBranch_id().equalsIgnoreCase("4"))
			{
				PrimeFaces.current().executeInitScript("window.open('northwoodregPrintFormat.xhtml')");
			}
			else if(ls.getBranch_id().equalsIgnoreCase("99")||ls.getBranch_id().equalsIgnoreCase("101"))
			{
				PrimeFaces.current().executeInitScript("window.open('ssChildrenRegistrationFormat.xhtml')");
			}
			else
			{
				PrimeFaces.current().executeInitScript("window.open('declarationFormCbse.xhtml')");
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast 1 student"));
		}
	}
	
	public void searchData()
	{
		Connection conn = DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().allStudentListbyMobNumber(mobNo,conn);
		if(list.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			b=false;
		}
		else
			b=true;

		total=String.valueOf(list.size());
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public AllStudentReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			classSection=new DatabaseMethods1().allClass(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		list=new DatabaseMethods1().allStudentList(conn);
		if(list.isEmpty())
	    {
	    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
	    	b=false;
	    }
	    else
	    	b=true;

	    total=String.valueOf(list.size());

	    try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}*/

	@PostConstruct
	public void init()
	{
		 // System.out.println("constructor.......");
		selectedCLassSection=selectedSection=mobNo="";
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{
			classSection=obj.allClass(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		pagename = (String) ss.getAttribute("studentListPage");
		if(pagename.equals("dashboard"))
		{
			selectedCLassSection="-1";
			selectedSection="1";
			
			list=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedCLassSection,selectedSection);

			if(list.isEmpty())
			{
				//	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
			}
			else
			{
				b=true;
			}

		}
		else
		{
			selectedCLassSection="";
			selectedSection="";
			b=false;
		}


		total=String.valueOf(list.size());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//list=new DatabaseMethods1().searchStudentList(query,conn);
		list=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : list)
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

	public void deleteSelectedStudent()
	{
		if(selStdList.size()>0)
		{
			Connection conn=DataBaseConnection.javaConnection();
			DataBaseMethodsReports dbr=new DataBaseMethodsReports();
			DatabaseMethods1 obj=new DatabaseMethods1();
			
			int i = dbr.deleteMultipleStudents(selStdList,obj.schoolId(),conn);
			if(i>=1)
			{
				String refNo9;
				try {
					refNo9=addWorkLog6(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				selStdList=new ArrayList<>();
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				pagename = (String) ss.getAttribute("studentListPage");
				if(pagename.equals("dashboard"))
				{
					selectedCLassSection="-1";
					selectedSection="1";
					searchStudentByClassSection();

					//list=obj.allStudentList(conn);
					total=String.valueOf(list.size());
				}
				else
				{
					if(searchBasis.equals("byclass"))
					{
						searchStudentByClassSection();
					}
					else
					{
						try {
							FacesContext.getCurrentInstance().getExternalContext().redirect("allStudentList.xhtml");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Students Deleted Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something Went Wrong. Please Try Again."));
			}
			try {
				conn.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast 1 student to delete"));
		}

	}
	
	public String addWorkLog6(Connection conn)
	{
	    String value = "Students-";
		String language= "";
		
		for(StudentInfo si : selStdList)
		{	
		  value += "("+si.getAddNumber()+")";
		}
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Mutiple Student","WEB",value,conn);
		return refNo;
	}


	public void searchStudentByName()
	{
		searchBasis="byname";
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : list)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						list=new ArrayList<>();
						list.add(info);


					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			if(list.isEmpty())
			{
				//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
			}
			else
				b=true;
			total=String.valueOf(list.size());
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}

	public void viewDetails() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		//selectedStudent=new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),selectedStudent.getAddNumber(),conn);
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
		try {
			conn.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", selectedStudent);
		if(ls.getCountry().equalsIgnoreCase("UAE"))
		{
			ss.setAttribute("enq_id", selectedStudent.getEnqUAEId());
			ss.setAttribute("heading", "Student Details");
			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("printStudentDetailsAe.xhtml");
		}
		else
		{
			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("printStudentDetails.xhtml");
		}

	}
	public void deleteStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			//int i=obj.deleteprmanentStudent(selectedStudent.getAddNumber(),conn);
			int i=obj.deleteStudent(selectedStudent.getAddNumber(),"DELETE","ACTIVE",conn);
			if(i>=1)
			{
				String refNo4;
				try {
					refNo4=addWorkLog4(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				obj.decreaseStudentInAddSchool(conn);
				String refNo5;
				try {
					refNo5=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				obj.blockUser(selectedStudent.getAddNumber(),"DELETE",conn);
				String refNo6;
				try {
					refNo6=addWorkLog3(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				new DataBaseMeathodJson().deleteStudentFromCommGroup(selectedStudent.getAddNumber(), "student", new DatabaseMethods1().schoolId(), conn);
				//				obj.decreaseStudentInAddSchool(conn);
				//				obj.deleteprmanentStudentAttendance(selectedStudent.getAddNumber(), conn);
				//				obj.decresestudentClassSum(selectedStudent.getSectionid(),conn);
				//				obj.deleteUser(selectedStudent.getAddNumber(),conn);
				String refNo7;
				try {
					refNo7=addWorkLog5(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				pagename = (String) ss.getAttribute("studentListPage");
				if(pagename.equals("dashboard"))
				{
					selectedCLassSection="-1";
					selectedSection="1";
					searchStudentByClassSection();

					//list=obj.allStudentList(conn);
					total=String.valueOf(list.size());
				}
				else
				{
					if(searchBasis.equals("byclass"))
					{
						searchStudentByClassSection();
					}
					else
					{
						FacesContext.getCurrentInstance().getExternalContext().redirect("allStudentList.xhtml");
					}
				}

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Student Deleted Successfully"));
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
	public String addWorkLog4(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Student-"+selectedStudent.getAddNumber();
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Student","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog5(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Student-"+selectedStudent.getAddNumber();
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Student From Common Grp","WEB",value,conn);
		return refNo;
	}

	public void inactiveStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			int i=obj.deleteStudent(selectedStudent.getAddNumber(),"INACTIVE","ACTIVE",conn);
			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				obj.decreaseStudentInAddSchool(conn);
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				obj.blockUser(selectedStudent.getAddNumber(),"INACTIVE",conn);
				String refNo3;
				try {
					refNo3=addWorkLog3(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				new DataBaseMeathodJson().deleteStudentFromCommGroup(selectedStudent.getAddNumber(), "student", new DatabaseMethods1().schoolId(), conn);
				String refNo8;
				try {
					refNo8=addWorkLog5(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				pagename = (String) ss.getAttribute("studentListPage");
				if(pagename.equals("dashboard"))
				{
					selectedCLassSection="-1";
					selectedSection="1";
					searchStudentByClassSection();

					//list=obj.allStudentList(conn);
					total=String.valueOf(list.size());
				}
				else
				{
					if(searchBasis.equals("byclass"))
					{
						searchStudentByClassSection();
					}
					else
					{
						FacesContext.getCurrentInstance().getExternalContext().redirect("allStudentList.xhtml");
					}
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Student Inactivated Successfully"));
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

	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Student-"+selectedStudent.getAddNumber();
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Inactive Student","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Student-"+selectedStudent.getAddNumber();
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Decrease Student in School","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Student-"+selectedStudent.getAddNumber();
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Block Student","WEB",value,conn);
		return refNo;
	}
	
	
	public void searchStudentByClassSection()
	{
		searchBasis="byclass";
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			list=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedCLassSection,selectedSection);
			//list=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
			if(list.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
			}
			else
				b=true;

			total=String.valueOf(list.size());
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
	public void editNow() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedStudent=new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),selectedStudent.getAddNumber(),conn);
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
		try {
			conn.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", selectedStudent);

		if(ls.getCountry().equalsIgnoreCase("UAE"))
		{
			ss.setAttribute("enq_id", selectedStudent.getEnqUAEId());
			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("editOnlineAdmissionAE.xhtml");
		}
		else
		{
			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("editStudentDetails.xhtml");
		}

	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
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
	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<StudentInfo> getSelStdList() {
		return selStdList;
	}


	public void setSelStdList(ArrayList<StudentInfo> selStdList) {
		this.selStdList = selStdList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
