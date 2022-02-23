package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

@ManagedBean(name="masterFeeCollection")
@ViewScoped

public class MasterFeeCollectionBean implements Serializable
{
	ArrayList<StudentInfo> studentList;
	boolean show;
	ArrayList<SelectItem> classSection,sectionList,branchList;
	String selectedSection,selectedClassSection,name;
	StudentInfo selectedStudent;
	String dueFeesPrevious,branches,schid;

	public String editNow()
	{
		if(selectedStudent!=null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", selectedStudent);

			selectedStudent=null;
			show=false;
			name=null;
			selectedClassSection=null;


			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			try {
				ec.redirect("masterStudentFeeCollection.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			//return "studentFeeCollection";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a student from the list", "Validation error"));
		}

		return "";
	}


	public String studentExpen()
	{
		if(selectedStudent!=null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", selectedStudent);

			selectedStudent=null;
			show=false;
			name=null;
			selectedClassSection=null;


			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			try {
				ec.redirect("billPayment.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			//return "studentFeeCollection";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a student from the list", "Validation error"));
		}

		return "";
	}


	public String backToFinanceDashboard()
	{
		selectedStudent=null;
		show=false;
		name=null;
		selectedClassSection=null;

		return "manageFinanceDashboard";
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(schid,selectedClassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(branches,query,conn);
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
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);

		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						studentList.add(info);
						selectedStudent=info;

						HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
						ss.setAttribute("selectedStudent", selectedStudent);

						FacesContext fc = FacesContext.getCurrentInstance();
						ExternalContext ec = fc.getExternalContext();

						try {
							ec.redirect("masterStudentFeeCollection.xhtml");
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						return "masterStudentFeeCollection";
						//show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
		return "";
	}


	public String searchStudentByNameByExpense()
	{
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);

		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						studentList.add(info);
						selectedStudent=info;

						HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
						ss.setAttribute("selectedStudent", selectedStudent);

						FacesContext fc = FacesContext.getCurrentInstance();
						ExternalContext ec = fc.getExternalContext();

						try {
							ec.redirect("billPayment.xhtml");
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						return "billPayment";
						//show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
		return "";
	}

	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentListByClassSectionSchid(schid,selectedSection,conn);
		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void branchWiseWork()
	{
		selectedClassSection=selectedSection="";
		sectionList = new ArrayList<>();
		Connection conn=DataBaseConnection.javaConnection();
		classSection=new DatabaseMethods1().allClass(schid,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public MasterFeeCollectionBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
				}
			}
		}
		/*Connection conn=DataBaseConnection.javaConnection();
		classSection=new DatabaseMethods1().allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}

	public void searchStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(name,conn);
		show=true;
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
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public String getDueFeesPrevious() {
		return dueFeesPrevious;
	}

	public void setDueFeesPrevious(String dueFeesPrevious) {
		this.dueFeesPrevious = dueFeesPrevious;
	}

	public String getName()
	{
		return name;
	}


	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}


	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}


	public String getBranches() {
		return branches;
	}


	public void setBranches(String branches) {
		this.branches = branches;
	}


	public String getSchid() {
		return schid;
	}


	public void setSchid(String schid) {
		this.schid = schid;
	}
}
