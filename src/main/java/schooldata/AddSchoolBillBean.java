package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="addSchoolBill")
@ViewScoped

public class AddSchoolBillBean implements Serializable
{
	ArrayList<SelectItem> schoolList;
	String schNm,billNo,amount;
	Date billDate=new Date();
	Date dueDate = new Date();
	Date limit = new Date();
	transient UploadedFile file;

	public AddSchoolBillBean()
	{
		limit.setDate(limit.getDate()+1);
		dueDate.setDate(dueDate.getDate()+1);

		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String add()
	{
		
		if(Integer.valueOf(amount)<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Amount must be greater than zero!"));
			return "";
		}
		
		String dt = new SimpleDateFormat("yyyy-MM-dd").format(billDate);
		String dueDt = new SimpleDateFormat("yyyy-MM-dd").format(dueDate);
		String name = "";
		Connection conn = DataBaseConnection.javaConnection();
		String dtt = new SimpleDateFormat("yMdhms").format(new Date());

		if(file.getFileName().endsWith("pdf"))
		{
			if (file != null && file.getSize() > 0) {
				String filePath1 = file.getFileName();
				String exten[] = filePath1.split("\\.");
				name = schNm + "_" + "bill_pdf" + "_" + dtt + "_" + dtt + "_5" + "." + exten[exten.length-1];
				new DatabaseMethods1().makeProfileSchid(schNm,file, name, conn);
			}

			int i = new DatabaseMethods1().addSchoolBill(schNm,billNo,dt,dueDt,name,amount,conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bill Added!"));
				billNo = schNm = "";
				billDate=new Date();
				dueDate = new Date();
				file = null;

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!"));

			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please choose only PDF file"));
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public String getSchNm() {
		return schNm;
	}

	public void setSchNm(String schNm) {
		this.schNm = schNm;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public Date getLimit() {
		return limit;
	}

	public void setLimit(Date limit) {
		this.limit = limit;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}


}
