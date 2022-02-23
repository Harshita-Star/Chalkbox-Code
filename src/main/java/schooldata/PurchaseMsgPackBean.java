package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="purchaseMsgPack")
@ViewScoped
public class PurchaseMsgPackBean implements Serializable{
	ArrayList<MessagePackInfo>packList = new ArrayList<>();
	MessagePackInfo selected;
	Double quantit,rate,tax,amount,totalAmount;
	String packName="",contactNo;
	public MessagePackInfo getSelected() {
		return selected;
	}
	public void setSelected(MessagePackInfo selected) {
		this.selected = selected;
	}
	SchoolInfoList ls;
	public PurchaseMsgPackBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		 ls=new DatabaseMethods1().fullSchoolInfo(conn);
		packList=new DatabaseMethods1().selectAllMessagePackValues(conn);
		contactNo=new DatabaseMethods1().contactNo("SMS Recharge Request",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void purchase()
	{
		

		HttpSession ss= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("msglist", selected);
		String custid=ls.getSchoolName()+"-"+ls.getSchid();
		String ord="SMSORDS"+new SimpleDateFormat("ydMhms").format(new Date());
		ss.setAttribute("orderid", ord);
		
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("http://www.chalkboxerp.in/DM/pgRedirectMessage.jsp?ORDER_ID="+ord+"&CUST_ID="+custid+"&INDUSTRY_TYPE_ID=PrivateEducation&CHANNEL_ID=WEB&TXN_AMOUNT="+selected.getTotalAmount());
	//	FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8080/Chalkbox/pgRedirectMessage.jsp?ORDER_ID="+ord+"&CUST_ID="+custid+"&INDUSTRY_TYPE_ID=PrivateEducation&CHANNEL_ID=WEB&TXN_AMOUNT="+selected.getTotalAmount());
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		/*
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username=(String) ss.getAttribute("username");
		int j = new DatabaseMethods1().checkChhotuRecharge(conn,0,new DatabaseMethods1().schoolId(),"approved");
		if(j==1)
		{
			int i = new DatabaseMethods1().updatePurchaseTable(conn,selected,0,new DatabaseMethods1().schoolId(),"approved");
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Purchase Request Sent successfully!"));
				String schName = new DatabaseMethods1().getSMSSchoolName(new DatabaseMethods1().schoolId(), conn);
				String typemessage="Hello Admin,\nA message recharge request of "+selected.getQuantity()+" messages has been received from - "+schName+".\nRegards.\nCB";

				new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage,"masteradmin",conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during Purchase!"));

			}
		}
		else
		{
			int i = new DatabaseMethods1().purchaseAllValuesInTable(conn,selected,new DatabaseMethods1().schoolId(),"pending",username);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Purchase Request Sent successfully!"));
				//new DatabaseMethods1().addMessages(new DatabaseMethods1().schoolId(), new Date(), Integer.valueOf(selected.getQuantity()), conn);
				//packList=new DatabaseMethods1().selectAllMessagePackValues(conn);
				String schName = new DatabaseMethods1().getSMSSchoolName(new DatabaseMethods1().schoolId(), conn);
				String typemessage="Hello Admin,\nA message recharge request of "+selected.getQuantity()+" messages has been received from - "+schName+".\nRegards.\nCB";

				new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage,"masteradmin",conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during Purchase!"));
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	*/}

	/*public void purchasee()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = new DatabaseMethods1().purchaseAllValuesInTable(conn,selected);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Purchase Request Sent successfully!"));
			String schName = new DatabaseMethods1().schoolNameById(new DatabaseMethods1().schoolId(), conn);
			String typemessage="Hello Admin,\nA message recharge request of "+selected.getQuantity()+" messages has been received from - "+schName+".Please do it on priority.\nRegards.\nTeam Chalkbox.";

	    		new DatabaseMethods1().messageurlMasterAdmin("8058100200", typemessage,"masteradmin",conn);
			//new DatabaseMethods1().addMessages(new DatabaseMethods1().schoolId(), new Date(), Integer.valueOf(selected.getQuantity()), conn);
		//packList=new DatabaseMethods1().selectAllMessagePackValues(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during Purchase!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public ArrayList<MessagePackInfo> getPackList() {
		return packList;
	}
	public void setPackList(ArrayList<MessagePackInfo> packList) {
		this.packList = packList;
	}
}

