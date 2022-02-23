package reports_module;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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
import schooldata.EmployeeInfo;

@ManagedBean(name="categoryWiseEmployeeMessage")
@ViewScoped
public class CategoryWiseEmployeeMessageBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String typeMessage,language;
	boolean show,englishShow,hindiShow;
	ArrayList<SelectItem> categoryList;
	String selectedCategory;
	ArrayList<EmployeeInfo> employeeList,selectedEmployeeList;
	String schoolId,sessionValue;
	DatabaseMethods1 obj=new DatabaseMethods1();


	public CategoryWiseEmployeeMessageBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schoolId,conn);
		categoryList=obj.allEmployeeCategory(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchByCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		hindiShow=false;language="english";englishShow=true;
		try
		{
			employeeList=obj.searchEmployeebyCategorySchidd(schoolId, selectedCategory,conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		show=true;
	}

	public void checkLanguage()
	{

		if(language.equalsIgnoreCase("english"))
		{
			englishShow=true;hindiShow=false;
		}
		else
		{
			englishShow=false;hindiShow=true;
		}

	}

	public void sendMessageCategoryWiseEmployee() throws UnsupportedEncodingException
	{
		int counter=1;
		int secondCounter=0;
		String contactNumber = "";
		new Date();
		//typeMessage=URLEncoder.encode(typeMessage,"UTF-8");
		for(EmployeeInfo info : selectedEmployeeList)
		{
			secondCounter++;
			if(counter==99 || secondCounter==selectedEmployeeList.size())
			{
				//new DatabaseMethods1().messageurl1(String.valueOf(info.getMobile()), typeMessage);
				//contactNumber=contactNumber+"91"+info.getMobile()+",";
				//	DataBaseMethodsSMSModule.sentMessage(info.getId(),messageTemp,info.getMobile(),date,"","Employee");
				String url="http://199.189.250.157/smsclient/api.php?username=sd-smart&password=62922178&source=update&dmobile="+contactNumber+"+&message="+typeMessage;
				try
				{
					URL myURL = new URL(url);
					myURL.openStream();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				//counter=0;contactNumber="";
			}
			else
			{
				contactNumber=contactNumber+"91"+info.getMobile()+",";
				counter++;
				//		DataBaseMethodsSMSModule.sentMessage(info.getId(),messageTemp,info.getMobile(),date,"","Employee");
			}

		}

		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("You have sent message to "+selectedEmployeeList.size()+" Employees "));

		selectedEmployeeList=null;
		typeMessage=null;
		selectedCategory=null;
		show=false;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public String getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public boolean isEnglishShow() {
		return englishShow;
	}
	public void setEnglishShow(boolean englishShow) {
		this.englishShow = englishShow;
	}
	public boolean isHindiShow() {
		return hindiShow;
	}
	public void setHindiShow(boolean hindiShow) {
		this.hindiShow = hindiShow;
	}
	public ArrayList<EmployeeInfo> getSelectedEmployeeList() {
		return selectedEmployeeList;
	}
	public void setSelectedEmployeeList(ArrayList<EmployeeInfo> selectedEmployeeList) {
		this.selectedEmployeeList = selectedEmployeeList;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
}
