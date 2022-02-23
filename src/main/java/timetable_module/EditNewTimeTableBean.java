package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SideMenuBean;

@ManagedBean(name="editNewTimeTableBean")
@ViewScoped

public class EditNewTimeTableBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<TimeTableComponentInfo> changeTableList,selectedList;
	boolean showTable,disableTable,checkNewTimeTable,disableCheckBox=true;
	CourseInfo courseDetail;
	Date effectiveDate;
	ArrayList<SelectItem> teacherList,subjectList;
	String selectedCourse,startTime,winterStartTime,selectedSection;
	TimeTableSettingInfo schedule_info;
	int noOfLec,flag;
	String message="",actionName,className;
	ArrayList<NewTimeTableInfo> timeTableList,timeTableList2;
	int submitFlag=0,selectedRow,dataTableFillSize=0;
	DataBase obj=new DataBase();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	ArrayList<SelectItem> courseNameList, branchNameList;
	DataBaseMethodsTimeTable objTT=new DataBaseMethodsTimeTable();

	public EditNewTimeTableBean()
	{
		Connection con= DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,con);

		changeTableList=new ArrayList<>();

		objTT.newTimeTableOfAllClass(changeTableList,con);
		showTable=true;

		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void createTimeTable()
	{
		Connection con= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();
		selectedRow= (int) UIComponent.getCurrentComponent(context).getAttributes().get("sNo");
		
		String categid="";
		String categid1 = dbm.employeeCategoryIdByName("Teacher", con);
		String categid2 = dbm.employeeCategoryIdByName("Sports Department", con);
		String categid3 = dbm.employeeCategoryIdByName("Librarian", con);
		String categid4 = dbm.employeeCategoryIdByName("Principal", con);
		String categid5 = dbm.employeeCategoryIdByName("Vice Principal", con);
		String categid6 = dbm.employeeCategoryIdByName("Administrative Officer", con);
		
		categid=categid1+"','"+categid2+"','"+categid3+"','"+categid4+"','"+categid5+"','"+categid6;
		
		for(TimeTableComponentInfo info:changeTableList)
		{
			if(selectedRow==info.getsNo())
			{
				if(!info.getCourseId().equals("") && !info.getSectionId().equals(""))
				{
					selectedCourse=info.getCourseId();
					selectedSection=info.getSectionId();
					effectiveDate=info.getEffectiveDate();
					className=info.getCourseName()+"-"+info.getSectionName();
					subjectList = dbm.allSubjects(selectedCourse,con);
					teacherList=dbm.allteacherOnly(categid,schoolId,con);


					timeTableList=showTable("temp_time_table");

					PrimeFaces context1 = PrimeFaces.current();
					context1.executeScript("PF('timeTableDialog').show();");
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You Can Not Edit Time Table For This Selection"));
				}
			}
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<NewTimeTableInfo> showTable(String tableName)
	{
		

		Connection con= DataBaseConnection.javaConnection();
		ArrayList<NewTimeTableInfo> tempList = new ArrayList<>();
		schedule_info = obj.timeTableInfo(con, schoolId);
		int lunchAfter=Integer.valueOf(schedule_info.getLunch_after());
		String lunchTime=schedule_info.getTime_of_lunch();

		noOfLec = Integer.valueOf(schedule_info.getNo_of_lec());
		String timeOfLec[] =  schedule_info.getTime_of_lec().split(",");
		String wintimeOfLec[] = schedule_info.getWinterLecTime().split(",");
		startTime =  schedule_info.getStart_time();
		winterStartTime = schedule_info.getWinterStartTime();
		for(int i=0;i<noOfLec;i++)
		{
			NewTimeTableInfo info = new NewTimeTableInfo();
			try
			{
				info.setLecTime(startTime+" - "+timeOfLec[i]);
				info.setWinterLecTime(winterStartTime+" - "+wintimeOfLec[i]);
				info.setLecNo(String.valueOf(i+1));
				info.setCourseId(selectedCourse);
				info.setSectionId(selectedSection);
				startTime=timeOfLec[i];
				winterStartTime = wintimeOfLec[i];
				if(i==lunchAfter-1)
				{
					String temp[]=startTime.split(":");
					String tempW[]=winterStartTime.split(":");
					
					String lunchtemp[]=lunchTime.split(":");

					int x=Integer.parseInt(temp[0])+Integer.parseInt(lunchtemp[0]);
					int y=Integer.parseInt(temp[1])+Integer.parseInt(lunchtemp[1]);
					startTime=x+":"+y;
					
					int a=Integer.parseInt(tempW[0])+Integer.parseInt(lunchtemp[0]);
					int b=Integer.parseInt(tempW[1])+Integer.parseInt(lunchtemp[1]);
					/*if(b>=60)
					{
						a=a+1;
						b=b-60;
					}*/
					winterStartTime=a+":"+b;
				}

			}
			catch (Exception e )
			{
				e.printStackTrace();
			}
			tempList.add(info);
		}
		flag = objTT.timeTableDetail(tempList,selectedCourse,selectedSection,schoolId,tableName,con);
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempList;
	}

	public void update()
	{
		Connection con= DataBaseConnection.javaConnection();
		message=objTT.checkDuplicateTime(timeTableList,selectedCourse,selectedSection,con);
		if(message.equals("none"))
		{
			int i;
			objTT.deleteTempTimeTableForEdit(selectedCourse,selectedSection,con);
			i=objTT.insertTimeTableInTemp(timeTableList,effectiveDate,selectedCourse,selectedSection,con);

			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(con);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Time Table For Batch:"+className+" is prepared!"));
				PrimeFaces.current().ajax().update("timeTableForm");
				PrimeFaces.current().ajax().update("form:data");
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error!"));
			}
		}
		else
		{
			PrimeFaces.current().ajax().update("alertForm");
			PrimeFaces.current().executeInitScript("PF('alertDialog').show()");
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(effectiveDate);
	
		SideMenuBean smb = new SideMenuBean();
		
		
		String clasname = dbm.classname(selectedCourse,schoolId, conn);		
		String sectonName = dbm.sectionNameByIdSchid(schoolId,selectedSection, conn);
		
		language ="Date-"+addDt+" --Class-"+clasname+" --Section-"+sectonName;
		
		for(NewTimeTableInfo tm :timeTableList)
		{	
		  if(smb.getList().getTimeTableSchedule().equalsIgnoreCase("summer"))
		  {	  
	        value += "[LectureNo-"+tm.getLecNo()+" -- Lecture Time-"+tm.getLecTime()+" - (Monday-{"+tm.getMon_tchr()+"-"+tm.getMon_sub()+"}"+" Tuesday-{"+tm.getTues_tchr()+"-"+tm.getTues_sub()+"}"+" Wednesday-{"+tm.getWed_tchr()+"-"+tm.getWed_sub()+"}"+" Thruday-{"+tm.getThur_tchr()+"-"+tm.getThur_sub()+"}"+" Friday-{"+tm.getFri_tchr()+"-"+tm.getFri_sub()+"} - (Saturday-{"+tm.getSat_tchr()+"-"+tm.getSat_sub()+"}"+")]";
		  }
		  else
		  {
		        value += "[LectureNo-"+tm.getLecNo()+" -- Lecture Time-"+tm.getWinterLecTime()+" - (Monday-{"+tm.getMon_tchr()+"-"+tm.getMon_sub()+"}"+" Tuesday-{"+tm.getTues_tchr()+"-"+tm.getTues_sub()+"}"+" Wednesday-{"+tm.getWed_tchr()+"-"+tm.getWed_sub()+"}"+" Thruday-{"+tm.getThur_tchr()+"-"+tm.getThur_sub()+"}"+" Friday-{"+tm.getFri_tchr()+"-"+tm.getFri_sub()+"} - (Saturday-{"+tm.getSat_tchr()+"-"+tm.getSat_sub()+"}"+")]";
		  }
		}
		String refNo = workLg.saveWorkLogMehod(language,"Edit New Time Table","WEB",value,conn);
		return refNo;
	}

	public void makeListForSecondBatch()
	{
		for(int k=0;k<timeTableList.size();k++)
		{
			NewTimeTableInfo temp1=timeTableList2.get(k);
			NewTimeTableInfo temp2=timeTableList.get(k);


			temp2.setMon_tchr(temp1.getMon_tchr());
			temp2.setMon_tchr1(temp1.getMon_tchr1());
			temp2.setMon_sub(temp1.getMon_sub());

			temp2.setMon_tchr_name(temp1.getMon_tchr_name());
			temp2.setMon_tchr_name1(temp1.getMon_tchr_name1());
			temp2.setMon_sub_name(temp1.getMon_sub_name());

			temp2.setTues_tchr(temp1.getTues_tchr());
			temp2.setTues_tchr1(temp1.getTues_tchr1());
			temp2.setTues_sub(temp1.getTues_sub());

			temp2.setTues_tchr_name(temp1.getTues_tchr_name());
			temp2.setTues_tchr_name1(temp1.getTues_tchr_name1());
			temp2.setTues_sub_name(temp1.getTues_sub_name());

			temp2.setWed_tchr(temp1.getWed_tchr());
			temp2.setWed_tchr1(temp1.getWed_tchr1());
			temp2.setWed_sub(temp1.getWed_sub());

			temp2.setWed_tchr_name(temp1.getWed_tchr_name());
			temp2.setWed_tchr_name1(temp1.getWed_tchr_name1());
			temp2.setWed_sub_name(temp1.getWed_sub_name());

			temp2.setThur_tchr(temp1.getThur_tchr());
			temp2.setThur_tchr1(temp1.getThur_tchr1());
			temp2.setThur_sub(temp1.getThur_sub());

			temp2.setThur_tchr_name(temp1.getThur_tchr_name());
			temp2.setThur_tchr_name1(temp1.getThur_tchr_name1());
			temp2.setThur_sub_name(temp1.getThur_sub_name());

			temp2.setFri_tchr(temp1.getFri_tchr());
			temp2.setFri_tchr1(temp1.getFri_tchr1());
			temp2.setFri_sub(temp1.getFri_sub());

			temp2.setFri_tchr_name(temp1.getFri_tchr_name());
			temp2.setFri_tchr_name1(temp1.getFri_tchr_name1());
			temp2.setFri_sub_name(temp1.getFri_sub_name());

			temp2.setSat_tchr(temp1.getSat_tchr());
			temp2.setSat_tchr1(temp1.getSat_tchr1());
			temp2.setSat_sub(temp1.getSat_sub());

			temp2.setSat_tchr_name(temp1.getSat_tchr_name());
			temp2.setSat_tchr_name1(temp1.getSat_tchr_name1());
			temp2.setSat_sub_name(temp1.getSat_sub_name());
		}
	}

	public ArrayList<TimeTableComponentInfo> getChangeTableList() {
		return changeTableList;
	}
	public void setChangeTableList(ArrayList<TimeTableComponentInfo> changeTableList) {
		this.changeTableList = changeTableList;
	}
	public boolean isShowTable() {
		return showTable;
	}
	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}
	public boolean isDisableTable() {
		return disableTable;
	}
	public void setDisableTable(boolean disableTable) {
		this.disableTable = disableTable;
	}
	public boolean isCheckNewTimeTable() {
		return checkNewTimeTable;
	}
	public void setCheckNewTimeTable(boolean checkNewTimeTable) {
		this.checkNewTimeTable = checkNewTimeTable;
	}
	public ArrayList<SelectItem> getTeacherList() {
		return teacherList;
	}
	public void setTeacherList(ArrayList<SelectItem> teacherList) {
		this.teacherList = teacherList;
	}
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}
	public String getSelectedCourse() {
		return selectedCourse;
	}
	public void setSelectedCourse(String selectedCourse) {
		this.selectedCourse = selectedCourse;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public TimeTableSettingInfo getSchedule_info() {
		return schedule_info;
	}
	public void setSchedule_info(TimeTableSettingInfo schedule_info) {
		this.schedule_info = schedule_info;
	}
	public int getNoOfLec() {
		return noOfLec;
	}
	public void setNoOfLec(int noOfLec) {
		this.noOfLec = noOfLec;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public ArrayList<NewTimeTableInfo> getTimeTableList() {
		return timeTableList;
	}
	public void setTimeTableList(ArrayList<NewTimeTableInfo> timeTableList) {
		this.timeTableList = timeTableList;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList<TimeTableComponentInfo> getSelectedList() {
		return selectedList;
	}
	public void setSelectedList(ArrayList<TimeTableComponentInfo> selectedList) {
		this.selectedList = selectedList;
	}
	public boolean isDisableCheckBox() {
		return disableCheckBox;
	}
	public void setDisableCheckBox(boolean disableCheckBox) {
		this.disableCheckBox = disableCheckBox;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public CourseInfo getCourseDetail() {
		return courseDetail;
	}
	public void setCourseDetail(CourseInfo courseDetail) {
		this.courseDetail = courseDetail;
	}
	public ArrayList<SelectItem> getCourseNameList() {
		return courseNameList;
	}
	public void setCourseNameList(ArrayList<SelectItem> courseNameList) {
		this.courseNameList = courseNameList;
	}
	public ArrayList<SelectItem> getBranchNameList() {
		return branchNameList;
	}
	public void setBranchNameList(ArrayList<SelectItem> branchNameList) {
		this.branchNameList = branchNameList;
	}


	public String getWinterStartTime() {
		return winterStartTime;
	}


	public void setWinterStartTime(String winterStartTime) {
		this.winterStartTime = winterStartTime;
	}

}
