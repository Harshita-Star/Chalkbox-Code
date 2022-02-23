package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import account_module.DataBase;


@ManagedBean(name="employeeAddmissionBean")
@ViewScoped
public class EmployeeAddmissionBean  implements Serializable {
	private static final long serialVersionUID = 1L;
	String name,fname,gender,email,dlNo,marital,address,comment,employeeCategory,category,qualification,mobile2,empusername,spouseName;
	UploadedFile empImage,aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,expImage,otherImage,dlImage;
	long mobile;
	int salary;
	double leave,daamt,hraamt;
	boolean showSpouse;
	Date leavingDate,dob,joiningDate,annDate;
	ArrayList<SelectItem> categoryList;
	String password,subCateg,balMsg,mobDigit,employeeUniqueId,aadhaarNo,panNo;
	String bankAccountNo,bankName,bankBranch,ifscCode,epfUanNo,esiNo,platform;
	DatabaseMethods1 obj= new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();

	public EmployeeAddmissionBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ls=new DatabaseMethods1().fullSchoolInfo(conn);
		gender="Male";
		dob=new Date();
		joiningDate=new Date();
		marital="Unmarried";

		if(ls.getCountry().equalsIgnoreCase("UAE"))
		{
			mobDigit="9";
		}
		else
		{
			mobDigit="10";
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String conductorRegister() {
		Connection conn=DataBaseConnection.javaConnection();
		boolean checkforconductgor=obj.chckconductorcat(conn);
		boolean checkformasteradmin=obj.checkformasteradmincon(conn);
		if(checkformasteradmin==false) {
			obj.createconinmasteradmin(conn);
		}
		if(checkforconductgor==false)
		{
			String res=obj.createconductorcat(conn);

			if(res.equals("yes")) {
				String concat=obj.concat(conn);
				/*boolean check=DBM.checkLoginDuplicacy(empusername,conn);
		if(check==false)
		{*/
				try
				{
					int i=obj.employeeAddmissionHere(salary, name, joiningDate, dob, fname, qualification,
							marital, gender, email, comment,concat, address, mobile, mobile2, /*leavingDate,*/
							empImage, spouseName, empusername, aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,
							expImage,otherImage,conn,dlNo,dlImage,leave,"Conductor",annDate,daamt,hraamt,
							"","","","","","","","","","none");
					if(i>=1)
					{
						String accountName = name+"-"+"Conductor"+"-"+i;
						new DataBase().addAccount("EXPENSES->Advance Salary",accountName,address,"","","",obj.schoolId(),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),conn);

						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Conductor registered successfully"));
						fname=null;qualification=null;marital=null;gender=null;email=null;comment=null;category=null;address=null;
						mobile=0;mobile2=null;empImage=null;leavingDate=null;spouseName=null;salary=0;name=null;joiningDate=null;dob=null;
						password=subCateg="";
						FacesContext.getCurrentInstance().getExternalContext().redirect("addBus.xhtml");
						return "addBus.xhtml";
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
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

				/*}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username : "+empusername+" ,already exist please "
																				+ "try something different! "));

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}*/
			}
			else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured "));
			}

		}
		else {
			String concat=obj.concat(conn);
			/*boolean check=DBM.checkLoginDuplicacy(empusername,conn);
			if(check==false)
			{*/
			try
			{
				int i=obj.employeeAddmissionHere(salary, name, joiningDate, dob, fname, qualification,
						marital, gender, email, comment,concat, address, mobile, mobile2, /*leavingDate,*/
						empImage, spouseName, empusername, aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,
						expImage,otherImage,conn,dlNo,dlImage,leave,"Conductor",annDate,daamt,hraamt,
						"","","","","","","","","","none");
				if(i>=1)
				{
					String accountName = name+"-"+"Conductor"+""+i;
					new DataBase().addAccount("EXPENSES->Advance Salary",accountName,address,"","","",obj.schoolId(),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),conn);

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Conductor registered successfully"));
					fname=null;qualification=null;marital=null;gender=null;email=null;comment=null;category=null;address=null;
					mobile=0;mobile2=null;empImage=null;leavingDate=null;spouseName=null;salary=0;name=null;joiningDate=null;dob=null;
					password="";
					FacesContext.getCurrentInstance().getExternalContext().redirect("addBus.xhtml");
					return "addBus.xhtml";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
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

			/*}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username : "+empusername+" ,already exist please "
																					+ "try something different! "));

				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}*/
		}
		return "";

	}



	public String driverRegister() {

		Connection conn=DataBaseConnection.javaConnection();
		boolean checkfordriver=obj.chckdrivercat(conn);
		boolean checkformasteradminfordriver=obj.checkformasteradmindri(conn);
		if(checkformasteradminfordriver==false) {
			obj.createdriinmasteradmin(conn);
		}

		if(checkfordriver==false)
		{
			String res=obj.createdrivercat(conn);

			if(res.equals("yes")) {
				String dricat=obj.drivercat(conn);
				/*boolean check=DBM.checkLoginDuplicacy(empusername,conn);
		if(check==false)
		{*/
				try
				{
					
					int i=obj.employeeAddmissionHere(salary, name, joiningDate, dob, fname, qualification,
							marital, gender, email, comment,dricat, address, mobile, mobile2, /*leavingDate,*/
							empImage, spouseName, empusername, aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,
							expImage,otherImage,conn,dlNo,dlImage,leave,"Driver",annDate,daamt,hraamt,
							"","","","","","","","","","none");
					if(i>=1)
					{
						String accountName = name+"-"+"Driver"+"-"+i;
						new DataBase().addAccount("EXPENSES->Advance Salary",accountName,address,"","","",obj.schoolId(),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),conn);

						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Driver registered successfully"));
						fname=null;qualification=null;marital=null;gender=null;email=null;comment=null;category=null;address=null;
						mobile=0;mobile2=null;empImage=null;leavingDate=null;spouseName=null;salary=0;name=null;joiningDate=null;dob=null;
						password="";
						FacesContext.getCurrentInstance().getExternalContext().redirect("addBus.xhtml");
						return "addBus.xhtml";
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
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

				/*		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username : "+empusername+" ,already exist please "
																				+ "try something different! "));

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
				 */		}
			else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured "));
			}

		}
		else {
			String dricat=obj.drivercat(conn);
			/*boolean check=DBM.checkLoginDuplicacy(empusername,conn);
			if(check==false)
			{*/
			try
			{
				int i=obj.employeeAddmissionHere(salary, name, joiningDate, dob, fname, qualification,
						marital, gender, email, comment,dricat, address, mobile, mobile2, /*leavingDate,*/
						empImage, spouseName, empusername, aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,
						expImage,otherImage,conn,dlNo,dlImage,leave,"Driver",annDate,daamt,hraamt,
						"","","","","","","","","","none");
				if(i>=1)
				{
					String accountName = name+"-"+"Driver"+"-"+i;
					new DataBase().addAccount("EXPENSES->Advance Salary",accountName,address,"","","",obj.schoolId(),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),conn);
					/*String cat=DBM.employeeCategoryById(dricat,conn);
	                    obj.addUserName(empusername,password,cat,conn,mobile);
					 */
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Driver registered successfully"));
					fname=null;qualification=null;marital=null;gender=null;email=null;comment=null;category=null;address=null;
					mobile=0;mobile2=null;empImage=null;leavingDate=null;spouseName=null;salary=0;name=null;joiningDate=null;dob=null;
					password="";
					FacesContext.getCurrentInstance().getExternalContext().redirect("addBus.xhtml");
					return "addBus.xhtml";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
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

			/*}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username : "+empusername+" ,already exist please "
																					+ "try something different! "));

				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}*/
		}
		return "";

	}
	public void showSpouseMethod()
	{

		if(marital.equals("Married"))
		{
			showSpouse=true;
		}
		else
		{
			showSpouse=false;
		}
	}

	public String addEmployeeCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int flag=0;
		int status=new DataBaseValidator().duplicateEmployeeCategory(employeeCategory,conn);
		if(status==0)
		{
			flag++;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Category already exist,choose a different one", "Validation error"));
		}
		if(flag==0)
		{
			try
			{
				int i=obj.addNewEmployeeCategory(employeeCategory,conn);
				if(i==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Category added successfully", "Validation error"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		employeeCategory=null;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void registerNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//int flag=0;
		boolean check=obj.checkLoginDuplicacy(empusername,conn);
		if(check==false)
		{
	    // boolean duplicateId = obj.checkEmployeUniqueIdDuplicasy(employeeUniqueId,conn,"add");
				
	    
			
			try
			{
				int i=obj.employeeAddmissionHere(salary, name, joiningDate, dob, fname, qualification,
						marital, gender, email, comment, category, address, mobile, mobile2, /*leavingDate,*/
						empImage, spouseName, empusername, aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,
						expImage,otherImage,conn,dlNo,dlImage,leave,subCateg,annDate,daamt,hraamt,employeeUniqueId,panNo,
						aadhaarNo,bankAccountNo,bankName,bankBranch,ifscCode,epfUanNo,esiNo,platform);
				if(i>=1)
				{
					String refNo;
			          try {
			        	  refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					} 
						
					
					String accountName = name+"-"+subCateg+"-"+i;
					new DataBase().addAccount("EXPENSES->Advance Salary",accountName,address,"","","",obj.schoolId(),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),conn);

					String refNo3;
					try {
						refNo3=addWorkLog3(conn,accountName);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					String cat=obj.employeeCategoryByIdSchid(obj.schoolId(),category,conn);
					obj.addUserName(empusername,password,cat,conn,mobile,platform);
					
					String refNo2;
					try {
						refNo2=addWorkLog2(conn,cat);
					} catch (Exception e) {
						e.printStackTrace();
					}

					//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employee registered successfully"));
					balMsg="Employee registered successfully";

					name=fname=empusername=password=category=subCateg=dlNo=employeeUniqueId="";
					bankAccountNo=bankName=bankBranch=ifscCode=epfUanNo=esiNo=panNo=aadhaarNo="";
					gender="Male";
					dob=new Date();
					joiningDate=new Date();
					marital="Unmarried";

					qualification="";email="";comment="";address="";
					mobile=0;mobile2="";leavingDate=null;spouseName="";salary=0;leave=0;

					empImage=aadharImage=panImage=voteridImage=dlImage=ugImage=pgImage=bedImage=policeImage=expImage=otherImage=null;

					PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
					PrimeFaces.current().ajax().update("msgForm");
					//return "employeeAddmission.xhtml";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
					//return "";
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
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username : "+empusername+" ,already exist please "
					+ "try something different! "));

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//return "";
		}

		//return "";

	}
	
	public String addWorkLog2(Connection conn,String cat)
	{
	    String value = "";
		String language= "";
		
	    value += "username-"+empusername+" --password-"+password+" --Category-"+cat+" --Mobile-"+mobile ; 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Employee username","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn,String accName)
	{
	    String value = "";
		String language= "";
		
	    value += "Account name-"+accName+" --address-"+address ; 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"EXPENSES->Advance Salary","WEB",value,conn);
		return refNo;
	}
	
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		String joinDt ="";
		if(joiningDate!=null)
		{	
			try {
				joinDt = formatter.format(joiningDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		String dobDt ="";
		if(dob!=null)
		{
			try {
				dobDt = formatter.format(dob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		String annDatee ="";
		if(annDate!=null)
		{
			try {
				annDatee = formatter.format(annDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	
		
		language = "Salary-"+salary+" --Name-"+name+" --join Date-"+joinDt+" --Dob-"+dobDt+" --Father-"+fname+" --Qualification-"+qualification+" --Marital-"+
				marital+" --Gender-"+gender+" --Email-"+email+" --Comment-"+comment+" --Category-"+category+" --Address-"+address+" --Mobile-"+mobile+" --Mobile2-"+mobile2
				+" --Spouse-"+spouseName+" --username-"+empusername+" --Dl No.-"+dlNo+" --Leave-"+leave+" --Sub Categ-"+subCateg+" --leave Date-"+annDatee+" --DA-"+daamt+" --HRA-"+hraamt+" --EmployeeUid-"+employeeUniqueId+" --Pan No-"+panNo+" --Aadhar No-"+
						aadhaarNo+" --Bank Acnt No.-"+bankAccountNo+" --Bank name-"+bankName+" --Bank Branch-"+bankBranch+" --IfSc Code-"+ifscCode+" --EpF Uan No-"+epfUanNo+" --Esi No-"+esiNo;
		
		String empImag = "";
		if(empImage != null && empImage.getSize()>0)
		{
			try {
				empImag = empImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		String addhImag = "";
		if(aadharImage != null && aadharImage.getSize()>0)
		{
			try {
				addhImag = aadharImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String panImag = "";
		if(panImage != null && panImage.getSize()>0)
		{
			try {
				panImag = panImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String vote = "";
		if(voteridImage != null && voteridImage.getSize()>0)
		{
			try {
				vote = voteridImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String ugImag = "";
		if(ugImage != null && ugImage.getSize()>0)
		{
			try {
				ugImag = ugImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String pgImag = "";
		if(pgImage != null && pgImage.getSize()>0)
		{
			try {
				pgImag = pgImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String beddImag = "";
		if(bedImage != null && bedImage.getSize()>0)
		{
			try {
				beddImag = bedImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String pol = "";
		if(policeImage != null && policeImage.getSize()>0)
		{
			try {
				pol = policeImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String expImg = "";
		if(expImage != null && expImage.getSize()>0)
		{
			try {
				expImg = expImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String othImg = "";
		if(otherImage != null && otherImage.getSize()>0)
		{
			try {
				othImg = otherImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String delImg = "";
		if(dlImage != null && dlImage.getSize()>0)
		{
			try {
				delImg = dlImage.getFileName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		
		
		
	    value = " Emp Image-"+empImag+" --Aadhar Image-"+addhImag+" --PAN IMAGE-"+panImag+" --VoterId-"+vote+" --Ug Image-"+ugImag+" --PgImage-"+pgImag+" --Bed. Image-"+beddImag+" --Police Image-"+pol
				+" --Exp Image-"+expImg+" --Other Image-"+othImg+" --Dl Image-"+delImg;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Employee Admission","WEB",value,conn);
		return refNo;
	}
	

	public ArrayList<SelectItem> getCategoryList() {
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
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
		return categoryList;
	}

	public boolean isShowSpouse() {
		return showSpouse;
	}

	public void setShowSpouse(boolean showSpouse) {
		this.showSpouse = showSpouse;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public String getEmployeeCategory() {
		return employeeCategory;
	}
	public void setEmployeeCategory(String employeeCategory) {
		this.employeeCategory = employeeCategory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getJoiningDate() {
		return joiningDate;
	}
	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
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
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}

	public long getMobile() {
		return mobile;
	}
	public void setMobile(long mobile) {
		this.mobile = mobile;
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
	public String getEmpusername() {
		return empusername;
	}

	public void setEmpusername(String empusername) {
		this.empusername = empusername;
	}
	public Date getLeavingDate() {
		return leavingDate;
	}

	public void setLeavingDate(Date leavingDate) {
		this.leavingDate = leavingDate;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public UploadedFile getEmpImage() {
		return empImage;
	}

	public void setEmpImage(UploadedFile empImage) {
		this.empImage = empImage;
	}

	public UploadedFile getAadharImage() {
		return aadharImage;
	}

	public void setAadharImage(UploadedFile aadharImage) {
		this.aadharImage = aadharImage;
	}

	public UploadedFile getPanImage() {
		return panImage;
	}

	public void setPanImage(UploadedFile panImage) {
		this.panImage = panImage;
	}

	public UploadedFile getVoteridImage() {
		return voteridImage;
	}

	public void setVoteridImage(UploadedFile voteridImage) {
		this.voteridImage = voteridImage;
	}

	public UploadedFile getUgImage() {
		return ugImage;
	}

	public void setUgImage(UploadedFile ugImage) {
		this.ugImage = ugImage;
	}

	public UploadedFile getPgImage() {
		return pgImage;
	}

	public void setPgImage(UploadedFile pgImage) {
		this.pgImage = pgImage;
	}

	public UploadedFile getBedImage() {
		return bedImage;
	}

	public void setBedImage(UploadedFile bedImage) {
		this.bedImage = bedImage;
	}

	public UploadedFile getPoliceImage() {
		return policeImage;
	}

	public void setPoliceImage(UploadedFile policeImage) {
		this.policeImage = policeImage;
	}

	public UploadedFile getExpImage() {
		return expImage;
	}

	public void setExpImage(UploadedFile expImage) {
		this.expImage = expImage;
	}

	public UploadedFile getOtherImage() {
		return otherImage;
	}

	public void setOtherImage(UploadedFile otherImage) {
		this.otherImage = otherImage;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDlNo() {
		return dlNo;
	}

	public void setDlNo(String dlNo) {
		this.dlNo = dlNo;
	}

	public UploadedFile getDlImage() {
		return dlImage;
	}

	public void setDlImage(UploadedFile dlImage) {
		this.dlImage = dlImage;
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

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public Date getAnnDate() {
		return annDate;
	}

	public void setAnnDate(Date annDate) {
		this.annDate = annDate;
	}

	public String getMobDigit() {
		return mobDigit;
	}

	public void setMobDigit(String mobDigit) {
		this.mobDigit = mobDigit;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
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

	public String getEmployeeUniqueId() {
		return employeeUniqueId;
	}

	public void setEmployeeUniqueId(String employeeUniqueId) {
		this.employeeUniqueId = employeeUniqueId;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
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
