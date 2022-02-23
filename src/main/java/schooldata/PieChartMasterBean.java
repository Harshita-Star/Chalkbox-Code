package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.model.chart.PieChartModel;

@ManagedBean(name="pieChartSchoolMaster")
@ViewScoped

public class PieChartMasterBean implements Serializable
{
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
		// createPieModel2();
	}

	private void createPieModel1() {
		HttpSession ss=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ArrayList<SelectItem> branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		String branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
				}
			}
		}

		Connection conn=DataBaseConnection.javaConnection();
		sum=new DatabaseMethods1().alltAttendance(branches,conn);
		pieModel1 = new PieChartModel();
		pieModel1.set("Present",sum.get(0).getPresent());
		pieModel1.set("Absent", sum.get(0).getAbsent());
		pieModel1.set("Leave", sum.get(0).getLeave());
		pieModel1.set("Medical Leave", sum.get(0).getMedical());
		pieModel1.set("Preparation Leave", sum.get(0).getPrepLeave());
		pieModel1.set("Not Marked", sum.get(0).getNotMarked());
		pieModel1.setShowDataLabels(true);
		pieModel1.setLegendPosition("w");
		//pieModel1.setSeriesColors("00A65A,DD4B39,F39C12,00C0EF");
		//pieModel1.setSeriesColors("93F2A3,F79BA3,FFBF79,8DC8FF");
		pieModel1.setSeriesColors("93F2A3,F79BA3,FFBF79,FFFF5722,FF0097A7,8DC8FF");
		pieModel1.setExtender("skinPie");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*private void createPieModel2() {
        pieModel2 = new PieChartModel();

        pieModel2.set("Brand 1", 540);
        pieModel2.set("Brand 2", 325);
        pieModel2.set("Brand 3", 702);
        pieModel2.set("Brand 4", 421);

        pieModel2.setTitle("Custom Pie");
        pieModel2.setLegendPosition("e");
        pieModel2.setFill(false);
        pieModel2.setShowDataLabels(true);
        pieModel2.setDiameter(150);
    }*/

	public void setPieModel1(PieChartModel pieModel1) {
		this.pieModel1 = pieModel1;
	}

	public void setPieModel2(PieChartModel pieModel2) {
		this.pieModel2 = pieModel2;
	}


}
