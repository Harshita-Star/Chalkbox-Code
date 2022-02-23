package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;

@ManagedBean(name="refundFee")
@ViewScoped
public class RefundFeeBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo> studentList,selectedStudentList;
	boolean show;
	ArrayList<SelectItem> classSection,sectionList;
	String selectedSection,selectedClassSection,name;
	String session="",schid="";
	Date date=new Date();
	DatabaseMethods1 obj= new DatabaseMethods1();
	public RefundFeeBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classSection=new DatabaseMethods1().allClass(conn);
		session=DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		schid=obj.schoolId();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		List<String> studentListt=new ArrayList<>();

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

	public String searchStudentByName()
	{
		Connection conn = DataBaseConnection.javaConnection();

		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					studentList=new ArrayList<>();
					studentList.add(info);
					obj.addCautionFeeDetail(studentList,schid,session,conn);
					show=true;
					break;
				}

			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		return "";
	}


	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new ArrayList<>();
		////// // System.out.println(selectedSection+"........."+schid+"......"+session);
		studentList=new DatabaseMethods1().searchStudentListByClassSection(selectedClassSection,selectedSection,conn);
		obj.addCautionFeeDetail(studentList,schid,session,conn);

		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void payRefundFee()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=0,k=0;
		for(StudentInfo info:studentList)
		{
			if(info.isCheck()==true)
			{
				k++;
				if(info.getPaidFee()>0 )
				{
					if(info.getLeftFee()>0)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Pay Complete Fee First FOR "+info.getFname()));
						break;
					}
					else
					{
						if(Double.parseDouble(info.getFeeamount())> (info.getPaidFee()))
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Refunded Amount Can Not Be Greater Than Paid Amount FOR "+info.getFname()));
							break;
						}
						else
						{
							i=obj.refundFee(info,schid,session,date,conn);
						}
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fee Is Not Paid Yet For "+info.getFname()+" .... So Can Not Be Refunded"));
					break;
				}
			}
		}
		if(k>0)
		{
			if(i>0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Refunded Sucessfully"));
				name=selectedClassSection=selectedSection="";
				studentList=new ArrayList<>();
				selectedStudentList=new ArrayList<>();show=false;

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occurred... Please Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At least One Student"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSelectedClassSection() {
		return selectedClassSection;
	}
	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public String getName()
	{
		return name;
	}

	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	 
}
