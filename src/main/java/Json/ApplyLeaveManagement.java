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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ViewScoped
@ManagedBean(name="applyLeaveJson")
public class ApplyLeaveManagement implements Serializable {

	String json;

	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	public ApplyLeaveManagement() {

		Connection conn=DataBaseConnection.javaConnection();

		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String start_date = params.get("start_date");
			String end_date = params.get("end_date");
			String reason=   params.get("reason");
			String schoolId=params.get("Schoolid");

			String status="";
			String strDate="";
			Date newDate=null;
			Date endDate=null;
			Date newDate1=null;
			Date endDate1=null;
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
	     
			if(checkRequest)
			{
				try {
					newDate= new SimpleDateFormat("dd/MM/yyyy").parse(start_date);
					endDate=new SimpleDateFormat("dd/MM/yyyy").parse(end_date);
					newDate1= new SimpleDateFormat("dd/MM/yyyy").parse(start_date);
					endDate1=new SimpleDateFormat("dd/MM/yyyy").parse(end_date);

				} catch (ParseException e) {
					
					e.printStackTrace();
				}

				String currentdate=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				currentdate=start_date+" 08:00 am";
				Date cdDATE=null;
				try {
					cdDATE = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(currentdate);
				} catch (ParseException e1) {
					
					e1.printStackTrace();
				}

				if(cdDATE.before(new Date()))
				{
					json="Time to apply today's leave is before 8:00 AM.";
				}
				else
				{

					if(studentid==null || studentid.equals("") || studentid.equalsIgnoreCase("null"))
					{
						json="not updated";
					}
					else
					{
						boolean check = false;
						boolean checkAlready = false;

						hloop:for(Date d = newDate1;(d.before(endDate1) || d.equals(endDate1));d.setDate(d.getDate()+1))
						{
							strDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
							check=DBJ.checkSchoolHolidayForAttendanceJSON(strDate, conn,schoolId);
							checkAlready=DBJ.checkAlreadyAppliedLeave(strDate,studentid,schoolId,conn);
							if(check)
							{
								break hloop;
							}

							if(checkAlready)
							{
								break hloop;
							}
						}

						if(check)
						{
							status="You cannot apply leave for these date(s) as there is a holiday on "+strDate;
						}
						else if(checkAlready)
						{
							status= "You have already applied leave for "+strDate;
						}
						else if(reason.trim().equalsIgnoreCase(""))
						{
							status="Please mention a reason.";
						}
						else
						{
							int i=DBJ.AppStudentLeave(studentid,reason,newDate,endDate,schoolId,conn);

							////// // System.out.println(i);
							if(i>0)
							{
								StudentInfo ln=DBJ.studentDetailslistByAddNo(studentid,schoolId,conn);
								String clsTchr = DBJ.classTeacherBySection1(ln.getSectionid(), schoolId, conn);
	                            DBJ.adminnotification("Leave",ln.getFname()+" Applied For Leave from "+start_date+"-"+end_date,"admin-"+schoolId,schoolId,"LeaveApply-"+studentid,conn);
								if(!clsTchr.equalsIgnoreCase("no"))
								{
									String[] teacher=clsTchr.split(",");
									for(int ii=0;ii<teacher.length;ii++)
									{
										DBJ.adminnotification("Leave",ln.getFname()+" Applied For Leave from "+start_date+"-"+end_date,"staff"+"-"+teacher[ii]+"-"+schoolId,schoolId,"LeaveApply-"+studentid,conn);
													
									}
								}
								status="updated";
							}
							else
							{
								status="not updated";
							}
						}


						json=status;
					}
				}
				
				
			}
			else
			{
				json="not updated";
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
