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

@ManagedBean(name="examDetailsByClassDetailsJsonBean")
@ViewScoped
public class ExamDetailsByClassDetailsJsonBean implements Serializable
{
	
	String json="";
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DataBaseMeathodJson objJSON=new DataBaseMeathodJson();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	
    public ExamDetailsByClassDetailsJsonBean() 
    {
    	Connection conn=DataBaseConnection.javaConnection();
    	try {
    		Map<String, String> params =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestParameterMap();
    		String schid = params.get("schid");
    		String classId=params.get("classId");
    		String teacherId=params.get("teacherid");
    		String sectionId=params.get("sectionId");
    		String termId=params.get("termId");
    		String examType=params.get("examType");
    		
    		
    		
    		
    		
    		JSONArray arr=new JSONArray();
            JSONArray arr1=new JSONArray();

    		Map<String, String> sysParams =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestHeaderMap();
    		String userAgent = sysParams.get("User-Agent");
    		boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
    		
    		if(checkRequest)
    		{
    			ArrayList<SelectItem>examTypeList ,allSubjectList;
    			
    			if(examType.equalsIgnoreCase("scholastic") || examType.equalsIgnoreCase("additional") )
    			{
    				examTypeList=objJSON.mainExamListOfClassSection(classId,examType.toLowerCase(),termId,schid,conn);
    				//examTypeList=DBM.selectedExamTypeOfClassSection(selectedClass, selectedTerm,selectedType,conn);
    				if(teacherId.equalsIgnoreCase("admin"))
    				{
    					allSubjectList=obj1.selectedSubjectTypeofClassSectionForExam(classId,examType.toLowerCase(),conn,schid);
    				}
    				else
    				{
    					String empid=objJSON.employeeIdbyEmployeeName(teacherId,schid,conn);
    					allSubjectList=objExam.AllEMployeeSubjectByTypeForExam(empid,sectionId,schid,examType.toLowerCase(),conn);
    				}
    				

    			}
    			else
    			{
    				examTypeList=new ArrayList<>();
    				
    				if(teacherId.equalsIgnoreCase("admin"))
    				{
    					allSubjectList=obj1.selectedSubjectTypeofClassSectionForExam(classId,examType.toLowerCase(),conn,schid);
    				}
    				else
    				{
    					String empid=objJSON.employeeIdbyEmployeeName(teacherId,schid,conn);
    					allSubjectList=objExam.AllEMployeeSubjectByTypeForExam(empid,sectionId,schid,examType.toLowerCase(),conn);
    				}
    		  }
    			
    			
    			for(SelectItem ls:examTypeList)
    			{
    				JSONObject obj1 = new JSONObject();
    				obj1.put("examId", String.valueOf(ls.getValue()));
    				obj1.put("examName",ls.getLabel());
    				arr.add(obj1);
    			}
              
    			for(SelectItem ls:allSubjectList)
    			{
    				
    					ArrayList<SelectItem>  examTypeList1=objJSON.mainExamListOfClassSection(classId,examType.toLowerCase(),termId,schid,conn);
    					String examId=(String) examTypeList1.get(0).getValue();
    				     boolean check=objJSON.checkExamSubject(examId,String.valueOf(ls.getValue()),conn);
    					if(check)
    					{
    						JSONObject obj2 = new JSONObject();
            				obj2.put("subjectId", String.valueOf(ls.getValue()));
            				obj2.put("subjectName",ls.getLabel());
            				arr1.add(obj2);
        				}
    				
    				
    			}
    		}
    		
    		JSONArray arr3=new JSONArray();
    		JSONObject obj3 = new JSONObject();
    		obj3.put("subject",arr1);
    		obj3.put("exam",arr);
    		arr3.add(obj3);
    		
    		json=arr3.toJSONString();
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
