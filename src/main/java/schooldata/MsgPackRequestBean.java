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

@ManagedBean(name="msgPackRequest")
@ViewScoped
public class MsgPackRequestBean implements Serializable{
	ArrayList<MessagePackInfo>packList = new ArrayList<>();
	MessagePackInfo selected;

	public MsgPackRequestBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		packList=new DatabaseMethods1().selectAllMsgPackRequest("pending",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void approved()
	{
		Connection conn = DataBaseConnection.javaConnection();

		int i=new DatabaseMethods1().updateMsgPackRequestInStatus(selected.getId(),"approved",conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request approved successfully!"));
			int k = new DatabaseMethods1().checkChhotuRechargeInMessageTransaction(conn,selected.getSchoolId());
			if(k==1)
			{
				int j = new DatabaseMethods1().updateChhotuRechargeInMessageTransaction(conn,selected.getQuantity(),selected.getSchoolId());
				if(j==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated successfully!"));
				}
			}
			else
			{
				new DatabaseMethods1().addMessages(selected.getSchoolId(), new Date(), Integer.valueOf(selected.getQuantity()), conn);
				packList=new DatabaseMethods1().selectAllMsgPackRequest("pending",conn);
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during approved!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	/*
	public void approved()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().updateMsgPackRequestInStatus(selected.getId(),"approved",conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request approved successfully!"));
			new DatabaseMethods1().addMessages(selected.getSchoolId(), new Date(), Integer.valueOf(selected.getQuantity()), conn);
			packList=new DatabaseMethods1().selectAllMsgPackRequest("pending",conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during approved!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}*/

	public void denied()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().updateMsgPackRequestInStatus(selected.getId(),"denied",conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request denied successfully!"));
			packList=new DatabaseMethods1().selectAllMsgPackRequest("pending",conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during denied!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public MessagePackInfo getSelected() {
		return selected;
	}
	public void setSelected(MessagePackInfo selected) {
		this.selected = selected;
	}

	public ArrayList<MessagePackInfo> getPackList() {
		return packList;
	}


	public void setPackList(ArrayList<MessagePackInfo> packList) {
		this.packList = packList;
	}

}