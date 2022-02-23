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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="staffsend")
@ViewScoped
public class StaffMessagesendJson implements Serializable{

	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	String json;
	String sectionid,message,msgtype;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DataBaseOnlineAdm objAdm= new DataBaseOnlineAdm();


	public StaffMessagesendJson()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			sectionid=params.get("mobile");
			message=params.get("msg");
			msgtype=params.get("msgtype");
			// emailMsg=params.get("msg");
			String status=params.get("status");
			final String schoolid=params.get("Schoolid");
			SchoolInfoList info = DBJ.fullSchoolInfo(schoolid, conn);
			String msgStatus = "Sorry, Request from unknown source is prohibited";
			
			message=message.replaceAll("Reagrds", "Regards");

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				double balance = new DatabaseMethods1().smsBalance(schoolid, conn);

				if(msgtype==null || msgtype.equalsIgnoreCase(""))
				{
					msgtype = "M";
				}

				if(status.equals("static"))
				{
					String contactNo="";
					String employee="";
					if(sectionid==null||sectionid.equals(""))
					{

					}
					else
					{

						if(message.contains("Dear Staff Member,"))
						{
							message.replace("Dear Staff Member,", "");
						}

						if(msgtype.contains("N"))
						{
							msgStatus = "Message Sent Successfully";
							String msgs="Dear Staff Member,\n"+message;
							String emp[]=sectionid.split("-");
							String id = "";
							for(int i=0;i<emp.length;i++)
							{
								String[] list=emp[i].split(",");
								try
								{
									id=list[0].split("@CB@")[1];
								}
								catch (Exception e)
								{
									id = "no";
								}

								if(!id.equalsIgnoreCase("no"))
								{
									DBJ.adminnotification("Message",msgs,"staff"+"-"+id+"-"+schoolid,schoolid,"StaffNotification-"+id,conn);
								}
							}
						}

						if(msgtype.contains("M"))
						{
							String emp[]=sectionid.split("-");
							if(!info.getCountry().equalsIgnoreCase("India"))
							{
								msgStatus = "Message Sent Successfully";
								String tp = message;
								Runnable r = new Runnable()
								{
									public void run()
									{
										String heading =  "<center class=\"red\">Message From "+info.getSchoolName()+"!</center>";
										String subject = "Message From "+info.getSchoolName();
										String msg = "<center>Dear Staff Member,<br></br>"+tp+"</center>";
										//String email = "";
										for(int i=0;i<emp.length;i++)
										{
											String[] list=emp[i].split(",");
											//	    		    				 	employee=list[0];
											//	    		    				 	email=list[1];
											if(list[1]!=null && !list[1].equalsIgnoreCase("") && !list[1].equalsIgnoreCase("null"))
											{
												objAdm.doMail(list[1], subject, heading, msg);
											}
										}


									}
								};
								new Thread(r).start();
							}
							else
							{
								message="Dear Staff Member,\n"+message;

								if(balance>0)
								{
									msgStatus = "Message Sent Successfully";
									for(int i=0;i<emp.length;i++)
									{
										String[] list=emp[i].split(",");
										if(contactNo.equals(""))
										{
											if (list[1].length() == 10
													&& !list[1].equals("2222222222")
													&& !list[1].equals("9999999999")
													&& !list[1].equals("1111111111")
													&& !list[1].equals("1234567890")
													&& !list[1].equals("0123456789"))
											{
												employee=list[0];
												contactNo=list[1];
											}
										}
										else
										{
											if(!contactNo.contains(list[1]))
											{
												if (list[1].length() == 10
														&& !list[1].equals("2222222222")
														&& !list[1].equals("9999999999")
														&& !list[1].equals("1111111111")
														&& !list[1].equals("1234567890")
														&& !list[1].equals("0123456789"))
												{
													employee=employee+","+list[0];
													contactNo=contactNo+","+list[1];
												}
											}
										}

									}

									//DBJ.messageurlsatff(contactNo,message,employee,schoolid,conn);
									DBJ.messageurlStaff(contactNo, message, employee, conn, schoolid);
								}
								else
								{
									msgStatus = "Dear User, You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";
									if(msgtype.contains("N"))
									{
										msgStatus = "Dear User, You have consumed the SMS credits received with your licence but NOTIFICATIONS SENT SUCCESSFULLY. Please contact administrator to renew SMS pack.";
									}
								}
							}
						}
					}
				}
				else
				{

					if(sectionid==null||sectionid.equals(""))
					{

					}
					else
					{
						//text.replaceAll("\\r|\\n", "");

						if(msgtype.contains("N"))
						{
							msgStatus = "Message Sent Successfully";
							String msgs="";
							String emp[]=sectionid.split("-");
							String id = "";
							for(int i=0;i<emp.length;i++)
							{
								String[] list=emp[i].split(",");
								try
								{
									msgs=message.replaceAll("#name",list[0].split("@CB@")[0]);
									msgs="Dear Staff Member,\n"+msgs;
									id=list[0].split("@CB@")[1];

									DBJ.adminnotification("Message",msgs,"staff"+"-"+id+"-"+schoolid,schoolid,"StaffNotification-"+id,conn);

								}
								catch (Exception e)
								{

								}
							}
						}

						if(msgtype.contains("M"))
						{
							String emp[]=sectionid.split("-");

							if(!info.getCountry().equalsIgnoreCase("India"))
							{
								msgStatus = "Message Sent Successfully";
								String tp = message;
								Runnable r = new Runnable()
								{
									public void run()
									{
										String heading =  "<center class=\"red\">Message From "+info.getSchoolName()+"!</center>";
										String subject = "Message From "+info.getSchoolName();
										//String msg = "<center>Dear Staff Member,<br></br>"+emailMsg;
										//String email = "";
										for(int i=0;i<emp.length;i++)
										{
											String[] list=emp[i].split(",");
											//		 			    				 	employee=list[0];
											//		 			    				 	email=list[1];
											String msg=tp.replaceAll("#name",list[0].split("@CB@")[0]);
											msg="<center>Dear Staff Member,<br></br>"+msg+"</center>";
											if(list[1]!=null && !list[1].equalsIgnoreCase("") && !list[1].equalsIgnoreCase("null"))
											{
												objAdm.doMail(list[1], subject, heading, msg);
											}
										}


									}
								};
								new Thread(r).start();
							}
							else
							{
								if(balance>0)
								{
									msgStatus = "Message Sent Successfully";
									for(int i=0;i<emp.length;i++)
									{
										String[] list=emp[i].split(",");
										String msg=message.replaceAll("#name",list[0].split("@CB@")[0]);
										msg="Dear Staff Member,\n"+msg;
										if (list[1].length() == 10
												&& !list[1].equals("2222222222")
												&& !list[1].equals("9999999999")
												&& !list[1].equals("1111111111")
												&& !list[1].equals("1234567890")
												&& !list[1].equals("0123456789"))
										{
											//new DatabaseMethods1(schoolid).messageurl1(list[1],message,"",conn);
											//DBJ.messageurlsatff(list[1],message,list[0],schoolid,conn);
											DBJ.messageurlStaff(list[1], msg, list[0], conn, schoolid);
										}
									}
								}
								else
								{
									msgStatus = "Dear User, You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";
									if(msgtype.contains("N"))
									{
										msgStatus = "Dear User, You have consumed the SMS credits received with your licence but NOTIFICATIONS SENT SUCCESSFULLY. Please contact administrator to renew SMS pack.";
									}
								}
							}
						}
					}
				}
			}

			json=msgStatus;
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
