package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="ExpireSchoolListBean")
@ViewScoped
public class ExpireSchoolListBean implements Serializable
{

	ArrayList<SchoolInfo> dataList;
	public ExpireSchoolListBean() {



		Connection conn=DataBaseConnection.javaConnection();
		dataList=new DatabaseMethods1().allExpireSchoolListChalkbox(conn);
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




}
