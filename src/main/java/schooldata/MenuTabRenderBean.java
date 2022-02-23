package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name="menuTab")
@ViewScoped
public class MenuTabRenderBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	boolean showClass,showStudent,showEmployee,showTransport,showFinance,showAttendence,showMessage,showPerformance,showTC,showPromotion,
	showPrevious,showPreviousFee;
	ArrayList<SelectItem> list;

	public MenuTabRenderBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SchoolInfoList info=obj.fullSchoolInfo(conn);
		if(info.getInstallSession().equals(DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn)))
		{
			showPreviousFee=true;
		}
		else
		{
			showPreviousFee=false;
		}
		list=new ArrayList<>();
		list=obj.checkModules(conn);
		for(SelectItem ss : list)
		{
			if(ss.getValue().equals("class"))
			{
				showClass=true;
			}

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
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public ArrayList<SelectItem> getList() {
		return list;
	}

	public void setList(ArrayList<SelectItem> list) {
		this.list = list;
	}

	public boolean isShowPreviousFee() {
		return showPreviousFee;
	}

	public void setShowPreviousFee(boolean showPreviousFee) {
		this.showPreviousFee = showPreviousFee;
	}
}
