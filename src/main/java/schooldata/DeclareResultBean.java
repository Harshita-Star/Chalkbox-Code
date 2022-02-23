package schooldata;

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

import exam_module.ExamInfo;

@ManagedBean(name="declareResult")
@ViewScoped

public class DeclareResultBean implements Serializable
{
	ArrayList<SelectItem> termList,classList;
	ArrayList<ExamInfo> examList,selectedExamList;
	String selectedClass,selectedTerm,session,schid;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	boolean showTable;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public DeclareResultBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		showTable=false;
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		classList=obj.allClass(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schid);

		selectedTerm = "";

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		////// // System.out.println("type : "+type);
		examList = obj.examListForResultDeclaration(selectedClass, selectedTerm, schid, conn,"declare");
		if(examList.size()<=0)
		{
			showTable = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Exam Found."));
		}
		else
		{
			showTable = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void search1()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		examList = obj.examListForResultDeclaration(selectedClass, selectedTerm, schid, conn,"undeclare");
		if(examList.size()<=0)
		{
			showTable = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Exam Found."));
		}
		else
		{
			showTable = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void declare()
	{
		if(selectedExamList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one exam for result declaration."));
		}
		else
		{
			Connection conn=DataBaseConnection.javaConnection();
			boolean check = false;
			ArrayList<ExamInfo> oprList = new ArrayList<>();
			ArrayList<ExamInfo> perList = new ArrayList<>();
			for(ExamInfo ee : selectedExamList)
			{
				check = obj.checkExamPerformance(ee.getExamid(), conn);
				if(check)
				{
					oprList.add(ee);
				}
				else
				{
					perList.add(ee);
				}
			}
			
			if(oprList.size()>0)
			{
				int i = obj.declareExamResult(oprList,conn,"declare");
				if(i>=1)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn,oprList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					examList = obj.examListForResultDeclaration(selectedClass, selectedTerm, schid, conn,"declare");
					for(ExamInfo ee : oprList)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Result declared successfully : "+ee.getExamName()));
					}
					
					HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					String notify=(String) ss.getAttribute("resultNotify");
					if(notify.equalsIgnoreCase("true"))
					{
						ArrayList<ExamInfo> newlist = oprList;
						
						
								String exName = "";
								for(ExamInfo ee : newlist)
								{
									if(ee.getExamName().contains("/"))
									{
										int first=ee.getExamName().lastIndexOf("/");
										exName=ee.getExamName().substring(0, first);
									}
									else
									{
										exName = ee.getExamName();
									}
									obj.notification(schid,"Result Declared","Result of Exam - "+exName+", Term - "+ee.getSemesterName()+" has been declared. Please check Student Performance in app.", selectedClass+"-"+schid,conn);
								}
								
								try {
									conn.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
						
						
					}
					
					selectedExamList = new ArrayList<>();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.. Please Try Again"));
				}
			}
			
			if(perList.size()>0)
			{
				for(ExamInfo ee : perList)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Performance is not added yet : "+ee.getExamName()));
				}
			}
		}
	}
	
	public String addWorkLog(Connection conn,ArrayList<ExamInfo> oprrList)
	{
	    String value = "";
		String language= "Class - "+selectedClass +" --- Term - "+selectedTerm;
		
		for(ExamInfo ec: oprrList)
		{
			value += ec.getExamid()+" -- ";
		}
		if(oprrList.size()>0)
		{
			value = value.substring(0, value.length()-4); 
		}
		
	
		String refNo = workLg.saveWorkLogMehod(language,"Declare Result","WEB",value,conn);
		return refNo;
	}
	


	public void undeclare()
	{
		if(selectedExamList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one exam for result undeclaration."));
		}
		else
		{
			Connection conn=DataBaseConnection.javaConnection();

			int i = obj.declareExamResult(selectedExamList,conn,"undeclare");
			if(i>=1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn,selectedExamList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				examList = obj.examListForResultDeclaration(selectedClass, selectedTerm, schid, conn,"undeclare");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Result of selected exam(s) is undeclared Successfully."));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.. Please Try Again"));
			}
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String addWorkLog2(Connection conn,ArrayList<ExamInfo> oprrList)
	{
	    String value = "";
		String language= "Class - "+selectedClass +" --- Term - "+selectedTerm;
		
		for(ExamInfo ec: oprrList)
		{
			value += ec.getExamid()+" -- ";
		}
		if(oprrList.size()>0)
		{
			value = value.substring(0, value.length()-4); 
		}
		
	
		String refNo = workLg.saveWorkLogMehod(language,"Undeclare Result","WEB",value,conn);
		return refNo;
	}


	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<ExamInfo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<ExamInfo> examList) {
		this.examList = examList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public ArrayList<ExamInfo> getSelectedExamList() {
		return selectedExamList;
	}

	public void setSelectedExamList(ArrayList<ExamInfo> selectedExamList) {
		this.selectedExamList = selectedExamList;
	}


}
