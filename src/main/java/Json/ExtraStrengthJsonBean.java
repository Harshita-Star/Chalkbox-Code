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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;
@ManagedBean(name="extraStrengthJson")
@ViewScoped
public class ExtraStrengthJsonBean implements Serializable
{
	String json="";
	ArrayList<SchoolInfo>schoolList = new ArrayList<>();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	public ExtraStrengthJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			JSONArray arrayList = new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				schoolList=obj1.selectAllStudentStrenght(conn);

				for(SchoolInfo ss:schoolList)
				{
					JSONObject js = new JSONObject();
					js.put("intid", ss.getIntId());
					js.put("id", ss.getId());
					js.put("schoolName", ss.getSchoolName());
					js.put("agreementFor", ss.getAgreementFor());
					js.put("strength", ss.getStrenght());
					js.put("status", ss.getStatus());

					arrayList.add(js);
				}
			}

			json=arrayList.toJSONString();
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
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public ArrayList<SchoolInfo> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SchoolInfo> schoolList) {
		this.schoolList = schoolList;
	}

}
