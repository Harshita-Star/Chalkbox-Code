<%@page import="schooldata.StudentInfo"%>
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
String amt = "";
String studentid = "";
HttpSession tt=request.getSession(false);
String schoolid = (String) tt.getAttribute("schoolid");

while(paramNames.hasMoreElements()) {
	String paramName = (String)paramNames.nextElement();
	amt = mapData.get("amount")[0];
	rcpt = mapData.get("receipt")[0];
	studentid = mapData.get("studentId")[0];
}

Connection con=DataBaseConnection.javaConnection();
SchoolInfoList schinfo=new DataBaseMeathodJson().fullSchoolInfo(schoolid, con);
StudentInfo stinfo = new DataBaseMeathodJson().studentDetailslistByAddNo(studentid, schoolid, con);

RazorpayClient razorpayClient = new RazorpayClient(schinfo.getRzp_key(), schinfo.getRzp_key_secret());
Order order = null;
try {
	  JSONObject orderRequest = new JSONObject();
	  orderRequest.put("amount", Integer.valueOf(amt)); // amount in the smallest currency unit
	  orderRequest.put("currency", curr);
	  orderRequest.put("receipt", rcpt);

	  order = razorpayClient.Orders.create(orderRequest);
	  	/* parameters.put("key", schinfo.getRzp_key());
		parameters.put("amount",amt);
		parameters.put("currency",curr);
		parameters.put("name", stinfo.getFullName());
		parameters.put("order_id", String.valueOf(order.get("id")));
		parameters.put("callback_url", "http://localhost:8081/CBX-Code/rzpResponse.jsp"); */
	} catch (RazorpayException e) {
	  System.out.println(e.getMessage());
	}

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
			outputHtml.append("'key': '"+schinfo.getRzp_key()+"',");
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
		outputHtml.append(" data-key=\""+schinfo.getRzp_key()+"\"");
		outputHtml.append(" data-amount=\""+amt+"\"");
		outputHtml.append(" data-currency=\"INR\"");
		outputHtml.append(" data-order_id=\""+String.valueOf(order.get("id"))+"\"");
		outputHtml.append(" data-buttontext=\"PAY NOW\"");
		outputHtml.append(" data-name=\""+schinfo.getSchoolName()+"\"");
		outputHtml.append(" data-prefill.name=\""+stinfo.getFullName()+"\"");
		outputHtml.append(" data-prefill.email=\""+stinfo.getFatherEmail()+"\"");
		outputHtml.append(" data-prefill.contact=\""+String.valueOf(stinfo.getFathersPhone())+"\"");
		
	outputHtml.append("></script>");
	outputHtml.append("<input type=\"hidden\" custom=\"Hidden Element\" name=\"hidden\">");
outputHtml.append("</form>");
outputHtml.append("</body>");
outputHtml.append("</html>");
out.println(outputHtml);

%>
