package schooldata;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;

public class BillPayment {

	public void values(String userid,String schoolId,String orderid) 
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		Date date = new Date();
		String currDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
		ArrayList<BillInfo> list = new DataBaseMethodsReports().myBills("unpaid",schoolId,conn);
		SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(schoolId, conn);
        double amount=0;		
        for(BillInfo selected:list)
		{
        	amount+=Double.parseDouble(selected.getAmount());
			new DataBaseMethodsReports().paySchoolBill(userid+"/PAYTM",userid,selected.getId(),orderid,currDate,conn);
	
		}
		
		String typemessage="Hello Admin,\n"
				+ls.getSchoolName()+ " has paid Rs. "+amount+" in favour of the raised bills. \nRegards.\nCB";
		String tempcontactNo=new DatabaseMethods1().contactNo("Add School",conn);
		new DatabaseMethods1().messageurlMasterAdmin(tempcontactNo, typemessage/*,"masteradmin",conn*/);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
	}
	
}
