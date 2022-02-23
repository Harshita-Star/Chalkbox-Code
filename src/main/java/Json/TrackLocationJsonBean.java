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

@ManagedBean(name="trackLocation")
@ViewScoped
public class TrackLocationJsonBean implements Serializable
{
	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public TrackLocationJsonBean(){

		Connection conn=DataBaseConnection.javaConnection();
		

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();


			String id = params.get("id");
			String lati=params.get("latitude");
			String longi=params.get("longitude");
			int i = 0;
			i=  DBM.submitlocation(id,lati,longi,conn);
			//int i=new DatabaseMethods1().news(concern);
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			JSONObject obj = new JSONObject();


			String status="";
			if(i>0)
			{
				status="u";
			}
			else
			{
				status="nu";
			}

			obj.put("st",status);

			arr.add(obj);


			mainobj.put("SJ", arr);

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
