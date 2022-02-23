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

import schooldata.DataBaseConnection;
import schooldata.RouteStop;
import schooldata.StudentInfo;

@ManagedBean(name="routeWiseStudentJson")
@ViewScoped

public class RouteWiseStudentJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	
	public RouteWiseStudentJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String schid = params.get("schid");
			String routeId = params.get("routeId");
			String date = params.get("date");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date newDate=null;
				try
				{
					newDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}

				String strDate = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
				
				/*final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
			    executorService.scheduleAtFixedRate(new Runnable() {
			        @Override
			        public void run() {
			        	fetchData(schid, routeId, strDate, conn);
			        }
			    }, 0, 1, TimeUnit.SECONDS);
				*/
				
				try
				{
				    Thread.sleep(3000);
				}
				catch(InterruptedException ex)
				{
				    Thread.currentThread().interrupt();
				}
				
				ArrayList<RouteStop> list = DBJ.routeWiseStopList(schid,routeId,strDate,conn);
				
				for(RouteStop ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("stopName",ss.getStopName());
					obj.put("stopId",ss.getGroupid());

					JSONArray arr2=new JSONArray();

					for(StudentInfo info:ss.getList())
					{
						JSONObject obj2 = new JSONObject();

						obj2.put("Student_Name",info.getFullName());
						obj2.put("Fathers_Name", info.getFname());
						obj2.put("Fathers_Phone", info.getFathersPhone());
						obj2.put("Mothers_Phone", info.getMothersPhone());
						obj2.put("Student_Phone", info.getStudentPhone());
						obj2.put("image", info.getStudent_image());
						obj2.put("className",info.getClassName()+"-"+info.getSectionName());
						obj2.put("Add_Number", info.getAddNumber());
						obj2.put("sr_no", info.getSrNo());
						obj2.put("stopId",ss.getGroupid());

						obj2.put("pickStatus", info.getPickdropInfo().getStudentPick());
						obj2.put("dropStatus", info.getPickdropInfo().getStudentDrop());
						obj2.put("pickTime", info.getPickdropInfo().getPickTime());
						obj2.put("dropTime", info.getPickdropInfo().getDropTime());

						obj2.put("schoolPickStatus", info.getPickdropInfo().getSchoolPick());
						obj2.put("schoolDropStatus", info.getPickdropInfo().getSchoolDrop());
						obj2.put("schoolPickTime", info.getPickdropInfo().getSchoolPickTime());
						obj2.put("schoolDropTime", info.getPickdropInfo().getSchoolDropTime());

						obj2.put("pickRemark", info.getPickdropInfo().getPickRemark());
						obj2.put("dropRemark", info.getPickdropInfo().getDropRemark());
						obj2.put("schoolPickRemark", info.getPickdropInfo().getSchoolPickRemark());
						obj2.put("schoolDropRemark", info.getPickdropInfo().getSchoolDropRemark());
						
						obj2.put("userid", info.getPickdropInfo().getUserid());
						obj2.put("actionBy", info.getPickdropInfo().getActionBy());

						arr2.add(obj2);

					}

					obj.put("Students", arr2);

					arr.add(obj);
				}
			}

			json=arr.toJSONString();

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
	
	/*public void fetchData(String schid,String routeId,String strDate,Connection conn)
	{
		ArrayList<RouteStop> list = DBJ.routeWiseStopList(schid,routeId,strDate,conn);
		JSONArray arr=new JSONArray();
		for(RouteStop ss:list)
		{
			JSONObject obj = new JSONObject();
			obj.put("stopName",ss.getStopName());
			obj.put("stopId",ss.getGroupid());

			JSONArray arr2=new JSONArray();

			for(StudentInfo info:ss.getList())
			{
				JSONObject obj2 = new JSONObject();

				obj2.put("Student_Name",info.getFullName());
				obj2.put("Fathers_Name", info.getFname());
				obj2.put("Fathers_Phone", info.getFathersPhone());
				obj2.put("Mothers_Phone", info.getMothersPhone());
				obj2.put("Student_Phone", info.getStudentPhone());
				obj2.put("image", info.getStudent_image());
				obj2.put("className",info.getClassName()+"-"+info.getSectionName());
				obj2.put("Add_Number", info.getAddNumber());
				obj2.put("sr_no", info.getSrNo());
				obj2.put("stopId",ss.getGroupid());

				obj2.put("pickStatus", info.getPickdropInfo().getStudentPick());
				obj2.put("dropStatus", info.getPickdropInfo().getStudentDrop());
				obj2.put("pickTime", info.getPickdropInfo().getPickTime());
				obj2.put("dropTime", info.getPickdropInfo().getDropTime());

				obj2.put("schoolPickStatus", info.getPickdropInfo().getSchoolPick());
				obj2.put("schoolDropStatus", info.getPickdropInfo().getSchoolDrop());
				obj2.put("schoolPickTime", info.getPickdropInfo().getSchoolPickTime());
				obj2.put("schoolDropTime", info.getPickdropInfo().getSchoolDropTime());

				obj2.put("pickRemark", info.getPickdropInfo().getPickRemark());
				obj2.put("dropRemark", info.getPickdropInfo().getDropRemark());
				obj2.put("schoolPickRemark", info.getPickdropInfo().getSchoolPickRemark());
				obj2.put("schoolDropRemark", info.getPickdropInfo().getSchoolDropRemark());

				arr2.add(obj2);

			}


			obj.put("Students", arr2);

			arr.add(obj);
		}

		//mainobj.put("SchoolJson", arr);

		json=arr.toJSONString();
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
