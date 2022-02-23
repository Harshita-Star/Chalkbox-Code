package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringEscapeUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

import com.itextpdf.text.html.HtmlUtilities;
import com.sun.faces.util.HtmlUtils;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmpInfo;

@ManagedBean(name="allotExecutiveSchool")
@ViewScoped

public class AllotExecutiveSchoolBean implements Serializable
{
	String executive;
	ArrayList<EmpInfo> empList = new ArrayList<>();
	public DualListModel<String> finallist;
	List<String> schListSource = new ArrayList<>();
	List<String> schListTarget = new ArrayList<>();
	boolean show = false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schoolId,sessionValue; 
	
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	
	
	public AllotExecutiveSchoolBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj1.schoolId();
		sessionValue=obj1.selectedSessionDetails(schoolId, conn);
		empList = dbr.allServiceExecutiveList("active",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();


		schListSource=dbr.allSchoolListStateDistWise(conn);
		schListTarget=dbr.executiveSchStringList(executive, "assign", conn);

		if(schListTarget.size()>0)
		{
			for(String ss:schListTarget)
			{
				schListSource.remove(ss);
			}
		}
		finallist = new DualListModel<>(schListSource,schListTarget);
		show=true;
		PrimeFaces.current().scrollTo("form1:pickList");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void cancel()
	{
		show=false;
	}
	
	public void submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String chk = dbr.checkExecutiveSchool(executive, conn);
		if(chk.equalsIgnoreCase("yes"))
		{
			int i = dbr.executiveSchoolAction("delete", executive, "", "", conn);
			if(i>=1)
			{
				if (finallist.getTarget().size() > 0)
				{
					for (String mm : finallist.getTarget()) 
					{
						String exec = StringEscapeUtils.unescapeXml(mm);
						String arr[] = exec.split(" / ");
						dbr.executiveSchoolAction("add", executive, arr[1],"", conn);
					}
				}	
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Record Updated Successfully!"));
				show=false;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
			}
		}
		else
		{
			if (finallist.getTarget().size() > 0)
			{
				for (String mm : finallist.getTarget()) 
				{
					String exec = StringEscapeUtils.unescapeXml(mm);
					String arr[] = exec.split(" / ");
					dbr.executiveSchoolAction("add", executive, arr[1],"", conn);
				}
			}	
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Record Updated Successfully!"));
			show=false;
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getExecutive() {
		return executive;
	}

	public void setExecutive(String executive) {
		this.executive = executive;
	}

	public ArrayList<EmpInfo> getEmpList() {
		return empList;
	}

	public void setEmpList(ArrayList<EmpInfo> empList) {
		this.empList = empList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public DualListModel<String> getFinallist() {
		return finallist;
	}

	public void setFinallist(DualListModel<String> finallist) {
		this.finallist = finallist;
	}

	public List<String> getSchListSource() {
		return schListSource;
	}

	public void setSchListSource(List<String> schListSource) {
		this.schListSource = schListSource;
	}

	public List<String> getSchListTarget() {
		return schListTarget;
	}

	public void setSchListTarget(List<String> schListTarget) {
		this.schListTarget = schListTarget;
	}
}
