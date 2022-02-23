package schooldata;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.Charsets;
import org.primefaces.model.StreamedContent;

import session_work.QueryConstants;
import student_module.DataBaseOnlineAdm;
import student_module.OnlineAdmInfo;
import student_module.RegistrationColumnName;
@ManagedBean(name="stdDocList")
@ViewScoped
public class StudentDocumentListBean implements Serializable {
	Category selectedSection;
	String classId;
	int sectionId;
	ArrayList<StudentInfo> stdList;
	StudentInfo selectedStd;
	transient StreamedContent file;
	DatabaseMethods1 obj= new DatabaseMethods1();
	SchoolInfoList ls;
	public StudentDocumentListBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		ls =new DatabaseMethods1().fullSchoolInfo(conn);
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedSection=(Category) ss.getAttribute("selectedSection");
		sectionId=selectedSection.getId();
		classId=selectedSection.getClassid();
		
		ArrayList<String> stdColList=makeStdColumnList();
		String columnName="";
		for(String column: stdColList)
		{
			columnName += column+",";
		}
		columnName = columnName.substring(0,columnName.length()-1);
		stdList=obj.searchStudentListForDocument(String.valueOf(sectionId), conn,columnName);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void downloadMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		FacesContext context = FacesContext.getCurrentInstance();
        String addNo = (String) UIComponent.getCurrentComponent(context).getAttributes().get("addNumber");
        
