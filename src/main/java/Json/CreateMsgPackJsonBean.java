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

import library_module.BookInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="createMsgPackJson")
@ViewScoped

public class CreateMsgPackJsonBean implements Serializable
{
	String json;
	ArrayList<BookInfo> allBook;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public CreateMsgPackJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			JSONObject mainobj  = new JSONObject();
			String value = "";
			String packName=params.get("packName");
			String quantity=params.get("quantity");
			String prize=params.get("prize");
			String amt=params.get("amt");
			String taxamt=params.get("taxamt");
			String totalamt=params.get("totalamt");

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				int i=DBM.addMessagePack(packName,quantity,prize,amt,taxamt,totalamt,conn);
				if(i>=1)
				{
					value= "sucessfully submitted";
					mainobj.put("status", value);
					arr.add(mainobj);
					json=arr.toJSONString();

				}
			}
			else
			{
				value= "Something Went Wrong!";
				mainobj.put("status", value);
				arr.add(mainobj);
				json=arr.toJSONString();
			}
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
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public ArrayList<BookInfo> getAllBook() {
		return allBook;
	}

	public void setAllBook(ArrayList<BookInfo> allBook) {
		this.allBook = allBook;
	}
}
