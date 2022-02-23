package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name="allSchoolSMSReport")
@ViewScoped


public class AllSchoolSMSReportBean implements Serializable
{
	ArrayList<SchoolInfo> dataList;
	ArrayList<SelectItem> sessionList;
	String session;

	public AllSchoolSMSReportBean()
	{

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

		sessionList=new ArrayList<>();
		for(int i=2016;i<=year;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		if(month>=4)
		{
			session=String.valueOf(year)+"-"+String.valueOf(year+1);
		}
		else
		{
			session=String.valueOf(year-1)+"-"+String.valueOf(year);
		}

		show();
	}

	public void show()
	{
		Connection conn=DataBaseConnection.javaConnection();

		dataList=new DatabaseMethods1().allSchoolSMSChalkbox(session,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SchoolInfo> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<SchoolInfo> dataList) {
		this.dataList = dataList;
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
}
