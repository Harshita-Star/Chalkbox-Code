package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import exam_module.DataBaseMethodsExam;
import session_work.RegexPattern;



@ManagedBean(name="stdExtraFieldDetail")
@ViewScoped
public class FillStudentExtraFieldDetailsBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<ExtraFieldInfo> studentList;
	String selectedClass,selectedSemester,selectedSection,fillType,remark,copyOrNot,session,remarkFillType,selectedSemesterAuto;
	ArrayList<SelectItem> classList,termList,sectionList;
	boolean showRemark,showOther,showTable,showMsg,showAttendance,disableAttedance,showAttendanceCopy,showAutoAtt,showRemarkFill,showRemarkCopy,showRemarkAuto,disableRemark;
	String maxAtt="",schid;
    String message="",attendanceFillType; 
    String maxAttendance;
    Date startDate,endDate;
    DatabaseMethods1 obj1=new DatabaseMethods1();
    DataBaseMethodsExam objExam=new DataBaseMethodsExam();
    
	public FillStudentExtraFieldDetailsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		schid=obj1.schoolId();
		classList=obj1.allClass(conn);
		startDate = new Date();
		endDate = new Date();
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void showSemester()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 dd=new DatabaseMethods1();
		termList=dd.selectedTermOfClass(selectedClass,conn,session,schid);
		sectionList=dd.allSection(selectedClass,conn);
		fillType ="";
		attendanceFillType ="";
		showTable = false;
		showAutoAtt = false;
		showAttendance = false;
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public void autoFillAtt()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		if(studentList.size()>0) {
		obj1.attendanceSummaryReportForExtraValue(startDate,endDate,selectedSection,schid,session,studentList,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void autoFillRemark()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		if(studentList.size()>0) {
		obj1.remarkSummaryReportForExtraValue(selectedSemesterAuto,selectedSection,schid,session,studentList,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void searchStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new ArrayList<>();
		ArrayList<StudentInfo> tempList=objExam.studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
		for(StudentInfo ll:tempList)
		{
			ExtraFieldInfo list=new ExtraFieldInfo();
			list.setsNo(ll.getsNo());
			list.setStudentId(ll.getAddNumber());
			list.setStudentName(ll.getFullName());
			list.setSrNo(ll.getSrNo());
			list.setClassName(ll.getClassName());
			list.setClassId(ll.getClassId());
			list.setSectionId(ll.getSectionid());
			list.setSectionName(ll.getSectionName());
			list.setBloodGroup(ll.getBloodGroup());
			list.setRollNo(ll.getRollNo());
			list.setCurrentRollNo(ll.getRollNo());
			studentList.add(list);
		}
		
		
		SchoolInfoList info = obj1.fullSchoolInfo(conn);
		if(info.getBranch_id().equals("54"))
		{
			Collections.sort(studentList, new MySalaryComp());
		}
		objExam.checkAlreadyFilledValue1(schid,selectedSection,studentList,selectedSemester,conn);	

		showTable=true;
		
		if((schid.equals("298") && Integer.parseInt(selectedClass)<=8)|| (schid.equals("299") && Integer.parseInt(selectedClass)>2))
		{
			showMsg=true;
		}
		else
		{
			showMsg=false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void copyRemark()
	{
		for(ExtraFieldInfo ll:studentList)
		{
			ll.setRemark(remark);
		}
	}
	
	
	
	
	public void checkFillType()
	{
		showTable=false;
		attendanceFillType ="";
		remarkFillType = "";
		if(fillType.equals("otherValue"))
		{
			showRemark=false;copyOrNot="";remark="";showOther=true;showAttendance=false;
			showAutoAtt = false;showAttendanceCopy =false;showRemarkFill=false;showRemarkCopy =false;showRemarkAuto=false;
		}
		else if(fillType.equals("remark"))
		{
			showRemark=true;showOther=false;showAttendance=false;showRemarkFill=true;
			showAutoAtt = false;showAttendanceCopy =false;
		}
		else {
			showRemark=false;showOther=false;showAttendance=true;
			showAutoAtt = false;showRemarkFill=false;showRemarkCopy =false;showRemarkAuto=false;
		}
	}
	
	public void checkAttendanceFillType()
	{
		showTable = false;
	   	if(fillType.equalsIgnoreCase("Attendance")) {
	   		
	   
		if(attendanceFillType.equalsIgnoreCase("Auto"))
		{
			disableAttedance = true;
			showAttendanceCopy = false;
			showAutoAtt = true;
		}
		else
		{
			disableAttedance = false;
			showAttendanceCopy = true;
			showAutoAtt = false;
		}
	   	}
	   	else {
	   		showAttendanceCopy = false;
	   		showAutoAtt = false;
	   	}
	}
	
	public void checkRemarkFillType()
	{
		showTable = false;
	   	if(fillType.equalsIgnoreCase("remark")) {
	   		
	   
		if(remarkFillType.equalsIgnoreCase("Auto"))
		{
			showRemarkAuto = true;
			showRemarkCopy = false;
			disableRemark = true;
		}
		else
		{
		    showRemarkCopy = true;	
		    showRemarkAuto = false;
		    disableRemark = false;
		}
	   	}
	   	else {
	   		showRemarkAuto = false;
			showRemarkCopy = false;
			disableRemark = false;
	   	}
	   	
	}
	

	public void maxAttendanceApplyAll()
	{
		for(ExtraFieldInfo ee : studentList)
		{
			ee.setMaxAttendance(maxAttendance);
		}
	}

	public void submitValue()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=objExam.addExtraValueOfStudent(studentList,selectedSection,selectedSemester,fillType,conn,session);
		if(i>=1)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String username=(String) ss.getAttribute("username");
			String type=(String) ss.getAttribute("type");
			String schoolid=obj1.schoolId();
			String className=obj1.classname(selectedClass, schoolid, conn);
			String sectionname =obj1.sectionNameByIdSchid(schoolid,selectedSection, conn);
			String termName=obj1.semesterNameFromid(selectedSemester, conn);
			String typeOfArea =fillType;
			
			
			String value="";
			if(fillType.equalsIgnoreCase("Remark"))
			{
				for(ExtraFieldInfo ls:studentList)
				{
					if(value.equals(""))
					{
						value="("+ls.getStudentId()+"----"+ls.getStudentName()+"-----:"+ls.getRemark()+")";
					}
					else
					{
						value=value+"("+ls.getStudentId()+"----"+ls.getStudentName()+"-----:"+ls.getRemark()+")";
					}
				}
					
			}
			
			
			String language=""+className+"-"+sectionname+"\n,"+""+termName+"\n,"+typeOfArea ;
			
		
			
			//language=language;
			
			String addedBy=obj1.employeeNameByuserName(username, conn);
			
			language=language+"\n,"+addedBy+"("+username+"-"+type+")" +"\n"+"  Submitted Successfully";
			
			String refNo =obj1.saveWorkLog(schoolid,language, username, typeOfArea,type, session, "WEB",value, conn);
			if(refNo.equals(""))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
                message="Dear User, \n Some Error Occure Please try again";
				
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Submitted Sucessfully"));
				message="Dear User, \n Student Extra Values/Remark updated successfully. Please Note Down Your Reference No : "+refNo+" For Any Further Query.";
			}
			selectedClass=selectedSection=selectedSemester="";studentList=new ArrayList<>();
			sectionList=termList=new ArrayList<>();showTable=showOther=showRemark=false;fillType=remark=copyOrNot=attendanceFillType=remarkFillType=selectedSemesterAuto="";
			showAttendance=disableAttedance=showAttendanceCopy=showAutoAtt=false;
			startDate = new Date();
			endDate = new Date();
			disableRemark =showRemarkCopy=showRemarkAuto=showRemarkFill=false;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public String getSelectedSemester() {
		return selectedSemester;
	}
	public void setSelectedSemester(String selectedSemester) {
		this.selectedSemester = selectedSemester;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public ArrayList<ExtraFieldInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<ExtraFieldInfo> studentList) {
		this.studentList = studentList;
	}
	public String getFillType() {
		return fillType;
	}
	public void setFillType(String fillType) {
		this.fillType = fillType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCopyOrNot() {
		return copyOrNot;
	}
	public void setCopyOrNot(String copyOrNot) {
		this.copyOrNot = copyOrNot;
	}
	public boolean isShowRemark() {
		return showRemark;
	}
	public void setShowRemark(boolean showRemark) {
		this.showRemark = showRemark;
	}
	public boolean isShowOther() {
		return showOther;
	}
	public void setShowOther(boolean showOther) {
		this.showOther = showOther;
	}
	public boolean isShowTable() {
		return showTable;
	}
	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public String getMaxAtt() {
		return maxAtt;
	}

	public void setMaxAtt(String maxAtt) {
		this.maxAtt = maxAtt;
	}
	

	class MySalaryComp implements Comparator<ExtraFieldInfo>
	{
		@Override
		public int compare(ExtraFieldInfo e1, ExtraFieldInfo e2)
		{

			String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public boolean isShowMsg() {
		return showMsg;
	}

	public void setShowMsg(boolean showMsg) {
		this.showMsg = showMsg;
	}

	public String getMaxAttendance() {
		return maxAttendance;
	}

	public void setMaxAttendance(String maxAttendance) {
		this.maxAttendance = maxAttendance;
	}

	public boolean isShowAttendance() {
		return showAttendance;
	}

	public void setShowAttendance(boolean showAttendance) {
		this.showAttendance = showAttendance;
	}

	public String getAttendanceFillType() {
		return attendanceFillType;
	}

	public void setAttendanceFillType(String attendanceFillType) {
		this.attendanceFillType = attendanceFillType;
	}

	public boolean isDisableAttedance() {
		return disableAttedance;
	}

	public void setDisableAttedance(boolean disableAttedance) {
		this.disableAttedance = disableAttedance;
	}

	public boolean isShowAttendanceCopy() {
		return showAttendanceCopy;
	}

	public void setShowAttendanceCopy(boolean showAttendanceCopy) {
		this.showAttendanceCopy = showAttendanceCopy;
	}

	public boolean isShowAutoAtt() {
		return showAutoAtt;
	}

	public void setShowAutoAtt(boolean showAutoAtt) {
		this.showAutoAtt = showAutoAtt;
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

	public String getRemarkFillType() {
		return remarkFillType;
	}

	public void setRemarkFillType(String remarkFillType) {
		this.remarkFillType = remarkFillType;
	}

	public boolean isShowRemarkFill() {
		return showRemarkFill;
	}

	public void setShowRemarkFill(boolean showRemarkFill) {
		this.showRemarkFill = showRemarkFill;
	}

	public boolean isShowRemarkCopy() {
		return showRemarkCopy;
	}

	public void setShowRemarkCopy(boolean showRemarkCopy) {
		this.showRemarkCopy = showRemarkCopy;
	}

	public boolean isShowRemarkAuto() {
		return showRemarkAuto;
	}

	public void setShowRemarkAuto(boolean showRemarkAuto) {
		this.showRemarkAuto = showRemarkAuto;
	}

	public String getSelectedSemesterAuto() {
		return selectedSemesterAuto;
	}

	public void setSelectedSemesterAuto(String selectedSemesterAuto) {
		this.selectedSemesterAuto = selectedSemesterAuto;
	}

	public boolean isDisableRemark() {
		return disableRemark;
	}

	public void setDisableRemark(boolean disableRemark) {
		this.disableRemark = disableRemark;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
