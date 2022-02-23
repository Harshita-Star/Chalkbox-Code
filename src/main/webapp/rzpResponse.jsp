<%@page import="com.razorpay.RazorpayException"%>
<%@page import="com.razorpay.Utils"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="schooldata.SchoolInfoList"%>
<%@page import="schooldata.DataBaseConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="Json.DataBaseMeathodJson"%>
<%@page import="schooldata.FeeInfo"%>
<%@page import="student_module.test"%>
<%@page import="student_module.PaytmConstants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,com.paytm.pg.merchant.CheckSumServiceHelper"%>
<%
Enumeration<String> paramNames = request.getParameterNames();

Map<String, String[]> mapData = request.getParameterMap();
TreeMap<String,String> parameters = new TreeMap<String,String>();
String paymentId =  "";
String orderId = "";
String signature = "";
JSONObject options = new JSONObject();

HttpSession tt=request.getSession(false);
ArrayList<FeeInfo> selectedList=(ArrayList<FeeInfo>)tt.getAttribute("onlist");
String addmissionNumber=(String) tt.getAttribute("username");
String schoolid = (String) tt.getAttribute("schoolid");
String session1 = (String) tt.getAttribute("selectedSession");

System.out.println(session1);

/* while(paramNames.hasMoreElements()) {
	String paramName = (String)paramNames.nextElement();
 */	
	paymentId = mapData.get("razorpay_payment_id")[0];
	orderId = mapData.get("razorpay_order_id")[0];
	signature = mapData.get("razorpay_signature")[0];
	System.out.println("payment id : "+paymentId);
	System.out.println("order id : "+orderId);
	System.out.println("signature : "+signature);
//}
/* boolean isValideChecksum = false;
String checkSum="";
 */String outputHTML="";
try{
	
		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList list=new DataBaseMeathodJson().fullSchoolInfo(schoolid, con);
		//System.out.println("----------"+list.getPaytm_marchent_key());
	
	
		if (StringUtils.isNotBlank(paymentId) && StringUtils.isNotBlank(signature)
		        && StringUtils.isNotBlank(orderId)) {
		      try {
		        options.put("razorpay_payment_id", paymentId);
		        options.put("razorpay_order_id", orderId);
		        options.put("razorpay_signature", signature);
		        boolean isEqual = Utils.verifyPaymentSignature(options, PaytmConstants.KEY_SECRET);

		        if (isEqual) {
		        	System.out.println("Payment Success");
		        }
		      } catch (RazorpayException e) {
		        System.out.println("Exception caused because of " + e.getMessage());
		        
		      }
		    }
		    
}catch(Exception e){
	outputHTML=e.toString();
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%= outputHTML %>
</body>
</html>