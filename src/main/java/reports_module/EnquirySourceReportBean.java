package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo1;

@ManagedBean(name="enquirySourceReport")
@ViewScoped

public class EnquirySourceReportBean implements Serializable
{
	ArrayList<SelectItem> categoryList;
	SelectItem selected = new SelectItem();
	ArrayList<StudentInfo1> list = new ArrayList<>();
	String source = "";
	String schoolId,sessionValue;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	
	public EnquirySourceReportBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
		
		String session = dbm.selectedSessionDetails(schoolId,conn);
		String arr[] = session.split("-");
		String strDate = "01-04-"+arr[0];
		String enDate = "31-03-"+arr[1];
		
		categoryList=dbm.allRefernceCategory(conn);
		
		String count = "0";
		for(SelectItem ss : categoryList)
		{
			count = dbr.countEnquiryBySourceId(String.valueOf(ss.getValue()),schoolId,strDate,enDate,conn);
			
			ss.setDescription(count);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void viewDetail() 
	{
		Connection conn = DataBaseConnection.javaConnection();
	
		source = selected.getLabel();
		String session = dbm.selectedSessionDetails(schoolId,conn);
		String arr[] = session.split("-");
		String strDate = "01-04-"+arr[0];
		String enDate = "31-03-"+arr[1];
		list = dbr.enquiryListBySourceId(String.valueOf(selected.getValue()),schoolId,strDate,enDate,conn);
		if(list.size()>0)
		{
			PrimeFaces.current().executeInitScript("PF('viewDlg').show()");
			PrimeFaces.current().ajax().update("viewForm");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found!"));
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public SelectItem getSelected() {
		return selected;
	}

	public void setSelected(SelectItem selected) {
		this.selected = selected;
	}

	public ArrayList<StudentInfo1> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo1> list) {
		this.list = list;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
}
