package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name = "addMultipleConcession")
@ViewScoped
public class AddMultipleConcessionBean implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> arrayList = new ArrayList<>();
	ArrayList<StudentInfo> studentList, multipleStudentList;
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String className, section, total, username, userType, schoolid;
	ArrayList<ClassInfo> list;
	boolean b;
	String conceesionCategory,concessionStatus;

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = new DatabaseMethods1().allSection(selectedClass, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public AddMultipleConcessionBean() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("accounts"))
		{
			classList=new DatabaseMethods1().allClass(conn);
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList = new DatabaseMethods1().cordinatorClassList(empid, schoolid, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		if (selectedSection.equals("-1")) {
			studentList = obj.multipleConcessionForAllSection(selectedClass, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());
			className = "All";
			sectionName = "All";
		} else {

			studentList = obj.MultipleConcessionByParticularSection(selectedSection, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addConcession() {
		Connection conn = DataBaseConnection.javaConnection();
		String session = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(conn);

		boolean updateTransportDetail = false;
		for (StudentInfo tt : multipleStudentList) {
			if(tt.getNewConcessionAssign().equals(tt.getOldconcessionCategory()))
			{
				concessionStatus=tt.getConcessionStatus();
			}
			else
			{
				conceesionCategory=new DatabaseMethods1().concessionCategoryNameById(new DatabaseMethods1().schoolId(), tt.getNewConcessionAssign(), conn);
				if(conceesionCategory.equalsIgnoreCase("General"))
				{
					concessionStatus="accepted";
				}
				else
				{
					if(info.getConcessionRequest().equals("no"))
					{
						concessionStatus="accepted";
					}
					else
					{
						concessionStatus="pending";
					}
				}
			}

			updateTransportDetail = new DatabaseMethods1().updateConcessionDetailInRegistration(tt.getAddNumber(),
					session, tt.getNewConcessionAssign(),concessionStatus ,conn);
		}
		if (multipleStudentList.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Row !! "));
		} else {
			if (updateTransportDetail == true) {
				
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Add Concession Detail Successfully "));
				b = false;
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("some Error Occurred !!"));
			}
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(conn);
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String session = DatabaseMethods1.selectedSessionDetails(schid,conn);

		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		if(selectedSection.equalsIgnoreCase("-1"))
			sectionname = "All";
		
		language = "Class -"+className+" -- Section -"+sectionname;
		
		for(StudentInfo tt : multipleStudentList)
		{
			if(tt.getNewConcessionAssign().equals(tt.getOldconcessionCategory()))
			{
				concessionStatus=tt.getConcessionStatus();
			}
			else
			{
				conceesionCategory=new DatabaseMethods1().concessionCategoryNameById(new DatabaseMethods1().schoolId(), tt.getNewConcessionAssign(), conn);
				if(conceesionCategory.equalsIgnoreCase("General"))
				{
					concessionStatus="accepted";
				}
				else
				{
					if(info.getConcessionRequest().equals("no"))
					{
						concessionStatus="accepted";
					}
					else
					{
						concessionStatus="pending";
					}
				}
			}
		  value += "( Student -"+tt.getAddNumber()+" --- Concession status -"+concessionStatus+" --- New concession Assign - "+tt.getNewConcessionAssign()+")";	
		}
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Multiple Concession","WEB",value,conn);
		return refNo;
	}

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
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

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
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

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
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

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	String sectionName;

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public ArrayList<StudentInfo> getMultipleStudentList() {
		return multipleStudentList;
	}

	public void setMultipleStudentList(ArrayList<StudentInfo> multipleStudentList) {
		this.multipleStudentList = multipleStudentList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

}
