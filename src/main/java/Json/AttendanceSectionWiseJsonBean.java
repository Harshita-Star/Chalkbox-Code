package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="attendancestudent")
public class AttendanceSectionWiseJsonBean implements Serializable{

	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	String json;
	String data;
	Date d=null;

	Connection conn=DataBaseConnection.javaConnection();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public AttendanceSectionWiseJsonBean()
	{
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			data=params.get("data");

			final String sectionid=params.get("sections");
			final String schoolid=params.get("Schoolid");
			final String date=params.get("date");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			String sts = "Something Went Wrong";
			
			if(checkRequest)
			{
				try {
					d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
				} catch (ParseException e) {
					
					e.printStackTrace();
				}

				/*int n=DBJ.getAllAttendance(d,schoolid,conn);
						if(n==0)
						{
							ArrayList<ClassInfo>classSection=DBJ.allClassList(schoolid,"","Admin",conn);

							for(ClassInfo ls:classSection)
							{
								for(Category ls1:ls.getCategoryList())
								{
									 DBJ.attendanceSectionLogin(String.valueOf(ls1.getId()),ls1.getList(),d,schoolid,conn);
								}
							}

						}*/



				/* Runnable r = new Runnable() {
			         public void run() {
				 */
				String[] list=data.split(",");

				String[] sectionalll=sectionid.split(",");

				for(int j=0;j<sectionalll.length;j++)
				{
					DBJ.deleteclassAttendance(sectionalll[j], schoolid, d, conn);
					DBJ.addclassAttendance(sectionalll[j], d,schoolid ,conn);
				}

				if(!data.equals(""))
				{
					for(int i=0;i<list.length;i++)
					{
						String[] d1=list[i].split("~_~");
						DBJ.updateAttendance(d1[0],d1[1],d,schoolid,conn);
					}

				}

				sts = "Attendance Mark";
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();

				obj.put("msg",sts);

				arr.add(obj);

				mainobj.put("SchoolJson", arr);

				json=mainobj.toJSONString();
			}
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
