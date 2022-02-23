package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONObject;
import org.primefaces.shaded.json.JSONArray;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;

@ManagedBean(name="addEnquiryJson")
@ViewScoped
public class AddEnquiryJsonBean implements Serializable {

	String json;
	String eno ;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public AddEnquiryJsonBean() {

		Connection conn= DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			//admissionDate,admissionNo, studentName, fatherName, motherName, gender, dob, address, mobno, email,class,year,schid
			//
			String session=params.get("session");//
			String studentName=params.get("studentName");//
			String admissionNo="";
			String fatherName=params.get("fatherName");//
			String motherName=params.get("motherName");//
			String gender=params.get("gender");//
			String address=params.get("address");//
			String mobno=params.get("mobno");//
			String email=params.get("email");//
			String stuClass=params.get("stuClass");//className
			String classid=params.get("classid");//
			String schid=params.get("schid");//
			String admissionDate=params.get("visitDate");//
			String dob=params.get("dob");//
			String remark=params.get("remark");	//
			String userId=params.get("userId");//
			String reference=params.get("reference");//
			String followDate=params.get("followDate");
			String status=params.get("status");//
			String followStatus=params.get("followupStatus");
			String category = params.get("category");
			String lastSchool = params.get("lastSchool");
			String referred = params.get("referredBy");

			String result="";

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = true;//new DataBaseMeathodJson().checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date d=null;
				Date d1=null;
				Date d2=null;
				if(admissionDate==null||admissionDate.equalsIgnoreCase(""))
				{
					d=new Date();
				}
				else
				{
					try {
						d = new SimpleDateFormat("dd/MM/yyyy").parse(admissionDate);
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}

				if(followDate==null||followDate.equalsIgnoreCase(""))
				{
					d2=new Date();
				}
				else
				{
					try {
						d2 = new SimpleDateFormat("dd/MM/yyyy").parse(followDate);
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}

				if(dob==null||dob.equalsIgnoreCase(""))
				{
					d1=new Date();
				}
				else
				{
					try {
						d1 = new SimpleDateFormat("dd/MM/yyyy").parse(dob);
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				
				if(category==null || category.equals(""))
				{
					category = "";
				}
				
				if(lastSchool==null || lastSchool.equals(""))
				{
					lastSchool = "";
				}
				
				if(referred==null || referred.equals(""))
				{
					referred = "";
				}
				
				//String res=DBJ.studentReg(d, admissionNo, studentName, fatherName, motherName, gender, d1, address, mobno, email, stuClass,remark, conn,schid,userId,eno,status);

				SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
				status="pending";
				if(info.getBranch_id().equalsIgnoreCase("54"))
				{
					eno = "BLM/ENQ/"+new SimpleDateFormat("yMdhms").format((new Date()));
				}
				else
				{
					eno = "CB/ENQ/"+new SimpleDateFormat("yMdhms").format((new Date()));

				}

				if(gender.equalsIgnoreCase("male"))
				{
					gender="Male";
				}
				else if(gender.equalsIgnoreCase("female"))
				{
					gender="Female";
				}
				
				result=DBJ.studentReg( d,admissionNo,studentName,fatherName,motherName,gender,d1,address,
						mobno, email,stuClass,d2,status,userId,remark,conn,eno,session,reference,schid,"",classid, category, lastSchool, referred);
				if(result.equals("successful"))
				{
					ArrayList<String> messageSetting=DBJ.checkmessageSetting(conn,schid);
					String message=messageSetting.get(2);
					if(message.equals("true"))
					{
						if(mobno.length()==10
								&& !mobno.equals("2222222222")
								&& !mobno.equals("9999999999")
								&& !mobno.equals("1111111111")
								&& !mobno.equals("1234567890")
								&& !mobno.equals("0123456789"))
						{
							if(info.getBranch_id().equalsIgnoreCase("52"))
							{
								String typemessage="Dear Sir/Madam,\nThank you For Visiting The Oasis.We Hope to see you again.\nRegards, \n"+info.getSmsSchoolName();
								DBJ.messageurl1(mobno,typemessage,studentName,schid,conn);
							}
							else
							{
								if(!info.getBranch_id().equalsIgnoreCase("22") && !info.getBranch_id().equalsIgnoreCase("27")) {
									 String templateId=new DataBaseMeathodJson().templateId(info.getKey(), "ENQ", conn);
										
										String typemessage="Dear Parent,\nThank you for visiting our "+info.getClient_type()+". Your enquiry reference no. is "+eno+". Please save it for admission purpose. We look forward to see you again.\nRegards, \n"+info.getSmsSchoolName();
									//	DBJ.messageurl1(mobno,typemessage,studentName,schid,conn);
										DBJ.messageurlenqwithTemplateId(schid,mobno,typemessage,studentName,templateId,conn);
								}
						
							}
						}
					}
					//eno="";
				}
				else
				{

				}
			}

			JSONArray arr=new JSONArray();
			JSONObject obj=new JSONObject();
			status="";
			if(result.equalsIgnoreCase("successful"))
			{
				status="updated";
			}
			else
			{
				status="not updated";
			}
			obj.put("status", status);
			obj.put("refno",eno );
			arr.put(obj);
			json=arr.toString();
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