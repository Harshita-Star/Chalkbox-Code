package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="srnoJosn")
@ViewScoped

public class SrnoJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 obj = new DatabaseMethods1();

	public SrnoJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("Schoolid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList info = DBJ.fullSchoolInfo(schid,conn);
				String srnoType = info.getSrnoType();
				String srno = "";
				if(srnoType.equalsIgnoreCase("manual"))
				{
					srno = "";
				}
				else
				{
					boolean check = obj.checkStudentsInSchool(schid,conn);
					if(check==false)
					{
						srno = info.getSrnoPrefix()+info.getSrnoStart();
					}
					else
					{
						srno = info.getSrnoPrefix()+obj.autoGeneratedSrNo(schid,(info.getSrnoPrefix().length()+1),conn);
					}
				}
				
				JSONObject mobj = new JSONObject();

				mobj.put("srnoType", srnoType);
				mobj.put("srno", srno);

				arr.add(mobj);
			}

			json=arr.toJSONString();
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
