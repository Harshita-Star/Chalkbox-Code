package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

@ManagedBean(name="dashBoardBean")
public class DashBoardBB implements Serializable{

	private static final long serialVersionUID = 1L;
	DashboardModel dashModel;

	public DashBoardBB()
	{
		dashModel=new DefaultDashboardModel();
		DashboardColumn column1=new DefaultDashboardColumn();
		DashboardColumn column2=new DefaultDashboardColumn();
		DashboardColumn column3=new DefaultDashboardColumn();
		DashboardColumn column4=new DefaultDashboardColumn();
		DashboardColumn column5=new DefaultDashboardColumn();
		DashboardColumn column6=new DefaultDashboardColumn();
		DashboardColumn column7=new DefaultDashboardColumn();

		column1.addWidget("manageStudentId");
		column2.addWidget("manageEmployeeId");
		column3.addWidget("manageClassId");
		column4.addWidget("manageFinanceId");
		column5.addWidget("manageReportId");
		column6.addWidget("manageTransportId");

		column1.addWidget("manageAttendanceId");
		column2.addWidget("managePerformanceId");
		column3.addWidget("sendMessageId");
		column4.addWidget("leavingcerticate");
		column5.addWidget("promotionId");
		column6.addWidget("marksheetid");

		column1.addWidget("previoudSessionid");
		column2.addWidget("changepasswordid");
		column3.addWidget("backupid");

		dashModel.addColumn(column1);
		dashModel.addColumn(column2);
		dashModel.addColumn(column3);
		dashModel.addColumn(column4);
		dashModel.addColumn(column5);
		dashModel.addColumn(column6);
		dashModel.addColumn(column7);

	}

	public DashboardModel getDashModel() {
		return dashModel;
	}

	public void setDashModel(DashboardModel dashModel) {
		this.dashModel = dashModel;
	}
}
