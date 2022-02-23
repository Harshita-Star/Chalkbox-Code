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

@ManagedBean(name="allBookJson")
@ViewScoped

public class AllBookJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	ArrayList<BookInfo> allBook;

	public AllBookJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schid");

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				allBook=new DataBaseMethodsLibraryModule().allBookReport(schId,"from Json",conn,"");
				for(BookInfo ss:allBook)
				{
					JSONObject obj = new JSONObject();
					obj.put("articleId",ss.getArticleId());
					obj.put("accessionNo", ss.getBookId());
					obj.put("bookName",ss.getBookName());
					obj.put("keyword", ss.getKeyword_book());
					obj.put("authorName", ss.getAuthorName());
					obj.put("publicationName", ss.getPublicationName());
					obj.put("subjectName", ss.getSubjectName());
					obj.put("subTitle", ss.getSubTitle());
					obj.put("yearOfPublish", ss.getYearOfPublish());
					obj.put("page", ss.getPage());
					obj.put("source", ss.getSource());
					obj.put("callNo", ss.getCallNo());
					obj.put("isbnNo", ss.getIsbnNo());
					obj.put("bookNo", ss.getBookNo());
					obj.put("bookPrice", ss.getBookPrice());
					obj.put("bookcategoryName", ss.getBookCategoryName());
					obj.put("addDate", ss.getAddDateStr());
					obj.put("status", ss.getReceivedStatus());
					arr.add(obj);
				}
			}

			//mainobj.put("SchoolJson", arr);

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
