package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

@ManagedBean(name="viewEmployee")

public class ViewEmployeeBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> categoryList;
	boolean showSpouse;
	int id,salary;
	double leave,daamt,hraamt;
	String joiningDateStr,dobStr,fname,lname,gender,category,qualification,previousImage,email,marital,address,comment,spouseName,mobile2,dlNo;
	Date joiningDate,dob,leavingDate;
	long mobile;
	String empImage,aadharImage,panNo,aadhaarNo,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,expImage,otherImage,dlImage,subCateg,employeeUniqueId;
	String bankAccountNo,bankName,bankBranch,ifscCode,epfUanNo,esiNo,platform;
	
	EmployeeInfo list;
	public void showSpouseMethod()
	{
		if(marital.equals("Married"))
		{
			showSpouse=true;
		}
		else
		{
			showSpouse=false;
			spouseName=null;
		}
	}

	public ViewEmployeeBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			 list=(EmployeeInfo) ss.getAttribute("selectedEmployee");
            
			id=list.getId();
			previousImage=list.getEmpImageTemp();
			aadharImage=list.getAadharImage();
			panImage=list.getPanImage();
			voteridImage=list.getVoteridImage();
			ugImage=list.getUgImage();
			pgImage=list.getPgImage();
			bedImage=list.getBedImage();
			policeImage=list.getPoliceImage();
			expImage=list.getExpImage();
			otherImage=list.getOtherImage();
			dlImage=list.getDlImage();
			dlNo=list.getDlNo();
			leave=list.getLeave();
			daamt = list.getDaamt();
			hraamt = list.getHraamt();
			subCateg=list.getSubCateg();
			employeeUniqueId = list.getEmployeeUniqueId();
			panNo = list.getPanNo();
			aadhaarNo = list.getAadhaarNo();
			bankAccountNo = list.getBankAccountNo();
			bankName = list.getBankName();
			bankBranch = list.getBankBranch();
			ifscCode = list.getIfscCode();
			epfUanNo = list.getEpfUanNo();
			esiNo = list.getEsiNo();
			platform = list.getPlatform().equalsIgnoreCase("both") ? "Both Web And App" : list.getPlatform().toUpperCase();

			/*SchoolInfoList ls=obj.fullSchoolInfo(conn);
			if(ls.getProjecttype().equals("online"))
			{
				String folderName=ls.getDownloadpath();
				previousImage=folderName+previousImage;
				aadharImage=folderName+aadharImage;
				panImage=folderName+panImage;
				voteridImage=folderName+voteridImage;
				ugImage=folderName+ugImage;
				pgImage=folderName+pgImage;
				bedImage=folderName+bedImage;
				policeImage=folderName+policeImage;
				expImage=folderName+expImage;
				otherImage=folderName+otherImage;
				dlImage=folderName+dlImage;
			}*/

			fname=list.getFname();
			lname=list.getLname();
			dobStr=list.getDobStr();
			gender=list.getGender();
			category=list.getCategory();
			salary=list.getSalary();
			qualification=list.getQualification();
			joiningDateStr=list.getJoiningDateStr();
			mobile=list.getMobile();
			email=list.getEmail();
			marital=list.getMarital();
			address=list.getAddress();
			comment=list.getComment();
			leavingDate=list.getLeavingDate();
			mobile2=list.getMobile2();

			if(marital==null || marital.equals(""))
			{
				showSpouse=false;
				spouseName = "";
			}
			else
			{
				if(marital.equals("Married"))
				{
					showSpouse=true;
					spouseName=list.getSpouseName();
				}
				else
				{
					showSpouse=false;
				}
			}

			categoryList=obj.allEmployeeCategory(conn);

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

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public boolean isShowSpouse() {
		return showSpouse;
	}

	public void setShowSpouse(boolean showSpouse) {
		this.showSpouse = showSpouse;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getPreviousImage() {
		return previousImage;
	}

	public void setPreviousImage(String previousImage) {
		this.previousImage = previousImage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMarital() {
		return marital;
	}

	public void setMarital(String marital) {
		this.marital = marital;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public Date getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getLeavingDate() {
		return leavingDate;
	}

	public void setLeavingDate(Date leavingDate) {
		this.leavingDate = leavingDate;
	}

	public long getMobile() {
		return mobile;
	}

	public void setMobile(long mobile) {
		this.mobile = mobile;
	}

	public String getEmpImage() {
		return empImage;
	}

	public void setEmpImage(String empImage) {
		this.empImage = empImage;
	}

	public String getAadharImage() {
		return aadharImage;
	}

	public void setAadharImage(String aadharImage) {
		this.aadharImage = aadharImage;
	}

	public String getPanImage() {
		return panImage;
	}

	public void setPanImage(String panImage) {
		this.panImage = panImage;
	}

	public String getVoteridImage() {
		return voteridImage;
	}

	public void setVoteridImage(String voteridImage) {
		this.voteridImage = voteridImage;
	}

	public String getUgImage() {
		return ugImage;
	}

	public void setUgImage(String ugImage) {
		this.ugImage = ugImage;
	}

	public String getPgImage() {
		return pgImage;
	}

	public void setPgImage(String pgImage) {
		this.pgImage = pgImage;
	}

	public String getBedImage() {
		return bedImage;
	}

	public void setBedImage(String bedImage) {
		this.bedImage = bedImage;
	}

	public String getPoliceImage() {
		return policeImage;
	}

	public void setPoliceImage(String policeImage) {
		this.policeImage = policeImage;
	}

	public String getExpImage() {
		return expImage;
	}

	public void setExpImage(String expImage) {
		this.expImage = expImage;
	}

	public String getOtherImage() {
		return otherImage;
	}

	public void setOtherImage(String otherImage) {
		this.otherImage = otherImage;
	}

	public String getDlNo() {
		return dlNo;
	}

	public void setDlNo(String dlNo) {
		this.dlNo = dlNo;
	}

	public String getDlImage() {
		return dlImage;
	}

	public void setDlImage(String dlImage) {
		this.dlImage = dlImage;
	}

	public String getJoiningDateStr() {
		return joiningDateStr;
	}

	public void setJoiningDateStr(String joiningDateStr) {
		this.joiningDateStr = joiningDateStr;
	}

	public String getDobStr() {
		return dobStr;
	}

	public void setDobStr(String dobStr) {
		this.dobStr = dobStr;
	}

	public double getLeave() {
		return leave;
	}

	public void setLeave(double leave) {
		this.leave = leave;
	}

	public String getSubCateg() {
		return subCateg;
	}

	public void setSubCateg(String subCateg) {
		this.subCateg = subCateg;
	}

	public double getDaamt() {
		return daamt;
	}

	public void setDaamt(double daamt) {
		this.daamt = daamt;
	}

	public double getHraamt() {
		return hraamt;
	}

	public void setHraamt(double hraamt) {
		this.hraamt = hraamt;
	}

	public EmployeeInfo getList() {
		return list;
	}

	public void setList(EmployeeInfo list) {
		this.list = list;
	}

	public String getEmployeeUniqueId() {
		return employeeUniqueId;
	}

	public void setEmployeeUniqueId(String employeeUniqueId) {
		this.employeeUniqueId = employeeUniqueId;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getEpfUanNo() {
		return epfUanNo;
	}

	public void setEpfUanNo(String epfUanNo) {
		this.epfUanNo = epfUanNo;
	}

	public String getEsiNo() {
		return esiNo;
	}

	public void setEsiNo(String esiNo) {
		this.esiNo = esiNo;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	
}
