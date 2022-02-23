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
String paytmChecksum =  "";

HttpSession tt=request.getSession(false);
ArrayList<FeeInfo> selectedList=(ArrayList<FeeInfo>)tt.getAttribute("onlist");
String addmissionNumber=(String) tt.getAttribute("username");
String schoolid = (String) tt.getAttribute("schoolid");
String session1 = (String) tt.getAttribute("selectedSession");

System.out.println(session1);

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
	if(schoolid.equals("251"))
	{
		 checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("YaaAReat0ymrB2O4", parameters);
		 isValideChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum("YaaAReat0ymrB2O4",parameters,paytmChecksum);
	}
	else if(schoolid.equals("252"))
	{
		 checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("3_@O9w5vWSb!I%jg", parameters);
		 isValideChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum("3_@O9w5vWSb!I%jg",parameters,paytmChecksum);
		
	}
	else
	{
		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList list=new DataBaseMeathodJson().fullSchoolInfo(schoolid, con);
		//System.out.println("----------"+list.getPaytm_marchent_key());
		 checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(list.getPaytm_marchent_key(), parameters);
		 isValideChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(list.getPaytm_marchent_key(),parameters,paytmChecksum);
		
	}
	
	if(isValideChecksum && parameters.containsKey("RESPCODE")){
		//System.out.print(parameters.toString());
		if(parameters.get("RESPCODE").equals("01")){
		  //System.out.print("outputHTML");
			outputHTML = parameters.toString();
			String a[]=parameters.toString().split(",");
		 
			
			new test().values(a[4], a[5],selectedList,addmissionNumber,schoolid,session1);
		 
		   response.sendRedirect("studentOnlineFee.xhtml");
		 
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