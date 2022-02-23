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

@ManagedBean(name = "transportDueFeeReportBean")
@ViewScoped
public class TransportDueFeeReportBean implements Serializable {

	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> routeNameList;
	ArrayList<RouteDetail> routeDetailList;
	String selectedRoute;
	boolean show;

	ArrayList<StudentInfo> studentList;

	public void getStudentRoute()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		routeDetailList = DBM.getAllStudentRoute(selectedRoute, conn);
		studentList = DBM.transportDueReport(routeDetailList, conn);
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Record found", "Validation Error"));
			show = false;
		} else {
			show = true;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public TransportDueFeeReportBean() {
		Connection conn = DataBaseConnection.javaConnection();
		routeNameList = new DatabaseMethods1().allTransportRoutes(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<RouteDetail> getRouteDetailList() {
		return routeDetailList;
	}

	public void setRouteDetailList(ArrayList<RouteDetail> routeDetailList) {
		this.routeDetailList = routeDetailList;
	}

	public ArrayList<SelectItem> getRouteNameList() {
		return routeNameList;
	}

	public void setRouteNameList(ArrayList<SelectItem> routeNameList) {
		this.routeNameList = routeNameList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
}
