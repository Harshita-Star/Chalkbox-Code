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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import exam_module.ExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="examperformancejsonbean")
@ViewScoped
public class Exam_Performance_Json_Bean implements Serializable
{

	String json;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();

	public Exam_Performance_Json_Bean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String classid = params.get("classid");
			String studentid = params.get("studentid");
			String termid = params.get("termid");
			String schoolid=params.get("Schoolid");
			String type=params.get("type");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			if(checkRequest)
			{
				ArrayList<ExamInfo> subExamList;
				String term="";
				String examid="";
			    if(type==null||type.equals("")||type.equalsIgnoreCase("scholastic")||type.equalsIgnoreCase("additional"))
			    {
			    	String temp[] = termid.split("-");
					 term = temp[0];
					 examid = temp[1];
		           	
			    }
			    else
			    {
			    	String temp[] = termid.split("-");
					 term = termid;
					 examid = "";
		           
			    }
				 if(type==null||type.equalsIgnoreCase(""))
	            {
	               subExamList = dbj.subExamListByTermAndExam(term,examid,classid,schoolid,conn);
	                dbj.studentExamPerformance(studentid, subExamList, schoolid, conn);
		
	            }
	            else
	            {
	            	
	             	subExamList = dbj.subExamListByTermAndExamforMainExam(term,examid,classid,schoolid,type,conn);
	             	if(type.equalsIgnoreCase("scholastic")||type.equalsIgnoreCase("additional"))
	    			{
	             		dbj.studentExamPerformance(studentid, subExamList, schoolid, conn);
	             			
	    			}
	             	else
	             	{
	             		dbj.studentExamPerformanceForOtherExam(studentid, subExamList, schoolid,type,conn);
	             		
	             	}
	                
	           
	            }
				
				for(ExamInfo ss : subExamList)
				{
					if(ss.getShow().equalsIgnoreCase("yes"))
					{
						if(ss.getShowExam().equalsIgnoreCase("yes"))
						{
							JSONObject obj = new JSONObject();
							obj.put("SubjectName", ss.getSubjectName());
							obj.put("Marks", ss.getMarksObtained());
							arr.add(obj);
						}
					}
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
