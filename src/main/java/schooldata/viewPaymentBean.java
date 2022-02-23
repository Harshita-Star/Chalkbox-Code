package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="viewPayment")
@ViewScoped
public class viewPaymentBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<PaymentInfo> dataList;
	PaymentInfo selectedRow;


	public viewPaymentBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		dataList=new DatabaseMethods1().allPaymentList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deletePayment()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.deletePayment(selectedRow.getId(),conn);
		if(i==1)
		{
			dataList=obj.allPaymentList(conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Payment Deleted Sucessfully"));
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

	public ArrayList<PaymentInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<PaymentInfo> dataList) {
		this.dataList = dataList;
	}
	public PaymentInfo getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(PaymentInfo selectedRow) {
		this.selectedRow = selectedRow;
	}
}
