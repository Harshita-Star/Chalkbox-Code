package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;

@ManagedBean(name="addPreviousFee")
@ViewScoped
public class AddPreviousFeeBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String studentName,previousFee;
	ArrayList<StudentInfo> studentList;

	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void addPreviousFees()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int index=studentName.lastIndexOf(":")+1;
		String id=studentName.substring(index);
		boolean check=obj.checkDuplicate(id,conn);
		if(check==true)
		{
		
			double i=new DatabaseMethods1().previousFeeByAddNumber(id, conn);
			if(i==0)
			{
			  int addPrevioscheck = new DatabaseMethods1().updatePreviousFees(id, Double.parseDouble(previousFee),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), conn);
				

				if (addPrevioscheck > 0) {
					
					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Update Fees Successfully "));
				
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("some Error OccurPlease Try again"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,choose a different Student Name", "Validation error"));
							
			}
		}
		else
		{
			int i=obj.addStudentFees(id,previousFee,DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),conn);
			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null,new FacesMessage("Previous Session Due Fee Add Successfully"));
				studentName="";
				previousFee="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"some Error OccurPlease Try again", "Validation error"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
	
		int index=studentName.lastIndexOf(":")+1;
		String id=studentName.substring(index);
		
		language = value = "StudentId -"+id+" -- Fee -"+previousFee;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Update Previous Fee","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
	
		int index=studentName.lastIndexOf(":")+1;
		String id=studentName.substring(index);
		
		language = value = "StudentId -"+id+" -- Fee -"+previousFee;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Previous Fee","WEB",value,conn);
		return refNo;
	}

	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getPreviousFee() {
		return previousFee;
	}
	public void setPreviousFee(String previousFee) {
		this.previousFee = previousFee;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
