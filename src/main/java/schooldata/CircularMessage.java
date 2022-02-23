package schooldata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
@ManagedBean(name="circularMessage")
@ViewScoped
public class CircularMessage implements Serializable
{

	private static final long serialVersionUID = 1L;
	String messageCategory,className1,typeMessage,selectedSection,language;
	ArrayList<SelectItem> sectionList,classListWithSecetion;
	ArrayList<StudentInfo> studentList,selectedStudentList;
	boolean showClassList,showclassName,show,hindiShow,englishShow;
	ArrayList<ClassInfo> classList,selectedClassList;

	public String getSelectedSection()
	{
		return selectedSection;
	}

	public void checkBalance()
	{
		String output  = getUrlContents("http://199.189.250.157/smsclient//balancecheckapi.php?username=sd-smart&&password=62922178");
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("You have "+output+" message left "));
	}
	private static String getUrlContents(String theUrl)
	{
		StringBuilder content = new StringBuilder();
		try
		{
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				content.append(line + "\n");
			}
			bufferedReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return content.toString();
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(className1,conn);
		selectedSection=null;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showGeneralCategoryList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		hindiShow=false;
		language="english";
		englishShow=true;
		if(messageCategory.equals("All"))
		{
			showclassName=false;
			show=false;
			showClassList=true;

			classList=obj.allSectionList(conn);
			className1=null;
			studentList=null;
			selectedSection=null;
		}
		else if(messageCategory.equals("ClassWise"))
		{
			showClassList=false;
			showclassName=true;
			classListWithSecetion=obj.allClass(conn);
			className1=null;
			selectedSection=null;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();
		hindiShow=false;
		language="english";
		englishShow=true;
		try
		{
			studentList=new DatabaseMethods1().searchStudentListByClassSection(className1,selectedSection,conn);
			show=true;
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

	public void sendMessageAllClass() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedClassList.size()>0)
		{
			studentList=new DatabaseMethods1().searchStudentListByClassSection(selectedClassList,conn);

			Date date=new Date();
			String messageTemp=typeMessage;
			for(StudentInfo info : studentList)
			{
				DataBaseMethodsSMSModule.circular(info.getAddNumber(), messageTemp, info.getFathersPhone(), date,info.getClassId(),"Student",conn);
			}

			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("You have sent message to "+studentList.size()+" stduents "));

			classList=selectedClassList=null;
			studentList=selectedStudentList=null;
			typeMessage=messageCategory=className1=null;
			show=showClassList=showclassName=false;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected, please select atleast one", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageClassWise() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		Date date=new Date();
		String messageTemp=typeMessage;
		typeMessage=URLEncoder.encode(typeMessage,"UTF-8");
		for(StudentInfo info : selectedStudentList)
		{
			DataBaseMethodsSMSModule.circular(info.getAddNumber(), messageTemp, info.getFathersPhone(), date,info.getClassId(),"Student",conn);
		}

		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));

		classList=selectedClassList=null;
		studentList=selectedStudentList=null;
		typeMessage=messageCategory=className1=null;
		show=showClassList=showclassName=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
	public String getClassName1() {
		return className1;
	}
	public void setClassName1(String className1) {
		this.className1 = className1;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isShowclassName() {
		return showclassName;
	}
	public void setShowclassName(boolean showclassName) {
		this.showclassName = showclassName;
	}
	public ArrayList<SelectItem> getClassListWithSecetion() {
		return classListWithSecetion;
	}
	public void setClassListWithSecetion(ArrayList<SelectItem> classListWithSecetion) {
		this.classListWithSecetion = classListWithSecetion;
	}
	public String getMessageCategory() {
		return messageCategory;
	}
	public void setMessageCategory(String messageCategory) {
		this.messageCategory = messageCategory;
	}
	public boolean isShowClassList() {
		return showClassList;
	}
	public void setShowClassList(boolean showClassList) {
		this.showClassList = showClassList;
	}
	public ArrayList<ClassInfo> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<ClassInfo> classList) {
		this.classList = classList;
	}
	public ArrayList<ClassInfo> getSelectedClassList() {
		return selectedClassList;
	}
	public void setSelectedClassList(ArrayList<ClassInfo> selectedClassList) {
		this.selectedClassList = selectedClassList;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public boolean isHindiShow() {
		return hindiShow;
	}
	public void setHindiShow(boolean hindiShow) {
		this.hindiShow = hindiShow;
	}
	public boolean isEnglishShow() {
		return englishShow;
	}
	public void setEnglishShow(boolean englishShow) {
		this.englishShow = englishShow;
	}

}






