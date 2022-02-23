package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="paymentStatusupdate")
@ViewScoped
public class PaymentStatusUpdateJsonBean implements Serializable
{
	
	
	String json="";
	public PaymentStatusUpdateJsonBean() {
		Connection conn= DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
			DatabaseMethods1 DBM=new DatabaseMethods1();

			String addmissionNumber=params.get("studentId");
			String schoolid=params.get("schid");
			String orderid=params.get("orderid");
			String paymentId = params.get("paymentId");
			String num="";
			
			if(paymentId==null || paymentId.equals(""))
			{
				paymentId = "";
			}
			
			int number = DBM.feeserailno(schoolid,conn);
			if (String.valueOf(number).length() == 1) {
				num = "0000" + String.valueOf(number);
			} else if (String.valueOf(number).length() == 2) {
				num = "000" + String.valueOf(number);
			} else if (String.valueOf(number).length() == 3) {
				num = "00" + String.valueOf(number);
			} else if (String.valueOf(number).length() == 4) {
				num = "0" + String.valueOf(number);
			} else if (String.valueOf(number).length() == 5) {
				num = String.valueOf(number);
			}

			
			
			
			int check=DBJ.updateFeeStatus(addmissionNumber,schoolid,orderid,num,"ACTIVE",paymentId,conn);
			//SELECT SUM(amount+discount) FROM `student_fee_table` WHERE `paymentMode` LIKE 'paytm' AND `schid` LIKE '302' and neftNo='ORDS2021197115027' and status='active'
			String modArr[]=new String[0];
			int totalAmt = DBJ.feeAmountByOrderid(schoolid,orderid,"ACTIVE",conn);
			DBM.blockStudentAppMods(addmissionNumber,"Fees Block",modArr,schoolid,totalAmt,"","auto",conn);
			
			
			JSONArray arr=new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("status",String.valueOf(check));
			arr.add(obj);

			json=arr.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
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
