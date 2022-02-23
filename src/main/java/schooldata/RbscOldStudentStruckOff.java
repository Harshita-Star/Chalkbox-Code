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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;
@ManagedBean(name="rbscOldStudentStruckOff")
@ViewScoped
public class RbscOldStudentStruckOff implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<TCInfo> tcDetail;
	StudentInfo studentList;
	String registerNo,addNumber,studentName,fatherName,motherName,reason,guardianName,occupation,relation,address,lastSchool,lastClass,
	lastResult;
	int j=0;
	Date admissionDate,removalDate,dob;
	ArrayList<SelectItem> sectionList=new ArrayList<>();
	public RbscOldStudentStruckOff()
	{
		tcDetail =new ArrayList<>();
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSectionListWithClass(conn);
		for(int i=1;i<=15;i++)
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
	public void checkAdmissionNumber()
	{
		Connection conn=DataBaseConnection.javaConnection();
		/*int status=new DatabaseMethods1().duplicateStudentNoEntry(addNumber,conn);
        if(status==1)
        {
            FacesContext fc=FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate SR.No "+addNumber+" found,try a different one","Duplicate SR.No "+addNumber+" found,try a different one"));
            addNumber="";
        }*/
		String status=new DatabaseMethods1().searchOldStudentRbscTransferCertificate(addNumber,conn);
		if(status!=null)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate SR.No ,Please Change..!"));
			addNumber="";
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
	public String submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		try
		{
			/*String status=DBM.searchOldStudentRbscTransferCertificate(addNumber,conn);
			if(status!=null)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("TC Already Generated"));
			}
			else
			{*/
			int i=DBM.addRbscOldStudentdetail(registerNo,addNumber,studentName,fatherName,motherName,reason,guardianName,occupation,relation,address,lastSchool,lastClass,lastResult,admissionDate,removalDate,dob,conn);
			if(i>=1)
			{
				DBM.addRbscDetail(addNumber,tcDetail,conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfully"));
				studentList=DBM.rbscStudentDetail(addNumber,conn);
				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.setAttribute("addNumber", addNumber);
				ss.setAttribute("rbscTCDetail", studentList);
				ss.setAttribute("tcType", "");
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("printRbscTc.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "rbscOldStudentStruckOff";
				/*HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.setAttribute("rbscTCDetail", selectedStudent);*/
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured..!"));
			}
			//}
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
	public ArrayList<TCInfo> getTcDetail() {
		return tcDetail;
	}
	public void setTcDetail(ArrayList<TCInfo> tcDetail) {
		this.tcDetail = tcDetail;
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
	public String getMotherName() {
		return motherName;
	}
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getGuardianName() {
		return guardianName;
	}
	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLastSchool() {
		return lastSchool;
	}
	public void setLastSchool(String lastSchool) {
		this.lastSchool = lastSchool;
	}
	public String getLastClass() {
		return lastClass;
	}
	public void setLastClass(String lastClass) {
		this.lastClass = lastClass;
	}
	public String getLastResult() {
		return lastResult;
	}
	public void setLastResult(String lastResult) {
		this.lastResult = lastResult;
	}
	public Date getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	public Date getRemovalDate() {
		return removalDate;
	}
	public void setRemovalDate(Date removalDate) {
		this.removalDate = removalDate;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public StudentInfo getStudentList() {
		return studentList;
	}
	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
