package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import exam_module.DataBaseMethodsExam;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="subExamListByExamIdJsonBean")
@ViewScoped
public class SubExamListByExamIdJsonBean implements Serializable{

String json="";
String selectedSession="";
String sub=	"";
int marks;
ArrayList<SelectItem>subExamList;
String subExam;
String subExamTemp="",examNameForCoscholastic="",selectedExam="";
String maxMarks;
String schid,classId,teacherId,sectionId,termId,examType,examId,SubjectId;
ArrayList<StudentInfo> studentDetails;
DataBaseMeathodJson dbj = new DataBaseMeathodJson();
DatabaseMethods1 DBM=new DatabaseMethods1();



public SubExamListByExamIdJsonBean() 
{
	Connection conn=DataBaseConnection.javaConnection();
	try {

    	Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();
		 schid = params.get("schid");
		 classId=params.get("classId");
		// teacherId=params.get("teacherid");
		 sectionId=params.get("sectionId");
		 termId=params.get("termId");
		 examType=params.get("examType");
		 examId=params.get("examId");
		
		
		JSONArray arr=new JSONArray();
		
		Map<String, String> sysParams =FacesContext.getCurrentInstance().
				getExternalContext().getRequestHeaderMap();
		String userAgent = sysParams.get("User-Agent");
		boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
		if(checkRequest)
		{
			String session=DBM.selectedSessionDetails(schid, conn);
			subExamList=new DataBaseMethodsExam().subExamListOfClassSection(classId,examType,examId,termId,session,conn);
			for(SelectItem ls:subExamList)
			{
				if(String.valueOf(ls.getValue()).contains("sub"))
				{
					JSONObject obj1 = new JSONObject();
					obj1.put("subExamName",String.valueOf(ls.getLabel()));
					obj1.put("SubExamId",String.valueOf(ls.getValue()));
					arr.add(obj1);
				}
				
			}
		}
		
		json=arr.toJSONString();
	} catch (Exception e) {
		// TODO: handle exception
	}
	finally {
		try {
			conn.close();
		} catch (Exception e) {
			
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
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}



}
