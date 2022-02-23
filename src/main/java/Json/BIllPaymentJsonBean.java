package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.DataBaseMethodsReports;
import schooldata.BillInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="billPaymentJson")
@ViewScoped
public class BIllPaymentJsonBean implements Serializable
{
	
	
	String json;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DataBaseMethodsReports DBR=  new DataBaseMethodsReports();
   public BIllPaymentJsonBean() {
	   Connection conn=DataBaseConnection.javaConnection();
	   try
	   {
		   Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String userid = params.get("userId");
			String schoolid = params.get("schoolid");
			String orderId = params.get("orderId");
			JSONArray arr=new JSONArray();
			
		  
			Date date = new Date();
			String currDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
			ArrayList<BillInfo> list = DBR.myBills("unpaid",schoolid,conn);
			SchoolInfoList ls=DBJ.fullSchoolInfo(schoolid, conn);
	        double amount=0;		
	        for(BillInfo selected:list)
			{
	        	amount+=Double.parseDouble(selected.getAmount());
				DBR.paySchoolBill(userid+"/PAYTM",userid,selected.getId(),orderId,currDate,conn);
		
			}
			
			String typemessage="Hello Admin,\n"
					+ls.getSchoolName()+ " has paid Rs. "+amount+" in favour of the raised bills. \nRegards.\nCB";
			String tempcontactNo=DBM.contactNo("Add School",conn);
			DBM.messageurlMasterAdmin(tempcontactNo, typemessage/*,"masteradmin",conn*/);
			JSONObject obj = new JSONObject();
			obj.put("msg", "Bill Payment Successfull");
			arr.add(obj);
		      
			json=arr.toJSONString();
	   } 
	   catch (Exception e) {}
	   finally 
	   {
		   try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	   }
}
   
   public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();

	}



   
}
