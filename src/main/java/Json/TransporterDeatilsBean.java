package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="transporterDeatils")
@ViewScoped
public class TransporterDeatilsBean implements Serializable
{
	String json;

	StudentInfo list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public TransporterDeatilsBean() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schoolid=params.get("Schoolid");
			/* String limit=params.get("limit");
	        String startingpoint=params.get("startpoint");
			 */

			//   Date newDate=null;
			/*  try {
			//newDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			 */
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList ls=DBJ.fullSchoolInfo(schoolid, conn);

				list=DBJ.studentDetailslistByAddNo(studentid,schoolid,conn);

				JSONObject obj = new JSONObject();
				if(list.getVehicleNo()==null || list.getVehicleNo().equals(""))
				{
					obj.put("Vechile_Number", "");
				}
				else
				{
					obj.put("Vechile_Number", list.getVehicleNo());
				}

				if(list.getDriverName()==null || list.getDriverName().equals(""))
				{
					obj.put("driver_Name","");
				}
				else
				{
					obj.put("driver_Name",list.getDriverName());
				}

				if(list.getDriverNo()==null || list.getDriverNo().equals(""))
				{
					obj.put("driver_number","");
				}
				else
				{
					obj.put("driver_number",list.getDriverNo());
				}

				if(list.getConductorName()==null || list.getConductorName().equals(""))
				{
					obj.put("conductor_Name","");
				}
				else
				{
					obj.put("conductor_Name",list.getConductorName());
				}

				if(list.getConductorNo()==null || list.getConductorNo().equals(""))
				{
					obj.put("conductor_number","");
				}
				else
				{
					obj.put("conductor_number",list.getConductorNo());
				}

				if(list.getAttendantName()==null || list.getAttendantName().equals(""))
				{
					obj.put("attendant_Name","");
				}
				else
				{
					obj.put("attendant_Name",list.getAttendantName());
				}

				if(list.getAttendNo()==null || list.getAttendNo().equals(""))
				{
					obj.put("attendant_number","");
				}
				else
				{
					obj.put("attendant_number",list.getAttendNo());
				}

				if(list.getTransportRoute()==null || list.getTransportRoute().equals(""))
				{
					obj.put("transport","");
				}
				else
				{
					obj.put("transport",list.getTransportRoute());
				}

				if(list.getShowDriver()==null || list.getShowDriver().equals(""))
				{
					obj.put("showDriver","");
				}
				else
				{
					obj.put("showDriver", list.getShowDriver());
				}

				if(list.getShowConductor()==null || list.getShowConductor().equals(""))
				{
					obj.put("showConductor", "");

				}
				else
				{
					obj.put("showConductor", list.getShowConductor());
				}

				if(list.getShowAttendant()==null || list.getShowAttendant().equals(""))
				{
					obj.put("showAttendant", "");	 }
				else
				{
					obj.put("showAttendant", list.getShowAttendant());
				}


				if(list.getEmpImageAttendant()==null || list.getEmpImageAttendant().equals(""))
				{
					obj.put("imageAttendant", "");	 }
				else
				{
					obj.put("imageAttendant", list.getEmpImageAttendant());
				}

				if(list.getEmpImageConductor()==null || list.getEmpImageConductor().equals(""))
				{
					obj.put("imageConducator", "");	 }
				else
				{
					obj.put("imageConducator", list.getEmpImageConductor());
				}

				if(list.getEmpImageDriver()==null || list.getEmpImageDriver().equals(""))
				{
					obj.put("imageDriver", "");	 }
				else
				{
					obj.put("imageDriver", list.getEmpImageDriver());
				}

				obj.put("url",ls.getDownloadpath());

				if(list.getGpsImei()==null||list.getGpsImei().equals(""))
				{
					obj.put("gpsImei","");
				}
				else
				{
					obj.put("gpsImei",list.getGpsImei());
				}

				if(list.getGpsPwd()==null||list.getGpsPwd().equals(""))
				{
					obj.put("gpsPassword","");
				}
				else
				{
					obj.put("gpsPassword",list.getGpsPwd());
				}
				
				obj.put("gpsProvider", ls.getGpsProvider());

				arr.add(obj);
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}




}
