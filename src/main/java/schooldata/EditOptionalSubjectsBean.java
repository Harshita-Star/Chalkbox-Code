package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;
@ManagedBean(name="editOptionalSubjects")
@ViewScoped
public class EditOptionalSubjectsBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<Subjects> subjectList;

	Subjects selectedSubject;
	

	ArrayList<Subjects> subList = new ArrayList<Subjects>();
	DatabaseMethods1 dmb = new DatabaseMethods1();

	public EditOptionalSubjectsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subjectList=new DatabaseMethods1().optionalSubjectList(conn);
		
		int i=1;
		for(Subjects cc:subjectList)
		{
			String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(),conn);
			
			//// // System.out.println(cc.getSubjectCode()+checker);
			if(checker == "no")
			{
				cc.setSerialNumber(String.valueOf(i));
				subList.add(cc);
				i++;
			}
			
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	


public void update()
{
   Connection conn=DataBaseConnection.javaConnection();
	DataBaseValidator objValidate=new DataBaseValidator();
	
   if(selectedSubject.getSubjectName().trim().equalsIgnoreCase(""))
  {
	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Subject Name"));
 
  }
  else
  {	  String schid = dmb.schoolId();
	  String session = dmb.selectedSessionDetails(schid,conn);	  
   int dupl = objValidate.duplicateSubjectEdit(String.valueOf(selectedSubject.getSubjectCode()),session,selectedSubject.getClassid(), selectedSubject.getSubjectName(),selectedSubject.getSubjectType(),schid,conn);

   if(dupl ==0)
   {	
	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Subject Found"));
		subjectList = new ArrayList<Subjects>();subList = new ArrayList<Subjects>();
		subjectList=new DatabaseMethods1().optionalSubjectList(conn);
		
		int i=1;
		for(Subjects cc:subjectList)
		{
			String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(),conn);
			
			//// // System.out.println(cc.getSubjectCode()+checker);
			if(checker == "no")
			{
				cc.setSerialNumber(String.valueOf(i));
				subList.add(cc);
				i++;
			}
			
		}
   }
   else
   {
		
		int status = dmb.editOptionalSubject(selectedSubject.getSubjectCode(),selectedSubject.getSubjectName(),selectedSubject.getAddInExam(), conn); 
		if(status==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Edited Sucessfuly "));
			
			subjectList = new ArrayList<Subjects>();subList = new ArrayList<Subjects>();
			subjectList=new DatabaseMethods1().optionalSubjectList(conn);
			
			int i=1;
			for(Subjects cc:subjectList)
			{
				String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(),conn);
				
				//// // System.out.println(cc.getSubjectCode()+checker);
				if(checker == "no")
				{
					cc.setSerialNumber(String.valueOf(i));
					subList.add(cc);
					i++;
				}
				
			}
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('editDialog').hide();");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));

		}
   }
  }	
	try {
		conn.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
}
	
	
	public void delete()
	{
		Connection conn=DataBaseConnection.javaConnection();
//// // System.out.println(selectedSubject.getSubjectCode());
		int status = dmb.deleteOptionalSubject(selectedSubject.getSubjectCode(), conn); 
		if(status==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Deleted "));
			
			subjectList = new ArrayList<Subjects>();subList = new ArrayList<Subjects>();
			subjectList=new DatabaseMethods1().optionalSubjectList(conn);
			
			int i=1;
			for(Subjects cc:subjectList)
			{
				String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(),conn);
				
				//// // System.out.println(cc.getSubjectCode()+checker);
				if(checker == "no")
				{
					cc.setSerialNumber(String.valueOf(i));
					subList.add(cc);
					i++;
				}
				
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));

		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}


	public ArrayList<Subjects> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<Subjects> subjectList) {
		this.subjectList = subjectList;
	}

	public Subjects getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(Subjects selectedSubject) {
		this.selectedSubject = selectedSubject;
	}


	public ArrayList<Subjects> getSubList() {
		return subList;
	}


	public void setSubList(ArrayList<Subjects> subList) {
		this.subList = subList;
	}




	public String getRegex() {
		return regex;
	}




	public void setRegex(String regex) {
		this.regex = regex;
	}


	

}

