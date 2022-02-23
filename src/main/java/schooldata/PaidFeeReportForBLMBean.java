package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name="paidFeeReportForBLM")
@ViewScoped

public class PaidFeeReportForBLMBean implements Serializable
{
	String name,selectedCLassSection,selectedSection,sectionName,className,totalamountString,selectedMonth,monthName;
	ArrayList<SelectItem> classSection,sectionList,branchList,tempInstallList,selectedInstallList,monthList,installmentList;
	Date date;
	int month=0,totalStudent;
	double totalDueAmount;
	ArrayList<StudentInfo>list;
	String[] checkMonthSelected;
	DatabaseMethods1 obj=new DatabaseMethods1();
	boolean showClass,showSchool,show;
	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="",schid,branches,newschid;

	public  PaidFeeReportForBLMBean()
	{
		selectedCLassSection=selectedSection="-1";
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
					newschid=String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
					newschid=newschid+","+String.valueOf(in.getValue());
				}
			}
		}
		installment();
		checkMonthSelected=new String[0];
		monthList=new DataBaseMethodsReports().monthListForReports();
	}


	public void checkMonth()
	{
		installmentList=new ArrayList<>();
		if(Integer.parseInt(selectedMonth)<3)
		{
			month=month+12;
		}
		else
		{
			month=Integer.parseInt(selectedMonth);
		}
		for(SelectItem ll:tempInstallList)
		{
			if(Integer.parseInt(ll.getValue().toString())<month)
			{
				if(ll.getLabel().equals("January"))
				{
					ll.setValue("1");
				}
				else if(ll.getLabel().equals("Feb-Mar"))
				{
					ll.setValue("3");
				}
				installmentList.add(ll);
			}
		}
	}

	public void installment()
	{
		tempInstallList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		tempInstallList.add(ss1);

		SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("6");
		tempInstallList.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("8");
		tempInstallList.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		tempInstallList.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("11");
		tempInstallList.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		tempInstallList.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("13");
		tempInstallList.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("15");
		tempInstallList.add(ss8);
	}

	public String monthName(int i)
	{
		String month="";
		if(i==4)
		{
			month="April";
		}
		else if(i==6)
		{
			month="May-June";
		}
		else if(i==8)
		{
			month="Jul-Aug";
		}
		else if(i==9)
		{
			month="September";
		}
		else if(i==11)
		{
			month="Oct-Nov";
		}
		else if(i==12)
		{
			month="December";
		}
		else if(i==1)
		{
			month="January";
		}
		else if(i==3)
		{
			month="Feb-Mar";
		}
		return month;
	}

	public void branchWiseWork()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedSection="-1";
		selectedCLassSection="-1";
		sectionList = new ArrayList<>();
		show=false;

		if(schid.equals("-1"))
		{
			showClass = false;
			showSchool=true;
			schname="B.L.M Academy";
			finalAddress="Haldwani";
			affiliationNo="";
			phoneno="";
		}
		else
		{
			showClass = true;
			showSchool=false;
			schoolInfo(schid);
			classSection=new DatabaseMethods1().allClass(schid,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public void allSections()
	{
		sectionList = new ArrayList<>();
		selectedSection="-1";
		if(!selectedCLassSection.equals("-1"))
		{
			Connection conn = DataBaseConnection.javaConnection();
			sectionList=obj.allSection(schid,selectedCLassSection,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void showReport()
	{
		if(schid.equals("-1"))
		{
			searchStudentByClassSection(newschid);
		}
		else
		{
			searchStudentByClassSection(schid);
		}
	}

	public void searchStudentByClassSection(String branches)
	{
		totalDueAmount=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{

			String session=DatabaseMethods1.selectedSessionDetails(branches,conn);
			String sectionid="";
			if(selectedCLassSection.equals("-1"))
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
			}
			else
			{
				sectionid = selectedSection;
			}

			int year=new Date().getYear()+1900;
			if(month>12)
			{
				year=year+1;
			}
			int day=obj.calculateDayInMonth(Integer.parseInt(selectedMonth), year);
			String startDate=year+"-"+selectedMonth+"-01";
			String endDate=year+"-"+selectedMonth+"-"+day;
			list=DBM.paidFeesForblm(branches,startDate,endDate, sectionid, checkMonthSelected, conn);
			selectedInstallList=new ArrayList<>();

			for(int i=0;i<checkMonthSelected.length;i++)
			{
				SelectItem ll=new SelectItem();
				ll.setLabel(checkMonthSelected[i]);
				ll.setValue(monthName(Integer.parseInt(checkMonthSelected[i])));
				selectedInstallList.add(ll);
			}


			if(list.size()>0)
			{

				for(StudentInfo ll:list)
				{
					totalDueAmount+=ll.getTotalFee();
				}
				totalStudent=list.size();
				monthName=obj.monthNameByNumber(Integer.parseInt(selectedMonth));
				totalamountString=String.format ("%.0f", totalDueAmount);

				if(schid.equals("-1"))
				{
					className="All";
					sectionName="All";
				}
				else
				{
					className=DBM.classNameFromidSchid(branches,selectedCLassSection,session,conn);
					sectionName=DBM.sectionNameByIdSchid(branches,selectedSection,conn);
				}
				show=true;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}

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


	public void schoolInfo(String schid)
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(schid,conn);
		schname=info.getSchoolName();
		address1=info.getAdd1();
		address2=info.getAdd2();
		address3=info.getAdd3();
		address4=info.getAdd4();
		phoneno=info.getPhoneNo();
		mobileno=info.getMobileNo();
		website=info.getWebsite();
		type=info.getClient_type();
		if(type.equalsIgnoreCase("institute"))
		{
			type="Institute";
		}
		else if(type.equalsIgnoreCase("school"))
		{
			type="School";
		}


		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		logo=savePath+info.getImagePath();
		headerImagePath=savePath+info.getMarksheetHeader();
		regno=info.getRegNo();
		finalAddress=address1;

		if(address2==null || address2.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address2;
		}

		if(address3==null || address3.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address3;
		}

		affiliationNo=address4;



		/*name="Dynamic Public School";
		address1="Alwar 301001";
		address2="Govt. Recognized";
		//address3="Run By : Smart Education & Social welfare Society (Reg. No. 143 Alwar 2009-10)";
		address4="IMG GLOBAL INFOTECH,Jai Complex, Alwar";*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public boolean isShowSchool() {
		return showSchool;
	}

	public void setShowSchool(boolean showSchool) {
		this.showSchool = showSchool;
	}

	public String getSchname() {
		return schname;
	}

	public void setSchname(String schname) {
		this.schname = schname;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getFinalAddress() {
		return finalAddress;
	}

	public void setFinalAddress(String finalAddress) {
		this.finalAddress = finalAddress;
	}

	public String getAffiliationNo() {
		return affiliationNo;
	}

	public void setAffiliationNo(String affiliationNo) {
		this.affiliationNo = affiliationNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}

	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public String getTotalamountString() {
		return totalamountString;
	}

	public void setTotalamountString(String totalamountString) {
		this.totalamountString = totalamountString;
	}


	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}


	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}


	public ArrayList<SelectItem> getSelectedInstallList() {
		return selectedInstallList;
	}


	public void setSelectedInstallList(ArrayList<SelectItem> selectedInstallList) {
		this.selectedInstallList = selectedInstallList;
	}


	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}


	public ArrayList<StudentInfo> getList() {
		return list;
	}


	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}


	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}


	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}


	public String getSelectedMonth() {
		return selectedMonth;
	}


	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}


	public String getMonthName() {
		return monthName;
	}


	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}


	public int getTotalStudent() {
		return totalStudent;
	}


	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}

}
