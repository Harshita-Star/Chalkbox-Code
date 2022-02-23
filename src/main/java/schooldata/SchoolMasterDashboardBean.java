package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="schoolMaster")
@ViewScoped

public class SchoolMasterDashboardBean implements Serializable
{
	public BarChartModel barModel;
	UploadedFile fileNew;
	String cunt,ticketType,userType,userid,description;
	String newStudent,enquirystudent,errorLabel,preview,bdyPreview,leaveStudent,totalGallery;
	ArrayList<String>datalist;
	String countPendingLeave;
	boolean showClass,showStudent,showEmployee,showTransport=false,showFinance,showAttendence,showMessage,showPerformance,showTC,showPromotion,
			showPrevious,setting,report,showHomeWork,messageParents=false,messageStudents=false,showWishAll=false;
	String messageStduent,sendTo,typeMessage="",status="";
	SchoolInfoList ls;
	String name;
	Date d=new  Date();
	StudentInfo selectStudent;
	ArrayList<StudentInfo> studentList,birthdayStudentList,sList;
	ArrayList<SelectItem> list,branchList;
	String totalCollection;
	String temp1,temp2,temp3,temp4,template="",birthdayWish="";
	ArrayList<Sum> sum;

	String branches="";

	public SchoolMasterDashboardBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		HttpSession ss=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		sendTo="student";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
				}
			}
		}
		String session=DBM.selectedSessionDetails(branches, conn);
		cunt=DBM.allStudentcount(branches,"","",session,"",conn);
		Date d=new  Date();
		int year1=d.getYear()+1900;
		String selectYear=String.valueOf(year1);
		enquirystudent=DBM.allNewStudentEnquirycount(branches,selectYear,conn);
		leaveStudent=DBM.allLeaveStudent(branches,selectYear,conn);
		messageStduent=DBM.todayMessage(branches,conn);
		totalCollection=DBM.todaysCollection(branches,"masterDashboard",conn);
	}

	public void goToAllStudent() throws IOException
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("studentListPage", "dashboard");

		FacesContext.getCurrentInstance().getExternalContext().redirect("masterAllStudentList.xhtml");

	}

	public void sendMessageToAll()
	{
		messageParents=messageStudents=false;
		Connection conn=DataBaseConnection.javaConnection();
		if(typeMessage==null || typeMessage.equals(""))
		{
			errorLabel="Please Wrtie a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else
		{
			if(sendTo.equals("student"))
			{
				schoolPreview();
			}
			else
			{
				staffPreview();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void schoolPreview()
	{
		preview="Dear Parent, "+typeMessage+" \nRegards, B.L.M. Academy, Haldwani";

		PrimeFaces.current().executeInitScript("PF('prevDialog').show()");
		PrimeFaces.current().ajax().update("prevForm");
	}

	public void staffPreview()
	{
		preview="Dear Staff Member, "+typeMessage+" \nRegards, B.L.M. Academy, Haldwani";

		PrimeFaces.current().executeInitScript("PF('prevDialog').show()");
		PrimeFaces.current().ajax().update("prevForm");
	}

	public void sendMessageNow() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{
			if(sendTo.equals("student"))
			{
				PrimeFaces.current().executeInitScript("PF('prevDialog').hide()");
				PrimeFaces.current().ajax().update("prevForm");
				sendMessageSchool();
			}
			else
			{
				PrimeFaces.current().executeInitScript("PF('prevDialog').hide()");
				PrimeFaces.current().ajax().update("prevForm");
				ArrayList<EmployeInfo> eList=new ArrayList<>();
				new ArrayList<>();
				for(SelectItem in : branchList)
				{
					eList=obj.allEmployees(String.valueOf(in.getValue()),conn);
					//eList.addAll(tempList);
					if(eList.isEmpty())
					{
						errorLabel="No Staff Member Found in "+in.getLabel()+".";
						PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
						PrimeFaces.current().ajax().update("errorForm");
					}
					else
					{
						String employeeContact="";
						String employeeNumber="";
						for(EmployeInfo ee : eList)
						{
							if(String.valueOf(ee.getMobile()).length()==10)
							{
								if(employeeContact.equals(""))
								{
									employeeContact=String.valueOf(ee.getMobile());
									employeeNumber=String.valueOf(ee.getName());
								}
								else
								{
									employeeContact=employeeContact+","+String.valueOf(ee.getMobile());
									employeeNumber=employeeNumber+","+String.valueOf(ee.getName());
								}
							}
						}

						////// // System.out.println(employeeContact.split(",").length);
						String msg="Dear Staff Member, "+typeMessage+" \nRegards, B.L.M. Academy, Haldwani";
						obj.messageurlStaff(employeeContact, msg,employeeNumber,conn,String.valueOf(in.getValue()),"");
						typeMessage="";
						sendTo="student";
						errorLabel="Message Sent Successfully in "+in.getLabel()+".";
						PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
						PrimeFaces.current().ajax().update("errorForm");
						PrimeFaces.current().ajax().update("mainForm");

						//FacesContext.getCurrentInstance().getExternalContext().redirect("schoolMasterDashboard.xhtml");
					}
				}


			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}


	public void sendMessageSchool() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		try
		{
			for(SelectItem in : branchList)
			{
				sList=DBM.allStudentListSchid(String.valueOf(in.getValue()),conn);
				if(sList.isEmpty())
				{
					errorLabel="No Student Found.";
					PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
					PrimeFaces.current().ajax().update("errorForm");
					//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Student Found", "Validation Error"));
				}
				else
				{
					String contactNumber="";
					String addNumber="";
					for(StudentInfo info : sList)
					{
						if(String.valueOf(info.getFathersPhone()).length()==10)
						{
							if(contactNumber.equals(""))
							{
								contactNumber=String.valueOf(info.getFathersPhone());
								addNumber=info.getAddNumber();
							}
							else
							{
								contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
								addNumber=addNumber+","+info.getAddNumber();
							}

						}

					}

					////// // System.out.println(contactNumber.split(",").length);
					String msg="Dear Parent, "+typeMessage+" \nRegards, B.L.M Academy, Haldwani";
					DBM.messageurl1(contactNumber, msg,addNumber,conn,String.valueOf(in.getValue()),"");

					//			FacesContext fc=FacesContext.getCurrentInstance();
					//			fc.addMessage(null, new FacesMessage("You have sent message to "+studentList.size()+" stduents "));

					errorLabel="Message Sent Successfully.";
					typeMessage="";
					PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
					PrimeFaces.current().ajax().update("errorForm");
					PrimeFaces.current().ajax().update("mainForm");
					//FacesContext.getCurrentInstance().getExternalContext().redirect("Dashboard.xhtml");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public String getNewStudent() {
		return newStudent;
	}

	public void setNewStudent(String newStudent) {
		this.newStudent = newStudent;
	}

	public String getCunt() {
		return cunt;
	}

	public void setCunt(String cunt) {
		this.cunt = cunt;
	}

	public String getEnquirystudent() {
		return enquirystudent;
	}

	public void setEnquirystudent(String enquirystudent) {
		this.enquirystudent = enquirystudent;
	}


	public ArrayList<String> getDatalist() {
		return datalist;
	}


	public void setDatalist(ArrayList<String> datalist) {
		this.datalist = datalist;
	}


	public String getMessageStduent() {
		return messageStduent;
	}


	public void setMessageStduent(String messageStduent) {
		this.messageStduent = messageStduent;
	}


	public String getTotalCollection() {
		return totalCollection;
	}


	public void setTotalCollection(String totalCollection) {
		this.totalCollection = totalCollection;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

	public String getCountPendingLeave() {
		return countPendingLeave;
	}

	public void setCountPendingLeave(String countPendingLeave) {
		this.countPendingLeave = countPendingLeave;
	}


	public StudentInfo getSelectStudent() {
		return selectStudent;
	}


	public void setSelectStudent(StudentInfo selectStudent) {
		this.selectStudent = selectStudent;
	}


	public ArrayList<StudentInfo> getBirthdayStudentList() {
		return birthdayStudentList;
	}


	public void setBirthdayStudentList(ArrayList<StudentInfo> birthdayStudentList) {
		this.birthdayStudentList = birthdayStudentList;
	}
	public String getSendTo() {
		return sendTo;
	}
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getTypeMessage() {
		return typeMessage;
	}

	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}

	public boolean isMessageParents() {
		return messageParents;
	}

	public void setMessageParents(boolean messageParents) {
		this.messageParents = messageParents;
	}

	public boolean isMessageStudents() {
		return messageStudents;
	}

	public void setMessageStudents(boolean messageStudents) {
		this.messageStudents = messageStudents;
	}

	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getTemp1() {
		return temp1;
	}

	public void setTemp1(String temp1) {
		this.temp1 = temp1;
	}

	public String getTemp2() {
		return temp2;
	}

	public void setTemp2(String temp2) {
		this.temp2 = temp2;
	}

	public String getTemp3() {
		return temp3;
	}

	public void setTemp3(String temp3) {
		this.temp3 = temp3;
	}

	public String getTemp4() {
		return temp4;
	}

	public void setTemp4(String temp4) {
		this.temp4 = temp4;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getBirthdayWish() {
		return birthdayWish;
	}

	public void setBirthdayWish(String birthdayWish) {
		this.birthdayWish = birthdayWish;
	}

	public String getBdyPreview() {
		return bdyPreview;
	}

	public void setBdyPreview(String bdyPreview) {
		this.bdyPreview = bdyPreview;
	}

	public String getLeaveStudent() {
		return leaveStudent;
	}

	public void setLeaveStudent(String leaveStudent) {
		this.leaveStudent = leaveStudent;
	}

	public boolean isShowWishAll() {
		return showWishAll;
	}

	public void setShowWishAll(boolean showWishAll) {
		this.showWishAll = showWishAll;
	}

	public String getTotalGallery() {
		return totalGallery;
	}

	public void setTotalGallery(String totalGallery) {
		this.totalGallery = totalGallery;
	}

	public UploadedFile getFileNew() {
		return fileNew;
	}

	public void setFileNew(UploadedFile fileNew) {
		this.fileNew = fileNew;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



}
