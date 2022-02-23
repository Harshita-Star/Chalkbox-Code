package Json;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;

public class HomeWorkJson implements Serializable {

	String studentjson;
	String data;
	String json, sms;
	ArrayList<StudentInfo> studentList = new ArrayList<>();

	String classid, sectionid, subjectid, desc, schid, imagePath, subjectType, userid;
	String notificationtitle = "";
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String clsSection = "";

	public void addHomework(String userid, String classid, String sectionid1, String subjectid, String desc1,
			String schid, String sms, String image, String type) {

		java.sql.Connection conn = DataBaseConnection.javaConnection();

		try {
			DataBaseMethodStudent objStd = new DataBaseMethodStudent();
			ArrayList<String> stdColumnList = objStd.attendanceFieldList();
			String session = DatabaseMethods1.selectedSessionDetails(schid, conn);

			Date pDate = new Date();
			Date assShowDate = new Date();
			String sectionArr[] = sectionid1.split(",");
			int i = 0;

			for (int j = 0; j < sectionArr.length; j++) {
				desc = desc1;
				sectionid = sectionArr[j];
				i = DBJ.submitAssignment(classid, sectionid, subjectid, userid, pDate, assShowDate, image, "", "", "",
						"", "", type, desc1, schid, conn);
				if (i > 0) {

					if (type.equalsIgnoreCase("homework")) {
						notificationtitle = "Home Work";
					} else {
						notificationtitle = "Notes";
					}

					String className = dbm.classNameFromidSchid(schid, classid, session, conn);
					String sectionName = "";

					if (sectionid.equalsIgnoreCase("All")) {
						sectionName = "All";
						clsSection = className;
					} else {
						sectionName = new DatabaseMethods1().sectionNameByIdSchid(schid, sectionid, conn);
						clsSection = className + "-" + sectionName;
					}

					String subjectName = "";
					if (subjectid.equalsIgnoreCase("All")) {
						subjectName = "All";
					} else {
						subjectName = DBJ.subjectNameFromid(subjectid, schid, conn);
					}
					String cls = "Class : " + clsSection + "\nSubject : " + subjectName;
					String desp = cls + "\n" + desc;

					Runnable r = new Runnable() {
						public void run() {
							Connection connect = DataBaseConnection.javaConnection();
							try {

								if (sms == null || sms.equals("")) {

								} else {
									if (schid.equals("221") || schid.equals("216") || schid.equals("302")) {

										String empName = dbm.teacherInfoByUserName(userid, conn).getFname();
										String empType = dbm.userTypeOfUser(userid, schid, conn);
										if (empType.equalsIgnoreCase("admin")) {
											empName = "ADMIN";
										}

										subjectType = DBJ.subjectNameAndTypeFromid(subjectid, schid, connect);
										String subType = subjectType.split(",")[1];
										String subjName = subjectType.split(",")[0];
										if (subjectid.equalsIgnoreCase("All")) {
											subjName = "All Subjets";
										} else {
											subjName = "Subject " + subjName;
										}

										
										
										if (subType.equalsIgnoreCase("Mandatory")) {
											String sid = sectionid;
											if (sectionid.equalsIgnoreCase("All")) {
												sid = "-1";
											}

											studentList = objStd.studentDetail("", sid, classid,
													QueryConstants.BY_CLASS_SECTION, QueryConstants.ATTENDANCE_RFID,
													null, null, "", "", "", "", session, schid, stdColumnList, connect);
										} else {
											studentList = dbm.getAllStudentStrentgthForOptional(schid, subjectid,
													sectionid, classid, "fromJson", connect);
										}
										

										for (StudentInfo ss : studentList) {

											String hw = "Dear " + ss.getFullName() + "\r\n" + "Home work of "
													+ subjName + " \r\n" + " of class " + clsSection
													+ " is being uploaded by " + empName + ".\r\n"
													+ "Kindly check and complete your work in time.\r\n"
													+ "Team Chalkbox";
											if (ss.getFathersPhone() == ss.getMothersPhone()) {
												DBJ.notification(
														notificationtitle, hw, ss.getFathersPhone() + "-"
																+ ss.getAddNumber() + "-" + schid,
														schid, "", connect);
											} else {
												DBJ.notification(
														notificationtitle, hw, ss.getFathersPhone() + "-"
																+ ss.getAddNumber() + "-" + schid,
														schid, "", connect);
												DBJ.notification(
														notificationtitle, hw, ss.getMothersPhone() + "-"
																+ ss.getAddNumber() + "-" + schid,
														schid, "", connect);
											}
										}

									} else {
										if (subjectid.equalsIgnoreCase("all")) {
											if (sectionid.equalsIgnoreCase("All")) {
												DBJ.notification(notificationtitle, desp, classid + "-" + schid, schid,
														"", connect);
												studentList = objStd.studentDetail("", "", classid,
														QueryConstants.BY_CLASS, QueryConstants.ATTENDANCE, null, null,
														"", "", "", "", session, schid, stdColumnList, connect);
											} else {
												DBJ.notification(notificationtitle, desp,
														sectionid + "-" + classid + "-" + schid, schid, "", connect);
												studentList = objStd.studentDetail("", sectionid, classid,
														QueryConstants.BY_CLASS_SECTION, QueryConstants.ATTENDANCE_RFID,
														null, null, "", "", "", "", session, schid, stdColumnList,
														connect);
											}
										} else {
											if (sectionid.equalsIgnoreCase("All")) {
												ArrayList<SelectItem> allSection = DBJ.allSection(classid, schid,
														connect);
												if (allSection.size() > 0) {
													subjectType = DBJ.subjectNameAndTypeFromid(subjectid, schid,
															connect);
													String[] temp = subjectType.split(",");
													if (temp[1].equalsIgnoreCase("Mandatory")) {
														DBJ.notification(notificationtitle, desp, classid + "-" + schid,
																schid, "", connect);
														studentList = objStd.studentDetail("", "", classid,
																QueryConstants.BY_CLASS, QueryConstants.ATTENDANCE,
																null, null, "", "", "", "", session, schid,
																stdColumnList, connect);
													} else {
														studentList = dbm.getAllStudentStrentgthForOptional(schid,
																subjectid, sectionid, classid, "fromJson", connect);
														for (StudentInfo ss : studentList) {
															if (ss.getFathersPhone() == ss.getMothersPhone()) {
																DBJ.notification(
																		notificationtitle, desp, ss.getFathersPhone()
																				+ "-" + ss.getAddNumber() + "-" + schid,
																		schid, "", connect);
															} else {
																DBJ.notification(
																		notificationtitle, desp, ss.getFathersPhone()
																				+ "-" + ss.getAddNumber() + "-" + schid,
																		schid, "", connect);
																DBJ.notification(
																		notificationtitle, desp, ss.getMothersPhone()
																				+ "-" + ss.getAddNumber() + "-" + schid,
																		schid, "", connect);
															}
														}
													}
												}
											} else {
												subjectType = DBJ.subjectNameAndTypeFromid(subjectid, schid, connect);
												String[] temp = subjectType.split(",");
												if (temp[1].equalsIgnoreCase("Mandatory")) {
													DBJ.notification(notificationtitle, desp,
															sectionid + "-" + classid + "-" + schid, schid, "",
															connect);
													studentList = objStd.studentDetail("", sectionid, classid,
															QueryConstants.BY_CLASS_SECTION,
															QueryConstants.ATTENDANCE_RFID, null, null, "", "", "", "",
															session, schid, stdColumnList, connect);
												} else {
													studentList = dbm.getAllStudentStrentgthForOptional(schid,
															subjectid, sectionid, classid, "fromJson", connect);
													for (StudentInfo ss : studentList) {
														if (ss.getFathersPhone() == ss.getMothersPhone()) {
															DBJ.notification(
																	notificationtitle, desp, ss.getFathersPhone() + "-"
																			+ ss.getAddNumber() + "-" + schid,
																	schid, "", connect);
														} else {
															DBJ.notification(
																	notificationtitle, desp, ss.getFathersPhone() + "-"
																			+ ss.getAddNumber() + "-" + schid,
																	schid, "", connect);
															DBJ.notification(
																	notificationtitle, desp, ss.getMothersPhone() + "-"
																			+ ss.getAddNumber() + "-" + schid,
																	schid, "", connect);
														}
													}
												}
											}
										}
									}

									if (sms.equalsIgnoreCase("yes")) {
										SchoolInfoList info = DBJ.fullSchoolInfo(schid, connect);

										if (studentList.size() > 0) {
											String contactNumber = "";
											String addNumber = "";
											int x = 0;

											String typeMessage = "Dear Parent," + "\nHomework for\n" + desp + "\n"
													+ "Regards\n" + info.getSmsSchoolName();
											for (StudentInfo ss : studentList) {
												if (String.valueOf(ss.getFathersPhone()).length() == 10
														&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
														&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
														&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
														&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
														&& !String.valueOf(ss.getFathersPhone()).equals("0123456789")) {
													x++;
													if (contactNumber.equals("")) {
														contactNumber = String.valueOf(ss.getFathersPhone());
														addNumber = ss.getAddNumber();
													} else {
														contactNumber = contactNumber + ","
																+ String.valueOf(ss.getFathersPhone());
														addNumber = addNumber + "," + ss.getAddNumber();
													}
												}

											}

											if (x > 0) {
												DBJ.messageurl1(contactNumber, typeMessage, addNumber, schid, connect);
											}
										}

									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								try {
									connect.close();
								} catch (Exception e2) {
									// TODO: handle exception
								}
							}

						}

					};
					new Thread(r).start();
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

}
