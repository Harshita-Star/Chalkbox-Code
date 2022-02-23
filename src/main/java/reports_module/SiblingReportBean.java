package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.SiblingInfo;
import schooldata.StudentInfo;

@ManagedBean(name="siblingReport")
@ViewScoped
public class SiblingReportBean implements Serializable
{
	ArrayList<SiblingInfo> columnList=new ArrayList<>();
	ArrayList<StudentInfo> stdList;
	DataBaseMethodsReports obj =new DataBaseMethodsReports();

	public SiblingReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		stdList=obj.siblingReport(columnList,conn);
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	public ArrayList<SiblingInfo> getColumnList() {
		return columnList;
	}

	public void setColumnList(ArrayList<SiblingInfo> columnList) {
		this.columnList = columnList;
	}

	public ArrayList<StudentInfo> getStdList() {
		return stdList;
	}

	public void setStdList(ArrayList<StudentInfo> stdList) {
		this.stdList = stdList;
	}
}
