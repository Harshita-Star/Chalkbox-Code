package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

@ManagedBean(name="feeEstimate")
@ViewScoped
public class FeeEstimateReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String studentType,selectedClass,selectedSection,selectedRoute,studentName;
	ArrayList<SelectItem> classList,sectionList,routeList,concessionCategoryList;
	boolean showNew,showOld;
	int concessionAmount;
	String addNumber,parentName,areaCode,concessionCategory;

	public FeeEstimateReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		concessionCategoryList=new DatabaseMethods1().allConnsessionType(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<StudentInfo> studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void checkStudentType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		if(studentType.equalsIgnoreCase("New"))
		{
			showNew=true;showOld=false;studentName="";
			classList=obj.allClass(conn);
			routeList = obj.allStops(conn);
		}
		else
		{
			showOld=true;showNew=false;studentName="";
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void submitDetails()
	{
		String srno="";
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if(studentType.equalsIgnoreCase("old"))
		{
			int index=studentName.lastIndexOf("-")+1;
			addNumber=studentName.substring(index);
			StudentInfo info=new DatabaseMethods1().selectedStudentDetailByAddNo(addNumber,conn);
			parentName= info.getMotherName()+" and " +info.getFathersName();
			studentName=info.getFname();
			areaCode=info.getRouteStop();
			selectedClass=info.getClassId();
			selectedSection=info.getSectionid();
			concessionCategory=info.getConcession();
			selectedRoute=info.getRouteId();
			srno=info.getSrNo();
		}

		rr.setAttribute("addNumber",addNumber);
		rr.setAttribute("parentName",parentName);
		rr.setAttribute("areaCode",areaCode);
		rr.setAttribute("concessionCategory",concessionCategory);
		rr.setAttribute("concessionAmount",concessionAmount);

		rr.setAttribute("selectedClass", selectedClass);
		rr.setAttribute("selectedSection", selectedSection);
		rr.setAttribute("selectedRoute", selectedRoute);
		rr.setAttribute("studentName", studentName);
		rr.setAttribute("studentType", studentType);
		rr.setAttribute("srno", srno);

		PrimeFaces.current().executeInitScript("window.open('printFeeEstimate.xhtml')");

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
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

	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}

	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}

	public boolean isShowNew() {
		return showNew;
	}

	public void setShowNew(boolean showNew) {
		this.showNew = showNew;
	}

	public boolean isShowOld() {
		return showOld;
	}

	public void setShowOld(boolean showOld) {
		this.showOld = showOld;
	}

	public String getSelectedRoute() {
		return selectedRoute;
	}
	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public ArrayList<SelectItem> getConcessionCategoryList() {
		return concessionCategoryList;
	}
	public void setConcessionCategoryList(ArrayList<SelectItem> concessionCategoryList) {
		this.concessionCategoryList = concessionCategoryList;
	}
	public String getConcessionCategory() {
		return concessionCategory;
	}
	public void setConcessionCategory(String concessionCategory) {
		this.concessionCategory = concessionCategory;
	}

	public int getConcessionAmount() {
		return concessionAmount;
	}

	public void setConcessionAmount(int concessionAmount) {
		this.concessionAmount = concessionAmount;
	}
}
