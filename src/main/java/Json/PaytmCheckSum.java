package Json;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.paytm.pg.merchant.CheckSumServiceHelper;

@ManagedBean(name="paytmCheckSum")
@ViewScoped
public class PaytmCheckSum implements Serializable{


	String json="";

	public PaytmCheckSum() {


		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();

		TreeMap<String,String> paramMap = new TreeMap<>();
		paramMap.put("MID" , params.get("MID"));
		paramMap.put("ORDER_ID" , params.get("ORDER_ID"));
		paramMap.put("CUST_ID" , params.get("CUST_ID"));
		paramMap.put("INDUSTRY_TYPE_ID" , params.get("INDUSTRY_TYPE_ID"));
		paramMap.put("CHANNEL_ID" , params.get("CHANNEL_ID"));
		paramMap.put("TXN_AMOUNT" , params.get("TXN_AMOUNT"));
		paramMap.put("WEBSITE" , params.get("WEBSITE"));
		paramMap.put("EMAIL" , params.get("EMAIL"));
		paramMap.put("MOBILE_NO" ,params.get("MOBILE_NO"));
		paramMap.put("CALLBACK_URL" , params.get("CALLBACK_URL"));
		String schid=params.get("MID");
		String marchent_key=params.get("marchent_key");
		try{
			String checkSum="";
			if(marchent_key==null||marchent_key.equals(""))
			{
				if(schid.equals("BLMBlo43887663064832"))
				{
					checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("3_@O9w5vWSb!I%jg", paramMap);
				}
				else if(schid.equals("BLMSrS53643455483506"))
				{
					checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("YaaAReat0ymrB2O4", paramMap);
				}
				else if(schid.equals("Chalkb01137209508671"))
				{
					checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("BJerS#zIxeuP_ZZt", paramMap);
				}
				//				else if(schid.equals("EverGr66980410342438"))
				//				{
				//					 checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("jA_!oZ86h9gFAGwk", paramMap);
				//				}

			}
			else
			{
				checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(marchent_key, paramMap);

			}
			//paramMap.put("CHECKSUMHASH" , checkSum);

			json=checkSum;

		}catch(Exception e) {
			
			e.printStackTrace();
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
