package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="studentAddConcern")
@ViewScoped
public class StudentAddConcernBean implements Serializable {


	String desc;
	Date startDate,endDate;
	Date startminDate,endMinDate;
	transient UploadedFile file;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();



	public StudentAddConcernBean() {

	}

	public void submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schid = (String) ss.getAttribute("schoolid");
		String studentid=(String) ss.getAttribute("username");

		String name = "";
		if (file != null && file.getSize() > 0)
		{
			int rendomNumber=(int)(Math.random()*9000)+1000;
			name = file.getFileName();
			String exten[]=name.split("\\.");
			name="concernfeedback"+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
			DBM.makeProfileSchid(schid,file,name,conn);
		}

		int i=DBJ.Schoolfeedback(studentid, desc,schid,conn,name);

		if(i>0)
		{
			StudentInfo ln=DBJ.studentDetailslistByAddNo(studentid,schid,conn);
			DBJ.adminnotification("Concern/Feedback",ln.getFname()+" Added A Concern/Feedback",
					"admin-"+schid,schid,"Concern-"+studentid,conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Concern is sucessfully Added!"));
			desc="";
			name="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occure."));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartminDate() {
		return startminDate;
	}

	public void setStartminDate(Date startminDate) {
		this.startminDate = startminDate;
	}

	public Date getEndMinDate() {
		return endMinDate;
	}

	public void setEndMinDate(Date endMinDate) {
		this.endMinDate = endMinDate;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
}