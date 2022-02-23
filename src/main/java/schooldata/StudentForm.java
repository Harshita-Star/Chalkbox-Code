package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;
@ManagedBean(name="studentForm")
@ViewScoped

public class StudentForm implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String studentName,gender,address,mobno,mothmobno,email,fatherName,motherName,admissionNo,addmissionclass,status,userId,description,balMsg,userType;
	Date dob,admissionDate,followDate,todayDate;
	String category="-1", stCategory="", lastSchool="", referedBy="";
	ArrayList<SelectItem> classList,categoryList, stCategoryList;
	//String paymentMode="cash",procpectusFee="250";
	boolean prospectusShow,normalShow;
	String eno ;

	ArrayList<SelectItem> sessionList;
	String selectedSession;
	double smsLimit;
	SchoolInfoList info = new SchoolInfoList();

	public void sessionMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int start=new DatabaseMethods1().schoolStartingSession(new DatabaseMethods1().schoolId(),conn);
		////// // System.out.println("start : "+start);
		sessionList=new ArrayList<>();

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

		for(int i=start;i<=year+1;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		if(month>=4)
		{
			selectedSession=String.valueOf(year)+"-"+String.valueOf(year+1);
		}
		else
		{
			selectedSession=String.valueOf(year-1)+"-"+String.valueOf(year);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  StudentForm()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) ss.getAttribute("type");
		userId=(String) ss.getAttribute("username");
		todayDate=new Date();
		followDate=new Date();
		Connection conn=DataBaseConnection.javaConnection();
		info=new DatabaseMethods1().fullSchoolInfo(conn);
		classList=new DatabaseMethods1().allClass(conn);
		categoryList=new DatabaseMethods1().allRefernceCategory(conn);
		stCategoryList = new DatabaseMethods1().studentCategoryList(conn);
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		admissionDate=new Date();
		if(info.getBranch_id().equals("54")
				|| info.getBranch_id().equals("69")
				|| info.getBranch_id().equals("71")
				|| info.getBranch_id().equals("3")
				|| info.getBranch_id().equals("25")
				|| info.getBranch_id().equals("22")
				|| info.getBranch_id().equals("27"))
		{
			prospectusShow=true;
			normalShow=false;
		}
		else
		{
			prospectusShow=false;
			normalShow=true;
		}

		sessionMethod();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/*
	public void sourceReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		DatabaseMethods1 dbm = new DatabaseMethods1();
		
		String count = "0";
		for(SelectItem ss : categoryList)
		{
			count = dbr.countEnquiryBySourceId(String.valueOf(ss.getValue()),dbm.schoolId(),conn);
			
			ss.setDescription(count);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
*/
	public String registration() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		info=DBM.fullSchoolInfo(conn);

		eno = "CB/ENQ/"+new SimpleDateFormat("yMdhms").format((new Date()));
		if(info.getBranch_id().equals("54"))
		{
			status="pending";
			eno = "BLM/ENQ/"+new SimpleDateFormat("yMdhms").format((new Date()));
		}
		else
		{
			status="pending";
		}
		/*else if(info.getBranch_id().equals("69")
				|| info.getBranch_id().equals("71")
				|| info.getBranch_id().equals("3")
				|| info.getBranch_id().equals("25")
				|| info.getBranch_id().equals("22")
				|| info.getBranch_id().equals("52")
				|| info.getBranch_id().equals("66"))
		{
			status="pending";
		}*/


		if(dob==null)
		{
			dob=new Date();
		}

		String className = "";

		cls:for(SelectItem ss : classList)
		{
			if(String.valueOf(ss.getValue()).equals(addmissionclass))
			{
				className = ss.getLabel();
				break cls;
			}
		}

		String result=DBM.studentRegSchid(DBM.schoolId(), admissionDate,admissionNo,studentName,fatherName,motherName,gender,dob,address,
				mobno, email,addmissionclass,followDate,status,userId,description,conn,eno,selectedSession,category,className,mothmobno, stCategory, lastSchool, referedBy);
		if(result.equals("successful"))
		{
			String refNo;
			try {
				refNo=addWorkLog(conn,className);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successfully Registered!!"));
			PrimeFaces.current().ajax().update("confirmForm");
			PrimeFaces.current().executeInitScript("PF('enqDlg').show()");
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			HttpSession cs=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            cs.setAttribute("enqSession", selectedSession);
            cs.setAttribute("enqAdmDate", formatter.format(admissionDate));
            
            String schidd = DBM.schoolId();
  	      String sessioncurrent = DatabaseMethods1.selectedSessionDetails(schidd,conn);
            String admiClasss = DBM.classNameFromidSchid(schidd, addmissionclass, sessioncurrent, conn);

            cs.setAttribute("enqClass", admiClasss);
            cs.setAttribute("enqStudent", studentName);
            cs.setAttribute("enqFather", fatherName);
            cs.setAttribute("enqMother", motherName);
            cs.setAttribute("enqGender", gender);
            cs.setAttribute("enqDob", formatter.format(dob));
            cs.setAttribute("enqAddress", address);
            cs.setAttribute("enqMob", mobno);
            cs.setAttribute("enqMotMob", mothmobno);
            cs.setAttribute("enqEmail", email);
            cs.setAttribute("enqCategory", category);
            cs.setAttribute("enqDescription", description);
            
		    PrimeFaces.current().executeInitScript("window.open('printAdmissionEnquiry.xhtml')");

            
            
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String message=(String) ss.getAttribute("enquiry");
			if(message.equals("true"))
			{

				double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
				if(balance >0 && balance <= smsLimit)
				{
					balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
							+ "We suggest you to top-up your account today to ensure uninterrupted activity";
					if (userType.equalsIgnoreCase("admin"))
					{
						PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
						PrimeFaces.current().ajax().update("MsgLimitForm");
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						return "";
					}
				}
				else if(balance <= 0)
				{
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
					if (userType.equalsIgnoreCase("admin"))
					{
						PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
						PrimeFaces.current().ajax().update("MsgOverForm");
					}
					else
					{
						balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

						PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
						PrimeFaces.current().ajax().update("MsgOtherForm");
					}
					admissionDate=new Date();admissionNo="";studentName="";fatherName="";motherName="";gender="";dob=new Date();address="";mobno=""; email="";
					followDate=new Date();status="";addmissionclass="";
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}

				if(mobno.length()==10
						&& !mobno.equals("2222222222")
						&& !mobno.equals("9999999999")
						&& !mobno.equals("1111111111")
						&& !mobno.equals("1234567890")
						&& !mobno.equals("0123456789"))
				{
					if(info.getBranch_id().equalsIgnoreCase("52"))
					{
						String typemessage="Dear Parent,\nThank you For Visiting The Oasis.We Hope to see you again.\nRegards, \n"+info.getSmsSchoolName();
						DBM.messageurlenq(mobno,typemessage,studentName,conn,"");

					}
					if(info.getBranch_id().equalsIgnoreCase("94"))
					{
						String typemessage="Dear Parent,\nGreetings from Seth Anandram Jaipuria School, Sitarganj. Thank you for showing interest in our school. Your enquiry no is "+eno+" Please keep it safe for addmission purpose. For any query plz call 9105000638, 9105000639\r\n" + 
								"Regards\r\n" + 
								"Team Jaipuria";
						DBM.messageurlenq(mobno,typemessage,studentName,conn,"");

					}
					else
					{ 
						String templateId=new DataBaseMeathodJson().templateId(info.getKey(), "ENQ", conn);
						
						
						String typemessage="Dear Parent,\nThank you for visiting our "+info.getClient_type()+". Your enquiry reference no. is "+eno+". Please save it for admission purpose. We look forward to see you again.\nRegards, \n"+info.getSmsSchoolName();
						DBM.messageurlenqwithTemplateId(mobno,typemessage,studentName,templateId,conn);

					}
				}

			}

			admissionDate=new Date();admissionNo="";studentName="";fatherName="";motherName="";gender="";dob=new Date();address="";mobno=""; email="";
			followDate=new Date();status="";addmissionclass="";
			//eno="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Try Another Admission Number or something went wrong"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";

	}
	
	public String addWorkLog(Connection conn,String className)
	{
	    String value = "";
		String language= "";
		
		Timestamp db = new Timestamp(admissionDate.getTime());
		
		Timestamp dt = new Timestamp(dob.getTime());
		
		language =  db+" -- "+selectedSession+" -- "+addmissionclass+" -- "+studentName+" -- "+fatherName+" -- "+motherName+" -- "+gender+" -- "+dt+" -- "+address+" -- "+mobno+" -- "+mothmobno+" -- "+email+" -- "+category+" -- "+description; 
		value = db+" -- "+admissionNo+" -- "+studentName+" -- "+fatherName+" -- "+motherName+" -- "+gender+" -- "+dob+" -- "+address+" -- "+
				mobno+" -- "+email+" -- "+addmissionclass+" -- "+followDate+" -- "+status+" -- "+userId+" -- "+description+" -- "+eno+" -- "+selectedSession+" -- "+category+" -- "+className+" -- "+mothmobno;
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Enquiry","WEB",value,conn);
		return refNo;
	}


	public void sendMsg()
	{

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		info=DBM.fullSchoolInfo(conn);
		if(mobno.length()==10
				&& !mobno.equals("2222222222")
				&& !mobno.equals("9999999999")
				&& !mobno.equals("1111111111")
				&& !mobno.equals("1234567890")
				&& !mobno.equals("0123456789"))
		{
			if(info.getBranch_id().equalsIgnoreCase("52"))
			{
				String typemessage="Dear Sir/Madam,\nThank you For Visiting The Oasis.We Hope to see you again.\nRegards, \n"+info.getSmsSchoolName();
				DBM.messageurlenq(mobno,typemessage,studentName,conn,"");

			}
			else
			{
				String typemessage="Dear Sir/Madam,\nThank you For Visiting Our "+info.getClient_type()+". "+eno+" is your enquiry reference no. Please keep it safe for admission purpose.We Hope to see you again.\nRegards, \n"+info.getSmsSchoolName();
				DBM.messageurlenq(mobno,typemessage,studentName,conn,"");

			}
		}
		admissionDate=new Date();admissionNo="";studentName="";fatherName="";motherName="";gender="";dob=new Date();address="";mobno=""; email="";
		followDate=new Date();status="";addmissionclass="";

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getAddmissionclass() {
		return addmissionclass;
	}
	public void setAddmissionclass(String addmissionclass) {
		this.addmissionclass = addmissionclass;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getMotherName() {
		return motherName;
	}
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	public String getAdmissionNo() {
		return admissionNo;
	}
	public void setAdmissionNo(String admissionNo) {
		this.admissionNo = admissionNo;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getFollowDate() {
		return followDate;
	}

	public void setFollowDate(Date followDate) {
		this.followDate = followDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

	public boolean isProspectusShow() {
		return prospectusShow;
	}

	public void setProspectusShow(boolean prospectusShow) {
		this.prospectusShow = prospectusShow;
	}

	public boolean isNormalShow() {
		return normalShow;
	}

	public void setNormalShow(boolean normalShow) {
		this.normalShow = normalShow;
	}

	public String getEno() {
		return eno;
	}

	public void setEno(String eno) {
		this.eno = eno;
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}

	public String getSelectedSession() {
		return selectedSession;
	}

	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public String getMothmobno() {
		return mothmobno;
	}

	public void setMothmobno(String mothmobno) {
		this.mothmobno = mothmobno;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getStCategory() {
		return stCategory;
	}

	public void setStCategory(String stCategory) {
		this.stCategory = stCategory;
	}

	public String getLastSchool() {
		return lastSchool;
	}

	public void setLastSchool(String lastSchool) {
		this.lastSchool = lastSchool;
	}

	public String getReferedBy() {
		return referedBy;
	}

	public void setReferedBy(String referedBy) {
		this.referedBy = referedBy;
	}

	public ArrayList<SelectItem> getStCategoryList() {
		return stCategoryList;
	}

	public void setStCategoryList(ArrayList<SelectItem> stCategoryList) {
		this.stCategoryList = stCategoryList;
	}
	

}
