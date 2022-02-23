package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.FeeInfo;
import schooldata.StudentInfo;
@ManagedBean(name="FeeRecipietJsonBean")
@ViewScoped
public class FeeRecipietJsonBean implements Serializable
{
	String json;

	ArrayList<FeeInfo> list;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	public FeeRecipietJsonBean() {

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schid=params.get("Schoolid");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list=dbj.FeeRecipiectjsin(studentid,schid,conn);
				// SchoolInfoList list1=new DataBaseMeathodJson().fullSchoolInfo(schid);
				//  ArrayList<NotificationInfo> info=new DatabaseMethods1().allMessageforapp(studentid);
				for(FeeInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("Date", ss.getPostdate());
					obj.put("RecipietNo", ss.getRecipietNo());
					obj.put("amount", String.valueOf(ss.getTotalFeeAmount()));
					obj.put("mode", ss.getPaymenmode());
					obj.put("month", ss.getFeeInstallMonth());
					arr.add(obj);
				}
			}
			
			mainobj.put("SchoolJson", arr);

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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}


}
