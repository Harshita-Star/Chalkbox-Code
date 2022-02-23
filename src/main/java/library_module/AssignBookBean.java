package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean (name="assignBook")
@ViewScoped
public class AssignBookBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String bookName,studentName,articleId,bookId,type,className,bookImage,studentImage;

	Date addDate=new Date();
	ArrayList<BookInfo> bookList,assignBookList;
	ArrayList<String> bookIdList;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem> freeBookList;
	Date expireDate;
	int studentAllowDays,teacherAllowDays,stdAllowQuantity,teacherAllowQuantity;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();
	String schoolId;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();



	public AssignBookBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=obj.fullSchoolInfo(conn);
		schoolId=obj.schoolId();
		expireDate=new Date();
		studentAllowDays=info.getStudentAllowDays();
		stdAllowQuantity=info.getStdAllowQuantity();
		teacherAllowDays=info.getTeacherAllowDays();
		teacherAllowQuantity=info.getTeacherAllowQuantity();

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void checkBookSelected()
	{
		Connection conn=DataBaseConnection.javaConnection();
		articleId=bookName.substring(bookName.lastIndexOf(" - ")+3);
		freeBookList=objLibrary.freeBookListByArticleId(articleId,conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkBookIdSelected()
	{
		Connection conn=DataBaseConnection.javaConnection();
		BookInfo ll=objLibrary.bookInfoBySubBookId(bookId,conn);
		bookImage=ll.getBarCodePath();
		bookName=ll.getBookName()+ " - "+ll.getAuthorName()+" - "+ll.getPublicationName()+" - "+ll.getArticleId();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteBook(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		bookList=objLibrary.bookForAutoComplete(query,conn);
		ArrayList<String> tempList=new ArrayList<>();

		for(BookInfo ll : bookList)
		{
			tempList.add(ll.getBookName()+ " - "+ll.getAuthorName()+" - "+ll.getPublicationName()+" - "+ll.getArticleId());
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
		bookIdList=objLibrary.bookIdForAutoComplete(query,conn);

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

		String studentId=studentName.substring(studentName.lastIndexOf("-")+1,studentName.lastIndexOf("/"));
		type=studentName.substring(studentName.lastIndexOf("/")+1);
		if(type.equals("Student"))
		{
			StudentInfo info=obj.studentDetailslistByAddNo(schoolId, studentId, conn);
			className=info.getClassName();
			expireDate.setDate(addDate.getDate()+studentAllowDays);
			studentImage=info.getStudent_image();
			/*PrimeFaces.current().ajax().update("form1:type");
			PrimeFaces.current().ajax().update("form1:className");
			PrimeFaces.current().ajax().update("form1:exDate");*/
		}
		else
		{
			studentImage=obj.employeeImage(Integer.parseInt(studentId),conn);
			expireDate.setDate(addDate.getDate()+teacherAllowDays);
			className="";
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ArrayList<String> autoCompleteStudent(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();

		studentList=obj.searchStudentTeacherList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();
		for(StudentInfo info : studentList)
		{
			if(info.getStudentType().equals("Student"))
				studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber()+"/Student");
			else
				studentListt.add(info.getFname()+"-"+info.getAddNumber()+"/"+"Teacher");
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
		
		articleId=bookName.substring(bookName.lastIndexOf(" - ")+3);
		String studentId=studentName.substring(studentName.lastIndexOf("-")+1,studentName.lastIndexOf("/"));

		assignBookList=objLibrary.assignedBookListOfStudent(schoolId,studentId, conn);
		int flag=0;
		if(type.equals("Student") && assignBookList.size()>=stdAllowQuantity)
		{
			flag=1;
		}
		else if(type.equals("Teacher") && assignBookList.size()>=teacherAllowQuantity)
		{
			flag=1;
		}
		else
		{
			flag=0;
		}
		if(flag==0)
		{
			int i=objLibrary.assignBook(articleId,studentId,addDate,expireDate,bookId,type,conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn,studentId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Assigned Sucessfully"));
				String expDate = new SimpleDateFormat("dd-MM-yyyy").format(expireDate);
				String bnm = bookName.split("-")[0];
				if(type.equalsIgnoreCase("Student"))
				{
					
					StudentInfo info=obj.selectedStudentDetailByAddNo(studentId, conn);
					if(info.getFathersPhone()==info.getMothersPhone())
					{
						DBJ.notification("Library","A book - "+bnm.trim()+" has been assigned to your ward "+info.getFname()+". Please make sure to return the book before due date "+expDate+" otherwise you will have to pay the penalty.", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
					}
					else
					{
						DBJ.notification("Library","A book - "+bnm.trim()+" has been assigned to your ward "+info.getFname()+". Please make sure to return the book before due date "+expDate+" otherwise you will have to pay the penalty.", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
						DBJ.notification("Library","A book - "+bnm.trim()+" has been assigned to your ward "+info.getFname()+". Please make sure to return the book before due date "+expDate+" otherwise you will have to pay the penalty.", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
					}
				}
				else
				{
					DBJ.adminnotification("Library","A book - "+bnm.trim()+" has been assigned to you. Please make sure to return the book before due date "+expDate+" otherwise you will have to pay the penalty.","staff"+"-"+studentId+"-"+schoolId,schoolId,"BookAssignStaff-"+studentId,conn);
				}

				articleId=studentId=studentName=bookName="";addDate=expireDate=new Date();bookId="";type=className="";
				freeBookList=new ArrayList<>();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}
		else
		{
			PrimeFaces.current().executeInitScript("PF('detailDialog').show()");
			PrimeFaces.current().ajax().update("detailForm");
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn,String studentId)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addDate);
		String expDt = formater.format(expireDate);
		
		value = "ArticleId-"+articleId+" --Student/Teacher Id-"+studentId+" --Add Date-"+addDt+" --Expiry date-"+expDt+" --BookId-"+bookId+" --Type-"+type+" --ClassName-"+className;
		
		String refNo = workLg.saveWorkLogMehod(language,"Assign Book","WEB",value,conn);
		return refNo;
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
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
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

	public int getStudentAllowDays() {
		return studentAllowDays;
	}

	public void setStudentAllowDays(int studentAllowDays) {
		this.studentAllowDays = studentAllowDays;
	}

	public int getTeacherAllowDays() {
		return teacherAllowDays;
	}

	public void setTeacherAllowDays(int teacherAllowDays) {
		this.teacherAllowDays = teacherAllowDays;
	}

	public int getStdAllowQuantity() {
		return stdAllowQuantity;
	}

	public void setStdAllowQuantity(int stdAllowQuantity) {
		this.stdAllowQuantity = stdAllowQuantity;
	}

	public int getTeacherAllowQuantity() {
		return teacherAllowQuantity;
	}

	public void setTeacherAllowQuantity(int teacherAllowQuantity) {
		this.teacherAllowQuantity = teacherAllowQuantity;
	}

	public String getBookImage() {
		return bookImage;
	}

	public void setBookImage(String bookImage) {
		this.bookImage = bookImage;
	}

	public String getStudentImage() {
		return studentImage;
	}

	public void setStudentImage(String studentImage) {
		this.studentImage = studentImage;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
