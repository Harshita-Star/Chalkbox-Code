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
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

@ManagedBean(name="adminHome")
@ViewScoped

public class AdminHomeBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	boolean showClass,showStudent,showEmployee,showTransport=false,showFinance,showAttendence,showMessage,showPerformance,showTC,showPromotion,
			showPrevious,setting,report,showHomeWork;
	String name;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem> list;

	public AdminHomeBean()
	{
		list=new ArrayList<>();
		HttpSession ss2=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String type=(String) ss2.getAttribute("type");
		type="admin";
		Connection conn = DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().checkModules(conn);
		for(SelectItem ss : list)
		{
			if(type.equalsIgnoreCase("Teacher"))
			{
				if(ss.getValue().equals("performance"))
				{
					showPerformance=true;
				}
				report=false;

				showHomeWork=true;
				setting=false;
				HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss1.setAttribute("TRANSPORT", showTransport);
			}
			else if(type.equalsIgnoreCase("vice principal"))
			{
				report=false;
				setting=false;
				if(ss.getValue().equals("student"))
				{
					showStudent=true;
				}
				showHomeWork=true;
				if(ss.getValue().equals("performance"))
				{
					showPerformance=true;
				}
				HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss1.setAttribute("TRANSPORT", showTransport);

			}
			else if(type.equalsIgnoreCase("Receptionist")||type.equalsIgnoreCase("admin"))
			{
				report=true;
				setting=true;
				if(ss.getValue().equals("class"))
				{
					showClass=true;
				}
				showHomeWork=true;
				if(ss.getValue().equals("student"))
				{
					showStudent=true;
				}

				if(ss.getValue().equals("employee"))
				{
					showEmployee=true;
				}

				if(ss.getValue().equals("attendence"))
				{
					showAttendence=true;
				}

				if(ss.getValue().equals("message"))
				{
					showMessage=true;
				}

				if(ss.getValue().equals("finance"))
				{
					showFinance=true;
				}

				if(ss.getValue().equals("tc"))
				{
					showTC=true;
				}

				if(ss.getValue().equals("promotion"))
				{
					showPromotion=true;
				}

				if(ss.getValue().equals("previous"))
				{
					showPrevious=true;
				}

				if(ss.getValue().equals("transport"))
				{
					showTransport=true;

				}


				if(ss.getValue().equals("performance"))
				{
					showPerformance=true;
				}

				HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss1.setAttribute("TRANSPORT", showTransport);
			}
			else if(type.equalsIgnoreCase("PRINCIPAL"))
			{
				report=false;
				setting=false;
				showHomeWork=true;
				if(ss.getValue().equals("performance"))
				{
					showPerformance=true;
				}
				HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss1.setAttribute("TRANSPORT", showTransport);

			}
			else if(type.equalsIgnoreCase("ADVISOR"))
			{
				report=false;
				setting=false;
				showHomeWork=false;
				if(ss.getValue().equals("student"))
				{
					showStudent=true;
				}

				if(ss.getValue().equals("finance"))
				{
					showFinance=true;
				}
				HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss1.setAttribute("TRANSPORT", showTransport);
			}
			else
			{

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}
		HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss1.setAttribute("studentList",studentList);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}


	@SuppressWarnings("unchecked")
	public void searchStudentByName()
	{
		HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList=(ArrayList<StudentInfo>) ss1.getAttribute("studentList");
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					ss.setAttribute("selectedStudent", info);
					PrimeFaces.current().executeInitScript("window.open('completeStudentDetails.xhtml')");
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SelectItem> getList() {
		return list;
	}
	public void setList(ArrayList<SelectItem> list) {
		this.list = list;
	}
	public boolean isSetting() {
		return setting;
	}
	public boolean isReport() {
		return report;
	}
	public void setReport(boolean report) {
		this.report = report;
	}
	public void setSetting(boolean setting) {
		this.setting = setting;
	}
	public boolean isShowHomeWork() {
		return showHomeWork;
	}
	public void setShowHomeWork(boolean showHomeWork) {
		this.showHomeWork = showHomeWork;
	}
	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public boolean isShowStudent() {
		return showStudent;
	}

	public void setShowStudent(boolean showStudent) {
		this.showStudent = showStudent;
	}

	public boolean isShowEmployee() {
		return showEmployee;
	}

	public void setShowEmployee(boolean showEmployee) {
		this.showEmployee = showEmployee;
	}

	public boolean isShowTransport() {
		return showTransport;
	}

	public void setShowTransport(boolean showTransport) {
		this.showTransport = showTransport;
	}

	public boolean isShowFinance() {
		return showFinance;
	}

	public void setShowFinance(boolean showFinance) {
		this.showFinance = showFinance;
	}

	public boolean isShowAttendence() {
		return showAttendence;
	}

	public void setShowAttendence(boolean showAttendence) {
		this.showAttendence = showAttendence;
	}

	public boolean isShowMessage() {
		return showMessage;
	}

	public void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}

	public boolean isShowPerformance() {
		return showPerformance;
	}

	public void setShowPerformance(boolean showPerformance) {
		this.showPerformance = showPerformance;
	}

	public boolean isShowTC() {
		return showTC;
	}

	public void setShowTC(boolean showTC) {
		this.showTC = showTC;
	}

	public boolean isShowPromotion() {
		return showPromotion;
	}

	public void setShowPromotion(boolean showPromotion) {
		this.showPromotion = showPromotion;
	}

	public boolean isShowPrevious() {
		return showPrevious;
	}

	public void setShowPrevious(boolean showPrevious) {
		this.showPrevious = showPrevious;
	}
}
