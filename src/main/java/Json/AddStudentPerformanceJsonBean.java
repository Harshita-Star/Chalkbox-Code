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

import exam_module.DataBaseMethodsBLMExam;
import exam_module.DataBaseMethodsExam;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="addStudentPerformanceJsonBean")
@ViewScoped
public class AddStudentPerformanceJsonBean implements Serializable{


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
String message="";
DataBaseMeathodJson objJson=new DataBaseMeathodJson();
DatabaseMethods1 obj=new DatabaseMethods1();
DataBaseMethodsBLMExam objBLM=new DataBaseMethodsBLMExam();
DataBaseMethodsExam objExam=new DataBaseMethodsExam();


    public AddStudentPerformanceJsonBean() 
    {
    	
   	    Connection conn = DataBaseConnection.javaConnection();
   	    
		try 
		{
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
			 String StudentNo=params.get("studentId");
			 String studentMarks=params.get("studentMarks");
			 subExam=params.get("sub_exam");
			 
			 String username=params.get("username");
			 String usertype=params.get("usertype");
		
			 String  session=obj.selectedSessionDetails(schid,conn);
			 int j=0;
			
			
			DataBaseMeathodJson objJSON=new DataBaseMeathodJson();
			
			
			
			
			if(examType.equalsIgnoreCase("scholastic")|| examType.equalsIgnoreCase("additional"))
			{
				
				 selectedSession=obj.selectedSessionDetails(schid,conn);
				 subExamList=objExam.subExamListOfClassSection(classId,examType,examId,termId,selectedSession,conn);
					
				 subExamValue();
				sub=objJson.subject(classId,/*selectedTerm,*/examType,SubjectId,schid,conn);
				marks=objJson.subjectMaxMarksFromId(classId,examId,subExamTemp,SubjectId,examType,selectedSession,termId,schid,conn);
				//("marks 1........"+marks);
				/*marks=obj.subjectMaxMarksFromid(selectedClass, selectedTerm, selectedExam, selectedSubject,selectedType,session,conn);
				//("marks 2........"+marks);*/

				maxMarks=String.valueOf(marks);
				sub=sub+"("+maxMarks+")";
			
				//studentDetail(conn);
			}
			else
			{
				ArrayList<SelectItem>examTypeList;
				examTypeList=objJSON.mainExamListOfClassSection(classId,examType.toLowerCase(),termId,schid,conn);
				examId=(String) examTypeList.get(0).getValue();
				
				subExamList=objExam.subExamListOfClassSection(classId,examType,examId,termId,selectedSession,conn);
				selectedSession=obj.selectedSessionDetails(schid,conn);
				
				sub=objJson.subject(classId,/*selectedTerm,*/examType,SubjectId,schid,conn);
				examNameForCoscholastic=(String) examTypeList.get(0).getLabel();

				/*examId=objJson.mainExamIdFromSubjectForCoscholastic(classId,examNameForCoscholastic,selectedSubject,selectedSession,termId,examType,schid,conn);
				selectedExam=examId;*/

				marks=objJson.subjectMaxMarksFromId(classId,examId,examNameForCoscholastic,SubjectId,examType,selectedSession,termId,schid,conn);
				/*marks=obj.subjectMaxMarksFromid(selectedClass, selectedTerm, selectedExam, selectedSubject,selectedType,session,conn);
				//("marks 2........"+marks);*/
			//	renderShowTable=true;
				maxMarks=String.valueOf(marks);
				sub=sub+"("+maxMarks+")";
				//studentDetail(conn);
			}

			
			
			String[] studentLs=StudentNo.split(",");
			String[] studentNols=studentMarks.split(",");
			
			ArrayList<StudentInfo>list=new ArrayList<>();
			for(int i=0;i<studentLs.length;i++)
			{
				
				StudentInfo lss=new StudentInfo();
				lss.setAddNumber(studentLs[i]);
				lss.setMarks(studentNols[i]);
			    list.add(lss);
			}
			
			
			
			
			if(examType.equalsIgnoreCase("coscholastic")||examType.equalsIgnoreCase("other"))
			{
				subExamTemp=examNameForCoscholastic;
			}
			/*boolean ls= obj.performanceSectionForSaveAndMessage(selectedSection, selectedTerm, selectedExam, studentDetails,
					subjectList,selectedType,selectedSubject,selectedabsentStudent,conn);*/
			boolean ls=objJSON.addStudentPerformance(sectionId,examId,subExamTemp, list, /*subjectList,*/examType,SubjectId,maxMarks,termId,conn,selectedSession,schid);
			/* renderShowTable=false;
		    selectedClass=selectedExam=selectedTerm=selectedType=selectedSection=selectedSubject=null;
		    showExam=false;*/
			
			 // System.out.println(ls);
			if(ls==true)
			{
				
				if(username==null)
				{
					j=1;
					message="Performance record updated successfully";
				}
				else
				{
					
					try {
						
						    String schoolid=schid;
						    String className=obj.classname(classId, schoolid, conn);
							String sectionname =obj.sectionNameByIdSchid(schoolid,sectionId, conn);
							String termName=obj.semesterNameFromidschoolid(termId,schoolid ,conn);
							String typeOfArea =examType;
							 // System.out.println("examId_____"+examId);
							String subjectName=obj.subjectNameFromid(SubjectId, conn);
							String examName=obj.examNameFromidFromschid(examId,schoolid ,conn);
							String subExamName=subExamTemp;
							String language=""+className+"-"+sectionname+"\n,"+""+termName+"\n,"+typeOfArea ;
							String description = "Class-"+classId+",Section-"+sectionId+",Term-"+termId+","+typeOfArea;
							if(!examName.equals(""))
							{
								language=language+"\n,"+examName;
								description += ",Exam-"+examId;
								
							}
							
							if(!subExamName.equals(""))
							{
							   if(!examName.equals(subExamName))
							   {
								   language=language+"\n,"+subExamName;
								   description += ",SubExam-"+subExamTemp;
							   }
								
							}
							
							language=language+"\n,"+subjectName;
							description += ",Subject-"+SubjectId;
							
							String addedBy=obj.employeeNameByuserNameschool(username,schoolid ,conn);
							
							language=language+"\n,"+addedBy+"("+username+"-"+usertype+")" +"\n"+" Marks Submitted Successfully";
							
							String value="";
							
							for(StudentInfo lss:list)
							{
								try {
									
								} catch (Exception e) {
									// TODO: handle exception
								}
								if(value.equals(""))
								{
									if(lss.getMarks().equals(""))
									{
										value="("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:0/"+maxMarks+")";
										
									}
									else
									{
										value="("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:"+lss.getMarks()+"/"+maxMarks+")";
													
									}
								}
								else
								{
									if(lss.getMarks().equals(""))
									{
										value=value+"("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:0/"+maxMarks+")";
										
									}
									else
									{
										value=value+"("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:"+lss.getMarks()+"/"+maxMarks+")";
														
									}
								}
							}
								
						
							 // System.out.println("in else another");
						
						
						String refNo =obj.saveWorkLog(schoolid,language, username, "MARKS FEEDING",usertype, session, "APP",value,description, conn);
						
						if(refNo.equals(""))
						{
							j=0;
						     message="Dear User, \n Some Error Occure Please try again";
								
						}
						else
						{
							j=1;
							message="Dear User, \n Performance record updated successfully. Please Note Down Your Reference No : "+refNo+" For Any Further Query.";
							
						}
						
					} catch (Exception e) {
					   e.printStackTrace();
					}
					   
						
					
						
				}
				
				
					
			
			}
			else
			{
				j=0;
			     message="Dear User, \n Some Error Occure Please try again";	
			}
			
