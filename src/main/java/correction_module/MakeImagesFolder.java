package correction_module;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;


@ManagedBean(name="makeImagesFolder")
@ViewScoped
public class MakeImagesFolder implements Serializable{
	
	ArrayList<SelectItem> schoolList = new ArrayList<SelectItem>();
	ArrayList<String> selectedSchools = new ArrayList<String>();
	ArrayList<String> folderList = new ArrayList<String>();
	
	
	public MakeImagesFolder()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList = allSchoolList(conn);
		folderList.add("SchoolDocuments");
		folderList.add("StudentDocuments");
		folderList.add("Homework");
		folderList.add("News");
		folderList.add("Gallery");
		folderList.add("Syllabus");
		folderList.add("Employee");
		folderList.add("AdmitCard");
		folderList.add("Marksheet");
		folderList.add("ELearning");
		folderList.add("PostConsent");
		folderList.add("TimeTable");
		folderList.add("SchoolInfo");
		folderList.add("StudentPhoto");
		folderList.add("Library");
		folderList.add("Visitor");
		
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	ArrayList<SelectItem> allSchoolList(Connection conn) {
		
		ArrayList<SelectItem> list = new ArrayList<>();
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			String qq = "select id,schoolName from add_school ";
			rr = st.executeQuery(qq);
			while (rr.next()) {
				SelectItem ii = new SelectItem();
				ii.setValue(rr.getString("id"));
				ii.setLabel(rr.getString("schoolName"));
				list.add(ii);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public void generate()
	{
		Connection conn = DataBaseConnection.javaConnection();
		for(String ss : selectedSchools)
		{
			String checkUploadPath = findSchoolUploadPAth(ss,conn);
			
			for(String folders : folderList)
			{
				Path path = Paths.get(checkUploadPath+folders);

			   if(!Files.exists(path))
		       {
				
				try
				{
					Files.createDirectories(path);
					Set<PosixFilePermission> perms = Files.readAttributes(path,PosixFileAttributes.class).permissions();

					// // System.out.format("Permissions before: %s%n",  PosixFilePermissions.toString(perms));

					perms.add(PosixFilePermission.OWNER_WRITE);
					perms.add(PosixFilePermission.OWNER_READ);
					perms.add(PosixFilePermission.OWNER_EXECUTE);
					perms.add(PosixFilePermission.GROUP_WRITE);
					perms.add(PosixFilePermission.GROUP_READ);
					perms.add(PosixFilePermission.GROUP_EXECUTE);
					perms.add(PosixFilePermission.OTHERS_WRITE);
					perms.add(PosixFilePermission.OTHERS_READ);
					perms.add(PosixFilePermission.OTHERS_EXECUTE);
					Files.setPosixFilePermissions(path, perms);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		       }	
			}
			
		}
		selectedSchools = new ArrayList<String>();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Folders Created")); 
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	public String findSchoolUploadPAth(String ss, Connection conn) {
		
        String uplPath = "";
        Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			String qq = "select uploadpath from schoolinfo where schid='"+ss+"' ";
			rr = st.executeQuery(qq);
			while (rr.next()) {
				
				uplPath = rr.getString("uploadpath");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return uplPath;
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}


	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}


	public ArrayList<String> getSelectedSchools() {
		return selectedSchools;
	}


	public void setSelectedSchools(ArrayList<String> selectedSchools) {
		this.selectedSchools = selectedSchools;
	}
	
	

}
