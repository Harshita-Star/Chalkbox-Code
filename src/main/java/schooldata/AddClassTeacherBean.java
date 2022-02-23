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

@ManagedBean (name="addClassTeacher")
@ViewScoped
public class AddClassTeacherBean implements Serializable {

	String selectedteacher;
	ArrayList<SelectItem> teacherlist;
	ArrayList<Category> allclass;
	ArrayList<Category> selectedclass;

	boolean showtable=false;
	public AddClassTeacherBean() {
		Connection conn = DataBaseConnection.javaConnection();
		teacherlist=new DatabaseMethods1().allteacher(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void done() {
		showtable=true;
		Connection conn = DataBaseConnection.javaConnection();
		allclass=new DatabaseMethods1().sectionListforclassteacher(selectedteacher,conn);
		//allclass=new DatabaseMethods1().allClassforteachers();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void allocate()
	{
		//int a=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		for(Category tt: selectedclass)
		{

			int i=obj.addinclassteacher(tt.getClassid(),selectedteacher,tt.getId(),conn);
			if(i>0)
			{
				//a=1;
				String refNo;
				try {
					refNo=addWorkLog(conn,tt.getClassid(),selectedteacher,tt.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class Teacher Allocated of Class "+tt.getClassName()+" - "+tt.getSectionName()));

				showtable=false;
			}

			/*FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class Teacher Allocated"));

            showtable=false;*/

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private String addWorkLog(Connection conn, String classid, String selectedteacher2, int id) {
		
		String value = "";
		String language= "";
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(classid , schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,String.valueOf(id), conn);
	
		
		language = "Teacher-"+selectedteacher2;
		
			
		  value = "Section-"+sectionname+" --Class-"+className;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Class teacher","WEB",value,conn);
		return refNo;
		
	}
	public ArrayList<SelectItem> getTeacherlist() {
		return teacherlist;
	}

	public void setTeacherlist(ArrayList<SelectItem> teacherlist) {
		this.teacherlist = teacherlist;
	}

	public String getSelectedteacher() {
		return selectedteacher;
	}

	public void setSelectedteacher(String selectedteacher) {
		this.selectedteacher = selectedteacher;
	}
	public boolean isShowtable() {
		return showtable;
	}
	public void setShowtable(boolean showtable) {
		this.showtable = showtable;
	}

	public ArrayList<Category> getAllclass() {
		return allclass;
	}
	public void setAllclass(ArrayList<Category> allclass) {
		this.allclass = allclass;
	}
	public ArrayList<Category> getSelectedclass() {
		return selectedclass;
	}
	public void setSelectedclass(ArrayList<Category> selectedclass) {
		this.selectedclass = selectedclass;
	}
}