//			for(StudentInfo ls:studentDetails)
//			{
//				
//				JSONObject obj1 = new JSONObject();
//				obj1.put("roll_no", ls.getRollno());
//				obj1.put("studentname",ls.getFname());
//				obj1.put("add_number",ls.getSrNo());
//				obj1.put("studentid", ls.getStudentid());
//				obj1.put("marks",ls.getMarks());
//				obj1.put("maxmarks",maxMarks);
//				obj1.put("subject", sub);
//				arr.add(obj1);
//				
//				
//				
//			}
			
			
		
	       
	            JSONArray arr=new JSONArray();
			    JSONObject obj1 = new JSONObject();
			    obj1.put("status", String.valueOf(j));
				obj1.put("msg", message);
				arr.add(obj1);
			   json=arr.toJSONString();
			
			
			
		
			
			
			
			
		    
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
		

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
		objJson.allPerformance(sectionId,examId,subExamTemp, studentDetails, /*subjectList,*/examType,SubjectId,termId,schid,conn);
		if(studentDetails.size()>0 )
		{
			
			SchoolInfoList info = objJson.fullSchoolInfo(schid,conn);
			if(info.getBranch_id().equals("54"))
			{
				Collections.sort(studentDetails, new MySalaryComp());
			}
			else {

				//(info.getBranch_id());

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
