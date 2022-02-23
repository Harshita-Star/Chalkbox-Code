package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import student_module.RegistrationColumnName;

@ManagedBean(name="wrongContactReport")
@ViewScoped
public class WrongContactNoReportBean  implements Serializable
{
	ArrayList<StudentInfo> studentList;
	String contactNo,schid,session;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<String> allContacts = new ArrayList<String>();
	ArrayList<String> selectedContacts = new ArrayList<String>();	
	String contact1="",contact2="",contact3="";
	DataBaseMethodsReports objReport=new DataBaseMethodsReports();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();

	
	public WrongContactNoReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		contactNo = obj.checkAllWrongMobileno(conn);
		searchData(conn);
	    String[] contacts = contactNo.split(","); 
	    for(int i=0;i<contacts.length;i++)
	    {
	    	allContacts.add(contacts[i].substring(1, contacts[i].length()-1));
	    	selectedContacts.add(contacts[i].substring(1, contacts[i].length()-1));
	    }
  		
		
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void searchData(Connection conn)
	{
		ArrayList<String> stdColumnList=new ArrayList<>();
		stdColumnList.add(RegistrationColumnName.ADMISSION_NUMBER);
		stdColumnList.add(RegistrationColumnName.SERIAL_NUMBER);
		stdColumnList.add(RegistrationColumnName.STUDENT_NAME);
		stdColumnList.add(RegistrationColumnName.FATHERS_PHONE);
		stdColumnList.add(RegistrationColumnName.FATHERS_NAME);
		stdColumnList.add(RegistrationColumnName.SECTION_ID);
		studentList=objStd.studentDetail(contactNo, "","", QueryConstants.WRONG_CONTACT_NO_WPS,  QueryConstants.PROMOTION_TABLE,null,null,"","","","",session, schid,stdColumnList, conn);
		
	}
	
	public void filterReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		contactNo = "";
		for(int h=0;h<selectedContacts.size();h++)
		{
			contactNo += "'"+selectedContacts.get(h)+"',"; 
		}
		if(!(selectedContacts.size() ==0))
		{
			contactNo = contactNo.substring(0,contactNo.length()-1);
		}
		
		if(!contact1.equalsIgnoreCase(""))
		{
			contactNo += ",'"+contact1+"'";
		}
		if(!contact2.equalsIgnoreCase(""))
		{
			contactNo += ",'"+contact2+"'";
		}
		if(!contact3.equalsIgnoreCase(""))
		{
			contactNo += ",'"+contact3+"'";
		}
		
		searchData(conn);
		//studentList=objReport.wrongContactNoReport(contactNo,schid,session,conn);
	
  		
		
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<String> getAllContacts() {
		return allContacts;
	}

	public void setAllContacts(ArrayList<String> allContacts) {
		this.allContacts = allContacts;
	}

	public ArrayList<String> getSelectedContacts() {
		return selectedContacts;
	}

	public void setSelectedContacts(ArrayList<String> selectedContacts) {
		this.selectedContacts = selectedContacts;
	}

	public String getContact1() {
		return contact1;
	}

	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	public String getContact2() {
		return contact2;
	}

	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}

	public String getContact3() {
		return contact3;
	}

	public void setContact3(String contact3) {
		this.contact3 = contact3;
	}
	
}
