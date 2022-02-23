package student_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="studentPhotoUpload")
@ViewScoped
public class StudentPhotoUploadBean implements Serializable
{
	String studentId,schoolId,sPhoto,fPhoto,mPhoto,sStatus,fStatus,mStatus,sRem,fRem,mRem;
	private static final long serialVersionUID = 1L;
	transient UploadedFile sFile;
	transient UploadedFile fFile;
	transient UploadedFile mFile;
	DataBaseOnlineAdm obj=new DataBaseOnlineAdm();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	public StudentPhotoUploadBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId = (String) ss.getAttribute("schoolid");
		studentId=(String) ss.getAttribute("username");

		String photos=obj.selectPhotosByStudentId(conn,studentId,schoolId);
		if(photos==null || photos.equals(""))
		{

		}
		else
		{

			String arr[]=photos.split(",");
			sPhoto=arr[0];
			fPhoto=arr[1];
			mPhoto=arr[2];
			sStatus=arr[3];
			fStatus=arr[4];
			mStatus=arr[5];
			sRem=arr[6];
			fRem=arr[7];
			mRem=arr[8];

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void send()
	{
		Connection conn = DataBaseConnection.javaConnection();


		try
		{
			String dt = new SimpleDateFormat("yMdhms").format(new Date());
			String name1 ="",name2="",name3="";
			if(sFile!= null&& fFile!= null && mFile!= null)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("please select photo!"));
			}
			else
			{
				if (sFile != null && sFile.getSize() > 0)
				{
					int rendomNumber=(int)(Math.random()*9000)+1000;
					name1 = sFile.getFileName();
					String exten[]=name1.split("\\.");
					name1="photo"+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					obj1.makeProfileSchid(schoolId,sFile,name1,conn);
				}
				if(fFile != null && fFile.getSize() > 0)
				{
					int rendomNumber=(int)(Math.random()*9000)+1000;
					name2 = fFile.getFileName();
					String exten[]=name2.split("\\.");
					name2="photo"+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					obj1.makeProfileSchid(schoolId,fFile,name2,conn);
				}
				if(mFile != null && mFile.getSize() > 0)
				{
					int rendomNumber=(int)(Math.random()*9000)+1000;
					name3 = mFile.getFileName();
					String exten[]=name3.split("\\.");
					name3="photo"+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					obj1.makeProfileSchid(schoolId,mFile,name3,conn);
				}
				if(name1.equals("") && name2.equals("") && name3.equals(""))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("All Photos are blank!"));
				}
				else
				{
					int j=obj.checkStudentEntryByStudentId(conn,studentId,schoolId);
					if(j>=1)
					{
						String s1="pending",s2="pending",s3="pending";
						if(name1.equals(""))
						{
							name1=(sPhoto==null || sPhoto.equalsIgnoreCase("")) ? "" : sPhoto;
							s1=(sStatus==null || sStatus.equalsIgnoreCase("")) ? "pending" : sStatus;
						}
						if(name2.equals(""))
						{
							name2=(fPhoto==null || fPhoto.equalsIgnoreCase("")) ? "" : fPhoto;
							s2=(fStatus==null || fStatus.equalsIgnoreCase("")) ? "pending" : fStatus;
						}
						if(name3.equals(""))
						{
							name3=(mPhoto==null || mPhoto.equalsIgnoreCase("")) ? "" : mPhoto;
							s3=(mStatus==null || mStatus.equalsIgnoreCase("")) ? "pending" : mStatus;
						}
						obj.updateStudentPhotos(conn,studentId,schoolId,s1,s2,s3,name1,name2,name3);
						FacesContext.getCurrentInstance().getExternalContext().redirect("studentPhotoUpload.xhtml");

					}
					else
					{
						int i=obj.insertStudentPhotoUpload(conn,studentId,schoolId,"pending",name1,name2,name3);
						if(i>0)
						{
							FacesContext context1 = FacesContext.getCurrentInstance();
							context1.addMessage(null, new FacesMessage("Photo Uploaded successfully!"));
							FacesContext.getCurrentInstance().getExternalContext().redirect("studentPhotoUpload.xhtml");
						}
						else
						{
							FacesContext context1 = FacesContext.getCurrentInstance();
							context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));

						}

					}
				}
				//int i=obj.news(message,obj.schoolId(),name,conn,type,selectedClassList);

			}


		} catch (IOException e) {
			
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
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public UploadedFile getsFile() {
		return sFile;
	}
	public void setsFile(UploadedFile sFile) {
		this.sFile = sFile;
	}
	public UploadedFile getfFile() {
		return fFile;
	}
	public void setfFile(UploadedFile fFile) {
		this.fFile = fFile;
	}
	public UploadedFile getmFile() {
		return mFile;
	}
	public void setmFile(UploadedFile mFile) {
		this.mFile = mFile;
	}
	public String getsPhoto() {
		return sPhoto;
	}
	public void setsPhoto(String sPhoto) {
		this.sPhoto = sPhoto;
	}
	public String getfPhoto() {
		return fPhoto;
	}
	public void setfPhoto(String fPhoto) {
		this.fPhoto = fPhoto;
	}
	public String getmPhoto() {
		return mPhoto;
	}
	public void setmPhoto(String mPhoto) {
		this.mPhoto = mPhoto;
	}
	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}
	public String getsStatus() {
		return sStatus;
	}
	public void setsStatus(String sStatus) {
		this.sStatus = sStatus;
	}
	public String getfStatus() {
		return fStatus;
	}
	public void setfStatus(String fStatus) {
		this.fStatus = fStatus;
	}
	public String getmStatus() {
		return mStatus;
	}
	public void setmStatus(String mStatus) {
		this.mStatus = mStatus;
	}
	public String getsRem() {
		return sRem;
	}
	public void setsRem(String sRem) {
		this.sRem = sRem;
	}
	public String getfRem() {
		return fRem;
	}
	public void setfRem(String fRem) {
		this.fRem = fRem;
	}
	public String getmRem() {
		return mRem;
	}
	public void setmRem(String mRem) {
		this.mRem = mRem;
	}

}
