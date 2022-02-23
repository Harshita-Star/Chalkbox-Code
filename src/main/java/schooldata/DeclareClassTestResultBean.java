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

@ManagedBean(name="declareClassTestResult")
@ViewScoped
public class DeclareClassTestResultBean implements Serializable{
	
	ArrayList<SelectItem> classSection = new ArrayList<>();
	ArrayList<SelectItem> sectionList = new ArrayList<>();
	ArrayList<ClassTest> testList,selectedTestList = new ArrayList<>();
	String selectedCLassSection,schid,session;
	String selectedSection;
	Boolean showTable=false;
	DatabaseMethods1 obj = new DatabaseMethods1();

	
	public DeclareClassTestResultBean()
	{
		Connection conn = DataBaseConnection.javaConnection();

		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		
		classSection = obj.allClass(conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = obj.allSection(selectedCLassSection, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		testList = obj.viewTestListClassSectionWise(session,schid,selectedCLassSection,selectedSection,conn);
	    if(testList.size()==0)
	    {
	        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No class test found for declaring result"));
	        	showTable = false;
	    }
	    else
	    {
	    	showTable = true;
	    }

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void declare()
	{
        Connection conn = DataBaseConnection.javaConnection();
		
        if(selectedTestList.size()==0)
        {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast 1 Class Test"));
        }
        else
        {
          int i=0;	
          for(ClassTest df : selectedTestList)
          {	 
            i = obj.declareClassTestResult(conn,df.getId(),schid);
          }
         
         if(i>=1)
         {	 
		   testList = obj.viewTestListClassSectionWise(session,schid,selectedCLassSection,selectedSection,conn);
		   selectedTestList = new ArrayList<>();  
		   if(testList.size()==0)
		    {
		        	showTable = false;
		    }
		    else
		    {
		    	showTable = true;
		    }
      	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success,Selected test result has been declared "));

         }
         else
         {
         	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some error occured")); 
         }
        }
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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


	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}


	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}


	public String getSelectedSection() {
		return selectedSection;
	}


	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}


	public ArrayList<ClassTest> getTestList() {
		return testList;
	}


	public void setTestList(ArrayList<ClassTest> testList) {
		this.testList = testList;
	}


	public ArrayList<ClassTest> getSelectedTestList() {
		return selectedTestList;
	}


	public void setSelectedTestList(ArrayList<ClassTest> selectedTestList) {
		this.selectedTestList = selectedTestList;
	}

	public Boolean getShowTable() {
		return showTable;
	}

	public void setShowTable(Boolean showTable) {
		this.showTable = showTable;
	}
	

	
}
