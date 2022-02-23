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

import org.primefaces.PrimeFaces;

@ManagedBean(name="addReceipt")
@ViewScoped
public class AddReceiptBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String description,school;
	Date addDate=new Date();
	double amount;
	ArrayList<SelectItem>schoolList;

	public void viewEditPayment()
	{
		PrimeFaces.current().executeInitScript("window.open('viewDeletePayment.xhtml')");
	}
	public AddReceiptBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addReceipt()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().addPayment(school, description, addDate, amount,"credit",conn);
		if(i==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Payment Added Sucessfully"));
			addDate=new Date();school=description="";amount=0;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}
	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}


}
