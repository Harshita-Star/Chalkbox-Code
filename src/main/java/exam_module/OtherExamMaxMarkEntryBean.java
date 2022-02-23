package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SubjectInfo;

@ManagedBean(name="otherExamMark")
@ViewScoped
public class OtherExamMaxMarkEntryBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<SubjectInfo> examList;
	DataBaseMethodsOasisExam oasis = new DataBaseMethodsOasisExam();
	String schoolId,sessionValue;
	DatabaseMethods1 obj=new DatabaseMethods1();


	public OtherExamMaxMarkEntryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId= obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schoolId,conn);
		examList=oasis.otherExamList(conn);
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void submitMarks()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=oasis.submitMarks(examList,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Submitted Successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
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
	
	public ArrayList<SubjectInfo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<SubjectInfo> examList) {
		this.examList = examList;
	}

}
