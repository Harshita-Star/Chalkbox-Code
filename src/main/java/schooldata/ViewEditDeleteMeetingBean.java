package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.shaded.json.JSONObject;

import Json.DataBaseMeathodJson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
@ManagedBean(name="viewEditDeleteMeeting")
@ViewScoped
public class ViewEditDeleteMeetingBean implements Serializable
{
	ArrayList<SelectItem> classSection,sectionList, subjectList, teacherList;
	String selectedCLassSection, staff,userType,schoolid;
	ArrayList<String> selectedSectionList;
	Date startDate,endDate;
	ArrayList<MeetingInfo>list=new ArrayList<>();
	ArrayList<MeetingInfo>selectedList=new ArrayList<>();
	MeetingInfo selectedMeeting;
	boolean show=false,renderStudentVisiblityInEdit,showAdmin=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	private DBMethodsNew dbNewObj = new DBMethodsNew();
	String empId;
	SchoolInfoList ls = new SchoolInfoList();
	
	public ViewEditDeleteMeetingBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ls=new DatabaseMethods1().fullSchoolInfo(conn);
		startDate = new Date();
		endDate = new Date();
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		schoolid=(String) ses.getAttribute("schoolid");
		try
		{
			String categid = obj.employeeCategoryIdByName("Teacher", conn);
			teacherList = obj.allteacherOnly(categid,schoolid,conn);
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection=obj.allClass(conn);
				renderStudentVisiblityInEdit = true;
				showAdmin=true;

			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
 					|| userType.equalsIgnoreCase("Administrative Officer"))
 			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection = obj.cordinatorClassList(empid, schoolid, conn);
				renderStudentVisiblityInEdit = true;
				showAdmin=true;
 			}
			else
			{
				 empId=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				renderStudentVisiblityInEdit = false;
				showAdmin=false;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


	}

	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(startDate.after(endDate))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select dates properly"));
		}
		else
		{
			if(showAdmin)
			{
				list=dbNewObj.allMeetingForManage(startDate,endDate,selectedCLassSection,schoolid,conn);
			}
			else
			{
				list=dbNewObj.allMeetingForManageTeacher(startDate, endDate, empId,schoolid, conn);
			}
		 if(list.size()==0)
		 {
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No meeting found"));
			 show = false; 
		 }
		 else {
		 show=true;
		 }
		} 
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update() 
	{
		if(selectedMeeting.getStartTime().after(selectedMeeting.getEndTime())) 
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Start Time and End Time Properly"));
		}	
		else
		{
			if(selectedMeeting.getZoomId().trim().equalsIgnoreCase(""))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Enter Zoom Id Properly"));
			}
			else
			{
				
				SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");

				String addDateStr = formatter1.format(selectedMeeting.getAddDate());

				String startDateStr = formatter3.format(selectedMeeting.getStartTime());
				startDateStr = addDateStr + " " + startDateStr;

				String endDateStr = formatter3.format(selectedMeeting.getEndTime());
				endDateStr = addDateStr + " " + endDateStr;
				int duration = 0;
				try {
					selectedMeeting.setStartTime(formatter2.parse(startDateStr));
					selectedMeeting.setEndTime(formatter2.parse(endDateStr));
					
					long difference_In_Time = selectedMeeting.getEndTime().getTime() - selectedMeeting.getStartTime().getTime();
					long difference_In_Minutes = (difference_In_Time / (1000 * 60));
					duration = Integer.parseInt(String.valueOf(difference_In_Minutes));
					if(duration>40)
					{
						duration = 40;
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Connection conn=DataBaseConnection.javaConnection();
				int i=obj.editMeeting(selectedMeeting,staff,conn);
				if(i>=1)
				{
					if(selectedMeeting.getMeetingId()==null || selectedMeeting.getMeetingId().equals("")) {
						
					}else {
						String topic = selectedMeeting.getSubjectName() + " - " + startDateStr;
						
						updateZoomMeetingAPI(selectedMeeting.getMeetingId(), duration, selectedMeeting.getStartTime(), topic, ls.getJwt());
					}
					
					if(showAdmin)
					{
						list=dbNewObj.allMeetingForManage(startDate,endDate,selectedCLassSection,schoolid,conn);
					}
					else
					{
						list=dbNewObj.allMeetingForManageTeacher(startDate, endDate, empId,schoolid, conn);
					}
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Meeting Updated Successfully"));
					PrimeFaces current = PrimeFaces.current();
					current.executeScript("PF('dlg2').hide();");
					
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
				}
				
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deleteNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
	

		int i=obj.deleteMeeting(selectedMeeting.getId(),schoolid,conn);
		if(i>=1)
		{
			if(selectedMeeting.getMeetingId()==null || selectedMeeting.getMeetingId().equals("")) {
				
			}else {
				deleteZoomMeetingAPI(selectedMeeting.getMeetingId(), ls.getJwt());
			}
				
			if(showAdmin)
			{
				list=dbNewObj.allMeetingForManage(startDate,endDate,selectedCLassSection,schoolid,conn);
			}
			else
			{
				list=dbNewObj.allMeetingForManageTeacher(startDate, endDate, empId,schoolid, conn);
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected Meeting Deleted Successfully"));
			
			if(list.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No meeting found"));
				show = false;
			}
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void deleteMultiple()
	{
	  if(selectedList.size()==0)
	  {
		 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select a Meeting"));
		 show = false;
	  }
      else
	 {
		Connection conn=DataBaseConnection.javaConnection();
		int i = 0;
		for(MeetingInfo ss : selectedList)
		{	
			 i = obj.deleteMeeting(ss.getId(),schoolid,conn); 
			 if(i>=1)
			 {
				 if(selectedMeeting.getMeetingId()==null || selectedMeeting.getMeetingId().equals("")) {
						
					}else {
						deleteZoomMeetingAPI(ss.getMeetingId(), ls.getJwt());
					}
			 }
		}
		if(i>=1)
		{
			
			if(showAdmin)
			{
				list=dbNewObj.allMeetingForManage(startDate,endDate,selectedCLassSection,schoolid,conn);
			}
			else
			{
				list=dbNewObj.allMeetingForManageTeacher(startDate, endDate, empId,schoolid, conn);
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected Meeting Deleted Successfully"));
			selectedList = new ArrayList<MeetingInfo>();
			if(list.size()==0)
			{
				 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No meeting found"));
				show = false;
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 } 
	}

	public void deleteZoomMeetingAPI(String meetingId, String jwt)
	{
		try 
		{
			OkHttpClient client = new OkHttpClient().newBuilder()
					  .build();
			MediaType mediaType = MediaType.parse("text/plain");
			RequestBody body = RequestBody.create(mediaType, "");
			Request request = new Request.Builder()
			  .url("https://api.zoom.us/v2/meetings/"+meetingId)
			  .method("DELETE", body)
			  .addHeader("Authorization", "Bearer "+jwt)
			  .build();
			Response response = client.newCall(request).execute();
//			ResponseBody respBody = response.body();
//			String strResponse = respBody.string();
//			JSONObject robj = new JSONObject(strResponse);
//			 // System.out.println(strResponse);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateZoomMeetingAPI(String meetingId, int duration, Date startDate, String topic, String jwt)
	{
		String strStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate).replace(" ", "T");
		TreeMap<String, Object> paramMap = new TreeMap<>();
		paramMap.put("topic" ,topic);
		paramMap.put("start_time" ,strStartDate);
		paramMap.put("duration", duration);
		JSONObject obj = new JSONObject(paramMap);
		String params = obj.toString();
		
		try 
		{
			OkHttpClient client = new OkHttpClient().newBuilder()
					  .build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, params);
			Request request = new Request.Builder()
			  .url("https://api.zoom.us/v2/meetings/"+meetingId)
			  .method("PATCH", body)
			  .addHeader("Authorization", "Bearer "+jwt)
			  .addHeader("Content-Type", "application/json")
			  .build();
			Response response = client.newCall(request).execute();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<MeetingInfo> getList() {
		return list;
	}
	public void setList(ArrayList<MeetingInfo> list) {
		this.list = list;
	}

	public MeetingInfo getselectedMeeting() {
		return selectedMeeting;
	}
	public void setselectedMeeting(MeetingInfo selectedMeeting) {
		this.selectedMeeting = selectedMeeting;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getStaff() {
		return staff;
	}
	public void setStaff(String staff) {
		this.staff = staff;
	}


	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ArrayList<MeetingInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<MeetingInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public ArrayList<String> getSelectedSectionList() {
		return selectedSectionList;
	}

	public void setSelectedSectionList(ArrayList<String> selectedSectionList) {
		this.selectedSectionList = selectedSectionList;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public boolean isRenderStudentVisiblityInEdit() {
		return renderStudentVisiblityInEdit;
	}

	public void setRenderStudentVisiblityInEdit(boolean renderStudentVisiblityInEdit) {
		this.renderStudentVisiblityInEdit = renderStudentVisiblityInEdit;
	}

	public boolean isShowAdmin() {
		return showAdmin;
	}

	public void setShowAdmin(boolean showAdmin) {
		this.showAdmin = showAdmin;
	}

	public ArrayList<SelectItem> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(ArrayList<SelectItem> teacherList) {
		this.teacherList = teacherList;
	}

	public MeetingInfo getSelectedMeeting() {
		return selectedMeeting;
	}

	public void setSelectedMeeting(MeetingInfo selectedMeeting) {
		this.selectedMeeting = selectedMeeting;
	}
	






}


/*
 <ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:p="http://primefaces.org/ui" template="/WEB-INF/template.xhtml">

	<ui:define name="title">VIewMeeting</ui:define>

	<ui:define name="content">

		<div class="ui-fluid">
			<div class="ui-g">
				<div class="ui-g-12">
					<div class="card card-w-title">
						<h:form id="form">
							<p:growl>
								<p:autoUpdate />
							</p:growl>
							<p:panel header="View/Edit/Delete Meeting">
								<p:dataTable value="#{viewEditDeleteMeeting.list}" var="var"
									reflow="true" selection="#{viewEditDeleteMeeting.selectedList}" rowKey="#{var.sno}" paginator="true" rows="10"
									paginatorTemplate="{RowsPerPageDropdown} {FirstPageLink} {PreviousPageLink} {CurrentPageReport} {NextPageLink} {LastPageLink}"
									rowsPerPageTemplate="10,20,30" filterDelay="1000">
									
									<f:facet name="header">
									 <p:commandButton value="Delete Multiple Meetings" action="#{viewEditDeleteMeeting.deleteMultiple}" update=":form" ></p:commandButton>
									</f:facet>
									
									<f:facet name="footer">
									 <p:commandButton value="Delete Multiple Meetings" action="#{viewEditDeleteMeeting.deleteMultiple}" update=":form" ></p:commandButton>
									</f:facet>

                                   <p:column selectionMode="multiple" exportable="false" style="width:50px;">
                                    </p:column>  

									<p:column headerText="S.No.">
										<p:outputLabel value="#{var.sno}"></p:outputLabel>
									</p:column>

									<p:column headerText="Add Date" filterBy="#{var.addDateStr}" filterMatchMode="contains">
										<p:outputLabel value="#{var.addDateStr}"></p:outputLabel>
									</p:column>
									
									<p:column headerText="Class" filterBy="#{var.className}" filterMatchMode="contains">
										<p:outputLabel value="#{var.className}"></p:outputLabel>
									</p:column>
								

									<p:column headerText="Section" filterBy="#{var.sectionName}" filterMatchMode="contains">
										<p:outputLabel value="#{var.sectionName}"></p:outputLabel>
									</p:column>
									
									<p:column headerText="Subject" filterBy="#{var.subjectName}" filterMatchMode="contains">
										<p:outputLabel value="#{var.subjectName}"></p:outputLabel>
									</p:column>
									
									<p:column headerText="Start Time">
										<p:outputLabel value="#{var.startTimeStr}"></p:outputLabel>
									</p:column>
									
									<p:column headerText="End Time">
										<p:outputLabel value="#{var.endTimeStr}"></p:outputLabel>
									</p:column>
									
									<p:column headerText="Show to Student " filterBy="#{var.studentVisibleStatus}" filterMatchMode="contains">
										<p:outputLabel value="#{var.studentVisibleStatus}"></p:outputLabel>
									</p:column>
									
									<p:column headerText="Zoom Id">
									   <a href="#{var.zoomId}" target="_blank"><p:outputLabel value="#{var.zoomId}"></p:outputLabel></a>
									</p:column>


									<p:column headerText="Delete">
										<p:commandButton icon="ui-icon-trash" update=":form"
											action="#{viewEditDeleteMeeting.deleteNow}">
											<f:setPropertyActionListener target="#{viewEditDeleteMeeting.selectedMeeting}"
												value="#{var}"></f:setPropertyActionListener>
										      <p:confirm header="Confirmation"
												message="Are you sure for delete this Flyer ?"
												icon="ui-icon-alert" />
										</p:commandButton>
									</p:column>
									
										<p:column headerText="Edit">
										<p:commandButton  update=":formEdit"
											oncomplete="PF('dlg2').show();" icon="ui-icon-pencil">
											<f:setPropertyActionListener
												target="#{viewEditDeleteMeeting.selectedMeeting}"
												value="#{var}"></f:setPropertyActionListener>
										</p:commandButton>
									</p:column>

								</p:dataTable>
							</p:panel>
						</h:form>
					</div>
					<p:confirmDialog global="true" showEffect="fade" hideEffect="fade">
							<p:commandButton value="Yes" type="button"
								styleClass="ui-confirmdialog-yes" icon="ui-icon-check" />
							<p:commandButton value="No" type="button"
								styleClass="ui-confirmdialog-no" icon="ui-icon-close" />
						</p:confirmDialog>
						
					<div class="card card-w-title">
						
						
						<h:form id="formEdit">
						 	<p:dialog resizable="false" widgetVar="dlg2"
									header="Edit Meeting"
									showEffect="fade" hideEffect="explode" modal="true">
									<div class="ui-g form-group">
										<p:panelGrid columns="2"
											styleClass="ui-panelgrid-blank form-group"
											style="border:0px none; background-color:transparent;">
											
									
											<p:outputLabel for="@next" style="color:red" value="Start Time:"></p:outputLabel>
											<p:calendar value="#{viewEditDeleteMeeting.selectedMeeting.startTime}" pattern="HH:mm" timeOnly="true" required="true" requiredMessage="Please Enter Start TIme"></p:calendar>
											
											<p:outputLabel for="@next" style="color:red" value="End Time:"></p:outputLabel>
											<p:calendar value="#{viewEditDeleteMeeting.selectedMeeting.endTime}" pattern="HH:mm" timeOnly="true"  required="true" requiredMessage="Please Enter End TIme"></p:calendar>
											
									        <p:outputLabel for="@next" style="color:red" value="Zoom Id:"></p:outputLabel>
									        <p:inputText value="#{viewEditDeleteMeeting.selectedMeeting.zoomId}" required="true" requiredMessage="Please Enter Zoom ID"></p:inputText>
											
											<p:outputLabel value="Show To Student" for="@next" style="color:red" rendered="#{viewEditDeleteMeeting.renderStudentVisiblityInEdit}"></p:outputLabel>
											<p:selectOneRadio
												value="#{viewEditDeleteMeeting.selectedMeeting.studentVisibleStatus}" required="true" rendered="#{viewEditDeleteMeeting.renderStudentVisiblityInEdit}" 
												requiredMessage="Please select show to student" filter="true" disabled="#{viewEditDeleteMeeting.selectedMeeting.studentVisibleStatus eq 'yes'}">
												<f:selectItem itemLabel="No" itemValue="no"></f:selectItem>
												<f:selectItem itemLabel="Yes" itemValue="yes"></f:selectItem>
												
											</p:selectOneRadio>
											
											<p:commandButton value="Update " update=":formEdit,:form"
												action="#{viewEditDeleteMeeting.update}"></p:commandButton>
										</p:panelGrid>
									</div>
								</p:dialog>
						</h:form>

					</div>
				</div>
			</div>
		</div>

	</ui:define>

</ui:composition>


 */
