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
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="forgotpasswordbean")
@ViewScoped
public class ForgotPassword implements Serializable
{
	String json;

	ArrayList<StudentInfo> list;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public ForgotPassword() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			/*String date = params.get("date");

		        Date newDate=null;
		        try {
				newDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
			 */
			int randomPIN =0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				selectedStudent=new DatabaseMethods1("").selectedStudentDetailByAddNo(studentid,conn);

				SchoolInfoList info=DBM.fullSchoolInfo(conn);
				randomPIN = (int)(Math.random()*9000)+1000;
				String.valueOf(randomPIN);
				info.getSchoolName();
			}

			/*  try {
					new DatabaseMethods1().runMessageURL(String.valueOf(selectedStudent.getFathersPhone()), typemessage);
				} catch (UnsupportedEncodingException e) {
					
					e.printStackTrace();
				}*/

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			/* for(StudentInfo ss:list)
					    {*/
			JSONObject obj = new JSONObject();
			obj.put("otp",String.valueOf(randomPIN));
			arr.add(obj);
			/*}*/

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
