package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name="stdVillage")
@ViewScoped
public class StudentVillageWise implements Serializable{
	String village;
	ArrayList<SelectItem> villageList = new ArrayList<>();
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	String schid;
	String session;
	boolean b = false;
	int total;
	
	public StudentVillageWise() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = DBM.schoolId();
		session = DBM.selectedSessionDetails(schid, conn);
		villageList = DBM.getAllVillages(schid,session,conn);
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		if(!village.equalsIgnoreCase("-1")) {
			studentList = DBM.getStudentDetailsVillageWise(schid,session,village,conn);
			
			if(studentList.size()>0) {
				total = studentList.size();
				b = true;
			}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("There is no students in this village."));
				b = false;
			}
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Any Village!"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public ArrayList<SelectItem> getVillageList() {
		return villageList;
	}

	public void setVillageList(ArrayList<SelectItem> villageList) {
		this.villageList = villageList;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	

}
