package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;

@ManagedBean(name="createMessagePackBean")
@ViewScoped
public class CreateMessagePackBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String messagePackName,messageQuantity,messagePerPrice;
	Double amount,taxAmount,totalAmount;

	public String addMessgaePack()
	{

		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			double amount=Double.valueOf(messageQuantity)*Double.valueOf(messagePerPrice);
			double taxAmount=amount*0.18;
			double totalAmount=taxAmount+amount;
			int i=new DatabaseMethods1().addMessagePack(messagePackName,messageQuantity,messagePerPrice,String.valueOf(amount),String.valueOf(taxAmount),String.valueOf(totalAmount),conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message Pack Create Successfully!"));

				return "createMessagePack.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
				return "";
			}

		}
		finally {

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getMessagePackName() {
		return messagePackName;
	}
	public void setMessagePackName(String messagePackName) {
		this.messagePackName = messagePackName;
	}
	public String getMessageQuantity() {
		return messageQuantity;
	}

	public void setMessageQuantity(String messageQuantity) {
		this.messageQuantity = messageQuantity;
	}

	public String getMessagePerPrice() {
		return messagePerPrice;
	}

	public void setMessagePerPrice(String messagePerPrice) {
		this.messagePerPrice = messagePerPrice;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
