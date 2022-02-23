package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name="demoteStudentBean")
@ViewScoped
public class DemoteStudentBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	String selectedClass,selectedSection,selectedPromoteToClass,selectedPromoteToSection,session,nextSession;
	ArrayList<SelectItem> sessionList,sectionList2,promotionClassList,classList,sectionList;
	boolean renderTable,renderPromotePanel;
	ArrayList<StudentInfo> studentList, selectedStudentList;
	Date postDate;

	public DemoteStudentBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classList=new DatabaseMethods1().allClass(conn);
		/*sessionList=new ArrayList<SelectItem>();
		String session=DatabaseMethods1.selectedSessionDetails();
		session=session.substring(0,session.indexOf("-"));
		int x=Integer.parseInt(session)-5;

		for(int i=x;i<=x+4;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));
			sessionList.add(item);
		}*/

		session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		String selectSession[]=session.split("-");
		session=(Integer.parseInt(selectSession[0])-1)+"-"+(Integer.parseInt(selectSession[0]));
		nextSession=(Integer.parseInt(selectSession[1]))+"-"+(Integer.parseInt(selectSession[1])+1);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections2()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList2=new DatabaseMethods1().allSection(selectedPromoteToClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectAndShowPromotPanel(){
		if(selectedStudentList.size()>0)
		{
			renderPromotePanel=true;
		}
		else
		{
			renderPromotePanel=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast One Student to Demote."));
		}

	}

	public void promotion()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		boolean check = false;
		/*if(selectedPromoteToClass.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select Demotion Class", "Validation Error"));
		}*/
		double dueFee=0;
		if(selectedStudentList!=null)
		{
			for(StudentInfo ll : selectedStudentList)
			{
				check=DBM.demoteStudentsToClass(ll.getSectionFrom(),ll.getSectionTo(),ll.getAddNumber(),/*postDate,*/session,conn);
				////// // System.out.println(ll.getSectionFrom()+"   "+ll.getSectionTo());
			}
		}

		if(check)
		{
			for(StudentInfo ll : selectedStudentList)
			{
				DBM.deleteTransportEntry(String.valueOf(ll.getAddNumber()),conn);
				dueFee=DBM.totalDueFeeForStudentDemotion(ll.getAddNumber(),session,conn);

				DBM.updatePreviousFees(ll.getAddNumber(), dueFee,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),conn);
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, +selectedStudentList.size()+" Student Demotion was successfull", ""));
			studentList=DBM.getAllStudentStrentgthForDemotion(selectedSection,/*nextSession,*/conn);
			renderPromotePanel=false;
		}

		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Select atleast one student to Demote", ""));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength()
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().getAllStudentStrentgthForDemotion(selectedSection,/*nextSession,*/conn);
		if(studentList.isEmpty()){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record found", "Validation Error"));
			renderTable=false;
		}
		else{
			renderTable=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public boolean isRenderPromotePanel(){
		return renderPromotePanel;
	}
	public void setRenderPromotePanel(boolean renderPromotePanel) {
		this.renderPromotePanel = renderPromotePanel;
	}
	public boolean isRenderTable() {
		return renderTable;
	}
	public void setRenderTable(boolean renderTable) {
		this.renderTable = renderTable;
	}
	public boolean isRenderTableb() {
		return renderTable;
	}
	public void setRenderTableb(boolean renderTableb) {
		this.renderTable = renderTableb;
	}
	public String getSelectedPromoteToClass() {
		return selectedPromoteToClass;
	}
	public void setSelectedPromoteToClass(String selectedPromoteToClass) {
		this.selectedPromoteToClass = selectedPromoteToClass;
	}
	public ArrayList<SelectItem> getPromotionClassList() {
		return promotionClassList;
	}
	public void setPromotionClassList(ArrayList<SelectItem> promotionClassList) {
		this.promotionClassList = promotionClassList;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public boolean isB() {
		return renderTable;
	}
	public void setB(boolean b) {
		this.renderTable = b;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<SelectItem> getSectionList2() {
		return sectionList2;
	}
	public void setSectionList2(ArrayList<SelectItem> sectionList2) {
		this.sectionList2 = sectionList2;
	}
	public String getSelectedPromoteToSection() {
		return selectedPromoteToSection;
	}
	public void setSelectedPromoteToSection(String selectedPromoteToSection) {
		this.selectedPromoteToSection = selectedPromoteToSection;
	}

	public Date getPostDate() {
		return postDate;
	}
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
}
