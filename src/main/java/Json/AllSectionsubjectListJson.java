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
@ManagedBean(name="allSection")
@ViewScoped
public class AllSectionsubjectListJson implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AllSectionsubjectListJson() {

		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String id = params.get("id");
			String schid=params.get("Schoolid");
			String type=params.get("type");
			//String tag=params.get("tag");
			String username=params.get("username");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			//// // System.out.println(userAgent);
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			if(checkRequest)
			{
				////// // System.out.println(type+"...."+username);
				ArrayList<SelectItem> subjectList=new ArrayList<>();
				ArrayList<SelectItem> sectionList=new ArrayList<>();

				if(type.equalsIgnoreCase("admin")
						|| type.equalsIgnoreCase("academic coordinator")
						|| type.equalsIgnoreCase("principal")
						|| type.equalsIgnoreCase("vice principal")
						|| type.equalsIgnoreCase("authority")
						|| type.equalsIgnoreCase("Accounts"))
				{
					subjectList=DBJ.allSubjectClassWise(id,schid,conn);
					sectionList=DBJ.allSection(id,schid,conn);
				}
				else
				{
					String tid=DBJ.teacherid(username, schid, conn);
					/*if(tag.equals("android"))
				    	{
				    		subjectList=DBJ.allSubjectTeacherSectionWise(id,schid,conn,tid);
				    	}
				    	else
				    	{*/
					subjectList=DBJ.allSubjectTeacherWise(id,schid,conn,tid);
					//}

					sectionList=DBJ.allSectionTeacherAndClassWise(id,schid,conn,tid); //NEED TO CHECK ON PRIORITY
				}




				String sectionid="select id",sectionname="Select Section",subjectid="All",subjectname="All";
				for(SelectItem ss:subjectList)
				{
					
					if(subjectid.contains(String.valueOf(ss.getValue())))
					{
						
					}
					else
					{
						subjectid=subjectid+","+ss.getValue();
						subjectname=subjectname+","+ss.getLabel();
							
					}
					
				}


				for(SelectItem ss:sectionList)
				{
					sectionid=sectionid+","+ss.getValue();
					sectionname=sectionname+","+ss.getLabel();
				}

				JSONObject obj = new JSONObject();
				obj.put("SubjectName", subjectname);
				obj.put("Subjectid", subjectid);
				obj.put("Sectionname", sectionname);
				obj.put("Sectionid", sectionid);

				arr.add(obj);
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		

		
		//type =student

	}









	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}



	private static final long serialVersionUID = 1L;
	private String id;
	private String pswd;

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}





}
