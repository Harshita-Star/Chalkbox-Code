package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;

@ManagedBean(name="editTransport")
@ViewScoped
public class EditTransportDetails implements Serializable
{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	Transport transport;
	ArrayList<SelectItem> routeList,classSection,sectionList;
	ArrayList<Transport> transportDetails;
	StudentInfo list;
	String selectedSection,name,selectedCLassSection,selectedRoute;
	boolean showTransport,show;
	ArrayList<StudentInfo> studentList;
	String transportStatus;
	
	boolean checkupdate=false;

	public  EditTransportDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			classSection=new DatabaseMethods1().allClass(conn);
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


	public void bulkalottment() {

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("studentBulkAllotment.xhtml");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public void searchStudentByName()
	{
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						studentList.add(info);
						list=info;
						/*show=true;*/
						rowSelectionListner();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}

	public void searchStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			studentList=new DatabaseMethods1().searchStudentList(name,conn);
			show=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void searchStudentByClassSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			studentList=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
			show=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void rowSelectionListner()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		String session=DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		String[] sess=session.split("-");

		SchoolInfoList info=obj.fullSchoolInfo(conn);
		int startSessionMonth=Integer.valueOf(info.getSchoolSession().split("-")[0]);
		int endSessionMonth=Integer.valueOf(info.getSchoolSession().split("-")[1])+12;

		try
		{
			if(list!=null)
			{
				transportDetails=obj.transportListDetails(obj.schoolId(),list.getAddNumber(),session,conn);
				routeList = obj.allStopsNew(conn);
				////// // System.out.println("size : "+transportDetails.size());
				if(transportDetails.size()==0)
				{
					checkupdate=true;
					transportDetails=new ArrayList<>();
					for(int i=4;i<=15;i++)
					{
						Transport tt=new Transport();
						tt.setStudentId(list.getAddNumber());
						tt.setNameAndSr(list.getFname()+" / "+list.getSrNo());
						tt.setClassid(list.getClassId());
						tt.setSectionid(list.getSectionid());
						tt.setRouteId(0);

						tt.setCheck(false);
						tt.setMonthInt(i);

						tt.setYear(Integer.parseInt(sess[0]));
						tt.setStatus("No");

						int month=i;
						if(i>12)
						{
							month-=12;
							tt.setYear(Integer.parseInt(sess[1]));
						}

						tt.setMonth(obj.monthNameByNumber(month));
						tt.setNewMonthInt(month);
						transportDetails.add(tt);
					}
					transportStatus="no";
					
				}
				else
				{
					transportStatus="no";
					for(Transport t : transportDetails)
					{
						if(t.getRouteId()!=0)
						{
							transportStatus="yes";
							break;
						}
					}
					
					for(Transport t : transportDetails)
					{
						t.setClassid(list.getClassId());
						t.setSectionid(list.getSectionid());
					}
				}
				showTransport=true;
			}
			else
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select atleast one student from list", "Validation error"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	public void allRowListner()
	{
		transportStatus="no";
		for(Transport t : transportDetails)
		{
			if(t.getRouteId()!=0)
			{
	          
				transportStatus="yes";
				break;
			}
		}
	}

	
	public void allTrasnportAllot()
	{
		if(transportStatus.equalsIgnoreCase("no"))
		{
			for(Transport t : transportDetails)
			{
				t.setRouteId(0);
			}
				
		}
	}
	
	
	public void allRouteSetup()
	{
		
		for(Transport t : transportDetails)
		{
			t.setRouteId(Integer.parseInt(selectedRoute));
		}
		
		if(selectedRoute.equalsIgnoreCase("0"))
		{
			transportStatus="no";
		}
		else
		{
			transportStatus="yes";
		}
	}

	public void updateDetails()
	{
		Connection conn =DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		ArrayList<Transport> transportDetailsNew = transportDetails;
		if(checkupdate==false)
		{
			boolean r=obj.updateTransportDetails(transportDetails,conn);
			if(r==true)
			{
				obj.updateTransportDetailsInReg(transportDetailsNew,conn);
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Student Details updated successfully"));
				checkupdate=false;

			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Some Error"));
			}
		}
		else
		{
			obj.addtransportDataEntry(transportDetails,conn);
			obj.updateTransportDetailsInReg(transportDetailsNew,conn);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Student Details updated successfully"));
			checkupdate=false;
		}

		showTransport=false;
		list=null;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}

	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}

	public StudentInfo getList() {
		return list;
	}

	public void setList(StudentInfo list) {
		this.list = list;
	}
	public Transport getTransport() {
		return transport;
	}
	public String getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}
	public ArrayList<Transport> getTransportDetails() {
		return transportDetails;
	}

	public void setTransportDetails(ArrayList<Transport> transportDetails) {
		this.transportDetails = transportDetails;
	}
	public boolean isShowTransport() {
		return showTransport;
	}

	public void setShowTransport(boolean showTransport) {
		this.showTransport = showTransport;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}


	public String getTransportStatus() {
		return transportStatus;
	}


	public void setTransportStatus(String transportStatus) {
		this.transportStatus = transportStatus;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
