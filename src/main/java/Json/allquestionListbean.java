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
import schooldata.StudentInfo;

@ManagedBean(name="allquestion")
@ViewScoped
public class allquestionListbean implements Serializable{



	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public allquestionListbean(){
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			if(checkRequest)
			{
				ArrayList<QuestionList> info=DBM.allquestion(studentid,conn);

				for(QuestionList ss:info)
				{
					JSONObject obj = new JSONObject();
					obj.put("qid",ss.getQid());
					obj.put("question", ss.getQuestion());
					obj.put("answer", ss.getAnswer());
					obj.put("o1", ss.getOp1());
					obj.put("o2", ss.getOp2());
					obj.put("o3", ss.getOp3());
					obj.put("o4", ss.getOp4());
					obj.put("anstatus", ss.getAnswerstatus());
					obj.put("studentans", ss.getStudentanswer());
					obj.put("ownrank", ss.getOwnrank());
					obj.put("r1", ss.getRank1());
					obj.put("r2", ss.getRank2());
					obj.put("r3", ss.getRank3());
					arr.add(obj);
				}
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
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
