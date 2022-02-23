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
import schooldata.DataBaseMethodsHostelModule;
import schooldata.DatabaseMethods1;
import schooldata.HealthCheckUpInfo;

@ManagedBean(name="studentInfirmaryReport")
@ViewScoped
public class StudentInfirmaryReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<HealthCheckUpInfo> healthList=new ArrayList<>();
	DataBaseMethodsHostelModule DBJ=new DataBaseMethodsHostelModule();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DB=new DataBaseMeathodJson();
	String schid;
	public StudentInfirmaryReportBean ()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss1.getAttribute("username");
		String schid =obj.schoolId();

		healthList=DBJ.viewAllHealthCheckUpDetailsStudentWise(conn, studentid, schid);
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public DataBaseMethodsHostelModule getDBJ() {
		return DBJ;
	}
	public void setDBJ(DataBaseMethodsHostelModule dBJ) {
		DBJ = dBJ;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ArrayList<HealthCheckUpInfo> getHealthList() {
		return healthList;
	}

	public void setHealthList(ArrayList<HealthCheckUpInfo> healthList) {
		this.healthList = healthList;
	}
}
