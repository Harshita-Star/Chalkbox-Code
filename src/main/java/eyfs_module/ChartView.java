package eyfs_module;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

@ManagedBean(name="cView")
@ViewScoped
public class ChartView implements Serializable
{
	public CartesianChartModel combinedModel;

	public ChartView()
	{
		createCombinedModel();
	}

	public CartesianChartModel getCombinedModel() {
		return combinedModel;
	}

	public void createCombinedModel() {
		combinedModel = new BarChartModel();

		BarChartSeries boys = new BarChartSeries();
		boys.setLabel("Boys");

		boys.set("2004", 120);
		boys.set("2005", 100);
		boys.set("2006", 44);
		boys.set("2007", 150);
		boys.set("2008", 25);

		LineChartSeries girls = new LineChartSeries();
		girls.setLabel("Girls");

		girls.set("2004", 52);
		girls.set("2005", 60);
		girls.set("2006", 110);
		girls.set("2007", 135);
		girls.set("2008", 120);

		combinedModel.addSeries(boys);
		combinedModel.addSeries(girls);

		combinedModel.setTitle("Bar and Line");
		combinedModel.setLegendPosition("ne");
		combinedModel.setMouseoverHighlight(false);
		combinedModel.setShowDatatip(false);
		combinedModel.setShowPointLabels(true);
		Axis yAxis = combinedModel.getAxis(AxisType.Y);
		yAxis.setMin(0);
		yAxis.setMax(200);
	}
}
