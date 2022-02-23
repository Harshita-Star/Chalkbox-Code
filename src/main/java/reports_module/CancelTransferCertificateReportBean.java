package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.TCInfo;
import tc_module.DataBaseMethodsTcModule;

@ManagedBean(name="cancelTc")
@ViewScoped
public class CancelTransferCertificateReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<TCInfo> allStudentTCList;
	DataBaseMethodsTcModule objTc=new DataBaseMethodsTcModule();
	String schid,session;
	DatabaseMethods1 obj=new DatabaseMethods1();

	
	public CancelTransferCertificateReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		allStudentTCList=objTc.allCancelledTcList(schid,session,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<TCInfo> getAllStudentTCList() {
		return allStudentTCList;
	}
	public void setAllStudentTCList(ArrayList<TCInfo> allStudentTCList) {
		this.allStudentTCList = allStudentTCList;
	}
}
