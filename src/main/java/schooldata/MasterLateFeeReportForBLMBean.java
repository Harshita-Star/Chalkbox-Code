package schooldata;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

@ManagedBean(name="masterLateDueFeeReport")
@ViewScoped
public class MasterLateFeeReportForBLMBean implements Serializable
{
	ArrayList<SelectItem> branchList;
	String[] checkMonthSelected;
	Date date=new Date();
	ArrayList<SelectItem> installmentList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schname,name,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="",schid,branches;
	boolean show;
	double dueLateFee,collectLateFee,discountLateFee;

	public MasterLateFeeReportForBLMBean()
	{
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
				}
				else
				{
					branches = branches+","+String.valueOf(in.getValue());
				}
			}
		}

		installment();
	}

	public void installment()
	{
		installmentList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList.add(ss1);

		SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("6");
		installmentList.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("8");
		installmentList.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		installmentList.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("11");
		installmentList.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		installmentList.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("1");
		installmentList.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("3");
		installmentList.add(ss8);
	}


	public void showReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			String newSchId=schid;
			if(schid.equals("-1"))
			{
				newSchId=branches;
			}
			dueLateFee=obj.dueLateFeesForblm(newSchId, date, "", checkMonthSelected, conn);
			collectLateFee=obj.blmLateFeeCollectionAmount(newSchId,checkMonthSelected, conn);
			discountLateFee=obj.blmLateFeeCollectionDiscount(newSchId,checkMonthSelected, conn);
			show=true;
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



		name="Dynamic Public School";
		address1="Alwar 301001";
		address2="Govt. Recognized";
		//address3="Run By : Smart Education & Social welfare Society (Reg. No. 143 Alwar 2009-10)";
		address4="IMG GLOBAL INFOTECH,Jai Complex, Alwar";

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}
	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}

	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}

	public double getDueLateFee() {
		return dueLateFee;
	}

	public void setDueLateFee(double dueLateFee) {
		this.dueLateFee = dueLateFee;
	}

	public double getCollectLateFee() {
		return collectLateFee;
	}

	public void setCollectLateFee(double collectLateFee) {
		this.collectLateFee = collectLateFee;
	}

	public double getDiscountLateFee() {
		return discountLateFee;
	}

	public void setDiscountLateFee(double discountLateFee) {
		this.discountLateFee = discountLateFee;
	}
}
