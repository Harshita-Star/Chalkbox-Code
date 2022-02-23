package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.EmployeeInfo;

public class EmployeeBeanprofileJson implements Serializable
{

	String json;
	EmployeeInfo info;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public EmployeeBeanprofileJson() {

		Connection conn=DataBaseConnection.javaConnection();

        try {
        	Map<String, String> params =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestParameterMap();
    		String addmission = params.get("addmission");
    		String schid=params.get("Schoolid");

    		JSONObject mainobj = new JSONObject();
    		JSONArray arr=new JSONArray();
    		JSONObject obj = new JSONObject();

    		Map<String, String> sysParams =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestHeaderMap();
    		String userAgent = sysParams.get("User-Agent");
    		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
    		
    		if(checkRequest)
    		{
    			info=DBJ.searchEmployeeUsername(addmission,schid,conn);

    			if(info.getFname()==null||info.getFname().equals(""))
    			{
    				obj.put("name", "");

    			}
    			else
    			{
    				obj.put("name", info.getFname());

    			}
    			//obj.put("fathername", info.getFa);
    			obj.put("location", info.getAddress());
    			// obj.put("mothersname", info.getMotherName());
    			obj.put("gender", info.getGender());
    			obj.put("address", info.getAddress());
    			obj.put("DOB", info.getDobStr());
    			obj.put("Phoneno", info.getMobile());
    			obj.put("img", info.getEmpImage());

    			arr.add(obj);
    		}

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
