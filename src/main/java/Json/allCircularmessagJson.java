package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.NotificationInfo;
import schooldata.SchoolInfoList;
@ManagedBean(name="allcircularmessage")
@ViewScoped
public class allCircularmessagJson implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 obj=new DatabaseMethods1();



	public allCircularmessagJson() {

		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String limit=params.get("limit");
			String startingpoint=params.get("startpoint");
			String schoolid=params.get("Schoolid");

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proce
			JSONArray arr=new JSONArray();
			
			String msg = "";
			
			if(true)
			{
				ArrayList<NotificationInfo> info=DBJ.allMessageforapp(studentid,limit,startingpoint,schoolid,conn);
				
				SchoolInfoList ls=DBJ.fullSchoolInfo(schoolid,conn);
				for(NotificationInfo ss:info)
				{
					/*Date ss1=null;
					    	 try {
								 ss1=new SimpleDateFormat("dd-MM-yyyy").parse(ss.getStrUdate());

						      } catch (ParseException e) {
								
								e.printStackTrace();
							}*/
					JSONObject obj = new JSONObject();
					obj.put("Date",new SimpleDateFormat("dd-MM-yyyy").format(ss.getDate()));
					obj.put("id", ss.getId());
					msg = ss.getMessage();
					if(studentid==null || studentid.equalsIgnoreCase(""))
					{
						if(ss.getClassName()==null || ss.getClassName().equalsIgnoreCase(""))
						{
							//msg = msg;
						}
						else if(ss.getClassName().equalsIgnoreCase("All"))
						{
							msg = msg + "\n\n" + "For All Classes";
						}
						else
						{
							msg = msg + "\n\n" + "For Class "+ss.getClassName()+" - "+ss.getSectionName();
						}
					}
					else
					{

					}


					obj.put("message", msg);
					if(ss.getFilename()==null || ss.getFilename().equals(""))
					{
						obj.put("file","");

					}
					else
					{
						obj.put("file",ls.getDownloadpath()+ss.getFilename());

					}
					arr.add(obj);
				}
			}
			//mainobj.put("SchoolJson", arr);

			json=arr.toJSONString();	
		} catch (Exception e) {
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
