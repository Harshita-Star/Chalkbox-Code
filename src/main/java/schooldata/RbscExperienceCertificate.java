package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;
@ManagedBean(name="rbscExperienceCertificate")
@ViewScoped
public class RbscExperienceCertificate implements Serializable
{
	String regex=RegexPattern.REGEX;
	String name,fatherName,villageName,districtName,stateName,designation,deptNo,selectType,address,schid,id;
	Date fromDate,toDate,issuedDate;
	EmployeeInfo selected=new EmployeeInfo();
	boolean show,showAddress,search,employee;
	ArrayList<EmployeeInfo> employeeList;
	DatabaseMethods1 obj = new DatabaseMethods1();
	public RbscExperienceCertificate()
	{
		fromDate=toDate=issuedDate=new Date();
	}
	public void select()
	{
		if(selectType.equalsIgnoreCase("Existing"))
		{
			search=true;
			showAddress=true;
			show=false;
		}
		else
		{
			show=true;
			search=false;
			showAddress=false;
			name="";fatherName="";villageName="";districtName="";stateName="";designation="";deptNo="";
			fromDate=new Date();toDate=new Date();issuedDate=new Date();
		}
	}
	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		employeeList=new DatabaseMethods1().searchEmployeebyName(query,conn);
		List<String> studentListt=new ArrayList<>();

		for(EmployeeInfo info : employeeList)
		{
			studentListt.add(info.getFname()+" / "+info.getLname()+ "-"+info.getId());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public void searchEmployeeByName()
	{
		int index=name.lastIndexOf("-")+1;
		id=name.substring(index);
		if(index!=0)
		{
			for(EmployeeInfo info : employeeList)
			{
				if(String.valueOf(info.getId()).equals(id))
				{
					try
					{
						employeeList=new ArrayList<>();
						employeeList.add(info);
						selected=info;
						show=true;
						showAddress=true;
						name=selected.getFname();fatherName=selected.getLname();villageName="";districtName="";stateName="";designation=selected.getSubCateg();deptNo="";
						fromDate=selected.getStartingDate();toDate=new Date();issuedDate=new Date();address=selected.getAddress();
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
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select employee name from Autocomplete list", "Validation error"));

		}

	}

	public String submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=obj.insertAllEmployeeEntries(conn,schid,id,name,fatherName,villageName,districtName,stateName,designation,deptNo,fromDate,toDate,issuedDate);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employee is registered successfully!"));
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("deptNo", deptNo);
			ss.setAttribute("name", name.toUpperCase());
			ss.setAttribute("fatherName", fatherName.toUpperCase());
			ss.setAttribute("villageName", villageName.toUpperCase());
			ss.setAttribute("districtName", districtName.toUpperCase());
			ss.setAttribute("stateName", stateName.toUpperCase());
			ss.setAttribute("designation", designation.toUpperCase());
			ss.setAttribute("fromDate", fromDate);
			ss.setAttribute("toDate", toDate);
			ss.setAttribute("issuedDate", issuedDate);
			PrimeFaces.current().executeInitScript("window.open('printRbscExperienceCertificate.xhtml')");
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "rbscExperienceCertificate.xhtml";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}

	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getVillageName() {
		return villageName;
	}

	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}
	public String getDeptNo() {
		return deptNo;
	}
	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}
	public String getSelectType() {
		return selectType;
	}
	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	public EmployeeInfo getSelected() {
		return selected;
	}

	public void setSelected(EmployeeInfo selected) {
		this.selected = selected;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isShowAddress() {
		return showAddress;
	}

	public void setShowAddress(boolean showAddress) {
		this.showAddress = showAddress;
	}
	public boolean isSearch() {
		return search;
	}
	public void setSearch(boolean search) {
		this.search = search;
	}
	public boolean isEmployee() {
		return employee;
	}
	public void setEmployee(boolean employee) {
		this.employee = employee;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}



}
