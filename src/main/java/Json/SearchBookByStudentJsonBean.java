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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import library_module.BookInfo;
import library_module.DataBaseMethodsLibraryModule;
import schooldata.DataBaseConnection;

@ManagedBean(name="searchBookJson")
@ViewScoped

public class SearchBookByStudentJsonBean implements Serializable
{
	String json;
	ArrayList<BookInfo> allBook;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public SearchBookByStudentJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String id=params.get("id");
			String schId=params.get("schid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				allBook=new DataBaseMethodsLibraryModule().searchBookByType(id,schId, conn);
				for(BookInfo ss:allBook)
				{
					JSONObject obj = new JSONObject();
					obj.put("articleId",ss.getArticleId());
					obj.put("accessionNo", ss.getBookId());
					obj.put("bookName",ss.getBookName());
					obj.put("addDate", ss.getAddDateStr());
					obj.put("author", ss.getAuthorName());
					obj.put("publication", ss.getPublicationName());
					obj.put("subject", ss.getSubjectName());
					obj.put("receiveStatus", ss.getReceivedStatus());
					arr.add(obj);
				}
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
