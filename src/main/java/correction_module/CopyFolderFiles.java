package correction_module;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;

import schooldata.DataBaseConnection;

@ManagedBean(name="copyFolderFiles")
@ViewScoped
public class CopyFolderFiles implements Serializable {
	
	static String source = "";
	ArrayList<SelectItem> schoolList = new ArrayList<SelectItem>();
	ArrayList<String> selectedSchools = new ArrayList<String>();
	MakeImagesFolder mk = new MakeImagesFolder();
	
	public CopyFolderFiles()
	{
		Connection conn= DataBaseConnection.javaConnection();
		 
		schoolList = mk.allSchoolList(conn);
		 
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void copy(){
		
		
	    Connection conn= DataBaseConnection.javaConnection();
	    
	    for(String ss :selectedSchools)
	    {	
	       source = mk.findSchoolUploadPAth(ss, conn);

		   copySchoolInfo(conn,source,ss);
		   copyLibraryBarCode(conn,source,ss);
		   copyVisitor(conn,source,ss);
	       copySchoolDocumnets(conn,source,ss);
	       copyStudentDocumnets(conn,source,ss);
	       copyNews(conn,source,ss);
	       copySyllabus(conn,source,ss);
	       copyAdmitCard(conn,source,ss);
	       copyELearning(conn,source,ss);
	       copyPostConsent(conn,source,ss);
	       copyMarksheet(conn,source,ss);
	       copyTimeTable(conn,source,ss);
	       copyHomework(conn,source,ss);
	      copyStudentPhotos(conn,source,ss);
	      copyEmployee(conn,source,ss);
	       copyGallery(conn,source,ss);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Done Copying")); 

	    }   
		 
	    selectedSchools = new ArrayList<String>();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
				
	}
	
	public void copySchoolInfo(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select marksheet_header,image_path,tc_header from schoolinfo where schid='"+schid+"'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			  String tcHeader = rr.getString("tc_header");
		      String schoolLogo = rr.getString("image_path");
		      String marksheetHeader = rr.getString("marksheet_header");
		      
		     if((tcHeader!=null)&& (!tcHeader.contains("/"))&& (!tcHeader.contains("\\")))
		     {
			  if(!tcHeader.trim().equalsIgnoreCase(""))
			  {	
			   Path path = Paths.get(source+tcHeader);
			   if(Files.exists(path))
			  { 
				try {
				File src = new File(source+tcHeader);
				File dest = new File(source+"/SchoolInfo/"+tcHeader);
				
				    FileUtils.copyFile(src, dest);
				} catch (IOException e) {
				    e.printStackTrace();
				}
			  }	
			  }
		     }
			
		      
		     if((schoolLogo!=null)&& (!schoolLogo.contains("/"))&& (!schoolLogo.contains("\\")))
		     {
			  if(!schoolLogo.trim().equalsIgnoreCase(""))
			  {	
				 Path path = Paths.get(source+schoolLogo);
				 if(Files.exists(path))
				 {  
				  try {
				  File src = new File(source+schoolLogo);
				  File dest = new File(source+"/SchoolInfo/"+schoolLogo);
				
				    FileUtils.copyFile(src, dest);
				} catch (IOException e) {
				    e.printStackTrace();
				}
			   }	  
			  }
		      }
				
		      
		     if((marksheetHeader!=null)&& (!marksheetHeader.contains("/"))&& (!marksheetHeader.contains("\\")))
		     {
			  if(!marksheetHeader.trim().equalsIgnoreCase(""))
			  {
				 Path path = Paths.get(source+marksheetHeader);
			     if(Files.exists(path))
			     {   
				  try {
				 File src = new File(source+marksheetHeader);
				 File dest = new File(source+"/SchoolInfo/"+marksheetHeader);
				
				    FileUtils.copyFile(src, dest);
				} catch (IOException e) {
				    e.printStackTrace();
				}
			   }  
			  }	
		      }
			
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
	
	}
	
	
	public void copyLibraryBarCode(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select barCode from book_list where schid='"+schid+"' and barCode!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				String barCode = rr.getString("barCode");
				
			   if((barCode!=null)&& (!barCode.contains("/"))&& (!barCode.contains("\\")))
			   {
				if(!barCode.trim().equalsIgnoreCase(""))
				{	
				  String check = barCode.substring(barCode.length()-1,barCode.length());
				
				  if(!check.equalsIgnoreCase("/"))
				  {
					 Path path = Paths.get(source+barCode);
					 if(Files.exists(path))
					 {
					  try {
						File src = new File(source+barCode);
						File dest = new File(source+"/Library/"+barCode);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						}
					 }
				  }
				}
				}
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
	
	}
	
	public void copyVisitor(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select image from add_visitor where schid='"+schid+"' and image!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				String image = rr.getString("image");
				
			   if((image!=null)&& (!image.contains("/"))&& (!image.contains("\\")))
			   {	
				if(!image.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+image);
					if(Files.exists(path))
				    {
					 try {
						File src = new File(source+image);
						File dest = new File(source+"/Visitor/"+image);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				  }
				}
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
	
	}
	
	public void copySchoolDocumnets(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select filename from school_documents where schid='"+schid+"' and filename!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			  String filename = rr.getString("filename");
			  if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			  {	
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/SchoolDocuments/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copyStudentDocumnets(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select filename from student_documents where schid='"+schid+"' and filename!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			   String filename = rr.getString("filename");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			   {	
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/StudentDocuments/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copyNews(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select filename from circular where schid='"+schid+"' and filename!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			   String filename = rr.getString("filename");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			   {	
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/News/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copySyllabus(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select file from syllabus where schid='"+schid+"' and file!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			   String filename = rr.getString("file");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			   {	
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/Syllabus/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copyAdmitCard(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select photo_name from admit_card where schid='"+schid+"' and photo_name!='no'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				String filename = rr.getString("photo_name");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			   {	
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/AdmitCard/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	
	public void copyELearning(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select image,link from online_lacture where schid='"+schid+"'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
//	           String link = rr.getString("link");
//				
//				if(!link.trim().equalsIgnoreCase(""))
//				{	
//					Path path = Paths.get(source+link);
//					if(Files.exists(path))
//				   {
//					try {
//						File src = new File(source+link);
//						File dest = new File(source+"/ELearning/"+link);
//						
//						    FileUtils.copyFile(src, dest);
//						} catch (IOException e) {
//						    e.printStackTrace();
//						} 
//				   }
//				
//				}
				
			   String image = rr.getString("image");
			   if((image!=null)&& (!image.contains("/"))&& (!image.contains("\\")))
			   {	
				if(!image.trim().equalsIgnoreCase(""))
				{	
					String[] spliteer = image.split(",");
					
					for(int j=0;j<spliteer.length;j++)
					{
					 Path path = Paths.get(source+spliteer[j]);
					 if(Files.exists(path))
				    {
					 try {
						File src = new File(source+spliteer[j]);
						File dest = new File(source+"/ELearning/"+spliteer[j]);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				    }
				  }
				 }		
				}
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
	
	}
	
	
	public void copyPostConsent(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select attachment from general_consent where schid='"+schid+"' and attachment!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				String filename = rr.getString("attachment");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			   {
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/PostConsent/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copyMarksheet(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select fileName from exam_file where schid='"+schid+"' and fileName!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			   String filename = rr.getString("fileName");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			   {
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/Marksheet/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copyTimeTable(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select image_name from ins_time_table where schid='"+schid+"' and image_name!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			   String filename = rr.getString("image_name");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
			   {
				if(!filename.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+filename);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+filename);
						File dest = new File(source+"/TimeTable/"+filename);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	
	public void copyHomework(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select assignment_photo1,assignment_photo2,assignment_photo3,assignment_photo4,assignment_photo5 from assignment where schid='"+schid+"'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			   String assignment_photo1 = rr.getString("assignment_photo1");
			   if((assignment_photo1!=null)&& (!assignment_photo1.contains("/"))&& (!assignment_photo1.contains("\\")))
			   {
				if(!assignment_photo1.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+assignment_photo1);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+assignment_photo1);
						File dest = new File(source+"/Homework/"+assignment_photo1);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				
				}
			   }
				
               String assignment_photo2 = rr.getString("assignment_photo2");
               if((assignment_photo2!=null)&& (!assignment_photo2.contains("/"))&& (!assignment_photo2.contains("\\")))
 			   {
				if(!assignment_photo2.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+assignment_photo2);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+assignment_photo2);
						File dest = new File(source+"/Homework/"+assignment_photo2);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				
				}
 			   }
                
			   String assignment_photo3 = rr.getString("assignment_photo3");
			   if((assignment_photo3!=null)&& (!assignment_photo3.contains("/"))&& (!assignment_photo3.contains("\\")))
	 		   {
				if(!assignment_photo3.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+assignment_photo3);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+assignment_photo3);
						File dest = new File(source+"/Homework/"+assignment_photo3);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				
				}
	 		   }
			   
				
               String assignment_photo4 = rr.getString("assignment_photo4");
               if((assignment_photo4!=null)&& (!assignment_photo4.contains("/"))&& (!assignment_photo4.contains("\\")))
   			   {
				if(!assignment_photo4.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+assignment_photo4);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+assignment_photo4);
						File dest = new File(source+"/Homework/"+assignment_photo4);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				
				}
   			   }
				
				
                String assignment_photo5 = rr.getString("assignment_photo5");	
               if((assignment_photo5!=null)&& (!assignment_photo5.contains("/"))&& (!assignment_photo5.contains("\\")))
  			   {
				if(!assignment_photo5.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+assignment_photo5);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+assignment_photo5);
						File dest = new File(source+"/Homework/"+assignment_photo5);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copyStudentPhotos(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select student_image_path,fatherImage,motherImage,g1Image,g2Image from registration1 where schid='"+schid+"'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
			   String student_image_path = rr.getString("student_image_path");
			   if((student_image_path!=null) && (!student_image_path.contains("/"))&& (!student_image_path.contains("\\")))
	 		   {
				if(!student_image_path.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+student_image_path);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+student_image_path);
						File dest = new File(source+"/StudentPhoto/"+student_image_path);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
               String fatherImage = rr.getString("fatherImage");
               if((fatherImage!=null)&& (!fatherImage.contains("/"))&& (!fatherImage.contains("\\")))
 	 		   {
				if(!fatherImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+fatherImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+fatherImage);
						File dest = new File(source+"/StudentPhoto/"+fatherImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
                
			   String motherImage = rr.getString("motherImage");
			   if((motherImage!=null)&& (!motherImage.contains("/"))&& (!motherImage.contains("\\")))
	 	 	   {
				if(!motherImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+motherImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+motherImage);
						File dest = new File(source+"/StudentPhoto/"+motherImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
                 String g1Image = rr.getString("g1Image");	
                if((g1Image!=null)&& (!g1Image.contains("/"))&& (!g1Image.contains("\\")))
   	 		   {
				if(!g1Image.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+g1Image);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+g1Image);
						File dest = new File(source+"/StudentPhoto/"+g1Image);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
				
               String g2Image = rr.getString("g2Image");	
               if((g2Image!=null)&& (!g2Image.contains("http:/"))&& (!g2Image.contains("\\")))
    	 	   {
				if(!g2Image.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+g2Image);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+g2Image);
						File dest = new File(source+"/StudentPhoto/"+g2Image);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
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
	
	}
	
	
	public void copyEmployee(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select id,emp_image,aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,expImage,otherImage,dlImage from employeeaddmission where schid='"+schid+"'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				// // System.out.println(rr.getString("id"));
			   String emp_image = rr.getString("emp_image");
			   if((emp_image!=null)&& (!emp_image.contains("/"))&& (!emp_image.contains("\\")))
		       {
				if(!emp_image.trim().equalsIgnoreCase(""))
				{	
					
					Path path = Paths.get(source+emp_image);
					// // System.out.println(source+emp_image);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+emp_image);
						File dest = new File(source+"/Employee/"+emp_image);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
               String aadharImage = rr.getString("aadharImage");	
               if((aadharImage!=null)&& (!aadharImage.contains("/"))&& (!aadharImage.contains("\\")))
 	    	   {
				if(!aadharImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+aadharImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+aadharImage);
						File dest = new File(source+"/Employee/"+aadharImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
                
			   String panImage = rr.getString("panImage");	
			   if((panImage!=null)&& (!panImage.contains("/"))&& (!panImage.contains("\\")))
		       {
				if(!panImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+panImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+panImage);
						File dest = new File(source+"/Employee/"+panImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
                 String voteridImage = rr.getString("voteridImage");
                 if((voteridImage!=null)&& (!voteridImage.contains("/"))&& (!voteridImage.contains("\\")))
  	    	   {
				if(!voteridImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+voteridImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+voteridImage);
						File dest = new File(source+"/Employee/"+voteridImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
				
                String ugImage = rr.getString("ugImage");	
                if((ugImage!=null)&& (!ugImage.contains("/"))&& (!ugImage.contains("\\")))
 	    	   {
				if(!ugImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+ugImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+ugImage);
						File dest = new File(source+"/Employee/"+ugImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
				
				  String pgImage = rr.getString("pgImage");	
				  if((pgImage!=null)&& (!pgImage.contains("/"))&& (!pgImage.contains("\\")))
		    	   {
					if(!pgImage.trim().equalsIgnoreCase(""))
					{	
						Path path = Paths.get(source+pgImage);
						if(Files.exists(path))
					   {
						try {
							File src = new File(source+pgImage);
							File dest = new File(source+"/Employee/"+pgImage);
							
							    FileUtils.copyFile(src, dest);
							} catch (IOException e) {
							    e.printStackTrace();
							} 
					   }
					}
					}
					
					
				  String bedImage = rr.getString("bedImage");
				  if((bedImage!=null)&& (!bedImage.contains("/"))&& (!bedImage.contains("\\")))
		    	   {
					if(!bedImage.trim().equalsIgnoreCase(""))
					{	
						Path path = Paths.get(source+bedImage);
						if(Files.exists(path))
					   {
						try {
							File src = new File(source+bedImage);
							File dest = new File(source+"/Employee/"+bedImage);
							
							    FileUtils.copyFile(src, dest);
							} catch (IOException e) {
							    e.printStackTrace();
							} 
					   }
					}
					}
					
				 String policeImage = rr.getString("policeImage");
				 if((policeImage!=null)&& (!policeImage.contains("/"))&& (!policeImage.contains("\\")))
		    	   {
					if(!policeImage.trim().equalsIgnoreCase(""))
					{	
						Path path = Paths.get(source+policeImage);
						if(Files.exists(path))
					   {
						try {
							File src = new File(source+policeImage);
							File dest = new File(source+"/Employee/"+policeImage);
							
							    FileUtils.copyFile(src, dest);
							} catch (IOException e) {
							    e.printStackTrace();
							} 
					   }
					}
					}
					
				 String expImage = rr.getString("expImage");
				 if((expImage!=null)&& (!expImage.contains("/"))&& (!expImage.contains("\\")))
		    	   {
					if(!expImage.trim().equalsIgnoreCase(""))
					{	
						Path path = Paths.get(source+expImage);
						if(Files.exists(path))
					   {
						try {
							File src = new File(source+expImage);
							File dest = new File(source+"/Employee/"+expImage);
							
							    FileUtils.copyFile(src, dest);
							} catch (IOException e) {
							    e.printStackTrace();
							} 
					   }
					}
					}
					
				String otherImage = rr.getString("otherImage");	
				if((otherImage!=null)&& (!otherImage.contains("/"))&& (!otherImage.contains("\\")))
		        {
				if(!otherImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+otherImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+otherImage);
						File dest = new File(source+"/Employee/"+otherImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
				
				String dlImage = rr.getString("dlImage");
				if((dlImage!=null)&& (!dlImage.contains("/"))&& (!dlImage.contains("\\")))
		    	{
				if(!dlImage.trim().equalsIgnoreCase(""))
				{	
					Path path = Paths.get(source+dlImage);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+dlImage);
						File dest = new File(source+"/Employee/"+dlImage);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				}
				}
					
					
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
	
	}
	
	
	public void copyGallery(Connection conn,String source,String schid)
	{
						
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select image from gallary_image where schid='"+schid+"' and image!=''";
			// // System.out.println(query);
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				String filename = rr.getString("image");
			   if((filename!=null)&& (!filename.contains("/"))&& (!filename.contains("\\")))
	    	   {
				if(!filename.trim().equalsIgnoreCase(""))
				{
                  String[] spliteer = filename.split(",");
					
				  for(int j=0;j<spliteer.length;j++)
				 {	
					
					Path path = Paths.get(source+spliteer[j]);
					if(Files.exists(path))
				   {
					try {
						File src = new File(source+spliteer[j]);
						File dest = new File(source+"/Gallery/"+spliteer[j]);
						
						    FileUtils.copyFile(src, dest);
						} catch (IOException e) {
						    e.printStackTrace();
						} 
				   }
				  }
				} 
				}
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
