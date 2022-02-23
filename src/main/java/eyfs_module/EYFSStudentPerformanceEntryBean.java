package eyfs_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="eyfsStdPerform")
@ViewScoped
public class EYFSStudentPerformanceEntryBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selectedClass,selectedTerm,selectedSection,mainParaId,subParaId,comment,fillType,copyOrNot,selectedAgeGroup;
	ArrayList<SelectItem> termList,classList,sectionList,mainParaList,subParaList,gradeList,ageGroupList;
	ArrayList<StudentInfo> studentDetails;

	boolean renderShowTable=false,renderShowTableAtt=false,showSubPara,showComment;
	String username,schoolid,type,session;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	DataBaseMeathodJson objJson = new DataBaseMeathodJson();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();

	public  EYFSStudentPerformanceEntryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		session=obj.selectedSessionDetails(schoolid,conn);

		if(type.equalsIgnoreCase("admin"))
		{
			classList=obj.allClass(conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
		}
		selectedSection=selectedTerm=mainParaId=subParaId=comment=fillType="";
		mainParaList=subParaList=new ArrayList<>();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		ageGroupList=objEYFS.ageGroupOfSelectedClass(selectedClass,schoolid, session, conn);
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schoolid);
		if(type.equalsIgnoreCase("admin"))
		{
			sectionList=obj.allSection(selectedClass,conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);
		}
		mainParaId=selectedTerm=subParaId=comment=fillType="";
		subParaList=new ArrayList<>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allMainParaList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		mainParaList=objEYFS.ageGroupMainParameterList(selectedAgeGroup,schoolid, session, conn);
		mainParaId=subParaId=comment=fillType="";
		subParaList=new ArrayList<>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSubParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subParaList=objEYFS.subParameterListByMainParameterAgeGroupTerm(selectedAgeGroup,selectedTerm,mainParaId, schoolid, session, conn);
		subParaId=comment=fillType="";
		gradeList=objEYFS.gradeParaListOfMainPara(selectedAgeGroup,mainParaId,session,schoolid,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkFillType()
	{
		if(fillType.equalsIgnoreCase("comment"))
		{
			showComment=true;showSubPara=false;subParaId=comment=copyOrNot="";
		}
		else if(fillType.equalsIgnoreCase("subParameter"))
		{
			showComment=false;showSubPara=true;subParaId=comment=copyOrNot="";
		}
		else if(fillType.equalsIgnoreCase("attachment"))
		{
			showComment=false;showSubPara=false;subParaId=comment=copyOrNot="";
		}
	}

	public String saveData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		boolean ls=objEYFS.addEYFSStudentPerformance(selectedClass,selectedSection,selectedTerm,mainParaId,fillType,subParaId,comment,studentDetails,conn,selectedAgeGroup);
		if(ls==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Performance record updated successfully"));
			studentDetails=new ArrayList<>();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occur Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void searchStudentDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();

		ArrayList<String> list=objStd.basicFieldsForStudentList();
		studentDetails=objStd.studentDetail(selectedAgeGroup, selectedSection,"", QueryConstants.AGE_GROUP,QueryConstants.ASSIGNED_AGE_GROUP, null,null,"","","","",session, schoolid, list, conn);

		objEYFS.allPerformanceForEYFS(selectedAgeGroup,selectedClass,selectedSection,mainParaId,subParaId, studentDetails,fillType,selectedTerm,conn);
		if(studentDetails.size()>0)
		{
			int x = 1;
			for(StudentInfo ss : studentDetails)
			{
				ss.setSno(x++);
			}
			if(fillType.equals("comment"))
			{
				if(copyOrNot.equals("yes"))
				{
					for(StudentInfo ss : studentDetails)
					{
						ss.setComment(comment);
					}
				}
			}
			
			if(fillType.equalsIgnoreCase("attachment"))
			{
				renderShowTableAtt=true;
				renderShowTable=false;
			}
			else
			{
				renderShowTableAtt=false;
				renderShowTable=true;
			}

			
		}
		else
		{
			renderShowTable=false;renderShowTableAtt=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
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


	class MyRollNoComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{
			if(e1.getRollNo()==null||e2.getRollNo()==null||e2.getRollNo().equals("")||e1.getRollNo().equals(""))
			{
				return 0;
			}
			else
			{
				if(e2.getRollNo().matches("-?\\d+(\\.\\d+)?")||e1.getRollNo().matches("-?\\d+(\\.\\d+)?"))
				{
					try
					{
						int sr1 = Integer.parseInt(e1.getRollNo());
						int sr2 = Integer.parseInt(e2.getRollNo());

						if(sr1 >= sr2)
						{
							return 1;
						}
						else
						{
							return -1;
						}
					}
					catch (Exception e)
					{
						return 0;
					}

				}
				else
				{
					return 0;
				}
			}
		}
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
	public boolean isRenderShowTable() {
		return renderShowTable;
	}
	public void setRenderShowTable(boolean renderShowTable) {
		this.renderShowTable = renderShowTable;
	}
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}
	public String getSelectedTerm() {
		return selectedTerm;
	}
	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
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

	public String getMainParaId() {
		return mainParaId;
	}

	public void setMainParaId(String mainParaId) {
		this.mainParaId = mainParaId;
	}

	public String getSubParaId() {
		return subParaId;
	}

	public void setSubParaId(String subParaId) {
		this.subParaId = subParaId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ArrayList<SelectItem> getMainParaList() {
		return mainParaList;
	}

	public void setMainParaList(ArrayList<SelectItem> mainParaList) {
		this.mainParaList = mainParaList;
	}

	public ArrayList<SelectItem> getSubParaList() {
		return subParaList;
	}

	public void setSubParaList(ArrayList<SelectItem> subParaList) {
		this.subParaList = subParaList;
	}

	public String getFillType() {
		return fillType;
	}

	public void setFillType(String fillType) {
		this.fillType = fillType;
	}

	public boolean isShowSubPara() {
		return showSubPara;
	}

	public void setShowSubPara(boolean showSubPara) {
		this.showSubPara = showSubPara;
	}

	public boolean isShowComment() {
		return showComment;
	}

	public void setShowComment(boolean showComment) {
		this.showComment = showComment;
	}

	public String getCopyOrNot() {
		return copyOrNot;
	}

	public ArrayList<SelectItem> getGradeList() {
		return gradeList;
	}

	public void setGradeList(ArrayList<SelectItem> gradeList) {
		this.gradeList = gradeList;
	}

	public void setCopyOrNot(String copyOrNot) {
		this.copyOrNot = copyOrNot;
	}

	public boolean isRenderShowTableAtt() {
		return renderShowTableAtt;
	}

	public void setRenderShowTableAtt(boolean renderShowTableAtt) {
		this.renderShowTableAtt = renderShowTableAtt;
	}

	public String getSelectedAgeGroup() {
		return selectedAgeGroup;
	}

	public void setSelectedAgeGroup(String selectedAgeGroup) {
		this.selectedAgeGroup = selectedAgeGroup;
	}

	public ArrayList<SelectItem> getAgeGroupList() {
		return ageGroupList;
	}

	public void setAgeGroupList(ArrayList<SelectItem> ageGroupList) {
		this.ageGroupList = ageGroupList;
	}
}
