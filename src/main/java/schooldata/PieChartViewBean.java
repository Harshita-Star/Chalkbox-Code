package schooldata;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.PieChartModel;
@ManagedBean(name="pieChartView")
@ViewScoped
public class PieChartViewBean implements Serializable{


	public PieChartModel pieModel1;
	public PieChartModel pieModel2;
	ArrayList<Sum> sum;


	@PostConstruct
	public void init() {
		createPieModels();
	}

	public PieChartModel getPieModel1() {
		return pieModel1;
	}

	public PieChartModel getPieModel2() {
		return pieModel2;
	}

	private void createPieModels() {
		createPieModel1();
		createPieModel2();
	}

	private void createPieModel1() {

		Connection conn=DataBaseConnection.javaConnection();
		sum=new DatabaseMethods1().alltAttendance(conn);
		pieModel1 = new PieChartModel();
		pieModel1.set("Present - "+sum.get(0).getPresent(), sum.get(0).getPresent());
		pieModel1.set("Absent - "+sum.get(0).getAbsent(), sum.get(0).getAbsent());
		pieModel1.set("Holiday - "+sum.get(0).getHoliday(), sum.get(0).getHoliday());
		pieModel1.set("Leave - "+sum.get(0).getLeave(), sum.get(0).getLeave());
		pieModel1.set("Medical Leave - "+sum.get(0).getMedical(), sum.get(0).getMedical());
		pieModel1.set("Preparation Leave - "+sum.get(0).getPrepLeave(), sum.get(0).getPrepLeave());
		pieModel1.set("Not Marked - "+sum.get(0).getNotMarked(), sum.get(0).getNotMarked());
		pieModel1.setShowDataLabels(true);
		pieModel1.setLegendPosition("w");
		//pieModel1.setSeriesColors("00A65A,DD4B39,F39C12,00C0EF");
		pieModel1.setSeriesColors("93F2A3,F79BA3,FF512DA8,FFBF79,FFFF5722,FF0097A7,8DC8FF");
		pieModel1.setExtender("skinPie");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void createPieModel2() {
		pieModel2 = new PieChartModel();

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 db=new DatabaseMethods1();
		String schid=db.schoolId();
		String session=db.selectedSessionDetails(schid, conn);
		int i=  Integer.valueOf(db.allStudentcount(schid,"","",session,"",conn));
		int c=  Integer.valueOf(db.allStudentcount(schid,"appDownload","",session,"",conn));

		pieModel2.set("App Download", c);
		pieModel2.set("Pending", i-c);

		pieModel2.setTitle("Total Download :"+String.valueOf(c));
		pieModel2.setLegendPosition("w");
		pieModel2.setFill(true);
		pieModel2.setShowDataLabels(true);
		//pieModel2.setDiameter(150);
		pieModel2.setSeriesColors("93f2a3,f79ba3");
		pieModel2.setExtender("skinPie");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void setPieModel1(PieChartModel pieModel1) {
		this.pieModel1 = pieModel1;
	}

	public void setPieModel2(PieChartModel pieModel2) {
		this.pieModel2 = pieModel2;
	}



}
