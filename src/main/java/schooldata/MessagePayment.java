package schooldata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import Json.DataBaseMeathodJson;

public class MessagePayment {

	
	
	public void values(MessagePackInfo selected,String userid,String schoolId) 
	{
		
		String check="success";
		Connection conn=DataBaseConnection.javaConnection();
		int j = new DatabaseMethods1().checkChhotuRecharge(conn,0,schoolId,"approved");
		if(j==1)
		{
			int i = new DatabaseMethods1().updatePurchaseTableMaster(conn,selected,0,schoolId,"approved",userid);
			if(i==1)
			{
				
				new DatabaseMethods1().updateChhotuRechargeInMessageTransaction(conn,selected.getQuantity(),schoolId);
				check="success";
			}
			else
			{
			 check="error";
			}
		}
		else
		{
			int i = new DatabaseMethods1().purchaseAllValuesInTable(conn,selected,schoolId,"approved",userid,"paid");
			if(i>=1)
			{
				
				new DatabaseMethods1().addMessages(schoolId, new Date(), Integer.valueOf(selected.getQuantity()), conn);
				check="success";
			}
			else
			{
				check="error";
			}
		}	
		
		
		if(check.equals("success"))
		{
			SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(schoolId, conn);
		 	String typemessage="Hello Admin,\n"
					+ls.getSchoolName()+ " has paid Rs. "+selected.getTotalAmount()+" for "+selected.getQuantity()+ " Messages. \nRegards.\nCB";
			String tempcontactNo=new DatabaseMethods1().contactNo("Add School",conn);
			new DatabaseMethods1().messageurlMasterAdmin(tempcontactNo, typemessage/*,"masteradmin",conn*/);
		
		}
	
		

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
	}

	
	
	
}
