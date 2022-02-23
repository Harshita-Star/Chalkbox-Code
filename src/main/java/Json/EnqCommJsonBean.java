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

@ManagedBean(name="enqCommJson")
@ViewScoped

public class EnqCommJsonBean implements Serializable
{
	String json;
	String receiver,message,schoolid;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public EnqCommJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			receiver=params.get("receiver");
			message=params.get("msg");
			// emailMsg=params.get("msg");
			schoolid=params.get("schoolid");

			String sts = "Something Went Wrong";
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				
				sts="Message Sent Successfully";
				Runnable r = new Runnable()
				{
					public void run()
					{
						SchoolInfoList info = DBJ.fullSchoolInfo(schoolid, conn);
						String contactNo = "";
						String employee = "";
						String msg = "Dear Parent,\n" + message + "\nRegards,\n" + info.getSmsSchoolName();
						String recvArr[]=receiver.split(",");
						for(int i=0;i<recvArr.length;i++)
						{
							String[] list=recvArr[i].split("-");
							if(contactNo.equals(""))
							{
								if (list[0].length() == 10
										&& !list[0].equals("2222222222")
										&& !list[0].equals("9999999999")
										&& !list[0].equals("1111111111")
										&& !list[0].equals("1234567890")
										&& !list[0].equals("0123456789"))
								{
									contactNo=list[0];
									employee=list[1];
								}
							}
							else
							{
								if(!contactNo.contains(list[0]))
								{
									if (list[0].length() == 10
											&& !list[0].equals("2222222222")
											&& !list[0].equals("9999999999")
											&& !list[0].equals("1111111111")
											&& !list[0].equals("1234567890")
											&& !list[0].equals("0123456789"))
									{
										contactNo=contactNo+","+list[0];
										employee=employee+","+list[1];
									}
								}
							}

						}

						if (!contactNo.equals(""))
						{
							DBJ.messageurlenq(contactNo, msg, employee, schoolid, conn);
						}
						
					}
				};
				new Thread(r).start();
			}
			

			JSONArray arr = new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("msg", sts);

			arr.add(obj);
			// mainobj.put("SchoolJson", arr);
			json = arr.toJSONString();
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
