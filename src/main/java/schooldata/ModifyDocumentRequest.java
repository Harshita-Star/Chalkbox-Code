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

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;

@ManagedBean(name="modifyDocRequest")
@ViewScoped
public class ModifyDocumentRequest implements Serializable{

	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass,username,userType;
	String selectedSection,selectedType,typeName;
	ArrayList<ClassInfo> list;
	StudentInfo selectedStudent,selectedDoc;
	boolean b;
	ArrayList<StudentInfo> studentList;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	ArrayList<StudentInfo> docList;
	DataBaseMethodsReports obj=new DataBaseMethodsReports();
	String schoolId,sessionValue,folderName;
	
	public ModifyDocumentRequest() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(schoolId, conn);
		folderName = ls.getDownloadpath();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		sessionValue=dbm.selectedSessionDetails(schoolId,conn);
		docList= dbm.findStudentSchoolDocuments(conn);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			classList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

			if(temp.size()>0)
			{
				classList.addAll(temp);
			}
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList = dbm.cordinatorClassList(empid, schoolId, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList=dbm.allClassListForClassTeacher(empid,schoolId,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp = dbm.allSection(selectedClass,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			sectionList=dbm.allSectionListForClassTeacher(empid,selectedClass,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void viewDocDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		
		docList=dbNew.documentRequestListOfStudent(selectedStudent.getAddNumber(), schoolId, sessionValue, folderName, conn);
		

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeRequest()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		
		int i=dbNew.closeDocumentRequest(selectedStudent.getId(),conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request closed successfully"));
			findDocs();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An error occured. Please try again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void findDocs() {
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		if (selectedSection.equals("-1")) {
			studentList = dbNew.studentListFromDocRequestForAllClass(selectedClass, schoolId, sessionValue, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

		} else {

			studentList =  dbNew.studentListFromDocRequestForSection(selectedSection, schoolId, sessionValue, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void approveRequest()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		
		int i=dbNew.updateDocStatus(selectedStudent.getId(),selectedDoc.getDocId(),selectedDoc.getStatus(),"approved",conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Document approved successfully"));
			viewDocDetails();
			PrimeFaces.current().executeInitScript("PF('viewDialog').show();");
			PrimeFaces.current().ajax().update("viewForm");
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An error occured. Please try again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void denyRequest()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		
		int i=dbNew.updateDocStatus(selectedStudent.getId(),selectedDoc.getDocId(),selectedDoc.getStatus(),"denied",conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Document denied successfully"));
			viewDocDetails();
			PrimeFaces.current().executeInitScript("PF('viewDialog').show();");
			PrimeFaces.current().ajax().update("viewForm");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An error occured. Please try again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addDocument()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		
		int i=dbNew.addDocument(selectedStudent.getId(),selectedDoc.getDocId(),conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Document added successfully"));
			viewDocDetails();
			PrimeFaces.current().executeInitScript("PF('viewDialog').show();");
			PrimeFaces.current().ajax().update("viewForm");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An error occured. Please try again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeDocument()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		String docDetail="";
		for(StudentInfo doc:docList)
		{
			if((!doc.getStatus().equalsIgnoreCase("Not Added")) && (!doc.getDocId().equalsIgnoreCase(selectedDoc.getDocId())))
			{
				docDetail=docDetail+doc.getDocId()+"-"+doc.getStatus()+",";
			}
		}
		if(docDetail.contains(","))
			docDetail=docDetail.substring(0,docDetail.lastIndexOf(","));
		
		
		if(docDetail!=null && !docDetail.equals(""))
		{
			int i=dbNew.removeDocument(selectedStudent.getId(),selectedDoc.getDocId(),docDetail,conn);
			if(i>0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Document removed successfully"));
				viewDocDetails();
				PrimeFaces.current().executeInitScript("PF('viewDialog').show();");
				PrimeFaces.current().ajax().update("viewForm");
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An error occured. Please try again"));
			}
		}
		else
		{
			PrimeFaces.current().executeInitScript("PF('msgDialog').show();");
			PrimeFaces.current().ajax().update("msgForm");
		}
			
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteEntry()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbNew=new DBMethodsNew();
		
		int i=dbNew.deleteEntry(selectedStudent.getId(),conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request deleted successfully"));
			viewDocDetails();
			findDocs();
			PrimeFaces.current().executeInitScript("PF('msgDialog').hide();");
			PrimeFaces.current().ajax().update("msgForm");
			PrimeFaces.current().executeInitScript("PF('viewDialog').hide();");
			PrimeFaces.current().ajax().update("viewForm");
			PrimeFaces.current().ajax().update("form");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An error occured. Please try again"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
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

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
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

	public ArrayList<StudentInfo> getDocList() {
		return docList;
	}

	public void setDocList(ArrayList<StudentInfo> docList) {
		this.docList = docList;
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

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public StudentInfo getSelectedDoc() {
		return selectedDoc;
	}

	public void setSelectedDoc(StudentInfo selectedDoc) {
		this.selectedDoc = selectedDoc;
	}
	
}
