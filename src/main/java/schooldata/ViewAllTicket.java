package schooldata;

import java.io.IOException;
import java.io.Serializable;
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

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;
@ManagedBean(name="viewAllTicket")
@ViewScoped
public class ViewAllTicket implements Serializable
{
	String regex=RegexPattern.REGEX;
	String selectedType,schoolId;
	String name,remark,remark1,remark2,remark3,remark4;
	String status="";
	Date resoltionDate;
	String userid,usertype,type;
	int count1,count2,count3,count4,count5,count6,count7;
	ArrayList<SelectItem> schoolList = new ArrayList<>();

	public void setCount5(int count5) {
		this.count5 = count5;
	}
	boolean check,errorStatus,suggestionStatus,showStatus,showSchoolid,showWrap;
	TicketRaisingInfo selectedTicket;
	ArrayList<TicketRaisingInfo>list=new ArrayList<>();

	public ViewAllTicket()
	{
		schoolId="All";
		remark4="Thank you for using ChalkBox. We thank you for your patience. We are happy to help.\nRegards,\nTeam ChalkBox";
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userid=(String) ss.getAttribute("username");
		usertype=(String) ss.getAttribute("type");
		resoltionDate=new Date();
		selectedType="";

		try {
			conn.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		//search();
		initial();
	}

	public void initial()
	{
		Connection conn = DataBaseConnection.javaConnection();
		list.clear();
		count1 = new DataBaseMethodsReports().countOfAllPending("pending",schoolId,conn);
		
		count2 = new DataBaseMethodsReports().countOfAllPending("Inprocess",schoolId,conn);

		count3 = new DataBaseMethodsReports().countOfAllPending("Not Appropriate",schoolId,conn);

		count4 = new DataBaseMethodsReports().countOfAllPending("Resolved",schoolId,conn);
			
		count5 = new DataBaseMethodsReports().countOfAllPending("All",schoolId,conn);
		
		count6 = new DataBaseMethodsReports().countOfAllPending("Wrapping Up",schoolId,conn);
		
		count7 = new DataBaseMethodsReports().countOfAllPending("close",schoolId,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void check1()
	{

		Connection conn = DataBaseConnection.javaConnection();
		//list=new DatabaseMethods1().allTicketByStatus("pending",schoolId,conn);
		type="pending";
		if(schoolId.equalsIgnoreCase("all"))
		{
			list=new DatabaseMethods1().allTicketByStatus("pending",conn);
		}
		else
		{
			list=new DatabaseMethods1().allTicketByStatus("pending",schoolId,conn);
		}

		showStatus=true;
		showWrap=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check2()
	{
		Connection conn = DataBaseConnection.javaConnection();
		type="Inprocess";
		if(schoolId.equalsIgnoreCase("all"))
		{
			list=new DatabaseMethods1().allTicketByStatus("Inprocess",conn);
		}
		else
		{
			list=new DatabaseMethods1().allTicketByStatus("Inprocess",schoolId,conn);
		}

		showStatus=true;
		showWrap=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check3()
	{
		Connection conn = DataBaseConnection.javaConnection();
		type="Not Appropriate";
		if(schoolId.equalsIgnoreCase("all"))
		{
			list=new DatabaseMethods1().allTicketByStatus("Not Appropriate",conn);
		}
		else
		{
			list=new DatabaseMethods1().allTicketByStatus("Not Appropriate",schoolId,conn);
		}

		showStatus=false;
		showWrap=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check4()
	{
		Connection conn = DataBaseConnection.javaConnection();
		type="Resolved";
		if(schoolId.equalsIgnoreCase("all"))
		{
			list=new DatabaseMethods1().allTicketByStatus("Resolved",conn);
		}
		else
		{
			list=new DatabaseMethods1().allTicketByStatus("Resolved",schoolId,conn);
		}

		showStatus=false;
		showWrap=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check5()
	{
		Connection conn = DataBaseConnection.javaConnection();
		type="All";
		if(schoolId.equalsIgnoreCase("all"))
		{
			list=new DatabaseMethods1().allTicketByStatus("All",conn);
		}
		else
		{
			list=new DatabaseMethods1().allTicketByStatus("All",schoolId,conn);
		}

		showStatus=false;
		showWrap=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void check6()
	{
		Connection conn = DataBaseConnection.javaConnection();
		type="Wrapping Up";
		if(schoolId.equalsIgnoreCase("all"))
		{
			list=new DatabaseMethods1().allTicketByStatus("Wrapping Up",conn);
		}
		else
		{
			list=new DatabaseMethods1().allTicketByStatus("Wrapping Up",schoolId,conn);
		}

		showStatus=false;
		showWrap=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void check7()
	{
		Connection conn = DataBaseConnection.javaConnection();
		type="close";
		if(schoolId.equalsIgnoreCase("all"))
		{
			list=new DatabaseMethods1().allTicketByStatus("close",conn);
		}
		else
		{
			list=new DatabaseMethods1().allTicketByStatus("close",schoolId,conn);
		}

		showStatus=false;
		showWrap=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkResolve()
	{
		if(selectedTicket.getType().equalsIgnoreCase("error"))
		{
			remark="Hello User,\nThe issue has been resolved, Please check.\nSorry for the incovenience caused.\nRegards,\nTeam ChalkBox";
		}
		else if(selectedTicket.getType().equalsIgnoreCase("suggestion"))
		{
			remark="Hello User,\nYour suggestion is valuable to us. It has been implemented and shall reflect in the next update. "
					+ "Please keep sharing your views and helping us in making ChalkBox the best.\nRegards,\nTeam ChalkBox";
		}
		else
		{
			remark = "";
		}
	}

	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().allTicket(selectedType,conn);
		if(list.size()>0)
		{
			check=true;
			if(selectedType.equals("error"))
			{
				errorStatus=true;
				suggestionStatus=false;
			}
			else
			{
				suggestionStatus=true;
				errorStatus=false;
			}
		}
		else
		{
			check=false;

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public boolean isShowSchoolid() {
		return showSchoolid;
	}

	public void setShowSchoolid(boolean showSchoolid) {
		this.showSchoolid = showSchoolid;
	}

	public void viewDetail() throws IOException
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedTicket", selectedTicket);

		//FacesContext.getCurrentInstance().getExternalContext().redirect("ticketCommenting.xhtml");
		PrimeFaces.current().executeInitScript("window.open('ticketCommenting.xhtml')");
	}

	public void updateresolved()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		i=obj.updateTicketRaisingRemark(userid, selectedTicket.getSchid(), selectedTicket.getId(), remark, usertype, "Resolved", conn);
		//	i=obj.updateDetailsOfTicket(selectedTicket.getId(),name,remark,status,resoltionDate,selectedTicket.getSchid(), conn);
		if(i>=1)
		{
			obj.updateStatusForticket(selectedTicket.getId(), "Resolved",conn);
			if(selectedTicket.getUsertype().equalsIgnoreCase("admin"))
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Resolved","1 new message received regarding this ticket","admin-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			else
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Resolved","1 new message received regarding this ticket","staff"+"-"+selectedTicket.getEmpid()+"-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfully"));
			name="";remark="";resoltionDate=new Date();
			//list=obj.allTicket(selectedType,conn);
			initial();
			if(schoolId.equalsIgnoreCase("all"))
			{
				list=new DatabaseMethods1().allTicketByStatus(type,conn);
			}
			else
			{
				list=new DatabaseMethods1().allTicketByStatus(type,schoolId,conn);
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public void updateInProcess()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		i=obj.updateTicketRaisingRemark(userid, selectedTicket.getSchid(), selectedTicket.getId(), remark1, usertype, "In Process", conn);
		//	i=obj.updateDetailsOfTicket(selectedTicket.getId(),name,remark,status,resoltionDate,selectedTicket.getSchid(), conn);
		if(i>=1)
		{
			obj.updateStatusForticket(selectedTicket.getId(),"In Process",conn);
			if(selectedTicket.getUsertype().equalsIgnoreCase("admin"))
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - In Process","1 new message received regarding this ticket","admin-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			else
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - In Process","1 new message received regarding this ticket","staff"+"-"+selectedTicket.getEmpid()+"-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfully"));
			name="";remark="";resoltionDate=new Date();
			//list=obj.allTicket(selectedType,conn);
			initial();
			if(schoolId.equalsIgnoreCase("all"))
			{
				list=new DatabaseMethods1().allTicketByStatus(type,conn);
			}
			else
			{
				list=new DatabaseMethods1().allTicketByStatus(type,schoolId,conn);
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateNotApp()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		i=obj.updateTicketRaisingRemark(userid, selectedTicket.getSchid(), selectedTicket.getId(), remark2, usertype, "Not Appropriate", conn);
		//	i=obj.updateDetailsOfTicket(selectedTicket.getId(),name,remark,status,resoltionDate,selectedTicket.getSchid(), conn);
		if(i>=1)
		{
			obj.updateStatusForticket(selectedTicket.getId(),"Not Appropriate",conn);
			if(selectedTicket.getUsertype().equalsIgnoreCase("admin"))
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Not Appropriate","1 new message received regarding this ticket","admin-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			else
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Not Appropriate","1 new message received regarding this ticket","staff"+"-"+selectedTicket.getEmpid()+"-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfully"));
			name="";remark="";resoltionDate=new Date();
			//list=obj.allTicket(selectedType,conn);
			initial();
			if(schoolId.equalsIgnoreCase("all"))
			{
				list=new DatabaseMethods1().allTicketByStatus(type,conn);
			}
			else
			{
				list=new DatabaseMethods1().allTicketByStatus(type,schoolId,conn);
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void updateconvert()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		i=obj.updateTicketRaisingRemark(userid, selectedTicket.getSchid(), selectedTicket.getId(), remark3, usertype, "pending", conn);
		//	i=obj.updateDetailsOfTicket(selectedTicket.getId(),name,remark,status,resoltionDate,selectedTicket.getSchid(), conn);
		if(i>=1)
		{
			obj.updateStatusForticket(selectedTicket.getId(),"pending",conn);
			if(selectedTicket.getUsertype().equalsIgnoreCase("admin"))
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Pending","1 new message received regarding this ticket","admin-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			else
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Pending","1 new message received regarding this ticket","staff"+"-"+selectedTicket.getEmpid()+"-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfully"));
			name="";remark3="";resoltionDate=new Date();
			//list=obj.allTicket(selectedType,conn);
			initial();
			if(schoolId.equalsIgnoreCase("all"))
			{
				list=new DatabaseMethods1().allTicketByStatus(type,conn);
			}
			else
			{
				list=new DatabaseMethods1().allTicketByStatus(type,schoolId,conn);
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void updateclosed()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		i=obj.updateTicketRaisingRemark(userid, selectedTicket.getSchid(), selectedTicket.getId(), remark4, usertype, "close", conn);
		//	i=obj.updateDetailsOfTicket(selectedTicket.getId(),name,remark,status,resoltionDate,selectedTicket.getSchid(), conn);
		if(i>=1)
		{
			obj.updateStatusForticket(selectedTicket.getId(),"close",conn);
			if(selectedTicket.getUsertype().equalsIgnoreCase("admin"))
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Closed","1 new message received regarding this ticket","admin-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			else
			{
				DBJ.adminnotification("Ticket : "+selectedTicket.getTicketNo()+" - Closed","1 new message received regarding this ticket","staff"+"-"+selectedTicket.getEmpid()+"-"+selectedTicket.getSchid(),selectedTicket.getSchid(),"Ticket-"+selectedTicket.getTicketNo(),conn);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfully"));
			name="";resoltionDate=new Date();
			//list=obj.allTicket(selectedType,conn);
			initial();
			if(schoolId.equalsIgnoreCase("all"))
			{
				list=new DatabaseMethods1().allTicketByStatus(type,conn);
			}
			else
			{
				list=new DatabaseMethods1().allTicketByStatus(type,schoolId,conn);
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void pending()
	{
		status="pending";
	}
	public String resolved()
	{
		Connection conn=DataBaseConnection.javaConnection();

		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.updateStatusForticket(selectedTicket.getId(),"close",conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ticket close Successfully"));

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur try Again"));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}


		return "viewAllTicket.xhtml";
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getCount3() {
		return count3;
	}

	public void setCount3(int count3) {
		this.count3 = count3;
	}

	public int getCount4() {
		return count4;
	}

	public void setCount4(int count4) {
		this.count4 = count4;
	}

	public int getCount5() {
		return count5;
	}


	public void implemented()
	{
		status="implemented";
	}
	public void noted()
	{
		status="noted";
	}
	public boolean isShowStatus() {
		return showStatus;
	}

	public void setShowStatus(boolean showStatus) {
		this.showStatus = showStatus;
	}

	public String getSelectedType() {
		return selectedType;
	}
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public boolean isErrorStatus() {
		return errorStatus;
	}
	public void setErrorStatus(boolean errorStatus) {
		this.errorStatus = errorStatus;
	}
	public boolean isSuggestionStatus() {
		return suggestionStatus;
	}
	public void setSuggestionStatus(boolean suggestionStatus) {
		this.suggestionStatus = suggestionStatus;
	}
	public ArrayList<TicketRaisingInfo> getList() {
		return list;
	}
	public void setList(ArrayList<TicketRaisingInfo> list) {
		this.list = list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public TicketRaisingInfo getSelectedTicket() {
		return selectedTicket;
	}
	public void setSelectedTicket(TicketRaisingInfo selectedTicket) {
		this.selectedTicket = selectedTicket;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getResoltionDate() {
		return resoltionDate;
	}
	public void setResoltionDate(Date resoltionDate) {
		this.resoltionDate = resoltionDate;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public String getRemark2() {
		return remark2;
	}
	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsertype() {
		return usertype;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public int getCount6() {
		return count6;
	}

	public void setCount6(int count6) {
		this.count6 = count6;
	}

	public int getCount7() {
		return count7;
	}

	public void setCount7(int count7) {
		this.count7 = count7;
	}

	public boolean isShowWrap() {
		return showWrap;
	}

	public void setShowWrap(boolean showWrap) {
		this.showWrap = showWrap;
	}

	public String getRemark3() {
		return remark3;
	}

	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}

	public String getRemark4() {
		return remark4;
	}

	public void setRemark4(String remark4) {
		this.remark4 = remark4;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
