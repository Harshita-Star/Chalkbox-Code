package tc_module;

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

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean(name = "struckOffStudent")
@ViewScoped
public class StruckOffStudentBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	StudentInfo selectedStudent;
	String name,reason,lastClass,text,perform,schid,session,selectedClass,selectedSection;
	ArrayList<SelectItem> classSection,sectionList;
	ArrayList<StudentInfo> studentList,list,selectedList;
	boolean show,showTextBox,showMultiple;
	Date date2;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsTcModule objTc=new DataBaseMethodsTcModule();
	DataBaseMeathodJson objJson = new DataBaseMeathodJson();
	ArrayList<String> reasons = new ArrayList<>();


	public StruckOffStudentBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		
		classSection=obj.allClass(conn);
		getReasons();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getReasons() {
		reasons = new ArrayList<>();
		if(schid.equals("313") || schid.equals("315")) {
			reasons.add("Due to long absent".toUpperCase());
			reasons.add("PARENT’S DESIRE".toUpperCase());
			reasons.add("FURTHER STUDIES".toUpperCase());
			reasons.add("To study else where".toUpperCase());
			reasons.add("To study for higher classes".toUpperCase());
			reasons.add("Parents transfer to other city".toUpperCase());
			reasons.add("Transfer of father".toUpperCase());
			reasons.add("Others".toUpperCase());
		}else {
			reasons.add("Due to long absent");
			reasons.add("PARENT'S DESIRE");
			reasons.add("FURTHER STUDIES");
			reasons.add("To study else where");
			reasons.add("To study for higher classes");
			reasons.add("Parents transfer to other city");
			reasons.add("Others");
		}
		return reasons;
	}
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			list=obj.searchStudentListByClassSection(selectedClass,selectedSection,conn);
			if(list.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			}
			else
			{
				show=false;
				showMultiple=true;
			}
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

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList = obj.searchStudentList(query, conn);
		List<String> studentListt = new ArrayList<>();
		for (StudentInfo info : studentList) {
			studentListt.add(info.getFname() + " / " + info.getFathersName() + " / " + info.getSrNo() + "-"
					+ info.getClassName() + "-" + info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudentByName() {
		Connection conn = DataBaseConnection.javaConnection();
		int index = name.lastIndexOf("-") + 1;
		String id = name.substring(index);
		if (index != 0)
		{

			for (StudentInfo info : studentList)

			
				if (String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						selectedStudent=info;
						lastClass=selectedStudent.getClassName();
						show = true;
						showMultiple = false;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void struckOffStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();

		String id=String.valueOf(selectedStudent.getAddNumber());
		String classId=selectedStudent.getSectionid();
		
		int i=objTc.addTCInformation(reason, date2, lastClass,id,perform,text,classId,schid,session,conn);
		if(i==1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			obj.blockUser(selectedStudent.getAddNumber(),"StruckOff",conn);
			
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			new DataBaseMeathodJson().deleteStudentFromCommGroup(selectedStudent.getAddNumber(), "student", schid, conn);
		
			String refNo3;
			try {
				refNo3=addWorkLog3(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Struck Off Successfully"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
		}

		date2=null;selectedStudent=null;
		lastClass=reason=perform= text=classId=name="";
		show=false;
		showMultiple = false;
		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
	
		language = "StudentId - "+selectedStudent.getAddNumber()+" --- Reason -"+reason+" --- Date - "+date2+" --- Last Class - "+lastClass+" --- performance - "+perform; 
		
		value = "StudentId - "+selectedStudent.getAddNumber()+" --- Reason -"+reason+" --- Date - "+date2+" --- Last Class - "+lastClass+" --- performance - "+perform+" --- ClassId -"+selectedStudent.getSectionid(); 

		
		String refNo = workLg.saveWorkLogMehod(language,"Struck off Student addTCInfo","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
	
		language = "StudentId - "+selectedStudent.getAddNumber();
		
		value = "StudentId - "+selectedStudent.getAddNumber(); 

		
		String refNo = workLg.saveWorkLogMehod(language,"Struck off Student BlockUser","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
	
		language = "StudentId - "+selectedStudent.getAddNumber();
		
		value = "StudentId - "+selectedStudent.getAddNumber(); 

		
		String refNo = workLg.saveWorkLogMehod(language,"Struck off Student DeleteFromCommonGroup","WEB",value,conn);
		return refNo;
	}
	
	public void struckOffMultiple()
	{
		if(selectedList.size()>0)
		{
			Connection conn=DataBaseConnection.javaConnection();
			//String id=String.valueOf(selectedStudent.getAddNumber());
			//String classId=selectedStudent.getSectionid();
			int i = 0,x=0;
			for(StudentInfo ss : selectedList)
			{
			
				if(ss.getReason().equals("") || ss.getLeavingDate()==null || ss.getPerformance().equals(""))
				{
					//No Entry
				}
				else
				{
					if(!ss.getReason().equalsIgnoreCase("others"))
					{
						ss.setOtherReason("");
						i=objTc.addTCInformation(ss.getReason(), ss.getLeavingDate(), ss.getClassName(), 
								ss.getAddNumber(),ss.getPerformance(),ss.getOtherReason(),ss.getSectionid(),schid,session,conn);
						if(i>=1)
						{
							x=x+1;
							obj.blockUser(ss.getAddNumber(),"StruckOff",conn);
							objJson.deleteStudentFromCommGroup(ss.getAddNumber(), "student", schid, conn);
						}
					}
					else
					{
						if(ss.getOtherReason().equals(""))
						{
							//No Entry
						}
						else
						{
							i=objTc.addTCInformation(ss.getReason(), ss.getLeavingDate(), ss.getClassName(), 
									ss.getAddNumber(),ss.getPerformance(),ss.getOtherReason(),ss.getSectionid(),schid,session,conn);
							if(i>=1)
							{
								x=x+1;
								obj.blockUser(ss.getAddNumber(),"StruckOff",conn);
								objJson.deleteStudentFromCommGroup(ss.getAddNumber(), "student", schid, conn);
							}
						}
					}
				}
			}
			
			if(x>=1)
			{
				String refNo4;
				try {
					refNo4=addWorkLog4(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selected Students : "+selectedList.size()+" Students Struck Off : "+x));

				date2=null;selectedStudent=null;
				lastClass=reason=perform= text=name="";
				show=false;
				showMultiple = false;
				selectedList = new ArrayList<>();
				list = new ArrayList<>();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill Mandatory Fields Properly"));
			}

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student to struck off"));
		}
		
	}
	
	public String addWorkLog4(Connection conn)
	{
	
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
	    String value = "";
		String language= "Class - "+className+" --- Section -"+sectionname;
	
		for(StudentInfo ss : selectedList)
		{
			if(ss.getReason().equals("") || ss.getLeavingDate()==null || ss.getPerformance().equals(""))
			{
				//No Entry
			}
			else
			{
				if(!ss.getReason().equalsIgnoreCase("others"))
				{
					ss.setOtherReason("");
					
					value += "(StudentId - "+ss.getAddNumber()+" --- Reason -"+ss.getReason()+" --- Date - "+ss.getLeavingDate()+" --- Last Class - "+ss.getClassName()+" --- performance - "+ss.getPerformance()+" --- ClassId -"+ss.getSectionid()+")"; 
				
				}
			}
		}	
		
		String refNo = workLg.saveWorkLogMehod(language,"Struck off Student Multiple","WEB",value,conn);
		return refNo;
	}
	

	public void checkReason()
	{
		if(reason.equalsIgnoreCase("others"))
		{
			showTextBox=true;
		}
		else
		{
			showTextBox=false;
		}
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLastClass() {
		return lastClass;
	}

	public void setLastClass(String lastClass) {
		this.lastClass = lastClass;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPerform() {
		return perform;
	}

	public void setPerform(String perform) {
		this.perform = perform;
	}

	public boolean isShowTextBox() {
		return showTextBox;
	}

	public void setShowTextBox(boolean showTextBox) {
		this.showTextBox = showTextBox;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public boolean isShowMultiple() {
		return showMultiple;
	}

	public void setShowMultiple(boolean showMultiple) {
		this.showMultiple = showMultiple;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public ArrayList<StudentInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<StudentInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
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

	
	public void setReasons(ArrayList<String> reasons) {
		this.reasons = reasons;
	}
	
	

}
