package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import session_work.RegexPattern;

@ManagedBean(name="assignCourseware")
@ViewScoped

public class AssignCoursewareBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String bookName,studentName,articleId,bookId,type,className,bookImage,session,schid;

	Date addDate=new Date();
	ArrayList<BookInfo> bookList,assignBookList;
	ArrayList<String> bookIdList;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem> freeBookList;
	String classid = "";
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();
	DatabaseMethods1 obj=new DatabaseMethods1();
	//Date expireDate;
	//int studentAllowDays,teacherAllowDays,stdAllowQuantity,teacherAllowQuantity;

	public AssignCoursewareBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);

		try
		{
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkBookSelected()
	{
		Connection conn=DataBaseConnection.javaConnection();
		articleId=bookName.substring(bookName.lastIndexOf("-")+2);
		BookInfo info = objLibrary.coursewareInfoById(articleId, conn);
		if(info.getBookFor().equalsIgnoreCase("class"))
		{
			classid=info.getType();
		}
		else
		{
			classid="na";
		}
		freeBookList=objLibrary.freeCoursewareListByArticleId(articleId,conn);

		if(freeBookList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This courseware is completely distributed. Please add more courseware to distribute"));
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkBookIdSelected()
	{
		Connection conn=DataBaseConnection.javaConnection();
		BookInfo ll=objLibrary.coursewareInfoBySubCoursewareId(bookId,conn);
		bookImage=ll.getBarCodePath();
		bookName=ll.getBookName()+ " - "+ll.getSubjectName()+" - "+ll.getArticleId();
		if(ll.getBookFor().equalsIgnoreCase("class"))
		{
			classid=ll.getType();
		}
		else
		{
			classid="na";
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteBook(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		bookList=objLibrary.coursewareForAutoComplete(query,conn);
		ArrayList<String> tempList=new ArrayList<>();

		for(BookInfo ll : bookList)
		{
			tempList.add(ll.getBookName()+ " - "+ll.getSubjectName()+" - "+ll.getArticleId());
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempList;
	}

	public ArrayList<String> autoCompleteBookId(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		bookIdList=objLibrary.coursewareIdForAutoComplete(query,conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookIdList;
	}


	public void checkSelectedType()
	{
		Connection conn=DataBaseConnection.javaConnection();

		String studentId=studentName.substring(studentName.lastIndexOf(":")+1);

		className=obj.studentDetailslistByAddNo(schid, studentId, conn).getClassName();

	}


	public ArrayList<String> autoCompleteStudent(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(classid.equalsIgnoreCase("na"))
		{
			studentList=obj.searchStudentList(query,conn);
		}
		else
		{
			
			ArrayList<String> list=new DataBaseMethodStudent().completeFieldsForStudentList();
			studentList=new DataBaseMethodStudent().studentDetail(query, "-1", classid, QueryConstants.BY_CLASS_SECTION,QueryConstants.LIKE, null,null,"","","","", session, schid, list, conn);
		}

		ArrayList<String> studentListt=new ArrayList<>();
		for(StudentInfo info : studentList)
		{
			//if(info.getStudentType().equals("Student"))
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
			/*else
				studentListt.add(info.getFname()+"-"+info.getAddNumber()+"/"+"Teacher");*/
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void assignBook()
	{
		Connection conn=DataBaseConnection.javaConnection();

		articleId=bookName.substring(bookName.lastIndexOf("-")+2);
		String studentId=studentName.substring(studentName.lastIndexOf(":")+1);

		assignBookList=objLibrary.assignedCoursewareListOfStudent(studentId, conn);
		int i=objLibrary.assignCourseware(articleId,studentId,addDate,bookId,conn);
		if(i==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Courseware Assigned Sucessfully"));
			articleId=studentId=studentName=bookName="";addDate=new Date();bookId="";type=className="";

			freeBookList=new ArrayList<>();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public ArrayList<SelectItem> getFreeBookList() {
		return freeBookList;
	}
	public void setFreeBookList(ArrayList<SelectItem> freeBookList) {
		this.freeBookList = freeBookList;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ArrayList<BookInfo> getAssignBookList() {
		return assignBookList;
	}

	public void setAssignBookList(ArrayList<BookInfo> assignBookList) {
		this.assignBookList = assignBookList;
	}

	public String getBookImage() {
		return bookImage;
	}

	public void setBookImage(String bookImage) {
		this.bookImage = bookImage;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
