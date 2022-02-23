package exam_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import session_work.RegexPattern;

@ManagedBean (name="editBulkExamSetting")
@ViewScoped
public class EditBulkExamSettingBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<ExamSettingInfo> examList;
	ExamSettingInfo selectedExam;
	ArrayList<SelectItem> classList;
	String  classId,schid,sessionValue,username,userType;
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<ExamSettingInfo> selectedExamList = new ArrayList<ExamSettingInfo>();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public EditBulkExamSettingBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schid, conn);
		examList=objExam.allExamSettingListForBulk(conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.allClassListForClassTeacher(empid,schid,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	public String editBulk()
	{
		Connection con = DataBaseConnection.javaConnection();
		int k=0;
		int selectionCheck =0;int requiredCheck=0;
		
		for(ExamSettingInfo emp: selectedExamList)
		{
		
				if(emp.getRound_off().equalsIgnoreCase(""))
				{
					requiredCheck =1;
				}
				else
				{
					if(emp.getRound_off().equalsIgnoreCase("yes") && emp.getRound_off_type().equalsIgnoreCase(""))
					{
						requiredCheck =1;
					}
					
				}
				
				if(emp.getExamName().trim().equalsIgnoreCase("")||emp.getNo_of_PT().trim().equalsIgnoreCase("")||emp.getReflectMark().trim().equalsIgnoreCase("")||
						emp.getInclude_PT().equalsIgnoreCase("")||emp.getHeader1().trim().equalsIgnoreCase("")||emp.getSchool_headerString().equalsIgnoreCase("")||
						emp.getStd_imageString().equalsIgnoreCase("")||emp.getMaxMarkML().trim().equalsIgnoreCase("")||emp.getMaxMarkAB().trim().equalsIgnoreCase("")||
						emp.getFinalMarks().equalsIgnoreCase("")||emp.getRank_base().equalsIgnoreCase("")||emp.getShowGradeScaleString().equalsIgnoreCase("")||emp.getShowGradeScaleCoscholString().equalsIgnoreCase("")||emp.getExamMarks().equalsIgnoreCase("")||
						emp.getRound_off_percent().equalsIgnoreCase("")||
						emp.getMarksformat().equalsIgnoreCase("")||emp.getCoschol_subString().equalsIgnoreCase("")||emp.getTermNameCoschol().equalsIgnoreCase("")||emp.getCoscholTerm().equalsIgnoreCase("")||
						emp.getAdditional_subString().equalsIgnoreCase("")||emp.getOther_subString().equalsIgnoreCase("")||emp.getOtherTerm().equalsIgnoreCase("")||
						emp.getTermNameOther().equalsIgnoreCase("")||emp.getDisci_subString().equalsIgnoreCase("")||emp.getTermNameDisci().equalsIgnoreCase("")||
						emp.getSepearte_disci().equalsIgnoreCase("")||emp.getDisci_term().equalsIgnoreCase("")||emp.getMarks_grade().equalsIgnoreCase(""))
				{
					
					  requiredCheck =1;
					
				}
				
				selectionCheck =1;
			
		}
		
		
		
		if(selectionCheck ==1)
		{
			if(requiredCheck==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill All Mandatory Fields In Selected Rows"));

			}
			else
			{	
			
			
			for(ExamSettingInfo emp: selectedExamList)
			{
			      k= objExam.editBulkExam(emp,con);
			      if(k>0)
			      {
			    	  String refNo;
						try {
							refNo=addWorkLog(con,emp);
						} catch (Exception e) {
							e.printStackTrace();
						}
			      }
				
			}	
			
			if(k>0)
			{
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selected Exam Setting Edited Successfully"));
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "examSetting.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));

			}
		}	
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast 1 Class "));
		}
		
		
		
	
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
		
	}
	
	public String addWorkLog(Connection conn,ExamSettingInfo emp)
		{
		String value = emp.toString();
		String language= "";

		String refNo = workLg.saveWorkLogMehod(language,"Edit Bulk Exam Setting","WEB",value,conn);
		return refNo;
		}

	
	
	public void goToExamSetting()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("examSetting.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	public void createPTList()
	{
		UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
	    int dummy = (int) component.getAttributes().get("Dummy");
	    
		for(ExamSettingInfo tt: examList )
		{
			 if(tt.getSno()==dummy)
			 { 
				 ArrayList<SelectItem> ptlist = new ArrayList<SelectItem>();
					for(int i=1;i<=Integer.parseInt(tt.getNo_of_PT());i++)
					{
						SelectItem ll=new SelectItem();
						ll.setLabel(String.valueOf(i));
						ll.setValue(String.valueOf(i));
						ptlist.add(ll);
					}
				tt.setPtlist(ptlist);	
			 }
		}	 
		
		
	}
	
	
	public void checkRoundOff()
	{
		UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
	    int dummy = (int) component.getAttributes().get("Dummy");
	    
		for(ExamSettingInfo tt: examList )
		{
			 if(tt.getSno()==dummy)
			 { 
		        if(tt.getRound_off().equalsIgnoreCase("Yes"))
		    	{
		        	tt.setShowType(false);
		        	tt.setShowType1(false);tt.setShowType2(true);
				}
				else
				{
					tt.setShowType(true);
					tt.setShowType1(true);tt.setShowType2(false);
				}
		       tt.setRound_off_type(""); 
		       tt.setMarksformat("");
		    }
		 }		 
	}
	

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}


	public ArrayList<ExamSettingInfo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<ExamSettingInfo> examList) {
		this.examList = examList;
	}

	public ExamSettingInfo getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(ExamSettingInfo selectedExam) {
		this.selectedExam = selectedExam;
	}


	public ArrayList<ExamSettingInfo> getSelectedExamList() {
		return selectedExamList;
	}


	public void setSelectedExamList(ArrayList<ExamSettingInfo> selectedExamList) {
		this.selectedExamList = selectedExamList;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}

