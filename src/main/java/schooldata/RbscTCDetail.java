package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;
@ManagedBean(name="rbscTCDetail")
@ViewScoped
public class RbscTCDetail implements Serializable
{
	String regex=RegexPattern.REGEX;
	StudentInfo studentList;
	String registerNo,addNumber,studentName,fatherName,srNo;
	ArrayList<TCInfo> tcDetail;
	int j=0;
	ArrayList<SelectItem> sectionList=new ArrayList<>();
	public RbscTCDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();


		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList=(StudentInfo) ss.getAttribute("rbscTCDetail");
		sectionList=obj.allSectionListWithClass(conn);
		addNumber=studentList.getAddNumber();
		studentName=studentList.getFname().toUpperCase();
		fatherName=studentList.getFathersName().toUpperCase();
		srNo=studentList.getSrNo();
		tcDetail=obj.rbscTCDetail(addNumber,conn);
		//tcDetail =new ArrayList<>();
		for(int i=(tcDetail.size()+1);i<=15;i++)
		{
			TCInfo in=new TCInfo();
			in.setsNo(String.valueOf(i));
			tcDetail.add(in);
			j=i;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void addNewRow()
	{
		int k=j;
		for(int i=k+1;i==k+1;k++)
		{
			TCInfo in=new TCInfo();
			in.setsNo(String.valueOf(i));
			tcDetail.add(in);
			j=i;
		}
	}
	public void checkMeeting()
	{
		FacesContext context=FacesContext.getCurrentInstance();
		int flag=0;
		String selectedRow= (String) UIComponent.getCurrentComponent(context).getAttributes().get("sNo");
		for(TCInfo ll:tcDetail)
		{
			if(ll.getsNo().equals(selectedRow))
			{
				
				if((ll.getDoa()==null)||(ll.getDop()==null))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill Date of Admission and Passing First"));
				}
				else
				{	
				 int doaYear=ll.getDoa().getYear()+1900;
				 int dopYear=ll.getDop().getYear()+1900;	
				 ll.setSession(doaYear+"-"+dopYear);
				 for(TCInfo info:tcDetail)
				 {
					if(info.getsNo().equals(selectedRow) && (info.getStudentMeeting()!=null && !info.getStudentMeeting().equals("")))
					{
						if(Integer.parseInt(info.getSchoolMeeting())<Integer.parseInt(info.getStudentMeeting()))
						{
							flag=1;
							break;
						}
					}

				 }
				 if(flag==1)
				 {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Meeting Can't be Greater Than School Meeting"));
					ll.setStudentMeeting("");
				 }
				} 
			}
		}
	}
	public String submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{
			obj.deleteTCDetail(addNumber,conn);
			int i=obj.addRbscDetail(addNumber,tcDetail,conn);
			if(i>=1)
			{
				obj.updateRbscTCStatus(addNumber, conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfully"));
				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.setAttribute("addNumber", addNumber);
				ss.setAttribute("tcType", "");
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("printRbscTc.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "rbscTCDetail";
				/*HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.setAttribute("rbscTCDetail", selectedStudent);*/
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured..!"));
			}

		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	public StudentInfo getStudentList() {
		return studentList;
	}
	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}
	public String getRegisterNo() {
		return registerNo;
	}
	public void setRegisterNo(String registerNo) {
		this.registerNo = registerNo;
	}
	public String getAddNumber() {
		return addNumber;
	}
	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public ArrayList<TCInfo> getTcDetail() {
		return tcDetail;
	}
	public void setTcDetail(ArrayList<TCInfo> tcDetail) {
		this.tcDetail = tcDetail;
	}
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSrNo() {
		return srNo;
	}
	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
