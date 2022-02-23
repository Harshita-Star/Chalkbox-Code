package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;
@ManagedBean(name="instituteEnquiryForm")
@ViewScoped
public class InstituteEnquiryForm implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem>classes=new ArrayList<>();
	ArrayList<String>information=new ArrayList<>();
	String studentName,address,mobno,instituteInfo,fatherName,addmissionclass,medium,studentMobno,preSchool,selectedClasses,
	remark,status,userId,balMsg,userType;
	Date admissionDate,followDate,todayDate;
	ArrayList<SelectItem> classList;
	double smsLimit;
	public InstituteEnquiryForm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		smsLimit = DBM.smsLimitReminder(DBM.schoolId(), conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userId=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");

		classList=DBM.allClass11(conn);
		classes=DBM.instituteClass(conn);
		admissionDate=new Date();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addEnqiury() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
	
		boolean check=DBM.checkDuplicateEnquiry(mobno,conn);
		if(check)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Enquiry Found"));
		}
		else
		{

			String informationInstitute="";
			for(String ss:information)
			{
				if(informationInstitute.equalsIgnoreCase(""))
				{
					informationInstitute=ss;
				}
				else
				{
					informationInstitute=informationInstitute+","+ss;
				}
			}
			String result=DBM.addEnqiury(admissionDate,studentName,fatherName,mobno,studentMobno,addmissionclass,medium,instituteInfo,
					address,selectedClasses,informationInstitute,preSchool,remark,status,followDate,userId,conn);
			if(result.equalsIgnoreCase("successfull"))
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
						admissionDate=new Date();studentName="";fatherName="";address="";mobno="";addmissionclass="";studentMobno=""; instituteInfo="";medium="";preSchool="";remark="";selectedClasses="";informationInstitute="";status="";followDate=new Date();
						classes=DBM.instituteClass(conn);
						information=new ArrayList<>();
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
						SchoolInfoList info=DBM.fullSchoolInfo(conn);
						String typemessage="Dear Sir/Madam,\nThank you For Visiting Our "+info.getClient_type()+".\nRegards, \n"+info.getSmsSchoolName();
						DBM.messageurl1(mobno,typemessage,studentName,conn,DBM.schoolId(),"");
					}
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successfully Registered!!"));
				admissionDate=new Date();studentName="";fatherName="";address="";mobno="";addmissionclass="";studentMobno=""; instituteInfo="";medium="";preSchool="";remark="";selectedClasses="";informationInstitute="";status="";followDate=new Date();
				classes=DBM.instituteClass(conn);
				information=new ArrayList<>();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
			}
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
		if(mobno.length()==10
				&& !mobno.equals("2222222222")
				&& !mobno.equals("9999999999")
				&& !mobno.equals("1111111111")
				&& !mobno.equals("1234567890")
				&& !mobno.equals("0123456789"))
		{
			SchoolInfoList info=DBM.fullSchoolInfo(conn);
			String typemessage="Dear Sir/Madam,\nThank you For Visiting Our "+info.getClient_type()+".\nRegards, \n"+info.getSmsSchoolName();
			DBM.messageurl1(mobno,typemessage,studentName,conn,DBM.schoolId(),"");
		}
		admissionDate=new Date();studentName="";fatherName="";address="";mobno="";addmissionclass="";studentMobno=""; instituteInfo="";medium="";preSchool="";remark="";selectedClasses="";status="";followDate=new Date();
		classes=DBM.instituteClass(conn);
		information=new ArrayList<>();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
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


	public String getInstituteInfo() {
		return instituteInfo;
	}

	public void setInstituteInfo(String instituteInfo) {
		this.instituteInfo = instituteInfo;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getAddmissionclass() {
		return addmissionclass;
	}

	public void setAddmissionclass(String addmissionclass) {
		this.addmissionclass = addmissionclass;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}
	public String getStudentMobno() {
		return studentMobno;
	}

	public void setStudentMobno(String studentMobno) {
		this.studentMobno = studentMobno;
	}

	public ArrayList<SelectItem> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<SelectItem> classes) {
		this.classes = classes;
	}



	public ArrayList<String> getInformation() {
		return information;
	}

	public void setInformation(ArrayList<String> information) {
		this.information = information;
	}

	public String getSelectedClasses() {
		return selectedClasses;
	}

	public void setSelectedClasses(String selectedClasses) {
		this.selectedClasses = selectedClasses;
	}

	public String getPreSchool() {
		return preSchool;
	}

	public void setPreSchool(String preSchool) {
		this.preSchool = preSchool;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}


}
