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

HttpSession tt=request.getSession(false);
String schoolid = (String) tt.getAttribute("schoolid");

while(paramNames.hasMoreElements()) {
	String paramName = (String)paramNames.nextElement();
	parameters.put(paramName,mapData.get(paramName)[0]);	
	//System.out.print(mapData.get(paramName)[0]);
}


Connection con=DataBaseConnection.javaConnection();
SchoolInfoList list=new DataBaseMeathodJson().fullSchoolInfo(schoolid, con);

String checkSum ="";
parameters.put("MID","Chalkb01137209508671");
parameters.put("CHANNEL_ID","WEB");
parameters.put("INDUSTRY_TYPE_ID","PrivateEducation");
parameters.put("WEBSITE","WEBPROD");
parameters.put("MOBILE_NO","9876543210");
parameters.put("EMAIL","test@gmail.com");
//parameters.put("CALLBACK_URL", "http://www.chalkboxerp.in/DMM/pgResponseBillLG.jsp");
parameters.put("CALLBACK_URL", "http://localhost:8083/Chalkbox/pgResponseBillLG.jsp");
 
checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("BJerS#zIxeuP_ZZt", parameters);


StringBuilder outputHtml = new StringBuilder();
outputHtml.append("<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>");
outputHtml.append("<html>");
outputHtml.append("<head>");
outputHtml.append("<title>Merchant Check Out Page</title>");
outputHtml.append("</head>");
outputHtml.append("<body>");
outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");
outputHtml.append("<form method='get' action='"+ "https://securegw.paytm.in/theia/processTransaction" +"' name='f1'>");
outputHtml.append("<table border='1'>");
outputHtml.append("<tbody>");

for(Map.Entry<String,String> entry : parameters.entrySet()) {
	String key = entry.getKey();
	String value = entry.getValue();
	outputHtml.append("<input type='hidden' name='"+key+"' value='" +value+"'>");	
}	  

outputHtml.append("<input type='hidden' name='CHECKSUMHASH' value='"+checkSum+"'>");
outputHtml.append("</tbody>");
outputHtml.append("</table>");
outputHtml.append("<script type='text/javascript'>");
outputHtml.append("document.f1.submit();");
outputHtml.append("</script>");
outputHtml.append("</form>");
outputHtml.append("</body>");
outputHtml.append("</html>");
out.println(outputHtml);

%>
