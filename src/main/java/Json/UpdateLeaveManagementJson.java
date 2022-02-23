package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ViewScoped
@ManagedBean(name="updateLeaveManagment")
public class UpdateLeaveManagementJson implements Serializable
{
	String json;
	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public UpdateLeaveManagementJson() {

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();


			String id=params.get("id");
			String status=params.get("status");
			String remark=params.get("remark");
			String schoolId=params.get("Schoolid");

			if(remark==null || remark.equalsIgnoreCase(""))
			{
				remark = "";
			}

			String jsonSts = "not updated";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(status.equalsIgnoreCase("3"))
				{
					ApplyLeaveJson info = DBJ.leaveDetails(id,schoolId,conn);
					int i=DBJ.deleteStudentLeave(id,schoolId,conn);

					if(i>0)
					{
						jsonSts="updated";
						DBJ.adminnotification("Leave",info.getFname()+" Deleted the Leave from "+info.getStartDate()+"-"+info.getEndDate(),"admin-"+schoolId,schoolId,"LeaveDelete-"+info.getStudentid(),conn);
						String clsTchr = DBJ.classTeacherBySection1(info.getSectionid(), schoolId, conn);
						if(!clsTchr.equalsIgnoreCase("no"))
						{
							String[] teacher=clsTchr.split(",");
							for(int ii=0;ii<teacher.length;ii++)
							{
								DBJ.adminnotification("Leave",info.getFname()+" Deleted the Leave from "+info.getStartDate()+"-"+info.getEndDate(),"staff"+"-"+teacher[ii]+"-"+schoolId,schoolId,"LeaveDelete-"+info.getStudentid(),conn);
												
							}
						}
					}
					else
					{
						jsonSts="not updated";
					}
				}
				else
				{
					String unm = params.get("unm");
					if(unm == null)
					{
						unm = "NA";
					}

					if(id.contains(","))
					{
						String idArr[] = id.split(",");
						for(int i=0;i<idArr.length;i++)
						{
							int x=DBJ.updateStudentLeave(idArr[i],status,schoolId,unm,remark,conn);
							if(x>0)
							{

								String addNumber=DBJ.getlevaeStudent(idArr[i],schoolId,conn);
								StudentInfo info=DBJ.studentDetailslistByAddNo(addNumber,schoolId,conn);

								if(status.equals("1"))
								{
									DBJ.notification("Leave","Your Leave Has Been Approved", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
									DBJ.notification("Leave","Your Leave Has Been Approved", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);

								}
								else
								{
									DBJ.notification("Leave","Your Leave Has Been Denied", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
									DBJ.notification("Leave","Your Leave Has Been Denied", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);

								}
								jsonSts="updated";

							}
							else
							{
								jsonSts="not updated";
							}
						}
					}
					else
					{
						int i=DBJ.updateStudentLeave(id,status,schoolId,unm,remark,conn);
						if(i>0)
						{

							String addNumber=DBJ.getlevaeStudent(id,schoolId,conn);
							StudentInfo info=DBJ.studentDetailslistByAddNo(addNumber,schoolId,conn);

							if(status.equals("1"))
							{
								DBJ.notification("Leave","Your Leave Has Been Approved", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
								DBJ.notification("Leave","Your Leave Has Been Approved", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);

							}
							else
							{
								DBJ.notification("Leave","Your Leave Has Been Denied", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
								DBJ.notification("Leave","Your Leave Has Been Denied", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);

							}
							jsonSts="updated";

						}
						else
						{
							jsonSts="not updated";
						}
					}
				}
			}

			json=jsonSts;
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
