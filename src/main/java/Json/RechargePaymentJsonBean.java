package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.MessagePackInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="rechargePaymentJsonBean")
@ViewScoped

public class RechargePaymentJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public RechargePaymentJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schoolId = params.get("schid");
			String userid = params.get("userid");

			String id = params.get("id");
			String quantity = params.get("quantity");
			String packName = params.get("packName");
			String rate = params.get("rate");
			String amount = params.get("amount");
			String tax = params.get("tax");
			String totamount = params.get("totalamount");

			String status = "error";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				MessagePackInfo selected = new MessagePackInfo();

				selected.setQuantity(quantity);
				selected.setPackName(packName);
				selected.setRate(rate);
				selected.setAmount(amount);
				selected.setTax(tax);
				selected.setTotalAmount(totamount);
				selected.setId(id);

				
				int j = DBM.checkChhotuRecharge(conn,0,schoolId,"approved");
				if(j==1)
				{
					int i = DBM.updatePurchaseTableMaster(conn,selected,0,schoolId,"approved",userid);
					if(i==1)
					{
						status = "success";
						DBM.updateChhotuRechargeInMessageTransaction(conn,quantity,schoolId);
					}
					else
					{
						status = "error";
					}
				}
				else
				{
					int i = DBM.purchaseAllValuesInTable(conn,selected,schoolId,"approved",userid,"paid");
					if(i>=1)
					{
						status = "success";
						DBM.addMessages(schoolId, new Date(), Integer.valueOf(quantity), conn);
					}
					else
					{
						status = "error";
					}
				}
			}
			
			if(status.equals("success"))
			{
				SchoolInfoList ls=DBJ.fullSchoolInfo(schoolId, conn);
		        
				String typemessage="Hello Admin,\n"
						+ls.getSchoolName()+ " has paid Rs. "+amount+" for "+quantity+ " Messages. \nRegards.\nCB";
				String tempcontactNo=DBM.contactNo("Add School",conn);
				DBM.messageurlMasterAdmin(tempcontactNo, typemessage/*,"masteradmin",conn*/);
				
			}
			
			
			

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			mainobj.put("status", status);
			arr.add(mainobj);

			json = arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
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
