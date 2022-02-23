package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="billPaymentSuccessBean")
@ViewScoped

public class BillPaymentSuccessBean implements Serializable{

String orderid,amount,billNo;
	
	public BillPaymentSuccessBean() {

		
		HttpSession ss= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//ArrayList<BillInfo> selected=	(BillInfo) ss.getAttribute("selectedBill");
		orderid=(String) ss.getAttribute("billOrderid");
		amount=(String)ss.getAttribute("amount");
		
		
		//amount=selected.getAmount();
	//	billNo=selected.getBillNo();
		
	
		
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	
	
}
