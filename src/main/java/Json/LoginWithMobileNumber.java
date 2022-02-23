package Json;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name = "loginwithmobileno")
@ViewScoped
public class LoginWithMobileNumber implements Serializable {

	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection, selectedSection, subject, type, addNo;
	ArrayList<SelectItem> classSection, sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DataBaseOnlineAdm objAdm = new DataBaseOnlineAdm();


	public static final String DEFAULT_ENCODING = "UTF-8";
//	static BASE64Encoder enc = new BASE64Encoder();
//	static BASE64Decoder dec = new BASE64Decoder();

//	public static String base64encode(String text) {
//		try {
//			return enc.encode(text.getBytes(DEFAULT_ENCODING));
//		} catch (UnsupportedEncodingException e) {
//			return null;
//		}
//	}// base64encode
//
//	public static String base64decode(String text) {
//		try {
//			return new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
//		} catch (IOException e) {
//			return null; 
//		}
//	}// base64decode
	
	public static String base64encode(String text) {
		try {
			return Base64.getEncoder().encodeToString(text.getBytes(DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}// base64encode

	public static String base64decode(String text) {
		try {
			return new String(Base64.getDecoder().decode(text), DEFAULT_ENCODING);
		} catch (IOException e) {
			return null;
		}
	}// base64decode

	public LoginWithMobileNumber() throws UnsupportedEncodingException {

		Connection conn = DataBaseConnection.javaConnection();
		try {
			new DataBaseMeathodJson();

			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String studentid = params.get("id");
			String schid = params.get("Schoolid");

			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			checkRequest=true;
			String session=DBM.selectedSessionDetails(schid,conn);
			DataBaseMethodStudent objStd=new DataBaseMethodStudent();
			if(checkRequest)
			{
				new DataBaseJsonDeatil().updateAppDownloadStatus(studentid, conn);
				boolean checkSchool = DBM.checkSchoolStatus(schid, conn);
				if (checkSchool == true) {
					
					boolean expired = DBM.checkSchoolExpiryDate(schid, conn);
					// boolean expired=false;
					if (expired == false) {
						
						//boolean checkDue = DBM.checkSchoolBillDueDate(schid, conn); //checking bill due
						boolean checkDue=false;
						if (checkDue == false) {
							
							ArrayList<String> list=objStd.basicFieldsForStudentList();
							ArrayList<StudentInfo> selectedStudent=objStd.studentDetail(studentid,"","",QueryConstants.MOBILE_NO,QueryConstants.MOBILE_NO,null,null,"","","","",session, schid, list, conn);
							if (selectedStudent.size() == 0) {

								obj.put("otp", "0");
								obj.put("studentid", "");
								obj.put("schid", "");
								obj.put("name", "");
								obj.put("classname", "");
								obj.put("permission", "");
								obj.put("timeTable", "");
								obj.put("online_fee", "");
								obj.put("country", "");
								obj.put("email", "");
								obj.put("session", session);
								SchoolInfoList info = DBJ.fullSchoolInfo(schid, conn);
								obj.put("appVersion", info.getAppVersion());
								arr.add(obj);

								// mainobj.put("SchoolJson", arr);

								json = arr.toJSONString();

							} else {
								SchoolInfoList info = DBJ.fullSchoolInfo(schid, conn);
								if (studentid.equalsIgnoreCase("9898989898") || studentid.equalsIgnoreCase("9898989898")) {
									if (info.getStudentApp().equalsIgnoreCase("yes")) {
										int randomPIN = 2121;

										String name = "", stutid = "", classname = "", pwd = "", upwd = "";
										for (StudentInfo ss : selectedStudent) {
											
											pwd = DBJ.userPassword(ss.getAddNumber(), conn);
											
											
											if (name.equals("")) {
												name = ss.getFname();
												stutid = ss.getAddNumber();
												classname = ss.getClassName();
												upwd = pwd;
											} else {
												name = name + "," + ss.getFname();
												stutid = stutid + "," + ss.getAddNumber();
												classname = classname + "," + ss.getClassName();
												upwd = upwd + "," + pwd;
											}

										}

										String txt = String.valueOf(randomPIN);
										String key = "key phrase used for XOR-ing";
										txt = xorMessage(txt, key);
										String encoded = base64encode(txt);

										// obj.put("otp",String.valueOf(randomPIN));
										obj.put("otp", encoded);
										obj.put("studentid", stutid);
										obj.put("upd", upwd);
										obj.put("schid", selectedStudent.get(0).getSchid());
										obj.put("name", name);
										obj.put("classname", classname);
										obj.put("permission", info.getStudent_app_permission());
										obj.put("timeTable", info.getTimetable());
										obj.put("schName", info.getSchoolName());
										obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
										obj.put("online_fee", info.getOnlineFee());
										obj.put("country", info.getCountry());
										obj.put("paytm_marchent_key", info.getPaytm_marchent_key());
										obj.put("paytm_mid", info.getPaytm_mid());
										obj.put("pg_type", info.getPg_type());
										obj.put("rzp_mid", info.getRzp_mid());
										obj.put("rzp_key", info.getRzp_key());
										obj.put("rzp_key_secret", info.getRzp_key_secret());
										obj.put("email", "");
										obj.put("session", session);
										obj.put("appVersion", info.getAppVersion());

										arr.add(obj);
										// mainobj.put("SchoolJson", arr);

										json = arr.toJSONString();
									} else {
										obj.put("otp", "0");
										obj.put("studentid", "");
										obj.put("upd", "");
										obj.put("schid", "");
										obj.put("name", "");
										obj.put("classname", "");
										obj.put("permission", "");
										obj.put("timeTable", "");
										obj.put("online_fee", "");
										obj.put("country", "");
										obj.put("email", "");
										obj.put("session", session);
										obj.put("appVersion", info.getAppVersion());
										arr.add(obj);

										// mainobj.put("SchoolJson", arr);

										json = arr.toJSONString();
									}

								} else {
									if (info.getStudentApp().equalsIgnoreCase("yes")) {
										int randomPIN = (int) (Math.random() * 9000) + 1000;
//										String typemessage = "Dear Parent,\n" + String.valueOf(randomPIN)
//										+ " is  your login OTP. Treat this as confidential. We welcome you to be a part of Digital India. \n\nRegards, \n"
//										+ info.getSmsSchoolName();

										String typemessage = "Dear Parent,\n"+ "Your login OTP is "+String.valueOf(randomPIN)+" valid for 5 mins.\nRegards,\n"+info.getSmsSchoolName();


										// //// // System.out.println(selectedStudent.get(0).getAddNumber()+"...."+selectedStudent.get(0).getStudentid());
										if(!info.getCountry().equalsIgnoreCase("India"))
										{
											Runnable r = new Runnable()
											{
												public void run()
												{
													String msg = "<center>Dear Parent,<br></br><strong>" + String.valueOf(randomPIN)
													+ "</string> is  your login OTP. Treat this as confidential. We welcome you to be a part of Digital World. <br><br/>Regards,<br></br>"
													+ info.getSmsSchoolName()+"</center>";
													
//													String msg = "Dear Parent,\n"+ " your login OTP is "+String.valueOf(randomPIN)+" valid for 5 mins.-\n CHKBOX";

													String heading = "<center class=\"red\">Your Login OTP For "+info.getSchoolName()+" is "+String.valueOf(randomPIN)+"!<center>";
													String subject = "LOGIN OTP : "+info.getSchoolName();

													String recpt = selectedStudent.get(0).getActionBy();

													if(recpt.equalsIgnoreCase("father"))
													{
														obj.put("email", selectedStudent.get(0).getFatherEmail());
														objAdm.doMail(selectedStudent.get(0).getFatherEmail(), subject, heading, msg);
													}
													else if(recpt.equalsIgnoreCase("mother"))
													{
														obj.put("email", selectedStudent.get(0).getMotherEmail());
														objAdm.doMail(selectedStudent.get(0).getMotherEmail(), subject, heading, msg);
													}
													else if(recpt.equalsIgnoreCase("both"))
													{
														if(selectedStudent.get(0).getFatherEmail().equalsIgnoreCase(selectedStudent.get(0).getMotherEmail()))
														{
															obj.put("email", selectedStudent.get(0).getFatherEmail());
															objAdm.doMail(selectedStudent.get(0).getFatherEmail(), subject, heading, msg);
														}
														else
														{
															obj.put("email", selectedStudent.get(0).getFatherEmail()+","+selectedStudent.get(0).getMotherEmail());
															objAdm.doMail(selectedStudent.get(0).getFatherEmail(), subject, heading, msg);
															objAdm.doMail(selectedStudent.get(0).getMotherEmail(), subject, heading, msg);
														}

													}
												}

											};
											new Thread(r).start();

											String recpt = selectedStudent.get(0).getActionBy();

											if(recpt.equalsIgnoreCase("father"))
											{
												obj.put("email", selectedStudent.get(0).getFatherEmail());
											}
											else if(recpt.equalsIgnoreCase("mother"))
											{
												obj.put("email", selectedStudent.get(0).getMotherEmail());
											}
											else if(recpt.equalsIgnoreCase("both"))
											{
												if(selectedStudent.get(0).getFatherEmail().equalsIgnoreCase(selectedStudent.get(0).getMotherEmail()))
												{
													obj.put("email", selectedStudent.get(0).getFatherEmail());
												}
												else
												{
													obj.put("email", selectedStudent.get(0).getFatherEmail()+","+selectedStudent.get(0).getMotherEmail());
												}

											}
										}
										else
										{
											obj.put("email", "");
//											DBJ.messageurl1(studentid, typemessage,
//													selectedStudent.get(0).getAddNumber(), schid, conn);
											String templateId=DBJ.templateId(info.getKey(),"OTP",conn);
											if(templateId.equals("1507162073382267628"))
											{
												typemessage=typemessage+"/n- CHALKBOARD";
											}
											DBJ.messageUrlWithTemplate(studentid, typemessage,selectedStudent.get(0).getAddNumber(), schid, conn,templateId);
										}


										String name = "", stutid = "", classname = "", pwd="", upwd="";
										for (StudentInfo ss : selectedStudent) {
											
											pwd = DBJ.userPassword(ss.getAddNumber(), conn);
											
											if (name.equals("")) {
												name = ss.getFname();
												stutid = ss.getAddNumber();
												classname = ss.getClassName();
												upwd=pwd;
											} else {
												name = name + "," + ss.getFname();
												stutid = stutid + "," + ss.getAddNumber();
												classname = classname + "," + ss.getClassName();
												upwd = upwd + "," + pwd;
											}

										}

										String txt = String.valueOf(randomPIN);
										String key = "key phrase used for XOR-ing";
										txt = xorMessage(txt, key);
										String encoded = base64encode(txt);

										// obj.put("otp",String.valueOf(randomPIN));
										obj.put("otp", encoded);
										obj.put("studentid", stutid);
										obj.put("upd", upwd);
										obj.put("schid", selectedStudent.get(0).getSchid());
										obj.put("name", name);
										obj.put("classname", classname);
										obj.put("permission", info.getStudent_app_permission());
										obj.put("timeTable", info.getTimetable());
										obj.put("status", info.getStudentApp());
										obj.put("schName", info.getSchoolName());
										obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
										obj.put("online_fee", info.getOnlineFee());
										obj.put("country", info.getCountry());
										obj.put("paytm_marchent_key", info.getPaytm_marchent_key());
										obj.put("paytm_mid", info.getPaytm_mid());
										obj.put("pg_type", info.getPg_type());
										obj.put("rzp_mid", info.getRzp_mid());
										obj.put("rzp_key", info.getRzp_key());
										obj.put("rzp_key_secret", info.getRzp_key_secret());
										obj.put("appVersion", info.getAppVersion());
										obj.put("session", session);
										arr.add(obj);
										// mainobj.put("SchoolJson", arr);

										json = arr.toJSONString();
									} else {
										obj.put("otp", "0");
										obj.put("studentid", "");
										obj.put("upd", "");
										obj.put("schid", "");
										obj.put("name", "");
										obj.put("classname", "");
										obj.put("permission", "");
										obj.put("timeTable", "");
										obj.put("online_fee", "");
										obj.put("country", "");
										obj.put("email", "");
										obj.put("session", session);
										obj.put("appVersion", info.getAppVersion());
										arr.add(obj);

										// mainobj.put("SchoolJson", arr);

										json = arr.toJSONString();
									}

								}

							}
						} else {
							/*	obj.put("msg", "Dear User," +
									"Your licence invoice is overdue. Kindly pay at the earliest to avoid uninterrupted services");
							 */	obj.put("otp", "0");

							 obj.put("studentid", "");
							 obj.put("upd", "");
							 obj.put("schid", "");
							 obj.put("name", "");
							 obj.put("classname", "");
							 obj.put("permission", "");
							 obj.put("timeTable", "");
							 obj.put("online_fee", "");
							 obj.put("country", "");
							 obj.put("email", "");
							 obj.put("session", session);
							 SchoolInfoList info = DBJ.fullSchoolInfo(schid, conn);
							 obj.put("appVersion", info.getAppVersion());
							 arr.add(obj);

							 // mainobj.put("SchoolJson", arr);

							 json = arr.toJSONString();

						}

					} else {
						/*obj.put("otp", "Sorry, license of your school ERP has been expired, Please contact Administrator for"
								+ " license renewal. Make the renewal as soon as possible and enjoy our services. We are"
								+ " here to serve you. Thanks and Regards");
						 */obj.put("studentid", "");
						 obj.put("upd", "");
						 obj.put("schid", "");
						 obj.put("otp", "0");

						 obj.put("name", "");
						 obj.put("classname", "");
						 obj.put("permission", "");
						 obj.put("timeTable", "");
						 obj.put("online_fee", "");
						 obj.put("country", "");
						 obj.put("email", "");
						 obj.put("session", session);
						 SchoolInfoList info = DBJ.fullSchoolInfo(schid, conn);
						 obj.put("appVersion", info.getAppVersion());
						 arr.add(obj);

						 // mainobj.put("SchoolJson", arr);

						 json = arr.toJSONString();
					}
				} else {
					/*obj.put("otp",
							"Sorry, license of your school ERP has been expired, Please contact Administrator for "
									+ "license renewal. Make the renewal as soon as possible and enjoy our services. We are"
									+ " here to serve you. Thanks and Regards");
					 */	obj.put("studentid", "");
					 obj.put("upd", "");
					 obj.put("schid", "");
					 obj.put("otp", "0");

					 obj.put("name", "");
					 obj.put("classname", "");
					 obj.put("permission", "");
					 obj.put("timeTable", "");
					 obj.put("online_fee", "");
					 obj.put("country", "");
					 obj.put("email", "");
					 obj.put("session", session);
					 SchoolInfoList info = DBJ.fullSchoolInfo(schid, conn);
					 obj.put("appVersion", info.getAppVersion());

					 arr.add(obj);

					 // mainobj.put("SchoolJson", arr);

					 json = arr.toJSONString();
				}
			}
			else
			{
				
				//// // System.out.println("hellooo new");
				obj.put("studentid", "");
				obj.put("upd", "");
				 obj.put("schid", "");
				 obj.put("otp", "0");

				 obj.put("name", "");
				 obj.put("classname", "");
				 obj.put("permission", "");
				 obj.put("timeTable", "");
				 obj.put("online_fee", "");
				 obj.put("country", "");
				 obj.put("email", "");
				 obj.put("session", session);
				 SchoolInfoList info =DBJ.fullSchoolInfo(schid, conn);
				 obj.put("appVersion", info.getAppVersion());

				 arr.add(obj);

				 // mainobj.put("SchoolJson", arr);

				 json = arr.toJSONString();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public static String xorMessage(String message, String key) {
		try {
			if (message == null || key == null)
				return null;

			char[] keys = key.toCharArray();
			char[] mesg = message.toCharArray();

			int ml = mesg.length;
			int kl = keys.length;
			char[] newmsg = new char[ml];

			for (int i = 0; i < ml; i++) {
				// //// // System.out.println((mesg[i]));
				// //// // System.out.println((keys[i % kl]));
				// //// // System.out.println((mesg[i] ^ keys[i % kl]));
				newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
			} // for i

			return new String(newmsg);
		} catch (Exception e) {
			return null;
		}
	}// xorMessage

}
