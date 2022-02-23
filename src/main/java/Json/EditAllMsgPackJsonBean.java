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
import schooldata.MessagePackInfo;

@ManagedBean(name="editAllMsgPackJson")
@ViewScoped

public class EditAllMsgPackJsonBean implements Serializable
{
	String json;
	ArrayList<MessagePackInfo>packList = new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	
	public EditAllMsgPackJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			new DataBaseMeathodJson();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			JSONObject mainobj = new JSONObject();
			JSONArray arr = new JSONArray();
			String value = "error";
			String packName = params.get("packname");
			String quantity = params.get("quantity");
			String rate = params.get("rate");
			String tax = params.get("tax");
			String amount = params.get("amount");
			String totalAmount = params.get("totalAmount");
			String id = params.get("id");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i = DBM.editAllValues(conn,id,packName,quantity,Double.valueOf(rate),Double.valueOf(tax),Double.valueOf(amount),Double.valueOf(totalAmount));
				if(i>=1)
				{
					value = "sucessfully edited";
				}
				else
				{
					value = "error";
				}
			}

			mainobj.put("status", value);
			arr.add(mainobj);
			json=arr.toJSONString();	
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		

		

	}




	public ArrayList<MessagePackInfo> getPackList() {
		return packList;
	}




	public void setPackList(ArrayList<MessagePackInfo> packList) {
		this.packList = packList;
	}




	public void renderJson() throws IOException {
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
