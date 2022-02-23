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
import schooldata.Route;
import schooldata.RouteStop;
import schooldata.StudentInfo;

@ManagedBean(name="transportRouteJson")
@ViewScoped
public class TransportRouteJson implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public TransportRouteJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid = params.get("Schoolid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<Route> list=DBJ.allTransportRouteListGPS(conn, schid);

				for(Route ls:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("routeId",ls.getId());
					obj.put("routeName",ls.getName());
					JSONArray arr2=new JSONArray();

					for(RouteStop ls2:ls.getShowStopList())
					{
						JSONObject obj2 = new JSONObject();
						obj2.put("stopName",ls2.getStopName());
						obj2.put("stopId",ls2.getGroupid());
						obj2.put("rName",ls.getName()+"-"+ls2.getStopName());

						JSONArray arr3=new JSONArray();

						for(StudentInfo ls3:ls2.getList())
						{
							JSONObject obj3 = new JSONObject();

							obj3.put("Student_Name",ls3.getFullName());
							obj3.put("Fathers_Name", ls3.getFname());
							obj3.put("Fathers_Phone", ls3.getFathersPhone());
							obj3.put("Mothers_Phone", ls3.getMothersPhone());
							obj3.put("Student_Phone", ls3.getStudentPhone());
							obj3.put("className",ls3.getClassName()+"-"+ls3.getSectionName());
							obj3.put("Add_Number", ls3.getAddNumber());
							obj3.put("sr_no", ls3.getSrNo());
							obj3.put("stopId",ls2.getGroupid());
							obj3.put("rName",ls.getName()+"-"+ls2.getStopName());

							arr3.add(obj3);
						}

						obj2.put("Students", arr3);

						arr2.add(obj2);
					}

					obj.put("Stop", arr2);

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
