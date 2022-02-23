package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name="studentWiseAttendantRecord")
@ViewScoped

public class StudentWiseAttendantRecordBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	StudentInfo selectedStudent = new StudentInfo();
	String name = "",studentType = "";
	String inBus1="",outBus1="",inSch="",outSch="",inBus2="",outBus2="";
	String inBusMorn="",notInBusMorn="",outBusMorn="",notOutBusMorn="",inSchool="",notInSchool="",outSchool="",notOutSchool="",
			inBusEven="",notInBusEven="",outBusEven="",notOutBusEven="";
	Date startDate = new Date();
	Date endDate = new Date();
	ArrayList<StudentInfo> studentList,slist;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	String schoolId,session;
	
	public StudentWiseAttendantRecordBean() {
		
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//studentList=new DatabaseMethods1().searchStudentList(query,conn);
		studentList=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");

		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	
	public void show()
	{
		Connection conn= DataBaseConnection.javaConnection();
		slist = new ArrayList<>();
		
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		
		ArrayList<Date> dates = new ArrayList<>();
		long interval = 24*1000 * 60 * 60; // 1 hour in millis
		long endTime1 =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
		long curTime = startDate.getTime();
		while (curTime <= endTime1) {
			dates.add(new Date(curTime));
			curTime += interval;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		selectedStudent = DBJ.studentDetailslistByAddNo(id, schoolId, conn);
		
		for(int j=0;j<dates.size();j++)
		{
			Date lDate =dates.get(j);
			StudentInfo tt = new StudentInfo();
			
			tt.setStartDate(sdf.format(lDate));
			tt.setFname(selectedStudent.getFname());
			tt.setClassName(tt.getClassName());
			tt.setPickdropInfo(DBJ.pickDropDetails(sdf1.format(lDate), id, schoolId, conn));
			
			slist.add(tt);
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showRFID()
	{
		inBus1=outBus1=inSch=outSch=inBus2=outBus2="";
		inBusMorn=notInBusMorn=outBusMorn=notOutBusMorn=inSchool=notInSchool=outSchool=notOutSchool=inBusEven=notInBusEven=outBusEven=notOutBusEven="";
		
		Connection conn= DataBaseConnection.javaConnection();
		slist = new ArrayList<>();
		
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		
		ArrayList<Date> dates = new ArrayList<>();
		long interval = 24*1000 * 60 * 60; // 1 hour in millis
		long endTime1 =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
		long curTime = startDate.getTime();
		while (curTime <= endTime1) {
			dates.add(new Date(curTime));
			curTime += interval;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		selectedStudent = DBJ.studentDetailslistByAddNo(id, schoolId, conn);
		if(selectedStudent==null)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Data Not Found!"));
		}
		else
		{
			if(selectedStudent.getRouteStop()==null || selectedStudent.getRouteStop().equalsIgnoreCase("null") 
					|| selectedStudent.getRouteStop().equalsIgnoreCase(""))
			{
				studentType="Non Transport";
			}
			else
			{
				studentType = "Transport";
			}
			
			int ib1=0,ob1=0,isch=0,osch=0,ib2=0,ob2=0;
			
			for(int j=0;j<dates.size();j++)
			{
				Date lDate =dates.get(j);
				StudentInfo tt = new StudentInfo();
				
				tt.setStartDate(sdf.format(lDate));
				tt.setFname(selectedStudent.getFname());
				tt.setClassName(tt.getClassName());
				tt.setRfidDataInfo(DBJ.rfidPickDropDetails(sdf1.format(lDate), id, schoolId, conn));
				
				if(!tt.getRfidDataInfo().getInBusMorn().equals(""))
				{
					ib1+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutBusMorn().equals(""))
				{
					ob1+=1;
				}
				
				if(!tt.getRfidDataInfo().getInBusEven().equals(""))
				{
					ib2+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutBusEven().equals(""))
				{
					ob2+=1;
				}
				
				if(!tt.getRfidDataInfo().getInSchool().equals(""))
				{
					isch+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutSchool().equals(""))
				{
					osch+=1;
				}
				
				slist.add(tt);
			}
			
			inBus1 = "Total Entries = "+dates.size()+"\n"+"Total Punches = "+ib1+"\n"+"Total Non Punches = "+(dates.size()-ib1);
			outBus1 = "Total Entries = "+dates.size()+"\n"+"Total Punches = "+ob1+"\n"+"Total Non Punches = "+(dates.size()-ob1);
			
			inSch = "Total Entries = "+dates.size()+"\n"+"Total Punches = "+isch+"\n"+"Total Non Punches = "+(dates.size()-isch);
			outSch = "Total Entries = "+dates.size()+"\n"+"Total Punches = "+osch+"\n"+"Total Non Punches = "+(dates.size()-osch);
			
			inBus2 = "Total Entries = "+dates.size()+"\n"+"Total Punches = "+ib2+"\n"+"Total Non Punches = "+(dates.size()-ib2);
			outBus2 = "Total Entries = "+dates.size()+"\n"+"Total Punches = "+ob2+"\n"+"Total Non Punches = "+(dates.size()-ob2);
		
			inBusMorn = "Total Punches = "+ib1;
			notInBusMorn = "Total Non Punches = "+(dates.size()-ib1);
			outBusMorn = "Total Punches = "+ob1;
			notOutBusMorn = "Total Non Punches = "+(dates.size()-ob1);
			
			inSchool = "Total Punches = "+isch;
			notInSchool = "Total Non Punches = "+(dates.size()-isch);
			outSchool = "Total Punches = "+osch;
			notOutSchool = "Total Non Punches = "+(dates.size()-osch);
			
			inBusEven = "Total Punches = "+ib2;
			notInBusEven = "Total Non Punches = "+(dates.size()-ib2);
			outBusEven = "Total Punches = "+ob2;
			notOutBusEven = "Total Non Punches = "+(dates.size()-ob2);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public ArrayList<StudentInfo> getSlist() {
		return slist;
	}

	public void setSlist(ArrayList<StudentInfo> slist) {
		this.slist = slist;
	}

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public String getInBus1() {
		return inBus1;
	}

	public void setInBus1(String inBus1) {
		this.inBus1 = inBus1;
	}

	public String getOutBus1() {
		return outBus1;
	}

	public void setOutBus1(String outBus1) {
		this.outBus1 = outBus1;
	}

	public String getInSch() {
		return inSch;
	}

	public void setInSch(String inSch) {
		this.inSch = inSch;
	}

	public String getOutSch() {
		return outSch;
	}

	public void setOutSch(String outSch) {
		this.outSch = outSch;
	}

	public String getInBus2() {
		return inBus2;
	}

	public void setInBus2(String inBus2) {
		this.inBus2 = inBus2;
	}

	public String getOutBus2() {
		return outBus2;
	}

	public void setOutBus2(String outBus2) {
		this.outBus2 = outBus2;
	}

	public String getInBusMorn() {
		return inBusMorn;
	}

	public void setInBusMorn(String inBusMorn) {
		this.inBusMorn = inBusMorn;
	}

	public String getNotInBusMorn() {
		return notInBusMorn;
	}

	public void setNotInBusMorn(String notInBusMorn) {
		this.notInBusMorn = notInBusMorn;
	}

	public String getOutBusMorn() {
		return outBusMorn;
	}

	public void setOutBusMorn(String outBusMorn) {
		this.outBusMorn = outBusMorn;
	}

	public String getNotOutBusMorn() {
		return notOutBusMorn;
	}

	public void setNotOutBusMorn(String notOutBusMorn) {
		this.notOutBusMorn = notOutBusMorn;
	}

	public String getInSchool() {
		return inSchool;
	}

	public void setInSchool(String inSchool) {
		this.inSchool = inSchool;
	}

	public String getNotInSchool() {
		return notInSchool;
	}

	public void setNotInSchool(String notInSchool) {
		this.notInSchool = notInSchool;
	}

	public String getOutSchool() {
		return outSchool;
	}

	public void setOutSchool(String outSchool) {
		this.outSchool = outSchool;
	}

	public String getNotOutSchool() {
		return notOutSchool;
	}

	public void setNotOutSchool(String notOutSchool) {
		this.notOutSchool = notOutSchool;
	}

	public String getInBusEven() {
		return inBusEven;
	}

	public void setInBusEven(String inBusEven) {
		this.inBusEven = inBusEven;
	}

	public String getNotInBusEven() {
		return notInBusEven;
	}

	public void setNotInBusEven(String notInBusEven) {
		this.notInBusEven = notInBusEven;
	}

	public String getOutBusEven() {
		return outBusEven;
	}

	public void setOutBusEven(String outBusEven) {
		this.outBusEven = outBusEven;
	}

	public String getNotOutBusEven() {
		return notOutBusEven;
	}

	public void setNotOutBusEven(String notOutBusEven) {
		this.notOutBusEven = notOutBusEven;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
