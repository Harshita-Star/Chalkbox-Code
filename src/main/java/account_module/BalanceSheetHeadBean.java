package account_module;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="balanceSheetHead")
@ViewScoped
public class BalanceSheetHeadBean implements Serializable
{

	ArrayList<UserInfo> assestList = new ArrayList<>();
	ArrayList<UserInfo> liablitiesList = new ArrayList<>();
	ArrayList<UserInfo> incomeList = new ArrayList<>();
	ArrayList<UserInfo> expenseList = new ArrayList<>();
	ArrayList<UserInfo> incomeList3 = new ArrayList<>();

	DataBase dbObj = new DataBase();
	String schId,financialYear,strAssAmount,strLibAmount;
	Date endDate;
	double assAmount,libAmount,totalIncome,totalExpense;
	boolean shw=false;
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public BalanceSheetHeadBean()
	{
		Connection con = DataBaseConnection.javaConnection();
		assAmount=libAmount=0.0;
		schId = obj1.schoolId();
		financialYear = obj1.selectedSessionDetails(schId,con);
		
		endDate = new Date();

		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String search()
	{
		assAmount=libAmount=totalIncome=totalExpense=0.0;
		Connection con = DataBaseConnection.javaConnection();

		liablitiesList= dbObj.balanceSheetDetails("Liablities",schId,financialYear,endDate,con);
		assestList= dbObj.balanceSheetDetails("ASSETS",schId,financialYear,endDate,con);

		incomeList3 = new ArrayList<>();
		shw=true;

		incomeList=dbObj.balanceSheetDetails("income",schId,financialYear,endDate,con);
		expenseList=dbObj.balanceSheetDetails("EXPENSES",schId,financialYear,endDate,con);


		for(UserInfo ff:incomeList)
		{
			totalIncome=totalIncome+ff.getAmount();
		}
		for(UserInfo ff:expenseList)
		{
			totalExpense=totalExpense+ff.getAmount();
		}


		if(liablitiesList.size()>assestList.size())
		{
			for(int i=0;i<liablitiesList.size();i++)
			{
				UserInfo obj = new  UserInfo();
				obj.setLibName(liablitiesList.get(i).getLibName());
				obj.setLibAmount(String.valueOf(liablitiesList.get(i).getAmount()));
				if(i<assestList.size())
				{
					obj.setAssetsName(assestList.get(i).getLibName());
					obj.setAssetsAmount(String.valueOf(assestList.get(i).getAmount()));
				}
				else if(i==assestList.size())
				{
					obj.setAssetsName("Net Profit");
					obj.setAssetsAmount(String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(totalIncome-totalExpense))));
				}
				else
				{
					obj.setAssetsName("");
					obj.setAssetsAmount("");
				}

				incomeList3.add(obj);
			}
		}
		else if(assestList.size()>liablitiesList.size())
		{
			for(int i=0;i<assestList.size();i++)
			{
				UserInfo obj = new  UserInfo();
				obj.setAssetsName(assestList.get(i).getLibName());
				obj.setAssetsAmount(String.valueOf(assestList.get(i).getAmount()));
				if(i<liablitiesList.size())
				{
					obj.setLibName(liablitiesList.get(i).getLibName());
					obj.setLibAmount(String.valueOf(liablitiesList.get(i).getAmount()));
				}
				else
				{
					obj.setLibName("");
					obj.setLibAmount("");

				}
				incomeList3.add(obj);
			}
			UserInfo obj1=new UserInfo();
			obj1.setAssetsName("Net Profit");
			obj1.setAssetsAmount(String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(totalIncome-totalExpense))));
			incomeList3.add(obj1);
		}
		else
		{
			for(int i=0;i<assestList.size();i++)
			{
				UserInfo obj = new  UserInfo();
				obj.setAssetsName(assestList.get(i).getLibName());
				obj.setLibName(liablitiesList.get(i).getLibName());
				obj.setLibAmount(String.valueOf(liablitiesList.get(i).getAmount()));
				obj.setAssetsAmount(String.valueOf(assestList.get(i).getAmount()));
				incomeList3.add(obj);
			}
			UserInfo obj1=new UserInfo();
			obj1.setAssetsName("Net Profit");
			obj1.setAssetsAmount(String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(totalIncome-totalExpense))));
			incomeList3.add(obj1);
		}

		for(UserInfo ff:incomeList3)
		{
			if(ff.getAssetsAmount() !=null && !(ff.getAssetsAmount().trim().equals("")))
			{
				assAmount=assAmount+Double.valueOf(ff.getAssetsAmount());
			}
			if(ff.getLibAmount() !=null && !(ff.getLibAmount().trim().equals("")))
			{
				libAmount = libAmount+Double.valueOf(ff.getLibAmount());
			}

		}

		strAssAmount =  String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(assAmount)));
		strLibAmount =  String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(libAmount)));

		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "balanceSheetHead.xhtml";
	}

	public ArrayList<UserInfo> getIncomeList3() {
		return incomeList3;
	}

	public void setIncomeList3(ArrayList<UserInfo> incomeList3) {
		this.incomeList3 = incomeList3;
	}


	public String getStrAssAmount() {
		return strAssAmount;
	}


	public void setStrAssAmount(String strAssAmount) {
		this.strAssAmount = strAssAmount;
	}


	public String getStrLibAmount() {
		return strLibAmount;
	}


	public void setStrLibAmount(String strLibAmount) {
		this.strLibAmount = strLibAmount;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isShw() {
		return shw;
	}

	public void setShw(boolean shw) {
		this.shw = shw;
	}



}
