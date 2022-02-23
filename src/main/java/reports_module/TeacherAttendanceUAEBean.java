package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmpInfo;

@ManagedBean (name="teacherAttendanceUAE")
@ViewScoped
public class TeacherAttendanceUAEBean implements Serializable{

	private static final long serialVersionUID = 1L;
	String selectedEmployee,username,schoolid,type,defaultAttend,insts="",outsts="",session;
	boolean renderShowTable=true,showStudentRecord;
	Date selectedDay=new Date();
	ArrayList<EmpInfo> teacherDetails;
	int i,j,k,l;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();

	public TeacherAttendanceUAEBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		selectedDay = new Date();
		
		try
		{	
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			username=(String) ss.getAttribute("username");
			schoolid=(String) ss.getAttribute("schoolid");
			type=(String) ss.getAttribute("type");
			session=obj.selectedSessionDetails(schoolid, conn);
			
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
	
	public void chckforstudentatndnce() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		String status = "", newin = "", newout = "",oldin = "", oldout = "";
		for(EmpInfo tt:teacherDetails)
		{
			if(tt.isInsert())
			{
				newin="yes";
			}
			else
			{
				newin="no";
			}
			
			if(tt.isOut())
			{
				newout="yes";
			}
			else
			{
				newout="no";
			}
			//i= dbr.checkEntry(conn,tt,selectedDay, obj.schoolId());
			status = dbr.checkStatus(conn,selectedDay,tt,schoolid);
			if(status.equals(""))
			{
				insertAction(tt, conn);
			}
			else 
			{
				String arr[]=status.split("/");
				oldin=arr[0];
				oldout=arr[1];
				
				if(!newin.equalsIgnoreCase(oldin))
				{
					inUpdate(tt, conn);
				}
				
				if(!newout.equalsIgnoreCase(oldout))
				{
					outUpdate(tt, conn);
				}
			}
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance Saved", "Alert"));
		
		studentDetail();
		
		try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void insertAction(EmpInfo tt, Connection conn)
	{
		if(tt.isInsert()==true)
		{
			insts="yes";
		}
		else 
		{
			insts="no";	
		}
		
		if(tt.isOut()==true)
		{
			outsts="yes";
		}
		else 
		{
			outsts="no";
		}
		
		if(insts.equalsIgnoreCase("yes") || outsts.equalsIgnoreCase("yes"))
		{
			int j = dbr.submitEntry(conn,tt,selectedDay,insts,outsts,schoolid);
			/*if(j!=0){
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance Saved", "Alert"));
	        }
	        else{
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
	        }*/
		}
	}
	
	public void inUpdate(EmpInfo tt, Connection conn)
	{
		if(tt.isInsert()==true)
		{
			insts="yes";
		}
		else 
		{
			insts="no";	
		}
		int j = dbr.updateEntry(conn,tt,selectedDay,insts, "in", schoolid);
		/*if(j!=0){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance Saved", "Alert"));
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
        }*/
	}

	public void outUpdate(EmpInfo tt, Connection conn)
	{
		if(tt.isOut()==true)
		{
			outsts="yes";
		}
		else 
		{
			outsts="no";
		}
		int k = dbr.updateEntry(conn,tt,selectedDay,outsts,"out", schoolid);
		/*if(k!=0){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance Saved", "Alert"));
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
        }*/
	}
	/*
	public void doAction(EmpInfo tt, Connection conn)
	{
		if(i.equalsIgnoreCase("true"))
		{
			
		}
		else
		{
			
		}
	}*/
	
	/*
	public void checkDefaultAttendance()
	{
		for(EmpInfo stu : teacherDetails)
		{
			stu.setAttendance(defaultAttend);
		}
	}
			
	public void saveNow()
    {          
        DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
        Connection conn = DataBaseConnection.javaConnection();
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDay);
        //boolean check=DBJ.checkHolidayForAttendanceJSON(strDate, conn,new DatabaseMethods1().schoolId());
        if(check==false)
        {
             int i=DatabaseMethods1.teacherAttendanceSection(teacherDetails, selectedDay,conn);
             if(i!=0){
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
        }
        else
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Attendance cannot be marked on holidays.", "Validation error"));
        }
      
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
	}*/
	
	public void studentDetail()
	{
		
		Connection conn = DataBaseConnection.javaConnection();
		teacherDetails=obj.allEmpInfo(conn);
		if(teacherDetails.size()>0)
		{
			renderShowTable=true;
			String in = "",intime = "", out="", outtime="";
			for(EmpInfo tt:teacherDetails)
			{
				String status = dbr.checkStatus(conn,selectedDay,tt,schoolid);
				if(status.equals(""))
				{
					tt.setInsert(false);
					tt.setOut(false);
					tt.setInTime(" ");
					tt.setOutTime(" ");
				}
				else if(!status.equals(""))
				{
					String arr[]=status.split("/");
					in=arr[0];
					out=arr[1];
					intime=arr[2];
					outtime=arr[3];
					
					tt.setInTime(intime);
					tt.setOutTime(outtime);
					
					if(in.equalsIgnoreCase("yes"))
					{
						tt.setInsert(true);
					}
					else
					{
						tt.setInsert(false);
					}
					
					if(out.equalsIgnoreCase("yes"))
					{
						tt.setOut(true);
					}
					else
					{
						tt.setOut(false); 
					}
				}
			}
		}
		else
		{
			renderShowTable=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Employee Found! Please add employee(s) then try again.", "Validation error"));
		}
		//String strDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDay);
		try
		{								
				
				//teacherDetails=DatabaseMethods1.allAttendanceForTeacher(selectedDay, teacherDetails,new DatabaseMethods1().schoolId(),conn);
				showStudentRecord=true;
			
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

}
