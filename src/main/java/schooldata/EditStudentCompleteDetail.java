package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
@ManagedBean(name="editStudentCompleteDetail")
@ViewScoped
public class EditStudentCompleteDetail implements Serializable
{
	ArrayList<StudentInfo> list=new ArrayList<>(), selectStudent=new ArrayList<>(), selectStudentDet=new ArrayList<>();
	boolean b,showPicTable;
	String total,option;
	StudentInfo selectedStudent;
	String selectedSection;
	String selectedCLassSection;

	ArrayList<SelectItem> sectionList,classSection;
	ArrayList<SelectItem> religionList;
	ArrayList<SelectItem> categoryList,houseList;
	String schid;
	DatabaseMethods1 obj= new DatabaseMethods1();
	
	public EditStudentCompleteDetail()
	{
		selectStudent=new ArrayList<>();
		selectStudentDet=new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		schid = obj.schoolId();

		option = "details";
		try
		{
			religionList=obj.allReligionList(conn);
			classSection=obj.allClass(conn);
			categoryList=obj.studentCategoryList(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		b=false;
		/*list=obj.searchStudentListByClassSection("",conn);
		if(list.isEmpty())
	    {
	    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
	    	b=false;
	    }
	    else
	    {
	    	b=true;
	    }
	    total=String.valueOf(list.size());*/
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
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
			list=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection, selectedSection,conn);
			
			
			
			if(list.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
				showPicTable=false;
			}
			else
			{
				selectStudent=new ArrayList<>();
				selectStudentDet=new ArrayList<>();
				if(option.equalsIgnoreCase("details"))
				{
					houseList=new ArrayList<>();
					houseList=new DatabaseMethods1().allHouseCategory(conn);
					if(houseList.size()>0)
					{
						int count = 0;
						for(SelectItem ss : houseList)
						{
							count=Integer.parseInt(new DatabaseMethods1().allStudentcount(new DatabaseMethods1().schoolId(), "house", selectedCLassSection, new DatabaseMethods1().selectedSessionDetails(new DatabaseMethods1().schoolId(),conn), String.valueOf(ss.getValue()), conn));
							ss.setLabel(ss.getLabel()+" ("+count+" Students)");
						}
					}
					b=true;
					showPicTable=false;
				}
				else
				{
					b=false;
					showPicTable=true;
				}

			}
			total=String.valueOf(list.size());
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
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int counterChk =0;
		int admmNoDuplChecker= 0;
		String admNoDuplWord = "";
		
		
		try
		{
			int i=0;
			if(option.equalsIgnoreCase("details"))
			{
				if(selectStudentDet.size()>0)
				{
					
					for(StudentInfo cvv: selectStudentDet)
					{
						int checker = obj.checkingForDuplAdmNoAllowed(conn);
						int status = 0;
						if(checker == 1)
						{
							if(!cvv.getSrNo().trim().equalsIgnoreCase(""))
							status=new DatabaseMethods1().duplicateStudentEntryInEdit(obj.schoolId(),cvv.getSrNo(),cvv.getAddNumber(),conn);
						
							if(status ==1)
							{
								admmNoDuplChecker++;
								admNoDuplWord = cvv.getSrNo();
							}
						}	
					}
					
					
					for(StudentInfo cvv: selectStudentDet)
					{
						if(cvv.getFname().trim().equalsIgnoreCase("")||cvv.getDob()==null||cvv.getStartingDate()==null)
						{
							counterChk++;
						}
					}
				
				if(admmNoDuplChecker>0)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Admission No. Found '"+admNoDuplWord+"'"));
				}
				else
				{	
			       if(counterChk==0)
				   {	
					i=new DatabaseMethods1().updateStudentBasicDetails(selectStudentDet,option,conn);
					if(i>0)
					{
						String refNo;
						try {
							refNo=addWorkLog(conn);
						} catch (Exception e) {
							e.printStackTrace();
						}
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Detail Updated Successfully"));
						selectStudentDet=new ArrayList<>();
						//						list = new ArrayList<>();
						//						b=false;

					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
					}
				   }
			       else
			       {
			    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill Mandatory Fields Properly"));  
			       }
				} 
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Student(s) to Edit Details."));
				}
			}
			else
			{
				if(selectStudent.size()>0)
				{
					i=new DatabaseMethods1().updateStudentBasicDetails(selectStudent,option,conn);
					if(i>0)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Photos Updated Successfully"));
						selectStudent=new ArrayList<>();
						list = new ArrayList<>();
						showPicTable=false;

					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Student(s) to Edit Details."));
				}
			}


		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
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
	    String value = "Students-";
		String language= "";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		for(StudentInfo si : selectStudentDet)
		{	
			String admDate = "";
			try {
				admDate = formatter.format(si.getStartingDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String dobDt = "";
			try {
				dobDt = formatter.format(si.getDob()); 
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		  	
			
		  value += "(SrNo-"+si.getSrNo()+" --Name-"+si.getFname()+" --Fahter-"+si.getFathersName()+" --Mother-"+si.getMotherName()
		  +" --gender-"+si.getGender()+" --AdmDate-"+admDate+" --Dob-"+dobDt+" --Phone-"+si.getFathersPhone()+" --Status-"+si.getStudentStatus()+" --AdmitClass-"+si.getAdmitClassName()
		  +" --Aadhar-"+si.getAadharNo()+" --Religion-"+si.getReligion()+" --Caste-"+si.getCaste()+" --Perm.Adr.-"+si.getPermanentAddress()+" --Blood Grp.-"+si.getBloodGroup()+" --Category Id-"+si.getCategId()
		  +" --type-"+si.getStudentType()+" --extraClass-"+si.getExtraClass()+" --Housename-"+si.getHouse()+" --FatherAadhar-"+si.getFatherAadhaar()+" --MotherAadhar-"+si.getMotherAadhaar()+" --Mother Phone-"+si.getMothersPhone()
		  +" --LedgerNo-"+si.getLedgerNo()+" --Geo Fence-"+si.getGeoFence()+" --CurrentAddress-"+si.getCurrentAddress()+" --RegNoIX-"+si.getRegNo_IX()+" --regNoXI-"+si.getRegNo_XI()+" --AdmnFileNo-"+si.getAdmnFileNo()+" --Adm_remark-"+si.getAdmRemark()+")";
		}
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Bulk Student","WEB",value,conn);
		return refNo;
	}
	
	
	public ArrayList<StudentInfo> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public ArrayList<StudentInfo> getSelectStudent() {
		return selectStudent;
	}
	public void setSelectStudent(ArrayList<StudentInfo> selectStudent) {
		this.selectStudent = selectStudent;
	}
	public ArrayList<SelectItem> getReligionList() {
		return religionList;
	}
	public void setReligionList(ArrayList<SelectItem> religionList) {
		this.religionList = religionList;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public ArrayList<SelectItem> getHouseList() {
		return houseList;
	}
	public void setHouseList(ArrayList<SelectItem> houseList) {
		this.houseList = houseList;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public boolean isShowPicTable() {
		return showPicTable;
	}
	public void setShowPicTable(boolean showPicTable) {
		this.showPicTable = showPicTable;
	}
	public ArrayList<StudentInfo> getSelectStudentDet() {
		return selectStudentDet;
	}
	public void setSelectStudentDet(ArrayList<StudentInfo> selectStudentDet) {
		this.selectStudentDet = selectStudentDet;
	}
	public String getSchid() {
		return schid;
	}
	public void setSchid(String schid) {
		this.schid = schid;
	}



}
