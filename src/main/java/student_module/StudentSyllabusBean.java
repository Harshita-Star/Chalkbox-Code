package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;

@ManagedBean(name="studentSyllabus")
@ViewScoped


public class StudentSyllabusBean implements Serializable
{
	ArrayList<SubjectInfo> list = new ArrayList<>();
	SubjectInfo selected = new SubjectInfo();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schoolId;
	

	public StudentSyllabusBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
        schoolId = obj1.schoolId();
		StudentInfo lss=DBJ.studentDetailslistByAddNo(studentid,schoolId,conn);
		list	=DBJ.viewSyllabus(lss.getClassId(),lss.getSectionid(),schoolId,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void view()
	{
		PrimeFaces.current().executeInitScript("window.open('"+selected.getAssignmentPhotoPath5()+"')");
	}

	public ArrayList<SubjectInfo> getList() {
		return list;
	}

	public void setList(ArrayList<SubjectInfo> list) {
		this.list = list;
	}

	public SubjectInfo getSelected() {
		return selected;
	}

	public void setSelected(SubjectInfo selected) {
		this.selected = selected;
	}


}
