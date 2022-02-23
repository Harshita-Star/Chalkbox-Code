package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.MessageHistory;
@ManagedBean(name="schoolLegerMessageBean")
@ViewScoped
public class SchoolMessageLedgerJson implements Serializable {

	ArrayList<MessageHistory>allDetails=new ArrayList<>();
	double debit,credit;
	String json="";
	DatabaseMethods1 objj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public SchoolMessageLedgerJson() {

		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schoolid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<MessageHistory>list=objj.messageHistory(schid,conn);
				ArrayList<MessageHistory>message=objj.messageDebitWise(schid,conn);

				allDetails.addAll(list);
				allDetails.addAll(message);

				Collections.sort(allDetails);
				for(MessageHistory ss1:allDetails)
				{
					JSONObject obj = new JSONObject();

					obj.put("date", ss1.getStrDate());
					obj.put("desc", ss1.getDescription());
					obj.put("debit", ss1.getDebit());
					obj.put("credit", ss1.getCredit());
					arr.put(obj);
				}
			}

			json = arr.toString();
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
