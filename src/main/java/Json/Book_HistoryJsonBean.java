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
import schooldata.DatabaseMethods1;

@ManagedBean(name="bookHistoryJson")
@ViewScoped

public class Book_HistoryJsonBean implements Serializable
{
	String json;
	ArrayList<BookInfo> allBook;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMethodsLibraryModule objLib = new DataBaseMethodsLibraryModule();

	public Book_HistoryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schId");
			String bookId=params.get("bookId");

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				allBook=objLib.bookHistoryByBookId(bookId,schId, conn);
				for(BookInfo ss:allBook)
				{
					JSONObject obj = new JSONObject();
					obj.put("articleId",ss.getArticleId());
					obj.put("accessionNo", ss.getBookId());
					obj.put("bookName",ss.getBookName());
					obj.put("authorName", ss.getAuthorName());
					obj.put("publicationName", ss.getPublicationName());
					obj.put("subjectName", ss.getSubjectName());
					obj.put("assignDate", ss.getAssignDateStr());
					obj.put("expireDate", ss.getExpireDateStr());
					obj.put("receiveStatus", ss.getReceivedStatus());
					obj.put("type", ss.getType());
					obj.put("name", ss.getStudentName());
					arr.add(obj);
				}

			}
			//mainobj.put("SchoolJson", arr);

			json=arr.toJSONString();
	
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
