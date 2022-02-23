package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="DeliveryReportBySchoolIdJson")
@ViewScoped
public class DeliveryReportBySchoolIdJson implements Serializable
{
	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	StudentInfo selectedStudent, selectedAss;
	public DeliveryReportBySchoolIdJson(){
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			/*String studentid = params.get("studentid");
	        String phoneno = params.get("phoneno");
			 */
			String schoolid=params.get("Schoolid");
			String type=params.get("type");
			String date=params.get("date");
			String endDate=params.get("endDate");

			ArrayList<DeliveryReport> list=new ArrayList<>();
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(date == null || date.equals(""))
				{

					list=DBJ.allDeliveryreport(schoolid, type, conn);
				}
				else
				{
					Date d=null;
					Date dend = null;
					try {
						d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
						if(endDate==null)
						{
							dend = new SimpleDateFormat("dd/MM/yyyy").parse(date);
							
						}
						else
						{
							dend = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
										
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					try {
						Calendar c = Calendar.getInstance(); 
						 c.setTime(dend); 
						 c.add(Calendar.DATE, 1);
						 dend = c.getTime();
					} catch (Exception e) {
						// TODO: handle exception
					}
					

					String strDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
					String endStrDate = new SimpleDateFormat("yyyy-MM-dd").format(dend);

					if(type.equalsIgnoreCase("student"))
					{
						type = "student','enq";
					}
					else if(type.equalsIgnoreCase("student','staff"))
					{
						type = "student','staff','enq";
					}
					//int i=new DatabaseMethods1(schoolid).updatePhoneNo(studentid,phoneno);
					list=DBJ.allDeliveryreportForDate(schoolid, type, conn, strDate,endStrDate);
				}

				for(DeliveryReport ls:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("batchid",ls.getBatchId() );
					obj.put("messageid", ls.getMessageid());

					/*int a=0;
						      if(ls.getMessage().length()>0 && ls.getMessage().length()<=160)
						      {
						    	a=1;
						      }
						      else if(ls.getMessage().length()>160 && ls.getMessage().length()<=306)
						      {
						    	 a=2;
						      }
						      else  if(ls.getMessage().length()>306 && ls.getMessage().length()<=459)
						      {
						    	 a=3;
						      }
						      else if(ls.getMessage().length()>459 && ls.getMessage().length()<=612)
						      {
						    	  a=4;
						      }
						      else
						      {
						    	  a=5;
						      }

							 int i=Integer.parseInt(ls.getCount())*a;*/

					obj.put("message", ls.getMessage());
					obj.put("count",ls.getCount());
					obj.put("messagecount",ls.getMcount());
					obj.put("date",ls.getDate());
					obj.put("type",ls.getType());

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
	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}




}
