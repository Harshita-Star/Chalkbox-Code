package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.ApplyLeaveJson;
import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="viewStudentLeave")
@ViewScoped
public class ViewStudentLeaveBean implements Serializable
{

	String schId;
	boolean show;
	ApplyLeaveJson selected;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();

	ArrayList<ApplyLeaveJson> list=new ArrayList<>();
	public ViewStudentLeaveBean()
	{
		int i=1;
		Connection conn= DataBaseConnection.javaConnection();

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
		schId = DBM.schoolId();
		list=DBJ.viewStudentLeaveforStudent(studentid,schId,conn);

		for(ApplyLeaveJson ls:list)
		{
			//StudentInfo lss=DBJ.studentDetailslistByAddNo(ls.getStudentid(),schoolId,conn);
			ls.setSno(i++);
			if(ls.getStatus().equals("0"))
			{
				ls.setStatus("Pending");
				ls.setShowDelete(false);

			}
			else if(ls.getStatus().equals("1"))
			{
				ls.setStatus("Approved");
				ls.setShowDelete(true);

			}
			else
			{
				ls.setStatus("Denied");
				ls.setShowDelete(true);
			}


		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void delete()
	{
		Connection conn= DataBaseConnection.javaConnection();

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
		

		int i=DBJ.deleteStudentLeave(selected.getId(),schId,conn);
		if(i>0)
		{
			DBJ.adminnotification("Leave",selected.getFname()+" Deleted the Leave from "+selected.getStartDate()+"-"+selected.getEndDate(),"admin-"+schId,schId,"LeaveDelete-"+studentid,conn);
			String clsTchr = DBJ.classTeacherBySection1(selected.getSectionid(), schId, conn);
			if(!clsTchr.equalsIgnoreCase("no"))
			{
				
				String[] teacher=clsTchr.split(",");
				for(int ii=0;ii<teacher.length;ii++)
				{
					DBJ.adminnotification("Leave",selected.getFname()+" Deleted the Leave from "+selected.getStartDate()+"-"+selected.getEndDate(),"staff"+"-"+teacher[ii]+"-"+schId,schId,"LeaveDelete-"+studentid,conn);
												
				}
				
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Leave is deleted successfully!"));
			list=DBJ.viewStudentLeaveforStudent(studentid,schId,conn);
			int x=1;
			for(ApplyLeaveJson ls:list)
			{
				//StudentInfo lss=DBJ.studentDetailslistByAddNo(ls.getStudentid(),schoolId,conn);
				ls.setSno(x++);
				if(ls.getStatus().equals("0"))
				{
					ls.setStatus("Pending");
					ls.setShowDelete(false);

				}
				else if(ls.getStatus().equals("1"))
				{
					ls.setStatus("Approved");
					ls.setShowDelete(true);

				}
				else
				{
					ls.setStatus("Denied");
					ls.setShowDelete(true);
				}

			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occure!"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getSchId() {
		return schId;
	}
	public void setSchId(String schId) {
		this.schId = schId;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public DataBaseMeathodJson getDBJ() {
		return DBJ;
	}
	public void setDBJ(DataBaseMeathodJson dBJ) {
		DBJ = dBJ;
	}
	public ArrayList<ApplyLeaveJson> getList() {
		return list;
	}
	public void setList(ArrayList<ApplyLeaveJson> list) {
		this.list = list;
	}
	public ApplyLeaveJson getSelected() {
		return selected;
	}
	public void setSelected(ApplyLeaveJson selected) {
		this.selected = selected;
	}
}

