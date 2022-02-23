package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.StudentInfo;

@ManagedBean(name="feeRecordReg")
@ViewScoped
public class FeeRecordRegisterBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList,sectionList,quarterList;
	ArrayList<StudentInfo> studentList;
	ArrayList<FeeInfo> classFeeList;
	String selectedClass,selectedSection,session,selectedQuarter,quarterName,className;
	boolean showData;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId;
	DataBaseMethodsReports objReport = new DataBaseMethodsReports();


	public FeeRecordRegisterBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId, conn);

		classList=obj.allClass(conn);

		quarterList=new ArrayList<>();
		SelectItem ll=new SelectItem();
		ll.setLabel("Quarter 1 (APR-JUN)");
		ll.setValue("1");
		quarterList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Quarter 2 (JUL-SEP)");
		ll.setValue("2");
		quarterList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Quarter 3 (OCT-DEC)");
		ll.setValue("3");
		quarterList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Quarter 4 (JAN-MAR)");
		ll.setValue("4");
		quarterList.add(ll);

		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public String quarterName()
	{
		if(selectedQuarter.equals("1"))
			return "Quarter 1 (APR-JUN)";
		if(selectedQuarter.equals("2"))
			return "Quarter 2 (JUL-SEP)";
		if(selectedQuarter.equals("3"))
			return "Quarter 3 (OCT-DEC)";
		if(selectedQuarter.equals("4"))
			return "Quarter 4 (JAN-MAR)";
		return "";
	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();


		sectionList=obj.allSection(selectedClass,conn);
		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


	public Date makeStartDate()
	{
		Date startDate=new Date();
		Date currentDate=new Date();
		int month=currentDate.getMonth()+1;
		int year=currentDate.getYear();
		startDate.setDate(1);
		if(month>4)
		{
			if(selectedQuarter.equals("1"))
			{
				startDate.setMonth(3);
			}
			else if(selectedQuarter.equals("2") )
			{
				startDate.setMonth(6);
			}
			else if(selectedQuarter.equals("3"))
			{
				startDate.setMonth(9);
			}
			else if(selectedQuarter.equals("4"))
			{
				startDate.setMonth(0);
				startDate.setYear(year+1);
			}
		}
		else
		{
			if(selectedQuarter.equals("1"))
			{
				startDate.setMonth(3);
				startDate.setYear(year-1);
			}
			else if(selectedQuarter.equals("2") )
			{
				startDate.setMonth(6);
				startDate.setYear(year-1);
			}
			else if(selectedQuarter.equals("3"))
			{
				startDate.setMonth(9);
				startDate.setYear(year-1);
			}
			else if(selectedQuarter.equals("4"))
			{
				startDate.setMonth(0);
			}
		}
		return startDate;
	}

	public Date makeEndDate()
	{
		Connection conn=DataBaseConnection.javaConnection();

		Date endDate=new Date();
		Date currentDate=new Date();
		int month=currentDate.getMonth()+1;
		int year=currentDate.getYear();

		if(month>4)
		{
			if(selectedQuarter.equals("1"))
			{
				endDate.setMonth(5);
				endDate.setDate(objReport.dayInMonth(6, year+1900));
			}
			else if(selectedQuarter.equals("2") )
			{
				endDate.setMonth(8);
				endDate.setDate(objReport.dayInMonth(9, year+1900));
			}
			else if(selectedQuarter.equals("3"))
			{
				endDate.setMonth(11);
				endDate.setDate(objReport.dayInMonth(12, year+1900));
			}
			else if(selectedQuarter.equals("4"))
			{
				endDate.setMonth(2);
				endDate.setYear(year+1);
				endDate.setDate(objReport.dayInMonth(3, year+1901));
			}
		}
		else
		{
			if(selectedQuarter.equals("1"))
			{
				endDate.setMonth(5);
				endDate.setYear(year-1);
				endDate.setDate(objReport.dayInMonth(6, year-1+1900));
			}
			else if(selectedQuarter.equals("2") )
			{
				endDate.setMonth(8);
				endDate.setYear(year-1);
				endDate.setDate(objReport.dayInMonth(9, year-1+1900));
			}
			else if(selectedQuarter.equals("3"))
			{
				endDate.setMonth(11);
				endDate.setYear(year-1);
				endDate.setDate(objReport.dayInMonth(12, year-1+1900));
			}
			else if(selectedQuarter.equals("4"))
			{
				endDate.setMonth(2);
				endDate.setDate(objReport.dayInMonth(3, year+1900));
			}
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return endDate;
	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		studentList=new ArrayList<>();
		if(selectedSection.equalsIgnoreCase("all"))
		{
			for(SelectItem section:sectionList)
			{
				studentList.addAll(obj.searchStudentListByClassSection(selectedClass,(String) section.getValue(), conn));
			}
			className=studentList.get(0).getClassName();
		}
		else
		{
			studentList=obj.searchStudentListByClassSection(selectedClass,selectedSection, conn);
			className=studentList.get(0).getClassName()+" - "+studentList.get(0).getSectionName();
		}

		if(studentList.size()>0)
		{
			showData=true;
			quarterName=quarterName();

			Date startDate=makeStartDate();
			Date endDate=makeEndDate();
			classFeeList=obj.feeListForFeeRecordRegister(selectedClass, conn);
			objReport.feeRecordRegister(startDate,endDate,studentList,classFeeList,conn);
			classFeeList=obj.feeListForFeeRecordRegisterHeader(selectedClass, conn);
		}
		try
		{
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void print()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("quarterName", quarterName);
		ss.setAttribute("className",className);
		ss.setAttribute("studentList", studentList);
		ss.setAttribute("classFeeList", classFeeList);
		PrimeFaces.current().executeInitScript("window.open('printFeeRecordRegister.xhtml')");
	}

	public ArrayList<SelectItem> getClassList()
	{
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getQuarterList() {
		return quarterList;
	}

	public void setQuarterList(ArrayList<SelectItem> quarterList) {
		this.quarterList = quarterList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<FeeInfo> getClassFeeList() {
		return classFeeList;
	}

	public void setClassFeeList(ArrayList<FeeInfo> classFeeList) {
		this.classFeeList = classFeeList;
	}

	public String getSelectedQuarter() {
		return selectedQuarter;
	}

	public void setSelectedQuarter(String selectedQuarter) {
		this.selectedQuarter = selectedQuarter;
	}

	public boolean isShowData() {
		return showData;
	}

	public void setShowData(boolean showData) {
		this.showData = showData;
	}

	public String getQuarterName() {
		return quarterName;
	}

	public void setQuarterName(String quarterName) {
		this.quarterName = quarterName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
