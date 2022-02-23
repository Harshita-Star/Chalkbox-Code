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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import schooldata.StudentInfo;
@ManagedBean(name="allTeacher")
@ViewScoped
public class allTeacherForMessageBean implements Serializable
{
	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public allTeacherForMessageBean(){

		Connection conn=DataBaseConnection.javaConnection();
		
        try {
        	Map<String, String> params =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestParameterMap();


    		String userid = params.get("id");
    		String type=params.get("type");
    		String schid=params.get("schid");

    		//new DatabaseMethods1().submitanswer(studentid,qid,answerstatus,answer);

    		//int i=new DatabaseMethods1().(userid);
    		
    		Map<String, String> sysParams =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestHeaderMap();
    		String userAgent = sysParams.get("User-Agent");
    		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
    		JSONObject mainobj = new JSONObject();
    		JSONArray arr=new JSONArray();
    		
    		if(checkRequest)
    		{
    			ArrayList<EmployeeInfo> employeeList;
    			if(type.equalsIgnoreCase("student"))
    			{
    				employeeList=DBM.searchEmployeebyCategorySchidd(schid,"1",conn);
    			}
    			else
    			{
    				employeeList=DBM.allstudentForChatting(schid,type,userid,conn);
    			}

    			for(EmployeeInfo ls:employeeList)
    			{

    				JSONObject obj = new JSONObject();

    				obj.put("name",ls.getFname());
    				obj.put("id",ls.getEmplyeeuserid());


    				arr.add(obj);
    				mainobj.put("SchoolJson", arr);
    			}

    		}
    		else
    		{
    			mainobj.put("SchoolJson", arr);
    		}

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
