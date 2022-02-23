package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
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

@ManagedBean(name="absentStudentMessagejson")
@ViewScoped
public class AbsentStudentMessageJson implements Serializable{


	String json;
	String sectionid,message;
	String AddmissionNo,schoolid;
	Connection conn=DataBaseConnection.javaConnection();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public AbsentStudentMessageJson()
	{
		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			sectionid=params.get("mobile");
			AddmissionNo=params.get("addNumber");
			message=params.get("msg");
			schoolid=params.get("Schoolid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			String msg = "Something Went Wrong";
			
			JSONArray arr=new JSONArray();
			if(checkRequest)
			{
				msg = "Message Sent Successfully";
				Runnable r = new Runnable()
				{
					public void run()
					{
						try {
						SchoolInfoList info=DBJ.fullSchoolInfo(schoolid,conn);
						String dateee=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
						String[] add=AddmissionNo.split(",");
						for(int i=0;i<add.length;i++)
						{
							DBJ.addMessageStatus(add[i],new Date(), schoolid, conn);
		
							StudentInfo ss= DBJ.studentDetailslistByAddNo(add[i],schoolid,conn);
							String mess="Dear parent,\nYour Ward "+ss.getFname()+" is absent on date:"+dateee+" from school.\nRegards,\n"+ info.getSmsSchoolName();
							
							if(info.getCountry().equalsIgnoreCase("India"))
							{
								if (String.valueOf(ss.getFathersPhone()).length() == 10
										&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
										&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
										&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
										&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
										&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
								{
									String templateId=DBJ.templateId(info.getKey(),"ABSENT",conn);
									DBJ.messageUrlWithTemplate(String.valueOf(ss.getFathersPhone()), mess,add[i],schoolid,conn,templateId);
								}
							}
							if(ss.getFathersPhone()==ss.getMothersPhone())
							{
								DBJ.notification("ABSENT",mess,ss.getFathersPhone()+"-"+add[i]+"-"+schoolid,schoolid,"",conn);
							}
							else
							{
								DBJ.notification("ABSENT",mess,ss.getFathersPhone()+"-"+add[i]+"-"+schoolid,schoolid,"",conn);
								DBJ.notification("ABSENT",mess,ss.getMothersPhone()+"-"+add[i]+"-"+schoolid,schoolid,"",conn);
							}
							
						}
					}
					catch (Exception e5) 
					{
						e5.printStackTrace();
					}
					finally
					{
					  try {
						  conn.close();
						
					} catch (Exception e6) {
						
					}	
					}
				
					}
				};
		
				new Thread(r).start();

			}
			else
			{
				
			}



			JSONObject mainobj = new JSONObject();

			JSONObject obj = new JSONObject();



			obj.put("status",msg);

			arr.add(obj);


			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			
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
