package Json;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ViewScoped
@ManagedBean(name="appExpenseJson")
public class AppExpenseJsonBean
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	public AppExpenseJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			//id, amount, exp_date, remark, username, schid
			String amount = params.get("amount");
			String remark = params.get("remark");
			String userName = params.get("userName");
			String schid = params.get("schid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			int i = 0;
			
			if(checkRequest)
			{
				Date d=new Date();
				/*try {
						d = new SimpleDateFormat("dd/MM/yyyy").parse(expDate);
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				 */

				i=DBJ.addAppExpense(amount,d,remark,userName,schid,conn);
				//		JSONObject mainobj = new JSONObject();
				//		JSONArray arr=new JSONArray();
			}
			
			JSONObject obj = new JSONObject();
			String status="";
			if(i>0)
			{
				status="updated";
			}
			else
			{
				status="not updated";
			}

			////// // System.out.println(status);
			obj.put("status",status);

			//arr.add(obj);

			json=obj.toJSONString();
			
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
