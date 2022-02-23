package schooldata;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;

import com.razorpay.Payment;
import com.razorpay.RazorpayClient;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import student_module.PaytmConstants;

public class TestSMS {

	public static void main(String[] args) {

//		String[] name = {"amit"};
//		String newArr[] = ArrayUtils.removeElement(name, "amit");
//		String newAdm = String.join(",", name);
	
//		String txt = String.valueOf(1711);
//		 // System.out.println("Start : "+txt);
//		String key = "key phrase used for XOR-ing";
//		txt = xorMessage(txt, key);
//		 // System.out.println("XOR : "+txt);
//		String encoded = base64encode(txt);
//		 // System.out.println("Encode : "+encoded);
//		String decoded = base64decode(encoded);
//		 // System.out.println("Decode : "+decoded);
//		String orginal = xorMessage(decoded, key);
//		 // System.out.println("End : "+orginal);
		
//		String resp = new TestSMS().getAccessToken();
//		 // System.out.println("Access Token : "+resp);
		
		//new TestSMS().newAccessToken();
//		String session= "2021-2022";
//		ArrayList<String> scharr = new ArrayList<String>();
//		try {
//			Connection conn = DataBaseConnection.javaConnection();
//			
//			Statement stt = conn.createStatement();
//			String qqq = "SELECT * FROM transport_waive WHERE session='"+session+"' GROUP By schid";
//			ResultSet rr = stt.executeQuery(qqq);
//			while(rr.next())
//			{
//				scharr.add(rr.getString("schid"));
//			}
//			
//			for(String schid : scharr)
//			{
//				 // System.out.println("SCHID : "+schid+" **************************************");
//				ArrayList<Transport> rlist = new DataBaseMethodsReports().transportWaivedList(session,schid,conn);
//				ArrayList<SelectItem> classlist = new DatabaseMethods1().allClassSessSchid(schid, session, conn);
//				for(SelectItem ss : classlist)
//				{
//					 // System.out.println("classid : "+ss.getValue());
//					for(Transport tt : rlist)
//					{
//						String qq = "insert into transport_waive(month,status,session,schid,classid) values(?,?,?,?,?)";
//						PreparedStatement st = conn.prepareStatement(qq);
//						
//						st.setString(1, tt.getMonth());
//						st.setString(2, tt.getStatus());
//						st.setString(3, session);
//						st.setString(4, schid);
//						st.setString(5, String.valueOf(ss.getValue()));
//						
//						st.execute();
//					}
//				}
//			}
//			
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	
//		String test = "SSBIPS" + "\n" + "- CB";
//		String print = test.split("\\n")[0];
//		System.out.println(print);
		
//		String exec = StringEscapeUtils.unescapeXml("test &#x2F; test");
//		System.out.println(exec);
		
		try {
			RazorpayClient razorpay = new RazorpayClient(PaytmConstants.MERCHANT_KEY, PaytmConstants.KEY_SECRET);
        	org.json.JSONObject captureRequest = new org.json.JSONObject();
        	captureRequest.put("amount", "100");
        	captureRequest.put("currency", "INR");
        	Payment txnDet = razorpay.Payments.fetch("pay_IqjyfXj3xqBJfM");
        	
        	System.out.println("Txn Status : "+txnDet.get("status"));
        	
        	Payment payment = razorpay.Payments.capture("pay_IqjyfXj3xqBJfM", captureRequest);
        	
        	System.out.println("Capture status : "+payment.get("status"));
        	
        	Payment newTxnDet = razorpay.Payments.fetch("pay_IqjyfXj3xqBJfM");
        	
        	System.out.println("New Txn Status : "+newTxnDet.get("status"));
        	
//        	List<Payment> payments = razorpay.Orders.fetchPayments("order_IlyERcbahtJp8f");
//        	System.out.println("New Txn Status : "+payments.get(0).get("status"));
		} catch (Exception e) {
			System.out.println("Error occured");
			System.out.println(e.getMessage().split(":")[0]);
			System.out.println(e.getMessage().split(":")[1]);
		}
		
	}

