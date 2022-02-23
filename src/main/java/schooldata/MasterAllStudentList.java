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

@ManagedBean(name="masterAllStudentReport")
@ViewScoped

public class MasterAllStudentList implements Serializable
{
	ArrayList<StudentInfo>list=new ArrayList<>();
	boolean b,showClass;
	String name,schid,branches;
	String total,mobNo,pagename,searchBasis;
	StudentInfo selectedStudent;
	ArrayList<SelectItem> sectionList,classSection,branchList;
	//DatabaseMethods1 dd=new DatabaseMethods1();
	String selectedSection;
	String selectedCLassSection;

	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="";

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(schid,selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void branchWiseWork()
	{
		selectedCLassSection=selectedSection="-1";
		sectionList = new ArrayList<>();

		if(schid.equals("-1"))
		{
			showClass = false;
			schname="B.L.M Academy";
			finalAddress="Haldwani";
			affiliationNo="";
			phoneno="";
		}
		else
		{
			showClass = true;
			schoolInfo(schid);

			Connection conn=DataBaseConnection.javaConnection();
			classSection=new DatabaseMethods1().allClass(schid,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		////// // System.out.println("dasass");
		schid="-1";
		mobNo="";
		selectedCLassSection=selectedSection="-1";
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		/*
		try
		{
			classSection=obj.allClass(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		 */

		schname="B.L.M Academy";
		finalAddress="Haldwani";
		affiliationNo="";
		phoneno="";

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		pagename = (String) ss.getAttribute("studentListPage");
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
				}
			}
		}

		if(pagename.equals("dashboard"))
		{
			list=obj.allStudentListSchid(branches,conn);
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
		list=new DatabaseMethods1().searchStudentList(branches,query,conn);
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
						schoolInfo(info.getSchid());
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
		selectedStudent=new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),selectedStudent.getAddNumber(),conn);
		try {
			conn.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", selectedStudent);

		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		cc.redirect("printStudentDetails.xhtml");
	}
	public void deleteStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			int i=obj.deleteprmanentStudent(selectedStudent.getAddNumber(),conn);
			if(i>=1)
			{
				obj.decreaseStudentInAddSchool(conn);
				obj.deleteprmanentStudentAttendance(selectedStudent.getAddNumber(), conn);
				obj.decresestudentClassSum(selectedStudent.getSectionid(),conn);
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				pagename = (String) ss.getAttribute("studentListPage");
				if(pagename.equals("dashboard"))
				{
					list=obj.allStudentList(conn);
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

	public void inactiveStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			int i=obj.deleteStudent(selectedStudent.getAddNumber(),"INACTIVE","ACTIVE",conn);
			if(i>=1)
			{
				obj.decreaseStudentInAddSchool(conn);
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				pagename = (String) ss.getAttribute("studentListPage");
				if(pagename.equals("dashboard"))
				{
					list=obj.allStudentList(conn);
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

	public void searchStudentByClassSection()
	{
		searchBasis="byclass";
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			list = new ArrayList<>();

			if(schid.equals("-1"))
			{
				/*ArrayList<StudentInfo> tempList = new ArrayList<>();
				for(SelectItem in : branchList)
				{
					tempList=new DatabaseMethods1().searchStudentListByClassSectionSchidWise(String.valueOf(in.getValue()),selectedCLassSection,selectedSection,conn);
					list.addAll(tempList);
				}*/
				list=new DatabaseMethods1().allStudentListSchid(branches,conn);

				//list=new DatabaseMethods1().searchStudentListByClassSectionSchidWise(branches,selectedCLassSection,selectedSection,conn);

			}
			else
			{
				list=new DatabaseMethods1().searchStudentListByClassSectionSchidWise(schid,selectedCLassSection,selectedSection,conn);

			}
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
		try {
			conn.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", selectedStudent);
		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		cc.redirect("editStudentDetails.xhtml");

	}

	public void schoolInfo(String schid)
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(schid,conn);
		schname=info.getSchoolName();
		address1=info.getAdd1();
		address2=info.getAdd2();
		address3=info.getAdd3();
		address4=info.getAdd4();
		phoneno=info.getPhoneNo();
		mobileno=info.getMobileNo();
		website=info.getWebsite();
		type=info.getClient_type();
		if(type.equalsIgnoreCase("institute"))
		{
			type="Institute";
		}
		else if(type.equalsIgnoreCase("school"))
		{
			type="School";
		}


		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		logo=savePath+info.getImagePath();
		headerImagePath=savePath+info.getMarksheetHeader();
		regno=info.getRegNo();
		finalAddress=address1;

		if(address2==null || address2.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address2;
		}

		if(address3==null || address3.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address3;
		}

		affiliationNo=address4;



		/*name="Dynamic Public School";
		address1="Alwar 301001";
		address2="Govt. Recognized";
		//address3="Run By : Smart Education & Social welfare Society (Reg. No. 143 Alwar 2009-10)";
		address4="IMG GLOBAL INFOTECH,Jai Complex, Alwar";*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
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

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public String getPagename() {
		return pagename;
	}

	public void setPagename(String pagename) {
		this.pagename = pagename;
	}

	public String getSearchBasis() {
		return searchBasis;
	}

	public void setSearchBasis(String searchBasis) {
		this.searchBasis = searchBasis;
	}

	public String getSchname() {
		return schname;
	}

	public void setSchname(String schname) {
		this.schname = schname;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getFinalAddress() {
		return finalAddress;
	}

	public void setFinalAddress(String finalAddress) {
		this.finalAddress = finalAddress;
	}

	public String getAffiliationNo() {
		return affiliationNo;
	}

	public void setAffiliationNo(String affiliationNo) {
		this.affiliationNo = affiliationNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}

	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

}
