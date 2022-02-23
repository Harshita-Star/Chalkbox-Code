package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
@ManagedBean(name="experienceCertificateReport")
@ViewScoped
public class ExperienceCertificateReportBean implements Serializable
{
	String name,fatherName,villageName,districtName,stateName,designation,deptNo,selectType,address,schid,id;
	Date fromDate,toDate,issuedDate;
	DatabaseMethods1 obj = new DatabaseMethods1();
	ArrayList<EmployeeInfo> employeeList = new ArrayList<>();
	EmployeeInfo selected;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue;
	
	public ExperienceCertificateReportBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		schid=obj1.schoolId();
		sessionValue=obj1.selectedSessionDetails(schid, conn);
		employeeList=obj.selectAllExperienceCertificate(schid,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void print()
	{

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("deptNo", selected.getDeptNo());
		ss.setAttribute("name", selected.getName());
		ss.setAttribute("fatherName", selected.getFname());
		ss.setAttribute("villageName", selected.getVillageName());
		ss.setAttribute("districtName", selected.getDistrictName());
		ss.setAttribute("stateName", selected.getStateName());
		ss.setAttribute("designation", selected.getDesignation());
		ss.setAttribute("fromDate", selected.getFromDate());
		ss.setAttribute("toDate", selected.getToDate());
		ss.setAttribute("issuedDate", selected.getIssueDate());
		PrimeFaces.current().executeInitScript("window.open('printRbscExperienceCertificate.xhtml')");
	}
	public void delete()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=obj.deleteEmployeeTableByTableId(conn,selected.getId());
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employee registration is successfully deleted!"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void edit()
	{
		DataBaseConnection.javaConnection();
		name=selected.getName();fatherName=selected.getFname();villageName=selected.getVillageName();districtName=selected.getDistrictName();stateName=selected.getStateName();designation=selected.getDesignation();deptNo=selected.getDeptNo();
		fromDate=selected.getFromDate();toDate=selected.getToDate();issuedDate=selected.getIssueDate();
		schid=selected.getSchid();id=selected.getEmpId();
	}
	public void submit()
	{
		Connection conn=DataBaseConnection.javaConnection();

		int i=obj.updateAllCertificateDetails(conn,schid,id,name,fatherName,villageName,districtName,stateName,designation,deptNo,fromDate,toDate,issuedDate,selected.getId());
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Certificate details is updated successfully!"));
			employeeList=obj.selectAllExperienceCertificate(schid,conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}
	public EmployeeInfo getSelected() {
		return selected;
	}
	public void setSelected(EmployeeInfo selected) {
		this.selected = selected;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSchid() {
		return schid;
	}
	public void setSchid(String schid) {
		this.schid = schid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
}