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

import session_work.RegexPattern;

@ManagedBean(name="editSubjects")
@ViewScoped
public class EditSubjectsBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<Subjects> subjectList,selectSubjList=new ArrayList<>();
	Subjects selectedSubject;
	boolean editDetailsShow;
	ArrayList<SelectItem> classList,sectionList,subjectTypeList;
	String selectedClass,type,selectedSection,selectedClassTemp,subjectName,addInExam;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator validator = new DataBaseValidator();

	public void updateNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		try
		{

			int flag=0;

			int status=validator.duplicateSubjectEdit(String.valueOf(selectedSubject.getId()),sessionValue,selectedClass, subjectName,type,schoolId,conn);
			if(status==0)
			{
				
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
			}

			if(flag==0)
			{
				try
				{
					int i=obj.updateSubjects(subjectName, selectedClass, selectedSubject.getId(),type,addInExam,conn);
					if(i==1)
					{
						String refNo3;
						try {
							refNo3=addWorkLog3(conn);
						} catch (Exception e) {
							e.printStackTrace();
						}
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage("Subject details updated successfully"));

						subjectList=obj.subjectList(conn);
						editDetailsShow=false;
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
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

	public EditSubjectsBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			subjectTypeList=obj.subjectTypeList();
			subjectList=obj.subjectList(conn);

			for(Subjects sub:subjectList){
				type=sub.getSubjectType();
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
	public void deleteNow()
	{
		Connection conn = DataBaseConnection.javaConnection();

		try
		{
			boolean checkAllocate = obj.subjectallocatedOrNot(selectedSubject.getId(),conn);
			if(checkAllocate)
			{
				FacesContext fc2=FacesContext.getCurrentInstance();
				fc2.addMessage(null, new FacesMessage("Can't Delete Selected Subject as it is Allocated to a teacher,Please Deallocate it first"));
			}
			else
			{	
			  int i=obj.deleteSubject(selectedSubject.getId(),conn);
			  if(i==1)
			 {
				  String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
				subjectList=obj.subjectList(conn);
				editDetailsShow=false;

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Selected Subject deleted successfully"));
			 }
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
	
	public String deleteMultipleNow()
	{
		Connection conn = DataBaseConnection.javaConnection();

		try
		{
			if(selectSubjList.size()>0)
			{
				int k= 0;
				String j="";
				
				for(int h=0;h<selectSubjList.size();h++)
				{
					boolean checkAllocate = obj.subjectallocatedOrNot(selectSubjList.get(h).getId(),conn);
					if(checkAllocate)
					{
						k++;
						j= selectSubjList.get(h).getSerialNumber() ;
					}
						
				}
				    
				if(k>=1)
				{
					FacesContext fc2=FacesContext.getCurrentInstance();
					fc2.addMessage(null, new FacesMessage("Selected Subjects contains allocated subject at serial Number "+j+" ,Please Deallocate it first"));
				}
				else
				{	
				
				int i=obj.deleteMultipleSubject(selectSubjList,conn);
				if(i>=1)
				{
					String refNo2;
					try {
						refNo2=addWorkLog2(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}

					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Selected Subject deleted successfully"));
					
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "editSubjects.xhtml";
				}
				else
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
				}
			  }	
			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Please select atleast one subject to delete!"));
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
		return "";
	}

	public void editSubjectDetails()
	{

		selectedClass=selectedSubject.getClassid();
		subjectName=selectedSubject.getSubjectName();
		type=selectedSubject.getSubjectType();
		selectedClassTemp=selectedClass;
		addInExam=selectedSubject.getExamid();
		editDetailsShow=true;
	}
	
	//For Delete
		public String addWorkLog(Connection conn)
		{
		    String value = "";
			String language= "";
			
			value = String.valueOf(selectedSubject.getId());

			String refNo = workLg.saveWorkLogMehod(language,"Delete Subject","WEB",value,conn);
			return refNo;
		}
		
		//For Multiple Delete
		public String addWorkLog2(Connection conn)
		{
		    String value = "";
			String language= "";
			for(Subjects ll : selectSubjList)
			{
			  value += String.valueOf(ll.getId())+" --- ";
			}
			if(!value.equalsIgnoreCase(""))
			{
				value = value.substring(0,value.length()-5);
			}
			String refNo = workLg.saveWorkLogMehod(language,"Delete Multiple Subject","WEB",value,conn);
			return refNo;
		}
		
		//For Edit
		public String addWorkLog3(Connection conn)
		{
		    String value = "";
			String language= "";
			
			language = type+" --- "+addInExam+" --- "+subjectName;
			value = subjectName+" --- "+selectedClass+" --- "+selectedSubject.getId()+" --- "+type+" --- "+addInExam;
		
			String refNo = workLg.saveWorkLogMehod(language,"Edit Subject","WEB",value,conn);
			return refNo;
		}


	public Subjects getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(Subjects selectedSubject) {
		this.selectedSubject = selectedSubject;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<Subjects> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<Subjects> subjectList) {
		this.subjectList = subjectList;
	}

	public boolean isEditDetailsShow() {
		return editDetailsShow;
	}

	public void setEditDetailsShow(boolean editDetailsShow) {
		this.editDetailsShow = editDetailsShow;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public String getAddInExam() {
		return addInExam;
	}

	public void setAddInExam(String addInExam) {
		this.addInExam = addInExam;
	}

	public ArrayList<Subjects> getSelectSubjList() {
		return selectSubjList;
	}

	public void setSelectSubjList(ArrayList<Subjects> selectSubjList) {
		this.selectSubjList = selectSubjList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}