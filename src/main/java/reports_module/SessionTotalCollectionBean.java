package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;

@ManagedBean (name="sessionTotalCollection")
@ViewScoped

public class SessionTotalCollectionBean implements Serializable {

	long totalcollection,amountpaid,amountremianing;
	DataBaseMethodsReports objReport=new DataBaseMethodsReports();

	public SessionTotalCollectionBean ()
	{
		Connection conn=DataBaseConnection.javaConnection();
		

		totalcollection=objReport.gettotalamountinthissession(conn);
		amountpaid=objReport.getamountpaid(conn);
		amountremianing=totalcollection-amountpaid;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public long getTotalcollection() {
		return totalcollection;
	}


	public void setTotalcollection(long totalcollection) {
		this.totalcollection = totalcollection;
	}


	public long getAmountpaid() {
		return amountpaid;
	}


	public void setAmountpaid(long amountpaid) {
		this.amountpaid = amountpaid;
	}


	public long getAmountremianing() {
		return amountremianing;
	}


	public void setAmountremianing(long amountremianing) {
		this.amountremianing = amountremianing;
	}



}
