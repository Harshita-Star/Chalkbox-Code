package library_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;


@ManagedBean (name="bookSettings")
@ViewScoped
public class BookSettingsBean implements Serializable {

	double lateFees;
	int stdAllowDays,tchAllowDays,stdAllowQuant,tchAllowQuant;
	boolean showLateFee=false,penalty=false;
	DataBaseMethodsLibraryModule objLibrary= new DataBaseMethodsLibraryModule();
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public BookSettingsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		general();
	}

	public void general()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=obj.fullSchoolInfo(conn);

		stdAllowDays=info.getStudentAllowDays();
		stdAllowQuant=info.getStdAllowQuantity();
		tchAllowDays=info.getTeacherAllowDays();
		tchAllowQuant=info.getTeacherAllowQuantity();
		String penaltySetting=info.getPenaltySetting();

		if(penaltySetting.equalsIgnoreCase("Yes"))
		{
			penalty=true;
			showLateFee=true;
			lateFees=objLibrary.getlatefees(conn);
		}
		else
		{
			penalty=false;
			showLateFee=false;
			lateFees=0;
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkPenalty()
	{

		if(penalty)
		{
			Connection conn=DataBaseConnection.javaConnection();
			showLateFee=true;
			lateFees=objLibrary.getlatefees(conn);

			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			showLateFee=false;
			lateFees=0;
		}

	}

	public void add()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			if(stdAllowDays >0 && stdAllowQuant>0 && tchAllowDays>0 && tchAllowQuant>0)
			{
				int flag=0;
				String strPenalty="";
				if(penalty)
				{
					strPenalty="Yes";
				}
				else
				{
					strPenalty="No";
				}

				objLibrary.updateLibrarySettingInSchoolInfo(strPenalty,stdAllowDays,stdAllowQuant,tchAllowDays,tchAllowQuant,conn);
				String refNo=addWorkLog(conn);
				
				if(flag==0)
				{
					int status=objLibrary.duplicatelatefess(conn);
					if(status==0)
					{
						flag++;
						//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate entry found for late fees"));
					}
				}

				if(flag==0)
				{
					int addsettings=objLibrary.addbooksettings(lateFees,conn);
					if(addsettings>=1)
					{
						String refNo2=addWorkLog2(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Library setting successfully updated."));
						general();
					}
				}
				else
				{
					int addsettings=objLibrary.editbooksettings(lateFees,conn);
					if(addsettings>=1)
					{
						String refNo3=addWorkLog3(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Library setting successfully updated."));
						general();
					}
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Day and quantity must be greater than 0"));
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void editsettings() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editBookSettings.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		String strPenalty="";
		if(penalty)
		{
			strPenalty="Yes";
		}
		else
		{
			strPenalty="No";
		}
		
		language = stdAllowDays+" --- "+tchAllowDays+" --- "+stdAllowQuant+" --- "+tchAllowQuant+" --- "+penalty+" --- "+lateFees; 
        value = strPenalty +" --- "+stdAllowDays+" --- "+stdAllowQuant+" --- "+tchAllowDays+" --- "+tchAllowQuant;
		
		String refNo = workLg.saveWorkLogMehod(language,"Library Setting","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
	
		try {
			value = String.valueOf(lateFees);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		
		String refNo = workLg.saveWorkLogMehod(language,"Library Add book Setting","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
	
		try {
			value = String.valueOf(lateFees);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		
		String refNo = workLg.saveWorkLogMehod(language,"Library Edit book Setting","WEB",value,conn);
		return refNo;
	}


	public double getLateFees() {
		return lateFees;
	}

	public void setLateFees(double lateFees) {
		this.lateFees = lateFees;
	}

	public int getStdAllowDays() {
		return stdAllowDays;
	}

	public void setStdAllowDays(int stdAllowDays) {
		this.stdAllowDays = stdAllowDays;
	}

	public int getTchAllowDays() {
		return tchAllowDays;
	}

	public void setTchAllowDays(int tchAllowDays) {
		this.tchAllowDays = tchAllowDays;
	}

	public int getStdAllowQuant() {
		return stdAllowQuant;
	}

	public void setStdAllowQuant(int stdAllowQuant) {
		this.stdAllowQuant = stdAllowQuant;
	}

	public int getTchAllowQuant() {
		return tchAllowQuant;
	}

	public void setTchAllowQuant(int tchAllowQuant) {
		this.tchAllowQuant = tchAllowQuant;
	}

	public boolean isShowLateFee() {
		return showLateFee;
	}

	public void setShowLateFee(boolean showLateFee) {
		this.showLateFee = showLateFee;
	}

	public boolean isPenalty() {
		return penalty;
	}

	public void setPenalty(boolean penalty) {
		this.penalty = penalty;
	}

}
