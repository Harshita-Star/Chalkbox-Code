package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.VisitorInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="addVisitorJson")
@ViewScoped
public class AddVisitorJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	public AddVisitorJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
				String actionName=params.get("actionName");
				if(actionName.equals("detail"))
				{
					String schId=params.get("schId");
					String mobileNo = params.get("mobileNo");
					SchoolInfoList info = DBJ.fullSchoolInfo(schId, conn);
					VisitorInfo visitInfo=DBM.visitorInfoByMobileNo(schId,mobileNo,conn);
					if(visitInfo.getName()==null || visitInfo.getName().equalsIgnoreCase("") || visitInfo.getName().equalsIgnoreCase("null"))
					{
						JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();
						obj.put("name","");
						obj.put("address","");
						obj.put("image", "");
						arr.add(obj);
						json=arr.toJSONString();
					}
					else
					{
						JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();
						obj.put("name",visitInfo.getName());
						obj.put("address",visitInfo.getAddress());
						obj.put("image", info.getDownloadpath()+visitInfo.getImagepath());
						arr.add(obj);

						json=arr.toJSONString();
					}
				}
				else if(actionName.equals("add"))
				{
					Date addDate = new Date();
					/*try {
						addDate = sdf.parse(params.get("date"));
					} catch (ParseException e) {
						
						e.printStackTrace();
					}*/
					String schId=params.get("schId");
					String visitorNo=DBM.generateVisitorNo(schId,conn);
					String name = params.get("name");
					String mobileNo = params.get("mobileNo");
					String address = params.get("address");
					String purpose = params.get("purpose");
					String toMeet = params.get("toMeet");
					String toMeetName=params.get("toMeetName");
					String otherDetails=params.get("otherDetails");
					String noOfPerson=params.get("noOfPerson");
					String personName=params.get("personName");
					String image=params.get("image");

					if(otherDetails==null || otherDetails.equalsIgnoreCase(""))
					{
						otherDetails = "";
					}

					String inTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
					int i=DBM.addVisitor(addDate,visitorNo,name,mobileNo,address,toMeet,purpose,otherDetails,noOfPerson,personName,image,conn,inTime,toMeetName,schId);
					if(i==1)
					{
						JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();
						obj.put("msg", "Visitor Added Sucessfully");
						arr.add(obj);
						json=arr.toJSONString();
					}
					else
					{
						JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();
						obj.put("msg", "An Error Occured");
						arr.add(obj);
						json=arr.toJSONString();
					}
				}
				else if(actionName.equals("out"))
				{
					String id=params.get("id");
					String out = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
					int i=DBM.updateVisitorOutTime(id,out,conn);
					if(i==1)
					{
						JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();
						obj.put("msg", "Visitor Updated Sucessfully");
						arr.add(obj);
						json=arr.toJSONString();
					}
					else
					{
						JSONArray arr=new JSONArray();

						JSONObject obj = new JSONObject();
						obj.put("msg", "An Error Occured");
						arr.add(obj);
						json=arr.toJSONString();
					}
				}
				else if(actionName.equals("list"))
				{
					Date startDate=null,endDate=null;
					try {
						startDate = sdf.parse(params.get("startDate"));
						endDate = sdf.parse(params.get("endDate"));
					} catch (ParseException e) {
						
						e.printStackTrace();
					}

					String sDate=sdf.format(startDate);
					String eDate=sdf.format(endDate);
					String schId=params.get("schId");
					DBJ.fullSchoolInfo(schId, conn);
					ArrayList<VisitorInfo> visitorList=new ArrayList<>();
					visitorList=DBM.allVisitorList(schId,sDate,eDate,conn);
					JSONArray arr=new JSONArray();

					for(VisitorInfo ll:visitorList)
					{
						JSONObject obj = new JSONObject();
						obj.put("sNo",ll.getsNo());
						obj.put("name",ll.getName());
						obj.put("mobileNo",ll.getMobileNo());
						obj.put("address",ll.getAddress());
						obj.put("id", ll.getId());
						obj.put("meetType", ll.getMeetType());
						obj.put("toMeetName", ll.getToMeet());
						if(ll.getOtherDetails()==null || ll.getOtherDetails().equalsIgnoreCase(""))
						{
							obj.put("otherDetails", "");
						}
						else
						{
							obj.put("otherDetails", ll.getOtherDetails());
						}
						obj.put("image", ll.getImagepath());
						obj.put("noOfPerson", ll.getNoOfPerson());
						obj.put("personName", ll.getPersonName());
						obj.put("in_time", ll.getInTime());
						obj.put("out_time", ll.getOutTime());
						obj.put("addDate",ll.getAddDateStr());
						obj.put("purpose",ll.getPurpose());
						obj.put("visitorNo",ll.getVisitorNo());
						arr.add(obj);
					}
					json=arr.toJSONString();

				}
				else if(actionName.equals("inList"))
				{
					String schId=params.get("schId");
					DBJ.fullSchoolInfo(schId, conn);
					ArrayList<VisitorInfo> visitorList=new ArrayList<>();
					visitorList=DBM.allInVisitorList(schId,conn);
					JSONArray arr=new JSONArray();

					for(VisitorInfo ll:visitorList)
					{
						JSONObject obj = new JSONObject();
						obj.put("sNo",ll.getsNo());
						obj.put("name",ll.getName());
						obj.put("mobileNo",ll.getMobileNo());
						obj.put("address",ll.getAddress());
						obj.put("id", ll.getId());
						obj.put("meetType", ll.getMeetType());
						obj.put("toMeetName", ll.getToMeet());
						if(ll.getOtherDetails()==null || ll.getOtherDetails().equalsIgnoreCase(""))
						{
							obj.put("otherDetails", "");
						}
						else
						{
							obj.put("otherDetails", ll.getOtherDetails());
						}

						obj.put("image", ll.getImagepath());
						obj.put("noOfPerson", ll.getNoOfPerson());
						obj.put("personName", ll.getPersonName());
						obj.put("in_time", ll.getInTime());
						obj.put("out_time", ll.getOutTime());
						obj.put("addDate",ll.getAddDateStr());
						obj.put("purpose",ll.getPurpose());
						obj.put("visitorNo",ll.getVisitorNo());
						arr.add(obj);
					}
					json=arr.toJSONString();

				}
				else
				{
					JSONArray arr=new JSONArray();
					JSONObject obj = new JSONObject();
					arr.add(obj);
					json=arr.toJSONString();
				}

			}
			else
			{
				JSONArray arr=new JSONArray();
				JSONObject obj = new JSONObject();
				arr.add(obj);
				json=arr.toJSONString();
			}
		}
		catch (Exception e) {
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
	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
