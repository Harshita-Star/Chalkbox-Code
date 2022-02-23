package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import exam_module.DataBaseMethodsBLMExam;

@ManagedBean(name="printAjmaniStdFormat")
@ViewScoped
public class PrintAjmaniStdFormat implements Serializable
{
	String name,className,section,dob,addDate;
	boolean show;
	ArrayList<StudentInfo> listOfStudent;
	ArrayList<SubjectInfo> manSubList,optSubList, subjList;
	ArrayList<String> addNoList,nameList,motherNameList,fatherNameList;
	SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	String session="";
	public PrintAjmaniStdFormat()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsBLMExam objBLM= new DataBaseMethodsBLMExam();
		DatabaseMethods1 obj=new DatabaseMethods1();
		session=DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		listOfStudent=(ArrayList<StudentInfo>) ss.getAttribute("selectedStudent");
		
		for(StudentInfo selStd:listOfStudent)
		{
			subjList = new ArrayList<>();
			fillStudentInfo(selStd);
			manSubList=obj.manadatorySubjectListForStudent(selStd.getClassId(), session, selStd.getAddNumber(), selStd.getSectionid(), "scholastic", conn);
			optSubList=obj.optionalSubjectListForStudent(selStd.getClassId(), session, selStd.getAddNumber(), selStd.getSectionid(), "scholastic", conn);
			subjList.addAll(manSubList);
			subjList.addAll(optSubList);
			
			selStd.setSubjectList(subjList);
			selStd.setOverallList(manSubList);
			selStd.setOverallRemarkList(optSubList);
			
			if(selStd.getClassStrName().equalsIgnoreCase("IX") || selStd.getClassStrName().equalsIgnoreCase("9") 
					|| selStd.getClassStrName().equalsIgnoreCase("9th") || selStd.getClassStrName().equalsIgnoreCase("Ninth") )
			{
				selStd.setPreviousClass("X");
			}
			else if(selStd.getClassStrName().equalsIgnoreCase("XI") || selStd.getClassStrName().equalsIgnoreCase("11") 
					|| selStd.getClassStrName().equalsIgnoreCase("11th") || selStd.getClassStrName().equalsIgnoreCase("Eleventh"))
			{
				selStd.setPreviousClass("XII");
			}
			else
			{
				selStd.setPreviousClass(selStd.getClassFromName());
			}
			
			show=true;
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fillStudentInfo(StudentInfo selStd) 
	{
		addNoList=new ArrayList<String>();
		motherNameList=new ArrayList<String>();
		fatherNameList=new ArrayList<String>();
		nameList=new ArrayList<String>();
		
		className=selStd.getClassName();
		section=selStd.getSectionName();
		addDate=sdf.format(selStd.getStartingDate());
		dob=sdf.format(selStd.getDob());
		
		selStd.setDobStr(dob);
		selStd.setStrAdmitDate(addDate);
		
		char arr[]=selStd.getSrNo().toCharArray();
		for(char sNo:arr)
		{
			addNoList.add(String.valueOf(sNo));
		}
		
		while(addNoList.size()<15)
		{
			addNoList.add(" ");
		}
		
		arr=selStd.getFullName().toCharArray();
		for(char sNo:arr)
		{
			nameList.add(String.valueOf(sNo));
		}
		
		while(nameList.size()<30)
		{
			nameList.add(" ");
		}
		
		arr=selStd.getFathersName().toUpperCase().toCharArray();
		for(char sNo:arr)
		{
			fatherNameList.add(String.valueOf(sNo));
		}
		
		while(fatherNameList.size()<30)
		{
			fatherNameList.add(" ");
		}
		
		arr=selStd.getMotherName().toUpperCase().toCharArray();
		for(char sNo:arr)
		{
			motherNameList.add(String.valueOf(sNo));
		}
		
		while(motherNameList.size()<30)
		{
			motherNameList.add(" ");
		}
		
		selStd.setAddNoList(addNoList);
		selStd.setMotherNameList(motherNameList);
		selStd.setFatherNameList(fatherNameList);
		selStd.setNameList(nameList);
	}
	
	
	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<StudentInfo> getListOfStudent() {
		return listOfStudent;
	}

	public void setListOfStudent(ArrayList<StudentInfo> listOfStudent) {
		this.listOfStudent = listOfStudent;
	}

	public ArrayList<String> getAddNoList() {
		return addNoList;
	}

	public void setAddNoList(ArrayList<String> addNoList) {
		this.addNoList = addNoList;
	}

	public ArrayList<String> getNameList() {
		return nameList;
	}

	public void setNameList(ArrayList<String> nameList) {
		this.nameList = nameList;
	}

	public ArrayList<String> getMotherNameList() {
		return motherNameList;
	}

	public void setMotherNameList(ArrayList<String> motherNameList) {
		this.motherNameList = motherNameList;
	}

	public ArrayList<String> getFatherNameList() {
		return fatherNameList;
	}

	public void setFatherNameList(ArrayList<String> fatherNameList) {
		this.fatherNameList = fatherNameList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SubjectInfo> getManSubList() {
		return manSubList;
	}

	public void setManSubList(ArrayList<SubjectInfo> manSubList) {
		this.manSubList = manSubList;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public ArrayList<SubjectInfo> getOptSubList() {
		return optSubList;
	}

	public void setOptSubList(ArrayList<SubjectInfo> optSubList) {
		this.optSubList = optSubList;
	}
	
	
}
