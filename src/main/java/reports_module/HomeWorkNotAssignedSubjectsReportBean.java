package reports_module;

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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.HomeWorkInfo;

@ManagedBean(name="homeWorkNotGiven")
@ViewScoped
public class HomeWorkNotAssignedSubjectsReportBean implements Serializable{

	Date dateSel;
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>();
    String selectedSection,selectedClass;
    DatabaseMethods1 obj=new DatabaseMethods1();
    ArrayList<SelectItem>  subjectList;
    ArrayList<HomeWorkInfo> homeWorkSubjectList = new ArrayList<HomeWorkInfo>();
    ArrayList<HomeWorkInfo> showList = new ArrayList<HomeWorkInfo>();
	DataBaseMethodsReports objReport = new DataBaseMethodsReports();
	String schoolId,session;
			
	public HomeWorkNotAssignedSubjectsReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId,conn);
		classList=obj.allClass(conn);
		dateSel = new Date();
	
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedClass,conn);
		subjectList=obj.allSubjectClassWise(selectedClass,conn);
//	// // System.out.println(subjectList.size());
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection con = DataBaseConnection.javaConnection();
		ArrayList<String> homeWorkSubjectIds = new ArrayList<String>();
		
		showList = new ArrayList<HomeWorkInfo>();
		
		homeWorkSubjectList=obj.allAssignmentOfDate(selectedClass,con,dateSel,selectedSection);
		
		int counter=0;
		for(HomeWorkInfo hm: homeWorkSubjectList)
		{
			//// // System.out.println(hm.getSubjectName());
			if(hm.getSubjectName().equalsIgnoreCase("All"))
			{
				counter =1;
			}
			else {
				homeWorkSubjectIds.add(hm.getSubjectId()); 
				
			}
			
		}

		int snoo =1;
		
		if(counter ==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("HomeWork Assigned In All Subjects"));
			showList = new ArrayList<HomeWorkInfo>();
		}
		else
		{
			for(SelectItem ff: subjectList)
			{
				if(homeWorkSubjectIds.contains(ff.getValue().toString()))
				{
					
				}
				else
				{
					HomeWorkInfo objj = new HomeWorkInfo();
					
					objj.setsNo(String.valueOf(snoo));
					objj.setSubjectName(ff.getLabel());
					
					snoo++;
					showList.add(objj);
					
				}
			}
			
			
		    	
		}
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	public Date getDateSel() {
		return dateSel;
	}


	public void setDateSel(Date dateSel) {
		this.dateSel = dateSel;
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


	public String getSelectedSection() {
		return selectedSection;
	}


	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}


	public String getSelectedClass() {
		return selectedClass;
	}


	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}


	public ArrayList<HomeWorkInfo> getShowList() {
		return showList;
	}


	public void setShowList(ArrayList<HomeWorkInfo> showList) {
		this.showList = showList;
	}



	
	
	
	
}

