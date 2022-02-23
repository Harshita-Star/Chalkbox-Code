package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import session_work.QueryConstants;
import session_work.RegexPattern;
@ManagedBean(name="parentloginbean")
@ViewScoped
public class Parent_Login_Bean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String studentid;


	public String login(String schid)
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();

		////// // System.out.println("schid : "+schid);

		//String schid = "215";


		boolean checkSchool = DBM.checkSchoolStatus(schid,conn);
		if(checkSchool==true)
		{
			boolean expired=DBM.checkSchoolExpiryDate(schid,conn);
			//boolean expired=false;
			if(expired==false)
			{
				String session = DatabaseMethods1.selectedSessionDetails(schid, conn);
				DataBaseMethodStudent objStd=new DataBaseMethodStudent();
				ArrayList<String> list=objStd.basicFieldsForStudentList();
				ArrayList<StudentInfo> selectedStudent=objStd.studentDetail(studentid,"","",QueryConstants.MOBILE_NO,QueryConstants.MOBILE_NO,null,null,"","","","",session, schid, list, conn);
				

				if(selectedStudent.size()==0)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Mobile No. Invalid", "Validation error"));
				}
				else
				{
					SchoolInfoList info=new DataBaseMeathodJson().fullSchoolInfo(schid,conn);
					int randomPIN = (int)(Math.random()*9000)+1000;
					String typemessage="Dear Parent, \n\n"
							+String.valueOf(randomPIN)+ " is  your login OTP. Treat this as confidential. We welcome you to be a part of Digital India. \n\nRegards, \n"+info.getSchoolName();


					////// // System.out.println(selectedStudent.get(0).getAddNumber()+"...."+selectedStudent.get(0).getStudentid());
					new DataBaseMeathodJson().messageurl1(studentid, typemessage,selectedStudent.get(0).getAddNumber(),schid,conn);

					String name="",stutid="",classname="";
					for(StudentInfo ss:selectedStudent)
					{
						if(name.equals(""))
						{
							name=ss.getFname();
							stutid=ss.getAddNumber();
							classname=ss.getClassName();
						}
						else
						{
							name=name+","+ss.getFname();
							stutid=stutid+","+ss.getAddNumber();
							classname=classname+","+ss.getClassName();

						}

					}


					HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
					ss.setAttribute("otp",String.valueOf(randomPIN) );
					ss.setAttribute("studentid",stutid);
					ss.setAttribute("schoolid",selectedStudent.get(0).getSchid());
					ss.setAttribute("name",name);
					ss.setAttribute("classname",classname);
					ss.setAttribute("permission",info.getStudent_app_permission());
					ss.setAttribute("timeTable",info.getTimetable());

					return "otpSubmit.xhtml";

					////// // System.out.println(json);
				}
			}
			else
			{

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Sorry, license of your school ERP has been expired, Please contact Administrator for"
						+ " license renewal. Make the renewal as soon as possible and enjoy our services. We are"
						+ " here to serve you. Thanks and Regards", "Validation error"));



			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Sorry, license of your school ERP has been expired, Please contact Administrator for"
					+ " license renewal. Make the renewal as soon as possible and enjoy our services. We are"
					+ " here to serve you. Thanks and Regards", "Validation error"));


		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";

	}


	public String getStudentid() {
		return studentid;
	}


	public void setStudentid(String studentid) {
		this.studentid = studentid;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
