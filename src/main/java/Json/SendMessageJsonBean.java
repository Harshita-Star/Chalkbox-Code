package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name = "sendmessgejsonbean")
@ViewScoped
public class SendMessageJsonBean implements Serializable {

	public void run() {

	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	String json;
	String sectionid, message, schoolid, msgtype;
	Connection conn = DataBaseConnection.javaConnection();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseOnlineAdm objAdm = new DataBaseOnlineAdm();


	public SendMessageJsonBean() 
	{

		try {
			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();

			final StringBuffer ls = new StringBuffer();
			ls.append(params.get("mobile"));
			message = params.get("msg");
			params.get("type");
			final String sendType = params.get("sendtype");
			schoolid = params.get("Schoolid");
			msgtype = params.get("msgtype");
	    	message=message.replaceAll("Reagrds", "Regards");

			String msgStatus = "Sorry, Request from unknown source is prohibited";
			DataBaseMethodStudent objStd=new DataBaseMethodStudent();
			ArrayList<String> list=objStd.birthdayFieldList();
			String session=DatabaseMethods1.selectedSessionDetails(schoolid, conn);
			
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList schInfo = DBJ.fullSchoolInfo(schoolid, conn);
				msgtype="M";
				
				
				double balance =dbm.smsBalance(schoolid, conn);

				if(!schInfo.getCountry().equalsIgnoreCase("India"))
				{
					msgStatus = "Message Sent Successfully";
					Runnable r = new Runnable()
					{
						public void run()
						{
							String staticMsg = "";
							String mobileno = "";
							String addNumber = "";
							String sectionlist[] = ls.toString().split(",");
							//SchoolInfoList sinfo = DBJ.fullSchoolInfo(schoolid, conn);
							String schoolName = dbm.schoolNameById(schoolid, conn);
							for (int i = 0; i < sectionlist.length; i++)
							{
								
								StudentInfo ls = objStd.studentDetail(sectionlist[i], "","", QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schoolid, list, conn).get(0);

								if (message.contains("#name"))
								{
									String msg = message.replace("#name", ls.getFname());
									if (msgtype.equals("M"))
									{
										String heading = "<center class=\"red\">Message From "+schoolName+"!</center>";
										String subject = "Message From "+schoolName;
										String msgss="<center>Dear Parent,<br></br>"+msg+"</center>";
										if(ls.getActionBy().equalsIgnoreCase("father"))
										{
											objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
										}
										else if(ls.getActionBy().equalsIgnoreCase("mother"))
										{
											objAdm.doMail(ls.getMotherEmail(), subject, heading, msgss);
										}
										else if(ls.getActionBy().equalsIgnoreCase("both"))
										{
											if(ls.getFatherEmail().equalsIgnoreCase(ls.getMotherEmail()))
											{
												objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
											}
											else
											{
												objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
												objAdm.doMail(ls.getMotherEmail(), subject, heading, msgss);
											}
										}
										else
										{
											objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
										}
									}
									else if (msgtype.equals("N"))
									{
										if (sendType.equalsIgnoreCase("student"))
										{
											String msgss = "Dear Student, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
										else if (sendType.equalsIgnoreCase("parent"))
										{
											String msgss = "Dear Parent, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
										else if (sendType.equalsIgnoreCase("both"))
										{
											String msgss = "Dear Parent/Student, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}

									}
									else
									{
										if (sendType.equalsIgnoreCase("student"))
										{
											String msgss = "Dear Student, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
										else if (sendType.equalsIgnoreCase("parent"))
										{
											String msgss = "Dear Parent, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
										else if (sendType.equalsIgnoreCase("both"))
										{
											String msgss = "Dear Parent/Student, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}

										String heading = "<center class=\"red\">Message From "+schoolName+"!</center>";
										String subject = "Message From "+schoolName;
										String msgss="<center>Dear Parent,<br></br>"+msg+"</center>";
										if(ls.getActionBy().equalsIgnoreCase("father"))
										{
											objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
										}
										else if(ls.getActionBy().equalsIgnoreCase("mother"))
										{
											objAdm.doMail(ls.getMotherEmail(), subject, heading, msgss);
										}
										else if(ls.getActionBy().equalsIgnoreCase("both"))
										{
											if(ls.getFatherEmail().equalsIgnoreCase(ls.getMotherEmail()))
											{
												objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
											}
											else
											{
												objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
												objAdm.doMail(ls.getMotherEmail(), subject, heading, msgss);
											}
										}
										else
										{
											objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
										}
									}

								}
								else
								{
									if (msgtype.contains("M")) 
									{
										String heading = "<center class=\"red\">Message From "+schoolName+"!</center>";
										String subject = "Message From "+schoolName;
										String msgss="<center>Dear Parent,<br></br>"+message+"</center>";
										if(ls.getActionBy().equalsIgnoreCase("father"))
										{
											objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
										}
										else if(ls.getActionBy().equalsIgnoreCase("mother"))
										{
											objAdm.doMail(ls.getMotherEmail(), subject, heading, msgss);
										}
										else if(ls.getActionBy().equalsIgnoreCase("both"))
										{
											if(ls.getFatherEmail().equalsIgnoreCase(ls.getMotherEmail()))
											{
												objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
											}
											else
											{
												objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
												objAdm.doMail(ls.getMotherEmail(), subject, heading, msgss);
											}
										}
										else
										{
											objAdm.doMail(ls.getFatherEmail(), subject, heading, msgss);
										}
									}

									if (msgtype.contains("N")) 
									{
										if (sendType.equalsIgnoreCase("student")) {
											String msgsss = "Dear Student, \n" + message;
											DBJ.notification("Message", msgsss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										} else if (sendType.equalsIgnoreCase("parent")) {
											String msgsss = "Dear Parent, \n" + message;
											DBJ.notification("Message", msgsss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgsss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										} else if (sendType.equalsIgnoreCase("both")) {
											String msgsss = "Dear Parent/Student, \n"
													+ message;
											DBJ.notification("Message", msgsss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgsss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgsss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
									}
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
						Runnable r = new Runnable()
						{
							public void run()
							{
								String staticMsg = "";
								String mobileno = "";
								String addNumber = "";
								String sectionlist[] = ls.toString().split(",");
								//SchoolInfoList sinfo = DBJ.fullSchoolInfo(schoolid, conn);

								for (int i = 0; i < sectionlist.length; i++)
								{
									StudentInfo ls = objStd.studentDetail(sectionlist[i], "","", QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schoolid, list, conn).get(0);

									if (message.contains("#name"))
									{
										String msg = message.replace("#name", ls.getFname());
										if (msgtype.equals("M"))
										{
											if (sendType.equalsIgnoreCase("student"))
											{
												String msgss = "Dear Student, \n" + msg;
												DBJ.messageurl1(ls.getStudentPhone(), msgss,
														ls.getAddNumber(), schoolid, conn);
											}
											else if (sendType.equalsIgnoreCase("parent"))
											{
												String msgss = "Dear Parent, \n" + msg;
												DBJ.messageurl1(
														String.valueOf(ls.getFathersPhone()),
														msgss, ls.getAddNumber(), schoolid, conn);
											}
											else if (sendType.equalsIgnoreCase("both"))
											{
												String msgss = "Dear Parent/Student, \n" + msg;
												DBJ.messageurl1(
														String.valueOf(ls.getFathersPhone()),
														msgss, ls.getAddNumber(), schoolid, conn);
												DBJ.messageurl1(ls.getStudentPhone(), msgss,
														ls.getAddNumber(), schoolid, conn);
											}
										}
										else if (msgtype.equals("N"))
										{
											if (sendType.equalsIgnoreCase("student"))
											{
												String msgss = "Dear Student, \n" + msg;
												DBJ.notification("Message", msgss,
														ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											}
											else if (sendType.equalsIgnoreCase("parent"))
											{
												String msgss = "Dear Parent, \n" + msg;
												DBJ.notification("Message", msgss,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											}
											else if (sendType.equalsIgnoreCase("both"))
											{
												String msgss = "Dear Parent/Student, \n" + msg;
												DBJ.notification("Message", msgss,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											}

										}
										else
										{
											if (sendType.equalsIgnoreCase("student"))
											{
												String msgss = "Dear Student, \n" + msg;
												DBJ.messageurl1(ls.getStudentPhone(), msgss,
														addNumber, schoolid, conn);
												DBJ.notification("Message", msgss,
														ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											}
											else if (sendType.equalsIgnoreCase("parent"))
											{
												String msgss = "Dear Parent, \n" + msg;
												DBJ.messageurl1(
														String.valueOf(ls.getFathersPhone()),
														msgss, addNumber, schoolid, conn);
												DBJ.notification("Message", msgss,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											}
											else if (sendType.equalsIgnoreCase("both"))
											{
												String msgss = "Dear Parent/Student, \n" + msg;

												DBJ.messageurl1(
														String.valueOf(ls.getFathersPhone()),
														msgss, addNumber, schoolid, conn);
												DBJ.notification("Message", msgss,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.messageurl1(ls.getStudentPhone(), msgss,
														addNumber, schoolid, conn);
												DBJ.notification("Message", msgss,
														ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											}
										}
									}
									else
									{
										if (mobileno.equals(""))
										{
											if (sendType.equalsIgnoreCase("student"))
											{
												if(ls.getStudentPhone().length()==10
														&& !String.valueOf(ls.getStudentPhone()).equals("2222222222")
														&& !String.valueOf(ls.getStudentPhone()).equals("9999999999")
														&& !String.valueOf(ls.getStudentPhone()).equals("1111111111")
														&& !String.valueOf(ls.getStudentPhone()).equals("1234567890")
														&& !String.valueOf(ls.getStudentPhone()).equals("0123456789"))
												{
													addNumber = ls.getAddNumber();
													mobileno = ls.getStudentPhone();
												}
											}
											else if (sendType.equalsIgnoreCase("parent"))
											{
												if (String.valueOf(ls.getFathersPhone()).length() == 10
														&& !String.valueOf(ls.getFathersPhone()).equals("2222222222")
														&& !String.valueOf(ls.getFathersPhone()).equals("9999999999")
														&& !String.valueOf(ls.getFathersPhone()).equals("1111111111")
														&& !String.valueOf(ls.getFathersPhone()).equals("1234567890")
														&& !String.valueOf(ls.getFathersPhone()).equals("0123456789"))
												{
													addNumber = ls.getAddNumber();
													mobileno = String.valueOf(ls
															.getFathersPhone());
												}
											}
											else if (sendType.equalsIgnoreCase("both"))
											{
												if (String.valueOf(ls.getFathersPhone()).length() == 10
														&& !String.valueOf(ls.getFathersPhone()).equals("2222222222")
														&& !String.valueOf(ls.getFathersPhone()).equals("9999999999")
														&& !String.valueOf(ls.getFathersPhone()).equals("1111111111")
														&& !String.valueOf(ls.getFathersPhone()).equals("1234567890")
														&& !String.valueOf(ls.getFathersPhone()).equals("0123456789")) {
													addNumber = ls.getAddNumber();
													mobileno = String.valueOf(ls
															.getFathersPhone());
												}

												if (mobileno.equals("")) {
													if (ls.getStudentPhone().length()==10
															&& !String.valueOf(ls.getStudentPhone()).equals("2222222222")
															&& !String.valueOf(ls.getStudentPhone()).equals("9999999999")
															&& !String.valueOf(ls.getStudentPhone()).equals("1111111111")
															&& !String.valueOf(ls.getStudentPhone()).equals("1234567890")
															&& !String.valueOf(ls.getStudentPhone()).equals("0123456789")) {
														addNumber = ls.getAddNumber();
														mobileno = ls.getStudentPhone();
													}
												}
												else
												{
													if (ls.getStudentPhone().length()==10
															&& !String.valueOf(ls.getStudentPhone()).equals("2222222222")
															&& !String.valueOf(ls.getStudentPhone()).equals("9999999999")
															&& !String.valueOf(ls.getStudentPhone()).equals("1111111111")
															&& !String.valueOf(ls.getStudentPhone()).equals("1234567890")
															&& !String.valueOf(ls.getStudentPhone()).equals("0123456789")) {
														addNumber = addNumber + ","
																+ ls.getAddNumber();
														mobileno = mobileno + ","
																+ ls.getStudentPhone();
													}
												}

											}

										}
										else
										{
											if (sendType.equalsIgnoreCase("student"))
											{
												if (!mobileno.contains(ls.getStudentPhone())) {
													if (ls.getStudentPhone().length()==10
															&& !String.valueOf(ls.getStudentPhone()).equals("2222222222")
															&& !String.valueOf(ls.getStudentPhone()).equals("9999999999")
															&& !String.valueOf(ls.getStudentPhone()).equals("1111111111")
															&& !String.valueOf(ls.getStudentPhone()).equals("1234567890")
															&& !String.valueOf(ls.getStudentPhone()).equals("0123456789")) {
														addNumber = addNumber + ","
																+ ls.getAddNumber();
														mobileno = mobileno + ","
																+ ls.getStudentPhone();
													}
												}
											}
											else if (sendType.equalsIgnoreCase("parent"))
											{
												if (!mobileno.contains(String.valueOf(ls
														.getFathersPhone()))) {
													if (String.valueOf(ls.getFathersPhone()).length() == 10
															&& !String.valueOf(ls.getFathersPhone()).equals("2222222222")
															&& !String.valueOf(ls.getFathersPhone()).equals("9999999999")
															&& !String.valueOf(ls.getFathersPhone()).equals("1111111111")
															&& !String.valueOf(ls.getFathersPhone()).equals("1234567890")
															&& !String.valueOf(ls.getFathersPhone()).equals("0123456789")) {
														addNumber = addNumber + ","
																+ ls.getAddNumber();
														mobileno = mobileno
																+ ","
																+ String.valueOf(ls
																		.getFathersPhone());
													}
												}
											}
											else if (sendType.equalsIgnoreCase("both"))
											{
												if (!mobileno.contains(String.valueOf(ls
														.getFathersPhone()))) {
													if (String.valueOf(ls.getFathersPhone()).length() == 10
															&& !String.valueOf(ls.getFathersPhone()).equals("2222222222")
															&& !String.valueOf(ls.getFathersPhone()).equals("9999999999")
															&& !String.valueOf(ls.getFathersPhone()).equals("1111111111")
															&& !String.valueOf(ls.getFathersPhone()).equals("1234567890")
															&& !String.valueOf(ls.getFathersPhone()).equals("0123456789")) {
														addNumber = addNumber + ","
																+ ls.getAddNumber();
														mobileno = mobileno
																+ ","
																+ String.valueOf(ls
																		.getFathersPhone());
													}
												}

												if (!mobileno.contains(ls.getStudentPhone()))
												{
													if (ls.getStudentPhone().length()==10
															&& !String.valueOf(ls.getStudentPhone()).equals("2222222222")
															&& !String.valueOf(ls.getStudentPhone()).equals("9999999999")
															&& !String.valueOf(ls.getStudentPhone()).equals("1111111111")
															&& !String.valueOf(ls.getStudentPhone()).equals("1234567890")
															&& !String.valueOf(ls.getStudentPhone()).equals("0123456789")) {
														addNumber = addNumber + ","
																+ ls.getAddNumber();
														mobileno = mobileno + ","
																+ ls.getStudentPhone();
													}
												}
											}

											/*
											 * if(!mobileno.contains(String.valueOf(ls.
											 * getFathersPhone()))) {
											 * if(String.valueOf(ls.getFathersPhone
											 * ()).length()==10) {
											 * addNumber=addNumber+","+ls.getAddmisssionNumber
											 * (); mobileno=mobileno+","+String.valueOf(ls.
											 * getFathersPhone()); } }
											 */
										}

										if (msgtype.contains("N"))
										{
											if (sendType.equalsIgnoreCase("student")) {
												String msgss = "Dear Student, \n" + message;
												DBJ.notification("Message", msgss,
														ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											} else if (sendType.equalsIgnoreCase("parent")) {
												String msgss = "Dear Parent, \n" + message;
												DBJ.notification("Message", msgss,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											} else if (sendType.equalsIgnoreCase("both")) {
												String msgss = "Dear Parent/Student, \n"
														+ message;
												DBJ.notification("Message", msgss,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
												DBJ.notification("Message", msgss,
														ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
														schoolid,"", conn);
											}
										}
									}
								}

								if (!mobileno.equals("")) 
								{
									if (msgtype.contains("M")) 
									{
										if (sendType.equalsIgnoreCase("student")) {
											staticMsg = "Dear Student, \n" + message;
										} else if (sendType.equalsIgnoreCase("parent")) {
											staticMsg = "Dear Parent, \n" + message;
										} else if (sendType.equalsIgnoreCase("both")) {
											staticMsg = "Dear Parent/Student, \n" + message;
										}

										DBJ.messageurl1(mobileno, staticMsg, addNumber,
												schoolid, conn);
									}

								}

								

							}
							
							
							
						};

						new Thread(r).start();
					}
					else
					{
						msgStatus = "Dear User, You have consumed the SMS credits received with your license. Please recharge immediately to enjoy uninterrupted services.";

						if (msgtype.contains("N"))
						{
							Runnable r = new Runnable()
							{
								public void run()
								{
									String sectionlist[] = ls.toString().split(",");
									DBJ.fullSchoolInfo(schoolid, conn);

									for (int i = 0; i < sectionlist.length; i++)
									{
										StudentInfo ls = objStd.studentDetail(sectionlist[i], "","", QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schoolid, list, conn).get(0);
										String msg = "";
										if (message.contains("#name"))
										{
											msg = message.replace("#name", ls.getFname());
										}
										else
										{
											msg = message;
										}

										if (sendType.equalsIgnoreCase("student"))
										{
											String msgss = "Dear Student, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
										else if (sendType.equalsIgnoreCase("parent"))
										{
											String msgss = "Dear Parent, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
										else if (sendType.equalsIgnoreCase("both"))
										{
											String msgss = "Dear Parent/Student, \n" + msg;
											DBJ.notification("Message", msgss,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
											DBJ.notification("Message", msgss,
													ls.getStudentPhone()+"-"+ls.getAddNumber() + "-" + schoolid,
													schoolid,"", conn);
										}
									}
								}
							};
							new Thread(r).start();

							msgStatus = "Dear User, You have consumed the SMS credits received with your licence but NOTIFICATIONS SENT SUCCESSFULLY. Please contact administrator to renew SMS pack.";

						}
					}
				}
			}

			JSONArray arr = new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("msg", msgStatus);

			arr.add(obj);
			// mainobj.put("SchoolJson", arr);
			json = arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			/*try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
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
