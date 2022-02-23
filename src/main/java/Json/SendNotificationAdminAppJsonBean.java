package Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="sendNotificationAdminAppJson")
@ViewScoped
public class SendNotificationAdminAppJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	public SendNotificationAdminAppJsonBean()
	{
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();
		JSONObject jobj = new JSONObject();
		JSONArray jarray = new JSONArray();
		DataBaseConnection.javaConnection();
		String notification=params.get("notification");
		String title = params.get("title");

		String groupid="chalkbox";
		String status="not updated";
		Map<String, String> sysParams =FacesContext.getCurrentInstance().
				getExternalContext().getRequestHeaderMap();
		String userAgent = sysParams.get("User-Agent");
		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
		if(checkRequest)
		{
			adminnotification(title, notification, groupid);
			status="updated";
		}

		jobj.put("value", status);
		jarray.add(jobj);
		json=jarray.toJSONString();

	}
	public void adminnotification(String title, String message1, String group
			)
	{
		final String apiKey = "AAAAaoFmDCc:APA91bFSCXCDvpPPN70s8v_d83f2p6EgJVkGOs_JIsg6muf5RUx1YQNFRxgNUP8jZjCwePaDEsx3KEDX3tynYMHq_ZI0qsGCZOoMipS2nc5C42JzeWkyCIu2rd5iTKN-elmUBH3ggDnt";
		URL url;
		try {
			url = new URL("https://fcm.googleapis.com/fcm/send");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "key=" + apiKey);

			conn.setDoOutput(true);

			JSONObject message = new JSONObject();
			message.put("to", "/topics/" + group);
			message.put("priority", "high");

			JSONObject notification = new JSONObject();
			notification.put("title", title);

			notification.put("body", message1);

			message.put("notification", notification);

			String input = message.toString();

			// String input =
			// "{\"notification\" : {\"title\" : \"Test\"}, \"to\":\"d1zUCWK_Eaw:APA91bE9z3FOZa7zT9m4us_lHmqK9z65a02_2LkudAb3jWPMQYva_SvTe0prrAVEceOnwNpm6DzKtBQ48WTFsR0IhvX0wOYbAgW4hXhhihk4BZqHpm-ZZW-cZ6DTXZkc9Q-doGe5M1jt\"}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			os.close();

			conn.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}


		} catch (Exception e) {
			e.printStackTrace();
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
