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

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.AboutUsInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
@ManagedBean(name="dailyImagejson")
@ViewScoped
public class DailyImageJson implements Serializable{
	String principalMessage,chairManMessage,schoolmeassage;
	String principalPicture,chairmanPicture,schoolPicture,pictue1,picture2,picture3,picture4,picture5;
	String json;
	ArrayList<AboutUsInfo> informationList;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public DailyImageJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schoolid=params.get("Schoolid");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList list1=DBJ.fullSchoolInfo(schoolid,conn);
				informationList =DBM.informationOfDailyImage(schoolid,conn);
				
				for(AboutUsInfo ls:informationList)
				{
					JSONObject obj = new JSONObject();
					obj.put("image",ls.getPictue1()+","+ls.getPicture2()+","+ls.getPicture3()+","+ls.getPicture4()+","+ls.getPicture5());
					obj.put("path",list1.getDownloadpath());
					arr.put(obj);
				}
			}

			json=arr.toString();
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
	public String getPrincipalMessage() {
		return principalMessage;
	}
	public void setPrincipalMessage(String principalMessage) {
		this.principalMessage = principalMessage;
	}
	public String getChairManMessage() {
		return chairManMessage;
	}
	public void setChairManMessage(String chairManMessage) {
		this.chairManMessage = chairManMessage;
	}
	public String getSchoolmeassage() {
		return schoolmeassage;
	}
	public void setSchoolmeassage(String schoolmeassage) {
		this.schoolmeassage = schoolmeassage;
	}
	public String getPrincipalPicture() {
		return principalPicture;
	}
	public void setPrincipalPicture(String principalPicture) {
		this.principalPicture = principalPicture;
	}
	public String getChairmanPicture() {
		return chairmanPicture;
	}
	public void setChairmanPicture(String chairmanPicture) {
		this.chairmanPicture = chairmanPicture;
	}
	public String getSchoolPicture() {
		return schoolPicture;
	}
	public void setSchoolPicture(String schoolPicture) {
		this.schoolPicture = schoolPicture;
	}
	public String getPictue1() {
		return pictue1;
	}
	public void setPictue1(String pictue1) {
		this.pictue1 = pictue1;
	}
	public String getPicture2() {
		return picture2;
	}
	public void setPicture2(String picture2) {
		this.picture2 = picture2;
	}
	public String getPicture3() {
		return picture3;
	}
	public void setPicture3(String picture3) {
		this.picture3 = picture3;
	}
	public String getPicture4() {
		return picture4;
	}
	public void setPicture4(String picture4) {
		this.picture4 = picture4;
	}
	public String getPicture5() {
		return picture5;
	}
	public void setPicture5(String picture5) {
		this.picture5 = picture5;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public ArrayList<AboutUsInfo> getInformationList() {
		return informationList;
	}
	public void setInformationList(ArrayList<AboutUsInfo> informationList) {
		this.informationList = informationList;
	}
}
