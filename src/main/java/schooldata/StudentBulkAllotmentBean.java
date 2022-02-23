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


@ManagedBean (name="studentBulkAllotment")
@ViewScoped
public class StudentBulkAllotmentBean implements Serializable{

	String routeName,stopName;
	ArrayList<SelectItem> routeList,stopList;
	ArrayList<ClassInfo> classSection;
	ArrayList<StudentInfo> selectedstudents;
	ArrayList<Transport> transportDetails;
	boolean showtable;
	boolean checkupdate=false;
	SchoolInfoList info;
	double fee=0;


	public StudentBulkAllotmentBean() {

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		routeList=obj.routenameall(conn);
		classSection=obj.allClassList("studentalso", conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updatestop() {
		Connection conn=DataBaseConnection.javaConnection();
		stopList=new DatabaseMethods1().stopnameall(conn,routeName);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showstudent() {
		Connection conn=DataBaseConnection.javaConnection();
		fee=new DatabaseMethods1().transportFeeByGroupId(stopName, conn);
		showtable=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public String name() {
		Connection conn=DataBaseConnection.javaConnection();
		String session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		DatabaseMethods1 obj=new DatabaseMethods1();
		String[] sess=session.split("-");
		info=obj.fullSchoolInfo(conn);
		int startSessionMonth=Integer.valueOf(info.getSchoolSession().split("-")[0]);
		int endSessionMonth=Integer.valueOf(info.getSchoolSession().split("-")[1])+12;

		try
		{
			for(ClassInfo ls:classSection)
			{
				for(Category lst:ls.getCategoryList())
				{
					for(StudentInfo ls11:lst.getList())
					{
						if(ls11.isSendmessage())
						{
							if(ls11!=null)
							{
								transportDetails=obj.transportListDetails(obj.schoolId(),ls11.getAddNumber(),session,conn);

								if(transportDetails.size()==0)
								{
									checkupdate=true;
								}
								else
								{
									checkupdate=false;
								}
								transportDetails=new ArrayList<>();

								for(int i=startSessionMonth;i<=endSessionMonth;i++)
								{
									Transport tt=new Transport();
									tt.setStudentId(ls11.getAddNumber());
									tt.setClassid(ls11.getClassId());
									tt.setSectionid(ls11.getSectionid());
									tt.setRouteId(0);
									tt.setCheck(false);
									tt.setMonthInt(i);
									tt.setRouteId(Integer.parseInt(stopName));
									tt.setYear(Integer.parseInt(sess[0]));
									tt.setStatus("Yes");

									int month=i;
									if(i>12)
									{
										month-=12;
										tt.setYear(Integer.parseInt(sess[1]));
									}

									/*if(new DatabaseMethods1().schoolId().equals("215") && month==6)
											{
												tt.setRouteId(0);
												tt.setStatus("No");
											}*/

									tt.setMonth(obj.monthNameByNumber(month));
									tt.setNewMonthInt(month);

									transportDetails.add(tt);
								}

								if(checkupdate==false)
								{
									boolean r=obj.updateTransportDetails(transportDetails,conn);
									if(r==true)
									{
										obj.updateTransportDetailInRegistration(ls11.getAddNumber(), session, stopName, ls11.getDiscountFees(), conn);
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
									//new DatabaseMethods1().updateTransportDetailsInReg(transportDetails,conn);
									obj.updateTransportDetailInRegistration(ls11.getAddNumber(), session, stopName, ls11.getDiscountFees(), conn);

								}

							}
						}

					}
				}

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Student Details updated successfully"));
		return "editTransportDetails.xhtml";
	}



	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}
	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}
	public ArrayList<SelectItem> getStopList() {
		return stopList;
	}
	public void setStopList(ArrayList<SelectItem> stopList) {
		this.stopList = stopList;
	}


	public String getRouteName() {
		return routeName;
	}


	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}


	public String getStopName() {
		return stopName;
	}


	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public boolean isShowtable() {
		return showtable;
	}

	public void setShowtable(boolean showtable) {
		this.showtable = showtable;
	}

	public ArrayList<ClassInfo> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<ClassInfo> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<StudentInfo> getSelectedstudents() {
		return selectedstudents;
	}

	public void setSelectedstudents(ArrayList<StudentInfo> selectedstudents) {
		this.selectedstudents = selectedstudents;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}
}
