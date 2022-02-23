package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "sendMessageToSchool")
@ViewScoped
public class SendMessageToSchool implements Serializable {

	String phoneNo;
	String addNumber;
	String message;

	public SendMessageToSchool() {

		Connection conn=DataBaseConnection.javaConnection();

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		final String schidd = (String) ss.getAttribute("schoolid");

		phoneNo = "";
		addNumber = "";

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		Runnable r = new Runnable() {
			public void run() {

				ArrayList<StudentInfo> info = new DatabaseMethods1(schidd).allStudentListForMessage(conn);
				for (StudentInfo ls : info) {
					if (phoneNo.equals("")) {
						phoneNo = String.valueOf(ls.getFathersPhone());
						addNumber = ls.getAddNumber();
					} else {
						phoneNo = phoneNo + "," + String.valueOf(ls.getFathersPhone());
						addNumber = ls.getAddNumber();

					}

				}

			}
		};

		new Thread(r).start();

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
