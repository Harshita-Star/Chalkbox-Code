
package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="uploadFile")
@ViewScoped
public class UploadExamPdfBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String name,examName,imagePath;
	transient
	UploadedFile fileImage;
	ArrayList<StudentInfo> studentList;
	ArrayList<String> examList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	
	public void allExams() 
	{
		StudentInfo sInfo=new StudentInfo();
		Connection conn = DataBaseConnection.javaConnection();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						sInfo=info;
						break;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}

		}
		examList = new DatabaseMethods1().checkListOfExams(sInfo.getClassId(), sInfo.getSectionid(), conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void addNow()
	{
		StudentInfo sInfo=new StudentInfo();
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			int index=name.lastIndexOf(":")+1;
			String id=name.substring(index);
			if(index!=0)
			{
				for(StudentInfo info : studentList)
				{
					if(String.valueOf(info.getAddNumber()).equals(id))
					{
						sInfo=info;
						break;
					}
				}

				if (fileImage != null && fileImage.getSize()>0)
				{
					//&& fileImage.getFileName().contains(".pdf")
					
					String dt = new SimpleDateFormat("yMdHms").format(new Date());
					int rendomNumber = (int) (Math.random() * 9000) + 1000;
					String filePath1 = fileImage.getFileName();
					if(filePath1.contains(".zip"))
					{
						FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Please upload correct file format"));
					}
					else
					{
						String exten[] = filePath1.split("\\.");
						imagePath = "uplStuMarksheet_" + String.valueOf(rendomNumber) + "_" + dt + "." + exten[exten.length-1];
						obj.makeProfileSchid(obj.schoolId(),fileImage, imagePath, conn);
						
						int i=obj.uploadExamFile(sInfo,examName,imagePath,conn);
						if(i>=1)
						{
							String refNo=addWorkLog(conn,sInfo);
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Uploaded Sucessfully"));
							HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
							String notify=(String) ss.getAttribute("marksheetNotify");
							if(notify.equalsIgnoreCase("true"))
							{
								if(sInfo.getFathersPhone() == sInfo.getMothersPhone())
								{
									obj.notification(obj.schoolId(),"Marksheet","Marksheet of "+sInfo.getFname()+" is uploaded for exam - "+examName+".", sInfo.getFathersPhone()+"-"+sInfo.getAddNumber()+"-"+obj.schoolId(),conn);
								}
								else
								{
									obj.notification(obj.schoolId(),"Marksheet","Marksheet of "+sInfo.getFname()+" is uploaded for exam - "+examName+".", sInfo.getFathersPhone()+"-"+sInfo.getAddNumber()+"-"+obj.schoolId(),conn);
									obj.notification(obj.schoolId(),"Marksheet","Marksheet of "+sInfo.getFname()+" is uploaded for exam - "+examName+".", sInfo.getMothersPhone()+"-"+sInfo.getAddNumber()+"-"+obj.schoolId(),conn);
								}
							}
							
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("An Error Occured... Please Try Again"));
						}
					}
					
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Please Upload a marksheet first"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Please Select a student from list"));
			}
		} 
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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


	public String addWorkLog(Connection conn,StudentInfo sInfo)
	{
	    String value = "";
		String language= "";
		
		language = "Student-"+sInfo.getAddNumber()+" -- Exam Name-"+examName;
		value = imagePath;
		

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Upload Exam Pdf","WEB",value,conn);
		return refNo;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public UploadedFile getFileImage() {
		return fileImage;
	}
	public void setFileImage(UploadedFile fileImage) {
		this.fileImage = fileImage;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<String> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<String> examList) {
		this.examList = examList;
	}
}
