package Json;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.JSONObject;

import com.paytm.pg.merchant.CheckSumServiceHelper;

@ManagedBean(name="PaymentStatusCheckingBean")
@ViewScoped
public class PaymentStatusCheckingBean implements Serializable
{
	
	
	String json;
   public PaymentStatusCheckingBean() {
	
	   

		TreeMap<String,String> paramMap = new TreeMap<>();
		paramMap.put("ORDER_ID" ,"ORDS2019151210311");
		paramMap.put("MID" , "BLMSrS53643455483506");
		
	   /**
	   * Generate checksum by parameters we have
	   * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
	   * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys 
	   */
	   String checksum = null;
	try {
		checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("YaaAReat0ymrB2O4", paramMap);
	} catch (Exception e1) {
		
		e1.printStackTrace();
	}

	   /* put generated checksum value here */
	   paramMap.put("CHECKSUMHASH", checksum);

	   /* prepare JSON string for request */
	   JSONObject obj = new JSONObject(paramMap);
	   String post_data = obj.toString();

	   /* for Staging */
	   URL url = null;
	try {
		url = new URL("https://securegw.paytm.in/order/status");
	} catch (MalformedURLException e) {
		
		e.printStackTrace();
	}

	   /* for Production */
	   // URL url = new URL("https://securegw.paytm.in/order/status");

	   try {
	   	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	   	connection.setRequestMethod("POST");
	   	connection.setRequestProperty("Content-Type", "application/json");
	   	connection.setDoOutput(true);

	   	DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
	   	requestWriter.writeBytes(post_data);
	   	requestWriter.close();
	   	String responseData = "";
	   	InputStream is = connection.getInputStream();
	   	BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
	   	if ((responseData = responseReader.readLine()) != null) {
	   		 // System.out.append("Response: " + responseData);
	   	}
	    // System.out.append("Request: " + post_data);
	   	
	   
	   	responseReader.close();
	   	
	   	
	   	json=responseData.toString();
	   } catch (Exception exception) {
	   	exception.printStackTrace();
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
