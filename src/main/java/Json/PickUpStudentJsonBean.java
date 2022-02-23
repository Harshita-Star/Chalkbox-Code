package Json;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
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
import schooldata.StudentInfo;

@ManagedBean(name="pickUpStudentJson")
@ViewScoped
public class PickUpStudentJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public PickUpStudentJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
        try {
        	Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

    		String addNo = params.get("addNo");
    		String remark = params.get("remark");
    		String gName = params.get("gName");
    		String gMobileNo = params.get("gMobileNo");
    		String gImage = params.get("gImage");
    		String gRelation = params.get("gRelation");
    		String gType = params.get("gType");
    		String schId=params.get("schId");
    		String actionName=params.get("actionName");
    		String userid = params.get("userid");
    		
    		if(userid==null || userid.equalsIgnoreCase(""))
    		{
    			userid = "";
    		}

    		Map<String, String> sysParams =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestHeaderMap();
    		String userAgent = sysParams.get("User-Agent");
    		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
    		
    		if(checkRequest)
    		{
    			if(actionName.equals("guardianDetail"))
    			{
    				StudentInfo stdInfo =DBM.studentDetailslistByAddNo(schId, addNo, conn);
    				if(stdInfo!=null)
    				{
    					ArrayList<StudentInfo> guardianList=DBM.makeGuardianList(schId,stdInfo, conn);
    					JSONArray arr=new JSONArray();

    					for(StudentInfo ll:guardianList)
    					{
    						JSONObject obj = new JSONObject();
    						obj.put("Type",ll.getName());
    						obj.put("Relation",ll.getRelation());
    						obj.put("mobileNo",ll.getContactNo());
    						obj.put("name",ll.getFname());
    						obj.put("image", ll.getSignImage());
    						arr.add(obj);
    					}
    					//mainobj.put("SchoolJson", arr);
    					json=arr.toJSONString();
    				}
    				else
    				{
    					JSONArray arr=new JSONArray();

    					JSONObject obj = new JSONObject();
    					obj.put("msg", "No Record Found");
    					arr.add(obj);
    					//mainobj.put("SchoolJson", arr);
    					json=arr.toJSONString();
    				}
    			}
    			else if(actionName.equals("pickup"))
    			{
    				StudentInfo selectedGuardian=new StudentInfo();
    				selectedGuardian.setName(gType);
    				selectedGuardian.setRelation(gRelation);
    				selectedGuardian.setFname(gName);
    				selectedGuardian.setContactNo(gMobileNo);
    				selectedGuardian.setSignImage(gImage);

    				StudentInfo ls = DBJ.studentDetailslistByAddNo(addNo, schId, conn);

    				int i=DBM.pickUpStudent(addNo,remark,selectedGuardian,schId,conn);
    				if(i==1)
    				{
    					if(ls.getFathersPhone()==ls.getMothersPhone())
    					{
    						DBJ.notification("Security", "Your ward "+ls.getFname()+" has been picked up from school by "+gName+" ("+gRelation+").",
    								ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schId, schId,"", conn);
    					}
    					else
    					{
    						DBJ.notification("Security", "Your ward "+ls.getFname()+" has been picked up from school by "+gName+" ("+gRelation+").",
    								ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schId, schId,"", conn);
    						DBJ.notification("Security", "Your ward "+ls.getFname()+" has been picked up from school by "+gName+" ("+gRelation+").",
    								ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schId, schId,"", conn);
    					}

    					String currentStop = DBJ.studentCurrentStop(addNo,schId,conn);
    					if(!currentStop.equalsIgnoreCase("no"))
    					{
    						String currentRoute = DBJ.routeIdFromStopGroupId(currentStop, DBM.selectedSessionDetails(schId,conn), conn, schId);
    						String rem = "Left With Parents";
    						String pickTime = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date());
    						DBJ.insertStudentPickDetail(addNo,schId,currentStop,currentRoute,rem,new Date(),"no","yes","schoolpick",userid,pickTime, conn);
    						DBJ.insertStudentDropDetail(addNo, schId, currentStop, currentRoute, rem, new Date(),"no","yes","drop",userid,pickTime, conn);
    					}

    					JSONArray arr=new JSONArray();

    					JSONObject obj = new JSONObject();
    					obj.put("msg", "Student Pick Up Sucessfully");
    					arr.add(obj);
    					//mainobj.put("SchoolJson", arr);
    					json=arr.toJSONString();
    				}
    				else
    				{
    					JSONArray arr=new JSONArray();

    					JSONObject obj = new JSONObject();
    					obj.put("msg", "An Error Occured");
    					arr.add(obj);
    					//mainobj.put("SchoolJson", arr);
    					json=arr.toJSONString();
    				}
    			}
    			else if(actionName.equals("otpGenerate"))
    			{
    				StudentInfo ls = DBJ.studentDetailslistByAddNo(addNo, schId, conn);
    				SchoolInfoList info = DBJ.fullSchoolInfo(schId, conn);
    				
    				int randomOtp = (int)(Math.random()*9000)+1000;
    				String strRandomOtp = String.valueOf(randomOtp);

    				String txt = strRandomOtp;
    				String key = "key phrase used for XOR-ing";
    				txt = xorMessage(txt, key);
    				String encoded = base64encode(txt);

    				String finalOtp=encoded;
    				String schName = new DatabaseMethods1().getSMSSchoolName(schId, conn);
    				String typemessage="Hello,\n"
    						+String.valueOf(randomOtp)+ " is  your verification OTP for student pick.Treat this as confidential.\nRegards.\n"+schName;

    				if(info.getCountry().equalsIgnoreCase("India"))
    				{
    					DBJ.messageurl1(gMobileNo, typemessage, addNo,
    							schId, conn);
    				}
    				else
    				{
    					if(ls.getFathersPhone()==ls.getMothersPhone())
    					{
    						DBJ.notification("Security", typemessage,
    								ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schId, schId,"", conn);
    					}
    					else
    					{
    						DBJ.notification("Security", typemessage,
    								ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schId, schId,"", conn);
    						DBJ.notification("Security", typemessage,
    								ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schId, schId,"", conn);
    					}
    				}

    				JSONArray arr=new JSONArray();

    				JSONObject obj = new JSONObject();
    				obj.put("OTP", finalOtp);
    				arr.add(obj);
    				//mainobj.put("SchoolJson", arr);
    				json=arr.toJSONString();
    			}
    		}
    		else
    		{
    			JSONArray arr=new JSONArray();

    			JSONObject obj = new JSONObject();
    			obj.put("msg", "An Error Occured");
    			arr.add(obj);
    			
    			json=arr.toJSONString();
    		}
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

	public static final String DEFAULT_ENCODING = "UTF-8";
//	static BASE64Encoder enc = new BASE64Encoder();
//	static BASE64Decoder dec = new BASE64Decoder();
//
//	public static String base64encode(String text) {
//		try {
//			return enc.encode(text.getBytes(DEFAULT_ENCODING));
//		} catch (UnsupportedEncodingException e) {
//			return null;
//		}
//	}// base64encode
//
//	public static String base64decode(String text) {
//		try {
//			return new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
//		} catch (IOException e) {
//			return null;
//		}
//	}// base64decode
	
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
				// //// // System.out.println((mesg[i]));
				// //// // System.out.println((keys[i % kl]));
				// //// // System.out.println((mesg[i] ^ keys[i % kl]));
				newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
			} // for i

			return new String(newmsg);
		} catch (Exception e) {
			return null;
		}
	}// xorMessage

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
