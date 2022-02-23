package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
@ManagedBean(name="scholarshipPrintBeantest")
@ViewScoped
public class ScholarshipPrint implements Serializable
{
	String regNo,schid,sName,fName,mName,className,dobStr,category,contactNo,image;
	Date dob;
	StudentInfo info=new StudentInfo();
	public ScholarshipPrint()
	{
		Connection conn=DataBaseConnection.javaConnection();
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();
		regNo =params.get("regNo");
		schid =params.get("Schoolid");
		info=new DatabaseMethods1().studentDetailslistByAddNoForScholarship(regNo, schid, conn);
		sName=info.getFname();
		fName=info.getFathersName();
		mName=info.getMotherName();
		className=info.getClassName();
		dobStr=info.getDobString();
		category=info.getCategory();
		contactNo=info.getPhone();
		image=info.getStudent_image();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getSchid() {
		return schid;
	}
	public void setSchid(String schid) {
		this.schid = schid;
	}
	public String getsName() {
		return sName;
	}
	public void setsName(String sName) {
		this.sName = sName;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getDobStr() {
		return dobStr;
	}
	public void setDobStr(String dobStr) {
		this.dobStr = dobStr;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

}
