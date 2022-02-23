package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "categoryWiseExpenseReport")
@ViewScoped
public class CategoryWiseExpenseReport implements Serializable {/*
	ArrayList<SelectItem> categoryList;
	Date startDate = new Date(), endDate = new Date();
	ArrayList<IncomeList> expenseList;
	String selectedcategory;
	boolean show = false;
	int cashAmount, chequeAmount;

	public CategoryWiseExpenseReport() {
		Connection conn = DataBaseConnection.javaConnection();

		startDate.setDate(1);
		categoryList = new DatabaseMethods1().allExpenseCategory(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showReport() {

		cashAmount = 0;
		chequeAmount = 0;
		if (selectedcategory.equals("-1")) {
			// expenseList=new DatabaseMethods1().allExpenseList(startDate,endDate);
			if (expenseList.size() > 0) {
				show = true;
				for (IncomeList info : expenseList) {
					if (info.getPaymentMode().equalsIgnoreCase("Cash")) {
						cashAmount += info.getAmount();
					} else {
						chequeAmount += info.getAmount();
					}
				}
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("No Record"));
			}

		} else {
			// expenseList=new
			// DatabaseMethods1().allExpenseList(startDate,endDate,selectedcategory);
			if (expenseList.size() > 0) {
				show = true;
				for (IncomeList info : expenseList) {
					if (info.getPaymentMode().equalsIgnoreCase("Cash")) {
						cashAmount += info.getAmount();
					} else {
						chequeAmount += info.getAmount();
					}
				}
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
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

	public ArrayList<IncomeList> getExpenseList() {
		return expenseList;
	}

	public void setExpenseList(ArrayList<IncomeList> expenseList) {
		this.expenseList = expenseList;
	}

	public String getSelectedcategory() {
		return selectedcategory;
	}

	public void setSelectedcategory(String selectedcategory) {
		this.selectedcategory = selectedcategory;
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
