package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="routeWiseFeesGraph")
@ViewScoped
public class RouteWiseFeeGraphBean implements Serializable{

	BarChartModel barmodel;
	private static final long serialVersionUID = 1L;
	ArrayList<FeeReportInfo> feesList = new ArrayList<>();
	ArrayList<SelectItem> studentList= new ArrayList<>();
	ArrayList<SelectItem>routeList = new ArrayList<>();
	ArrayList<SelectItem>monthList = new ArrayList<>();

	DataBaseMethodsReports obj  = new DataBaseMethodsReports();
	DatabaseMethods1 dbm=new DatabaseMethods1();
	String schoolId,sessionValue;
	String selectRoute,routefees;
	double fees;
	boolean showbar;
	ArrayList<String> onlyRoutes= new ArrayList<>();

	
	public RouteWiseFeeGraphBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
		//routeList = new DatabaseMethods1().findAllRoutes(conn);
		routeList = dbm.allStops(conn);
		
		for(SelectItem ss:routeList) {
			
			String[] spliter = ss.getLabel().split(":-");
			
			if(!onlyRoutes.contains(spliter[0])){
			        onlyRoutes.add(spliter[0]);	
			}
			
		}
		
//		for(SelectItem ss:routeList) {
//			SelectItem rs = new SelectItem();
//			rs.setValue(ss.getValue());
//			rs.setLabel(ss.getLabel().s);
//			
//			onlyRoutes
//			
//		}
		
