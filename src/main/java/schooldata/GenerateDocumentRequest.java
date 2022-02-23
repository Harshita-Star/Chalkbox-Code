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

import org.primefaces.PrimeFaces;

import session_work.DatabaseMethodSession;
@ManagedBean(name="generateDocumentRequest")
@ViewScoped
public class GenerateDocumentRequest implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo>list=new ArrayList<>(),selStdList=new ArrayList<>();
	boolean b;
	String searchBasis,name;
	ArrayList<SelectItem> sectionList,classSection,allDocList;
	ArrayList<String> selectedDocument=new ArrayList<>();
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	String selectedSection,selectedCLassSection,schId,session;
	
	public GenerateDocumentRequest() 
	{
		selectedCLassSection=selectedSection;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		schId=obj.schoolId();
		session=obj.selectedSessionDetails(schId, conn);
		try
		{
			classSection=obj.allClass(conn);
			selectedCLassSection=selectedSection="";
			b=false;
			
			conn.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : list)
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

	
	public void searchStudentByName()
	{
		searchBasis="byname";
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : list)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						list=new ArrayList<>();
						list.add(info);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			if(list.isEmpty())
			{
				b=false;
			}
			else
				b=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}
	
	public void searchStudentByClassSection()
	{
		searchBasis="byclass";
		Connection conn = DataBaseConnection.javaConnection();
		selStdList.clear();
		selectedDocument.clear();
		try
		{
			list=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedCLassSection,selectedSection);
			if(list.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
			}
			else
				b=true;
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
	
	public void makeDocumentList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		DBMethodsNew dbObj=new DBMethodsNew();
		try
		{
			boolean exist=false;
			for(StudentInfo student:selStdList)
			{
				exist=dbObj.checkDocReqExistForStd(student.getAddNumber(), schId, session, conn);
				if(exist==true)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request already generated for "+student.getFname()+"."));
					PrimeFaces.current().ajax().update("form1");
					break;
				}
			}
			if(exist==false)
			{
				allDocList=obj.allDocType(conn);
				PrimeFaces.current().executeInitScript("PF('documentDialog').show();");
				PrimeFaces.current().ajax().update("documentForm");
				
			}
			
			conn.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void submitRequest()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DBMethodsNew dbObj=new DBMethodsNew();
		try
		{
			if(selStdList.size()>0)
			{
				if(selectedDocument.size()>0)
				{
					String document="";
					for(String student:selectedDocument)
					{
						document+=student+"-pending,";
					}
					if(document.contains(","))
						document=document.substring(0,document.lastIndexOf(","));
					
					int i=0;
					for(StudentInfo student:selStdList)
					{
						i=dbObj.generateDocumentRequest(student.getAddNumber(), document, schId, session,conn);
					}
					if(i>0)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request generated successfully"));
						selStdList=new ArrayList<>();
						selectedDocument=new ArrayList<>();
						name="";
						selectedCLassSection=selectedSection="";
						b=false;
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one document"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student"));
			}
			
				
			conn.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
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
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<StudentInfo> getSelStdList() {
		return selStdList;
	}
	public void setSelStdList(ArrayList<StudentInfo> selStdList) {
		this.selStdList = selStdList;
	}
	public ArrayList<SelectItem> getAllDocList() {
		return allDocList;
	}
	public void setAllDocList(ArrayList<SelectItem> allDocList) {
		this.allDocList = allDocList;
	}
	public ArrayList<String> getSelectedDocument() {
		return selectedDocument;
	}
	public void setSelectedDocument(ArrayList<String> selectedDocument) {
		this.selectedDocument = selectedDocument;
	}
}


