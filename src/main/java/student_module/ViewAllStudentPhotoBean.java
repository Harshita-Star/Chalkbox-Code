package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="viewAllStudentPhotos")
@ViewScoped
public class ViewAllStudentPhotoBean implements Serializable {
	String schoolId,username,type,remark;
	ArrayList<StudentPhotoInfo>pendingList = new ArrayList<>();
	StudentPhotoInfo selected;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public ViewAllStudentPhotoBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId = (String) ss.getAttribute("schoolid");
		username=(String) ss.getAttribute("username");
		type=(String) ss.getAttribute("type");
		pendingList=obj.selectAllPendingStudentPhotos(conn,schoolId);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void approve1()
	{
		Connection conn = DataBaseConnection.javaConnection();
		remark = " ";
		int i=obj.updateSinglePhotoStatus(conn,selected.getStudentId(),schoolId,"approved","s_status","s_rem",remark);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student's photo has been approved successfully!"));
			obj.updateStudentImagePathInRegistration1(conn,selected.getsPhoto(),"student_image_path",selected.getStudentId(),schoolId);
			pendingList=obj.selectAllPendingStudentPhotos(conn,schoolId);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Student Id-"+selected.getStudentId(); 
		
		String refNo = workLg.saveWorkLogMehod(language,"Approve Student Photo","WEB",value,conn);
		return refNo;
	}
	
	
	public void reject1()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.updateSinglePhotoStatus(conn,selected.getStudentId(),schoolId,"rejected","s_status","s_rem",remark);
		if(i>=1)
		{
			String refNo4;
			try {
				refNo4=addWorkLog4(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student's photo has been rejected successfully!"));
			//int j=obj.updateStudentImagePathInRegistration1(conn,selected.getsPhoto(),"student_image_path",selected.getStudentId(),schoolId);
			pendingList=obj.selectAllPendingStudentPhotos(conn,schoolId);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog4(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Student Id-"+selected.getStudentId()+" --Remark-"+remark; 
		
		String refNo = workLg.saveWorkLogMehod(language,"Reject Student Photo","WEB",value,conn);
		return refNo;
	}
	
	public void approve2()
	{
		Connection conn = DataBaseConnection.javaConnection();
		remark = " ";
		int i=obj.updateSinglePhotoStatus(conn,selected.getStudentId(),schoolId,"approved","f_status","f_rem",remark);
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Father's photo has been approved successfully!"));
			obj.updateStudentImagePathInRegistration1(conn,selected.getfPhoto(),"fatherImage",selected.getStudentId(),schoolId);
			pendingList=obj.selectAllPendingStudentPhotos(conn,schoolId);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Student Id-"+selected.getStudentId(); 
		
		String refNo = workLg.saveWorkLogMehod(language,"Approve Fahter Photo","WEB",value,conn);
		return refNo;
	}
	
	
	public void reject2()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.updateSinglePhotoStatus(conn,selected.getStudentId(),schoolId,"rejected","f_status","f_rem",remark);
		if(i>=1)
		{
			String refNo5;
			try {
				refNo5=addWorkLog5(conn);
			} catch (Exception e) {
				e.printStackTrace();
				
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Father's photo has been rejected successfully!"));
			//int j=obj.updateStudentImagePathInRegistration1(conn,selected.getfPhoto(),"fatherImage",selected.getStudentId(),schoolId);
			pendingList=obj.selectAllPendingStudentPhotos(conn,schoolId);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog5(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Student Id-"+selected.getStudentId()+" --Remark-"+remark; 
		
		String refNo = workLg.saveWorkLogMehod(language,"Reject Father Photo","WEB",value,conn);
		return refNo;
	}
	
	public void approve3()
	{
		Connection conn = DataBaseConnection.javaConnection();
		remark = " ";
		int i=obj.updateSinglePhotoStatus(conn,selected.getStudentId(),schoolId,"approved","m_status","m_rem",remark);
		if(i>=1)
		{
			String refNo3;
			try {
				refNo3=addWorkLog3(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mother's photo has been approved successfully!"));
			obj.updateStudentImagePathInRegistration1(conn,selected.getmPhoto(),"motherImage",selected.getStudentId(),schoolId);
			pendingList=obj.selectAllPendingStudentPhotos(conn,schoolId);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Student Id-"+selected.getStudentId(); 
		
		String refNo = workLg.saveWorkLogMehod(language,"Approve Mother Photo","WEB",value,conn);
		return refNo;
	}
	
	
	public void reject3()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.updateSinglePhotoStatus(conn,selected.getStudentId(),schoolId,"rejected","m_status","m_rem",remark);
		if(i>=1)
		{
			String refNo6;
			try {
				refNo6=addWorkLog6(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mother's photo has been rejected successfully!"));
			//int j=obj.updateStudentImagePathInRegistration1(conn,selected.getmPhoto(),"motherImage",selected.getStudentId(),schoolId);
			pendingList=obj.selectAllPendingStudentPhotos(conn,schoolId);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog6(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Student Id-"+selected.getStudentId()+" --Remark-"+remark; 
		
		String refNo = workLg.saveWorkLogMehod(language,"Reject Mother Photo","WEB",value,conn);
		return refNo;
	}
	
	
	public void approve()
	{
		////// // System.out.println("ap1"+selected.isPhoto1());
		////// // System.out.println("ap2"+selected.isPhoto2());
		////// // System.out.println("ap3"+selected.isPhoto3());
		if(selected.isPhoto1()==false && selected.isPhoto2()==false && selected.isPhoto3()==false)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one!"));
		}
		else
		{
			if(selected.isPhoto1()==true)
			{
				approve1();
			}
			if(selected.isPhoto2()==true)
			{
				approve2();
			}
			if(selected.isPhoto3()==true)
			{
				approve3();
			}

		}

		remark = "";
		//int i=obj.updatePhotoStatusByStudentID(conn,selected.getStudentId(),schoolId,"approved");
	}
	public void reject()
	{
		////// // System.out.println("p1"+selected.isPhoto1());
		////// // System.out.println("p2"+selected.isPhoto2());
		////// // System.out.println("p3"+selected.isPhoto3());
		if(selected.isPhoto1()==false && selected.isPhoto2()==false && selected.isPhoto3()==false)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one!"));
		}
		else
		{
			if(selected.isPhoto1()==true)
			{
				reject1();
			}
			if(selected.isPhoto2()==true)
			{
				reject2();
			}
			if(selected.isPhoto3()==true)
			{
				reject3();
			}

		}

		remark = "";
		//int i=obj.updatePhotoStatusByStudentID(conn,selected.getStudentId(),schoolId,"rejected");
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<StudentPhotoInfo> getPendingList() {
		return pendingList;
	}
	public void setPendingList(ArrayList<StudentPhotoInfo> pendingList) {
		this.pendingList = pendingList;
	}
	public StudentPhotoInfo getSelected() {
		return selected;
	}
	public void setSelected(StudentPhotoInfo selected) {
		this.selected = selected;
	}
	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}
	public DatabaseMethods1 getObj2() {
		return obj2;
	}
	public void setObj2(DatabaseMethods1 obj2) {
		this.obj2 = obj2;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
