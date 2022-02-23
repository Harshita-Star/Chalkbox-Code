package lecture_plan;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="submitLecPlanReport")
@ViewScoped

public class SubmittedLecturePlanReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String username,schid,type,session;
	ArrayList<LecturePlanInfo> subjectList,tempList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DBMethodsLecturePlan objLecture=new DBMethodsLecturePlan();
	DataBaseMeathodJson objJSON=new DataBaseMeathodJson();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public SubmittedLecturePlanReportBean() 
	{
		Connection conn= DataBaseConnection.javaConnection();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		
		session=DatabaseMethods1.selectedSessionDetails(schid,conn);
		
		String empid=objJSON.employeeIdbyEmployeeName(username,schid,conn);
		subjectList=objLecture.allAllocatedSubjects(empid, session, schid,type, conn);
		for(LecturePlanInfo info:subjectList)
		{
			tempList=objLecture.lecturePlanDetailOfSubject(empid, info.getClassId(), info.getSectionId(), info.getSubjectId(), session, schid, conn);
			if(tempList.size()>0)
			{
				info.setStatus("Submitted");
			}
			else
			{
				info.setStatus("Not Submitted");
				tempList.clear();
			}
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public ArrayList<LecturePlanInfo> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<LecturePlanInfo> subjectList) {
		this.subjectList = subjectList;
	}
}
