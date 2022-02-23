package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="studentCoursewareReport")
@ViewScoped


public class StudentCoursewareReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String studentName;
	int totalLeft;
	ArrayList<StudentInfo> studentList;
	ArrayList<BookInfo> bookList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();

	public ArrayList<String> autoCompleteStudent(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=obj.searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();
		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return studentListt;
	}


	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String studentId=studentName.substring(studentName.lastIndexOf(":")+1);
		bookList=dbl.studentCoursewareHistory(studentId,conn);
		/*for(BookInfo ll:bookList)
		{
			if(ll.getReceivedStatus().equals("No"))
				totalLeft++;
		}*/


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<BookInfo> getBookList() {
		return bookList;
	}
	public void setBookList(ArrayList<BookInfo> bookList) {
		this.bookList = bookList;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public int getTotalLeft() {
		return totalLeft;
	}
	public void setTotalLeft(int totalLeft) {
		this.totalLeft = totalLeft;
	}
}
