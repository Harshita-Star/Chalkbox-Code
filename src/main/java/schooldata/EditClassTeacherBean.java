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

@ManagedBean (name="editClassTeacher")
@ViewScoped

public class EditClassTeacherBean implements Serializable {


	String selectedClass,id,selectedsection;
	ArrayList<ClassTeacherInfo> classteacherlist;
	ClassTeacherInfo selectedteacher;
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	DatabaseMethods1 obj= new DatabaseMethods1();

	public EditClassTeacherBean () {
		Connection conn = DataBaseConnection.javaConnection();
		classteacherlist=obj.viewallclassteacher(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editBookDetail() {

		Connection conn = DataBaseConnection.javaConnection();
		
        selectedClass ="";
		classList=obj.allClassforupdate(conn);
		sectionList = new ArrayList<SelectItem>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sectionname() {
		{
			String[] splt= selectedteacher.getTeachername().split(" - ");
			
			String teacherId = splt[1];
			
			
			Connection conn = DataBaseConnection.javaConnection();
			try
			{
				//// // System.out.println(selectedClass);
				sectionList=obj.selectsection(teacherId,selectedClass,conn);
				setSelectedClass(selectedClass);

			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


	}
	public void updatetacher()
	{
		Connection conn = DataBaseConnection.javaConnection();
		id=selectedteacher.getId();
		
		//// // System.out.println(selectedsection+"ad");
		
		String res=obj.updateclassteacher(id,selectedClass,selectedsection,conn);
		if(res.equals("i"))
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class Teacher Updated Sucessfully"));
			classteacherlist=obj.viewallclassteacher(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		Connection conn = DataBaseConnection.javaConnection();
		id=selectedteacher.getId();
		String res=obj.deleteteacher(id,/*selectedClass,*/conn);
		if(res.equals("i"))
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class Teacher Deleted Sucessfully"));
			classteacherlist=obj.viewallclassteacher(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
		value = language ="Selected Id"+selectedteacher.getId()+" --Teacher-"+selectedteacher.getTeachername();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Class Teacher","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedsection, conn);
		
		language ="Selected Teacher Id"+selectedteacher.getId()+" --Teacher-"+selectedteacher.getTeachername();
		
		value =  "Class-"+className+" --Section-"+sectionname;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Class Teacher","WEB",value,conn);
		return refNo;
	}
	
	

	public ArrayList<ClassTeacherInfo> getClassteacherlist() {
		return classteacherlist;
	}

	public void setClassteacherlist(ArrayList<ClassTeacherInfo> classteacherlist) {
		this.classteacherlist = classteacherlist;
	}

	public ClassTeacherInfo getSelectedteacher() {
		return selectedteacher;
	}

	public void setSelectedteacher(ClassTeacherInfo selectedteacher) {
		this.selectedteacher = selectedteacher;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSelectedsection() {
		return selectedsection;
	}

	public void setSelectedsection(String selectedsection) {
		this.selectedsection = selectedsection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

}
