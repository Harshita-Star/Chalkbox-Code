package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name="studentWiseFeeAllocation")
@ViewScoped
public class StudentWiseFeeAllocationBean implements Serializable
{


	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>(),feeList=new ArrayList<>(),concessionCategoryList=new ArrayList<>();
	String selectedClass,feeCategory;
	String sectionName,conceesionCategory;
	String selectedSection;
	ArrayList<StudentInfo>infoList=new ArrayList<>();
	ArrayList<StudentInfo>feesLIstCheck=new ArrayList<>();
	ArrayList<FeeInfo>monthList=new ArrayList<>();
	Boolean show=false;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	
	public StudentWiseFeeAllocationBean() {

		Connection conn=DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		classList=dbm.allClass(conn);
		feeList=dbm.viewFeeListForStudentWise(conn);
		concessionCategoryList=dbm.allConnsessionType(conn);
		conceesionCategory="-1";
		show=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=dbm.allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void search()
	{

		Connection conn=DataBaseConnection.javaConnection();
		monthList=dbm.viewMonthListByFees(feeCategory,conn);
		 
		feesLIstCheck=new ArrayList<>();
		
		Map<String, String> map = new HashMap<>();

		StudentInfo ls=new StudentInfo();
		for(FeeInfo fees:monthList )
		{
			map.put(fees.getMonthId(), String.valueOf(0));
		}
		
		ls.setFeesMap(map);
		
		feesLIstCheck.add(ls);
		
		infoList=dbm.viewStudentWiseFeesForAdd(selectedClass,selectedSection,feeCategory,conceesionCategory,conn);
		
	//	infoList=dbm.viewStudentWiseFeeForAdd(selectedSection,feeCategory,conceesionCategory,conn);
		
		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void addStudentFees() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			if(infoList.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"No Student Found","No student Found"));
			}
			else
			{
				dbm.studentWiseFeeAdd(feeCategory,selectedSection,monthList,infoList,conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Fee Updated Successfully","Fee Updated Successfully"));
				selectedSection=feeCategory="";
				selectedClass = "select";
				conceesionCategory="-1";
				infoList = new ArrayList<>();
				show=false;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		/*int count=new DatabaseMethods1().deleteStudentWiseAllocation(feeCategory,selectedSection,conn);
		if(count>0)
		{*/
		/*}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Some Error occur PLease try again","Some Error occur PLease try again"));
		
		}*/
		//fc.getExternalContext().redirect("studentWiseFeeAllocation.xhtml");

	}
	
	
	public void updateFees()
	{
		
		// // System.out.println("check");
		for(StudentInfo ls:infoList)
		{
			Map<String, String> map = new HashMap<>();
			for(FeeInfo lst:monthList)
			{
				String amount=feesLIstCheck.get(0).getFeesMap().get(lst.getMonthId());
				if(!amount.equals("0"))
				{
					map.put(lst.getMonthId(), amount);
									
				}
				else
				{
					map.put(lst.getMonthId(), ls.getFeesMap().get(lst.getMonthId()));
				}
			}
			ls.setFeesMap(map);
		}
		
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}


	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}


	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}


	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}


	public ArrayList<SelectItem> getFeeList() {
		return feeList;
	}


	public void setFeeList(ArrayList<SelectItem> feeList) {
		this.feeList = feeList;
	}


	public String getSelectedClass() {
		return selectedClass;
	}


	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}


	public String getFeeCategory() {
		return feeCategory;
	}


	public void setFeeCategory(String feeCategory) {
		this.feeCategory = feeCategory;
	}


	public String getSectionName() {
		return sectionName;
	}


	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}


	public String getSelectedSection() {
		return selectedSection;
	}


	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}


	public ArrayList<StudentInfo> getInfoList() {
		return infoList;
	}


	public void setInfoList(ArrayList<StudentInfo> infoList) {
		this.infoList = infoList;
	}


	public ArrayList<FeeInfo> getMonthList() {
		return monthList;
	}


	public void setMonthList(ArrayList<FeeInfo> monthList) {
		this.monthList = monthList;
	}


	public ArrayList<StudentInfo> getFeesLIstCheck() {
		return feesLIstCheck;
	}


	public void setFeesLIstCheck(ArrayList<StudentInfo> feesLIstCheck) {
		this.feesLIstCheck = feesLIstCheck;
	}


	public Boolean getShow() {
		return show;
	}


	public void setShow(Boolean show) {
		this.show = show;
	}


	public ArrayList<SelectItem> getConcessionCategoryList() {
		return concessionCategoryList;
	}


	public void setConcessionCategoryList(ArrayList<SelectItem> concessionCategoryList) {
		this.concessionCategoryList = concessionCategoryList;
	}


	public String getConceesionCategory() {
		return conceesionCategory;
	}


	public void setConceesionCategory(String conceesionCategory) {
		this.conceesionCategory = conceesionCategory;
	}



}
