package tc_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import exam.DatabaseMethodExam;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import schooldata.TCInfo;

@ManagedBean(name="printTcForAll")
@ViewScoped
public class PrintTcBean implements Serializable{
	
	String tcNumber;
	String schid,session;
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	ArrayList<TCInfo> tcList = new ArrayList<>();
	DatabaseMethods1 dbm  = new DatabaseMethods1();
	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	boolean table = false;
	TCInfo selectedStudent;
	
	public PrintTcBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = dbm.schoolId();
		session = dbm.selectedSessionDetails(schid, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void search() {
		Connection conn = DataBaseConnection.javaConnection();
//		studentList = dbExam.getOldStudentDetailsForTcGenerte(tcNumber,schid,conn);
//		studentList = dbExam.getStudentDetailsFOrTcGenertae(tcNumber,schid,studentList,conn);
		tcList = dbExam.getTcDetailsForOld(tcNumber,schid,conn);
		tcList = dbExam.getStudentDetailsForTcGenerateFirst(tcNumber,schid,tcList,conn);
		
		
		
		if(tcList.size()>0) {
			table = true;
		}else {
			table = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No TC for this Tc number / Student Name"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void print(){
		Connection conn = DataBaseConnection.javaConnection();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("tcInfo", selectedStudent);
		ss.setAttribute("schoolid", schid);
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("printTransferCertificate.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getTcNumber() {
		return tcNumber;
	}

	public void setTcNumber(String tcNumber) {
		this.tcNumber = tcNumber;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public boolean isTable() {
		return table;
	}

	public void setTable(boolean table) {
		this.table = table;
	}

	public TCInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(TCInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public ArrayList<TCInfo> getTcList() {
		return tcList;
	}

	public void setTcList(ArrayList<TCInfo> tcList) {
		this.tcList = tcList;
	}
	
}
