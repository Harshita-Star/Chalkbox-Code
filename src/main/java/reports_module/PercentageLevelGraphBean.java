package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import exam_module.DataBaseMethodsExam;
import exam_module.DataBaseMethodsExamReport;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;


@ManagedBean(name="percentageLevelGraph")
@ViewScoped
public class PercentageLevelGraphBean implements Serializable{
	LineChartModel lineModel1;
	ArrayList<termInfo> newtermList = new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>();
	ArrayList<SelectItem> termList=new ArrayList<>();
	ArrayList<SelectItem>examList = new ArrayList<>();
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	String selectClass,selectTerm,selectSection,termName,schId,session;
	boolean show=false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DataBaseMethodsExamReport objExamReport=new DataBaseMethodsExamReport();
	
	int thirtyToForty,FortyFifty,fiftySixty,sixtySeventy,zeroThirty,seventyEighty,EightyNinety,NinetyHunderd;
	int arr[] = new int[10];
	ArrayList<GradeInfo> gradeList;
	  
	public PercentageLevelGraphBean()
	{
		
		Connection conn = DataBaseConnection.javaConnection();
		classList=obj1.allClass(conn);
		schId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schId, conn);
		selectTerm="all";
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}

	


	public void show()
	{
		Connection conn=DataBaseConnection.javaConnection();
		show=false;

	         thirtyToForty=0;FortyFifty=0;fiftySixty=0;sixtySeventy=0;zeroThirty=0;seventyEighty=0;EightyNinety=0;NinetyHunderd=0;
	         
	         if(selectTerm.equalsIgnoreCase("all")) {
	        	 
	        	  gradeList = new ArrayList<>();
	        	 
	        	 for (int i = 0; i < termList.size(); i++) {
                
	    	         thirtyToForty=0;FortyFifty=0;fiftySixty=0;sixtySeventy=0;zeroThirty=0;seventyEighty=0;EightyNinety=0;NinetyHunderd=0;

	    	         studentList=de.studentBasicDetailsForMarksheet(schId,"", conn,"byClass",selectSection);
	        			
	        		    
	        			examList=de.mainExamListOfClassSection(selectClass,"scholastic",termList.get(i).getValue().toString(),conn);
	        			
	        			for(int k=0;k<studentList.size();k++)
	        			{
	        				double avg=0,totalSum =0,totalMax =0;
		        			for(SelectItem ss:examList)
		        			{
		        				
		        				double sum=objExamReport.selectObtainMarksFromTable(conn, studentList.get(k).getAddNumber(), ss.getValue().toString(), termList.get(i).getValue().toString(), "", "studentSectionWise", schId, session, selectSection);
		        				double maxmarks=objExamReport.selectMaxMarksFromTable(conn, studentList.get(k).getAddNumber(), ss.getValue().toString(), termList.get(i).getValue().toString(), "", "studentSectionWise", schId, session, selectSection);
		        			
		        				
		        			     totalSum += sum;
		        			     totalMax += maxmarks;
		        				
		        			}
		        			try {
		        				avg = totalSum*100/totalMax;
		        			} catch (Exception e) {
		        				e.printStackTrace();
		        			}
	        		
	        			if((avg>=0)&&(avg<=32)) {
	        				zeroThirty ++;
	        			}
	        			if((avg>=32)&&(avg<=40)) {
	        				thirtyToForty ++;
	        			}
	        			if((avg>=40)&&(avg<=50)) {
	        				FortyFifty ++;
	        			}
	        			if((avg>=50)&&(avg<=60)) {
	        				fiftySixty ++;
	        			}
	        			if((avg>=60)&&(avg<=70)) {
	        				sixtySeventy ++;
	        			}
	        			if((avg>=70)&&(avg<=80)) {
	        				seventyEighty ++;
	        			}
	        			if((avg>=80)&&(avg<=90)) {
	        				EightyNinety ++;
	        			}
	        			if((avg>=90)&&(avg<=100)) {
	        				NinetyHunderd ++;
	        			}	
	        			
	        			
	        			}
	        			GradeInfo gg = new GradeInfo();
	        			
	        			gg.setZeroThirty(zeroThirty);
	        			gg.setThirtyToForty(thirtyToForty);
	        			gg.setFortyFifty(FortyFifty);
	        			gg.setFiftySixty(fiftySixty);
                        gg.setSixtySeventy(sixtySeventy);
                        gg.setSeventyEighty(seventyEighty);
                        gg.setEightyNinety(EightyNinety);
                        gg.setNinetyHunderd(NinetyHunderd);
                       gradeList.add(gg);
	        			
	        		 
				}
	        	 
	         }
	         else {
	        	 studentList=de.studentBasicDetailsForMarksheet(schId,"", conn,"byClass",selectSection);
		    
			examList=de.mainExamListOfClassSection(selectClass,"scholastic",selectTerm,conn);
			
			for(int k=0;k<studentList.size();k++) {
				
			
			//	//// // System.out.println("Student");
				double avg=0,totalSum =0,totalMax =0;
			for(SelectItem ss:examList)
			{
				double sum=objExamReport.selectObtainMarksFromTable(conn, studentList.get(k).getAddNumber(), ss.getValue().toString(),selectTerm, "", "studentSectionWise", schId, session, selectSection);
				double maxmarks=objExamReport.selectMaxMarksFromTable(conn, studentList.get(k).getAddNumber(), ss.getValue().toString(), selectTerm, "", "studentSectionWise", schId, session, selectSection);
			
			     totalSum += sum;
			     totalMax += maxmarks;
			}
			try {
				avg = totalSum*100/totalMax;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if((avg>=0)&&(avg<=32)) {
				zeroThirty ++;
			}
			if((avg>=32)&&(avg<=40)) {
				thirtyToForty ++;
			}
			if((avg>=40)&&(avg<=50)) {
				FortyFifty ++;
			}
			if((avg>=50)&&(avg<=60)) {
				fiftySixty ++;
			}
			if((avg>=60)&&(avg<=70)) {
				sixtySeventy ++;
			}
			if((avg>=70)&&(avg<=80)) {
				seventyEighty ++;
			}
			if((avg>=80)&&(avg<=90)) {
				EightyNinety ++;
			}
			if((avg>=90)&&(avg<=100)) {
				NinetyHunderd ++;
			}			
			
			}
			
	         }

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		show=true;
		createLineModels();
	}
	



	public void allTerm()
	{
		selectTerm="all";
		newtermList = new ArrayList<>();
		Connection conn=DataBaseConnection.javaConnection();

		termList=obj1.selectedTermOfClass(selectClass,conn,session,schId);

		sectionList=obj1.allSection(selectClass,conn);

		for(SelectItem ss:termList)
		{
			termInfo tt  = new termInfo();
			tt.setTermId(String.valueOf(ss.getValue()));
			tt.setTermName(ss.getLabel());
			newtermList.add(tt);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}





	private void createLineModels() {
		lineModel1 = initLinearModel();
		lineModel1.setTitle("Percentage Level Chart");
		lineModel1.setLegendPosition("e");
		lineModel1.setExtender("barext");
		lineModel1.setAnimate(true);
		lineModel1.getAxis(AxisType.Y);
		lineModel1.setShowPointLabels(true);
		  lineModel1.getAxes().put(AxisType.X, new CategoryAxis("Years"));;
		/*lineModel1.setShowPointLabels(true);
    lineModel1.getAxes().put(AxisType.X, new CategoryAxis("Percentage"));
    lineModel1.getAxes().put(AxisType.Y, new CategoryAxis("Percentage"));*/


		Axis yAxis = lineModel1.getAxis(AxisType.Y);
		yAxis.setMin(0);
		
		yAxis.setLabel("No of Students");
		Axis xAxis = lineModel1.getAxis(AxisType.X);
		xAxis.setLabel("Levels");
	}


	private LineChartModel initLinearModel() {
		LineChartModel model = new LineChartModel();
		Connection conn=DataBaseConnection.javaConnection();

		 if(selectTerm.equalsIgnoreCase("all")) {
			 
			 for (int i = 0; i < termList.size(); i++) {
			 LineChartSeries series1 = new LineChartSeries();
			 termName=obj1.semesterNameFromid(termList.get(i).getValue().toString(), conn);
				series1.setLabel(termName);
			        series1.set("0-32(E Grade)",gradeList.get(i).getZeroThirty() );
					series1.set("32-40(D Grade)",gradeList.get(i).getThirtyToForty() );
					series1.set("40-50(C2 Grade)",gradeList.get(i).getFortyFifty());
					series1.set("50-60(C1 Grade)",gradeList.get(i).getFiftySixty());
					series1.set("60-70(B2 Grade)", gradeList.get(i).getSixtySeventy());
					series1.set("70-80(B1 Grade)",gradeList.get(i).getSeventyEighty());
					series1.set("80-90(A2 Grade)",gradeList.get(i).getEightyNinety() );
					series1.set("90-100(A1 Grade)",gradeList.get(i).getNinetyHunderd());
					
				
				model.addSeries(series1);
			 }
		 }
		 else {
			LineChartSeries series1 = new LineChartSeries();
			termName=obj1.semesterNameFromid(selectTerm, conn);
			series1.setLabel(termName);
		        series1.set("0-32(E Grade)", zeroThirty);
				series1.set("32-40(D Grade)",thirtyToForty );
				series1.set("40-50(C2 Grade)", FortyFifty);
				series1.set("50-60(C1 Grade)",fiftySixty );
				series1.set("60-70(B2 Grade)", sixtySeventy);
				series1.set("70-80(B1 Grade)",seventyEighty);
				series1.set("80-90(A2 Grade)",EightyNinety );
				series1.set("90-100(A1 Grade)", NinetyHunderd);
				
			
			model.addSeries(series1);
		 }
		 try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}



	public LineChartModel getLineModel1() {
		return lineModel1;
	}
	public void setLineModel1(LineChartModel lineModel1) {
		this.lineModel1 = lineModel1;
	}
	public ArrayList<termInfo> getNewtermList() {
		return newtermList;
	}
	public void setNewtermList(ArrayList<termInfo> newtermList) {
		this.newtermList = newtermList;
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

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}
	public ArrayList<SelectItem> getExamList() {
		return examList;
	}
	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}
	public String getSelectClass() {
		return selectClass;
	}
	public void setSelectClass(String selectClass) {
		this.selectClass = selectClass;
	}
	public String getSelectTerm() {
		return selectTerm;
	}
	public void setSelectTerm(String selectTerm) {
		this.selectTerm = selectTerm;
	}

	public String getSelectSection() {
		return selectSection;
	}




	public void setSelectSection(String selectSection) {
		this.selectSection = selectSection;
	}




	public String getTermName() {
		return termName;
	}




	public void setTermName(String termName) {
		this.termName = termName;
	}




	public DatabaseMethods1 getObj1() {
		return obj1;
	}




	public void setObj1(DatabaseMethods1 obj1) {
		this.obj1 = obj1;
	}




	public DataBaseMethodsExam getDe() {
		return de;
	}




	public void setDe(DataBaseMethodsExam de) {
		this.de = de;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}


}

