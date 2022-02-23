package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
@ManagedBean(name="studentViewConcern")
@ViewScoped
public class StudentViewConcernBean implements Serializable
{

	String json;
	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schid;
	
	public StudentViewConcernBean() {

		Connection conn=DataBaseConnection.javaConnection();

		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss1.getAttribute("username");
		String schid = obj1.schoolId();
		list =DBJ.SchoolTotalfeedback(studentid,schid,conn);
		SchoolInfoList ls=DBJ.fullSchoolInfo(schid,conn);
		int i=1;
		for(StudentInfo ss:list)
		{
			ss.setSno(i++);
			if(ss.getSignImage().equals(""))
			{
				ss.setSignImage("");

			}
			else
			{
				ss.setSignImage(ls.getDownloadpath()+ss.getSignImage());

			}

			ss.setStrtcDate(new SimpleDateFormat("dd-MMM-yyyy").format(ss.getDate()));
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public ArrayList<StudentInfo> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}
}
