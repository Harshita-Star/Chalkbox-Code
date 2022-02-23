package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;

@ManagedBean(name="complaintDiaryBean")
@ViewScoped

public class ComplaintDiaryBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<ComplaintInfo> studentList;
	String name="",strDate="",type;
	Date complaintDate;
	ArrayList<StudentInfo> stList=new ArrayList<>();

	public ComplaintDiaryBean() {
		type = "all";
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		stList=new DatabaseMethods1().searchStudentList(query,conn);
		List<String> studentListt=new ArrayList<>();
		for(StudentInfo info : stList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public String searchComplaint()
	{
		strDate="";
		String addNumber="";
		if(complaintDate!=null)
		{
			strDate = new SimpleDateFormat("yyyy-MM-dd").format(complaintDate);
		}

		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : stList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					stList=new ArrayList<>();
					addNumber=id;
					break;
					//selectedStudent=info;
					/*achievement();
					return "printFinalMarksheet";*/
				}
			}
		}


		if(addNumber.equals("") && strDate.equals(""))
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Note: Please Search By Student Name or Complaint Date or Both"));
			return "";
		}
		else
		{
			Connection conn = DataBaseConnection.javaConnection();
			studentList = new DatabaseMethods1().studentComplaintDiary(addNumber, strDate,type, conn);
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "";

	}

	public ArrayList<ComplaintInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<ComplaintInfo> studentList) {
		this.studentList = studentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public Date getComplaintDate() {
		return complaintDate;
	}

	public void setComplaintDate(Date complaintDate) {
		this.complaintDate = complaintDate;
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
