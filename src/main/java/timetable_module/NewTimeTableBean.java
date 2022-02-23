package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import schooldata.Category;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SideMenuBean;

@ManagedBean(name = "newTimeTableBean")
@ViewScoped

public class NewTimeTableBean implements Serializable {
	private static final long serialVersionUID = 1L;
	ArrayList<TimeTableComponentInfo> selectedList;
	Date effectiveDate, minDate;
	boolean showTable, disableTable, checkNewTimeTable, disableCheckBox = true;
	CourseInfo courseDetail;
	ArrayList<SelectItem> teacherList, subjectList,classList;
	ArrayList<Category> selectedclass, changeTableListt;
	String selectedclasss, selectedsection,selectedValue;
	String startTime,winterStartTime, selectedBranch, selectedSem_Year, selectedSection,
	selectedBatch;
	TimeTableSettingInfo schedule_info;
	int noOfLec, flag;
	String allCourse = "", allSection = "", allbranch = "", allSem = "", allbatch = "", message = "", actionName,
			className, nameOfClass;
	ArrayList<NewTimeTableInfo> timeTableList=new ArrayList<>(), timeTableList2=new ArrayList<>();
	int selectedRow, submitFlag = 0, dataTableFillSize = 0;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	DataBaseMethodsTimeTable objTimeTable = new DataBaseMethodsTimeTable();
	DataBase obj=new DataBase();

