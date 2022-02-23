package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="recahrgePaymentSuccessfullBean")
@ViewScoped
public class RecahrgePaymentSuccessfullBean implements Serializable {
  
	
	String orderid,amount,quantity;
	
	public RecahrgePaymentSuccessfullBean() {

		
		HttpSession ss= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		MessagePackInfo selected=	(MessagePackInfo) ss.getAttribute("msglist");
		orderid=(String) ss.getAttribute("orderid");
		
		amount=selected.getTotalAmount();
		quantity=selected.getQuantity();
		
	
		
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

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	
	
}
