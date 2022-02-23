package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.DietInfo;
import schooldata.StudentInfo;
@ManagedBean(name="studentNotification")
@ViewScoped
public class StudentNotificationBean implements Serializable
{
	ArrayList<DietInfo> list =new ArrayList<>();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schid;
	public StudentNotificationBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss1.getAttribute("username");
		String schid = obj.schoolId();
		StudentInfo	selectedStudent=DBJ.studentDetailslistByAddNo(studentid,schid,conn);
		String classid=selectedStudent.getClassId();
		String	selectedSection=selectedStudent.getSectionid();
		String notification1="",notification2="",notification3="",notification4="";
		notification1=classid+"-"+schid;
		notification2=selectedSection+"-"+classid+"-"+schid;
		notification3=String.valueOf(selectedStudent.getFathersPhone())+"-"+studentid+"-"+schid;
		notification4=schid;


		list=DBJ.webNotificationMethod(notification1,notification2,notification3,notification4,schid,"student",conn);
		DBJ.NotificationReadMessage(notification1,notification2,notification3,notification4,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<DietInfo> getList() {
		return list;
	}
	public void setList(ArrayList<DietInfo> list) {
		this.list = list;
	}
	public DataBaseMeathodJson getDBJ() {
		return DBJ;
	}
	public void setDBJ(DataBaseMeathodJson dBJ) {
		DBJ = dBJ;
	}
}