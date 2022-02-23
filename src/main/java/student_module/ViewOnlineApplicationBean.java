package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.chart.PieChartModel;

import schooldata.ClassTest;
import schooldata.ComplaintInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="viewOnlineApplication")
@ViewScoped

public class ViewOnlineApplicationBean implements Serializable
{
	OnlineAdmInfo info = new OnlineAdmInfo();
	LoginInfo linfo = new LoginInfo();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseOnlineAdm DBO = new DataBaseOnlineAdm();
	ArrayList<SiblingAEInfo> completeList=new ArrayList<>();
	ArrayList<ComplaintInfo> complaintList = new ArrayList<>();
	ArrayList<ClassTest> list = new ArrayList<>();
	int redCount=0,greenCount=0,blueCount=0;
	public PieChartModel pieModel1;
	SchoolInfoList ls=new SchoolInfoList();
	StudentInfo studentList;
	String enqid = "",heading="";

	public ViewOnlineApplicationBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		enqid = (String) ss.getAttribute("enq_id");
		heading  = (String) ss.getAttribute("heading");
		info = DBO.onlineAdmInfoById(enqid, "id", conn);

		completeList=new ArrayList<>();
		completeList=DBO.siblingListByEnqId(info.getId(),conn);

		if(heading.equalsIgnoreCase("Student Details"))
		{
			studentList=(StudentInfo) ss.getAttribute("selectedStudent");
			list=DBM.allTestReport(studentList.getAddNumber(),conn,"student");
			complaintList = DBM.studentComplaintDiary(studentList.getAddNumber(), "","all", conn);
			redCount=greenCount=blueCount=0;
			for(ComplaintInfo ss1:complaintList)
			{
				if(ss1.getType().equalsIgnoreCase("complaint"))
				{
					redCount=redCount+1;
				}
				else if(ss1.getType().equalsIgnoreCase("appreciation"))
				{
					greenCount=greenCount+1;
				}
				else if(ss1.getType().equalsIgnoreCase("neutral"))
				{
					blueCount=blueCount+1;
				}


			}

			createPieModels();
		}

		/*int st = completeList.size()+1;
		for(int k=st;k<=5;k++)
		{
			SiblingAEInfo ll=new SiblingAEInfo();
			ll.setSno(k);
			ll.setName("");
			ll.setClass_name("");
			completeList.add(ll);
		}*/


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createPieModels() {
		createPieModel1();
	}
	private void createPieModel1() {

		Connection conn=DataBaseConnection.javaConnection();


		String sectionId=studentList.getSectionid();
		////// // System.out.println("sectionId"+sectionId);
		//int  sum2=new DatabaseMethods1().totalStudent(conn,studentId);

		int b=DBM.TotalPresentDays(conn,sectionId);
		////// // System.out.println("b value"+b);
		int a=DBM.totalStudentAbsent(conn,studentList.getAddNumber(),"A");
		int l=DBM.totalStudentAbsent(conn,studentList.getAddNumber(),"L");
		int ml=DBM.totalStudentAbsent(conn,studentList.getAddNumber(),"ML");
		int pl=DBM.totalStudentAbsent(conn,studentList.getAddNumber(),"PL");
		int h=DBM.totalStudentAbsent(conn,studentList.getAddNumber(),"H");
		int d=b-(a+l+ml+pl+h);
		//     //// // System.out.println("absent-"+a);
		//     //// // System.out.println("leave-"+c);
		//     //// // System.out.println("present"+d);
		//sum=new DatabaseMethods1().alltAttendance(conn);
		pieModel1 = new PieChartModel();
		pieModel1.set("Present - "+d, d);
		pieModel1.set("Absent - "+a, a);
		pieModel1.set("Leave - "+l, l);
		pieModel1.set("Holiday - "+h, h);
		pieModel1.set("Medical Leave - "+ml, ml);
		pieModel1.set("Preparation Leave - "+pl, pl);
		/* pieModel1.set("Leave - "+sum.get(0).getLeave(), sum.get(0).getLeave());
   pieModel1.set("Not Marked - "+sum.get(0).getNotMarked(), sum.get(0).getNotMarked());*/
		pieModel1.setShowDataLabels(true);
		pieModel1.setTitle("Total Meetings - "+b);
		pieModel1.setLegendPosition("w");
		//pieModel1.setSeriesColors("00A65A,DD4B39,F39C12,00C0EF");
		pieModel1.setSeriesColors("93F2A3,F79BA3,FFBF79,FF512DA8,FFFF5722,FF0097A7");
		pieModel1.setExtender("skinPie");
		try
		{
			conn.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}


	public void viewFile(String link)
	{
		PrimeFaces.current().executeInitScript("window.open('"+link+"')");
	}


	public OnlineAdmInfo getInfo() {
		return info;
	}

	public void setInfo(OnlineAdmInfo info) {
		this.info = info;
	}

	public LoginInfo getLinfo() {
		return linfo;
	}

	public void setLinfo(LoginInfo linfo) {
		this.linfo = linfo;
	}

	public ArrayList<SiblingAEInfo> getCompleteList() {
		return completeList;
	}

	public void setCompleteList(ArrayList<SiblingAEInfo> completeList) {
		this.completeList = completeList;
	}

	public String getEnqid() {
		return enqid;
	}

	public void setEnqid(String enqid) {
		this.enqid = enqid;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}


	public ArrayList<ComplaintInfo> getComplaintList() {
		return complaintList;
	}


	public void setComplaintList(ArrayList<ComplaintInfo> complaintList) {
		this.complaintList = complaintList;
	}


	public int getRedCount() {
		return redCount;
	}


	public void setRedCount(int redCount) {
		this.redCount = redCount;
	}


	public int getGreenCount() {
		return greenCount;
	}


	public void setGreenCount(int greenCount) {
		this.greenCount = greenCount;
	}


	public int getBlueCount() {
		return blueCount;
	}


	public void setBlueCount(int blueCount) {
		this.blueCount = blueCount;
	}


	public PieChartModel getPieModel1() {
		return pieModel1;
	}


	public void setPieModel1(PieChartModel pieModel1) {
		this.pieModel1 = pieModel1;
	}

	public ArrayList<ClassTest> getList() {
		return list;
	}

	public void setList(ArrayList<ClassTest> list) {
		this.list = list;
	}


}
