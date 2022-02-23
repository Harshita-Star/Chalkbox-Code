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

@ManagedBean(name="subjectRank")
@ViewScoped

public class SubjectRankBean implements Serializable
{
	String schid,selectedClass;
	ArrayList<SubjectInfo> subjectList;
	ArrayList<SelectItem> classList;
	boolean show=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public SubjectRankBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		classList = obj.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();

		subjectList = obj.subjectListOfParticularClass(selectedClass, conn);
		if(subjectList.isEmpty())
		{
			show=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Subjects Found in This Class."));
		}
		else
		{
			show=true;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();

		int i = obj.assignSubjectRank(subjectList,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Rank Assigned Successfully."));
			subjectList = obj.subjectListOfParticularClass(selectedClass, conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= selectedClass;
		int x=1;
		
		for (SubjectInfo ss : subjectList) {
		
			if(value.equals(""))
			{
				value = "("+ss.getSubjectId()+"---"+x+")";
			}
			else
			{
				value = value + "("+ss.getSubjectId()+"---"+x+")";
			}

			
		 x=x+1;	
		}
		
		
		String refNo = workLg.saveWorkLogMehod(language,"Subject Ordering","WEB",value,conn);
		return refNo;
	}


	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}


}
