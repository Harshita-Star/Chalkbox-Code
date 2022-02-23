package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
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
import schooldata.SchoolInfoList;

@ManagedBean(name="enqPaymentDetailJson")
@ViewScoped

public class EnqPaymentDetailJsonBean implements Serializable
{
	String json;
	
	public EnqPaymentDetailJsonBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson dbj = new DataBaseMeathodJson();
			DatabaseMethods1 dbm = new DatabaseMethods1();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String paymentType = params.get("paymentType").toUpperCase();//REGISTRATION, PROSPECTUS
			String schid = params.get("schid");
			String studentName = params.get("studentName");
			String mobno = params.get("fatherMob");
			String enqid = params.get("enqId");
			String orderid = params.get("orderId");
			String amt = params.get("amount");
			String session = "";
			String paymentMode = "PAYTM";
			String remark = "Paid online by student via website.";
			String bankName="",submittedBankName="",chequeNumber=orderid;
			Date chequeDate = new Date();
			
			SchoolInfoList info = dbj.fullSchoolInfo(schid,conn);
			
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month=now.get(Calendar.MONTH)+1;
			if(month>=4)
			{
				session=String.valueOf(year)+"-"+String.valueOf(year+1);
			}
			else
			{
				session=String.valueOf(year-1)+"-"+String.valueOf(year);
			}
			
			int receiptNo=Integer.parseInt(dbm.recepietNoForother(schid,conn))+1;
			int regNo=Integer.parseInt(dbm.prospectusNoForother(schid,paymentType,conn))+1;
			
			if(schid.equals("251") && receiptNo==1)
			{
				receiptNo = 6443;
			}
			else if (schid.equals("252") && receiptNo==1)
			{
				receiptNo = 566;
			}
			
			if(paymentType.equalsIgnoreCase("REGISTRATION"))
			{
				if(schid.equals("251") && regNo==1)
				{
					regNo = 1782;
				}
				else if (schid.equals("252") && regNo==1)
				{
					regNo = 174;
				}
			}
			else if(paymentType.equalsIgnoreCase("PROSPECTUS"))
			{
				if(schid.equals("251") && regNo==1)
				{
					regNo = 1870;
				}
				else if (schid.equals("252") && regNo==1)
				{
					regNo = 175;
				}
			}
			
			int i=dbj.insertenquiryFees(session,enqid,paymentType,amt,receiptNo,paymentMode,remark,conn,String.valueOf(regNo), 
					bankName,submittedBankName,chequeNumber,chequeDate,schid);
			if(i>=1)
			{
				String typemessage = "";
				if(paymentType.equalsIgnoreCase("REGISTRATION"))
				{
					if(info.getBranch_id().equals("22") || info.getBranch_id().equals("27"))
					{
						typemessage="Dear Sir/Madam,\nRegistration of your ward "+studentName+" is confirmed. Please contact school administration for Screening Test related details.\nRegards,\n"+info.getSmsSchoolName();
					}
					else
					{
						typemessage="Dear Sir/Madam,\nReceived payment of Rs." + amt + " in favour of Registration Fee. Please contact school administration for further assistance.\nRegards,\n"+info.getSmsSchoolName();
					}
				}
				else if(paymentType.equalsIgnoreCase("PROSPECTUS"))
				{
					typemessage="Dear Sir/Madam,\nReceived payment of Rs." + amt + " in favour of is Prospectus Fee. Please contact school administration for further assistance.\nRegards,\n"+info.getSmsSchoolName();
				}
				
				if(mobno.length()==10
						&& !mobno.equals("2222222222")
						&& !mobno.equals("9999999999")
						&& !mobno.equals("1111111111")
						&& !mobno.equals("1234567890")
						&& !mobno.equals("0123456789"))
				{
					dbj.messageurlenq(mobno, typemessage, studentName, schid, conn);
				}
				
				obj.put("receiptNo",Integer.toString(regNo) );
				obj.put("registrationNo", Integer.toString(receiptNo));
				obj.put("status", "success");
			}
			else
			{
				obj.put("receiptNo", "");
				obj.put("registrationNo", "");
				obj.put("status", "failed");
			}
			
			
			arr.add(obj);
			json=arr.toJSONString();
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
