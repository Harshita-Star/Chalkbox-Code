package schooldata;

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

@ManagedBean(name="uploadSchoolDocuments")
@ViewScoped
public class UploadSchoolDocumentBean implements Serializable{
	

	ArrayList<StudentInfo> docList = new ArrayList<>();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	StudentInfo selected = new StudentInfo();
	ArrayList<StudentInfo> viewList = new ArrayList<>();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public  UploadSchoolDocumentBean() {
		
		Connection con = DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,con);
		
	 for(int i=0;i<5;i++) {
		
		StudentInfo sf = new StudentInfo();
		sf.setSno(i+1);
		docList.add(sf);
	}
	 
	 viewList = dbm.checkSchoolDocsPresent(con);
	 try {
		con.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}
	
	public void addRow() {
		
		for(int i=0;i<1;i++) {
		StudentInfo sf = new StudentInfo();
		sf.setSno(docList.size()+1);
		docList.add(sf);
	}
	}
	
	
	public String uploadDocs()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		

		int size = docList.size();
		int counter=0;
		for (StudentInfo  in :docList) {
			
			if (in.getFile() == null) {
		         counter++;
			}
		}
		
		
            for (StudentInfo  in :docList) {
			if ((in.getFile() != null) &&(in.getDocumentName().trim().equalsIgnoreCase(""))) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Empty Document Name at Row "+in.getSno()));
				return "uploadSchoolDocuments.xhtml";
			}
		}
		
		if(counter==size) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Choose any Document Before Submitting"));
			return "";
		}
		else {
			
		
		int i=dbm.addSchoolDocument(docList,conn);
		if(i>0)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("Documents Uploaded Successfully"));
	 		
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "uploadSchoolDocuments.xhtml";
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
	
	
	public String addWorkLog(Connection conn)
	{
	    String language = ""; 
		String value="";
		for(StudentInfo lss:docList)
		{
			if (lss.getFile() != null && lss.getFile().getSize() > 0) {
				
			
			if(value.equals(""))
			{
				
					value="("+lss.getDocumentName()+"----"+lss.getFile().getFileName()+")";
				
			}
			else
			{
				
					value=value+"("+lss.getDocumentName()+"----"+lss.getFile().getFileName()+")";
				
			}
		  }
		}
		String refNo = workLg.saveWorkLogMehod(language,"School Document Upload","WEB",value,conn);
		return refNo;
	}

	

	public void viewDocss() {
		
	String filename =	selected.getFileNo();
	//// // System.out.println(filename);
	PrimeFaces.current().executeInitScript("window.open('"+filename+"')");
	
		
		
	}
	
	
	public void deleteDoc() {
		
		Connection conn=DataBaseConnection.javaConnection();
		
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(schoolId, conn);
		String path = ls.getUploadpath();
		int status =dbm.deleteSchoolDocuments(selected.getId(),conn);
		if(status>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			File file = new File(path+selected.getFileNo().substring(selected.getFileNo().lastIndexOf("/")+1));
			file.delete();
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Document Deleted"));
	 		viewList = dbm.checkSchoolDocsPresent(conn);
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
	    String language = "Id -"+selected.getId(); 
		String value=language;
		
		String refNo = workLg.saveWorkLogMehod(language,"School Document Removed","WEB",value,conn);
		return refNo;
	}
	

	public ArrayList<StudentInfo> getDocList() {
		return docList;
	}

	public void setDocList(ArrayList<StudentInfo> docList) {
		this.docList = docList;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
	}

	public ArrayList<StudentInfo> getViewList() {
		return viewList;
	}

	public void setViewList(ArrayList<StudentInfo> viewList) {
		this.viewList = viewList;
	}


	
	

}