        try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
		if(ls.getCountry().equalsIgnoreCase("UAE"))
		{
			downloadUae(addNo);
		}
		else
		{
			downloadIndia(addNo);
		}
	}
	
	public void downloadIndia(String addNo)
	{
		Connection conn=DataBaseConnection.javaConnection();
        
        for(StudentInfo info : stdList)
		{
        	if(info.getAddNumber().equals(addNo))
        	{
        		selectedStd=info;
        		break;
        	}
        }
        
        
        String fileName=selectedStd.getFullName()+"_"+selectedStd.getSrNo()+"_docs.zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String uploadPath="";
		if (ls.getProjecttype().equalsIgnoreCase("online"))
		{
			uploadPath=ls.getUploadpath();
		}
		
	    String realPath=uploadPath;
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
		 
			for(SelectItem doc:selectedStd.getDocList())
			{
				FileInputStream fin = new FileInputStream(realPath+doc.getValue());
				String ext=String.valueOf(doc.getValue()).substring(String.valueOf(doc.getValue()).lastIndexOf(".")+1);
                zout.putNextEntry(new ZipEntry(doc.getLabel()+"."+ext));
                int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				  
				//close the InputStream
				fin.close();
			}
			zout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fc.responseComplete();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void downloadUae(String addNo)
	{
		Connection conn=DataBaseConnection.javaConnection();
        
        for(StudentInfo info : stdList)
		{
	        	if(info.getAddNumber().equals(addNo))
	        	{
	        		selectedStd=info;
	        		break;
	        	}
        }
        
        OnlineAdmInfo onlinfo = new DataBaseOnlineAdm().onlineAdmInfoById(selectedStd.getEnqUAEId(), "id", conn);
        
        String fileName=selectedStd.getFullName()+"_"+selectedStd.getSrNo()+"_docs.zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String uploadPath="";
		if (ls.getProjecttype().equalsIgnoreCase("online"))
		{
			uploadPath=ls.getUploadpath();
		}
		
	    String realPath=uploadPath;
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
			
			//Stud pic
			if(!onlinfo.getSt_image().isEmpty())
			{
				String filename = onlinfo.getSt_image().substring(onlinfo.getSt_image().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getSt_image().substring(onlinfo.getSt_image().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("StudentPhoto"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//father pic
			if(!onlinfo.getF_image().isEmpty())
			{
				String filename = onlinfo.getF_image().substring(onlinfo.getF_image().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getF_image().substring(onlinfo.getF_image().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("FatherPhoto"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//mother pic
			if(!onlinfo.getM_image().isEmpty())
			{
				String filename = onlinfo.getM_image().substring(onlinfo.getM_image().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getM_image().substring(onlinfo.getM_image().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("MotherPhoto"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//guardian pic
			if(!onlinfo.getG_image().isEmpty())
			{
				String filename = onlinfo.getG_image().substring(onlinfo.getG_image().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getG_image().substring(onlinfo.getG_image().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("GuardianPhoto"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//fat ppt
			if(!onlinfo.getDocinfo().getParent_ppt().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getParent_ppt().substring(onlinfo.getDocinfo().getParent_ppt().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getParent_ppt().substring(onlinfo.getDocinfo().getParent_ppt().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("FatherPassport"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//fat visa
			if(!onlinfo.getDocinfo().getParent_visa().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getParent_visa().substring(onlinfo.getDocinfo().getParent_visa().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getParent_visa().substring(onlinfo.getDocinfo().getParent_visa().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("FatherVisa"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//fat emid
			if(!onlinfo.getDocinfo().getParent_emid().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getParent_emid().substring(onlinfo.getDocinfo().getParent_emid().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getParent_emid().substring(onlinfo.getDocinfo().getParent_emid().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("FatherE-ID"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//mot ppt
			if(!onlinfo.getDocinfo().getMother_ppt().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getMother_ppt().substring(onlinfo.getDocinfo().getMother_ppt().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getMother_ppt().substring(onlinfo.getDocinfo().getMother_ppt().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("MotherPassport"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//mot visa
			if(!onlinfo.getDocinfo().getMother_visa().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getMother_visa().substring(onlinfo.getDocinfo().getMother_visa().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getMother_visa().substring(onlinfo.getDocinfo().getMother_visa().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("MotherVisa"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//mot emid
			if(!onlinfo.getDocinfo().getMother_emid().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getMother_emid().substring(onlinfo.getDocinfo().getMother_emid().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getMother_emid().substring(onlinfo.getDocinfo().getMother_emid().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("MotherE-ID"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//stud ppt
			if(!onlinfo.getDocinfo().getStudent_ppt().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getStudent_ppt().substring(onlinfo.getDocinfo().getStudent_ppt().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getStudent_ppt().substring(onlinfo.getDocinfo().getStudent_ppt().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("StudentPassport"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//stud visa
			if(!onlinfo.getDocinfo().getStudent_visa().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getStudent_visa().substring(onlinfo.getDocinfo().getStudent_visa().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getStudent_visa().substring(onlinfo.getDocinfo().getStudent_visa().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("StudentVisa"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//stud emid
			if(!onlinfo.getDocinfo().getStudent_emid().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getStudent_emid().substring(onlinfo.getDocinfo().getStudent_emid().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getStudent_emid().substring(onlinfo.getDocinfo().getStudent_emid().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("StudentE-ID"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//guar ppt
			if(!onlinfo.getDocinfo().getG_ppt().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getG_ppt().substring(onlinfo.getDocinfo().getG_ppt().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getG_ppt().substring(onlinfo.getDocinfo().getG_ppt().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("GuardianPassport"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//guar visa
			if(!onlinfo.getDocinfo().getG_visa().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getG_visa().substring(onlinfo.getDocinfo().getG_visa().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getG_visa().substring(onlinfo.getDocinfo().getG_visa().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("GuardianVisa"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//guar emid
			if(!onlinfo.getDocinfo().getG_emid().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getG_emid().substring(onlinfo.getDocinfo().getG_emid().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getG_emid().substring(onlinfo.getDocinfo().getG_emid().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("GuardianE-ID"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//birth
			if(!onlinfo.getDocinfo().getBirth_cert().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getBirth_cert().substring(onlinfo.getDocinfo().getBirth_cert().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getBirth_cert().substring(onlinfo.getDocinfo().getBirth_cert().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("BirthCertificate"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//vaccination
			if(!onlinfo.getDocinfo().getVaccination().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getVaccination().substring(onlinfo.getDocinfo().getVaccination().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getVaccination().substring(onlinfo.getDocinfo().getVaccination().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("VaccinationReport"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
			
			//fam book
			if(!onlinfo.getDocinfo().getFamily_book().isEmpty())
			{
				String filename = onlinfo.getDocinfo().getFamily_book().substring(onlinfo.getDocinfo().getFamily_book().lastIndexOf("/")+1);
				FileInputStream fin = new FileInputStream(realPath+filename);
				String ext=onlinfo.getDocinfo().getFamily_book().substring(onlinfo.getDocinfo().getFamily_book().lastIndexOf(".")+1);
	            zout.putNextEntry(new ZipEntry("FamilyBook"+"."+ext));
	            int length;
				while((length = fin.read(buffer)) > 0)
				{
				   zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
		 
			zout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fc.responseComplete();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void downloadAllStudentDocument() 
	{   
		Connection conn=DataBaseConnection.javaConnection();
        
		SchoolInfoList ls =new DatabaseMethods1().fullSchoolInfo(conn);
		
		if(ls.getCountry().equalsIgnoreCase("UAE"))
		{
			downloadAllStudentDocumentUae();
		}
		else
		{
			downloadAllStudentDocumentIndia();
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
	
	public void downloadAllStudentDocumentIndia() 
	{   
		String fileName=selectedSection.getClassName()+"_"+selectedSection.getCategory()+"_docs.zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String realPath=stdList.get(0).getPaidby();
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
			int i = 1;
		 
			for(StudentInfo p : stdList)
			{
				for(SelectItem doc:p.getDocList())
				{
//					String time = new SimpleDateFormat("yMdHms").format(new Date());
//					int randomPIN = (int) (Math.random() * 9000) + 1000;
//					
					FileInputStream fin = new FileInputStream(realPath+doc.getValue());
					String ext=String.valueOf(doc.getValue()).substring(String.valueOf(doc.getValue()).lastIndexOf(".")+1);
//	                zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+time+"_"+randomPIN+"/"+doc.getLabel()+"."+ext));
					zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/"+doc.getLabel()+"."+ext));
	                int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					  
					//close the InputStream
					fin.close();
				}
				i=i+1;
			}
			zout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fc.responseComplete();
	}

	public ArrayList<String> makeStdColumnList()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.ADMISSION_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.SERIAL_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.STUDENT_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.FATHERS_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.STUDENT_IMAGE_PATH);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.ENQ_UAE_ID);
		
		return list;
	}
	
	
	public void downloadAllStudentDocumentUae() 
	{   
		Connection conn = DataBaseConnection.javaConnection();
		String fileName=selectedSection.getClassName()+"_"+selectedSection.getCategory()+"_docs.zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String realPath=stdList.get(0).getPaidby();
	    
	    OnlineAdmInfo onlinfo = new OnlineAdmInfo();
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
			
			int i = 1;
			
			for(StudentInfo p : stdList)
			{
				onlinfo = new DataBaseOnlineAdm().onlineAdmInfoById(p.getEnqUAEId(), "id", conn);
				//Stud pic
				if(!onlinfo.getSt_image().isEmpty())
				{
					String filename = onlinfo.getSt_image().substring(onlinfo.getSt_image().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getSt_image().substring(onlinfo.getSt_image().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/StudentPhoto"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//father pic
				if(!onlinfo.getF_image().isEmpty())
				{
					String filename = onlinfo.getF_image().substring(onlinfo.getF_image().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getF_image().substring(onlinfo.getF_image().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/FatherPhoto"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//mother pic
				if(!onlinfo.getM_image().isEmpty())
				{
					String filename = onlinfo.getM_image().substring(onlinfo.getM_image().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getM_image().substring(onlinfo.getM_image().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/MotherPhoto"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//guardian pic
				if(!onlinfo.getG_image().isEmpty())
				{
					String filename = onlinfo.getG_image().substring(onlinfo.getG_image().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getG_image().substring(onlinfo.getG_image().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/GuardianPhoto"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//fat ppt
				if(!onlinfo.getDocinfo().getParent_ppt().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getParent_ppt().substring(onlinfo.getDocinfo().getParent_ppt().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getParent_ppt().substring(onlinfo.getDocinfo().getParent_ppt().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/FatherPassport"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//fat visa
				if(!onlinfo.getDocinfo().getParent_visa().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getParent_visa().substring(onlinfo.getDocinfo().getParent_visa().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getParent_visa().substring(onlinfo.getDocinfo().getParent_visa().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/FatherVisa"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//fat emid
				if(!onlinfo.getDocinfo().getParent_emid().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getParent_emid().substring(onlinfo.getDocinfo().getParent_emid().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getParent_emid().substring(onlinfo.getDocinfo().getParent_emid().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/FatherE-ID"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//mot ppt
				if(!onlinfo.getDocinfo().getMother_ppt().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getMother_ppt().substring(onlinfo.getDocinfo().getMother_ppt().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getMother_ppt().substring(onlinfo.getDocinfo().getMother_ppt().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/MotherPassport"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//mot visa
				if(!onlinfo.getDocinfo().getMother_visa().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getMother_visa().substring(onlinfo.getDocinfo().getMother_visa().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getMother_visa().substring(onlinfo.getDocinfo().getMother_visa().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/MotherVisa"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//mot emid
				if(!onlinfo.getDocinfo().getMother_emid().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getMother_emid().substring(onlinfo.getDocinfo().getMother_emid().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getMother_emid().substring(onlinfo.getDocinfo().getMother_emid().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/MotherE-ID"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//stud ppt
				if(!onlinfo.getDocinfo().getStudent_ppt().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getStudent_ppt().substring(onlinfo.getDocinfo().getStudent_ppt().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getStudent_ppt().substring(onlinfo.getDocinfo().getStudent_ppt().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/StudentPassport"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//stud visa
				if(!onlinfo.getDocinfo().getStudent_visa().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getStudent_visa().substring(onlinfo.getDocinfo().getStudent_visa().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getStudent_visa().substring(onlinfo.getDocinfo().getStudent_visa().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/StudentVisa"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//stud emid
				if(!onlinfo.getDocinfo().getStudent_emid().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getStudent_emid().substring(onlinfo.getDocinfo().getStudent_emid().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getStudent_emid().substring(onlinfo.getDocinfo().getStudent_emid().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/StudentE-ID"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//guar ppt
				if(!onlinfo.getDocinfo().getG_ppt().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getG_ppt().substring(onlinfo.getDocinfo().getG_ppt().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getG_ppt().substring(onlinfo.getDocinfo().getG_ppt().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/GuardianPassport"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//guar visa
				if(!onlinfo.getDocinfo().getG_visa().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getG_visa().substring(onlinfo.getDocinfo().getG_visa().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getG_visa().substring(onlinfo.getDocinfo().getG_visa().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/GuardianVisa"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//guar emid
				if(!onlinfo.getDocinfo().getG_emid().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getG_emid().substring(onlinfo.getDocinfo().getG_emid().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getG_emid().substring(onlinfo.getDocinfo().getG_emid().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/GuardianE-ID"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//birth
				if(!onlinfo.getDocinfo().getBirth_cert().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getBirth_cert().substring(onlinfo.getDocinfo().getBirth_cert().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getBirth_cert().substring(onlinfo.getDocinfo().getBirth_cert().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/BirthCertificate"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//vaccination
				if(!onlinfo.getDocinfo().getVaccination().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getVaccination().substring(onlinfo.getDocinfo().getVaccination().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getVaccination().substring(onlinfo.getDocinfo().getVaccination().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/VaccinationReport"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				
				//fam book
				if(!onlinfo.getDocinfo().getFamily_book().isEmpty())
				{
					String filename = onlinfo.getDocinfo().getFamily_book().substring(onlinfo.getDocinfo().getFamily_book().lastIndexOf("/")+1);
					FileInputStream fin = new FileInputStream(realPath+filename);
					String ext=onlinfo.getDocinfo().getFamily_book().substring(onlinfo.getDocinfo().getFamily_book().lastIndexOf(".")+1);
		            zout.putNextEntry(new ZipEntry(p.getFullName()+"_"+i+"/FamilyBook"+"."+ext));
		            int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					fin.close();
				}
				i=i+1;
			}
			zout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fc.responseComplete();
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public Category getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(Category selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<StudentInfo> getStdList() {
		return stdList;
	}

	public void setStdList(ArrayList<StudentInfo> stdList) {
		this.stdList = stdList;
	}

	public StudentInfo getSelectedStd() {
		return selectedStd;
	}

	public void setSelectedStd(StudentInfo selectedStd) {
		this.selectedStd = selectedStd;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}
}
