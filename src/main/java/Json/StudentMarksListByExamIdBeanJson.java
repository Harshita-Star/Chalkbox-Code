package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;


@ManagedBean(name="studentMarksListByExamIdBeanJson")
@ViewScoped
public class StudentMarksListByExamIdBeanJson implements Serializable{
	
	
String json="";
String selectedSession="";
String sub=	"";
Double marks;
ArrayList<SelectItem>subExamList;
String subExam;
String subExamTemp="",examNameForCoscholastic="",selectedExam="";
String maxMarks;
String schid,classId,teacherId,sectionId,termId,examType,examId,SubjectId;
ArrayList<StudentInfo> studentDetails;
DataBaseMeathodJson objJson=new DataBaseMeathodJson();
DatabaseMethods1 DBM = new DatabaseMethods1();
DataBaseMeathodJson objJSON=new DataBaseMeathodJson();


public StudentMarksListByExamIdBeanJson() 
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
			 SubjectId=params.get("subjectId");
			 subExam=params.get("sub_exam");

			
			 
			 JSONArray arr=new JSONArray();
				
			 Map<String, String> sysParams =FacesContext.getCurrentInstance().
						getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = objJson.checkRequestAuthenticity(userAgent); // If true, proceed else block
			if(checkRequest)
			{
				
				
				ArrayList<SelectItem>examTypeList ,allSubjectList;
				
				if(examType.equalsIgnoreCase("scholastic")|| examType.equalsIgnoreCase("additional"))
				{
					
					selectedSession=DBM.selectedSessionDetails(schid,conn);
					subExamList=new DataBaseMethodsExam().subExamListOfClassSection(classId,examType,examId,termId,selectedSession,conn);
					subExamValue();
					sub=objJson.subject(classId,/*selectedTerm,*/examType,SubjectId,schid,conn);
					marks=objJson.subjectMaxMarksFromId(classId,examId,subExamTemp,SubjectId,examType,selectedSession,termId,schid,conn);
					//("marks 1........"+marks);
					/*marks=obj.subjectMaxMarksFromid(selectedClass, selectedTerm, selectedExam, selectedSubject,selectedType,session,conn);
					//("marks 2........"+marks);*/
					

					maxMarks=String.valueOf(marks);
					sub=sub+"("+maxMarks+")";
					
					studentDetail(conn);
				}
				else
				{
					selectedSession=DBM.selectedSessionDetails(schid,conn);
					
					sub=objJson.subject(classId,/*selectedTerm,*/examType,SubjectId,schid,conn);
					
					
					examTypeList=objJSON.mainExamListOfClassSection(classId,examType.toLowerCase(),termId,schid,conn);
					examId=(String) examTypeList.get(0).getValue();
					examNameForCoscholastic=(String) examTypeList.get(0).getLabel();
					
					/*examId=objJson.mainExamIdFromSubjectForCoscholastic(classId,examNameForCoscholastic,selectedSubject,selectedSession,termId,examType,schid,conn);
					selectedExam=examId;*/

					marks=objJson.subjectMaxMarksFromId(classId,examId,examNameForCoscholastic,SubjectId,examType,selectedSession,termId,schid,conn);
					/*marks=obj.subjectMaxMarksFromid(selectedClass, selectedTerm, selectedExam, selectedSubject,selectedType,session,conn);
					//("marks 2........"+marks);*/
				//	renderShowTable=true;
					maxMarks=String.valueOf(marks);
					sub=sub+"("+maxMarks+")";
					
					studentDetail(conn);
				}
				
				for(StudentInfo ls:studentDetails)
				{
					JSONObject obj1 = new JSONObject();
					obj1.put("roll_no", ls.getRollNo());
					obj1.put("studentname",ls.getFname());
					obj1.put("add_number",ls.getSrNo());
					obj1.put("studentid", ls.getAddNumber());
					if(ls.getMarks().equals(""))
					{
						obj1.put("marks","0");
						
					}
					else
					{
						obj1.put("marks",ls.getMarks());
								
					}
					obj1.put("maxmarks",String.valueOf(marks));
					obj1.put("subject", sub);
					arr.add(obj1);
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
    
    
    public void subExamValue()
	{
		if(subExamList.size()>0)
		{
			String value=(String) subExamList.get(0).getValue();
			if(value.contains("sub"))
			{
				subExamTemp=subExam.substring(0,subExam.lastIndexOf("/"));
			}
			else
			{
				subExamTemp=value.substring(0,value.lastIndexOf("/"));
			}
		}
	}
    
    
    public void studentDetail(Connection conn)
	{
		
    	
		String subjectType=objJson.checkSubjectType(SubjectId,schid,conn);
		//// // System.out.println(subjectType);
		if(subjectType.equalsIgnoreCase("Mandatory"))
		{
			studentDetails=objJson.searchStudentListByClassSection1(sectionId,/*selectedSubject,*/schid,conn);
		}
		else
		{
			studentDetails=objJson.searchStudentListBySubject(classId,sectionId,SubjectId,schid,conn);
		}
		
		if(examType.equalsIgnoreCase("coscholastic")||examType.equalsIgnoreCase("other"))
		{
			subExamTemp=examNameForCoscholastic;
		}
		objJson.allPerformance(sectionId,examId,subExamTemp, studentDetails, /*subjectList,*/examType,SubjectId,termId,schid,conn);
		//	subjectList=obj.subjectListDetailsOfPerticularClassForPerformance(selectedClass, selectedType, selectedTerm, selectedExam);
		//subjectList=obj.subjectListDetailsOfPerticularClassForPerformance1(selectedClass, selectedType, selectedTerm,selectedSubject, selectedExam);
		if(studentDetails.size()>0 )
		{
			
			SchoolInfoList info = objJson.fullSchoolInfo(schid,conn);
			if(info.getBranch_id().equals("54"))
			{
				Collections.sort(studentDetails, new MySalaryComp());
			}
			else 
			{
                if(!info.getBranch_id().equals("52"))
				{
					Collections.sort(studentDetails, new MyRollNoComp());
				}


			}
			
			
		}
		
	}

	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}


	class MyRollNoComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			//	String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			//String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			if(e1.getRollNo()==null||e2.getRollNo()==null||e2.getRollNo().equals("")||e1.getRollNo().equals(""))
			{
				return 0;
			}
			else
			{
				if(e2.getRollNo().matches("-?\\d+(\\.\\d+)?")||e1.getRollNo().matches("-?\\d+(\\.\\d+)?"))
				{
					try
					{
						int sr1 = Integer.parseInt(e1.getRollNo());
						int sr2 = Integer.parseInt(e2.getRollNo());

						if(sr1 >= sr2)
						{
							return 1;
						}
						else
						{
							return -1;
						}
					}
					catch (Exception e)
					{
						return 0;
					}


				}
				else
				{
					return 0;


				}

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
