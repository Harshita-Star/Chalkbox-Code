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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="pickUpStudentReportJson")
@ViewScoped

public class PickUpStudentReportJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public PickUpStudentReportJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String start_date = params.get("startDate");
			String end_date = params.get("endDate");
			String schid=params.get("schid");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date startDate=new Date();
				Date endDate=new Date();

				try {
					startDate= new SimpleDateFormat("dd/MM/yyyy").parse(start_date);
					endDate=new SimpleDateFormat("dd/MM/yyyy").parse(end_date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				SchoolInfoList info = DBJ.fullSchoolInfo(schid, conn);
				ArrayList<StudentInfo> studentList = DBM.pickUpReport(schid, startDate, endDate, conn);
				
				for(StudentInfo ll:studentList)
				{
					JSONObject obj = new JSONObject();
					obj.put("pickdate",ll.getStrAdmitDate());
					obj.put("studentid",ll.getAddNumber());
					obj.put("srNo",ll.getSrNo());
					obj.put("type",ll.getAssType());
					obj.put("name",ll.getFname());
					obj.put("classname", ll.getClassName());
					obj.put("relation", ll.getRelation());
					obj.put("mobile", ll.getContactNo());
					obj.put("image", info.getDownloadpath()+ll.getSignImage());
					obj.put("pickername", ll.getName());
					obj.put("remark", ll.getRemark());
					obj.put("picktime", ll.getStrDob());
					arr.add(obj);
				}
			}
			
			json=arr.toJSONString();
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

	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();
	}
}
