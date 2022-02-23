package schooldata;

import java.io.Serializable;
import java.sql.Connection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printAdmissionEnquiry")
@ViewScoped

public class PrintAdmissionEnquiryBean implements Serializable
{
   String sessionn,admDate,admissionClass,student,father,mother,gender,dob,address,mobno,motherMobNo,email,category,description;
   DatabaseMethods1 obj = new DatabaseMethods1();
   SchoolInfoList info = new SchoolInfoList();
   
	public PrintAdmissionEnquiryBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		info=new DatabaseMethods1().fullSchoolInfo(conn);

		HttpSession cs=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		
	      sessionn =  (String) cs.getAttribute("enqSession");
          admDate =  (String) cs.getAttribute("enqAdmDate");
          admissionClass = (String) cs.getAttribute("enqClass");
          student = (String) cs.getAttribute("enqStudent");
          father = (String) cs.getAttribute("enqFather");
          mother = (String) cs.getAttribute("enqMother");
          gender = (String) cs.getAttribute("enqGender");
          dob = (String) cs.getAttribute("enqDob");
          address = (String) cs.getAttribute("enqAddress");
          mobno = (String) cs.getAttribute("enqMob");
          motherMobNo = (String) cs.getAttribute("enqMotMob");
          email = (String) cs.getAttribute("enqEmail");
          category = (String) cs.getAttribute("enqCategory");
          description = (String) cs.getAttribute("enqDescription");
          
          

	      if(category.equalsIgnoreCase("-1"))
	      {
	    	 category = "Other"; 
	      }
	      else
	      {
		      category = obj.referernceManeById(category,conn);

	      }
          
          try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAdmDate() {
		return admDate;
	}

	public void setAdmDate(String admDate) {
		this.admDate = admDate;
	}

	public String getAdmissionClass() {
		return admissionClass;
	}

	public void setAdmissionClass(String admissionClass) {
		this.admissionClass = admissionClass;
	}

	public String getStudent() {
		return student;
	}

	public void setStudent(String student) {
		this.student = student;
	}

	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}

	public String getMother() {
		return mother;
	}

	public void setMother(String mother) {
		this.mother = mother;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobno() {
		return mobno;
	}

	public void setMobno(String mobno) {
		this.mobno = mobno;
	}

	public String getMotherMobNo() {
		return motherMobNo;
	}

	public void setMotherMobNo(String motherMobNo) {
		this.motherMobNo = motherMobNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSessionn() {
		return sessionn;
	}

	public void setSessionn(String sessionn) {
		this.sessionn = sessionn;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}




}

