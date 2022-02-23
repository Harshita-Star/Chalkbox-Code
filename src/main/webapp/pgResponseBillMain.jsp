<%@page import="schooldata.BillPayment"%>
<%@page import="schooldata.BillInfo"%>
<%@page import="schooldata.MessagePayment"%>
<%@page import="schooldata.MessagePackInfo"%>
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
String paytmChecksum =  "";

HttpSession tt=request.getSession(false);
String userid=(String) tt.getAttribute("username");
String schoolid = (String) tt.getAttribute("schoolid");
String ordid = (String) tt.getAttribute("billOrderid");

while(paramNames.hasMoreElements()) {
	String paramName = (String)paramNames.nextElement();
	
	if(paramName.equals("CHECKSUMHASH")){
		paytmChecksum = mapData.get(paramName)[0];
	}else{
		parameters.put(paramName,mapData.get(paramName)[0]);
	}
}
boolean isValideChecksum = false;
String checkSum="";

String outputHTML="";
try{
	
	     checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("BJerS#zIxeuP_ZZt", parameters);
		 isValideChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum("BJerS#zIxeuP_ZZt",parameters,paytmChecksum);
	
	if(isValideChecksum && parameters.containsKey("RESPCODE")){
		//System.out.print(parameters.toString());
		if(parameters.get("RESPCODE").equals("01")){
		  //System.out.print("outputHTML");
			outputHTML = parameters.toString();
			String a[]=parameters.toString().split(",");
		 
			
	  // new test().values(a[4], a[5],selectedList,addmissionNumber,schoolid);
		 
	  
	  new BillPayment().values(userid, schoolid, ordid);
		   response.sendRedirect("billPaymentSuccessMain.xhtml");
		 
		}else{
			outputHTML="<b>Payment Failed.</b>";
		
		}
	}else{
		outputHTML="<b>Checksum mismatched.</b>";
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