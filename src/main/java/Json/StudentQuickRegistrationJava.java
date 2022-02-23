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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONObject;

import exam_module.ExamInfo;
import schooldata.ClassTest;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="quickregistration")
@ViewScoped
public class StudentQuickRegistrationJava implements Serializable
{

	String json;
	ArrayList<ClassTest> classTestList;
	ArrayList<ExamInfo> examList;
	boolean testStatus=false,examStatus=false;
	String message="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public StudentQuickRegistrationJava()
	{
		Connection conn=DataBaseConnection.javaConnection();
	
		try {
			
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String addNumber = params.get("addNumber");
			String sectionid = params.get("sectionid");
			String studentName = params.get("StudentName");
			String postdate = params.get("postDate");
			String schid = params.get("Schoolid");
			String session=DBM.selectedSessionDetails(schid, conn);
			String dob = params.get("dob");
			String status = params.get("status");
			String PhoneNo = params.get("phoneNo");
			String studentPhone = params.get("studentPhone");
			String father = params.get("fatherName");
			String mother = params.get("motherName");
			String gender = params.get("gender");
			
			if(father==null)
			{
				father = "";
			}
			
			if(mother==null)
			{
				mother = "";
			}
			
			
			if(gender==null)
			{
				gender = "";
			}
			
			String msg = "Sorry, Request from unknown source is prohibited";
			JSONObject obj1 = new JSONObject();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); 
			// If true, proceed else block
			if(checkRequest)
			{
				SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
				ArrayList<SelectItem> connsessionList=DBJ.allConnsessionType(schid, conn);
				String concession = (String) connsessionList.get(0).getValue();
				
				String classid = DBJ.classSectionNameFromid(sectionid,schid, conn);
				boolean check=true;

				Date d=null;
				try {
					
					if(dob == null || dob.equals("")) {
						dob = "01/01/2000";
					}
					
					d = new SimpleDateFormat("dd/MM/yyyy").parse(dob);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				Date pdate=null;
				try {
					pdate = new SimpleDateFormat("dd/MM/yyyy").parse(postdate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
				
				
				int checker = DBM.checkForDuplicateAdmisnNoAllowed(schid,session,conn);
				int dupNum = 0;
				
				if(checker==1) {
					if (!addNumber.trim().equalsIgnoreCase("")) {
						dupNum = DBM.duplicateStudentEntry(schid, addNumber.trim(), conn);
					}
				}
				if (dupNum == 1) {
					
					msg="Duplicate admission Number found,try a different one";
					obj1.put("status",msg);
									
				} else {
					int agreement = DBM.checkAgreementFor(schid, conn);
					int currentStrength = Integer.parseInt(DBM.allStudentcount(schid,"","",session,"", conn));

					if(agreement<500)
					{
						if(currentStrength>=(agreement+25))
						{
							msg = "You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.";
							check = false;
						}
					}
					else
					{
						if(currentStrength>=(agreement+50))
						{
							msg = "You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.";
							check = false;
						}
					}
					if(check=true)
					{
						//(new DatabaseMethods1().selectedSessionDetails()).split("-");

						if(status.equalsIgnoreCase("New"))
						{
							String[] sesion=(DatabaseMethods1.selectedSessionDetails(schid,conn)).split("-");

							Date startdate = null;
							Date endDate = null;
							try {
								if(info.getSchoolSession().equals("4-3"))
								{
									startdate=new  SimpleDateFormat("dd/MM/yyyy").parse("31/03/"+sesion[0]);
									endDate = new  SimpleDateFormat("dd/MM/yyyy").parse("01/04/"+sesion[1]);
								}
								else
								{
									startdate=new  SimpleDateFormat("dd/MM/yyyy").parse("30/04/"+sesion[0]);
									endDate = new  SimpleDateFormat("dd/MM/yyyy").parse("01/05/"+sesion[1]);
								}

							} catch (ParseException e) {
								
								e.printStackTrace();
							}

							if(pdate.after(startdate)&&pdate.before(endDate))
							{
								check=true;
							}
							else
							{
								check=false;
								msg = "For New Admission, Admission Date Must Be In This Session i.e "+DatabaseMethods1.selectedSessionDetails(schid,conn)+".";
							}


						}
					}
					if(check==true)
					{
						String srnoType = info.getSrnoType();

						if(srnoType.equalsIgnoreCase("auto"))
						{
							boolean checkStu = DBM.checkStudentsInSchool(schid,conn);
							if(checkStu==false)
							{
								addNumber = info.getSrnoPrefix()+info.getSrnoStart();
							}
							else
							{
								addNumber = info.getSrnoPrefix()+DBM.autoGeneratedSrNo(schid,(info.getSrnoPrefix().length()+1),conn);
							}
						}

						String concessionStatus="accepted";

						String discountFee = "0";
						ArrayList<String> documentsSubmitted=new ArrayList<>();
						int i = DBJ.studentRegistrationSession(schid,DatabaseMethods1.selectedSessionDetails(schid,conn), "", pdate, studentName,
								d
								, sectionid, "", Long.valueOf(PhoneNo), "", "", gender, "Indian", "", "", "", "", 0, "", "", "", "India", father,
								mother, "", "", "", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
								"", "", "", "", "No", "", "", "", "No", "", concession, "", "", "", "", "", "", documentsSubmitted,
								"", "", "", "", "", "", "", "", "", status.toLowerCase(), "", discountFee, "", "", conn, studentPhone,
								addNumber, concessionStatus, "temp",classid);
						//int i=DBJ.insertQuickRegistration(addNumber,studentName,PhoneNo,status.toLowerCase(),sectionid,schid,d,pdate,schid,conn,studentPhone);

						if(i>0)
						{
							String className1=DBJ.classSectionNameFromid(sectionid, schid, conn);
							int maxnumber=i;
							DBM.updateStudentId("CB"+String.valueOf(maxnumber),i,conn);
							DBJ.transportDataEntry(pdate,"CB"+String.valueOf(maxnumber), "", "No",schid,className1,conn);
							new DataBaseMeathodJson().addclassAttendanceINNew(sectionid,new Date(),DBM.schoolId(),conn);
							DBJ.increaseStudentInAddSchool(schid,conn);

							
							String className=DBM.classNameFromidSchid(schid,className1,DatabaseMethods1.selectedSessionDetails(schid,conn),conn);

							//info=DBJ.fullSchoolInfo(schid,conn);

							classTestList=DBJ.selectedClassTestList(className1,sectionid,conn,schid);
							examList=DBJ.selectedClassExamList(className1,sectionid,conn,schid);

							for(ClassTest ct:classTestList)
							{
								testStatus=DBJ.checkClassTestPerformanceStatus(ct.getId(),conn,schid);
								if(testStatus==true)
								{
									DBJ.entryOfNewStudentInClassTestPerformance("CB"+String.valueOf(maxnumber),ct.getId(),conn,schid);
								}
								else
								{

								}
							}
							for(ExamInfo ee:examList)
							{
								DBJ.entryOfNewStudentInExamPerformance("CB"+String.valueOf(maxnumber),ee.getClassid(),ee.getSubjectid(),ee.getSemesterid(),ee.getExamid(),ee.getExamType(),conn,schid,ee.getMaxMark(),ee.getExamName());
							}

							ArrayList<String> messageSetting=DBJ.checkmessageSetting(conn,schid);
							message=messageSetting.get(1);
							if(message.equals("true"))
							{
								String typeMessage="";
								if(String.valueOf(PhoneNo).length()==10
										&& !String.valueOf(PhoneNo).equals("2222222222")
										&& !String.valueOf(PhoneNo).equals("9999999999")
										&& !String.valueOf(PhoneNo).equals("1111111111")
										&& !String.valueOf(PhoneNo).equals("1234567890")
										&& !String.valueOf(PhoneNo).equals("0123456789"))
								{

									/*if(info.getSchoolAppName().equalsIgnoreCase("N/A"))
									{
									*/		/*
									 * } else {
									 * typeMessage="Dear Parent,"+"\n"+"Thank You for admission of your ward "
									 * +studentName+" in class "
									 * +className+". Now you can access your ward's information on your mobile. Please search "
									 * +info.getSchoolAppName()
									 * +" on Google Playstore or Apple store. Enter your registered mobile no. and get OTP verified instantly. We welcome you to be a part of Digital India !"
									 * +"\n"+"Regards\n"+info.getSmsSchoolName(); }
									 */

									
									typeMessage="Dear Parent,"+"\n"+"Thank You for admission of your ward "+studentName+" in class "+className+". Now you can access your ward's information on your mobile."+"\n"+"Regards\n"+info.getSmsSchoolName();
									
									String templateId=DBJ.templateId(info.getKey(), "ADDMISSION", conn);
									
									DBJ.messageUrlWithTemplate(String.valueOf(PhoneNo), typeMessage,"CB"+String.valueOf(maxnumber),schid,conn,templateId);
								}
							}

							obj1.put("status", "student Added");
						}
						else
						{
							obj1.put("status","Something Went Wrong. Please Try Again!");
						}
					}
					else
					{
						obj1.put("status",msg);
					}
						
				}
			}
			else
			{
				obj1.put("status",msg);
			}

			json=obj1.toJSONString();
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
