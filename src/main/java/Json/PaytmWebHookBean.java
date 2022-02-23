package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="paytmWebHook")
@ViewScoped
public class PaytmWebHookBean implements Serializable{
	
	String json="";
	
	public PaytmWebHookBean() 
	{
		
		Connection conn = DataBaseConnection.javaConnection();
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();
		
	
	  String custmerId=params.get("CUSTID");
	  String orderID=params.get("ORDERID");
	  String status=params.get("STATUS");
	  
	  
	  Map<String, String> map= new DataBaseMeathodJson().selectedFeeListByOrderId(custmerId, orderID,conn);
	  String schId=map.get("schId");
	  
	  if(!schId.equals(""))
	  {
		  String txnstatus="";
		  String num="";
		  if(status.equalsIgnoreCase("TXN_SUCCESS"))
		  {
			  int number = new DatabaseMethods1().feeserailno(schId,conn);
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
				txnstatus="ACTIVE";
			}
			else
			{
				txnstatus=status;
			}

		  int check=new DataBaseMeathodJson().updateFeeStatus(custmerId,schId,orderID,num,status,conn);
			  
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
