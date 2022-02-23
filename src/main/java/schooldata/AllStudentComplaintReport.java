package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="allStudentComplaintReport")
@ViewScoped
public class AllStudentComplaintReport implements Serializable
{
	ArrayList<HomeWorkInfo>list=new ArrayList<>();
	public AllStudentComplaintReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().allStudentComplaintReport(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<HomeWorkInfo> getList() {
		return list;
	}
	public void setList(ArrayList<HomeWorkInfo> list) {
		this.list = list;
	}


}
