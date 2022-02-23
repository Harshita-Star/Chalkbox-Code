package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import timetable_module.CourseInfo;
import timetable_module.NewTimeTableInfo;
import timetable_module.TimeTableSettingInfo;

@ManagedBean(name="classWiseTimeTbaleJson")
@ViewScoped
public class ClassWiseTimeTableJson implements Serializable {

	String json;
	String SchoolId="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	private static final long serialVersionUID = 1L;
	String selectedCourse,selectedBranch,selectedSem_Year,selectedSection,type,login_teacher_id,dept,courseType="Semester",batch1,batch2,startTime, searchType="current";
	ArrayList<SelectItem> courseList,branchList,semYearList,sectionList;
	boolean disableBranch;
	CourseInfo courseDetail;
	TimeTableSettingInfo schedule_info;
	int noOfLec,flag;
	ArrayList<NewTimeTableInfo> timeTableList,timeTableList2,finalTimeTableList;

	public ClassWiseTimeTableJson()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			selectedCourse=params.get("class_id");
			selectedSection=params.get("section_id");
			SchoolId=params.get("Schoolid");
			ArrayList<NewTimeTableInfo> tempList = new ArrayList<>();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList sInfo=DBJ.fullSchoolInfo(SchoolId, conn);

				schedule_info = DBJ.timeTableInfo(SchoolId,conn);

				if(schedule_info!=null)
				{
					noOfLec = Integer.valueOf(schedule_info.getNo_of_lec());
					String timeOfLec[] =  schedule_info.getTime_of_lec().split(",");
					startTime =  schedule_info.getStart_time();
					
					String wintimeOfLec[] =  schedule_info.getWinterLecTime().split(",");
					String winterStartTime =  schedule_info.getWinterStartTime();
					
					timeTableList = new ArrayList<>();
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
					flag = DBJ.timeTableDetail(tempList,noOfLec,startTime,selectedCourse,selectedSection,selectedSem_Year,tableName,SchoolId,conn);
					if(flag==0){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Time Table Found"));
					}
					timeTableList=tempList;

					if(flag==0)
					{
						finalTimeTableList=new ArrayList<>();
						finalTimeTableList.addAll(timeTableList);

						JSONArray arr=new JSONArray();
						for(NewTimeTableInfo ls:finalTimeTableList)
						{
							JSONObject obj = new JSONObject();
							obj.put("Lec_No", sInfo.getLectureLabel()+" "+ls.getLecNo());
							if(sInfo.getTimeTableSchedule().equalsIgnoreCase("summer"))
							{
								obj.put("lec_time", ls.getLecTime());
							}
							else
							{
								obj.put("lec_time", ls.getWinterLecTime());
							}
							obj.put("mon_tec", ls.getMon_tchr_name());
							obj.put("mon_sub", ls.getMon_sub_name());
							obj.put("tue_tec", ls.getTues_tchr_name());
							obj.put("tue_sub", ls.getTues_sub_name());
							obj.put("wed_tec", ls.getWed_tchr_name());
							obj.put("wed_sub", ls.getWed_sub_name());
							obj.put("thu_tec", ls.getThur_tchr_name());
							obj.put("thu_sub", ls.getThur_sub_name());
							obj.put("fri_tec", ls.getFri_tchr_name());
							obj.put("fri_sub", ls.getFri_sub_name());
							obj.put("sat_tec", ls.getSat_tchr_name());
							obj.put("sat_sub", ls.getSat_sub_name());
							arr.add(obj);
						}

						json=arr.toJSONString();
					}
					else
					{
						finalTimeTableList=new ArrayList<>();
						for(NewTimeTableInfo first : timeTableList)
						{
							NewTimeTableInfo feed=new NewTimeTableInfo();
							feed.setLecNo(first.getLecNo());
							
							if(sInfo.getTimeTableSchedule().equalsIgnoreCase("summer"))
							{
								feed.setLecTime(first.getLecTime());
							}
							else
							{
								feed.setLecTime(first.getWinterLecTime());
							}
							
							//for(NewTimeTableInfo second : timeTableList2)
							//					{
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
							/*}*/

							finalTimeTableList.add(feed);
						}
						//JSONObject mainobj = new JSONObject();
						JSONArray arr=new JSONArray();
						for(NewTimeTableInfo ls:finalTimeTableList)
						{
							JSONObject obj = new JSONObject();
							obj.put("Lec_No", sInfo.getLectureLabel()+" "+ls.getLecNo());
							if(sInfo.getTimeTableSchedule().equalsIgnoreCase("summer"))
							{
								obj.put("lec_time", ls.getLecTime());
							}
							else
							{
								obj.put("lec_time", ls.getWinterLecTime());
							}
							//obj.put("lec_time", ls.getLecTime());
							obj.put("mon_tec", ls.getMon_tchr_name());
							obj.put("mon_sub", ls.getMon_sub_name());
							obj.put("tue_tec", ls.getTues_tchr_name());
							obj.put("tue_sub", ls.getTues_sub_name());
							obj.put("wed_tec", ls.getWed_tchr_name());
							obj.put("wed_sub", ls.getWed_sub_name());
							obj.put("thu_tec", ls.getThur_tchr_name());
							obj.put("thu_sub", ls.getThur_sub_name());
							obj.put("fri_tec", ls.getFri_tchr_name());
							obj.put("fri_sub", ls.getFri_sub_name());
							obj.put("sat_tec", ls.getSat_tchr_name());
							obj.put("sat_sub", ls.getSat_sub_name());
							arr.add(obj);
						}

						json=arr.toJSONString();
					}
				}
				else
				{
					JSONArray arr=new JSONArray();
					JSONObject obj = new JSONObject();
					obj.put("Lec_No", "N.A.");
					obj.put("lec_time", "N.A.");
					obj.put("mon_tec", "N.A.");
					obj.put("mon_sub", "N.A.");
					obj.put("tue_tec", "N.A.");
					obj.put("tue_sub", "N.A.");
					obj.put("wed_tec", "N.A.");
					obj.put("wed_sub", "N.A.");
					obj.put("thu_tec", "N.A.");
					obj.put("thu_sub", "N.A.");
					obj.put("fri_tec", "N.A.");
					obj.put("fri_sub", "N.A.");
					obj.put("sat_tec", "N.A.");
					obj.put("sat_sub", "N.A.");
					arr.add(obj);

					json=arr.toJSONString();
				}
			}
			else
			{
				JSONArray arr=new JSONArray();
				JSONObject obj = new JSONObject();
				obj.put("Lec_No", "N.A.");
				obj.put("lec_time", "N.A.");
				obj.put("mon_tec", "N.A.");
				obj.put("mon_sub", "N.A.");
				obj.put("tue_tec", "N.A.");
				obj.put("tue_sub", "N.A.");
				obj.put("wed_tec", "N.A.");
				obj.put("wed_sub", "N.A.");
				obj.put("thu_tec", "N.A.");
				obj.put("thu_sub", "N.A.");
				obj.put("fri_tec", "N.A.");
				obj.put("fri_sub", "N.A.");
				obj.put("sat_tec", "N.A.");
				obj.put("sat_sub", "N.A.");
				arr.add(obj);

				json=arr.toJSONString();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


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


}
