package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.NotificationInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="progressReport")
@ViewScoped
public class ProgressReportBean implements Serializable{


	ArrayList<NotificationInfo> newsList = new ArrayList<>();
	NotificationInfo selected = new NotificationInfo();
	String file="na",schoolId,session;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();


	public ProgressReportBean()
	{
		file="na";
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//ss.setAttribute("username", username);
		String	username=(String) ss.getAttribute("username");
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=DBM.schoolId();
		session = DBM.selectedSessionDetails(schoolId,conn);
		newsList=dbj.allProgressList(username,schoolId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=dbj.fullSchoolInfo(schoolId,conn);

		file = selected.getFilename();

		if(file==null || file.equalsIgnoreCase("na") || file.equals(""))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Result file unavailable. Please try after sometime."));
		}
		else
		{
			file = ls.getDownloadpath()+selected.getFilename();
			PrimeFaces.current().ajax().update("form2");
			PrimeFaces.current().executeInitScript("PF('alrtDlg').show();");
		}

		//PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getFilename()+"')");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<NotificationInfo> getNewsList() {
		return newsList;
	}

	public void setNewsList(ArrayList<NotificationInfo> newsList) {
		this.newsList = newsList;
	}

	public NotificationInfo getSelected() {
		return selected;
	}

	public void setSelected(NotificationInfo selected) {
		this.selected = selected;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
