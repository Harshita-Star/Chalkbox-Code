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

@ManagedBean(name="MsgPackRequestJson")
@ViewScoped

public class MsgPackRequestJsonBean implements Serializable
{
	String json;
	ArrayList<MessagePackInfo>packList = new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public MsgPackRequestJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				packList=DBM.selectAllMsgPackRequest("pending",conn);
				for(MessagePackInfo mm:packList)
				{

					JSONObject mainobj = new JSONObject();
					mainobj.put("packName", mm.getPackName());
					mainobj.put("msgQuantity", mm.getQuantity());
					mainobj.put("perPrice", mm.getRate());
					mainobj.put("tax", mm.getTax());
					mainobj.put("totalamt", mm.getTotalAmount());
					mainobj.put("amt", mm.getAmount());
					mainobj.put("id", mm.getId());
					mainobj.put("schid", mm.getSchoolId());
					mainobj.put("schoolName", mm.getSchoolName());
					arr.add(mainobj);
				}
			}

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

	public ArrayList<MessagePackInfo> getPackList() {
		return packList;
	}
	public void setPackList(ArrayList<MessagePackInfo> packList) {
		this.packList = packList;
	}

}
