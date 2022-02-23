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

@ManagedBean(name="trailBalanceHead")
@ViewScoped
public class TrialBalanceHeadBean implements Serializable
{

	ArrayList<UserInfo> allList = new ArrayList<>();
	ArrayList<UserInfo> incomeList = new ArrayList<>();
	ArrayList<UserInfo> expenseList = new ArrayList<>();
	ArrayList<UserInfo> assetsList = new ArrayList<>();
	ArrayList<UserInfo> liablitiesList= new ArrayList<>();
	DataBase dbObj = new DataBase();
	String schId,financialYear,strPrCr,strPrDr;
	double prDr=0.0,prCr=0.0;
	Date startDate,endDate;
	boolean shw=false;
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public TrialBalanceHeadBean()
	{
		startDate=endDate=new Date();

		//	HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//	schId = (String) ss1.getAttribute("schId");
		//	financialYear = (String) ss1.getAttribute("financialYear");
		Connection con = DataBaseConnection.javaConnection();
		schId = obj1.schoolId();
		financialYear = obj1.selectedSessionDetails(schId,con);
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String search()
	{
		prCr=prDr=0.0;
		allList=new ArrayList<>();
		Connection con = DataBaseConnection.javaConnection();
		incomeList= dbObj.incomeDetails("income",schId,financialYear,startDate,endDate,con);
		expenseList= dbObj.incomeDetails("EXPENSES",schId,financialYear,startDate,endDate,con);
		assetsList= dbObj.incomeDetails("ASSETS",schId,financialYear,startDate,endDate,con);
		liablitiesList= dbObj.incomeDetails("Liablities",schId,financialYear,startDate,endDate,con);

		allList.addAll(incomeList);
		allList.addAll(expenseList);
		allList.addAll(assetsList);
		allList.addAll(liablitiesList);

		if(allList.size()>0)
		{
			shw=true;
		}
		else
		{
			shw=false;
		}
		for(UserInfo ff:allList)
		{
			prCr=prCr+Double.valueOf(ff.getCreditStr());
			prDr=prDr+Double.valueOf(ff.getDebitStr());
		}
		strPrCr=String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(prCr)));
		strPrDr=String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(prDr)));

		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "trialBalanceHead.xhtml";
	}
	public ArrayList<UserInfo> getAllList() {
		return allList;
	}

	public void setAllList(ArrayList<UserInfo> allList) {
		this.allList = allList;
	}
	public String getStrPrCr() {
		return strPrCr;
	}
	public void setStrPrCr(String strPrCr) {
		this.strPrCr = strPrCr;
	}
	public String getStrPrDr() {
		return strPrDr;
	}
	public void setStrPrDr(String strPrDr) {
		this.strPrDr = strPrDr;
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

	public boolean isShw() {
		return shw;
	}

	public void setShw(boolean shw) {
		this.shw = shw;
	}

}
