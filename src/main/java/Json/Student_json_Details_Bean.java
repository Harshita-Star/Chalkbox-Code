package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;


@ManagedBean(name="studentjsondetailsbean")
@ViewScoped
public class Student_json_Details_Bean implements Serializable
{
	String json;
	StudentInfo info;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public Student_json_Details_Bean() {

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String addmission = params.get("addmission");
			String schid=params.get("Schoolid");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				// DataBaseJsonDeatil().updateAppDownloadStatusByAdmissionNo(addmission,conn);
				info=DBJ.studentDetailslistByAddNo(addmission,schid,conn);
				SchoolInfoList list=new DataBaseJsonDeatil().fullSchoolInfo(schid, conn);
				String flyerStatus = new DataBaseJsonDeatil().flyerStatus(schid,conn);
				JSONObject obj = new JSONObject();

				if(info.getFname()==null||info.getFname().equals(""))
				{
					obj.put("studentname", "");

				}
				else
				{
					obj.put("studentname", info.getFname());

				}
				obj.put("fathername", info.getFathersName());
				obj.put("classname", info.getClassName());
				obj.put("mothersname", info.getMotherName());
				obj.put("gender", info.getGender());
				obj.put("address", info.getCurrentAddress());
				obj.put("DOB", info.getDobString());
				obj.put("Phoneno", info.getFathersPhone());
				obj.put("classid", info.getClassId());
				obj.put("sectionid", info.getSectionid());
				obj.put("img", info.getStudent_image());
				obj.put("sr_no", info.getSrNo());
				obj.put("blood_grp", info.getBloodGroup());
				obj.put("q_show",list.getqIconShow());
				obj.put("show_flyer",flyerStatus);

				arr.add(obj);
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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


}
