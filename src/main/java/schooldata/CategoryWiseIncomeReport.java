package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="categoryWiseIncomeReport")
@ViewScoped
public class CategoryWiseIncomeReport implements Serializable
{/*
	ArrayList<SelectItem>categoryList;
	Date startDate=new Date(),endDate=new Date();
	ArrayList<IncomeList> incomeList;
	String selectedcategory;
	boolean show=false;
	int cashAmount,chequeAmount;

	public CategoryWiseIncomeReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		startDate.setDate(1);
		categoryList=new DatabaseMethods1().allIncomeCategory(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showReport()
	{

	cashAmount=0;chequeAmount=0;
	if (selectedcategory.equals("-1"))
	{
	//incomeList=new DatabaseMethods1().allIncomeCategoryReport(startDate,endDate);
	 if(incomeList.size()>0)
	 {
		 show=true;
		 for(IncomeList info:incomeList)
		 {
			 if(info.getPaymentMode().equalsIgnoreCase("Cash"))
			 {
					cashAmount+=info.getAmount();
			 }
			 else
			 {
			    	chequeAmount+=info.getAmount();
			 }
		 }
	 }
	 else
	 {
		 FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("No Record"));
	 }
	}
	else
	{
		//incomeList=new DatabaseMethods1().allIncomeCategoryReport(startDate,endDate,selectedcategory);
		 if(incomeList.size()>0)
		 {
			 show=true;
			 for(IncomeList info:incomeList)
			 {
				 if(info.getPaymentMode().equalsIgnoreCase("Cash"))
				 {
						cashAmount+=info.getAmount();
				 }
				 else
				 {
				    	chequeAmount+=info.getAmount();
				 }
			 }
		 }
		 else
		 {
			 FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("No Data"));
		 }
	}
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
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
	public String getSelectedcategory() {
		return selectedcategory;
	}
	public void setSelectedcategory(String selectedcategory) {
		this.selectedcategory = selectedcategory;
	}

	public ArrayList<IncomeList> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(ArrayList<IncomeList> incomeList) {
		this.incomeList = incomeList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public int getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(int cashAmount) {
		this.cashAmount = cashAmount;
	}

	public int getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(int chequeAmount) {
		this.chequeAmount = chequeAmount;
	}





 */}