	public void calcDateTimeDiff()
	{
		try 
		{
			SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss");
			String start_date = "02-09-2021 09:45:00" , end_date = "06-08-2021 14:30:00";
			Date d1 = sdf.parse(start_date);
			//Date d2 = sdf.parse(end_date);
			Date d2 = new Date(); 
			long difference_In_Time = d2.getTime() - d1.getTime();
//			long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
//			long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
			
			//only in minutes
			
			long difference_In_Minutes = (difference_In_Time / (1000));
			
			// // System.out.println("Hour : "+difference_In_Hours);
			 // System.out.println("Minute : "+difference_In_Minutes);
			 // System.out.println("Time : "+difference_In_Time);
			
//			String strStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d1).replace(" ", "T");
//			TreeMap<String, Object> paramMap = new TreeMap<>();
//			paramMap.put("topic" ,"ios");
//			paramMap.put("type" ,"2");
//			paramMap.put("start_time" ,strStartDate);
//			paramMap.put("duration", 40);
//			paramMap.put("password" ,"123456");
//			
//			JSONObject obj = new JSONObject(paramMap);
//			 // System.out.println(obj.toString());
//			 // System.out.println(strStartDate);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final String DEFAULT_ENCODING = "UTF-8";
//	static BASE64Encoder enc = new BASE64Encoder();
//	static BASE64Decoder dec = new BASE64Decoder();

	public static String base64encode(String text) {
		try {
			return Base64.getEncoder().encodeToString(text.getBytes(DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}// base64encode

	public static String base64decode(String text) {
		try {
			return new String(Base64.getDecoder().decode(text), DEFAULT_ENCODING);
		} catch (IOException e) {
			return null;
		}
	}// base64decode

	public static String xorMessage(String message, String key) {
		try {
			if (message == null || key == null)
				return null;

			char[] keys = key.toCharArray();
			char[] mesg = message.toCharArray();

			int ml = mesg.length;
			int kl = keys.length;
			char[] newmsg = new char[ml];

			for (int i = 0; i < ml; i++) {
				newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
			}

			return new String(newmsg);
		} catch (Exception e) {
			return null;
		}
	}// xorMessage
	
	public void newAccessToken() {
		try {
			String authorization="SBdW5YqDR6imu6bgZVJog:pJK9AHvtB67dy1xllygsbtRKgbBbb4mB";
	        String encodedAuth="Basic "+Base64.getEncoder().encodeToString(authorization.getBytes());
	         // System.out.println(encodedAuth);
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				MediaType mediaType = MediaType.parse("text/plain");
				RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				  .addFormDataPart("grant_type","authorization_code")
				  .addFormDataPart("redirect_uri","https://www.chalkbox.in/")
				  .addFormDataPart("code","ptp9FFLiLf__9XDfpvDQkmDk6TvN6XAEg")
				  .build();
				Request request = new Request.Builder()
				  .url("https://zoom.us/oauth/token")
				  .method("POST", body)
				  .addHeader("Authorization", encodedAuth)
				  .build();
				
					Response response = client.newCall(request).execute();
					ResponseBody respBody = response.body();
					JSONObject obj = new JSONObject(respBody.string());
					String accessToken = obj.getString("access_token");
					String refreshToken = obj.getString("refresh_token");
					 // System.out.println("Access Token : "+accessToken);
					 // System.out.println("Refresh Token : "+refreshToken);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
	}
	
	public String getAccessToken()
	{
		String resp = "";
		String authorization="SBdW5YqDR6imu6bgZVJog:pJK9AHvtB67dy1xllygsbtRKgbBbb4mB";
        String encodedAuth="Basic "+Base64.getEncoder().encodeToString(authorization.getBytes());
         // System.out.println(encodedAuth);
//		TreeMap<String,String> headMap = new TreeMap<>();
//		headMap.put("Authorization" ,encodedAuth);
		
		TreeMap<String,String> paramMap = new TreeMap<>();
		paramMap.put("grant_type" ,"authorization_code");
		paramMap.put("code" ,"iAgPQXh2Iw__9XDfpvDQkmDk6TvN6XAEg");
		paramMap.put("redirect_uri" ,"https://www.chalkbox.in/");
		
		JSONObject paramObj = new JSONObject(paramMap);
		//JSONObject headObj = new JSONObject(headMap);
		
		String post_data = paramObj.toString();
		
		URL url = null;
		try {
			url = new URL("https://zoom.us/oauth/token");

		   	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		   	connection.setRequestMethod("POST");
		   	connection.setRequestProperty("Content-Type", "multipart/form-data");
		   	connection.setRequestProperty("Authorization", encodedAuth);
		   	connection.setDoOutput(true);

		   	DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
		   	requestWriter.writeBytes(post_data);
		   	requestWriter.close();
		   	 // System.out.println(post_data);
		   	String responseData = "";
		   	InputStream is = connection.getInputStream();
		   	BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
		   	if ((responseData = responseReader.readLine()) != null) {
		   		
		   		//String json="["+responseData+"]";
		   		resp = responseData;
		   	}
		   	
		   
		   	responseReader.close();
		   	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resp;
	}
	
	public String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

	public String sendRequest(String url) {
        String result = "";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpParams httpParameters = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            InputStream ips = response.getEntity().getContent();
            BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String s;
            while (true) {
                s = buf.readLine();
                if (s == null || s.length() == 0)
                    break;
                sb.append(s);
            }
            buf.close();
            ips.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public void parseFromJSONResponse(String respo)
    {
        JSONArray myjson;
        JSONObject obj;
        ArrayList<FeeInfo> list = new ArrayList<>();
        try
        {
        //// // System.out.println(respo);
            myjson = new JSONArray(respo);
            if(myjson.length()>0)
            {
            		//// // System.out.println(myjson);
            		for(int i=0;i<myjson.length();i++)
            		{
            			obj = myjson.getJSONObject(i);
            			FeeInfo ff = new FeeInfo();
            			ff.setSno(i+1);
            			ff.setFeeMonth(obj.get("feemonth").toString());
            			ff.setMonthId(obj.get("month").toString());
            			ff.setAmount(Double.valueOf(obj.get("feeLeftAmount").toString()));
            			ff.setLateFee(Double.valueOf(obj.get("totalPaid").toString()));
            			ff.setTotalFee(Double.valueOf(obj.get("totalFee").toString()));

            			list.add(ff);
            		}
            		
            		
            		for(FeeInfo fi : list)
            		{
            			//// // System.out.println("Fee Month : "+fi.getFeeMonth());
            			//// // System.out.println("Month Id: "+fi.getMonthId());
            			//// // System.out.println("Total Fee : "+fi.getTotalFee());
            			//// // System.out.println("Total Paid : "+fi.getLateFee());
            			//// // System.out.println("Total Due : "+fi.getAmount());
            		}


            }
            else
            {
            		//// // System.out.println("No Result");
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
