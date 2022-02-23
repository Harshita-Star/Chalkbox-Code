package Json;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="chhotuRechargeOtpJson")
@ViewScoped
public class ChhotuRechargeOtpJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	
	public ChhotuRechargeOtpJsonBean() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId = params.get("schoolId");

			String submitted="Something Went Wrong!",contactNo="",strRandomOtp="",finalOtp="0";
			Integer randomOtp=0;

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				contactNo=DBM.contactNo("Chotu Recharge",conn);
				int i=DBM.checkChhotuRecharge(conn,0,schId,"approved");
				if(i==1)
				{
					submitted="Chhotu Recharge is already exist for this schoolid!Please try again..";
					finalOtp="0";
				}
				else
				{
					int j=DBM.checkChhotuRechargeInMessageTransaction(conn,schId);
					if(j==1)
					{
						submitted="Chhotu Recharge is already exist for this schoolid!Please try again...";
						finalOtp="0";
					}
					else
					{
						strRandomOtp = "";
						randomOtp = (int)(Math.random()*9000)+1000;
						strRandomOtp = String.valueOf(randomOtp);

						String txt = strRandomOtp;
						String key = "key phrase used for XOR-ing";
						txt = xorMessage(txt, key);
						String encoded = base64encode(txt);

						finalOtp=encoded;

						String schName = DBM.getSMSSchoolName(schId, conn);
						String typemessage="Hello Admin,\n"
								+String.valueOf(randomOtp)+ " is your OTP for Chhotu Recharge - "+schName+".\nRegards.\nCB";

						DBM.messageurlMasterAdmin(contactNo, typemessage/*,"masteradmin",conn*/);
					}
				}
			}
			
			mainobj.put("status", submitted);
			mainobj.put("otp", finalOtp);
			arr.add(mainobj);
			json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}


	public static final String DEFAULT_ENCODING = "UTF-8";
//	static BASE64Encoder enc = new BASE64Encoder();
//	static BASE64Decoder dec = new BASE64Decoder();

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

	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();

	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}




}
