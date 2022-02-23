package schooldata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import exam.DatabaseMethodExam;
import session_work.RegexPattern;

@ManagedBean(name = "transportBeanReport")
@ViewScoped
public class TransportFeeReportBean {

	String regex = RegexPattern.REGEX;
	Date feedate = new Date(), endDate;
	Date date;
	boolean show;
	String var;
	String selectedClass, selectedSection;
	String totalamountString;
	ArrayList<SelectItem> classList, sectionList;
	ArrayList<StudentInfo> studentList;
	String schid;
	DatabaseMethods1 obj = new DatabaseMethods1();
	DataBaseMethodStudent objStd = new DataBaseMethodStudent();
	ArrayList<FeeInfo> classFeeList;
	ArrayList<FeeDynamicList> installmentList;
	ArrayList<SelectItem> feeses = new ArrayList<>();

	public TransportFeeReportBean() {
		Connection conn = DataBaseConnection.javaConnection();
		date = new Date();
		classList = obj.allClass(conn);
		installmentList = new DatabaseMethods1().getAllInstallment(new DatabaseMethods1().schoolId(), conn);
		classFeeList = obj.viewFeeList1(new DatabaseMethods1().schoolId(), conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query) {
		Connection conn = DataBaseConnection.javaConnection();
		studentList = obj.searchStudentList(query, conn);
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList) {
			// studentListt.add(info.getFname() + "-" + info.getAddNumber());
			studentListt.add(info.getFname() + " / " + info.getFathersName() + "-" + info.getClassName() + "-:"
					+ info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = obj.allSection(selectedClass, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showDueReport() {

		Connection conn = DataBaseConnection.javaConnection();

		ArrayList<FeeDynamicList> feeListTest = new ArrayList<>();

		for (FeeDynamicList ls : installmentList) {
			feeListTest.add(ls);
		}
		
		String sectionid="";
		if(selectedClass.equals("-1"))
		{
			sectionid="-1";
		}
		else if(selectedSection.equals("-1"))
		{
			for(SelectItem ii : sectionList)
			{
				if(sectionid.equals(""))
				{
					sectionid = String.valueOf(ii.getValue());
				}
				else
				{
					sectionid = sectionid + "','" + String.valueOf(ii.getValue());
				}
			}
			
			sectionid="('"+sectionid+"')";
			
		}
		else  
		{
			sectionid = "('"+selectedSection+"')";
		}
		
		
		studentList = new ArrayList<>();
		studentList = new DatabaseMethodExam().getDueFeesReport(obj.schoolId(), date, selectedSection, feeListTest,
				"All", selectedClass, conn);
		double total = 0.0;
		for (StudentInfo ss : studentList) {
			total += ss.getTotalFee();
		}
		totalamountString = String.valueOf(total);

		if (studentList.size() > 0) {
			var = "Due";
			show = true;
		} else {
			show = false;
		}

		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public void showPaidReport() {
		Connection conn = DataBaseConnection.javaConnection();

		ArrayList<FeeDynamicList> feeListTest = new ArrayList<>();

		for (FeeDynamicList ls : installmentList) {
			feeListTest.add(ls);
		}
		
		String sectionid="";
		if(selectedClass.equals("-1"))
		{
			sectionid="-1";
		}
		else if(selectedSection.equals("-1"))
		{
			for(SelectItem ii : sectionList)
			{
				if(sectionid.equals(""))
				{
					sectionid = String.valueOf(ii.getValue());
				}
				else
				{
					sectionid = sectionid + "','" + String.valueOf(ii.getValue());
				}
			}
			
			sectionid="('"+sectionid+"')";
			
		}
		else  
		{
			sectionid = "('"+selectedSection+"')";
		}
		
		
		
		studentList = new ArrayList<>();
		studentList = new DatabaseMethodExam().getPaidFeesReport(obj.schoolId(), sectionid, feeListTest,
				selectedClass, conn);
		int i = 1;
		for (StudentInfo g : studentList) {
			g.setsNo(Integer.toString(i++));
			for (FeeDynamicList j : installmentList) {
				if (g.getFeesMap().containsKey(j.getInsatllmentValue()) == false) {
					g.getFeesMap().put(j.getInsatllmentValue(), "0");
				}
			}
		}

		if (studentList.size() > 0) {
			var = "Paid";
			show = true;
		} else {
			show = false;
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getFeeses() {
		return feeses;
	}

	public void setFeeses(ArrayList<SelectItem> feeses) {
		this.feeses = feeses;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public Date getFeedate() {
		return feedate;
	}

	public void setFeedate(Date feedate) {
		this.feedate = feedate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
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

	

	public String getTotalamountString() {
		return totalamountString;
	}

	public void setTotalamountString(String totalamountString) {
		this.totalamountString = totalamountString;
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

	

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public DataBaseMethodStudent getObjStd() {
		return objStd;
	}

	public void setObjStd(DataBaseMethodStudent objStd) {
		this.objStd = objStd;
	}

	

	public ArrayList<FeeDynamicList> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(ArrayList<FeeDynamicList> installmentList) {
		this.installmentList = installmentList;
	}

	

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

}