		showbar=false;
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//show();
	}
	private BarChartModel initBarModel() {
		//("initbarmodel");
		BarChartModel model = new BarChartModel();
		ChartSeries Fees = new ChartSeries();
		Fees.setLabel("Fees");
		for(FeeReportInfo ss:feesList)
		{
			Fees.set(ss.getDateName(), Double.valueOf(ss.getFees()));
		}
		model.addSeries(Fees);

		return model;
	}


	public void show() {
		showbar=false;
		Connection conn = DataBaseConnection.javaConnection();
		monthList = new ArrayList<>();

		SelectItem mm = new SelectItem();


		mm = new SelectItem();
		mm.setLabel("April");
		mm.setValue(4);
		monthList.add(mm);
		
		
		mm = new SelectItem();
		mm.setLabel("May");
		mm.setValue(5);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("June");
		mm.setValue(6);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("July");
		mm.setValue(7);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("August");
		mm.setValue(8);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Sept");
		mm.setValue(9);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Oct");
		mm.setValue(10);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Nov");
		mm.setValue(11);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Dec");
		mm.setValue(12);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Jan");
		mm.setValue(1);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Feb");
		mm.setValue(2);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("March");
		mm.setValue(3);
		monthList.add(mm);

        ArrayList<String> stoplist = new ArrayList<>(); 
        
        
        for(SelectItem ro: routeList) {
        	
        	String[] splitter = ro.getLabel().split(":-");
        	 if(splitter[0].equalsIgnoreCase(selectRoute)) {
        		 stoplist.add(ro.getValue().toString());
        	 }
        	
        }
        
       
        
      //  stoplist = new DatabaseMethods1().allRouteStop(new DatabaseMethods1().schoolId(), selectRoute,conn);
        
       
        
		feesList = new ArrayList<>();
		double arr[] = new double[12];
		arr[0] = 0.0;
		arr[1] = 0.0;
		arr[2] = 0.0;
		arr[3] = 0.0;
		arr[4] = 0.0;
		arr[5] = 0.0;
		arr[6] = 0.0;
		arr[7] = 0.0;
		arr[8] = 0.0;
		arr[9] = 0.0;
		arr[10] = 0.0;
		arr[11] = 0.0;
		
		
		
		
	
		for(int l=0;l<stoplist.size();l++) {
			
		
		routefees=dbm.routeStopListAllAmount(schoolId,stoplist.get(l).toString(),conn);
		fees=Double.valueOf(routefees);
	
		//(fees);
		studentList = obj.selectAllStudentId(conn,stoplist.get(l).toString());
		String session = dbm.selectedSessionDetails(schoolId,conn);
		String sess[]=session.split("-");
		String start=sess[0];
		String end = sess[1];
		String year="";
		double temp=0;
		for(SelectItem nn:monthList)
		{
			temp=0;
			if(Integer.valueOf(String.valueOf(nn.getValue()))>=4)
			{
				year=start;
			}
			else
			{
				year=end;
			}
			FeeReportInfo ff=new FeeReportInfo();
		//	 //// // System.out.println(nn.getValue());
			for(SelectItem st:studentList)
			{
                
				String status = obj.checkStatusforSchoolId(conn,nn,st.getValue(),stoplist.get(l).toString(),year);
				if(status.equalsIgnoreCase("yes"))
				{
					Double discnt=0.0;
					String	discount =dbm.checkDiscountFrom(conn,st.getValue());
					
					if(discount==null)
					{
						discnt=0.0;
					}
					else if(discount.equals(""))
					{
						discnt=0.0;
					}
					discnt=Double.valueOf(discount);

					temp=temp+(fees-discnt);
			
				}
			}
			   if(nn.getLabel().equalsIgnoreCase("April")) {
			    	arr[0] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("May")) {
			    	arr[1] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("June")) {
			    	arr[2] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("July")) {
			    	arr[3] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("August")) {
			    	arr[4] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("Sept")) {
			    	arr[5] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("Oct")) {
			    	arr[6] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("Nov")) {
			    	arr[7] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("Dec")) {
			    	arr[8] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("Jan")) {
			    	arr[9] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("Feb")) {
			    	arr[10] += temp; 
			    }
			    if(nn.getLabel().equalsIgnoreCase("March")) {
			    	arr[11] += temp; 
			    }
			 
			   // //// // System.out.println(arr[0]);
			   // //// // System.out.println(arr[1]);
				ff.setDateName(nn.getLabel()+"-"+year);
				feesList.add(ff);
			
		//	arr[0] += temp;
		
		
			
		 
		    
				
		}
		
	
			
			
			
		}
		
		for (int i = 0; i < feesList.size(); i++) {
			
			String[] spliter = feesList.get(i).getDateName().split("-");
			
			  if(spliter[0].equalsIgnoreCase("April")) {
			    	feesList.get(i).setFees(String.valueOf(arr[0])); 
			    } 
		    if(spliter[0].equalsIgnoreCase("May")) {
		    	feesList.get(i).setFees(String.valueOf(arr[1])); 
		    }
		    if(spliter[0].equalsIgnoreCase("June")) {
		    	feesList.get(i).setFees(String.valueOf(arr[2])); 
		    }
		    if(spliter[0].equalsIgnoreCase("July")) {
		    	feesList.get(i).setFees(String.valueOf(arr[3]));  
		    }
		    if(spliter[0].equalsIgnoreCase("August")) {
		    	feesList.get(i).setFees(String.valueOf(arr[4])); 
		    }
		    if(spliter[0].equalsIgnoreCase("Sept")) {
		    	feesList.get(i).setFees(String.valueOf(arr[5]));  
		    }
		    if(spliter[0].equalsIgnoreCase("Oct")) {
		    	feesList.get(i).setFees(String.valueOf(arr[6])); 
		    }
		    if(spliter[0].equalsIgnoreCase("Nov")) {
		    	feesList.get(i).setFees(String.valueOf(arr[7]));  
		    }
		    if(spliter[0].equalsIgnoreCase("Dec")) {
		    	feesList.get(i).setFees(String.valueOf(arr[8])); 
		    }
		    if(spliter[0].equalsIgnoreCase("Jan")) {
		    	feesList.get(i).setFees(String.valueOf(arr[9]));  
		    }
		    if(spliter[0].equalsIgnoreCase("Feb")) {
		    	feesList.get(i).setFees(String.valueOf(arr[10]));  
		    }
		    if(spliter[0].equalsIgnoreCase("March")) {
		    	feesList.get(i).setFees(String.valueOf(arr[11]));  
		    }
		     


		}

		showbar=true;
		createBarModel();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void createBarModel() {
		barmodel = initBarModel();
		barmodel.setTitle("Route Fees Bar Reports");
		barmodel.setLegendPosition("ne");
		barmodel.setExtender("barext");
		barmodel.setAnimate(true);
		Axis xAxis = barmodel.getAxis(AxisType.X);
		xAxis.setLabel("Month");
		Axis yAxis = barmodel.getAxis(AxisType.Y);
		yAxis.setLabel("Fees");
		yAxis.setMin(0);
		double max=0;

		for(FeeReportInfo ss:feesList)
		{

			if(max<Double.valueOf(ss.getFees()))
			{
				max=Double.valueOf(ss.getFees());
			}

		}
		yAxis.setMax(max+100);
		yAxis.setTickFormat("%d");
	}
	public BarChartModel getBarmodel() {
		return barmodel;
	}
	public void setBarmodel(BarChartModel barmodel) {
		this.barmodel = barmodel;
	}
	public ArrayList<FeeReportInfo> getFeesList() {
		return feesList;
	}
	public void setFeesList(ArrayList<FeeReportInfo> feesList) {
		this.feesList = feesList;
	}
	public ArrayList<SelectItem> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<SelectItem> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}
	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}
	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}
	public DataBaseMethodsReports getObj() {
		return obj;
	}
	public void setObj(DataBaseMethodsReports obj) {
		this.obj = obj;
	}
	public String getSelectRoute() {
		return selectRoute;
	}
	public void setSelectRoute(String selectRoute) {
		this.selectRoute = selectRoute;
	}
	public boolean isShowbar() {
		return showbar;
	}
	public void setShowbar(boolean showbar) {
		this.showbar = showbar;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public ArrayList<String> getOnlyRoutes() {
		return onlyRoutes;
	}
	public void setOnlyRoutes(ArrayList<String> onlyRoutes) {
		this.onlyRoutes = onlyRoutes;
	}
	
}