	public NewTimeTableBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,conn);
		classList=obj1.allClass(conn);
		changeTableListt = new ArrayList<>();
		minDate = new Date();
		minDate.setDate(minDate.getDate() + 1);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void continueProcess() {
		Connection conn = DataBaseConnection.javaConnection();
		// String session=DataBase.selectedSessionDetails();
		objTimeTable.deleteTempTimetable(conn, schoolId);
		changeTableListt = new ArrayList<>();
		minDate = new Date();
		minDate.setDate(minDate.getDate() + 1);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createTableList() {
		Connection conn = DataBaseConnection.javaConnection();
		changeTableListt = new ArrayList<>();

		/*checkNewTimeTable=new DataBaseMethodsTimeTable().checkNewTimeTable(selectedValue,conn);
		if(checkNewTimeTable==true)
		{
			PrimeFaces context1 = PrimeFaces.current();
			context1.executeScript("PF('confirmDialog').show();");
		}
		else
		{*/
		changeTableListt=new ArrayList<>();
		objTimeTable.sectionList(selectedValue,changeTableListt, conn);
		showTable=true;
		//}



		showTable = true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void createTimeTable() {

		Connection conn = DataBaseConnection.javaConnection();
		
		FacesContext context = FacesContext.getCurrentInstance();
		selectedRow = ((int) UIComponent.getCurrentComponent(context).getAttributes().get("sno"));
		String categid="";
		String categid1 = obj1.employeeCategoryIdByName("Teacher", conn);
		String categid2 = obj1.employeeCategoryIdByName("Sports Department", conn);
		String categid3 = obj1.employeeCategoryIdByName("Librarian", conn);
		String categid4 = obj1.employeeCategoryIdByName("Principal", conn);
		String categid5 = obj1.employeeCategoryIdByName("Vice Principal", conn);
		String categid6 = obj1.employeeCategoryIdByName("Administrative Officer", conn);
		
		categid=categid1+"','"+categid2+"','"+categid3+"','"+categid4+"','"+categid5+"','"+categid6;
		
		for (Category tt : changeTableListt) {

			if (selectedRow == tt.getSno()) {
				selectedclasss = tt.getClassid();
				selectedsection = String.valueOf(tt.getId());
				subjectList = obj1.allSubjects(tt.getClassid(), conn);
				teacherList = obj1.allteacherOnly(categid,schoolId,conn);
				nameOfClass = (obj1.classname(selectedclasss,schoolId, conn) + " - "
						+ obj1.sectionNameByIdSchid(schoolId,selectedsection, conn));
				timeTableList = showTable(selectedclasss, "time_table");
				PrimeFaces context1 = PrimeFaces.current();
				context1.executeScript("PF('timeTableDialog').show();");
			}
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<NewTimeTableInfo> showTable(String batch, String tableName) {
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<NewTimeTableInfo> tempList = new ArrayList<>();
		schedule_info = obj.timeTableInfo(conn, schoolId);
		int lunchAfter=Integer.valueOf(schedule_info.getLunch_after());
		String lunchTime=schedule_info.getTime_of_lunch();


		noOfLec = Integer.valueOf(schedule_info.getNo_of_lec());
		String timeOfLec[] = schedule_info.getTime_of_lec().split(",");
		String wintimeOfLec[] = schedule_info.getWinterLecTime().split(",");
		startTime = schedule_info.getStart_time();
		winterStartTime = schedule_info.getWinterStartTime();

		for(int i=0;i<noOfLec;i++)
		{
			NewTimeTableInfo info = new NewTimeTableInfo();
			try
			{
				info.setLecTime(startTime+" - "+timeOfLec[i]);
				info.setWinterLecTime(winterStartTime+" - "+wintimeOfLec[i]);
				info.setLecNo(String.valueOf(i+1));
				info.setClassid(selectedclasss);
				// //// // System.out.println("classid"+selectedclasss);
				info.setSectionId(selectedsection);
				info.setSemId(selectedSem_Year);
				// info.setClassid(selectedclasss);
				info.setCategory(selectedsection);
				info.setBatch(batch);
				startTime=timeOfLec[i];
				winterStartTime = wintimeOfLec[i];
				if(i==lunchAfter-1)
				{
					String temp[]=startTime.split(":");
					String tempW[]=winterStartTime.split(":");
					
					String lunchtemp[]=lunchTime.split(":");

					int x=Integer.parseInt(temp[0])+Integer.parseInt(lunchtemp[0]);
					int y=Integer.parseInt(temp[1])+Integer.parseInt(lunchtemp[1]);
					if(y>=60)
					{
						x=x+1;
						y=y-60;
					}
					startTime=x+":"+y;
					
					int a=Integer.parseInt(tempW[0])+Integer.parseInt(lunchtemp[0]);
					int b=Integer.parseInt(tempW[1])+Integer.parseInt(lunchtemp[1]);
					if(b>=60)
					{
						a=a+1;
						b=b-60;
					}
					winterStartTime=a+":"+b;
				}
			}
			catch (Exception e )
			{
				e.printStackTrace();
			}
			tempList.add(info);
		}
		flag = objTimeTable.timeTableDetail(tempList, /* noOfLec,startTime, */selectedclasss, selectedsection,
				selectedSem_Year, tableName, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tempList;
	}

	public String update() {
		Connection conn = DataBaseConnection.javaConnection();

		try {

			message = objTimeTable.checkDuplicateTime(timeTableList, allCourse,
					allbranch/* ,allSection,allSem,allbatch */, conn);
			if (message.equals("none")) {
				int i;
				i = objTimeTable
						.insertTimeTableInTemp(/* selectedCourse,selectedBranch,selectedSem_Year, */timeTableList
								/* ,selectedSection,selectedBatch */, effectiveDate, selectedclasss, selectedsection, conn);
				if (i >= 1) {

					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					submitFlag = submitFlag + 1;
					changeTableListt.remove(selectedRow - 1);

					nameOfClass = obj1.classname(selectedclasss,schoolId, conn) + " - "
							+ obj1.sectionNameByIdSchid(schoolId,selectedsection, conn);
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Time Table For " + nameOfClass + " is prepared!"));

					i = 1;

					nameOfClass = obj1.classname(selectedclasss,schoolId, conn) + " - "
							+ obj1.sectionNameByIdSchid(schoolId,selectedsection, conn);
					PrimeFaces.current().ajax().update("form:data");
					PrimeFaces.current().ajax().update("timeTableForm");

				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error!"));
				}
			} else {
				PrimeFaces.current().ajax().update("alertForm");
				PrimeFaces.current().executeInitScript("PF('alertDialog').show()");
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return actionName;
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(effectiveDate);
		
		SideMenuBean smb = new SideMenuBean();
		
		
		String clasname = obj1.classname(selectedclasss,schoolId, conn);		
		String sectonName = obj1.sectionNameByIdSchid(schoolId,selectedsection, conn);
		
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
		String refNo = workLg.saveWorkLogMehod(language,"Generate Time Table","WEB",value,conn);
		return refNo;
	}
	
	

	public void checkCompleteChange() {
		FacesContext context = FacesContext.getCurrentInstance();
		actionName = (String) UIComponent.getCurrentComponent(context).getAttributes().get("actionName");
		if (submitFlag != 0 && submitFlag != dataTableFillSize) {
			PrimeFaces.current().ajax().update("confirmFormOfBack");
			PrimeFaces.current().executeInitScript("PF('confirmDialogBack').show()");
		} else {
			goMethod();
		}
	}

	public void goFromThisPage() {
		Connection conn = DataBaseConnection.javaConnection();
		// String session=DataBase.selectedSessionDetails();
		objTimeTable.deleteTempTimetable(conn,schoolId);
		goMethod();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void goMethod() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (actionName.equals("logout")) {
				// new DataBase().logoutDetails();
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.invalidate();
				FacesContext.getCurrentInstance().getExternalContext().redirect("loginPage.xhtml");
			} else if (actionName.equals("back")) {
				context.getExternalContext().redirect("timeTableDashboard.xhtml");
			} else if (actionName.equals("home")) {
				context.getExternalContext().redirect("mainDashBoard.xhtml");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void makeListForSecondBatch() {
		for (int k = 0; k < timeTableList.size(); k++) {
			NewTimeTableInfo temp1 = timeTableList2.get(k);
			NewTimeTableInfo temp2 = timeTableList.get(k);

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
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
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
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getWinterStartTime() {
		return winterStartTime;
	}

	public void setWinterStartTime(String winterStartTime) {
		this.winterStartTime = winterStartTime;
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

	public String getSelectedBatch() {
		return selectedBatch;
	}

	public void setSelectedBatch(String selectedBatch) {
		this.selectedBatch = selectedBatch;
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

	public ArrayList<Category> getSelectedclass() {
		return selectedclass;
	}

	public void setSelectedclass(ArrayList<Category> selectedclass) {
		this.selectedclass = selectedclass;
	}

	public String getSelectedclasss() {
		return selectedclasss;
	}

	public void setSelectedclasss(String selectedclasss) {
		this.selectedclasss = selectedclasss;
	}

	public String getSelectedsection() {
		return selectedsection;
	}

	public void setSelectedsection(String selectedsection) {
		this.selectedsection = selectedsection;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public String getNameOfClass() {
		return nameOfClass;
	}

	public void setNameOfClass(String nameOfClass) {
		this.nameOfClass = nameOfClass;
	}

	public ArrayList<Category> getChangeTableListt() {
		return changeTableListt;
	}

	public void setChangeTableListt(ArrayList<Category> changeTableListt) {
		this.changeTableListt = changeTableListt;
	}

	public String getAllCourse() {
		return allCourse;
	}

	public void setAllCourse(String allCourse) {
		this.allCourse = allCourse;
	}

	public String getAllbranch() {
		return allbranch;
	}

	public void setAllbranch(String allbranch) {
		this.allbranch = allbranch;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

}
