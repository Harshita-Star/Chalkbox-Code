package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="studentWelcome")
@ViewScoped
public class StudentWelcomeBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String studentImage=null;
	StudentInfo info;


	public StudentWelcomeBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username =(String) ss.getAttribute("username");
		studentImage=obj.studentImage(obj.schoolId(),"imageWithPath",username,conn);
		info=obj.studentDetailslistByAddNo(obj.schoolId(),username,conn);
		//ss.setAttribute("deatil",info);

		/*String temp=new DatabaseMethods1().admitClass(String.valueOf(studentList.getAddNumber()));
		if(temp.equals("new"))
		{
			admitClass=studentList.getClassName();
		}
		else
		{
			String classid1=new DatabaseMethods1().classSectionNameFromid(temp);
			admitClass=(new DatabaseMethods1().classNameFromid(classid1,session));
		}
		 */

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*public StreamedContent getStudentImage() {
		return studentImage;
	}
	public void setStudentImage(StreamedContent studentImage) {
		this.studentImage = studentImage;
	}*/

	public StudentInfo getInfo() {
		return info;
	}
	public String getStudentImage() {
		return studentImage;
	}

	public void setStudentImage(String studentImage) {
		this.studentImage = studentImage;
	}

	public void setInfo(StudentInfo info) {
		this.info = info;
	}
}
