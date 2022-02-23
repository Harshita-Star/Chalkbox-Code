package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import timetable_module.CourseInfo;
import timetable_module.DataBase;
import timetable_module.DataBaseMethodsTimeTable;
import timetable_module.NewTimeTableInfo;
import timetable_module.TimeTableSettingInfo;

@ManagedBean(name="studentTimeTable")
@ViewScoped

public class StudentTimeTableBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selectedCourse,selectedBranch,selectedSem_Year,selectedSection,type,file,login_teacher_id,dept,courseType="Semester",batch1,batch2,startTime, searchType="current";
	ArrayList<SelectItem> courseList,branchList,semYearList,sectionList;
	boolean disableBranch,showImage,showManual;
	CourseInfo courseDetail;
	TimeTableSettingInfo schedule_info;
	int noOfLec,flag;
	ArrayList<NewTimeTableInfo> timeTableList,timeTableList2,finalTimeTableList;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schoolId;
    DataBaseMethodsTimeTable dtb = new DataBaseMethodsTimeTable();

	public StudentTimeTableBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		searchType="current";

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String id = (String) ss.getAttribute("username");
		schoolId = obj1.schoolId();
		StudentInfo stuinfo = obj1.studentDetailslistByAddNo(schoolId,id, conn);
		selectedSection = stuinfo.getSectionid();
		selectedCourse = stuinfo.getClassId();

		SchoolInfoList list=obj1.fullSchoolInfo(conn);
		if(list.getTimetable().equalsIgnoreCase("Manual"))
		{
			showManual=true;
			showImage=false;
			checkSubject();
		}
		else
		{
			showManual=false;
			showImage=true;
			file	=DBJ.viewInsTimeTable(selectedCourse,selectedSection,schoolId,conn);
		}



		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}


	public void checkSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		batch1=obj1.sectionNameByIdSchid(schoolId,selectedSection,conn);
		timeTableList=showTable();

		/*	batch2=new DataBase().sectionNameById(selectedSection)+"2";
		timeTableList2=showTable(batch2);*/
		finalPrint();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<NewTimeTableInfo> showTable()
	{
		selectedSem_Year=obj1.schoolId();
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<NewTimeTableInfo> tempList = new ArrayList<>();
		schedule_info = new DataBase().timeTableInfo(conn,schoolId);
		int lunchAfter=Integer.valueOf(schedule_info.getLunch_after());
		String lunchTime=schedule_info.getTime_of_lunch();

		noOfLec = Integer.valueOf(schedule_info.getNo_of_lec());
		String timeOfLec[] =  schedule_info.getTime_of_lec().split(",");
		startTime =  schedule_info.getStart_time();
		timeTableList = new ArrayList<>();
		for(int i=0;i<noOfLec;i++)
		{
			NewTimeTableInfo info = new NewTimeTableInfo();
			try
			{
				info.setLecTime(startTime+" - "+timeOfLec[i]);
				info.setLecNo(String.valueOf(i+1));
				info.setCourseId(selectedCourse);
				info.setSectionId(selectedSection);
				startTime=timeOfLec[i];
				if(i==lunchAfter-1)
				{
					String temp[]=startTime.split(":");
					String lunchtemp[]=lunchTime.split(":");

					int x=Integer.parseInt(temp[0])+Integer.parseInt(lunchtemp[0]);
					int y=Integer.parseInt(temp[1])+Integer.parseInt(lunchtemp[1]);
					startTime=x+":"+y;
				}
			}
			catch (Exception e )
			{
				e.printStackTrace();
			}
			tempList.add(info);
		}
		String tableName="time_table";
		if(searchType.equals("current"))
		{
			tableName="time_table";
		}
		else
		{
			tableName="temp_time_table";
		}
		flag = dtb.timeTableDetail(tempList,/*noOfLec,startTime,*/selectedCourse,selectedSection,selectedSem_Year,tableName,conn);
		if(flag==0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Time Table Found"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tempList;
	}

	public void finalPrint()
	{
		if(flag==0)
		{
			finalTimeTableList=new ArrayList<>();
			finalTimeTableList.addAll(timeTableList);
		}
		else
		{
			finalTimeTableList=new ArrayList<>();
			for(NewTimeTableInfo first : timeTableList)
			{
				NewTimeTableInfo feed=new NewTimeTableInfo();
				feed.setLecNo(first.getLecNo());
				feed.setLecTime(first.getLecTime());
				//for(NewTimeTableInfo second : timeTableList2)
				//{
				feed.setMon_tchr_name(first.getMon_tchr_name());
				//feed.setMon_tchr_name1(first.getMon_tchr_name1());
				feed.setMon_sub_name(first.getMon_sub_name());

				feed.setTues_tchr_name(first.getTues_tchr_name());
				//feed.setTues_tchr_name1(first.getTues_tchr_name1());
				feed.setTues_sub_name(first.getTues_sub_name());

				feed.setWed_tchr_name(first.getWed_tchr_name());
				//feed.setWed_tchr_name1(first.getWed_tchr_name1());
				feed.setWed_sub_name(first.getWed_sub_name());

				feed.setThur_tchr_name(first.getThur_tchr_name());
				//feed.setThur_tchr_name1(first.getThur_tchr_name1());
				feed.setThur_sub_name(first.getThur_sub_name());

				feed.setFri_tchr_name(first.getFri_tchr_name());
				//feed.setFri_tchr_name1(first.getFri_tchr_name1());
				feed.setFri_sub_name(first.getFri_sub_name());

				feed.setSat_tchr_name(first.getSat_tchr_name());
				//feed.setSat_tchr_name1(first.getSat_tchr_name1());
				feed.setSat_sub_name(first.getSat_sub_name());
				//	}

				finalTimeTableList.add(feed);

			}
		}
	}
	public String getSelectedCourse() {
		return selectedCourse;
	}
	public void setSelectedCourse(String selectedCourse) {
		this.selectedCourse = selectedCourse;
	}
	public String getSelectedBranch() {
		return selectedBranch;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public String getSelectedSem_Year() {
		return selectedSem_Year;
	}
	public void setSelectedSem_Year(String selectedSem_Year) {
		this.selectedSem_Year = selectedSem_Year;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLogin_teacher_id() {
		return login_teacher_id;
	}
	public void setLogin_teacher_id(String login_teacher_id) {
		this.login_teacher_id = login_teacher_id;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getCourseType() {
		return courseType;
	}
	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
	public String getBatch1() {
		return batch1;
	}
	public void setBatch1(String batch1) {
		this.batch1 = batch1;
	}
	public String getBatch2() {
		return batch2;
	}
	public void setBatch2(String batch2) {
		this.batch2 = batch2;
	}
	public ArrayList<SelectItem> getCourseList() {
		return courseList;
	}
	public void setCourseList(ArrayList<SelectItem> courseList) {
		this.courseList = courseList;
	}
	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}
	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public ArrayList<SelectItem> getSemYearList() {
		return semYearList;
	}
	public void setSemYearList(ArrayList<SelectItem> semYearList) {
		this.semYearList = semYearList;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public boolean isDisableBranch() {
		return disableBranch;
	}
	public void setDisableBranch(boolean disableBranch) {
		this.disableBranch = disableBranch;
	}
	public CourseInfo getCourseDetail() {
		return courseDetail;
	}
	public void setCourseDetail(CourseInfo courseDetail) {
		this.courseDetail = courseDetail;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
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
	public ArrayList<NewTimeTableInfo> getTimeTableList2() {
		return timeTableList2;
	}
	public void setTimeTableList2(ArrayList<NewTimeTableInfo> timeTableList2) {
		this.timeTableList2 = timeTableList2;
	}
	public ArrayList<NewTimeTableInfo> getFinalTimeTableList() {
		return finalTimeTableList;
	}
	public void setFinalTimeTableList(ArrayList<NewTimeTableInfo> finalTimeTableList) {
		this.finalTimeTableList = finalTimeTableList;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}


	public String getFile() {
		return file;
	}


	public void setFile(String file) {
		this.file = file;
	}


	public boolean isShowImage() {
		return showImage;
	}


	public void setShowImage(boolean showImage) {
		this.showImage = showImage;
	}


	public boolean isShowManual() {
		return showManual;
	}


	public void setShowManual(boolean showManual) {
		this.showManual = showManual;
	}
}
