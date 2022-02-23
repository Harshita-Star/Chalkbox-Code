package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import student_module.RegistrationColumnName;
@ManagedBean(name="RfidJsonBean")
@ViewScoped
public class RfidJsonBean implements Serializable {


	String json="";
	String schid,studentid,rfidno;
	String title = "";
	String notify = "";
	String msg = "";
	String smsSchName= "";
	Connection conn=DataBaseConnection.javaConnection();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	StudentInfo ls = new StudentInfo();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	
	public RfidJsonBean() throws ParseException {

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String start = params.get("start");
			String end=params.get("end");
			String imei=params.get("imei");
			String date=params.get("date");
			String data=params.get("d");
			int i=0;
			
			dbj.addRFidData(start,end,imei,date,data,conn);
			DatabaseMethods1 DBM=new DatabaseMethods1();
			
			if(data==null || data.equals(""))
			{
				json="done";
			}
			else
			{
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
				////// // System.out.println("RFID Call Started : "+new Date());
				String tIntv = dbm.contactNo("RFID Bus Interval Minute", conn);
				String sIntv = dbm.contactNo("RFID School Interval Minute", conn);
				
				long transIntv = Long.valueOf(tIntv.equalsIgnoreCase("") ? "0" : tIntv);
				long schIntv = Long.valueOf(sIntv.equalsIgnoreCase("") ? "0" : sIntv);
				
				ArrayList<SelectItem> data25 = dbj.decodeRFIDData(data);
				for(SelectItem datainfo:data25)
				{
					rfidno = datainfo.getLabel();
					String dt = datainfo.getDescription();
					
					int hour = Integer.parseInt(String.valueOf(datainfo.getValue()));
//					//// // System.out.println("Device Type : "+deviceType);
//					//// // System.out.println("IMEI : "+imei);
//					//// // System.out.println("RFID : "+rfidno);
					
					Date d1 = format.parse(dt);
					String dateTime = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(d1);
					
					
					String session = DatabaseMethods1.selectedSessionDetails(schid,conn);
					ArrayList<String> list=new ArrayList<>();
					list.add(RegistrationColumnName.ADMISSION_NUMBER);
					studentid = new DataBaseMethodStudent().studentDetail(rfidno, "", "", QueryConstants.RFID_NUMBER,"",
							null,null,"","","","", session, schid, list, conn).get(0).getAddNumber(); // IN this method no need of schid.. so it can go blank
					
					
					if(!studentid.equals(""))
					{

						String actionDt = new SimpleDateFormat("yyyy-MM-dd").format(d1);
						schid = DBM.schoolIdByStudentId(studentid, conn);
						
						SelectItem deviceinfo = dbr.rfidDeviceInfoIMEIAndSchid(imei,schid, conn);
						String deviceType = deviceinfo.getDescription();
						
						String currentStop = dbj.studentCurrentStop(studentid,schid,conn);
						String currentRoute = dbj.routeIdFromStopGroupId(currentStop, DatabaseMethods1.selectedSessionDetails(schid,conn), conn, schid);
						ls = dbj.studentDetailslistByAddNo(studentid, schid, conn);
						
						title = "";
						notify = "";
						msg = "";
						smsSchName = dbj.fullSchoolInfo(schid, conn).getSmsSchoolName();
						//boolean attendantApp = new DataBaseMeathodJson().appLoginPermission("Attendant", schid, conn).equalsIgnoreCase("true") ? true : false;
						boolean attendantApp = true;
						
						String newImei = "";
						if(deviceType.equalsIgnoreCase("Transport"))
						{
							newImei = imei+"-"+deviceType;
							String id = dbj.busIdByDeviceId(String.valueOf(deviceinfo.getValue()),schid,conn);
							if(!id.equals(""))
							{
								newImei = id+"-"+deviceType;
							}
						}
						else
						{
							newImei = imei+"-"+deviceType;
						}
					  
						if(deviceType.equalsIgnoreCase("Transport"))
						{
							title = "Transport";
							if(hour<=10)
							{
								String check = dbj.checkRFIDEntry(studentid,actionDt,"in_bus_morn",conn);
								if(check.equalsIgnoreCase("no"))
								{
									dbj.rfidInOutSchool(studentid,rfidno,actionDt,schid,"in_bus_morn","in_bus_morn_imei",dateTime,newImei,conn);
									if(attendantApp)
									{
										dbj.insertStudentPickDetail(studentid, schid, currentStop, currentRoute, "RFID Card Punch", d1,"no","yes","pick","",dateTime, conn);
									}
									
									notify = "Your ward "+ls.getFname()+" has boarded the School bus from home bus stop.";
									msg = "Dear Parent,\n"+"Your ward "+ls.getFname()+" has boarded the School bus from home bus stop.\nRegards\n"+smsSchName;
								}
								else
								{
									SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
									
									Date prevDt = null;
									Date currDt = null;
									try
									{
										prevDt = sdf.parse(check);
										currDt = sdf.parse(dateTime);
										long min = TimeUnit.MILLISECONDS.toMinutes(currDt.getTime() - prevDt.getTime());
									
										if(min >= transIntv)
										{
											dbj.rfidInOutSchool(studentid,rfidno,actionDt,schid,"out_bus_morn","out_bus_morn_imei",dateTime,newImei,conn);
											if(attendantApp)
											{
												dbj.insertStudentDropDetail(studentid, schid, currentStop, currentRoute, "RFID Card Punch", d1,"no","yes","schooldrop","",dateTime, conn);
											}
											notify = "Your ward "+ls.getFname()+" has deboarded the School bus at School.";
											msg = "Dear Parent,\n"+"Your ward "+ls.getFname()+" has deboarded the School bus at School.\nRegards\n"+smsSchName;
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
									
								}
							}
							else
							{
								String check = dbj.checkRFIDEntry(studentid,actionDt,"in_bus_even",conn);
								if(check.equalsIgnoreCase("no"))
								{
									dbj.rfidInOutSchool(studentid,rfidno,actionDt,schid,"in_bus_even","in_bus_even_imei",dateTime,newImei,conn);
									if(attendantApp)
									{
										dbj.insertStudentPickDetail(studentid, schid, currentStop, currentRoute, "RFID Card Punch", d1,"no","yes","schoolpick","",dateTime, conn);
									}
									notify = "Your ward "+ls.getFname()+" has boarded the School bus from School bus stop.";
									msg = "Dear Parent,\n"+"Your ward "+ls.getFname()+" has boarded the School bus from School bus stop.\nRegards\n"+smsSchName;
								}
								else
								{
									SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
									
									Date prevDt = null;
									Date currDt = null;
									try
									{
										prevDt = sdf.parse(check);
										currDt = sdf.parse(dateTime);
										long min = TimeUnit.MILLISECONDS.toMinutes(currDt.getTime() - prevDt.getTime());
									
										if(min >= transIntv)
										{
											dbj.rfidInOutSchool(studentid,rfidno,actionDt,schid,"out_bus_even","out_bus_even_imei",dateTime,newImei,conn);
											if(attendantApp)
											{
												dbj.insertStudentDropDetail(studentid, schid, currentStop, currentRoute, "RFID Card Punch", d1,"no","yes","drop","",dateTime, conn);
											}
											notify = "Your ward "+ls.getFname()+" has deboarded the School bus at home bus stop.";
											msg = "Dear Parent,\n"+"Your ward "+ls.getFname()+" has deboarded the School bus at home bus stop.\nRegards\n"+smsSchName;
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
							}
						}
						else
						{
							title = "Security";
							if(hour<12)
							{
								String check = dbj.checkRFIDEntry(studentid,actionDt,"in_school",conn);
								if(check.equalsIgnoreCase("no"))
								{
									dbj.rfidInOutSchool(studentid,rfidno,actionDt,schid,"in_school","in_school_imei",dateTime,newImei,conn);
									dbj.insertStudentSchoolInOutDetail(studentid, schid, currentStop, currentRoute, "RFID Card Punch", d1,"no","yes","inschool","",dateTime, conn);
									
									notify = "Your ward "+ls.getFname()+" has entered the School premises.";
									msg = "Dear Parent,\n"+"Your ward "+ls.getFname()+" has entered the School premises.\nRegards\n"+smsSchName;
								}
								else
								{
									SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
									
									Date prevDt = null;
									Date currDt = null;
									try
									{
										prevDt = sdf.parse(check);
										currDt = sdf.parse(dateTime);
										long min = TimeUnit.MILLISECONDS.toMinutes(currDt.getTime() - prevDt.getTime());
									
										if(min >= schIntv)
										{
											dbj.rfidInOutSchool(studentid,rfidno,actionDt,schid,"out_school","out_school_imei",dateTime,newImei,conn);
											dbj.insertStudentSchoolInOutDetail(studentid, schid, currentStop, currentRoute, "RFID Card Punch", d1,"no","yes","outschool","",dateTime, conn);
										
											notify = "Your ward "+ls.getFname()+" has left the School premises.";
											msg = "Dear Parent,\n"+"Your ward "+ls.getFname()+" has left the School premises.\nRegards\n"+smsSchName;
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
							}
							else
							{
								dbj.rfidInOutSchool(studentid,rfidno,actionDt,schid,"out_school","out_school_imei",dateTime,newImei,conn);
								dbj.insertStudentSchoolInOutDetail(studentid, schid, currentStop, currentRoute, "RFID Card Punch", d1,"no","yes","outschool","",dateTime, conn);
							
								notify = "Your ward "+ls.getFname()+" has left the School premises.";
								msg = "Dear Parent,\n"+"Your ward "+ls.getFname()+" has left the School premises.\nRegards\n"+smsSchName;
							}
							
						}

						String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
						if(actionDt.equalsIgnoreCase(currDate))
						{
							
						 SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
							
							Date prevDt = null;
							Date currDt = null;
							
								prevDt = new Date();
								currDt = sdf1.parse(dateTime);
							
								long min = TimeUnit.MILLISECONDS.toMinutes(prevDt.getTime() - currDt.getTime());
								//// // System.out.println("intrrval________"+min);
								
								if(min<60)
								{
									//// // System.out.println("in If ________"+min);
										
									
									String finalTitle = title;
									String finalMsg = msg;
									String finalNotify = notify;
									String permit ="";
									permit= dbj.checkCommType(schid,"rfid", conn); //notification, sms, both
									if(permit.equalsIgnoreCase("notification"))
									{
										if(!finalNotify.equals(""))
										{
											if(ls.getFathersPhone()==ls.getMothersPhone())
											{
												dbj.notification(finalTitle, finalNotify,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
											}
											else
											{
												dbj.notification(finalTitle, finalNotify,
														ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
												dbj.notification(finalTitle, finalNotify,
														ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
											}
										}
									
									}
									else if(permit.equalsIgnoreCase("sms"))
									{
										if (String.valueOf(ls.getFathersPhone()).length() == 10
												&& !String.valueOf(ls.getFathersPhone()).equals("2222222222")
												&& !String.valueOf(ls.getFathersPhone()).equals("9999999999")
												&& !String.valueOf(ls.getFathersPhone()).equals("1111111111")
												&& !String.valueOf(ls.getFathersPhone()).equals("1234567890")
												&& !String.valueOf(ls.getFathersPhone()).equals("0123456789"))
										{
											dbj.messageurl1(String.valueOf(ls.getFathersPhone()), finalMsg,studentid,schid,conn);
										}
									}
									else if(permit.equalsIgnoreCase("both"))
									{
										
										if(!finalNotify.equals(""))
										{
											if(ls.getFathersPhone()==ls.getMothersPhone())
											{
												dbj.notification(finalTitle, finalNotify,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
											}
											else
											{
												dbj.notification(finalTitle, finalNotify,
													ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
												dbj.notification(finalTitle, finalNotify,
													ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
											}
										}
									}
								}
							}
								}
								
							
							
					
					i=i+1;
					////// // System.out.println("Index : "+i);
					
				}
				
				////// // System.out.println("RFID Response Gone : "+new Date());
				json="done";
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
