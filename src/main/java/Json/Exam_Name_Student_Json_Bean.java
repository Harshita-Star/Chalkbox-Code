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
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import exam_module.ExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;


@ManagedBean(name="examnamestudentjsonbean")
@ViewScoped
public class Exam_Name_Student_Json_Bean  implements Serializable
{

	String json;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();


	public Exam_Name_Student_Json_Bean()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String addmission = params.get("classid");
			String schoolid=params.get("Schoolid");
			String status=params.get("status");
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr2=new JSONArray();
			
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				
				if(status==null||status.equalsIgnoreCase("off"))
				{
					
						     ArrayList<ExamInfo> info=dbj.examNameFromclassid(addmission,schoolid,conn);
	                        JSONArray arr=new JSONArray();
							for(ExamInfo ss:info)
							{
								JSONObject obj = new JSONObject();
								obj.put("ExamName", ss.getExamName());
								obj.put("termId", ss.getSemesterid());
								arr2.add(obj);

							}
							
						
						mainobj.put("SchoolJson", arr2);
						json=mainobj.toJSONString();
							
					
				}
				else
				{
					ArrayList<SelectItem> list=dbm.subjectTypeList();
					
					for(SelectItem ss1:list)
					{
						ArrayList<ExamInfo> info=dbj.examNameFromclassidMainExam(addmission,schoolid,ss1.getValue().toString(),conn);

						
						
						if(info.size()>0)
						{
							
							
							JSONObject obj1 = new JSONObject();
							obj1.put("type",ss1.getValue().toString() );
							JSONArray arr=new JSONArray();
							for(ExamInfo ss:info)
							{
								JSONObject obj = new JSONObject();
								obj.put("ExamName", ss.getExamName());
								obj.put("termId", ss.getSemesterid());
								arr.add(obj);

							}
							
							obj1.put("result",arr);
			               arr2.add(obj1);
						}
						
						
							
					}
					json=arr2.toJSONString();
				}
				
				
				
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
