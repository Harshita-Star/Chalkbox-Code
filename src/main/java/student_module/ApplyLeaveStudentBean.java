package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean(name="applyLeaveStduentBean")
@ViewScoped
public class ApplyLeaveStudentBean implements Serializable {

	String regex=RegexPattern.REGEX;
	String desc,schid;
	Date startDate,endDate;
	Date startminDate,endMinDate;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ApplyLeaveStudentBean() {

		Connection conn = DataBaseConnection.javaConnection();
		schid = dbm.schoolId();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String currentdate=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		currentdate=currentdate+" 10:00 am";
		Date cdDATE=null;
		try {
			cdDATE = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(currentdate);
		} catch (ParseException e1) {
			
			e1.printStackTrace();
		}

		if(cdDATE.before(new Date()))
		{
			startDate=new Date();
			startminDate=new Date();
			endDate=new Date();
			endMinDate=new Date();
			startDate.setDate(new Date().getDate()+1);
			startminDate.setDate(new Date().getDate()+1);
			endDate.setDate(new Date().getDate()+1);
			endMinDate.setDate(new Date().getDate()+1);

		}
		else
		{
			startDate=new Date();
			startminDate=new Date();
			endDate=new Date();
			endMinDate=new Date();

		}


	}


	public void changeDate()
	{
		if(endDate.before(startDate))
		{
			endDate=startDate;

		}

		endMinDate=startDate;

	}


	public String studentLeaveApply() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schoolId = (String) ss.getAttribute("schoolid");
		String username=(String) ss.getAttribute("username");
		Date newDate1=new Date();
		Date endDate1=new Date();
		String start_date=new SimpleDateFormat("dd/MM/yyyy").format(startDate);
		String end_date=new SimpleDateFormat("dd/MM/yyyy").format(endDate);
		newDate1= new SimpleDateFormat("dd/MM/yyyy").parse(start_date);
		endDate1=new SimpleDateFormat("dd/MM/yyyy").parse(end_date);
		String strDate="";
		try {
			boolean check = false;
			boolean checkAlready = false;

			hloop:for(Date d = newDate1;(d.before(endDate1) || d.equals(endDate1));d.setDate(d.getDate()+1))
			{
				strDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
				check=DBJ.checkSchoolHolidayForAttendanceJSON(strDate, conn,schoolId);
				checkAlready=DBJ.checkAlreadyAppliedLeave(strDate,username,schoolId,conn);
				if(check)
				{
					break hloop;
				}

				if(checkAlready)
				{
					break hloop;
				}
			}

			if(check)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You cannot apply leave for these date(s) as there is a holiday on "+strDate));
			}
			else if(checkAlready)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You have already applied leave for "+strDate));
			}
			else
			{


				int i=DBJ.AppStudentLeave(username,desc,startDate,endDate,schoolId,conn);
				////// // System.out.println(i);
				if(i>0)
				{

					StudentInfo ln=DBJ.studentDetailslistByAddNo(username,schoolId,conn);
					String clsTchr = DBJ.classTeacherBySection1(ln.getSectionid(), schoolId, conn);

					DBJ.adminnotification("Leave",ln.getFname()+" Applied For Leave from "+new SimpleDateFormat("dd/MM/yyyy").format(startDate)+"-"+new SimpleDateFormat("dd/MM/yyyy").format(startDate),"admin-"+schoolId,schoolId,"LeaveApply-"+username,conn);
					if(!clsTchr.equalsIgnoreCase("no"))
					{
						
						String[] teacher=clsTchr.split(",");
						for(int ii=0;ii<teacher.length;ii++)
						{
							DBJ.adminnotification("Leave",ln.getFname()+" Applied For Leave from "+start_date+"-"+end_date,"staff"+"-"+teacher[ii]+"-"+schoolId,schoolId,"LeaveApply-"+username,conn);
										
						}
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Leave Apply Sucessfully"));
					return "studentLeave";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
					return 	"";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		return "";
	}

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartminDate() {
		return startminDate;
	}
	public void setStartminDate(Date startminDate) {
		this.startminDate = startminDate;
	}
	public Date getEndMinDate() {
		return endMinDate;
	}
	public void setEndMinDate(Date endMinDate) {
		this.endMinDate = endMinDate;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
