package Json;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ExtraFieldInfo;
import schooldata.StudentInfo;

@ManagedBean(name="addstudentExtraValuesBeanJson")
@ViewScoped
public class AddStudentExtraValuesBeanJson implements Serializable {

	
	
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
	DatabaseMethods1 dbm=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	
	public AddStudentExtraValuesBeanJson()
	{
	
		 Connection conn=DataBaseConnection.javaConnection();

		 try 
		 {
			 Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		    	String username = params.get("username");
		    	String usertype= params.get("usertype");
		    	schid = params.get("schid");
				 classId=params.get("classId");
				 sectionId=params.get("sectionId");
				 termId=params.get("termId");
				 examType=params.get("type");
				
	   			String data=params.get("data");
	   			JSONObject obj = new JSONObject();
	   			int j=0;
	   			String message="";
	   			 Type type = new TypeToken<java.util.List<ExtraFieldInfo>>(){}.getType();
				    ArrayList<ExtraFieldInfo> contactList = new Gson().fromJson(data, type);
				   String  session=dbm.selectedSessionDetails(schid,conn);
				    int i=DBJ.addExtraValueOfStudent(contactList,sectionId,termId,examType,conn,session,schid);
				    
				   if(i>0)
				   {
					   String schoolid=schid;
						String className=dbm.classname(classId, schoolid, conn);
						String sectionname =dbm.sectionNameByIdSchid(schoolid,sectionId, conn);
						String termName=dbm.semesterNameFromidschoolid(termId,schoolid,conn);
						String typeOfArea =examType;
						
						
						String value="";
						if(examType.equalsIgnoreCase("Remark"))
						{
							for(ExtraFieldInfo ls:contactList)
							{
								if(value.equals(""))
								{
									value="("+ls.getStudentId()+"----"+ls.getStudentName()+"-----:"+ls.getRemark()+")";
								}
								else
								{
									value=value+"("+ls.getStudentId()+"----"+ls.getStudentName()+"-----:"+ls.getRemark()+")";
								}
							}
								
						}
						
						
						String language=""+className+"-"+sectionname+"\n,"+""+termName+"\n,"+typeOfArea ;
						
					
						
						//language=language;
						
						String addedBy=dbm.employeeNameByuserNameschool(username,schoolid,conn);
						
						language=language+"\n,"+addedBy+"("+username+"-"+usertype+")" +"\n"+"  Submitted Successfully";
						
					
						String refNo =dbm.saveWorkLog(schoolid,language, username, typeOfArea,usertype, session, "APP",value, conn);
						if(refNo.equals(""))
						{
							j=0;
				            message="Dear User, \n Some Error Occure Please try again";
							
						}
						else
						{
							j=1;
							message="Dear User, \n Student Extra Values/Remark updated successfully. Please Note Down Your Reference No : "+refNo+" For Any Further Query.";
						}
				   }
				   else
				   {
					   j=0;
					   message="Dear User, \n Some Error Occure Please try again"; 
				   }
				    
				
				   JSONArray arr=new JSONArray();
				   
				   
				   obj.put("status", String.valueOf(j));
				   obj.put("msg", message);
				   
				   arr.add(obj);
				   
				   
				   json=arr.toJSONString();
		 }
		 catch (Exception e)
		 {
		 	// TODO: handle exception
		 }
		 finally
		 {
			 try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
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
