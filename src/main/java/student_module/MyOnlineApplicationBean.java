package student_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="myApplication")
@ViewScoped

public class MyOnlineApplicationBean implements Serializable
{
	ArrayList<OnlineAdmInfo> list = new ArrayList<>();
	OnlineAdmInfo selected = new OnlineAdmInfo();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseOnlineAdm DBO = new DataBaseOnlineAdm();
	LoginInfo linfo = new LoginInfo();

	public MyOnlineApplicationBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		linfo=(LoginInfo) ss.getAttribute("signInfo");
		String unm = linfo.getUnm();
		list = DBO.myApplication(unm,"login_id",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void admForm()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("enq_id", selected.getId());

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("onlineAdmae.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void viewDetail()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("enq_id", selected.getId());
		if(selected.getStatus().equalsIgnoreCase("pending") ||
				selected.getStatus().equalsIgnoreCase("application accepted") ||
				selected.getStatus().equalsIgnoreCase("application rejected"))
		{
			ss.setAttribute("heading", "Registration Form");
			PrimeFaces.current().executeInitScript("window.open('viewRegApplication.xhtml')");
		}
		else
		{
			ss.setAttribute("heading", "Admission Form");
			PrimeFaces.current().executeInitScript("window.open('viewOnlineApplication.xhtml')");
		}

		/*try {

			FacesContext.getCurrentInstance().getExternalContext().redirect("viewOnlineApplication.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	public void delete()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i = DBO.deleteApplication(selected.getId(),conn);
		if(i>=1)
		{
			//Delete krne pe School ko notify krna h via email
			DBO.deleteSiblingAe(selected.getId(), conn);
			DBO.deleteDocument(selected.getId(),conn);
			DBO.deleteMedical(selected.getId(),conn);
			DBO.deleteVaccination(selected.getId(),conn);
			String unm = linfo.getUnm();
			list = DBO.myApplication(unm,"login_id",conn);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Application Deleted Successfully"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<OnlineAdmInfo> getList() {
		return list;
	}
	public void setList(ArrayList<OnlineAdmInfo> list) {
		this.list = list;
	}
	public OnlineAdmInfo getSelected() {
		return selected;
	}
	public void setSelected(OnlineAdmInfo selected) {
		this.selected = selected;
	}


}
