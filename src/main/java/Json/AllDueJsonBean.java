package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.DataBaseMethodsReports;
import schooldata.BillInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
@ManagedBean(name="allDueJson")
@ViewScoped
public class AllDueJsonBean implements Serializable
{
	ArrayList<BillInfo> list = new ArrayList<>();
	String json="";
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DataBaseMethodsReports dbr= new DataBaseMethodsReports();

	public AllDueJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String status = params.get("status");

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();
			
			if(checkRequest)
			{
				if(status.equalsIgnoreCase("unpaid") || status.equalsIgnoreCase("paid") || status.equalsIgnoreCase("all"))
				{
					list = dbr.allOverdueBills(status,conn);
				}
				else
				{
					String dueDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());

					list = dbr.allOverdueBills(dueDate,conn);
				}
				for(BillInfo ss:list)
				{
					JSONObject jobj = new JSONObject();
					jobj.put("Id", ss.getId());
					jobj.put("schId", ss.getSchid());
					SchoolInfoList ls=DBJ.fullSchoolInfo(ss.getSchid(),conn);

					jobj.put("schoolName", ss.getSchoolName());
					jobj.put("billNo", ss.getBillNo());
					jobj.put("billDateStr", ss.getBillDateStr());
					jobj.put("dueDateStr", ss.getDueDateStr());
					jobj.put("File", ls.getDownloadpath()+ss.getFile());
					arr.add(jobj);
				}
			}
			
			json=arr.toJSONString();
			
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
	public String getJson() {
		return json;
	}
	public ArrayList<BillInfo> getList() {
		return list;
	}
	public void setList(ArrayList<BillInfo> list) {
		this.list = list;
	}
	public void setJson(String json) {
		this.json = json;
	}
}
