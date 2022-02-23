<%@page import="org.json.JSONObject"%>
<%@page import="com.razorpay.RazorpayClient"%>
<%@page import="com.razorpay.RazorpayException"%>
<%@page import="com.razorpay.Order"%>
<%@page import="schooldata.SchoolInfoList"%>
<%@page import="java.sql.Connection"%>
<%@page import="schooldata.DataBaseConnection"%>
<%@page import="Json.DataBaseMeathodJson"%>
<%@page import="schooldata.DatabaseMethods1"%>
<%@page import="schooldata.SchoolInfo"%>
<%@page import="student_module.PaytmConstants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,com.paytm.pg.merchant.CheckSumServiceHelper"%>    
<%

Enumeration<String> paramNames = request.getParameterNames();
Map<String, String[]> mapData = request.getParameterMap();
TreeMap<String,String> parameters = new TreeMap<String,String>();
String paytmChecksum =  "";
String rcpt = "";
String curr = "INR";
String amt = "100";
HttpSession tt=request.getSession(false);
String schoolid = (String) tt.getAttribute("schoolid");

while(paramNames.hasMoreElements()) {
	String paramName = (String)paramNames.nextElement();
	//amt = mapData.get("amount")[0];
	//curr = mapData.get("currency")[0];
	rcpt = mapData.get("receipt")[0];
	//parameters.put(paramName,mapData.get(paramName)[0]);	
	//System.out.println(curr);
}

RazorpayClient razorpayClient = new RazorpayClient(PaytmConstants.MERCHANT_KEY, PaytmConstants.KEY_SECRET);
Order order = null;
try {
	  JSONObject orderRequest = new JSONObject();
	  orderRequest.put("amount", Integer.valueOf(amt)); // amount in the smallest currency unit
	  orderRequest.put("currency", curr);
	  orderRequest.put("receipt", rcpt);

	  	order = razorpayClient.Orders.create(orderRequest);
	  	parameters.put("key",PaytmConstants.MERCHANT_KEY);
		parameters.put("amount",amt);
		parameters.put("currency",curr);
		parameters.put("name","Piyush Singh");
		parameters.put("order_id", String.valueOf(order.get("id")));
		parameters.put("callback_url", "http://localhost:8081/CBX-Code/rzpResponse.jsp");
	} catch (RazorpayException e) {
	  System.out.println(e.getMessage());
	}

	Connection con=DataBaseConnection.javaConnection();
	SchoolInfoList list=new DataBaseMeathodJson().fullSchoolInfo(schoolid, con);

	String checkSum ="";
 	//checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(list.getPaytm_marchent_key(), parameters);


StringBuilder outputHtml = new StringBuilder();
outputHtml.append("<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>");
outputHtml.append("<html>");
outputHtml.append("<head>");
outputHtml.append("<title>Merchant Check Out Page</title>");
outputHtml.append("</head>");
outputHtml.append("<body>");
outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
/* outputHtml.append("<form>");
	outputHtml.append("<button id='rzp-button1'>Pay</button>");
	outputHtml.append("<script src='https://checkout.razorpay.com/v1/checkout.js'></script>");
	outputHtml.append("<script type='text/javascript'>");
		outputHtml.append("var options = {");
			outputHtml.append("'key': '"+PaytmConstants.MERCHANT_KEY+"',");
			outputHtml.append("'amount': '"+amt+"',");
			outputHtml.append("'currency': 'INR',");
			outputHtml.append("'name': 'Piyush Singh',");
			outputHtml.append("'order_id': '"+String.valueOf(order.get("id"))+"',");
			outputHtml.append("'callback_url': 'http://localhost:8081/CBX-Code/rzpResponse.jsp'");
		outputHtml.append("};");
	
		outputHtml.append("var rzp1 = new Razorpay(options)");
		outputHtml.append("document.getElementById('rzp-button1').onclick = function(e){");
			outputHtml.append("rzp1.open();");
		outputHtml.append("});");
	outputHtml.append("</script>"); */
outputHtml.append("<form method='POST' action='"+ "http://localhost:8081/CBX-Code/rzpResponse.jsp" +"' name='f1'>");

	outputHtml.append("<script ");
		outputHtml.append("src=https://checkout.razorpay.com/v1/checkout.js");
		outputHtml.append(" data-key=\""+PaytmConstants.MERCHANT_KEY+"\"");
		outputHtml.append(" data-amount=\""+amt+"\"");
		outputHtml.append(" data-currency=\"INR\"");
		outputHtml.append(" data-order_id=\""+String.valueOf(order.get("id"))+"\"");
		outputHtml.append(" data-buttontext=\"PAY NOW\"");
		outputHtml.append("data-name=\"Shikshankur The Global School\"");
	outputHtml.append("></script>");
	outputHtml.append("<input type=\"hidden\" custom=\"Hidden Element\" name=\"hidden\">");
outputHtml.append("</form>");
outputHtml.append("</body>");
outputHtml.append("</html>");
out.println(outputHtml);

%>
