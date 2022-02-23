package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;

@ManagedBean(name="editDeleteMsgPack")
@ViewScoped
public class EditDeleteMsgPackBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	ArrayList<MessagePackInfo>packList = new ArrayList<>();
	MessagePackInfo selected;
	Double rate,tax,amount,totalAmount;
	String packName="",quantit;
	public MessagePackInfo getSelected() {
		return selected;
	}
	public void setSelected(MessagePackInfo selected) {
		this.selected = selected;
	}
	public EditDeleteMsgPackBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		packList=new DatabaseMethods1().selectAllMessagePackValues(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void edit()
	{
		packName=selected.getPackName();
		quantit=selected.getQuantity();
		selected.setQuantity(quantit);

		rate= Double.valueOf(selected.getRate());
		selected.setRate(String.valueOf(rate));
		amount=Double.valueOf(quantit)*rate;
		selected.setAmount(String.valueOf(amount));
		tax=amount*0.18;
		selected.setTax(String.valueOf(tax));
		totalAmount=amount+Double.valueOf(tax);
		selected.setTotalAmount(String.valueOf(totalAmount));
		Connection conn = DataBaseConnection.javaConnection();
		int i = new DatabaseMethods1().editAllValues(conn,selected.getId(),selected.getPackName(),quantit,rate,tax,amount,totalAmount);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("edited successfully!"));
			packList=new DatabaseMethods1().selectAllMessagePackValues(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during edition!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = new DatabaseMethods1().deleteSelectedRow(conn,selected.getId());
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("deleted successfully!"));
			packList=new DatabaseMethods1().selectAllMessagePackValues(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during deletion!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<MessagePackInfo> getPackList() {
		return packList;
	}
	public void setPackList(ArrayList<MessagePackInfo> packList) {
		this.packList = packList;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}

