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

@ManagedBean(name="addMessage")
@ViewScoped
public class AddMessage implements Serializable{
	ArrayList<SelectItem>schoolList;
	int mszQty;
	String schNm;
	Date date=new Date();

	public AddMessage()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void message()
	{
		int i;
		Connection conn = DataBaseConnection.javaConnection();

		if(mszQty>0)
		{
			i=new DatabaseMethods1().addMessages(schNm,date,mszQty,conn);

			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message Added Sucessfully"));
				schNm="";
				mszQty=0;

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message Quantity Greater Than Zero"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public int getMszQty() {
		return mszQty;
	}
	public void setMszQty(int mszQty) {
		this.mszQty = mszQty;
	}
	public String getSchNm() {
		return schNm;
	}
	public void setSchNm(String schNm) {
		this.schNm = schNm;
	}
	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}



}
