package schooldata;



import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;



@ManagedBean(name="cashFlowReport")
@ViewScoped
public class CashFlowReportBean implements Serializable
{
	String type="IN/OUT",basis,month="",year="", bankName;

	ArrayList<SelectItem> yearList,monthList, bankList;

	ArrayList<UserInfo> extraExpenseList,finalList,extraIncomeList,inList,outList, bankInList, bankOutList,extraIncomeBankList,extraExpenseBankList;

	ArrayList<UserInfo> shareAmountList;
	boolean showCashBook,showParticular,renderDaily,renderCustom,renderMonthly,renderYearly,showReportTable,renderTransfer,
	renderIn,renderOut;
	double totalAmount=0,totalEntry=0,totalPenalty=0,inAmount=0,outAmount=0, totalIn, totalOut, openingBalance;
	Date todayDate=null,startDate=new Date(),endDate=new Date(), startSessionDate;
	Date tdyDate,standard,date1,date2,date3,date4,date5,date6,date7,date8;
	Timestamp tdydt,stdt,stndrd,dt1,dt2,dt3,dt4,dt5,dt6,dt7,dt8;
	String ssDate;
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	DecimalFormat db=new DecimalFormat("##.##");
	public CashFlowReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		basis="custom";
		bankList=new ArrayList<>();
		bankList=new DataBaseMethodsIncome_Expense().allBankList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkTypeWithBasis()
	{
		if(basis.equals("daily"))
		{
			renderDaily=true;
			renderMonthly=false;
			renderYearly=false;
			renderCustom=false;
			startDate=endDate=null;
			month="";
			year="";
		}
		else if(basis.equals("monthly"))
		{
			monthList=new ArrayList<>();
			for(int i=1;i<=12;i++)
			{
				SelectItem info=new SelectItem();
				info.setValue(i);
				info.setLabel(new DatabaseMethods1().monthNameByNumber(i));
				monthList.add(info);
			}
			renderDaily=false;
			renderMonthly=true;
			renderYearly=false;
			renderCustom=false;
			todayDate=null;
			startDate=endDate=null;
			year="";
		}
		else if(basis.equals("yearly"))
		{
			yearList=new ArrayList<>();

			for(int i=2013;i<=2030;i++)
			{
				SelectItem info=new SelectItem();
				info.setValue(i);
				info.setLabel(String.valueOf(i));
				yearList.add(info);
			}

			renderDaily=false;
			renderMonthly=false;
			renderYearly=true;
			renderCustom=false;
			month="";
			todayDate=null;
			startDate=endDate=null;

		}
		else if(basis.equals("custom"))
		{
			renderDaily=false;
			renderMonthly=false;
			renderYearly=false;
			renderCustom=true;
			todayDate=null;
			month="";
			year="";
		}
	}

