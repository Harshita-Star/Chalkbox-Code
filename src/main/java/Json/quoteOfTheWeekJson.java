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

import org.primefaces.model.file.UploadedFile;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.AboutUsInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="quoteOfTheWeekJson")
@ViewScoped
public class quoteOfTheWeekJson implements Serializable
{
	String principalMessage,chairManMessage,schoolmeassage,emailId,contactNo,website,QuoteOfTheWeek;
	UploadedFile principalPicture,chairmanPicture,schoolPicture;
	ArrayList<AboutUsInfo> informationList;
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public quoteOfTheWeekJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			DatabaseMethods1 DBM=new DatabaseMethods1();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			/*QuoteOfTheWeek=params.get("quoteOfTheWeek");*/
			String schoolid=params.get("Schoolid");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				informationList =DBM.informationOfQuoteOfTheWeek(schoolid,conn);
				
				for(AboutUsInfo ls:informationList)
				{
					JSONObject obj = new JSONObject();
					obj.put("quote",ls.getQuoteOfTheweek());
					arr.put(obj);
				}
			}
			
			json=arr.toString();
			/*if(informationList.size()==0){
		String aboutUs=DBM.insertDetails(QuoteOfTheWeek,schoolid,conn);
		if(aboutUs.equals("i")){

			 JSONObject mainobj = new JSONObject();
			 JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();

			      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
			      obj.put("type", "student");
			      mainobj.put("SchoolJson", arr);

					json=mainobj.toJSONString();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ADD DETAIL SUCCESSFULLY"));
			contactNo="";
			emailId="";
			website="";
			QuoteOfTheWeek="";
			informationList =DBM.informationOfQuoteOfTheWeek(schoolid,conn);
		}
		else{


			 JSONObject mainobj = new JSONObject();
			 JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();

			      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
			      obj.put("type", "student");
			      mainobj.put("SchoolJson", arr);

					json=mainobj.toJSONString();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error occured try again !!"));

		}
		}else{
			String aboutUs=DBM.updateDetails(QuoteOfTheWeek,schoolid,conn);
			if(aboutUs.equals("i")){

				 JSONObject mainobj = new JSONObject();
				 JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();

				      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
				      obj.put("type", "student");
				      mainobj.put("SchoolJson", arr);

						json=mainobj.toJSONString();

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ADD DETAIL SUCCESSFULLY"));
				contactNo="";
				emailId="";
				website="";
				QuoteOfTheWeek="";
				informationList =DBM.informationOfQuoteOfTheWeek(schoolid,conn);
			}
			else{

				 JSONObject mainobj = new JSONObject();
				 JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();

				      obj.put("msg", "ADD DETAIL SUCCESSFULLY");
				      obj.put("type", "student");
				      mainobj.put("SchoolJson", arr);

						json=mainobj.toJSONString();

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error occured try again !!"));
			}
		}*/

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