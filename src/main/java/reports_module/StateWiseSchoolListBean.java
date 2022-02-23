package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;

@ManagedBean(name="stateSchoolList")
@ViewScoped
public class StateWiseSchoolListBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SchoolInfo> dataList;
	ArrayList<SelectItem> countryList;
	ArrayList<String> selectedCountryList;
	String selectedCountry;
	SchoolInfo selectedRow;
	DataBaseMethodsReports obj=new DataBaseMethodsReports();
	String schoolId,sessionValue;
	DatabaseMethods1 dbm = new DatabaseMethods1();

	public StateWiseSchoolListBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId,conn);
		countryList=obj.stateList(conn);
		//dataList=obj.countryWiseSchoolList(selectedCountry,"country",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedCountry="";

		for(String country:selectedCountryList)
		{
			selectedCountry+=country+"','";
		}

		if(selectedCountry.contains("','"))
			selectedCountry=selectedCountry.substring(0,selectedCountry.lastIndexOf("','"));

		dataList=obj.countryWiseSchoolList(selectedCountry,"state",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SchoolInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<SchoolInfo> dataList) {
		this.dataList = dataList;
	}
	public SchoolInfo getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(SchoolInfo selectedRow) {
		this.selectedRow = selectedRow;
	}
	public ArrayList<SelectItem> getCountryList() {
		return countryList;
	}
	public void setCountryList(ArrayList<SelectItem> countryList) {
		this.countryList = countryList;
	}
	public ArrayList<String> getSelectedCountryList() {
		return selectedCountryList;
	}
	public void setSelectedCountryList(ArrayList<String> selectedCountryList) {
		this.selectedCountryList = selectedCountryList;
	}
}
