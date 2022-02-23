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
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="teacherAttendBean")
@ViewScoped
public class TeacherAttendanceBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selectedEmployee,username,schoolid,type,defaultAttend;
	boolean renderShowTable=true,showStudentRecord;
	Date selectedDay=new Date();
	ArrayList<EmpInfo> teacherDetails;
	int i,j,k,l,m,n,o;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public void chckforstudentatndnce()
	{
		i=j=k=l=m=n=o=0;
		for(EmpInfo ls:teacherDetails)
		{
			String we=ls.getAttendance();
			if(we.equals("A"))
			{
				i++;
			}
			if(we.equals("P"))
			{
				j++;
			}
			if(we.equals("L"))
			{
				k++;
			}
			if(we.equals("P_H"))
			{
				l++;
			}
			if(we.equals("QL")) {
				m++;
			}
			if(we.equals("3/4L")) {
				n++;
			}
			if(we.equals("DL")) {
				o++;
			}
		}
	}

	public void checkDefaultAttendance()
	{
		for(EmpInfo stu : teacherDetails)
		{
			stu.setAttendance(defaultAttend);
		}
	}

	public TeacherAttendanceBean()
	{
		selectedDay = new Date();
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			username=(String) ss.getAttribute("username");
			schoolid=(String) ss.getAttribute("schoolid");
			type=(String) ss.getAttribute("type");

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

	public void saveNow()
	{
		new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();
		new SimpleDateFormat("yyyy-MM-dd").format(selectedDay);
		//boolean check=DBJ.checkHolidayForAttendanceJSON(strDate, conn,new DatabaseMethods1().schoolId());
		/*if(check==false)
        {*/
		int i=DatabaseMethods1.teacherAttendanceSection(teacherDetails, selectedDay,conn);
		if(i!=0){
			
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			renderShowTable=false;
			selectedDay = new Date();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance Saved", "Alert"));
		}
		else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*}
        else
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Attendance cannot be marked on holidays.", "Validation error"));
        }*/

	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String dt = formater.format(selectedDay);
		
		language = "Date-"+dt;
		
		for(EmpInfo vb: teacherDetails)
		{	
		  value += "( Id-"+vb.getId()+" --Status-"+vb.getAttendance()+")";
		}
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Teacher Attendance","WEB",value,conn);
		return refNo;
	}
	
	

	public void defineHoliday()
	{
		Connection conn = DataBaseConnection.javaConnection();
		teacherDetails=obj.allEmpInfo(conn);
		for(EmpInfo stu : teacherDetails)
		{
			stu.setAttendance("P");
		}
		showStudentRecord=true;
		renderShowTable=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentDetail()
	{
		renderShowTable=true;
		Connection conn = DataBaseConnection.javaConnection();
		new SimpleDateFormat("yyyy-MM-dd").format(selectedDay);
		try
		{
			/*boolean checkHoliday = obj.checkHolidayForAttendance(strDate,conn);
			if(checkHoliday==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Attendance on holiday cannot be marked", "Validation error"));
			}
			else
			{*/
			teacherDetails=obj.allEmpInfo(conn);
			teacherDetails=DatabaseMethods1.allAttendanceForTeacher(selectedDay, teacherDetails,new DatabaseMethods1().schoolId(),conn);
			showStudentRecord=true;
			//}

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
	public boolean isRenderShowTable() {
		return renderShowTable;
	}

	public void setRenderShowTable(boolean renderShowTable) {
		this.renderShowTable = renderShowTable;
	}

	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}

	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}

	public Date getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(Date selectedDay) {
		this.selectedDay = selectedDay;
	}
	public ArrayList<EmpInfo> getTeacherDetails() {
		return teacherDetails;
	}

	public void setTeacherDetails(ArrayList<EmpInfo> teacherDetails) {
		this.teacherDetails = teacherDetails;
	}

	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public String getDefaultAttend() {
		return defaultAttend;
	}

	public void setDefaultAttend(String defaultAttend) {
		this.defaultAttend = defaultAttend;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getO() {
		return o;
	}

	public void setO(int o) {
		this.o = o;
	}
	
}
