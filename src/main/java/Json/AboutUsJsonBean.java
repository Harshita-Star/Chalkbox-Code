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
import schooldata.SchoolInfoList;

@ManagedBean(name="aboutUsJson")
@ViewScoped
public class AboutUsJsonBean implements Serializable{
	String principalMessage,chairManMessage,schoolmeassage;
	String principalPicture,chairmanPicture,schoolPicture;
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	
	
	public AboutUsJsonBean()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			
			String schoolid=params.get("Schoolid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONArray arr=new JSONArray();
			if(checkRequest)
			{
				ArrayList<AboutUsInfo> informationList=new ArrayList<>();
				SchoolInfoList list1=DBJ.fullSchoolInfo(schoolid,conn);
				informationList =DBJ.informationOfAboutUs(schoolid,conn);
				
				for(AboutUsInfo ls:informationList)
				{
					JSONObject obj = new JSONObject();
					obj.put("principalMsz",ls.getPrincipalMessage());
					obj.put("chairmanMsz",ls.getChairManMessage());
					obj.put("schoolMsz",ls.getSchoolmeassage());
					obj.put("pimage", ls.getPrincipalPicture());
					obj.put("cimage", ls.getChairmanPicture());
					obj.put("simage", ls.getSchoolPicture());
					obj.put("path",list1.getDownloadpath());
					arr.put(obj);
				}
			}

			json=arr.toString();
			/*if(informationList.size()==0)
			 {
					String name="";
					String name1="";
					String name2="";
					int rendomNumber=0;
					if(principalPicture!=null)
					{
						BufferedImage image = null;
						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try {
							imageByte = decoder.decodeBuffer(principalPicture);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
					    rendomNumber=(int)(Math.random()*9000)+1000;
					    name="event"+String.valueOf(rendomNumber)+".jpg";
					    DBJ.makeScanPath11(bis,name,schoolid,conn);
					}
					if(chairmanPicture!=null)
					{
						BufferedImage image = null;
						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try {
							imageByte = decoder.decodeBuffer(chairmanPicture);
						} catch (IOException e) {
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
					    rendomNumber=(int)(Math.random()*9000)+1000;
					    name1="event"+String.valueOf(rendomNumber)+".jpg";
					    DBJ.makeScanPath11(bis,name1,schoolid,conn);
					}

					if(schoolPicture!=null)
					{
						BufferedImage image = null;
						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try {
							imageByte = decoder.decodeBuffer(schoolPicture);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
						rendomNumber=(int)(Math.random()*9000)+1000;
						name2="event"+String.valueOf(rendomNumber)+".jpg";
						DBJ.makeScanPath11(bis,name2,schoolid,conn);
					}
					String aboutUs=DBJ.insertAboutForJson(principalMessage, chairManMessage, schoolmeassage, name, name1, name2,schoolid,conn);
					if(aboutUs.equals("i"))
					{
						 JSONObject mainobj = new JSONObject();
						 JSONArray arr=new JSONArray();
						 JSONObject obj = new JSONObject();
					     obj.put("msg", "ADD DETAIL SUCCESSFULLY");
					     obj.put("type", "student");
					     mainobj.put("SchoolJson", arr);
					     json=mainobj.toJSONString();
					     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ADD DETAIL SUCCESSFULLY"));
					     principalMessage="";
					     chairManMessage="";
					     schoolmeassage="";
					     informationList =DBJ.informationOfAboutUs(schoolid,conn);
					}
					else
					{
						 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error occured try again !!"));
						 JSONObject mainobj = new JSONObject();
						 JSONArray arr=new JSONArray();
						 JSONObject obj = new JSONObject();
						 obj.put("msg", "error occured try again !!");
						 arr.add(obj);
						 mainobj.put("SchoolJson", arr);
						 json=mainobj.toJSONString();
					}
				}
				else
				{
					String name="";
					String name1="";
					String name2="";
					int rendomNumber=0;
					if(principalPicture!=null)
					{
						BufferedImage image = null;
						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try {
							imageByte = decoder.decodeBuffer(principalPicture);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
						rendomNumber=(int)(Math.random()*9000)+1000;
						name="event"+String.valueOf(rendomNumber)+".jpg";
						DBJ.makeScanPath11(bis,name,schoolid,conn);
					}
					if(chairmanPicture!=null)
					{
						BufferedImage image = null;
						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try {
							imageByte = decoder.decodeBuffer(chairmanPicture);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
						rendomNumber=(int)(Math.random()*9000)+1000;
						name1="event"+String.valueOf(rendomNumber)+".jpg";
						DBJ.makeScanPath11(bis,name1,schoolid,conn);
					}

					if(schoolPicture!=null)
					{
	            		BufferedImage image = null;
	            		byte[] imageByte = null;
	            		BASE64Decoder decoder = new BASE64Decoder();
	            		try {
	            			imageByte = decoder.decodeBuffer(schoolPicture);
	            		} catch (IOException e) {
	            			
	            			e.printStackTrace();
	            		}
	            		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
	            		rendomNumber=(int)(Math.random()*9000)+1000;
	            		name2="event"+String.valueOf(rendomNumber)+".jpg";
	            		new DatabaseMethods1(schoolid).makeScanPath11(bis,name2,conn);
					}
					String aboutUs=DBM.insertAboutDetailsForJson(principalMessage,chairManMessage,schoolmeassage,name, name1, name2,schoolid,conn);
					if(aboutUs.equals("i"))
					{
						 JSONObject mainobj = new JSONObject();
						 JSONArray arr=new JSONArray();
						 JSONObject obj = new JSONObject();
						 obj.put("msg", "ADD DETAIL SUCCESSFULLY");
						 obj.put("type", "student");
						 mainobj.put("SchoolJson", arr);
						 json=mainobj.toJSONString();
						 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("ADD DETAIL SUCCESSFULLY"));
						 principalMessage="";
						 chairManMessage="";
						 schoolmeassage="";
						 informationList =DBM.informationOfAboutUs(schoolid,conn);
					}
					else
					{
						 JSONObject mainobj = new JSONObject();
						 JSONArray arr=new JSONArray();
						 JSONObject obj = new JSONObject();
						 obj.put("msg", "error occured try again !!");
						 json = mainobj.toJSONString();
						 FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("error occured try again !!"));
					}
				}*/
		} 
		catch (Exception e) {
			e.printStackTrace();
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
