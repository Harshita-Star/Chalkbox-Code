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

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;
@ManagedBean(name="branchJson")
@ViewScoped

public class BranchJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public BranchJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String branchid=params.get("branchid");
			String type=params.get("type");
			
			if(type==null||type.equals(""))
			{
				type="old";
			}
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
			JSONArray arr=new JSONArray();
			JSONArray arr2=new JSONArray();
			JSONArray arr3=new JSONArray();
			JSONObject mainobj = new JSONObject();
			if(checkRequest)
			{
				ArrayList<SelectItem> list = new ArrayList<>();
				list = DBJ.allBranchList(branchid, conn);
				
				JSONObject objj = new JSONObject();

				objj.put("name", "Select Branch");
				objj.put("schid", "");
				objj.put("schoolcode", "");
				arr2.put(objj);

				if(list.size()>0)
				{
					for(SelectItem ss : list)
					{
						JSONObject obj = new JSONObject();

						obj.put("name", ss.getLabel());
						obj.put("schid", String.valueOf(ss.getValue()));
						obj.put("schoolcode", ss.getDescription());
						arr2.put(obj);
					}
				}
			}

			
			JSONObject obj = new JSONObject();
	        obj.put("login_type", DBJ.BranchLoginType(branchid, conn));
	        arr.put(obj);
			
	        
	        mainobj.put("login",arr);
	        mainobj.put("Branch", arr2);
	        
	        arr3.put(mainobj);
	        
			if(type.equals("old"))
			{
				json=arr2.toString();
			}
			else
			{
				json=arr3.toString();
			}
			
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

	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();
	}
}
