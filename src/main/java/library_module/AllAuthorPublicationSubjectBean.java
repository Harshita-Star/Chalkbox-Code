package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean(name="allAutPublSub")
@ViewScoped
public class AllAuthorPublicationSubjectBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String searchType,name,tableName,schid,idName;
	BookInfo selectedItem;
	ArrayList<BookInfo> list;
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();
	DatabaseMethods1 obj=new DatabaseMethods1();

	public AllAuthorPublicationSubjectBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void search()
	{
		idName();
		tableName();
		Connection conn=DataBaseConnection.javaConnection();
		list=objLibrary.allAuthPublSubList(schid,tableName,conn);
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void editDetails()
	{
		name=selectedItem.getAuthorName();
	}

	public void deleteAutPublSub()
	{
		Connection conn=DataBaseConnection.javaConnection();
		boolean check=objLibrary.checkAuthPublSubForDeletion(selectedItem.getAuhtorId(),schid,idName,conn);
		if(check==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Is Assigned With It.... Can Not Be Deleted"));
		}
		else
		{
			int i=objLibrary.deleteAuthor_Publication_Subject(selectedItem.getAuhtorId(),schid,tableName,conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Deleted Sucessfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.... Please Try Again"));
			}
			search();
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateAutPublSub()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=objLibrary.updateAuthor_Publication_Subject(selectedItem.getAuhtorId(),name,schid,tableName,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Sucessfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.... Please Try Again"));
		}
		search();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	public void tableName()
	{
		if(searchType.equals("Author"))
		{
			tableName="author_table";
		}
		else if(searchType.equals("Subject"))
		{
			tableName="lib_subject";
		}
		else if(searchType.equals("Publication"))
		{
			tableName="publication_table";
		}
	}

	public void idName()
	{
		if(searchType.equals("Author"))
		{
			tableName="authorId";
		}
		else if(searchType.equals("Subject"))
		{
			tableName="subjectId";
		}
		else if(searchType.equals("Publication"))
		{
			tableName="publicationId";
		}
	}

	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public BookInfo getSelectedItem() {
		return selectedItem;
	}
	public void setSelectedItem(BookInfo selectedItem) {
		this.selectedItem = selectedItem;
	}
	public ArrayList<BookInfo> getList() {
		return list;
	}
	public void setList(ArrayList<BookInfo> list) {
		this.list = list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
