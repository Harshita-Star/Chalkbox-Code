package Json;

import java.io.IOException;
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
import schooldata.DataBaseMethodsHostelModule;
import schooldata.HealthCheckUpInfo;

@ManagedBean(name="studentHealthCheckupJson")
@ViewScoped

public class StudentHealthCheckupJsonBean
{
	String json;
	DataBaseMethodsHostelModule DBH=new DataBaseMethodsHostelModule();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public StudentHealthCheckupJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid=params.get("studentid");
			String schoolid=params.get("Schoolid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<HealthCheckUpInfo> healthList=DBH.viewAllHealthCheckUpDetailsStudentWise(conn, studentid, schoolid);

				String details = "";
				
				for(HealthCheckUpInfo ls:healthList)
				{
					details="";
					JSONObject obj = new JSONObject();
					obj.put("checkupDate",ls.getCheckUpDate());

					obj.put("checkupTime",ls.getTimevalue());
					obj.put("detailLabel","Reason of visit,Treatment advice,Remark,Height,Weight,B.P.,RBC,WBC,Haemoglobin");

					if(ls.getDisease().equals(""))
					{
						details="N/A";
					}
					else
					{
						details=ls.getDisease();
					}

					if(ls.getTreatment().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getTreatment();
					}

					if(ls.getRemarks().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getRemarks();
					}

					if(ls.getHeight().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getHeight();
					}

					if(ls.getWeight().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getWeight();
					}

					if(ls.getBloodPressure().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getBloodPressure();
					}

					if(ls.getRbc().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getRbc();
					}

					if(ls.getWbc().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getWbc();
					}
					if(ls.getHemoglobin().equals(""))
					{
						details=details+",N/A";
					}
					else
					{
						details=details+","+ls.getHemoglobin();
					}


					obj.put("detailValue",details);
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
