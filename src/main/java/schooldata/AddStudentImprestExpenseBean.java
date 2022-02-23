package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;

@ManagedBean(name="addStudentImprestExpenseBean")
@ViewScoped
public class AddStudentImprestExpenseBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo>list=new ArrayList<>();
	boolean b;
	ArrayList<StudentExpeseLedgerList>expenceList;

	String name, username, schoolid, userType;
	String total,mobNo,pagename,searchBasis;
	StudentInfo selectedStudent;
	ArrayList<SelectItem> sectionList,classSection;
	//DatabaseMethods1 dd=new DatabaseMethods1();
	String selectedSection;
	String selectedCLassSection;
	Date addexpdate=new Date(),maxDate=new Date();
	public AddStudentImprestExpenseBean()
	{
		selectedCLassSection=selectedSection=mobNo="";
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classSection = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			classSection.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

			if(temp.size()>0)
			{
				classSection.addAll(temp);
			}
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classSection = obj.cordinatorClassList(empid, schoolid, conn);
		}

		expenceList=obj.allStudentExpenseCategoryList(conn);




		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allSection(selectedCLassSection,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
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
		list=new DatabaseMethods1().searchStudentList(query,conn);
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
				b=false;
			}
			else
			{
				b=true;
				for(StudentInfo lst : list)
				{

					Map<String, String> map = new HashMap<>();
					for (StudentExpeseLedgerList ls : expenceList) {

						map.put(ls.getCategoryId(), "0");

					}

					lst.setFeesMap(map);



				}
			}

			total=String.valueOf(list.size());
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}

	public void searchStudentByClassSection()
	{
		searchBasis="byclass";
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			list=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
			if(list.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
			}
			else
			{
				b=true;
				for(StudentInfo lst : list)
				{

					Map<String, String> map = new HashMap<>();
					new HashMap<String, String>();

					for (StudentExpeseLedgerList ls : expenceList) {
						map.put(ls.getCategoryId(), "0");

					}

					lst.setFeesMap(map);



				}
			}

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


	public String submitExpense()
	{
		Boolean a=false;
		int i=0;
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			for(StudentInfo lst : list)
			{
				Map<String, String> map = lst.getFeesMap();

				for (StudentExpeseLedgerList ls : expenceList)
				{
					String expenseAmount = map.get(ls.getCategoryId());
					if (expenseAmount.equals("") || expenseAmount == null) {
						expenseAmount = "0";
					}

					if(Integer.parseInt(expenseAmount)>0)
					{
						i=new DatabaseMethods1().addImprestAccountBal(lst.getAddNumber(),"cash","Debit",expenseAmount,ls.getCategoryId(),conn,addexpdate);
						a=true;
					}


				}




			}

			if(a==true)
			{
				if(i>0)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Expense Added","Expense Added"));

					return "addStudentImprestExpense.xhtml";
				}
				else
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Some Error Occur Please try Again","Some Error Occur Please try Again"));
					return "";
				}
			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"",""));
				return "";
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
		return "";

	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addexpdate);
		
		language = "Date-"+addDt;
		
		for(StudentInfo lst : list)
		{
			Map<String, String> map = lst.getFeesMap();

			for (StudentExpeseLedgerList ls : expenceList)
			{
				String expenseAmount = map.get(ls.getCategoryId());
				if (expenseAmount.equals("") || expenseAmount == null) {
					expenseAmount = "0";
				}

				if(Integer.parseInt(expenseAmount)>0)
				{
					value += "(Student-"+lst.getAddNumber()+"  Expense Amount-"+expenseAmount+" CategoryId-"+ls.getCategoryId()+") "; 
				}


			}
		}
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Student Imprest Expense","WEB",value,conn);
		return refNo;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getMobNo() {
		return mobNo;
	}

	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
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

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<StudentExpeseLedgerList> getExpenceList() {
		return expenceList;
	}

	public void setExpenceList(ArrayList<StudentExpeseLedgerList> expenceList) {
		this.expenceList = expenceList;
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

	public Date getAddexpdate() {
		return addexpdate;
	}

	public void setAddexpdate(Date addexpdate) {
		this.addexpdate = addexpdate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
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
