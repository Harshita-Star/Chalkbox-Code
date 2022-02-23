package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="extrastrenghtReport")
@ViewScoped
public class ExtrastrenghtReportBean implements Serializable{
	ArrayList<SchoolInfo>schoolList = new ArrayList<>();
	SchoolInfo selected = new SchoolInfo();
	String count;
	int agreement;
	public ExtrastrenghtReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().selectAllStudentStrenght(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void details()
	{
		agreement = selected.getAgreementFor();
	}

	public void edit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(agreement<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Agreement for must be greater than zero"));
		}
		else
		{
			new DatabaseMethods1().updateAgreement(selected.getId(),agreement,conn);
			schoolList=new DatabaseMethods1().selectAllStudentStrenght(conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public ArrayList<SchoolInfo> getSchoolList() {
		return schoolList;
	}


	public void setSchoolList(ArrayList<SchoolInfo> schoolList) {
		this.schoolList = schoolList;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public int getAgreement() {
		return agreement;
	}

	public void setAgreement(int agreement) {
		this.agreement = agreement;
	}

	public SchoolInfo getSelected() {
		return selected;
	}

	public void setSelected(SchoolInfo selected) {
		this.selected = selected;
	}
}