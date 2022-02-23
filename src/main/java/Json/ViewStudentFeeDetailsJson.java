package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@ManagedBean(name="viewstudentFeeDeatils")
@ViewScoped
public class ViewStudentFeeDetailsJson implements Serializable{

	String json;

	ArrayList<StudentInfo> list;
	public ViewStudentFeeDetailsJson() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid=params.get("id");
			String schid=params.get("Schoolid");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list =new DatabaseMethods1(schid).SchoolTotalStudentFeeDeatils(studentid,conn);

				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("Date", new SimpleDateFormat("dd-MMM-yyyy").format(ss.getDate()));
					obj.put("review", ss.getDescription());
					obj.put("studentname", ss.getStudentName());
					obj.put("status", ss.getStatus());
					obj.put("feeamount", ss.getFeeamount());
					obj.put("paidby", ss.getPaidby());
					obj.put("ctno", ss.getCtno());

					if(ss.getAdmitclassid().equals(""))
					{
						obj.put("image","" );
					}
					else
					{
						SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
						obj.put("image",info.getDownloadpath()+ss.getAdmitclassid());
					}
					arr.add(obj);
				}
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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
