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

import reports_module.DataBaseMethodsReports;
import schooldata.BillInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;



@ManagedBean(name="schoolBillJson")
@ViewScoped
public class SchoolBillJsonBean implements Serializable{
	
	
	
	
	
	ArrayList<BillInfo> list = new ArrayList<>();
	
	String json="";
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	
	public SchoolBillJsonBean() {

	
			Connection conn = DataBaseConnection.javaConnection();
	
			try {
				Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
				String schid = params.get("Schoolid");
				String status= params.get("status");

				
				// // System.out.println(schid);
				JSONArray arr=new JSONArray();
				SchoolInfoList lst=dbj.fullSchoolInfo(schid, conn);
				list = dbr.myBills(status,schid,conn);
				
				for(BillInfo ls:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("id", ls.getId());
					obj.put("billNo", ls.getBillNo());
					obj.put("billDate", ls.getBillDateStr());
					obj.put("amount", ls.getAmount());
					obj.put("dueDate", ls.getDueDateStr());
					obj.put("status", ls.getStatus());
					
	                if(ls.getFile().equals(""))
	                {
	                	obj.put("file", "");
	                    	
	                }
	                else
	                {
	                	obj.put("file", lst.getDownloadpath()+ls.getFile());
	                }
					arr.add(obj);
		        }
				
				
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



	public String getJson() {
		return json;
	}



	public void setJson(String json) {
		this.json = json;
	}
	
	

}
