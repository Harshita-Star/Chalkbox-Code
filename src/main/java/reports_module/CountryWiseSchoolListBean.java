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

@ManagedBean(name="countrySchoolList")
@ViewScoped
public class CountryWiseSchoolListBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SchoolInfo> dataList;
	ArrayList<SelectItem> countryList;
	ArrayList<String> selectedCountryList;
	String selectedCountry;
	SchoolInfo selectedRow;
	DatabaseMethods1 dbm = new DatabaseMethods1(); 
	String schoolId,sessionValue;
	DataBaseMethodsReports obj=new DataBaseMethodsReports();

	public CountryWiseSchoolListBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		countryList=obj.countryList(conn);
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
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


		dataList=obj.countryWiseSchoolList(selectedCountry,"country",conn);
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
