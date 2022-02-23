package Json;

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

@ManagedBean(name="addInstituteEnquiryJson")
@ViewScoped
public class AddInstituteEnquiryJsonBean implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public AddInstituteEnquiryJsonBean() {

		Connection conn= DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();


			String visitDate=params.get("visitDate");
			String studentName=params.get("studentName");
			String fatherName=params.get("fatherName");
			String pMobNo=params.get("pMobno");
			String sMobNo=params.get("sMobno");
			String stuClass=params.get("stuClass");
			String medium=params.get("medium");
			String institute_ifany=params.get("institute_ifany");
			String preschool=params.get("preschool");
			String address=params.get("address");
			String institute_class=params.get("institute_class");
			String instituteinfo=params.get("instituteinfo");
			String schid=params.get("schid");
			String remark=params.get("remark");
			String userId=params.get("userId");
			String status=params.get("status");

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();
			JSONObject obj=new JSONObject();
			String sts="not updated";
			
			if(checkRequest)
			{
				Date d=null;
				if(visitDate==null||visitDate.equalsIgnoreCase(""))
				{
					d=new Date();
				}
				else
				{
					try {
						d = new SimpleDateFormat("dd/MM/yyyy").parse(visitDate);
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}

				String res=DBJ.addInstitueEnquiry(d,studentName,fatherName,pMobNo,sMobNo,stuClass,medium,institute_ifany,preschool,address,institute_class,instituteinfo,schid,remark,userId,status,conn);
				
				if(res.equals("successful"))
				{
					sts="updated";
				}
				else
				{
					sts="not updated";
				}
			}
			
			obj.put("status", sts);
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