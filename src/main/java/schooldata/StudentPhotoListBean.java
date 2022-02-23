package schooldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.Charsets;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import session_work.QueryConstants;
import student_module.RegistrationColumnName;
@ManagedBean(name="stdPhotoList")
@ViewScoped
public class StudentPhotoListBean implements Serializable {
	Category selectedSection;
	String classId;
	int sectionId;
	ArrayList<StudentInfo> stdList;
	StudentInfo selectedStd;
	transient StreamedContent file;
	DatabaseMethods1 obj= new DatabaseMethods1();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	public StudentPhotoListBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedSection=(Category) ss.getAttribute("selectedSection");
		sectionId=selectedSection.getId();
		classId=selectedSection.getClassid();
		
		String schid=obj.schoolId();
		String session=obj.selectedSessionDetails(schid, conn);
		ArrayList<String> stdColList=makeStdColumnList();

		stdList =objStd.studentDetail("", String.valueOf(sectionId), classId, QueryConstants.BY_CLASS_SECTION, QueryConstants.CLASS_STRENGTH, null, null,QueryConstants.IMAGE_WITH_PATH, QueryConstants.KEYWORD, "", "", session, schid,stdColList, conn);
		
		//stdList=obj.searchStudentListForImage(String.valueOf(sectionId), conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void downloadMethod()
	{
		FacesContext context = FacesContext.getCurrentInstance();
        String index = (String) UIComponent.getCurrentComponent(context).getAttributes().get("addNumber");
        
        for(StudentInfo info:stdList)
        {
        	if(info.getAddNumber().equals(index))
        	{
        		selectedStd=info;
        		break;
        	}
        }
        
		String fl=selectedStd.getStdImageWithoutPath();
		int first=fl.lastIndexOf(".")+1;
		int last=fl.length();
		String ext=fl.substring(first, last);
		
		String fileName=selectedStd.getFullName()+"_"+selectedStd.getSrNo()+".jpg";

		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/

		String realPath = selectedStd.getImageUploadPath();
		try 
		{
			InputStream stream = new FileInputStream(new File(realPath+fl));
			if(ext.equals("png"))
			{  
//				file = new DefaultStreamedContent(stream, "file/png", fileName);
				file = new DefaultStreamedContent().builder().contentType("file/png").name(fileName).stream(()->stream).build();
			}
			else if(ext.equals("jpg"))
			{
//				file = new DefaultStreamedContent(stream, "file/jpg", fileName);
				file = new DefaultStreamedContent().builder().contentType("file/jpg").name(fileName).stream(()->stream).build();
				
			}
			else if(ext.equals("jpeg"))
			{
//				file = new DefaultStreamedContent(stream, "file/jpeg", fileName);
				file = new DefaultStreamedContent().builder().contentType("file/jpeg").name(fileName).stream(()->stream).build();
			}
			else if(ext.equals("gif"))
			{
//				file = new DefaultStreamedContent(stream, "file/gif", fileName);
				file = new DefaultStreamedContent().builder().contentType("file/gif").name(fileName).stream(()->stream).build();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Unsupported File Format!!"));
			}
			
		}
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
	}
	
	public void downloadAllStudentPhoto () 
	{   
		String fileName=selectedSection.getClassName()+"_"+selectedSection.getCategory()+".zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String realPath=stdList.get(0).getImageUploadPath();
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
		    int i=stdList.size();
		    
		    for(int ii = stdList.size()-1; ii >= 0; ii--)
		    {
		    	FileInputStream fin=null;
		    	try
		    	{
		    		fin = new FileInputStream(realPath+stdList.get(ii).getStdImageWithoutPath());
		    	}
		    	catch(Exception e)
		    	{
		    		i=i-1;
		    		continue;
		    	}
		    	
		    	if(fin!=null)
		    	{
	                zout.putNextEntry(new ZipEntry(stdList.get(ii).getFullName().trim()+i+".jpg"));
	                int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					  
					//close the InputStream
					fin.close();
					i=i-1;
		    	}
		    }
		    
			/*for(StudentInfo p : stdList)
			{
			 i=i+1;
			}*/
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
		return list;
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