	public void viewReport() throws SQLException, ParseException
	{
		DecimalFormat df=db;

		if(startDate.equals(endDate) || startDate.before(endDate))
		{
			String sDate="01-04-2017";
			//String eDate="31-03-2016";

			SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
			startSessionDate=f.parse(sDate);
			ssDate=new SimpleDateFormat("yyyy-MM-dd").format(startSessionDate.getTime());

			if(startDate.after(startSessionDate) || startDate.equals(startSessionDate))
			{
				bankName="N/A";
				if(type.equals("IN"))
				{
					showCashBook=false;
					renderTransfer=true;
					renderIn=true;
					renderOut=false;
					showParticular=false;
					in(todayDate,month,year,startDate,endDate);
					finalList=new ArrayList<>();
					finalList.addAll(inList);
					totalEntry=finalList.size();
					int i=1;
					totalAmount=totalPenalty=0;
					for(UserInfo uu : finalList)
					{
						uu.setSno(i++);
						if((uu.getTransferredToBankName()==null || uu.getTransferredToBankName().equals("")))
						{
							totalAmount+=uu.getAmount();


							totalAmount=Double.valueOf(df.format(totalAmount));
							totalPenalty+=uu.getPenalty();
						}

					}
					if(finalList.size()>0)
					{
						showReportTable=true;
					}
					else
					{
						showReportTable=false;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found!"));

					}
				}
				else if(type.equals("OUT"))
				{
					showCashBook=false;
					renderOut=true;
					renderIn=false;
					renderTransfer=false;
					showParticular=false;
					out(todayDate,month,year,startDate,endDate);
					finalList=new ArrayList<>();
					finalList.addAll(outList);

					totalEntry=finalList.size();
					int i=1;
					totalAmount=totalPenalty=0;
					for(UserInfo uu : finalList)
					{
						uu.setSno(i++);
						uu.setAmountOut(uu.getAmount());
						uu.setAmount(0);
						if(uu.getPayMode().equalsIgnoreCase("cash"))
						{
							totalAmount+=uu.getAmountOut();
							totalAmount=Double.valueOf(df.format(totalAmount));
							totalPenalty+=uu.getPenalty();
						}

					}
					if(finalList.size()>0)
					{
						showReportTable=true;
					}
					else
					{
						showReportTable=false;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found!"));

					}
				}
				else if(type.equals("IN/OUT"))
				{
					renderTransfer=true;
					renderIn=renderOut=true;
					showParticular=true;
					in(todayDate,month,year,startDate,endDate);
					out(todayDate,month,year,startDate,endDate);
					finalList=new ArrayList<>();
					finalList.addAll(inList);
					finalList.addAll(outList);
					totalEntry=finalList.size();
					int i=1;
					totalAmount=totalPenalty=inAmount=outAmount=0;


					for(UserInfo uu : finalList)
					{
						uu.setSno(i++);
						if(uu.getType().equals("out"))
						{
							uu.setAmountOut(uu.getAmount());

							uu.setAmount(0);
							//	if(uu.getPayMode().equalsIgnoreCase("cash"))
							{
								outAmount+=uu.getAmountOut();
								outAmount=Double.valueOf(df.format(outAmount));
								totalPenalty+=uu.getPenalty();
							}
						}
						else
						{
							//if((uu.getTransferredToBankName()==null || uu.getTransferredToBankName().equals("")))
							{

								inAmount+=uu.getAmount();
								inAmount=Double.valueOf(df.format(inAmount));
								totalPenalty+=uu.getPenalty();
							}
						}

					}
					if(finalList.size()>0)
					{
						showReportTable=true;
					}
					else
					{
						showReportTable=false;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found!"));

					}
					totalAmount=Double.valueOf(df.format(inAmount))+
							Double.valueOf(df.format(outAmount));
					totalAmount=Double.valueOf(df.format(totalAmount));
				}

				if(finalList.size()>0)
				{
					showCashBook=true;
				}
				else
				{
					showCashBook=false;
				}

				Collections.sort(finalList);


				for(int i=1;i<=finalList.size();i++)
				{
					finalList.get(i-1).setSno(i);
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Not a valid Date Selection For Current Finalnical Year !!!"));
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Date Properly !!!"));
		}
	}

	public void in(Date tdyDate, String mnth, String yr,Date strtdt,Date enddt) throws SQLException
	{
		Connection conn = DataBaseConnection.javaConnection();
		inList=new ArrayList<>();
		totalAmount=totalEntry=totalPenalty=0;

		extraIncomeList=new DatabaseMethods1().allIncomeListCashFlow(tdyDate,mnth,yr,strtdt,enddt,conn);

		inList.addAll(extraIncomeList);


		for(UserInfo ii : inList)
		{
			ii.setType("in");
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void printBankBook() throws SQLException
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		bankInList=new ArrayList<>();bankOutList=new ArrayList<>();
		openingBalance=totalIn=totalOut=0;
		extraIncomeBankList=obj.allIncomeListBankFlow(todayDate,month,year,startDate,endDate, bankName,conn);
		bankInList.addAll(extraIncomeBankList);

		for(UserInfo hh : bankInList)
		{
			hh.setType("bankIn");
		}

		extraExpenseBankList=obj.allExpenseListForBankFlow(todayDate,month,year,startDate,endDate, bankName,conn);
		bankOutList.addAll(extraExpenseBankList);

		for(UserInfo hh : bankOutList)
		{
			hh.setAmountOut(hh.getAmount());
			hh.setAmount(0);
		}


		totalIn=obj.openingBalanceFromIncomeTable(startDate, bankName,conn);
		totalOut=obj.openingBalanceFromExpenseTable(startDate, bankName,conn);

		openingBalance=totalIn-totalOut;
		openingBalance=Double.parseDouble(db.format(openingBalance));


		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("inlist",bankInList);
		ss.setAttribute("outList",bankOutList);
		String customDate=sdf.format(startDate)+"-"+sdf.format(endDate);
		ss.setAttribute("date",customDate);
		ss.setAttribute("bank", bankName);
		ss.setAttribute("balance",openingBalance);
		ss.setAttribute("dt", startDate);

		PrimeFaces.current().executeInitScript("window.open('printBankBook.xhtml')");

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//**************************************************************************************************************************

	public void out(Date tdyDate, String mnth, String yr,Date strtdt,Date enddt) throws SQLException
	{
		Connection conn = DataBaseConnection.javaConnection();
		totalOut=0;
		outList=new ArrayList<>();
		totalAmount=totalEntry=totalPenalty=0;
		extraExpenseList=new DatabaseMethods1().allExpenseListForCashFlow(tdyDate,mnth,yr,strtdt,enddt,conn);



		outList.addAll(extraExpenseList);

		for(UserInfo oo : outList)
		{
			oo.setType("out");
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	public void printCashBook2()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();


		totalIn=obj.openingBalanceFromIncomeTable(startDate, "cash",conn);
		totalOut=obj.openingBalanceFromExpenseTable(startDate, "cash",conn);

		openingBalance=totalIn-totalOut;
		openingBalance=Double.parseDouble(db.format(openingBalance));



		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("inlist",inList);
		ss.setAttribute("outList",outList);
		ss.setAttribute("balance",openingBalance);
		ss.setAttribute("dt", startDate);

		String customDate=sdf.format(startDate)+"-"+sdf.format(endDate);
		ss.setAttribute("date",customDate);

		PrimeFaces.current().executeInitScript("window.open('printCashBook2.xhtml')");

		inList=outList=new ArrayList<>();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void print()
	{
		Date date=new Date();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("list",finalList);
		ss.setAttribute("total",totalAmount);
		ss.setAttribute("totalPenalty",totalPenalty);
		ss.setAttribute("size",totalEntry);
		ss.setAttribute("type",type);
		ss.setAttribute("basis",basis);
		if(basis.equals("daily"))
		{
			ss.setAttribute("date",sdf.format(todayDate));
		}
		else if(basis.equals("monthly"))
		{
			String monthName=new DatabaseMethods1().monthNameByNumber(Integer.valueOf(month));
			ss.setAttribute("date",(monthName+" - "+String.valueOf(date.getYear()+1900)));
		}
		else if(basis.equals("yearly"))
		{
			ss.setAttribute("date",year);
		}
		else if(basis.equals("custom"))
		{
			String customDate=sdf.format(startDate)+"-"+sdf.format(endDate);
			ss.setAttribute("date",customDate);
		}

		PrimeFaces.current().executeInitScript("window.open('printCashFlowReport.xhtml')");

	}
	//****************************************************************************************************************


	public void setAllDates()
	{
		date1=new Date();date2=new Date();
		date3=new Date();date4=new Date();
		date5=new Date();date6=new Date();
		date7=new Date();date8=new Date();

		dt1=new Timestamp(date1.getTime());dt2=new Timestamp(date2.getTime());
		dt3=new Timestamp(date3.getTime());dt4=new Timestamp(date4.getTime());
		dt5=new Timestamp(date5.getTime());dt6=new Timestamp(date6.getTime());
		dt7=new Timestamp(date7.getTime());dt8=new Timestamp(date8.getTime());


		standard=new Date();
		tdyDate=new Date();
		tdydt=new Timestamp(endDate.getTime());
		tdydt.setHours(0);tdydt.setMinutes(0);tdydt.setSeconds(0);tdydt.setNanos(0);

		stdt=new Timestamp(startDate.getTime());
		stdt.setHours(0);stdt.setMinutes(0);stdt.setSeconds(0);stdt.setNanos(0);

		stndrd=new Timestamp(endDate.getTime());
		stndrd.setDate(30);
		stndrd.setMonth(5);
		stndrd.setYear(endDate.getYear());
		stndrd.setHours(0);stndrd.setMinutes(0);stndrd.setSeconds(0);stndrd.setNanos(0);

		dt1.setDate(1);
		dt1.setMonth(3);
		dt1.setHours(0);dt1.setMinutes(0);dt1.setSeconds(0);dt1.setNanos(0);

		dt2.setDate(30);
		dt2.setMonth(5);
		dt2.setHours(0);dt2.setMinutes(0);dt2.setSeconds(0);dt2.setNanos(0);

		dt3.setDate(1);
		dt3.setMonth(6);
		dt3.setHours(0);dt3.setMinutes(0);dt3.setSeconds(0);dt3.setNanos(0);

		dt4.setDate(30);
		dt4.setMonth(8);
		dt4.setHours(0);dt4.setMinutes(0);dt4.setSeconds(0);dt4.setNanos(0);

		dt5.setDate(1);
		dt5.setMonth(9);
		dt5.setHours(0);dt5.setMinutes(0);dt5.setSeconds(0);dt5.setNanos(0);

		dt6.setDate(31);
		dt6.setMonth(11);
		dt6.setHours(0);dt6.setMinutes(0);dt6.setSeconds(0);dt6.setNanos(0);

		dt7.setDate(1);
		dt7.setMonth(0);
		dt7.setHours(0);dt7.setMinutes(0);dt7.setSeconds(0);dt7.setNanos(0);

		dt8.setDate(31);
		dt8.setMonth(2);
		dt8.setHours(0);dt8.setMinutes(0);dt8.setSeconds(0);dt8.setNanos(0);


		//rdFdList=new DataBase().showUser();
	}

	public boolean isRenderDaily() {
		return renderDaily;
	}

	public void setRenderDaily(boolean renderDaily) {
		this.renderDaily = renderDaily;
	}

	public boolean isRenderMonthly() {
		return renderMonthly;
	}

	public void setRenderMonthly(boolean renderMonthly) {
		this.renderMonthly = renderMonthly;
	}

	public boolean isRenderYearly() {
		return renderYearly;
	}

	public void setRenderYearly(boolean renderYearly) {
		this.renderYearly = renderYearly;
	}

	public String getBasis() {
		return basis;
	}

	public void setBasis(String basis) {
		this.basis = basis;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

	public ArrayList<UserInfo> getFinalList() {
		return finalList;
	}

	public void setFinalList(ArrayList<UserInfo> finalList) {
		this.finalList = finalList;
	}
	public boolean isShowReportTable() {
		return showReportTable;
	}

	public void setShowReportTable(boolean showReportTable) {
		this.showReportTable = showReportTable;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getTotalEntry() {
		return totalEntry;
	}

	public void setTotalEntry(double totalEntry) {
		this.totalEntry = totalEntry;
	}

	public double getTotalPenalty() {
		return totalPenalty;
	}

	public void setTotalPenalty(double totalPenalty) {
		this.totalPenalty = totalPenalty;
	}

	public boolean isRenderCustom() {
		return renderCustom;
	}

	public void setRenderCustom(boolean renderCustom) {
		this.renderCustom = renderCustom;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isRenderTransfer() {
		return renderTransfer;
	}

	public void setRenderTransfer(boolean renderTransfer) {
		this.renderTransfer = renderTransfer;
	}

	public boolean isRenderIn() {
		return renderIn;
	}

	public void setRenderIn(boolean renderIn) {
		this.renderIn = renderIn;
	}

	public boolean isRenderOut() {
		return renderOut;
	}

	public void setRenderOut(boolean renderOut) {
		this.renderOut = renderOut;
	}

	public double getInAmount() {
		return inAmount;
	}

	public void setInAmount(double inAmount) {
		this.inAmount = inAmount;
	}

	public double getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(double outAmount) {
		this.outAmount = outAmount;
	}

	public boolean isShowParticular() {
		return showParticular;
	}

	public void setShowParticular(boolean showParticular) {
		this.showParticular = showParticular;
	}

	public boolean isShowCashBook() {
		return showCashBook;
	}

	public void setShowCashBook(boolean showCashBook) {
		this.showCashBook = showCashBook;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public ArrayList<SelectItem> getBankList() {
		return bankList;
	}

	public void setBankList(ArrayList<SelectItem> bankList) {
		this.bankList = bankList;
	}

	public double getTotalIn() {
		return totalIn;
	}

	public void setTotalIn(double totalIn) {
		this.totalIn = totalIn;
	}

	public double getTotalOut() {
		return totalOut;
	}

	public void setTotalOut(double totalOut) {
		this.totalOut = totalOut;
	}

	public double getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(double openingBalance) {
		this.openingBalance = openingBalance;
	}
}
