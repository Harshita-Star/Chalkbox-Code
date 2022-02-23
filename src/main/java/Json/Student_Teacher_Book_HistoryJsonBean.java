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

@ManagedBean(name="studentTeacherBookJson")
@ViewScoped

public class Student_Teacher_Book_HistoryJsonBean implements Serializable
{
	String json;
	ArrayList<BookInfo> allBook;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public Student_Teacher_Book_HistoryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schId");
			String studentId=params.get("studentId");

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				allBook=new DataBaseMethodsLibraryModule().studentBookHistory(studentId,schId, conn);
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
