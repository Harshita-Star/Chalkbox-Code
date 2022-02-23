package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.MeetingInfo;
import schooldata.StudentInfo;

@ManagedBean(name="liveMeetingBean")
@ViewScoped
public class LiveMeetingBean implements Serializable
{

	ArrayList<MeetingInfo> list= new ArrayList<MeetingInfo>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public LiveMeetingBean() {
	
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
		String schoolId = obj.schoolId();
		StudentInfo info =obj.studentDetailslistByAddNo(schoolId,studentid,conn);
	     // System.out.println(info.getFullName());
		list=DBJ.viewMeetingDetails(info.getClassId(),info.getSectionid(),schoolId,"zoom",conn);
		
		if(schoolId.equals("216") || schoolId.equals("221")) {
			list = DBJ.checkForOptionSubjectsAllocationforLiveClass(list, studentid,schoolId,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

	public ArrayList<MeetingInfo> getList() {
		return list;
	}

	public void setList(ArrayList<MeetingInfo> list) {
		this.list = list;
	}

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public DataBaseMeathodJson getDBJ() {
		return DBJ;
	}

	public void setDBJ(DataBaseMeathodJson dBJ) {
		DBJ = dBJ;
	}
	
	
}
