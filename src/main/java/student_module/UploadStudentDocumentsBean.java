package student_module;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="uploadStudentDocuments")
@ViewScoped
public class UploadStudentDocumentsBean implements Serializable{
	
	
	ArrayList<StudentInfo> studentList;
	ArrayList<StudentInfo> docList;
	String name;
	boolean ren,fileRender;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	StudentInfo selected = new StudentInfo();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=dbm.searchStudentList(query,conn);
		

		ArrayList<String> studentListt=new ArrayList<String>();
		
		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	
	public void checkDocuments() {
		
		Connection conn=DataBaseConnection.javaConnection();
		docList= dbm.findStudentSchoolDocuments(conn);
		if(docList.size()>0) {
			
		if(name.equalsIgnoreCase(""))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Student from autocomplete list"));
		}
		else
		{	
		 ren= true;
		 String[] splitter = name.split("-:");
		 docList = dbm.checkStudentDocsPresent(docList,splitter[1],conn);
		
		 for(StudentInfo inf: docList) {
			
			if(inf.getFileNo().equalsIgnoreCase("no")) {
				inf.setFileRender(false);
			}
			else {
				inf.setFileRender(true);
			}
		 }	
		}
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Document Found"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String uploadDocs()
	{
		
	  if(name.equalsIgnoreCase(""))
	  {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Student from autocomplete list"));
		return "";
	  }
	  else
	  {
		Connection conn=DataBaseConnection.javaConnection();
		
		String[] splitter = name.split("-:");
		int size = docList.size();
		int counter=0;
		for (StudentInfo  in :docList) {
			
			if (in.getFile() == null) {
		         counter++;
			}
		}
		
		if(counter==size) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Choose any Document Before Submitting"));
			return "";
		}
		else {
		int i=dbm.addStudentDocument(docList,splitter[1],conn);
		if(i>0)
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("Documents Uploaded Successfully"));
	 		
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "uploadStudentDocuments.xhtml";
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));
	 		try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
		}
	  }	
	}
	
	public void viewDocss() {
		String filename =	selected.getFileNo();
		//// // System.out.println("sd");
		PrimeFaces.current().executeInitScript("window.open('"+filename+"')");
	}
	
	
	public void deleteDoc()
	{
       Connection conn=DataBaseConnection.javaConnection();
		
		String schidd = dbm.schoolId();
		
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(schidd, conn);
		String path = ls.getUploadpath();
		 
		
	       int status =dbm.deleteStudentDocuments(selected.getId(),conn);
		
	       if(status>=1)
	      {
	    	   String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	   File file = new File(path+selected.getFileName());
				file.delete();
	    	FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("Document Deleted"));
	 		checkDocuments();
	    	
	      }
	      else 
	      {
	    
	    	FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
	      }
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "Id-"+String.valueOf(selected.getId());
		String language = "";

		String refNo = workLg.saveWorkLogMehod(language,"Remove Student Document","WEB",value,conn);
		return refNo;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRen() {
		return ren;
	}

	public void setRen(boolean ren) {
		this.ren = ren;
	}

	public boolean isFileRender() {
		return fileRender;
	}

	public void setFileRender(boolean fileRender) {
		this.fileRender = fileRender;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
	}
	
	

}
