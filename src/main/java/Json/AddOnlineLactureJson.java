package Json;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import schooldata.StudentInfo;
import session_work.QueryConstants;

public class AddOnlineLactureJson implements Serializable{
	
	


	String studentjson;
	String data;
	String json,sms;
	ArrayList<StudentInfo> studentList=new ArrayList<>();

	String classid,sectionid,subjectid,desc,schid,imagePath,subjectType,userid;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM= new DatabaseMethods1();


	public void addOnlineLacture(String userid,String classid,String sectionid,String subjectid, String desc,String schid,String image,String title,String link)
	{
 
		java.sql.Connection conn=DataBaseConnection.javaConnection();
		
		try
		{
			DataBaseMethodStudent objStd=new DataBaseMethodStudent();
			ArrayList<String> stdColumnList=objStd.attendanceFieldList();
			String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
			
			String staffName = DBM.employeeNameByuserNameWithSchid(userid, schid, conn);
			if(staffName.equals(""))
			{
				staffName = userid.toUpperCase()+"("+DBM.userTypeOfUser(userid, schid, conn)+")";
			}
			Date pDate=new Date();
			
			if(title==null||title.equals("")) {
				  title = "";
			  }
			
			int i=DBJ.submitOnlineLacture(classid,sectionid,subjectid,userid, pDate,image,link,title, desc,schid,conn);
			if(i>0)
			{
				String className = DBM.classNameFromidSchid(schid,classid, session, conn);
				String sectionName = "";
				String clsSection = "";
				if(sectionid.equalsIgnoreCase("All"))
				{
					sectionName = "All";
					clsSection = className;
				}
				else
				{
					sectionName = new DatabaseMethods1().sectionNameByIdSchid(schid,sectionid, conn);
					clsSection = className+"-"+sectionName;
				}

				String subjectName = "";
				if(subjectid.equalsIgnoreCase("All"))
				{
					subjectName = "All";
				}
				else
				{
					subjectName = DBJ.subjectNameFromid(subjectid, schid, conn);
				}
				
				String desp = "Dear User,\n"
						+ "Kindly check \"E- Learning\" icon as a file is being uploaded by "+staffName+" subject teacher of "+subjectName+" in class "+clsSection+" under topic "+title+".\n"
						+ "\n"
						+ "Regards";
				
				//String desp ="Online lecture added for <"+subjectName+">";

				Runnable r = new Runnable()
				{
					public void run()
					{
						Connection connect = DataBaseConnection.javaConnection();
						try {
							ArrayList<EmployeeInfo> principleId = DBM.getPrincipleId(schid, connect);
							ArrayList<EmployeeInfo> cordinators = DBJ.cordinatorListByClassId(classid, schid, session, "active", connect);
							ArrayList<EmployeeInfo> notifyStaff = new ArrayList<EmployeeInfo>();
							notifyStaff.addAll(principleId);
							notifyStaff.addAll(cordinators);
							
							DBJ.adminnotification("E-Learning",desp,"admin-"+schid,schid,"",connect);
							for(EmployeeInfo empi : notifyStaff) 
							{
								DBJ.adminnotification("E-Learning",desp,"staff"+"-"+empi.getId()+"-"+schid,schid,"",connect);
							}
							
							if(subjectid.equalsIgnoreCase("all"))
							{
								if(sectionid.equalsIgnoreCase("All"))
								{
									DBJ.notification("E-Learning",desp,classid+"-"+schid,schid,"",connect);
									//studentList=objStd.studentDetail("", "", classid,QueryConstants.BY_CLASS,QueryConstants.ATTENDANCE,null,null,"","","","", session,schid, stdColumnList, connect);
								}
								else
								{
									DBJ.notification("E-Learning",desp,sectionid+"-"+classid+"-"+schid,schid,"",connect);
									//studentList=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, connect);
								}
							}
							else
							{
								if(sectionid.equalsIgnoreCase("All"))
								{
									ArrayList<SelectItem> allSection = DBJ.allSection(classid, schid, connect);
									if(allSection.size()>0)
									{
										subjectType=DBJ.subjectNameAndTypeFromid(subjectid,schid,connect);
										String[] temp=subjectType.split(",");
										if(temp[1].equalsIgnoreCase("Mandatory"))
										{
											DBJ.notification("E-Learning",desp,classid+"-"+schid,schid,"",connect);
											//studentList=objStd.studentDetail("", "", classid,QueryConstants.BY_CLASS,QueryConstants.ATTENDANCE,null,null,"","","","", session,schid, stdColumnList, connect);
										}
										else
										{
											studentList=DBM.getAllStudentStrentgthForOptional(schid,subjectid,sectionid,classid,"fromJson",connect);
											for(StudentInfo ss : studentList)
											{
												if(ss.getFathersPhone()==ss.getMothersPhone())
												{
													DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",connect);
												}
												else
												{
													DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",connect);
													DBJ.notification("E-Learning",desp, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",connect);
												}
											}
										}
									}
								}
								else
								{
									subjectType=DBJ.subjectNameAndTypeFromid(subjectid,schid,connect);
									String[] temp=subjectType.split(",");
									if(temp[1].equalsIgnoreCase("Mandatory"))
									{
										DBJ.notification("E-Learning",desp,sectionid+"-"+classid+"-"+schid,schid,"",connect);
										//studentList=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, connect);
									}
									else
									{
										studentList=DBM.getAllStudentStrentgthForOptional(schid,subjectid,sectionid,classid,"fromJson",connect);
										for(StudentInfo ss : studentList)
										{
											if(ss.getFathersPhone()==ss.getMothersPhone())
											{
												DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",connect);
											}
											else
											{
												DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",connect);
												DBJ.notification("E-Learning",desp, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",connect);
											}
										}
									}
								}
							}

						}
						catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							try {
								connect.close();
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
						


					
					}

				};
				new Thread(r).start();
			}

		}
		catch (Exception e) {
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

}
