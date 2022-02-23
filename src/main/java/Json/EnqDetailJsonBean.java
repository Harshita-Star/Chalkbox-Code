package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
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
import schooldata.StudentInfo1;

@ManagedBean(name="enqDetailJson")
@ViewScoped

public class EnqDetailJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();

	public EnqDetailJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schid = params.get("schid");
			String refNo = params.get("enqNo");
			String session = "";
			SchoolInfoList info = dbj.fullSchoolInfo(schid,conn);
			
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month=now.get(Calendar.MONTH)+1;
			if(month>=4)
			{
				session=String.valueOf(year)+"-"+String.valueOf(year+1);
			}
			else
			{
				session=String.valueOf(year-1)+"-"+String.valueOf(year);
			}
			
			StudentInfo1 enqinfo = dbj.studentEnquiryByRefNo(refNo, session, schid, info, conn);
			
			if(enqinfo.getEnquiryStatus().equalsIgnoreCase("no record"))
			{
				obj.put("status", "No Record Found");
			}
			else if(enqinfo.getEnquiryStatus().equalsIgnoreCase("denied"))
			{
				obj.put("status", "This Enquiry Has Been Rejected. Please Contact School Administration For Further Assistance.");
			}
			else if(enqinfo.getEnquiryStatus().equalsIgnoreCase("done") || enqinfo.getEnquiryStatus().equalsIgnoreCase("accepted"))
			{
				obj.put("status", "Admission of Your Ward "+enqinfo.getStudentName()+" Has Been Done. Please Contact School Administration For Further Assistance.");
			}
			else if(enqinfo.getEnquiryStatus().equalsIgnoreCase("pending"))
			{
				double pamt = dbm.enqFeeAmountToPay("Prospectus Fee",session,schid,conn);
				double ramt = dbm.enqFeeAmountToPay("Registration Fee",session,schid,conn);
				
				obj.put("status", "pending");
				obj.put("enqId", enqinfo.getId());
				obj.put("enqDate", enqinfo.getStringadmissionDate());
				obj.put("studentName", enqinfo.getStudentName().toUpperCase());
				obj.put("fatherName", enqinfo.getFatherName().toUpperCase());
				obj.put("motherName", enqinfo.getMotherName().toUpperCase());
				obj.put("gender", enqinfo.getGender().toUpperCase());
				obj.put("dob", enqinfo.getStringdob());
				obj.put("address", enqinfo.getAddress());
				obj.put("fatherMob", enqinfo.getMobno());
				obj.put("motherMob", enqinfo.getMothmobno());
				obj.put("email", enqinfo.getEmail());
				obj.put("enqNo", enqinfo.getRefNo());
				obj.put("registrationFeeStatus", enqinfo.isStatusRegistration() ? "paid" : "unpaid");
				obj.put("prospectusFeeStatus", enqinfo.isStatusProspectus() ? "paid" : "unpaid");
				obj.put("registrationFeeAmount", new DecimalFormat("##").format(ramt));
				obj.put("prospectusFeeAmount", new DecimalFormat("##").format(pamt));
				obj.put("enqType", info.getEnqType());//both,registration,prospectus
				obj.put("paytm_marchent_key", info.getPaytm_marchent_key());
				obj.put("paytm_mid", info.getPaytm_mid());
				obj.put("class", enqinfo.getAdmissionclass());
				
			}
			
			arr.add(obj);
			json=arr.toJSONString();
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
