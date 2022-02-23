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
import schooldata.ExtraFieldInfo;
import schooldata.StudentInfo;

@ManagedBean(name="studentRemarkExtraValuesJsonBean")
@ViewScoped
public class StudentRemarkExtraValuesJson implements Serializable{
	
	
	
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
	
	public StudentRemarkExtraValuesJson() {
	
		
		 Connection conn=DataBaseConnection.javaConnection();
		 
		 try {
			 DataBaseMeathodJson objJson=new DataBaseMeathodJson();
		    	
		    	Map<String, String> params =FacesContext.getCurrentInstance().
						getExternalContext().getRequestParameterMap();
				 schid = params.get("schid");
				 classId=params.get("classId");
				// teacherId=params.get("teacherid");
				 sectionId=params.get("sectionId");
				 termId=params.get("termId");
				 examType=params.get("type");
				
				 
				 ArrayList<ExtraFieldInfo> studentList=new DataBaseMethodsExam().studentListByClassId(schid,sectionId,conn);
				 
				 new DataBaseMethodsExam().checkAlreadyFilledValue1(schid,sectionId,studentList,termId,conn);
					
				 JSONArray arr=new JSONArray();
					
				 
				 
				 
					for(ExtraFieldInfo ls:studentList)
					{
						JSONObject obj1 = new JSONObject();
						obj1.put("roll_no", ls.getRollNo());
						obj1.put("studentname",ls.getStudentName());
						obj1.put("studentid", ls.getStudentId());
					    obj1.put("id",ls.getId());
					    obj1.put("height",ls.getHeight());
					    obj1.put("weight", ls.getWeight());
					    obj1.put("vision",ls.getVision());
					    obj1.put("attendance",ls.getAttendance());
					    obj1.put("maxattendance",ls.getMaxAttendance());
					    obj1.put("remark",ls.getRemark());
					  	arr.add(obj1);
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
}
