package schooldata;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
@ManagedBean(name="printRbscExperienceCertificate")
@ViewScoped
public class PrintRbscExperienceCertificate implements Serializable
{
	String name,fatherName,villageName,districtName,stateName,designation,deptNo;
	String fromDate,toDate,issuedDate;

	public PrintRbscExperienceCertificate()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		name=(String) ss.getAttribute("name");
		fatherName=(String) ss.getAttribute("fatherName");
		villageName=(String) ss.getAttribute("villageName");
		districtName=(String) ss.getAttribute("districtName");
		stateName=(String) ss.getAttribute("stateName");
		deptNo=(String) ss.getAttribute("deptNo");
		designation=(String) ss.getAttribute("designation");
		fromDate=sdf.format(ss.getAttribute("fromDate"));
		toDate=sdf.format(ss.getAttribute("toDate"));
		issuedDate=sdf.format(ss.getAttribute("issuedDate"));
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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}




}
