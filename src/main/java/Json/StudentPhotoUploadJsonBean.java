package Json;

import java.sql.Connection;
import java.sql.SQLException;

import schooldata.DataBaseConnection;
import student_module.DataBaseOnlineAdm;

public class StudentPhotoUploadJsonBean
{
	public void addPhoto(String studentid, String schid, String type, String imagefile)
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			DataBaseOnlineAdm DBO = new DataBaseOnlineAdm();
			String photoCol = "",statusCol = "", sphoto = "",fphoto = "",mphoto = "";
			String status = "pending";
			if(type.equalsIgnoreCase("student"))
			{
				sphoto = imagefile;
				photoCol = "s_photo";
				statusCol = "s_status";
			}
			else if(type.equalsIgnoreCase("father"))
			{
				fphoto = imagefile;
				photoCol = "f_photo";
				statusCol = "f_status";
			}
			else if(type.equalsIgnoreCase("mother"))
			{
				mphoto = imagefile;
				photoCol = "m_photo";
				statusCol = "m_status";
			}

			int j=DBO.checkStudentEntryByStudentId(conn,studentid,schid);
			if(j>=1)
			{
				DBO.updateStudentPhotoByType(studentid,photoCol,statusCol,status,imagefile,conn);
			}
			else
			{
				DBO.insertStudentPhotoUpload(conn,studentid,schid,status,sphoto,fphoto,mphoto);
			}
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

}
