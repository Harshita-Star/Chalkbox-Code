package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
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

import session_work.RegexPattern;

@ManagedBean(name="blmMasterAddEnquiry")
@ViewScoped

public class BlmMasterAddEnquiryBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String studentName,gender,address,mobno,mothmobno,email,fatherName,motherName,admissionNo,addmissionclass,status,userId,description,schoolid;
	Date dob,admissionDate,followDate,todayDate;
	ArrayList<SelectItem> classList;
	boolean prospectusShow,normalShow;
	String eno,balMsg ;
	double smsLimit;
	String category = "-1";

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	ArrayList<SelectItem> branchList = new ArrayList<>();
	ArrayList<SelectItem> sessionList;
	String selectedSession;

	public void sessionMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		addmissionclass = "";
		classList = new DatabaseMethods1().allClass(schoolid,conn);
		int start=new DatabaseMethods1().schoolStartingSession(schoolid,conn);
		////// // System.out.println("start : "+start);
		sessionList=new ArrayList<>();
		for(int i=start;i<=2050;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

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

	public  BlmMasterAddEnquiryBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userId=(String) ss.getAttribute("username");
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		todayDate=new Date();
		followDate=new Date();
		Connection conn=DataBaseConnection.javaConnection();
		admissionDate=new Date();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String registration() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		SchoolInfoList info=DBM.fullSchoolInfo(schoolid,conn);
		if(info.getBranch_id().equalsIgnoreCase("54"))
		{
			status="pending";
			eno = "BLM/ENQ/"+new SimpleDateFormat("yMdhms").format((new Date()));
		}
		else
		{
			eno = "CB/ENQ/"+new SimpleDateFormat("yMdhms").format((new Date()));

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

		String result=DBM.studentRegSchid(schoolid, admissionDate,admissionNo,studentName,fatherName,motherName,gender,dob,address,
				mobno, email,addmissionclass,followDate,status,userId,description,conn,eno,selectedSession,category,className,mothmobno, "", "", "");
		if(result.equals("successful"))
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String message=(String) ss.getAttribute("enquiry");
			if(message.equals("true"))
			{

				double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
				if(balance >0 && balance <= smsLimit)
				{
					balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
							+ "We suggest you to top-up your account today to ensure uninterrupted activity";
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
				else if(balance <= 0)
				{
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
					PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
					PrimeFaces.current().ajax().update("MsgOverForm");

					admissionDate=new Date();admissionNo="";studentName="";fatherName="";motherName="";gender="";dob=new Date();address="";mobno=""; email="";
					followDate=new Date();status="";addmissionclass="";
					schoolid="";
					sessionList = new ArrayList<>();
					classList = new ArrayList<>();
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
					String typemessage="Dear Sir/Madam,\nThank you For Visiting Our "+info.getClient_type()+". "+eno+" is your enquiry reference no. Please keep it safe for admission purpose.We Hope to see you again.\nRegards, \n"+info.getSmsSchoolName();
					DBM.messageurl1(mobno,typemessage,"temp",conn,schoolid,"");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successfully Registered!!"));
			PrimeFaces.current().ajax().update("confirmForm");
			PrimeFaces.current().executeInitScript("PF('enqDlg').show()");
			admissionDate=new Date();admissionNo="";studentName="";fatherName="";motherName="";gender="";dob=new Date();address="";mobno=""; email="";
			followDate=new Date();status="";addmissionclass="";
			schoolid="";
			sessionList = new ArrayList<>();
			classList = new ArrayList<>();
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

	public void sendMsg()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();

		SchoolInfoList info=DBM.fullSchoolInfo(conn);
		if(mobno.length()==10
				&& !mobno.equals("2222222222")
				&& !mobno.equals("9999999999")
				&& !mobno.equals("1111111111")
				&& !mobno.equals("1234567890")
				&& !mobno.equals("0123456789"))
		{
			String typemessage="Dear Sir/Madam,\nThank you For Visiting Our "+info.getClient_type()+". "+eno+" is your enquiry reference no. Please keep it safe for admission purpose.We Hope to see you again.\nRegards, \n"+info.getSmsSchoolName();
			DBM.messageurl1(mobno,typemessage,"temp",conn,schoolid,"");
		}
		admissionDate=new Date();admissionNo="";studentName="";fatherName="";motherName="";gender="";dob=new Date();address="";mobno=""; email="";
		followDate=new Date();status="";addmissionclass="";
		schoolid="";
		sessionList = new ArrayList<>();
		classList = new ArrayList<>();

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


	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
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
	
}
