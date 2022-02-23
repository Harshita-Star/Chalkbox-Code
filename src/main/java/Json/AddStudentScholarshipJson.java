package Json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONObject;
import org.primefaces.shaded.json.JSONArray;

import schooldata.DataBaseConnection;
import sun.misc.BASE64Decoder;
@ManagedBean(name="addStudentScholarshipJson")
@ViewScoped
public class AddStudentScholarshipJson implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public AddStudentScholarshipJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String studentName=params.get("studentName");
			String stuClass=params.get("stuClass");
			String fatherName=params.get("fatherName");
			String motherName=params.get("motherName");
			String dob=params.get("dob");
			String category=params.get("category");
			String bplStatus=params.get("bplStatus");
			String bplNo=params.get("bplNo");
			String caste=params.get("caste");
			String singleChild=params.get("singleChild");
			String gender=params.get("gender");
			String address=params.get("address");
			String mobno=params.get("mobno");
			String alterMobno=params.get("alterMobno");
			String schid=params.get("schid");
			String preSchoolName=params.get("preSchoolName");
			String medium=params.get("medium");
			String preClass=params.get("preClass");
			String percentage=params.get("percentage");
			String txnNo=params.get("txnNo");
			String imagePath = params.get("image");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			int i = 0;
			if(checkRequest)
			{
				int rendomNumber=0;
				String name="";
				if(!imagePath.equals(""))
				{
					byte[] imageByte = null;
					BASE64Decoder decoder = new BASE64Decoder();
					try {
						imageByte = decoder.decodeBuffer(imagePath);
					} catch (IOException e) {
						e.printStackTrace();
					}
					ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
					rendomNumber=(int)(Math.random()*9000)+1000;
					name="student"+String.valueOf(rendomNumber)+".jpg";
					DBJ.makeScanPath11(bis,name,schid,conn);

				}
				Date d=null;
				if(dob==null||dob.equalsIgnoreCase(""))
				{
					d=new Date();
				}
				else
				{
					try {
						d = new SimpleDateFormat("dd/MM/yyyy").parse(dob);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				int	number=DBJ.regNo(stuClass,schid,conn);
				String num="";
				if(String.valueOf(number).length()==1)
				{
					num="AST-2018"+"-"+stuClass+"-"+"0000"+String.valueOf(number);
				}
				else if(String.valueOf(number).length()==2)
				{
					num="AST-2018"+"-"+stuClass+"-"+"000"+String.valueOf(number);
				}
				else if(String.valueOf(number).length()==3)
				{
					num="AST-2018"+"-"+stuClass+"-"+"00"+String.valueOf(number);
				}
				else if(String.valueOf(number).length()==4)
				{
					num="AST-2018"+"-"+stuClass+"-"+"0"+String.valueOf(number);
				}
				else if(String.valueOf(number).length()==5)
				{
					num="AST-2018"+"-"+stuClass+"-"+String.valueOf(number);
				}
				i=DBJ.addStudentScholarshipDetail(studentName,stuClass,fatherName,motherName,d,category,bplStatus,bplNo,caste,singleChild,gender,address,mobno,alterMobno,schid,preSchoolName,medium,preClass,percentage,num,name,txnNo,conn);
				
			}
			
			JSONArray arr=new JSONArray();
			JSONObject obj=new JSONObject();
			if(i>=1)
			{
				obj.put("status","Details Added Successfully");
			}
			else
			{
				obj.put("status","Some Error Occured");
			}
			arr.put(obj);
			json=arr.toString();	
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally 
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
